package com.ken2.engine;

import java.util.ArrayList;

import com.ken2.Game_Components.Board.Coin;
import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Vertex;

/**
 * class for each diagonal 
 */
public class Diagonal {
    private ArrayList<Move> possibleMoves = new ArrayList<Move>();
    private int[] diskPosition = new int[2];
    private Direction direction; //enum object with direction params
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
        int currentX = diskPosition[0];
        int currentY = diskPosition[1];

        int deltaX = direction.getDeltaX();
        int deltaY = direction.getDeltaY();

        boolean ringPassesCoin = false;
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
               else{
                   if(board[newX][newY].getPlayObject()[0]!=null){ // check for the ring
                    break;
                } else if (board[newX][newY].getPlayObject()[1]!=null){ // check for the coin
                       Coin coin = (Coin)board[newX][newY].getPlayObject()[1];
                       coinFlip.add(coin);
                       ringPassesCoin = true;//ring passes coin, we flip them

                } else{//empty position
                       if(ringPassesCoin) {
                           possibleMoves.add(new Move(newX,newY, new ArrayList<Coin>(coinFlip)));//move to empty space
                           break;
                       }else {//ring didnt pass the coin
                           possibleMoves.add(new Move(newX, newY));
                       }
                   }

            }


            currentX = newX;
            currentY = newY;
        }
        return this.possibleMoves;

    }

}