package Main;

public class GameState {
    private BoardRenderer boardRenderer;
    private String currentTurn;
    private boolean gameOver;
    private String winner;
    private boolean turnMoved;
    private boolean endTurnButtonVisible;

    public GameState(BoardRenderer boardRenderer) {
        this.boardRenderer = boardRenderer;
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
        boardRenderer.initializeBoard();
        gameOver = false;
        winner = null;
        currentTurn = "BLUE";
        turnMoved = false;
        endTurnButtonVisible = false;
    }

    public void clearPossibleMoves() {
        SquareInfo[][] squareInfo = boardRenderer.getSquareInfo();
        for (int i = 0; i < BoardRenderer.BOARD_SIZE; i++) {
            for (int j = 0; j < BoardRenderer.BOARD_SIZE; j++) {
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
        return BoardRenderer.BOARD_SIZE;
    }

    public SquareInfo[][] getSquareInfo() {
        return boardRenderer.getSquareInfo();
    }

    public void setSquareInfo(SquareInfo[][] squareInfo) {
        boardRenderer.setSquareInfo(squareInfo);
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

    public BoardRenderer getBoardRenderer() {
        return boardRenderer;
    }
}