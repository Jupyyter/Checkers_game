package Main;

public class GameState {
    private static final int BOARD_SIZE = 8;
    private static final int INITIAL_RED_ROWS = 3;
    private static final int INITIAL_BLUE_ROWS = 3;

    private SquareInfo[][] board;
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
    public void update() {
       // This method can be empty if you don't need to update the game state every frame
       // or you can add game logic that needs to be checked continuously
   }
    public void initializeBoard() {
        board = new SquareInfo[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new SquareInfo();
                if ((i + j) % 2 == 0) {
                    if (i < INITIAL_RED_ROWS) {
                        board[i][j].setPieceColor(SquareInfo.PieceColor.RED);
                    } else if (i >= BOARD_SIZE - INITIAL_BLUE_ROWS) {
                        board[i][j].setPieceColor(SquareInfo.PieceColor.BLUE);
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

    public void setCurrentTurn(String turn) {
        this.currentTurn = turn;
    }

    public void endTurn() {
        currentTurn = currentTurn.equals("BLUE") ? "RED" : "BLUE";
        turnMoved = false;
        endTurnButtonVisible = false;
    }

    public SquareInfo[][] getBoard() {
        return board;
    }

    public String getCurrentTurn() {
        return currentTurn;
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

    public void setTurnMoved(boolean moved) {
        this.turnMoved = moved;
        this.endTurnButtonVisible = moved;
    }

    public boolean isEndTurnButtonVisible() {
        return endTurnButtonVisible;
    }

    public void setEndTurnButtonVisible(boolean visible) {
        this.endTurnButtonVisible = visible;
    }
}