package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class SplashScreenWindow extends JWindow {

    private static final long serialVersionUID = 1L;

    private JProgressBar progressBar;
    private JLabel statusLabel;

    public SplashScreenWindow() {
        initializeUI();
    }

    private void initializeUI() {
        setSize(560, 420);
        setLocationRelativeTo(null);

        // Classic corporate dark blue & gold palette
        Color primaryDarkBlue = new Color(13, 33, 66);
        Color accentGold = new Color(240, 171, 0);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBackground(primaryDarkBlue);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentGold, 2),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // --- Top/Center Panel: Logo + System Info ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Load and resize logo.png
        JLabel logoLabel = createLogoLabel(250, 165);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel titleLabel = new JLabel("FRENTE DE CAIXA (PDV)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 4)));

        JLabel subtitleLabel = new JLabel("Sistema de Gestão de Vendas - Edição 1999", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 215, 235));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(subtitleLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // Left-aligned technical environment info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        JLabel versionLabel = new JLabel("<html>Versão 1.0.0 MVP<br>Ambiente Local Offline</html>");
        versionLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        versionLabel.setForeground(accentGold);
        infoPanel.add(versionLabel, BorderLayout.WEST);
        centerPanel.add(infoPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- Bottom Panel: Loading Status & Progress Bar ---
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 8));
        bottomPanel.setOpaque(false);

        statusLabel = new JLabel("Inicializando módulos do sistema...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        statusLabel.setForeground(Color.WHITE);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(500, 24));
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("SansSerif", Font.BOLD, 12));
        progressBar.setForeground(accentGold);
        progressBar.setBackground(new Color(10, 22, 46));
        progressBar.setBorder(BorderFactory.createLineBorder(accentGold, 1));

        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JLabel createLogoLabel(int targetWidth, int targetHeight) {
        // Searches for logo.png in /resources/logo.png or /view/logo.png
        URL imageUrl = getClass().getResource("/images/logo.png");
        if (imageUrl == null) {
            imageUrl = getClass().getResource("/images/logo.png");
        }

        if (imageUrl != null) {
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(
                targetWidth, targetHeight, Image.SCALE_SMOOTH
            );
            return new JLabel(new ImageIcon(scaledImage));
        } else {
            // Fallback text if logo.png is not placed in the folder yet
            JLabel fallbackLabel = new JLabel("Mercadinho São Francisco", SwingConstants.CENTER);
            fallbackLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            fallbackLabel.setForeground(new Color(240, 171, 0));
            return fallbackLabel;
        }
    }

    public void startLoadingAndLaunchMainApp() {
        setVisible(true);

        SwingWorker<Void, LoadingStep> loaderWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                LoadingStep[] steps = {
                    new LoadingStep(15, "Verificando diretório local sales_history/..."),
                    new LoadingStep(40, "Carregando catálogo de produtos na memória..."),
                    new LoadingStep(70, "Sincronizando cadastro de clientes e limites de fiado..."),
                    new LoadingStep(90, "Preparando interface gráfica do operador de caixa..."),
                    new LoadingStep(100, "Sistema pronto!")
                };

                for (LoadingStep step : steps) {
                    Thread.sleep(550);
                    publish(step);
                }
                return null;
            }

            @Override
            protected void process(List<LoadingStep> chunks) {
                LoadingStep latest = chunks.get(chunks.size() - 1);
                progressBar.setValue(latest.progress);
                statusLabel.setText(latest.message);
            }

            @Override
            protected void done() {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        };

        loaderWorker.execute();
    }

    private record LoadingStep(int progress, String message) {}
}