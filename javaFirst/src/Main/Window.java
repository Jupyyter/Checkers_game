package Main;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;

public class Window extends JFrame {
    private JFrame jframe;

    public Window(Pannel pannel) {// define window
        jframe = new JFrame();
        jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);
        jframe.setResizable(true);
        jframe.add(pannel);
        jframe.setTitle("MARIOANA EROINA COCAINA CRACK");
        jframe.pack();
        //full screen the window
        jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);

        jframe.setVisible(rootPaneCheckingEnabled);
    }
}
