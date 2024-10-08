package Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class BoardRenderer {
    private int topLeftX, topLeftY, squareDim, gamePointX, gamePointY, offset, scaledWidth, scaledHeight;
    private BufferedImage checkersBorder, redSquare, blackSquare, point, bluePiece, redPiece, blueKing, redKing;
    private BufferedImage yellowHighlight, greenHighlight;
    private double scaling = 1.0;

    public BoardRenderer(BufferedImage checkersBorder, BufferedImage redSquare, 
                         BufferedImage blackSquare, BufferedImage point, BufferedImage bluePiece, 
                         BufferedImage redPiece, BufferedImage blueKing, BufferedImage redKing, 
                         BufferedImage yellowHighlight, BufferedImage greenHighlight) {
        this.checkersBorder = checkersBorder;
        this.redSquare = redSquare;
        this.blackSquare = blackSquare;
        this.point = point;
        this.bluePiece = bluePiece;
        this.redPiece = redPiece;
        this.blueKing = blueKing;
        this.redKing = redKing;
        this.yellowHighlight = yellowHighlight;
        this.greenHighlight = greenHighlight;
    }

    public void calculateScalingAndPositions(JPanel panel, int boardSize) {
        int imgWidth = checkersBorder.getWidth();
        int imgHeight = checkersBorder.getHeight();
        double panelAspectRatio = (double) panel.getWidth() / panel.getHeight();
        double imgAspectRatio = (double) imgWidth / imgHeight;
        
        if (panelAspectRatio > imgAspectRatio) {
            scaling = (double) panel.getHeight() / imgHeight;
        } else {
            scaling = (double) panel.getWidth() / imgWidth;
        }
        
        scaledWidth = (int) Math.ceil(scaling * imgWidth);
        scaledHeight = (int) Math.ceil(scaling * imgHeight);
        topLeftX = (panel.getWidth() - scaledWidth) / 2;
        topLeftY = (panel.getHeight() - scaledHeight) / 2;
        
        gamePointX = topLeftX + (int) (5 * scaling);
        gamePointY = topLeftY + (int) (5 * scaling);
        squareDim = (int) (redSquare.getWidth() * scaling);
        offset = scaledWidth - (10 * (int)scaling) - (squareDim * boardSize);
    }

    public void renderBoard(Graphics g, SquareInfo[][] squareInfo, int boardSize) {
        drawSquaresAndPieces(g, squareInfo, boardSize);
        drawBorder(g);
    }

    private void drawBorder(Graphics g) {
        g.drawImage(checkersBorder, topLeftX, topLeftY, scaledWidth, scaledHeight, null);
    }

    private void drawSquaresAndPieces(Graphics g, SquareInfo[][] squareInfo, int boardSize) {
        int pieceDim = (int) (bluePiece.getWidth() * scaling);

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                int x = gamePointX + squareDim * j;
                int y = gamePointY + squareDim * i;
                
                drawSquare(g, i, j, x, y, boardSize);
                drawHighlight(g, squareInfo[i][j], x, y);
                drawPiece(g, squareInfo[i][j], x, y, pieceDim);
                drawPossibleMove(g, squareInfo[i][j], x, y);

                updateSquareInfo(squareInfo[i][j], x, y, i, j, boardSize);
            }
        }
    }

    private void drawSquare(Graphics g, int i, int j, int x, int y, int boardSize) {
        BufferedImage squareImage = ((i + j) % 2 == 0) ? redSquare : blackSquare;
        int width = (j == boardSize - 1) ? squareDim + offset : squareDim;
        int height = (i == boardSize - 1) ? squareDim + offset : squareDim;
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

    private void updateSquareInfo(SquareInfo square, int x, int y, int i, int j, int boardSize) {
        square.setLocationX(x);
        square.setLocationY(y);
        square.setWidth(squareDim + (j == boardSize - 1 ? offset : 0));
        square.setHeight(squareDim + (i == boardSize - 1 ? offset : 0));
    }
}