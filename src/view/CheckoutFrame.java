package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import model.CartItem;
import model.Customer;
import model.Product;

public class CheckoutFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    // Output directory and filename for sales history
    private static final String SALES_DIR_PATH = "sales_history";
    private static final String SALES_FILE_PATH = SALES_DIR_PATH + File.separator + "sales_log.txt";

    // UI Components (English Naming)
    private JTextField productCodeField;
    private JTextField quantityField;
    private JTextField paymentInputField;
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private JLabel totalAmountLabel;
    private JLabel changeAmountLabel;
    private JComboBox<String> paymentMethodComboBox;

    // Simulation Repositories (Offline Local Memory)
    private final List<CartItem> currentCartItems = new ArrayList<>();
    private final Map<String, Product> productCatalog = new HashMap<>();
    private final Map<String, Customer> registeredCustomers = new HashMap<>();

    // Daily Closing Accumulated Values (In Cents)
    private long accumulatedCashInCents = 0;
    private long accumulatedCardInCents = 0;
    private long accumulatedTabInCents = 0;

    public CheckoutFrame() {
        super("Mercadinho São Francisco - Frente de Caixa (MVP)");
        ensureSalesDirectoryExists();
        loadMockData();
        configureWindow();
        initializeComponents();
    }

    private void ensureSalesDirectoryExists() {
        File salesDir = new File(SALES_DIR_PATH);
        if (!salesDir.exists()) {
            salesDir.mkdirs();
        }
    }

    private void loadMockData() {
        // Products catalog (Prices in cents to avoid float precision issues)
        productCatalog.put("78910", new Product("78910", "Arroz 5kg", 2500)); // R$ 25,00
        productCatalog.put("78911", new Product("78911", "Feijão 1kg", 850));   // R$ 8,50
        productCatalog.put("78912", new Product("78912", "Leite 1L", 520));    // R$ 5,20

        // Customers registered for tab credit ("fiado")
        registeredCustomers.put("123456", new Customer("123456", "João Silva", 50000)); // R$ 500,00 limit
    }

    private void configureWindow() {
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void initializeComponents() {
        // --- Top Panel: Input Product ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Adicionar Item à Compra"));

        topPanel.add(new JLabel("Cód. Produto:"));
        productCodeField = new JTextField(8);
        topPanel.add(productCodeField);

        topPanel.add(new JLabel("Qtd:"));
        quantityField = new JTextField("1", 4);
        topPanel.add(quantityField);

        JButton addItemButton = new JButton("Adicionar Item");
        addItemButton.addActionListener(this::handleAddItem);
        topPanel.add(addItemButton);

        JButton cancelItemButton = new JButton("Cancelar Item Selecionado");
        cancelItemButton.addActionListener(this::handleCancelItem);
        topPanel.add(cancelItemButton);

        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Items Table ---
        tableModel = new DefaultTableModel(
            new Object[]{"#", "Código", "Descrição", "Qtd", "Preço Un. (R$)", "Subtotal (R$)"}, 0
        );
        itemsTable = new JTable(tableModel);
        add(new JScrollPane(itemsTable), BorderLayout.CENTER);

        // --- Bottom Panel: Payment and Daily Closing ---
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Payment Subpanel
        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Pagamento"));

        totalAmountLabel = new JLabel("Total: R$ 0,00");
        totalAmountLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalAmountLabel.setForeground(new Color(0, 128, 0));
        paymentPanel.add(totalAmountLabel);

        paymentPanel.add(new JLabel("Forma:"));
        paymentMethodComboBox = new JComboBox<>(new String[]{"DINHEIRO", "CARTAO", "FIADO"});
        paymentPanel.add(paymentMethodComboBox);

        paymentPanel.add(new JLabel("Valor Pago / CPF:"));
        paymentInputField = new JTextField(8);
        paymentPanel.add(paymentInputField);

        JButton checkoutButton = new JButton("Finalizar Venda");
        checkoutButton.addActionListener(this::handleCheckout);
        paymentPanel.add(checkoutButton);

        changeAmountLabel = new JLabel("Troco: R$ 0,00");
        changeAmountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        paymentPanel.add(changeAmountLabel);

        // Closing Subpanel
        JPanel closingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton dailySummaryButton = new JButton("Ver Fechamento do Dia");
        dailySummaryButton.setBackground(new Color(70, 130, 180));
        dailySummaryButton.setForeground(Color.WHITE);
        dailySummaryButton.addActionListener(this::handleDailySummary);
        closingPanel.add(dailySummaryButton);

        bottomPanel.add(paymentPanel);
        bottomPanel.add(closingPanel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void handleAddItem(ActionEvent event) {
        String code = productCodeField.getText().trim();
        int quantity;

        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Product product = productCatalog.get(code);
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Produto não encontrado! (Tente: 78910, 78911 ou 78912)", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentCartItems.add(new CartItem(product, quantity));
        refreshTable();
        productCodeField.setText("");
        quantityField.setText("1");
        productCodeField.requestFocus();
    }

    private void handleCancelItem(ActionEvent event) {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow >= 0) {
            currentCartItems.remove(selectedRow);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um item na tabela para cancelar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        long grandTotalInCents = 0;

        for (int i = 0; i < currentCartItems.size(); i++) {
            CartItem item = currentCartItems.get(i);
            long subtotalInCents = item.getSubtotalInCents();
            grandTotalInCents += subtotalInCents;

            tableModel.addRow(new Object[]{
                i + 1,
                item.getProduct().getCode(),
                item.getProduct().getName(),
                item.getQuantity(),
                formatCurrency(item.getProduct().getPriceInCents()),
                formatCurrency(subtotalInCents)
            });
        }
        totalAmountLabel.setText(String.format("Total: R$ %s", formatCurrency(grandTotalInCents)));
    }

    private void handleCheckout(ActionEvent event) {
        long totalSaleInCents = calculateTotalCartInCents();
        if (totalSaleInCents == 0) {
            JOptionPane.showMessageDialog(this, "Nenhum item na venda!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String paymentMethod = (String) paymentMethodComboBox.getSelectedItem();
        long changeInCents = 0;
        String extraInfo = "";

        if ("DINHEIRO".equals(paymentMethod)) {
            try {
                double paidAmountInReal = Double.parseDouble(paymentInputField.getText().replace(",", "."));
                long paidAmountInCents = Math.round(paidAmountInReal * 100);

                if (paidAmountInCents < totalSaleInCents) {
                    JOptionPane.showMessageDialog(this, "Valor pago é insuficiente!", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                changeInCents = paidAmountInCents - totalSaleInCents;
                changeAmountLabel.setText(String.format("Troco: R$ %s", formatCurrency(changeInCents)));
                accumulatedCashInCents += totalSaleInCents;
                extraInfo = "Pago: R$ " + formatCurrency(paidAmountInCents) + " | Troco: R$ " + formatCurrency(changeInCents);

                JOptionPane.showMessageDialog(this, "Venda em Dinheiro Finalizada!\nTroco: R$ " + formatCurrency(changeInCents));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Informe um valor pago válido em dinheiro.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if ("CARTAO".equals(paymentMethod)) {
            accumulatedCardInCents += totalSaleInCents;
            changeAmountLabel.setText("Troco: R$ 0,00");
            extraInfo = "Pagamento via Cartão";
            JOptionPane.showMessageDialog(this, "Venda no Cartão Finalizada!");
        } else if ("FIADO".equals(paymentMethod)) {
            String customerCpf = paymentInputField.getText().trim();
            Customer customer = registeredCustomers.get(customerCpf);

            if (customer == null) {
                JOptionPane.showMessageDialog(this, "Cliente não cadastrado para fiado!", "Erro Fiado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (totalSaleInCents > customer.getTabCreditLimitInCents()) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Venda excede o limite fiado do cliente!\nLimite disponível: R$ " + formatCurrency(customer.getTabCreditLimitInCents()), 
                    "Limite Excedido", 
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            customer.deductTabCredit(totalSaleInCents);
            accumulatedTabInCents += totalSaleInCents;
            changeAmountLabel.setText("Troco: R$ 0,00");
            extraInfo = "Cliente: " + customer.getName() + " (CPF: " + customerCpf + ") | Novo Limite: R$ " + formatCurrency(customer.getTabCreditLimitInCents());

            JOptionPane.showMessageDialog(
                this, 
                "Venda Fiado Autorizada!\nNovo Limite do Cliente: R$ " + formatCurrency(customer.getTabCreditLimitInCents())
            );
        }

        // Persist sale into sales_history/sales_log.txt
        saveSaleToLogFile(paymentMethod, totalSaleInCents, extraInfo);

        currentCartItems.clear();
        refreshTable();
        paymentInputField.setText("");
    }

    private void saveSaleToLogFile(String paymentMethod, long totalSaleInCents, String extraInfo) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (FileWriter fw = new FileWriter(SALES_FILE_PATH, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println("--------------------------------------------------");
            pw.println("DATA/HORA: " + timestamp);
            pw.println("FORMA DE PAGAMENTO: " + paymentMethod);
            pw.println("ITENS DA COMPRA:");
            for (CartItem item : currentCartItems) {
                pw.printf("  - [%s] %s x%d -> R$ %s%n",
                    item.getProduct().getCode(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    formatCurrency(item.getSubtotalInCents())
                );
            }
            pw.println("TOTAL DA VENDA: R$ " + formatCurrency(totalSaleInCents));
            if (!extraInfo.isEmpty()) {
                pw.println("OBSERVAÇÕES: " + extraInfo);
            }
            pw.println("--------------------------------------------------\n");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                this, 
                "Erro ao salvar o histórico de vendas no arquivo: " + e.getMessage(), 
                "Erro de Persistência", 
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleDailySummary(ActionEvent event) {
        long totalDaySalesInCents = accumulatedCashInCents + accumulatedCardInCents + accumulatedTabInCents;
        String summaryMessage = String.format(
            "=== FECHAMENTO DO DIA ===\n\n" +
            "Total em Dinheiro: R$ %s\n" +
            "Total em Cartão:   R$ %s\n" +
            "Total em Fiado:    R$ %s\n" +
            "-----------------------------\n" +
            "TOTAL VENDIDO:     R$ %s\n\n" +
            "Histórico salvo em: %s",
            formatCurrency(accumulatedCashInCents),
            formatCurrency(accumulatedCardInCents),
            formatCurrency(accumulatedTabInCents),
            formatCurrency(totalDaySalesInCents),
            SALES_FILE_PATH
        );

        JOptionPane.showMessageDialog(this, summaryMessage, "Fechamento do Dia", JOptionPane.INFORMATION_MESSAGE);
    }

    private long calculateTotalCartInCents() {
        return currentCartItems.stream().mapToLong(CartItem::getSubtotalInCents).sum();
    }

    private String formatCurrency(long amountInCents) {
        return String.format("%.2f", amountInCents / 100.0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CheckoutFrame().setVisible(true));
    }
}