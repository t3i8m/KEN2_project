package com.ken2.engine;

import java.util.ArrayList;

import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Vertex;

/**
 * class for each diagonal 
 */
public class Diagonal {
    private ArrayList<Move> possibleMoves = new ArrayList<Move>();
    private int[] diskPosition = new int[2];
    private Direction direction; //enum object with direction params

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
        int currentX = diskPosition[0];
        int currentY = diskPosition[1];

        int deltaX = direction.getDeltaX();
        int deltaY = direction.getDeltaY();

        while(true){
            int newX = currentX+deltaX;
            int newY = currentY+deltaY;


            if (newX < 0 || newX >= board.length || newY < 0 || newY >= board[0].length) {
                System.out.println("Out of bounds: (" + newX + ", " + newY + ")");

                break; 
            }
            System.out.println("Checking position: (" + newX + ", " + newY + ")");

            if (board[newX][newY] == null) {
                System.out.println("Found null at: (" + newX + ", " + newY + ")");

                break; 
            }


            possibleMoves.add(new Move(newX, newY));
            currentX = newX;
            currentY = newY;
        }
        return this.possibleMoves;

    }

}
