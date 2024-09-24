package com.ken2.engine;

import com.ken2.Game_Components.Board.Vertex;

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

    public int getDeltaX(){
        return deltaX;
    }

    public int getDeltaY(){
        return deltaY;
    }

    public boolean isValidMove(Vertex[][] board, int x, int y) {
        int newX = x + deltaX;
        int newY = y + deltaY;

        if ((newX >= 0) && (newX < board.length) && (newY >= 0) && (newY < board[0].length)) {
            return board[newX][newY] != null; 
        }
        return false;
    }
}
