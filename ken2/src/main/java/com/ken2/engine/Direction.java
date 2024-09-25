package com.ken2.engine;

import com.ken2.Game_Components.Board.Vertex;

/**
 * enum data structure to store all of the possible diagonal directions
 */
public enum Direction {
    UP(-2,0),
    DOWN(2,0),
    LEFT(0, -2),
    RIGHT(0,2),
    DIAG_UP_RIGHT(-2, 2),  
    DIAG_DOWN_LEFT(2, -2), 
    DIAG_UP_LEFT(-2, -2), 
    DIAG_DOWN_RIGHT(2, 2);

    private final int deltaX;
    private final int deltaY;

    Direction(int deltaX, int deltaY){
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    /**
     * change on the x 
     * @return int change on the x axis
     */
    public int getDeltaX(){
        return deltaX;
    }

    /**
     * change on the y
     * @return int change on the y axis
     */
    public int getDeltaY(){
        return deltaY;
    }

    /**
     * checks if the next move fits on the board
     * 
     * @param board 
     * @param x
     * @param y
     * @return
     */
    public boolean isValidMove(Vertex[][] board, int x, int y) {
        int newX = x + deltaX;
        int newY = y + deltaY;

        if ((newX >= 0) && (newX < board.length) && (newY >= 0) && (newY < board[0].length)) {
            return board[newX][newY] != null; 
        }
        return false;
    }
}