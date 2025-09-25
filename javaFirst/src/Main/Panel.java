package Main;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.io.File;

public class Panel extends JPanel {
    private BufferedImage checkersBorder, redSquare, blackSquare, point, bluePiece, redPiece, blueKing, redKing, redWin, blueWin;
    private BufferedImage yellowHighlight, greenHighlight;
    private MouseInputs mouseInputs;
    private boolean isEndTurnButtonHovered = false;
    private Rectangle playAgainButton;
    private Rectangle backToMenuButton;
    private boolean isPlayAgainHovered = false;
    private boolean isBackToMenuHovered = false;
    public Runnable backToMenuAction;

    private Font customFont;
    private Rectangle turnIndicator;
    private Rectangle endTurnButton;

    private GameBoard gameBoard;
    private GameState gameState;

    public Panel(Runnable backToMenuAction) {
        this.backToMenuAction = backToMenuAction;
        
        importImages();
        
        gameBoard = new GameBoard(checkersBorder, redSquare, blackSquare, point, 
                                          bluePiece, redPiece, blueKing, redKing, 
                                          yellowHighlight, greenHighlight);
        
        gameState = new GameState(gameBoard);
        
        initializeButtons();
        loadCustomFont();
        initializeTurnIndicator();
        
        mouseInputs = new MouseInputs(this, gameBoard); // Update MouseInputs constructor
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
        
        setOpaque(true);
        setBackground(Color.BLACK);
    }
    private void initializeButtons() {
        playAgainButton = new Rectangle(0, 0, 170, 40);
        backToMenuButton = new Rectangle(0, 0, 210, 40);
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
        endTurnButton = new Rectangle(15, 65, 210, 40);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        gameBoard.calculateScalingAndPositions(this, gameState.getBoardSize());
        gameBoard.renderBoard(g2d, gameState.getSquareInfo(), gameState.getBoardSize());
        
        drawTurnIndicator(g2d);
        drawWinnerIfGameOver(g2d);
        
        if (gameState.isEndTurnButtonVisible() && !gameState.isGameOver()) {
            drawEndTurnButton(g2d);
        }

        positionButtons();
        
        drawCustomButton(g2d, backToMenuButton, "BACK TO MENU", isBackToMenuHovered);
        
        if (gameState.isGameOver()) {
            drawCustomButton(g2d, playAgainButton, "PLAY AGAIN", isPlayAgainHovered);
        }
    }

    private void drawEndTurnButton(Graphics2D g2d) {
        Color buttonColor = isEndTurnButtonHovered ? 
            new Color(255,255,0, 177) : 
            new Color(128, 0, 128, 177);

        g2d.setColor(buttonColor);
        g2d.fill(endTurnButton);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(isEndTurnButtonHovered ? 4 : 3));
        g2d.draw(endTurnButton);

        g2d.setColor(Color.WHITE);
        g2d.setFont(customFont);
        FontMetrics fm = g2d.getFontMetrics();
        String text = "END THIS TURN";
        int textX = endTurnButton.x + (endTurnButton.width - fm.stringWidth(text)) / 2;
        int textY = endTurnButton.y + ((endTurnButton.height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY);

        if (isEndTurnButtonHovered) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void drawWinnerIfGameOver(Graphics g) {
        if (gameState.isGameOver()) {
            BufferedImage winImage = gameState.getWinner().equals("RED") ? redWin : blueWin;
            
            // Calculate dimensions that maintain aspect ratio and fit in window
            double imageAspectRatio = (double) winImage.getWidth() / winImage.getHeight();
            double windowAspectRatio = (double) getWidth() / getHeight();
            
            int winImageWidth, winImageHeight;
            
            if (windowAspectRatio > imageAspectRatio) {
                // Window is wider than image - constrain by height
                winImageHeight = (int)(getHeight() * 0.7); // 70% of window height
                winImageWidth = (int)(winImageHeight * imageAspectRatio);
            } else {
                // Window is taller than image - constrain by width
                winImageWidth = (int)(getWidth() * 0.8); //  80% of window width
                winImageHeight = (int)(winImageWidth / imageAspectRatio);
            }
            
            // Center the image
            int winImageX = (getWidth() - winImageWidth) / 2;
            int winImageY = (getHeight() - winImageHeight) / 3-29; // Position at 1/3 of window height
            
            g.drawImage(winImage, winImageX, winImageY, winImageWidth, winImageHeight, null);
        }
    }

    
private void positionButtons() {
    int backButtonX = getWidth() - backToMenuButton.width - 20;
    int backButtonY = 20;
    backToMenuButton.setLocation(backButtonX, backButtonY);

    if (gameState.isGameOver()) {
        BufferedImage winImage = gameState.getWinner().equals("RED") ? redWin : blueWin;
        double imageAspectRatio = (double) winImage.getWidth() / winImage.getHeight();
        
        int winImageHeight;
        if ((double) getWidth() / getHeight() > imageAspectRatio) {
            winImageHeight = (int)(getHeight() * 0.7);
        } else {
            int winImageWidth = (int)(getWidth() * 0.8);
            winImageHeight = (int)(winImageWidth / imageAspectRatio);
        }
        
        int winImageBottom = (getHeight() - winImageHeight) / 3 + winImageHeight;
        
        // Position Play Again button below the image
        int playAgainY = winImageBottom + 20; // 20 pixels below the image
        int playAgainX = (getWidth() - playAgainButton.width) / 2;
        playAgainButton.setLocation(playAgainX, playAgainY);
    }
}
    
    private void drawTurnIndicator(Graphics2D g2d) {
        Color turnColor = gameState.getCurrentTurn().equals("BLUE") ? new Color(0, 0, 255, 177) : new Color(255, 0, 0, 200);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.draw(turnIndicator);

        g2d.setColor(turnColor);
        g2d.fill(turnIndicator);

        g2d.setColor(Color.WHITE);
        g2d.setFont(customFont);
        FontMetrics fm = g2d.getFontMetrics();
        String text = gameState.getCurrentTurn() + " TURN";
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
            redWin = ImageIO.read(getClass().getResourceAsStream("/imgs/redWin.png"));
            blueWin = ImageIO.read(getClass().getResourceAsStream("/imgs/blueWin.png"));
            yellowHighlight = ImageIO.read(getClass().getResourceAsStream("/imgs/yellowHighlight.png"));
            greenHighlight = ImageIO.read(getClass().getResourceAsStream("/imgs/greenHighlight.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawCustomButton(Graphics2D g2d, Rectangle button, String text, boolean isHovered) {
        Color buttonColor = isHovered ? new Color(44, 95, 45, 197) : new Color(151, 188, 98,197);
        
        g2d.setColor(buttonColor);
        g2d.fill(button);

        g2d.setColor(isHovered ? new Color(151, 188, 98,197) : new Color(44, 95, 45, 197));
        g2d.setStroke(new BasicStroke(isHovered ? 4 : 3));
        g2d.draw(button);

        g2d.setColor(isHovered ? new Color(151, 188, 98,197) : new Color(44, 95, 45, 197));
        g2d.setFont(customFont);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = button.x + (button.width - fm.stringWidth(text)) / 2;
        int textY = button.y + ((button.height - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(text, textX, textY);
    }

    public void clearPossibleMoves() {
        gameState.clearPossibleMoves();
       // gameBoard.setSquareInfo(gameState.getSquareInfo());
    }

    public void setGameOver(String winner) {
        gameState.setGameOver(winner);
        repaint();
    }

    public void restartGame() {
        gameBoard.initializeBoard();
        gameState.resetGameState();
        repaint();
    }

    public SquareInfo[][] getSquareInfo() {
        return gameBoard.getSquareInfo();
    }

    public void setSquareInfo(SquareInfo[][] squareInfo) {
        gameBoard.setSquareInfo(squareInfo);
    }

    public void setCurrentTurn(String turn) {
        gameState.setCurrentTurn(turn);
        repaint();
    }

    public void setTurnMoved(boolean moved) {
        gameState.setTurnMoved(moved);
        repaint();
    }

    public boolean isTurnMoved() {
        return gameState.isTurnMoved();
    }

    public void endTurn() {
        gameState.endTurn();
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
        return gameState.isGameOver();
    }

    public GameState getGameState() {
        return gameState;
    }
    
}