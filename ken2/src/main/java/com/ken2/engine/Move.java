package com.ken2.engine;

import com.ken2.Game_Components.Board.Coin;
import com.ken2.Game_Components.Board.Vertex;

import java.util.ArrayList;

/**
 * class to store all of the moves
 */
public class Move {
    private final ArrayList<Coin> coinFlip;
    private int xPosition;
    private int yPosition;
    private Direction direction; //enum object with direction params
    // private int[] targetPosition = null;
    // private int[] fromPosition = null;
    private Vertex startingVertex;


    /**
     * constructor
     * @param start
     * @param end
     */
    public Move(int start, int end, ArrayList<Coin>coinFlip, Direction direction){
        this.xPosition = start;
        this.yPosition = end;
        this.coinFlip = coinFlip;
        this.direction = direction;
    }

    // Overloaded constructor for moves without coins to flip
    public Move(int xPosition, int yPosition, Direction direction) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.coinFlip = new ArrayList<>(); // Initialize as empty list
        this.direction = direction;

    }

    // public Move(int[] targetPosition, int[] fromPosition) {
    //     this.targetPosition = targetPosition;
    //     this.fromPosition = fromPosition;

    //     this.xPosition = targetPosition[0];
    //     this.yPosition = targetPosition[1];
    //     this.coinFlip = new ArrayList<>(); // Initialize as empty list

    // }

    // public int[] getTargetPosition(){
    //     return this.targetPosition;
    // }

    
    // public int[] getFromPosition(){
    //     return this.fromPosition;
    // }

    public void setStartingVertex(Vertex newVertex){
        this.startingVertex = newVertex;
    }

    public Vertex getStartingVertex(){
        return this.startingVertex;
    }

    /**
     * getter for a direction
     * @return
     */
    public Direction getDirection() {
        return direction;
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