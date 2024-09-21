package Main;

import java.awt.EventQueue;
import javax.swing.Timer;

public class Game implements Runnable {
    private static final int DEFAULT_FPS = 60;
    private static final int FRAME_DELAY = 1000 / DEFAULT_FPS;

    private Window window;
    private Pannel panel;
    private Timer gameTimer;

    public Game() {
        EventQueue.invokeLater(() -> {
            panel = new Pannel();
            window = new Window(panel);
            panel.requestFocus();
            startGameLoop();
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
        // This method is now empty as we're using Swing Timer
    }
}