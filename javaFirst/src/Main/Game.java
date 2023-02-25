package Main;

public class Game implements Runnable {
    private Window window;
    private Pannel pannel;
    private Thread gameThread;
    private final int defaultFPS = 120;

    public Game() {
        pannel = new Pannel();
        window = new Window(pannel);
        pannel.requestFocus();
        startGameLoop();
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();// run is called here by the Thread
    }

    public void run() {// game loop that refreshes game every 1 second / " defaultFPS " seconds

        double timeOnFrame = 1000000000.0 / defaultFPS;// timeOnFrame=1 second divided by 120 frames
        long lastFrame = System.nanoTime();
        int outputFPS = 0;
        long lastTimeCheck = 0;

        while (true) {
            long nowFrame = System.nanoTime();
            if (nowFrame - lastFrame >= timeOnFrame) {// switch frames
                pannel.repaint();
                lastFrame = nowFrame;
                outputFPS++;
            }
            // print FPS
            if (System.currentTimeMillis() - lastTimeCheck >= 1000) {
                lastTimeCheck = System.currentTimeMillis();
                System.out.println("FPS: " + outputFPS);
                outputFPS = 0;
            }
        }

    }
}
