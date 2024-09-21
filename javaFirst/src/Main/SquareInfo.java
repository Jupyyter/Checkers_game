package Main;
public class SquareInfo {
    public enum PieceColor {
        NONE, RED, BLUE
    }

    private PieceColor pieceColor;
    private boolean isKing;
    private boolean isHighlighted;
    private boolean isPossibleMove;
    private int width, height;
    private int locationX, locationY;

    public SquareInfo() {
        this.pieceColor = PieceColor.NONE;
        this.isKing = false;
        this.isHighlighted = false;
        this.isPossibleMove = false;
    }

    public boolean hasOppositeColor(PieceColor color) {
        return (this.pieceColor == PieceColor.BLUE && color == PieceColor.RED) ||
               (this.pieceColor == PieceColor.RED && color == PieceColor.BLUE);
    }

    // Getters and setters
    public PieceColor getPieceColor() { return pieceColor; }
    public void setPieceColor(PieceColor pieceColor) { this.pieceColor = pieceColor; }

    public boolean isKing() { return isKing; }
    public void setKing(boolean king) { isKing = king; }

    public boolean isHighlighted() { return isHighlighted; }
    public void setHighlighted(boolean highlighted) { isHighlighted = highlighted; }

    public boolean isPossibleMove() { return isPossibleMove; }
    public void setPossibleMove(boolean possibleMove) { isPossibleMove = possibleMove; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public int getLocationX() { return locationX; }
    public void setLocationX(int locationX) { this.locationX = locationX; }

    public int getLocationY() { return locationY; }
    public void setLocationY(int locationY) { this.locationY = locationY; }
}