package Main;

public class GameState {
    private static final int BOARD_SIZE = 8;
    private static final int INITIAL_RED_ROWS = 3;
    private static final int INITIAL_BLUE_ROWS = 3;

    private SquareInfo[][] squareInfo;
    private String currentTurn;
    private boolean gameOver;
    private String winner;
    private boolean turnMoved;
    private boolean endTurnButtonVisible;

    public GameState() {
        initializeBoard();
        currentTurn = "BLUE";
        gameOver = false;
        winner = null;
        turnMoved = false;
        endTurnButtonVisible = false;
    }

    public void initializeBoard() {
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

    public void setGameOver(String winner) {
        this.gameOver = true;
        this.winner = winner;
        System.out.println(winner + " wins the game!");
    }

    public void restartGame() {
        initializeBoard();
        gameOver = false;
        winner = null;
        currentTurn = "BLUE";
        turnMoved = false;
        endTurnButtonVisible = false;
    }

    public void clearPossibleMoves() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                squareInfo[i][j].setPossibleMove(false);
            }
        }
    }

    public void endTurn() {
        currentTurn = currentTurn.equals("BLUE") ? "RED" : "BLUE";
        turnMoved = false;
        endTurnButtonVisible = false;
    }
    public int getBoardSize() {
       return BOARD_SIZE;
   }
    // Getters and setters
    public SquareInfo[][] getSquareInfo() {
        return squareInfo;
    }

    public void setSquareInfo(SquareInfo[][] squareInfo) {
        this.squareInfo = squareInfo;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getWinner() {
        return winner;
    }

    public boolean isTurnMoved() {
        return turnMoved;
    }

    public void setTurnMoved(boolean turnMoved) {
        this.turnMoved = turnMoved;
        this.endTurnButtonVisible = turnMoved;
    }

    public boolean isEndTurnButtonVisible() {
        return endTurnButtonVisible;
    }
}