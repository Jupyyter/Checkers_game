package Inputs;

import Main.Pannel;
import Main.SquareInfo;

import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

public class MouseInputs implements MouseInputListener {
    private static final Logger LOGGER = Logger.getLogger(MouseInputs.class.getName());
    private static final int BOARD_SIZE = 8;

    private final Pannel panel;
    private SquareInfo.PieceColor currentTurn;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean isCapturing = false;
    private List<int[]> captureSequence = new ArrayList<>();

    public MouseInputs(Pannel panel) {
        this.panel = panel;
        this.currentTurn = SquareInfo.PieceColor.BLUE; // blue starts first
        panel.setCurrentTurn("BLUE"); // Set initial turn on the panel
        checkForAvailableMoves(); // Check for available moves at the start of the game
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int[] boardCoordinates = getBoardCoordinates(e.getX(), e.getY());
        if (boardCoordinates == null) {
            return;
        }

        int row = boardCoordinates[0];
        int col = boardCoordinates[1];

        handleSquareClick(row, col);
    }

    private int[] getBoardCoordinates(int x, int y) {
        SquareInfo[][] squareInfo = panel.getSquareInfo();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                SquareInfo square = squareInfo[row][col];
                if (x >= square.getLocationX() && x < square.getLocationX() + square.getWidth() &&
                    y >= square.getLocationY() && y < square.getLocationY() + square.getHeight()) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    private void handleSquareClick(int row, int col) {
        SquareInfo[][] squareInfo = panel.getSquareInfo();
        SquareInfo clickedSquare = squareInfo[row][col];

        // Clear previous highlights
        clearHighlights();

        // Highlight the clicked square
        clickedSquare.setHighlighted(true);

        if (isCapturing) {
            // If we're in the middle of a capture sequence, only allow moves for the capturing piece
            if (row == selectedRow && col == selectedCol) {
                // Clicked on the capturing piece, show possible moves
                calculatePossibleMoves(row, col);
            } else if (clickedSquare.isPossibleMove()) {
                // Clicked on a valid capture move
                movePiece(row, col);
            }
        } else {
            if (selectedRow == -1 && selectedCol == -1) {
                // No piece selected yet
                if (clickedSquare.getPieceColor() == currentTurn) {
                    selectPiece(row, col);
                }
            } else {
                // A piece is already selected
                if (clickedSquare.isPossibleMove()) {
                    movePiece(row, col);
                } else if (clickedSquare.getPieceColor() == currentTurn) {
                    // Selecting a different piece of the same color
                    selectPiece(row, col);
                } else {
                    // Clicking an invalid square, deselect the current piece
                    deselectPiece();
                }
            }
        }

        panel.repaint();
    }

    private void selectPiece(int row, int col) {
        deselectPiece();
        selectedRow = row;
        selectedCol = col;
        panel.getSquareInfo()[row][col].setHighlighted(true);
        calculatePossibleMoves(row, col);
    }

    private void clearHighlights() {
        SquareInfo[][] squareInfo = panel.getSquareInfo();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                squareInfo[i][j].setHighlighted(false);
            }
        }
    }

    private void deselectPiece() {
        if (selectedRow != -1 && selectedCol != -1) {
            clearPossibleMoves();
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    private void calculatePossibleMoves(int row, int col) {
        clearPossibleMoves();
        SquareInfo[][] squareInfo = panel.getSquareInfo();
        SquareInfo currentSquare = squareInfo[row][col];
        boolean isKing = currentSquare.isKing();
        SquareInfo.PieceColor color = currentSquare.getPieceColor();

        boolean captureAvailable = checkCaptures(row, col, color, isKing);

        if (!isCapturing) {
            checkRegularMoves(row, col, color, isKing);
        }

        // Even if no moves are available, keep the piece selected
    }

    private boolean checkCaptures(int row, int col, SquareInfo.PieceColor color, boolean isKing) {
        boolean captureAvailable = false;
        int[][] directions = getDirections(color, isKing);

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            int jumpRow = row + 2 * dir[0];
            int jumpCol = col + 2 * dir[1];

            if (isValidPosition(jumpRow, jumpCol)) {
                SquareInfo middleSquare = panel.getSquareInfo()[newRow][newCol];
                SquareInfo jumpSquare = panel.getSquareInfo()[jumpRow][jumpCol];

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
                SquareInfo newSquare = panel.getSquareInfo()[newRow][newCol];
                if (newSquare.getPieceColor() == SquareInfo.PieceColor.NONE) {
                    newSquare.setPossibleMove(true);
                }
            }
        }
    }

    private void movePiece(int newRow, int newCol) {
        SquareInfo[][] squareInfo = panel.getSquareInfo();
        SquareInfo oldSquare = squareInfo[selectedRow][selectedCol];
        SquareInfo newSquare = squareInfo[newRow][newCol];

        // Move the piece
        newSquare.setPieceColor(oldSquare.getPieceColor());
        newSquare.setKing(oldSquare.isKing());
        oldSquare.setPieceColor(SquareInfo.PieceColor.NONE);
        oldSquare.setKing(false);

        // Check if the piece should become a king
        if (newRow == 0 && currentTurn == SquareInfo.PieceColor.BLUE) {
            newSquare.setKing(true);
        } else if (newRow == BOARD_SIZE - 1 && currentTurn == SquareInfo.PieceColor.RED) {
            newSquare.setKing(true);
        }

        // Handle captures
        boolean captureOccurred = false;
        if (Math.abs(newRow - selectedRow) == 2) {
            int capturedRow = (newRow + selectedRow) / 2;
            int capturedCol = (newCol + selectedCol) / 2;
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
            panel.getSquareInfo()[newRow][newCol].setHighlighted(true);
            selectedRow = newRow;
            selectedCol = newCol;
        } else {
            // End of turn
            if (captureSequence.size() > 0) {
                // Clear all intermediate positions
                for (int i = 0; i < captureSequence.size() - 1; i++) {
                    int[] pos = captureSequence.get(i);
                    squareInfo[pos[0]][pos[1]].setPieceColor(SquareInfo.PieceColor.NONE);
                    squareInfo[pos[0]][pos[1]].setKing(false);
                }
            }
            // Switch turns
            currentTurn = getOppositeColor(currentTurn);
            panel.setCurrentTurn(currentTurn == SquareInfo.PieceColor.BLUE ? "BLUE" : "RED"); // Update turn indicator
            deselectPiece();
            captureSequence.clear(); // Clear the capture sequence at the end of the turn
            isCapturing = false; // Reset the capturing flag
            
            // Check for available moves for the next player
            checkForAvailableMoves();
        }
    }

    private void clearPossibleMoves() {
        SquareInfo[][] squareInfo = panel.getSquareInfo();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
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
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    private SquareInfo.PieceColor getOppositeColor(SquareInfo.PieceColor color) {
        return (color == SquareInfo.PieceColor.RED) ? SquareInfo.PieceColor.BLUE : SquareInfo.PieceColor.RED;
    }

    // New method to check for available moves
    private void checkForAvailableMoves() {
        boolean hasAvailableMoves = false;
        SquareInfo[][] squareInfo = panel.getSquareInfo();

        if (squareInfo == null) {
            LOGGER.severe("squareInfo is null in checkForAvailableMoves");
            return;
        }

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (squareInfo[i][j].getPieceColor() == currentTurn) {
                    calculatePossibleMoves(i, j);
                    for (int row = 0; row < BOARD_SIZE; row++) {
                        for (int col = 0; col < BOARD_SIZE; col++) {
                            if (squareInfo[row][col].isPossibleMove()) {
                                hasAvailableMoves = true;
                                break;
                            }
                        }
                        if (hasAvailableMoves) break;
                    }
                    clearPossibleMoves();
                }
                if (hasAvailableMoves) break;
            }
            if (hasAvailableMoves) break;
        }

        if (!hasAvailableMoves) {
            // Current player has no available moves, they lose
            String winner = (currentTurn == SquareInfo.PieceColor.RED) ? "BLUE" : "RED";
            panel.setGameOver(winner);
        }
    }

    // Other mouse event methods (unchanged)
    @Override
    public void mouseClicked(MouseEvent e) {
        LOGGER.fine("Mouse clicked at: " + e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        LOGGER.fine("Mouse released at: " + e.getPoint());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        LOGGER.fine("Mouse entered at: " + e.getPoint());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        LOGGER.fine("Mouse exited at: " + e.getPoint());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        LOGGER.fine("Mouse dragged at: " + e.getPoint());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        LOGGER.fine("Mouse moved at: " + e.getPoint());
    }
}