// Import the necessary libraries
package Main;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;

// Create a custom JFrame class called "Window"
public class Window extends JFrame {

    private JFrame jframe; // Declare an instance variable "jframe" of type JFrame

    // Define the constructor for the Window class
    public Window(Pannel pannel) {

        // Initialize the instance variable "jframe" as a new JFrame object
        jframe = new JFrame();

        // Set the default close operation for the JFrame to exit on close
        jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);

        // Set the JFrame to be resizable
        jframe.setResizable(true);

        // Add the given Pannel object to the JFrame
        jframe.add(pannel);

        // Set the title of the JFrame
        jframe.setTitle("MARIOANA EROINA COCAINA CRACK");

        // Resize the JFrame to fit its contents
        jframe.pack();

        // Set the JFrame to be in full screen mode
        jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Make the JFrame visible
        jframe.setVisible(rootPaneCheckingEnabled);
    }
}
//:):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):):)