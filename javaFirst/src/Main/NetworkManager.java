//no
package Main;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.Window;

public class NetworkManager {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isHost;
    private boolean isConnected;

    public NetworkManager(boolean isHost) {
        this.isHost = isHost;
        this.isConnected = false;
    }

    public boolean setupConnection(Window window) {
        if (isHost) {
            return setupServer(window);
        } else {
            return connectToServer(window);
        }
    }

    private boolean setupServer(Window window) {
        try {
            serverSocket = new ServerSocket(5000);
            JOptionPane.showMessageDialog(window, "Waiting for client to connect...", "Host Game", JOptionPane.INFORMATION_MESSAGE);
            clientSocket = serverSocket.accept();
            System.out.println("Client connected.");
            setupStreams();
            isConnected = true;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(window, "Failed to set up server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private boolean connectToServer(Window window) {
        String serverAddress = JOptionPane.showInputDialog(
            window,
            "Enter IP Address of the host:",
            "localhost");
        
        if (serverAddress == null || serverAddress.trim().isEmpty()) {
            return false;
        }

        try {
            clientSocket = new Socket(serverAddress, 5000);
            System.out.println("Connected to server.");
            setupStreams();
            isConnected = true;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(window, "Failed to connect to the server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    private void setupStreams() throws IOException {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void sendMove(String move) {
        if (isConnected && out != null) {
            out.println(move);
        }
    }

    public String receiveMove() throws IOException {
        if (isConnected && in != null && in.ready()) {
            return in.readLine();
        }
        return null;
    }

    public boolean isHost() {
        return isHost;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void close() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
            isConnected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}