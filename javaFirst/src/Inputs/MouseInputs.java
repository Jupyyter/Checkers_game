package Inputs;

import Main.GameBoard;
import Main.Panel;
import Main.SquareInfo;
import Main.MoveValidator;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;
import java.util.logging.Logger;

public class MouseInputs implements MouseInputListener {
    private static final Logger LOGGER = Logger.getLogger(MouseInputs.class.getName());

    private final Panel panel;
    private final GameBoard gameBoard;
    private final MoveValidator moveValidator;
    private SquareInfo.PieceColor currentTurn;
    private int selectedRow = -1;
    private int selectedCol = -1;

    public MouseInputs(Panel panel, GameBoard gameBoard) {
        this.panel = panel;
        this.gameBoard = gameBoard;
        this.currentTurn = SquareInfo.PieceColor.BLUE;
        this.moveValidator = new MoveValidator(panel.getSquareInfo());
        panel.setCurrentTurn("BLUE");
        checkForAvailableMoves();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Rectangle endTurnButton = panel.getEndTurnButton();
        Rectangle playAgainButton = panel.getPlayAgainButton();
        Rectangle backToMenuButton = panel.getBackToMenuButton();

        // Handle button clicks
        if (panel.isTurnMoved() && !panel.isGameOver() && endTurnButton.contains(e.getPoint())) {
            handleEndTurnClick();
            return;
        }

        if (panel.isGameOver() && playAgainButton.contains(e.getPoint())) {
            panel.restartGame();
            return;
        }

        if (backToMenuButton.contains(e.getPoint())) {
            if (panel.backToMenuAction != null) {
                panel.backToMenuAction.run();
            }
            return;
        }

        // Handle game board clicks
        int[] boardCoordinates = getBoardCoordinates(e.getX(), e.getY());
        if (boardCoordinates == null) {
            return;
        }

        int row = boardCoordinates[0];
        int col = boardCoordinates[1];

        handleSquareClick(row, col);
    }

    private void handleEndTurnClick() {
        // End the current turn
        currentTurn = moveValidator.getOppositeColor(currentTurn);
        moveValidator.setCurrentTurn(currentTurn);
        panel.setCurrentTurn(currentTurn == SquareInfo.PieceColor.BLUE ? "BLUE" : "RED");
        panel.setTurnMoved(false);
        moveValidator.setCapturing(false);
        moveValidator.clearCaptureSequence();
        deselectPiece();
        checkForAvailableMoves();

        // Reset cursor and hover state
        panel.setCursor(Cursor.getDefaultCursor());
        panel.setEndTurnButtonHovered(false);
    }

    private int[] getBoardCoordinates(int x, int y) {
        SquareInfo[][] squareInfo = gameBoard.getSquareInfo();
        for (int row = 0; row < GameBoard.BOARD_SIZE; row++) {
            for (int col = 0; col < GameBoard.BOARD_SIZE; col++) {
                SquareInfo square = squareInfo[row][col];
                if (x >= square.getLocationX() && x < square.getLocationX() + square.getWidth() &&
                    y >= square.getLocationY() && y < square.getLocationY() + square.getHeight()) {
                    return new int[] { row, col };
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

        if (moveValidator.isCapturing()) {
            // If in the middle of a capture sequence, only allow moves for the capturing piece
            if (row == selectedRow && col == selectedCol) {
                // Clicked on the capturing piece, show possible moves
                moveValidator.calculatePossibleMoves(row, col);
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
        moveValidator.calculatePossibleMoves(row, col);
    }

    private void clearHighlights() {
        SquareInfo[][] squareInfo = panel.getSquareInfo();
        for (int i = 0; i < GameBoard.BOARD_SIZE; i++) {
            for (int j = 0; j < GameBoard.BOARD_SIZE; j++) {
                squareInfo[i][j].setHighlighted(false);
            }
        }
    }

    private void deselectPiece() {
        if (selectedRow != -1 && selectedCol != -1) {
            moveValidator.clearPossibleMoves();
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    private void movePiece(int newRow, int newCol) {
        boolean endTurn = moveValidator.movePiece(selectedRow, selectedCol, newRow, newCol);

        // Set turnMoved to true after a successful move
        panel.setTurnMoved(true);

        if (endTurn) {
            currentTurn = moveValidator.getOppositeColor(currentTurn);
            moveValidator.setCurrentTurn(currentTurn);
            panel.setCurrentTurn(currentTurn == SquareInfo.PieceColor.BLUE ? "BLUE" : "RED");
            deselectPiece();
            panel.setTurnMoved(false);

            // Check for available moves for the next player
            checkForAvailableMoves();
        } else {
            // Continue the current turn for additional captures
            panel.getSquareInfo()[newRow][newCol].setHighlighted(true);
            selectedRow = newRow;
            selectedCol = newCol;
        }
    }

    private void checkForAvailableMoves() {
        boolean hasAvailableMoves = moveValidator.checkForAvailableMoves();

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
        // Also reset cursor on mouse release, just to be safe
        if (!panel.getEndTurnButton().contains(e.getPoint())) {
            panel.setCursor(Cursor.getDefaultCursor());
            panel.setEndTurnButtonHovered(false);
        }
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
        Rectangle endTurnButton = panel.getEndTurnButton();
        Rectangle playAgainButton = panel.getPlayAgainButton();
        Rectangle backToMenuButton = panel.getBackToMenuButton();

        boolean cursorChanged = false;

        // Check End Turn button
        if (panel.isTurnMoved() && !panel.isGameOver()) {
            boolean isHovering = endTurnButton.contains(e.getPoint());
            panel.setEndTurnButtonHovered(isHovering);
            if (isHovering)
                cursorChanged = true;
        }

        // Check Play Again button
        if (panel.isGameOver()) {
            boolean isHovering = playAgainButton.contains(e.getPoint());
            panel.setPlayAgainHovered(isHovering);
            if (isHovering)
                cursorChanged = true;
        }

        // Check Back to Menu button
        boolean isHovering = backToMenuButton.contains(e.getPoint());
        panel.setBackToMenuHovered(isHovering);
        if (isHovering)
            cursorChanged = true;

        // Update cursor
        if (cursorChanged) {
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            panel.setCursor(Cursor.getDefaultCursor());
        }
    }
}