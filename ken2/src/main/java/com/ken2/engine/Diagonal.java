package com.ken2.engine;

import java.util.ArrayList;

import com.ken2.Game_Components.Board.Coin;
import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.PlayObj;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.ui.MainAppcopy;



/**
 * class for each diagonal 
 */
public class Diagonal {
    private int[] diskPosition = new int[2];
    private Direction direction; //enum object with direction params
    private ArrayList<Move> possibleMoves = new ArrayList<Move>();
    private ArrayList<Coin>coinFlip = new ArrayList<>();
    private Game_Board gameBoard;




    public Diagonal(Direction direction, int[] diskPosition, Game_Board gameBoard){
        this.direction = direction;
        this.diskPosition = diskPosition;
        this.gameBoard = gameBoard;
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

            if (newX < 0 || newX >= board.length || newY < 0 || newY >= board[0].length) {
                break;
            }
            if (board[newX][newY]!= null && board[newX][newY].hasRing()) {
                break;
            }

            if (board[newX][newY] != null && board[newX][newY].hasCoin()) {
                PlayObj playObject = board[newX][newY].getPlayObject()[1];
                if (playObject instanceof Coin) {
                    coinFlip.add((Coin) playObject);
                }
                hasPassedMarker = true;
            } else {
                if (hasPassedMarker) {
                    possibleMoves.add(new Move(newX, newY, new ArrayList<>(coinFlip), direction));

                } else {
                    possibleMoves.add(new Move(newX, newY, direction));
                }
            }

            currentX = newX;
            currentY = newY;
        }
        // flipCoins();
        return possibleMoves;
    }
    // public void flipCoins(ArrayList<Coin> coinFlips) {
    //     for (Coin coin : coinFlips) {
    //         coin.flipCoin();
    //         Vertex vertex = gameBoard.getVertexByCoin(coin);
    //             System.out.println("Coin color flipped and updated on the board at vertex: " + vertex.getVertextNumber());

    //     }
    // }



}