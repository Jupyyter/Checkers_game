package Main;

public class GameState {
    private GameBoard gameBoard;
    private String currentTurn;
    private boolean gameOver;
    private String winner;
    private boolean turnMoved;
    private boolean endTurnButtonVisible;

    public GameState(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        resetGameState();
    }

    public void resetGameState() {
        currentTurn = "BLUE";
        gameOver = false;
        winner = null;
        turnMoved = false;
        endTurnButtonVisible = false;
    }

    public void setGameOver(String winner) {
        this.gameOver = true;
        this.winner = winner;
    }

    public void restartGame() {
        gameBoard.initializeBoard();
        gameOver = false;
        winner = null;
        currentTurn = "BLUE";
        turnMoved = false;
        endTurnButtonVisible = false;
    }

    public void clearPossibleMoves() {
        SquareInfo[][] squareInfo = gameBoard.getSquareInfo();
        for (int i = 0; i < GameBoard.BOARD_SIZE; i++) {
            for (int j = 0; j < GameBoard.BOARD_SIZE; j++) {
                squareInfo[i][j].setPossibleMove(false);
            }
        }
    }

    public void endTurn() {
        currentTurn = currentTurn.equals("BLUE") ? "RED" : "BLUE";
        turnMoved = false;
        endTurnButtonVisible = false;
    }

    // Getters and setters
    public int getBoardSize() {
        return GameBoard.BOARD_SIZE;
    }

    public SquareInfo[][] getSquareInfo() {
        return gameBoard.getSquareInfo();
    }

    public void setSquareInfo(SquareInfo[][] squareInfo) {
        gameBoard.setSquareInfo(squareInfo);
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

    public GameBoard getgameBoard() {
        return gameBoard;
    }
}