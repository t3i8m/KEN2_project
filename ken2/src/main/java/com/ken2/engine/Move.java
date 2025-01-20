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
    private double reward = -10000000000.0;

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
        try{
            return this.startingVertex;
        }catch(Exception ex){
            GameEngine ge = new GameEngine();
            return ge.gameBoard.getVertex(ge.gameBoard.getVertexNumberFromPosition(xPosition, yPosition));
        }
        
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        
        if (obj == null || getClass() != obj.getClass()) return false;

        Move other = (Move) obj;
        return xPosition == other.xPosition &&
            yPosition == other.yPosition &&
            direction == other.direction &&
            ((startingVertex == null && other.startingVertex == null) ||
                (startingVertex != null && other.startingVertex != null && startingVertex.equals(other.startingVertex))) &&
            ((coinFlip == null && other.coinFlip == null) ||
                (coinFlip != null && other.coinFlip != null && coinFlip.equals(other.coinFlip)));
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(xPosition);
        result = 31 * result + Integer.hashCode(yPosition);
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + (startingVertex != null ? startingVertex.hashCode() : 0);
        result = 31 * result + (coinFlip != null ? coinFlip.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        GameEngine ge = new GameEngine();
    
        builder.append("Move: [");
        builder.append("Start: ").append(startingVertex != null ? startingVertex.getVertextNumber() : "null").append(", ");
    
        int vertexNumber = ge.gameBoard.getVertexNumberFromPosition(xPosition, yPosition);
        Vertex targetVertex = ge.gameBoard.getVertex(vertexNumber);
    
        if (targetVertex == null) {
            builder.append("Target: (invalid position), ");
        } else {
            builder.append("Target: (").append(targetVertex.getVertextNumber()).append("), ");
        }
    
        builder.append("Direction: ").append(direction != null ? direction.name() : "null").append(", ");
        builder.append("Flipped Coins: ").append(coinFlip != null ? coinFlip.size() : 0);
        builder.append("]");
        builder.append("Reward: ").append(reward); 


        return builder.toString();
    }
    public void setReward(double reward) {
        this.reward = reward;
    }

    public double getReward() {
        return reward;
    }
    public Vertex getTargetVertex(GameEngine engine){
        if(engine == null || engine.gameBoard == null){
            return null;
        }
        int vertexNumber = engine.gameBoard.getVertexNumberFromPosition(xPosition,yPosition);
        if(vertexNumber < 0){
            return null;
        }
        return engine.gameBoard.getVertex(vertexNumber);
    }



    
}