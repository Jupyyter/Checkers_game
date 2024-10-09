package Main;

import java.awt.EventQueue;
import javax.swing.Timer;
import javax.swing.SwingUtilities;

public class Game implements Runnable {
    private static final int DEFAULT_FPS = 60;
    private static final int FRAME_DELAY = 1000 / DEFAULT_FPS;

    private Window window;
    private Panel panel;
    private Menu menu;
    private Timer gameTimer;

    public Game() {
        EventQueue.invokeLater(() -> {
            menu = new Menu(this::startGame);
            window = new Window(menu);
        });
    }

    private void startGame() {
        SwingUtilities.invokeLater(() -> {
            panel = new Panel(this::backToMenu);
            window.setContentPane(panel);
            window.revalidate();
            window.repaint();
            panel.requestFocus();
            startGameLoop();
        });
    }

    private void backToMenu() {
        SwingUtilities.invokeLater(() -> {
            if (gameTimer != null) {
                gameTimer.stop();
            }
            menu = new Menu(this::startGame);
            window.setContentPane(menu);
            window.revalidate();
            window.repaint();
        });
    }

    private void startGameLoop() {
        gameTimer = new Timer(FRAME_DELAY, e -> {
            update();
            panel.repaint();
        });
        gameTimer.start();
    }

    private void update() {
        // Update game state here
    }

    @Override
    public void run() {
        // This method is now empty as im using Swing Timer
    }
}