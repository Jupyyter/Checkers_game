package Main;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import Inputs.MouseInputs;
import java.io.File;

public class Panel extends JPanel {
    private static final int BOARD_SIZE = 8;
    private static final int INITIAL_RED_ROWS = 3;
    private static final int INITIAL_BLUE_ROWS = 3;

    private int topLeftX, topLeftY, squareDim, gamePointX, gamePointY, offset, scaledWidth, scaledHeight;
    private BufferedImage checkersBorder, redSquare, blackSquare, point, highlighter, bluePiece, redPiece, blueKing, redKing, redWin, blueWin;
    private BufferedImage yellowHighlight, greenHighlight;
    private double scaling = 1.0;
    private SquareInfo[][] squareInfo;
    private MouseInputs mouseInputs;
    private boolean isEndTurnButtonHovered = false;
    private Rectangle playAgainButton;
    private Rectangle backToMenuButton;
    private boolean isPlayAgainHovered = false;
    private boolean isBackToMenuHovered = false;
    public Runnable backToMenuAction;

    private Font customFont;
    private Rectangle turnIndicator;
    private Rectangle endTurnButton; // New button rectangle
    private String currentTurn = "BLUE"; // Initial turn
    private boolean gameOver = false;
    private String winner = null;
    private boolean turnMoved = false; // Track if a move has been made this turn
    private boolean endTurnButtonVisible = false; // Track if the end turn button should be visible

    public Panel(Runnable backToMenuAction) {
        this.backToMenuAction = backToMenuAction;
        initializeButtons();
        importImages();
        initializeBoard();
        loadCustomFont();
        initializeTurnIndicator();
        mouseInputs = new MouseInputs(this);
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
        setOpaque(true);
        setBackground(Color.BLACK);
    }
    private void initializeButtons() {
        // Initialize rectangles instead of JButtons
        playAgainButton = new Rectangle(0, 0, 170, 40);
        backToMenuButton = new Rectangle(0, 0, 210, 40);
    }
    private void drawCustomButton(Graphics2D g2d, Rectangle button, String text, boolean isHovered) {
        Color buttonColor = isHovered ? new Color(44, 95, 45, 197) : new Color(151, 188, 98,197);
        
        // Draw button background
        g2d.setColor(buttonColor);
        g2d.fill(button);

        // Draw white outline
        g2d.setColor(isHovered ? new Color(151, 188, 98,197) : new Color(44, 95, 45, 197));
        g2d.setStroke(new BasicStroke(isHovered ? 4 : 3));
        g2d.draw(button);

        // Draw text
        g2d.setColor(isHovered ? new Color(151, 188, 98,197) : new Color(44, 95, 45, 197));
        g2d.setFont(customFont);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = button.x + (button.width - fm.stringWidth(text)) / 2;
        int textY = button.y + ((button.height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY);
    }
    private void initializeBoard() {
        squareInfo = new SquareInfo[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                squareInfo[i][j] = new SquareInfo();
                if ((i + j) % 2 == 0) {
                    if (i < INITIAL_RED_ROWS) {
                        squareInfo[i][j].setPieceColor(SquareInfo.PieceColor.RED);
                    } else if (i >= BOARD_SIZE - INITIAL_BLUE_ROWS) {
                        squareInfo[i][j].setPieceColor(SquareInfo.PieceColor.BLUE);
                    }
                }
            }
        }
    }

    private void loadCustomFont() {
        try {
            File fontFile = new File("fonts/font.ttf");
            if (fontFile.exists()) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(18f);
            } else {
                InputStream is = getClass().getResourceAsStream("/fonts/font2.ttf");
                if (is != null) {
                    customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(18f);
                    is.close();
                } else {
                    throw new IOException("Font file not found in file system or resources");
                }
            }
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 18);
        }
    }

    private void initializeTurnIndicator() {
        turnIndicator = new Rectangle(15, 15, 170, 40);
        endTurnButton = new Rectangle(15, 65, 210, 40); // Position it below the turn indicator
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        calculateScalingAndPositions();
        drawSquaresAndPieces(g2d);
        drawBorder(g2d);
        drawTurnIndicator(g2d);
        drawWinnerIfGameOver(g2d);
        
        if (endTurnButtonVisible && turnMoved && !gameOver) {
            drawEndTurnButton(g2d);
        }

        positionButtons();
        
        // Draw Back to Menu button
        drawCustomButton(g2d, backToMenuButton, "BACK TO MENU", isBackToMenuHovered);
        
        // Draw Play Again button if game is over
        if (gameOver) {
            drawCustomButton(g2d, playAgainButton, "PLAY AGAIN", isPlayAgainHovered);
        }
    }

    private void drawEndTurnButton(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Change button color based on hover state
        Color buttonColor = isEndTurnButtonHovered ? 
            new Color(255,255,0, 177) : // Brighter purple when hovered
            new Color(128, 0, 128, 177);   // Normal purple

        // Draw button background
        g2d.setColor(buttonColor);
        g2d.fill(endTurnButton);

        // Draw white outline, slightly thicker when hovered
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(isEndTurnButtonHovered ? 4 : 3));
        g2d.draw(endTurnButton);

        // Draw text
        g2d.setColor(Color.WHITE);
        g2d.setFont(customFont);
        FontMetrics fm = g2d.getFontMetrics();
        String text = "END THIS TURN";
        int textX = endTurnButton.x + (endTurnButton.width - fm.stringWidth(text)) / 2;
        int textY = endTurnButton.y + ((endTurnButton.height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY);

        // Optional: change cursor when hovering
        if (isEndTurnButtonHovered) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void calculateScalingAndPositions() {
        int imgWidth = checkersBorder.getWidth();
        int imgHeight = checkersBorder.getHeight();
        double panelAspectRatio = (double) getWidth() / getHeight();
        double imgAspectRatio = (double) imgWidth / imgHeight;
        
        if (panelAspectRatio > imgAspectRatio) {
            scaling = (double) getHeight() / imgHeight;
        } else {
            scaling = (double) getWidth() / imgWidth;
        }
        
        scaledWidth = (int) Math.ceil(scaling * imgWidth);
        scaledHeight = (int) Math.ceil(scaling * imgHeight);
        topLeftX = (getWidth() - scaledWidth) / 2;
        topLeftY = (getHeight() - scaledHeight) / 2;
        
        gamePointX = topLeftX + (int) (5 * scaling);
        gamePointY = topLeftY + (int) (5 * scaling);
        squareDim = (int) (redSquare.getWidth() * scaling);
        offset = scaledWidth - (10 * (int)scaling) - (squareDim * BOARD_SIZE);
    }

    private void drawBorder(Graphics g) {
        g.drawImage(checkersBorder, topLeftX, topLeftY, scaledWidth, scaledHeight, null);
    }

    private void drawSquaresAndPieces(Graphics g) {
        int pieceDim = (int) (bluePiece.getWidth() * scaling);

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int x = gamePointX + squareDim * j;
                int y = gamePointY + squareDim * i;
                
                drawSquare(g, i, j, x, y);
                drawHighlight(g, i, j, x, y);
                drawPiece(g, i, j, x, y, pieceDim);
                drawPossibleMove(g, i, j, x, y);

                updateSquareInfo(i, j, x, y);
            }
        }
    }

    private void drawSquare(Graphics g, int i, int j, int x, int y) {
        BufferedImage squareImage = ((i + j) % 2 == 0) ? redSquare : blackSquare;
        int width = (j == BOARD_SIZE - 1) ? squareDim + offset : squareDim;
        int height = (i == BOARD_SIZE - 1) ? squareDim + offset : squareDim;
        g.drawImage(squareImage, x, y, width, height, null);
    }

    private void drawHighlight(Graphics g, int i, int j, int x, int y) {
        SquareInfo square = squareInfo[i][j];
        if (square.isHighlighted()) {
            BufferedImage highlightImage = square.getPieceColor() != SquareInfo.PieceColor.NONE ? yellowHighlight : greenHighlight;
            g.drawImage(highlightImage, x, y, squareDim, squareDim, null);
        }
    }

    private void drawPiece(Graphics g, int i, int j, int x, int y, int pieceDim) {
        SquareInfo square = squareInfo[i][j];
        if (square.getPieceColor() == SquareInfo.PieceColor.RED) {
            g.drawImage(square.isKing() ? redKing : redPiece, x, y, pieceDim, pieceDim, null);
        } else if (square.getPieceColor() == SquareInfo.PieceColor.BLUE) {
            g.drawImage(square.isKing() ? blueKing : bluePiece, x, y, pieceDim, pieceDim, null);
        }
    }

    private void drawPossibleMove(Graphics g, int i, int j, int x, int y) {
        SquareInfo square = squareInfo[i][j];
        if (square.isPossibleMove()) {
            g.drawImage(point, x, y, squareDim, squareDim, null);
        }
    }

    private void updateSquareInfo(int i, int j, int x, int y) {
        squareInfo[i][j].setLocationX(x);
        squareInfo[i][j].setLocationY(y);
        squareInfo[i][j].setWidth(squareDim + (j == BOARD_SIZE - 1 ? offset : 0));
        squareInfo[i][j].setHeight(squareDim + (i == BOARD_SIZE - 1 ? offset : 0));
    }

    private void drawWinnerIfGameOver(Graphics g) {
        if (gameOver) {
            BufferedImage winImage = winner.equals("RED") ? redWin : blueWin;
            
            // Calculate dimensions while maintaining aspect ratio
            int winImageWidth = scaledWidth / 2;
            int winImageHeight = (winImageWidth * winImage.getHeight()) / winImage.getWidth();
            
            // Center horizontally relative to the game board
            int winImageX = topLeftX + (scaledWidth - winImageWidth) / 2;
            
            // Vertical position (you can adjust this if needed)
            int winImageY = getHeight() / 4 - winImageHeight / 2;
            
            g.drawImage(winImage, winImageX, winImageY, winImageWidth, winImageHeight, null);
        }
    }
    private void positionButtons() {
        // Position "Back to Menu" button
        int backButtonX = getWidth() - backToMenuButton.width - 20;
        int backButtonY = 20;
        backToMenuButton.setLocation(backButtonX, backButtonY);

        // Position "Play Again" button if game is over
        if (gameOver) {
            int winImageBottom = getHeight() / 4 + scaledHeight / 4;
            int playAgainY = winImageBottom + (getHeight() - winImageBottom) / 2 - playAgainButton.height / 2;
            int playAgainX = (getWidth() - playAgainButton.width) / 2;
            playAgainButton.setLocation(playAgainX, playAgainY);
        }
    }
    
    private void drawTurnIndicator(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set color based on current turn
        Color turnColor = currentTurn.equals("BLUE") ? new Color(0, 0, 255, 177) : new Color(255, 0, 0, 200);

        // Draw white outline
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3)); // Set the thickness of the outline
        g2d.draw(turnIndicator);

        // Fill rectangle with turn color
        g2d.setColor(turnColor);
        g2d.fill(turnIndicator);

        // Draw text
        g2d.setColor(Color.WHITE);
        g2d.setFont(customFont);
        FontMetrics fm = g2d.getFontMetrics();
        String text = currentTurn + " TURN";
        int textX = turnIndicator.x + (turnIndicator.width - fm.stringWidth(text)) / 2;
        int textY = turnIndicator.y + ((turnIndicator.height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY);
    }
    private void importImages() {
        try {
            checkersBorder = ImageIO.read(getClass().getResourceAsStream("/imgs/checkersBorder.png"));
            redSquare = ImageIO.read(getClass().getResourceAsStream("/imgs/redSquare.png"));
            blackSquare = ImageIO.read(getClass().getResourceAsStream("/imgs/blackSquare.png"));
            point = ImageIO.read(getClass().getResourceAsStream("/imgs/point.png"));
            bluePiece = ImageIO.read(getClass().getResourceAsStream("/imgs/blueThing.png"));
            blueKing = ImageIO.read(getClass().getResourceAsStream("/imgs/blueKing.png"));
            redPiece = ImageIO.read(getClass().getResourceAsStream("/imgs/redThing.png"));
            redKing = ImageIO.read(getClass().getResourceAsStream("/imgs/redKing.png"));
            highlighter = ImageIO.read(getClass().getResourceAsStream("/imgs/highlighter.png"));
            redWin = ImageIO.read(getClass().getResourceAsStream("/imgs/redWin.png"));
            blueWin = ImageIO.read(getClass().getResourceAsStream("/imgs/blueWin.png"));
            yellowHighlight = ImageIO.read(getClass().getResourceAsStream("/imgs/yellowHighlight.png"));
            greenHighlight = ImageIO.read(getClass().getResourceAsStream("/imgs/greenHighlight.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearPossibleMoves() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                squareInfo[i][j].setPossibleMove(false);
            }
        }
    }

    private boolean isRedLose() {
        return !hasPiecesOfColor(SquareInfo.PieceColor.RED);
    }

    private boolean isBlueLose() {
        return !hasPiecesOfColor(SquareInfo.PieceColor.BLUE);
    }
    public void setGameOver(String winner) {
        this.gameOver = true;
        this.winner = winner;
        System.out.println(winner + " wins the game!");
        repaint();
    }
    public void restartGame() {
        initializeBoard();
        gameOver = false;
        winner = null;
        currentTurn = "BLUE";
        turnMoved = false;
        endTurnButtonVisible = false;
        repaint();
    }
    private boolean hasPiecesOfColor(SquareInfo.PieceColor color) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (squareInfo[i][j].getPieceColor() == color) {
                    return true;
                }
            }
        }
        return false;
    }

    public SquareInfo[][] getSquareInfo() {
        return squareInfo;
    }

    public void setSquareInfo(SquareInfo[][] squareInfo) {
        this.squareInfo = squareInfo;
    }

    public void setCurrentTurn(String turn) {
        this.currentTurn = turn;
        repaint();
    }
    public void setTurnMoved(boolean moved) {
        this.turnMoved = moved;
        this.endTurnButtonVisible = moved;
        repaint();
    }

    public boolean isTurnMoved() {
        return turnMoved;
    }

    public void endTurn() {
        currentTurn = currentTurn.equals("BLUE") ? "RED" : "BLUE";
        turnMoved = false;
        endTurnButtonVisible = false;
        repaint();
    }

    public Rectangle getEndTurnButton() {
        return endTurnButton;
    }
    public void setEndTurnButtonHovered(boolean hovered) {
        if (this.isEndTurnButtonHovered != hovered) {
            this.isEndTurnButtonHovered = hovered;
            repaint();
        }
    }

    public boolean isEndTurnButtonHovered() {
        return isEndTurnButtonHovered;
    }
    public Rectangle getPlayAgainButton() {
        return playAgainButton;
    }

    public Rectangle getBackToMenuButton() {
        return backToMenuButton;
    }

    public void setPlayAgainHovered(boolean hovered) {
        if (this.isPlayAgainHovered != hovered) {
            this.isPlayAgainHovered = hovered;
            repaint();
        }
    }

    public void setBackToMenuHovered(boolean hovered) {
        if (this.isBackToMenuHovered != hovered) {
            this.isBackToMenuHovered = hovered;
            repaint();
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }
}