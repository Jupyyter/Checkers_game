package Main;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JPanel;

public class Window extends JFrame {
    private JFrame jframe;

    public Window(Menu menu) {
        jframe = new JFrame();
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setResizable(true);
        jframe.setContentPane(menu);
        jframe.setTitle("Checkers Game");
        // Set a minimum size for the window
        jframe.setMinimumSize(new Dimension(800, 600));
        // Set a default size when not maximized
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.8);
        int height = (int) (screenSize.height * 0.8);
        jframe.setSize(width, height);
        // Center the window on the screen
        jframe.setLocationRelativeTo(null);
        // Maximize the window
        jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
        jframe.setVisible(true);
    }

    public void setContentPane(JPanel panel) {
        jframe.setContentPane(panel);
        jframe.revalidate();
        jframe.repaint();
    }
}