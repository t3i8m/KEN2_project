package com.ken2.engine;

import com.ken2.Game_Components.Board.Vertex;

/**
 * Enum data structure to store all possible straight-line directions in a hexagonal grid.
 */
public enum Direction {
    UP(-2, 0),
    DOWN(2, 0),
    LEFT_UP(-1, -1),
    RIGHT_DOWN(1, 1),
    LEFT_DOWN(1, -1),
    RIGHT_UP(-1, 1);

    private final int deltaX;
    private final int deltaY;

    Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    /**
     * Change on the x-axis.
     * @return int change on the x-axis
     */
    public int getDeltaX() {
        return deltaX;
    }

    /**
     * Change on the y-axis.
     * @return int change on the y-axis
     */
    public int getDeltaY() {
        return deltaY;
    }

    /**
     * Checks if the next move fits on the board.
     *
     * @param board The game board
     * @param x The current x position
     * @param y The current y position
     * @return boolean true if the move is within bounds and on a valid vertex
     */
    public boolean isValidMove(Vertex[][] board, int x, int y) {
        int newX = x + deltaX;
        int newY = y + deltaY;

        if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length) {
            return board[newX][newY] != null;
        }
        return false;
    }
}
