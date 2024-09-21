package Main;

import java.awt.Graphics;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import Inputs.MouseInputs;

public class Pannel extends JPanel {
    private static final int BOARD_SIZE = 8;
    private static final int INITIAL_RED_ROWS = 3;
    private static final int INITIAL_BLUE_ROWS = 3;

    private int topLeftX, topLeftY, squareDim, gamePointX, gamePointY, offset, scaledWidth, scaledHeight;
    private BufferedImage checkersBorder, redSquare, blackSquare, point, highlighter, bluePiece, redPiece, blueKing, redKing, redWin, blueWin;
    private BufferedImage yellowHighlight, greenHighlight;
    private double scaling = 1.0;
    private SquareInfo[][] squareInfo;
    private MouseInputs mouseInputs;

    public Pannel() {
        mouseInputs = new MouseInputs(this);
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
        importImages();
        initializeBoard();
        setOpaque(true);  // Optimization: Avoid unnecessary repainting of underlying components
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        calculateScalingAndPositions();
        drawSquaresAndPieces(g);
        drawWinnerIfGameOver(g);
        drawBorder(g);  // Draw border last so it appears on top
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
        if (isRedLose()) {
            g.drawImage(blueWin, topLeftX, topLeftY, scaledWidth, scaledHeight, null);
        } else if (isBlueLose()) {
            g.drawImage(redWin, topLeftX, topLeftY, scaledWidth, scaledHeight, null);
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
}