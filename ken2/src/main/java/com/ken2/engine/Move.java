package com.ken2.engine;

import com.ken2.Game_Components.Board.Coin;

import java.util.ArrayList;

/**
 * class to store all of the moves
 */
public class Move {
    private final ArrayList<Coin> coinFlip;
    private int xPosition;
    private int yPosition;

    /**
     * constructor
     * @param start
     * @param end
     */
    public Move(int start, int end, ArrayList<Coin>coinFlip){
        this.xPosition = start;
        this.yPosition = end;
        this.coinFlip = coinFlip;
    }

    // Overloaded constructor for moves without coins to flip
    public Move(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.coinFlip = new ArrayList<>(); // Initialize as empty list
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
    public ArrayList<Coin>getFlippedCoins(){
        return coinFlip;
    }

}