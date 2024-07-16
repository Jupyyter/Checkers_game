package Main;

public class Game implements Runnable {
    private Window window;
    private Pannel pannel;
    private Thread gameThread;
    private final int defaultFPS = 60;

    public Game() {
        pannel = new Pannel(); // Create a new instance of Pannel
        window = new Window(pannel); // Create a new Window and pass in the Pannel instance
        pannel.requestFocus(); // Request focus for the Pannel
        startGameLoop(); // Start the game loop
    }

    private void startGameLoop() {
        gameThread = new Thread(this); // Create a new Thread and pass in the current instance of Game
        gameThread.start(); // Start the game loop in a new thread
    }

    public void run() {// This is the main game loop that refreshes the game every (1 second / "defaultFPS") seconds

        double timeOnFrame = 1000000000.0 / defaultFPS; // Calculate the time in nanoseconds that each frame should take
        long lastFrame = System.nanoTime(); // Get the current time in nanoseconds
        int outputFPS = 0; // Keep track of the frames per second
        long lastTimeCheck = 0; // Keep track of the time for printing the frames per second

        while (true) { // The game loop will run indefinitely until the game is stopped

            long nowFrame = System.nanoTime(); // Get the current time in nanoseconds
            if (nowFrame - lastFrame >= timeOnFrame) { // Check if it's time to switch frames
                pannel.repaint(); // Refresh the Pannel
                lastFrame = nowFrame; // Update the last frame time to the current time
                outputFPS++; // Increase the frames per second counter
            }

            if (System.currentTimeMillis() - lastTimeCheck >= 1000) { // Check if it's time to print the frames per second
                lastTimeCheck = System.currentTimeMillis(); // Update the last time check to the current time
                //System.out.println("FPS: " + outputFPS); // Print the frames per second
                outputFPS = 0; // Reset the frames per second counter
            }
        }

    }
}