package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Menu extends JPanel {
    private JButton playButton;
    private Image backgroundImage;

    public Menu(Runnable startGameAction) {
        setLayout(null); // Use null layout for custom positioning
        setBackground(Color.BLACK);

        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/imgs/checkers.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create and configure the play button
        playButton = new JButton("Play");
        try {
            // Load the font from the resource folder
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/font2.ttf")).deriveFont(Font.BOLD, 24);
            playButton.setFont(customFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            playButton.setFont(new Font("Arial", Font.BOLD, 24));
        }
        
        playButton.setBackground(Color.WHITE);
        playButton.setForeground(Color.BLACK);
        playButton.setFocusPainted(false);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGameAction.run();
            }
        });

        add(playButton);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        if (backgroundImage != null && playButton != null) {
            int imgWidth = backgroundImage.getWidth(this);
            int imgHeight = backgroundImage.getHeight(this);
            int imgX = (getWidth() - imgWidth) / 2;
            int imgY = getHeight() / 4 - imgHeight / 2; // Position at 1/4 of the window height

            // Calculate button position
            int buttonWidth = 150;
            int buttonHeight = 40;
            int buttonX = (getWidth() - buttonWidth) / 2;
            int buttonY = imgY + imgHeight + (getHeight() - (imgY + imgHeight)) / 2 - buttonHeight / 2;

            playButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the background image
        if (backgroundImage != null) {
            int imgWidth = backgroundImage.getWidth(this);
            int imgHeight = backgroundImage.getHeight(this);
            int x = (getWidth() - imgWidth) / 2;
            int y = getHeight() / 4 - imgHeight / 2; // Position at 1/4 of the window height
            g.drawImage(backgroundImage, x, y, this);
        }
    }
}