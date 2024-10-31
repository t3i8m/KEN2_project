package com.ken2.engine;

import java.util.ArrayList;

import com.ken2.Game_Components.Board.Coin;
import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Vertex;

/**
 * class for each diagonal 
 */
public class Diagonal {
    private int[] diskPosition = new int[2];
    private Direction direction; //enum object with direction params
    private ArrayList<Move> possibleMoves = new ArrayList<Move>();
    private ArrayList<Coin>coinFlip = new ArrayList<Coin>();


    public Diagonal(Direction direction, int[] diskPosition){
        this.direction = direction;
        this.diskPosition = diskPosition;
    }

    /**
     * getter for a direction
     * @return
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * setter for a direction
     * @return
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * class to get possible points on the diagonal by following specific rules of the game Yinsh
     * @param board
     * @return
     */
    
    // implement logic to check rules
    public ArrayList<Move> moveAlongDiagonal(Vertex[][] board) {
        possibleMoves.clear();
        coinFlip.clear();

        int currentX = diskPosition[0];
        int currentY = diskPosition[1];
        int deltaX = direction.getDeltaX();
        int deltaY = direction.getDeltaY();
        boolean hasPassedMarker = false;

        while (true) {
            int newX = currentX + deltaX;
            int newY = currentY + deltaY;

            // Check if the new coordinates are within bounds
            if (newX < 0 || newX >= board.length || newY < 0 || newY >= board[0].length) {
                break;
            }

            Vertex currentVertex = board[newX][newY];

            // Stop if there's a ring in the path
            if (currentVertex != null && currentVertex.hasRing()) {
                break;
            }

            // If there’s a coin, mark it for flipping and continue
            if (currentVertex != null && currentVertex.hasCoin()) {
                coinFlip.add((Coin) currentVertex.getPlayObject()[1]);
                hasPassedMarker = true;
            } else {
                // If we’ve passed markers and now find an empty spot, mark this as a possible move
                if (hasPassedMarker) {
                    possibleMoves.add(new Move(newX, newY, new ArrayList<>(coinFlip), direction));
                    break; // Stop after reaching the first empty cell after passing markers
                } else {
                    // Add empty cell as a possible move if we haven’t passed any markers
                    possibleMoves.add(new Move(newX, newY, direction));
                }
            }

            // Update position to continue along the direction
            currentX = newX;
            currentY = newY;
        }

        return possibleMoves;
    }




}