package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField usernameField;
    private JPasswordField passwordField;

    // Offline local credentials (Username -> Password) 
    // username: caixa or dono, password: 1234 or admin
    private final Map<String, String> validCredentials = new HashMap<>();

    public LoginFrame() {
        super("Mercadinho São Francisco - Autenticação");
        loadAuthorizedUsers();
        configureWindow();
        initializeComponents();
    }

    private void loadAuthorizedUsers() {
        // Roles defined in project scope: Cashier Operator and Store Owner
        validCredentials.put("caixa", "1234");
        validCredentials.put("dono", "admin");
    }

    private void configureWindow() {
        setSize(460, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        URL iconUrl = getClass().getResource("/resources/logo.png");
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        }
    }

    private void initializeComponents() {
        Color primaryDarkBlue = new Color(13, 33, 66);
        Color panelDarkBlue = new Color(19, 44, 86);
        Color accentGold = new Color(240, 171, 0);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBackground(primaryDarkBlue);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentGold, 2),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        // --- Header: Logo & Title ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel logoLabel = createLogoLabel(190, 125);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(logoLabel);

        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel titleLabel = new JLabel("ACESSO AO SISTEMA (PDV)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Identifique-se para abrir o caixa", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(200, 215, 235));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Center: Login Form ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(panelDarkBlue);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(45, 75, 125), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);

        JLabel userLabel = new JLabel("Usuário:");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        userLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        usernameField = new JTextField(16);
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Senha:");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        passLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(16);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.addActionListener(this::handleLogin); // Allows pressing Enter to submit
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // --- Footer: Login Button & Credentials Hint ---
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);

        JButton loginButton = new JButton("ENTRAR NO CAIXA");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setBackground(accentGold);
        loginButton.setForeground(primaryDarkBlue);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(260, 38));
        loginButton.addActionListener(this::handleLogin);
        footerPanel.add(loginButton);

        footerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel hintLabel = new JLabel("Acesso teste: caixa / 1234  ou  dono / admin", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        hintLabel.setForeground(new Color(180, 195, 220));
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerPanel.add(hintLabel);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JLabel createLogoLabel(int targetWidth, int targetHeight) {
        URL imageUrl = getClass().getResource("/resources/logo.png");
        if (imageUrl == null) {
            imageUrl = getClass().getResource("/view/logo.png");
        }

        if (imageUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(
                targetWidth, targetHeight, Image.SCALE_SMOOTH
            );
            return new JLabel(new ImageIcon(scaledImage));
        } else {
            JLabel fallbackLabel = new JLabel("[Mercadinho São Francisco]", SwingConstants.CENTER);
            fallbackLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            fallbackLabel.setForeground(new Color(240, 171, 0));
            return fallbackLabel;
        }
    }

    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim().toLowerCase();
        String password = new String(passwordField.getPassword());

        if (validCredentials.containsKey(username) && validCredentials.get(username).equals(password)) {
            dispose();
            SwingUtilities.invokeLater(() -> new CheckoutFrame().setVisible(true));
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Usuário ou senha inválidos!\nUse 'caixa' (1234) ou 'dono' (admin).",
                "Falha na Autenticação",
                JOptionPane.ERROR_MESSAGE
            );
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
}