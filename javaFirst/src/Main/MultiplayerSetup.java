//i tried something, i am to bored to continue doing that
package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MultiplayerSetup extends JPanel {
    private JTextField ipAddressField;
    private JButton hostButton;
    private JButton joinButton;
    private JButton backButton;
    private Font customFont;
    private NetworkManager networkManager;

    public MultiplayerSetup(Runnable startHostGame, Runnable startClientGame, Runnable backToMenu) {
        setLayout(null);
        setBackground(Color.BLACK);

        loadCustomFont();

        ipAddressField = new JTextField(20);
        hostButton = createButton("Host Game", e -> {
            networkManager = new NetworkManager(true);
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    boolean connected = networkManager.setupConnection(SwingUtilities.getWindowAncestor(MultiplayerSetup.this));
                    return null;
                }

                @Override
                protected void done() {
                    if (networkManager.isConnected()) {
                        startHostGame.run();
                    }
                }
            };
            worker.execute();
        });
        joinButton = createButton("Join Game", e -> {
            networkManager = new NetworkManager(false);
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    boolean connected = networkManager.setupConnection(SwingUtilities.getWindowAncestor(MultiplayerSetup.this));
                    return null;
                }

                @Override
                protected void done() {
                    if (networkManager.isConnected()) {
                        startClientGame.run();
                    }
                }
            };
            worker.execute();
        });
        backButton = createButton("Back to Menu", e -> backToMenu.run());

        add(ipAddressField);
        add(hostButton);
        add(joinButton);
        add(backButton);

        try {
            String localIP = InetAddress.getLocalHost().getHostAddress();
            JLabel ipLabel = new JLabel("Your IP: " + localIP);
            ipLabel.setForeground(Color.WHITE);
            ipLabel.setFont(customFont);
            add(ipLabel);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(customFont);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        return button;
    }

    private void loadCustomFont() {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/font2.ttf")).deriveFont(18f);
        } catch (Exception e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 18);
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();
        int buttonWidth = 150;
        int buttonHeight = 40;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        ipAddressField.setBounds(centerX - 100, centerY - 100, 200, 30);
        hostButton.setBounds(centerX - buttonWidth / 2, centerY - 50, buttonWidth, buttonHeight);
        joinButton.setBounds(centerX - buttonWidth / 2, centerY, buttonWidth, buttonHeight);
        backButton.setBounds(centerX - buttonWidth / 2, centerY + 50, buttonWidth, buttonHeight);
    }
}