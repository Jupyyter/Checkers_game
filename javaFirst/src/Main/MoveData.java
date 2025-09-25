package Main;

import java.io.Serializable;

public class MoveData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int oldRow;
    private int oldCol;
    private int newRow;
    private int newCol;
    private SquareInfo.PieceColor pieceColor;
    
    public MoveData(int oldRow, int oldCol, int newRow, int newCol, SquareInfo.PieceColor pieceColor) {
        this.oldRow = oldRow;
        this.oldCol = oldCol;
        this.newRow = newRow;
        this.newCol = newCol;
        this.pieceColor = pieceColor;
    }
    
    // Getters
    public int getOldRow() { return oldRow; }
    public int getOldCol() { return oldCol; }
    public int getNewRow() { return newRow; }
    public int getNewCol() { return newCol; }
    public SquareInfo.PieceColor getPieceColor() { return pieceColor; }
}