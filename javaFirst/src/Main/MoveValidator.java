package Main;

public class MoveValidator {
       private static final int BOARD_SIZE = 8;
       private final GameState gameState;
    public MoveValidator(GameState gameState) {
       this.gameState = gameState;
   }
    public boolean checkCaptures(int row, int col, SquareInfo.PieceColor color, boolean isKing, SquareInfo[][] board) {
        boolean captureAvailable = false;
        int[][] directions = getDirections(color, isKing);

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            int jumpRow = row + 2 * dir[0];
            int jumpCol = col + 2 * dir[1];

            if (isValidPosition(jumpRow, jumpCol)) {
                SquareInfo middleSquare = board[newRow][newCol];
                SquareInfo jumpSquare = board[jumpRow][jumpCol];

                if (middleSquare.getPieceColor() == getOppositeColor(color) &&
                        jumpSquare.getPieceColor() == SquareInfo.PieceColor.NONE) {
                    jumpSquare.setPossibleMove(true);
                    captureAvailable = true;
                }
            }
        }

        return captureAvailable;
    }

    public void checkRegularMoves(int row, int col, SquareInfo.PieceColor color, boolean isKing, SquareInfo[][] board) {
        int[][] directions = getDirections(color, isKing);

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isValidPosition(newRow, newCol)) {
                SquareInfo newSquare = board[newRow][newCol];
                if (newSquare.getPieceColor() == SquareInfo.PieceColor.NONE) {
                    newSquare.setPossibleMove(true);
                }
            }
        }
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    public SquareInfo.PieceColor getOppositeColor(SquareInfo.PieceColor color) {
        return (color == SquareInfo.PieceColor.RED) ? SquareInfo.PieceColor.BLUE : SquareInfo.PieceColor.RED;
    }

    private int[][] getDirections(SquareInfo.PieceColor color, boolean isKing) {
        if (isKing) {
            return new int[][] { { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };
        } else if (color == SquareInfo.PieceColor.RED) {
            return new int[][] { { 1, -1 }, { 1, 1 } };
        } else {
            return new int[][] { { -1, -1 }, { -1, 1 } };
        }
    }
    public void handleBoardClick(int row, int col) {
       SquareInfo[][] board = gameState.getBoard();
       SquareInfo clickedSquare = board[row][col];
       String currentTurn = gameState.getCurrentTurn();
       
       // Changed getPossibleMove() to isPossibleMove()
       if (clickedSquare.isPossibleMove()) {
           // Move logic
           // Update the board
           gameState.setTurnMoved(true);
       } else if (clickedSquare.getPieceColor().toString().equals(currentTurn)) {
           // Show possible moves
           calculatePossibleMoves(row, col, board);
       }
   }
    public void calculatePossibleMoves(int row, int col, SquareInfo[][] board) {
        clearPossibleMoves(board);
        SquareInfo currentSquare = board[row][col];
        boolean isKing = currentSquare.isKing();
        SquareInfo.PieceColor color = currentSquare.getPieceColor();

        boolean captureAvailable = checkCaptures(row, col, color, isKing, board);

        if (!captureAvailable) {
            checkRegularMoves(row, col, color, isKing, board);
        }
    }

    private void clearPossibleMoves(SquareInfo[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j].setPossibleMove(false);
            }
        }
    }
}