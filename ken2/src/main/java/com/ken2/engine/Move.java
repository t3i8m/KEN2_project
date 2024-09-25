package com.ken2.engine;

/**
 * class to store all of the moves
 */
public class Move {
    private int xPosition;
    private int yPosition;

    /**
     * constructor 
     * @param start
     * @param end
     */
    public Move(int start, int end){
        this.xPosition = start;
        this.yPosition = end;
    }

    /**
     * to get x position of the move 
     * @return
     */
    public int getXposition(){
        return this.xPosition;
    }

    /**
     * to get y position of the move 
     * @return
     */
    public int getYposition(){
        return this.yPosition;
    }
}