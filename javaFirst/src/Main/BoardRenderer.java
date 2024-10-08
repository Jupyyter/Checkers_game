package Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class BoardRenderer {
    private static final int BOARD_SIZE = 8;
    private GameState gameState;
    private BufferedImage checkersBorder, redSquare, blackSquare, point, highlighter, bluePiece, redPiece, blueKing, redKing, redWin, blueWin;
    private BufferedImage yellowHighlight, greenHighlight;
    private int topLeftX, topLeftY, squareDim, gamePointX, gamePointY, offset, scaledWidth, scaledHeight;
    private double scaling = 1.0;
    private Font customFont;

    public BoardRenderer(GameState gameState) {
        this.gameState = gameState;
        importImages();
        loadCustomFont();
    }

    public void calculateScalingAndPositions(int panelWidth, int panelHeight) {
        if (checkersBorder == null) return;

        // Use a fixed aspect ratio if the image doesn't load
        int imgWidth = checkersBorder.getWidth();
        int imgHeight = checkersBorder.getHeight();
        
        // Maintain aspect ratio while fitting in the panel
        double scaleX = (double) panelWidth / imgWidth;
        double scaleY = (double) panelHeight / imgHeight;
        scaling = Math.min(scaleX, scaleY) * 0.9; // Use 90% of available space
        
        scaledWidth = (int) (imgWidth * scaling);
        scaledHeight = (int) (imgHeight * scaling);
        
        // Center the board in the panel
        topLeftX = (panelWidth - scaledWidth) / 2;
        topLeftY = (panelHeight - scaledHeight) / 2;
        
        // Calculate square dimensions and game board starting point
        squareDim = scaledWidth / 10; // Assuming the board is 8x8 with 1 square border
        gamePointX = topLeftX + squareDim;
        gamePointY = topLeftY + squareDim;
        
        // Offset for the last row/column
        offset = squareDim / 4;
    }

    public void drawBoard(Graphics g) {
       if (checkersBorder == null) {
           g.setColor(Color.RED);
           g.drawString("Error: Could not load board images", 50, 50);
           return;
       }

       // Draw border
       g.drawImage(checkersBorder, topLeftX, topLeftY, scaledWidth, scaledHeight, null);

       // Draw squares and pieces
       drawSquaresAndPieces(g);
   }

    private void drawBorder(Graphics g) {
        g.drawImage(checkersBorder, topLeftX, topLeftY, scaledWidth, scaledHeight, null);
    }

    private void drawSquaresAndPieces(Graphics g) {
       SquareInfo[][] squareInfo = gameState.getBoard();
       
       for (int i = 0; i < BOARD_SIZE; i++) {
           for (int j = 0; j < BOARD_SIZE; j++) {
               int x = gamePointX + (squareDim * j);
               int y = gamePointY + (squareDim * i);
               
               // Draw square
               BufferedImage squareImage = ((i + j) % 2 == 0) ? redSquare : blackSquare;
               g.drawImage(squareImage, x, y, squareDim, squareDim, null);
               
               // Draw piece if exists
               SquareInfo square = squareInfo[i][j];
               if (square.getPieceColor() != SquareInfo.PieceColor.NONE) {
                   BufferedImage pieceImage = getPieceImage(square);
                   if (pieceImage != null) {
                       g.drawImage(pieceImage, x, y, squareDim, squareDim, null);
                   }
               }
               
               // Update square info for mouse interaction
               square.setLocationX(x);
               square.setLocationY(y);
               square.setWidth(squareDim);
               square.setHeight(squareDim);
           }
       }
   }
   private BufferedImage getPieceImage(SquareInfo square) {
       if (square.getPieceColor() == SquareInfo.PieceColor.RED) {
           return square.isKing() ? redKing : redPiece;
       } else if (square.getPieceColor() == SquareInfo.PieceColor.BLUE) {
           return square.isKing() ? blueKing : bluePiece;
       }
       return null;
   }
    private void drawSquare(Graphics g, int i, int j, int x, int y) {
        BufferedImage squareImage = ((i + j) % 2 == 0) ? redSquare : blackSquare;
        int width = (j == BOARD_SIZE - 1) ? squareDim + offset : squareDim;
        int height = (i == BOARD_SIZE - 1) ? squareDim + offset : squareDim;
        g.drawImage(squareImage, x, y, width, height, null);
    }

    private void drawHighlight(Graphics g, SquareInfo square, int x, int y) {
        if (square.isHighlighted()) {
            BufferedImage highlightImage = square.getPieceColor() != SquareInfo.PieceColor.NONE ? yellowHighlight : greenHighlight;
            g.drawImage(highlightImage, x, y, squareDim, squareDim, null);
        }
    }

    private void drawPiece(Graphics g, SquareInfo square, int x, int y, int pieceDim) {
        if (square.getPieceColor() == SquareInfo.PieceColor.RED) {
            g.drawImage(square.isKing() ? redKing : redPiece, x, y, pieceDim, pieceDim, null);
        } else if (square.getPieceColor() == SquareInfo.PieceColor.BLUE) {
            g.drawImage(square.isKing() ? blueKing : bluePiece, x, y, pieceDim, pieceDim, null);
        }
    }

    private void drawPossibleMove(Graphics g, SquareInfo square, int x, int y) {
        if (square.isPossibleMove()) {
            g.drawImage(point, x, y, squareDim, squareDim, null);
        }
    }

    private void updateSquareInfo(SquareInfo square, int x, int y) {
        square.setLocationX(x);
        square.setLocationY(y);
        square.setWidth(squareDim);
        square.setHeight(squareDim);
    }

    private void drawTurnIndicator(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Rectangle turnIndicator = new Rectangle(15, 15, 170, 40);
        String currentTurn = gameState.getCurrentTurn();

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

    private void drawWinnerIfGameOver(Graphics g) {
        if (gameState.isGameOver()) {
            BufferedImage winImage = gameState.getWinner().equals("RED") ? redWin : blueWin;
            
            // Calculate dimensions while maintaining aspect ratio
            int winImageWidth = scaledWidth / 2;
            int winImageHeight = (winImageWidth * winImage.getHeight()) / winImage.getWidth();
            
            // Center horizontally relative to the game board
            int winImageX = topLeftX + (scaledWidth - winImageWidth) / 2;
            
            // Vertical position (you can adjust this if needed)
            int winImageY = (int)(scaledHeight / 4 - winImageHeight / 2);
            
            g.drawImage(winImage, winImageX, winImageY, winImageWidth, winImageHeight, null);
        }
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

    private void loadCustomFont() {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/font.ttf")).deriveFont(18f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.BOLD, 18);
        }
    }
    public Font getCustomFont() {
       return customFont;
   }

   public int getScaledHeight() {
       return scaledHeight;
   }
}