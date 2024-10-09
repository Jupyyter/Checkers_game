package Main;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MoveValidator {
    private static final Logger LOGGER = Logger.getLogger(MoveValidator.class.getName());

    private SquareInfo[][] squareInfo;
    private SquareInfo.PieceColor currentTurn;
    private boolean isCapturing = false;
    private List<int[]> captureSequence = new ArrayList<>();

    public MoveValidator(SquareInfo[][] squareInfo) {
        this.squareInfo = squareInfo;
        this.currentTurn = SquareInfo.PieceColor.BLUE;
    }

    public void setCurrentTurn(SquareInfo.PieceColor currentTurn) {
        this.currentTurn = currentTurn;
    }

    public boolean isCapturing() {
        return isCapturing;
    }

    public void setCapturing(boolean capturing) {
        isCapturing = capturing;
    }

    public List<int[]> getCaptureSequence() {
        return captureSequence;
    }

    public void clearCaptureSequence() {
        captureSequence.clear();
    }

    public void calculatePossibleMoves(int row, int col) {
        clearPossibleMoves();
        SquareInfo currentSquare = squareInfo[row][col];
        boolean isKing = currentSquare.isKing();
        SquareInfo.PieceColor color = currentSquare.getPieceColor();

        boolean captureAvailable = checkCaptures(row, col, color, isKing);

        if (!isCapturing) {
            checkRegularMoves(row, col, color, isKing);
        }
    }

    public boolean checkCaptures(int row, int col, SquareInfo.PieceColor color, boolean isKing) {
        boolean captureAvailable = false;
        int[][] directions = getDirections(color, isKing);

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            int jumpRow = row + 2 * dir[0];
            int jumpCol = col + 2 * dir[1];

            if (isValidPosition(jumpRow, jumpCol)) {
                SquareInfo middleSquare = squareInfo[newRow][newCol];
                SquareInfo jumpSquare = squareInfo[jumpRow][jumpCol];

                if (middleSquare.getPieceColor() == getOppositeColor(color) &&
                        jumpSquare.getPieceColor() == SquareInfo.PieceColor.NONE) {
                    jumpSquare.setPossibleMove(true);
                    captureAvailable = true;
                }
            }
        }

        return captureAvailable;
    }

    private void checkRegularMoves(int row, int col, SquareInfo.PieceColor color, boolean isKing) {
        int[][] directions = getDirections(color, isKing);

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isValidPosition(newRow, newCol)) {
                SquareInfo newSquare = squareInfo[newRow][newCol];
                if (newSquare.getPieceColor() == SquareInfo.PieceColor.NONE) {
                    newSquare.setPossibleMove(true);
                }
            }
        }
    }

    public boolean movePiece(int oldRow, int oldCol, int newRow, int newCol) {
        SquareInfo oldSquare = squareInfo[oldRow][oldCol];
        SquareInfo newSquare = squareInfo[newRow][newCol];

        // Move the piece
        newSquare.setPieceColor(oldSquare.getPieceColor());
        newSquare.setKing(oldSquare.isKing());
        oldSquare.setPieceColor(SquareInfo.PieceColor.NONE);
        oldSquare.setKing(false);

        // Check if the piece should become a king
        if (newRow == 0 && currentTurn == SquareInfo.PieceColor.BLUE) {
            newSquare.setKing(true);
        } else if (newRow == GameBoard.BOARD_SIZE - 1 && currentTurn == SquareInfo.PieceColor.RED) {
            newSquare.setKing(true);
        }

        // Handle captures
        boolean captureOccurred = false;
        if (Math.abs(newRow - oldRow) == 2) {
            int capturedRow = (newRow + oldRow) / 2;
            int capturedCol = (newCol + oldCol) / 2;
            squareInfo[capturedRow][capturedCol].setPieceColor(SquareInfo.PieceColor.NONE);
            squareInfo[capturedRow][capturedCol].setKing(false);
            captureOccurred = true;
            captureSequence.add(new int[]{newRow, newCol});
        }

        // Check for additional captures
        clearPossibleMoves();
        boolean additionalCaptures = checkCaptures(newRow, newCol, currentTurn, newSquare.isKing());

        if (captureOccurred && additionalCaptures) {
            isCapturing = true;
            return false; // Don't end the turn yet
        } else {
            if (captureSequence.size() > 0) {
                // Clear all intermediate positions
                for (int i = 0; i < captureSequence.size() - 1; i++) {
                    int[] pos = captureSequence.get(i);
                    squareInfo[pos[0]][pos[1]].setPieceColor(SquareInfo.PieceColor.NONE);
                    squareInfo[pos[0]][pos[1]].setKing(false);
                }
            }

            isCapturing = false;
            captureSequence.clear();
            return true; // End the turn
        }
    }

    public void clearPossibleMoves() {
        for (int i = 0; i < GameBoard.BOARD_SIZE; i++) {
            for (int j = 0; j < GameBoard.BOARD_SIZE; j++) {
                squareInfo[i][j].setPossibleMove(false);
            }
        }
    }

    private int[][] getDirections(SquareInfo.PieceColor color, boolean isKing) {
        if (isKing) {
            return new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        } else if (color == SquareInfo.PieceColor.RED) {
            return new int[][]{{1, -1}, {1, 1}};
        } else {
            return new int[][]{{-1, -1}, {-1, 1}};
        }
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < GameBoard.BOARD_SIZE && col >= 0 && col < GameBoard.BOARD_SIZE;
    }

    public SquareInfo.PieceColor getOppositeColor(SquareInfo.PieceColor color) {
        return (color == SquareInfo.PieceColor.RED) ? SquareInfo.PieceColor.BLUE : SquareInfo.PieceColor.RED;
    }

    public boolean checkForAvailableMoves() {
        for (int i = 0; i < GameBoard.BOARD_SIZE; i++) {
            for (int j = 0; j < GameBoard.BOARD_SIZE; j++) {
                if (squareInfo[i][j].getPieceColor() == currentTurn) {
                    calculatePossibleMoves(i, j);
                    for (int row = 0; row < GameBoard.BOARD_SIZE; row++) {
                        for (int col = 0; col < GameBoard.BOARD_SIZE; col++) {
                            if (squareInfo[row][col].isPossibleMove()) {
                                clearPossibleMoves();
                                return true;
                            }
                        }
                    }
                    clearPossibleMoves();
                }
            }
        }
        return false;
    }
}