package com.ken2.engine;

import java.util.ArrayList;

import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.engine.Diagonal;
import com.ken2.Game_Components.Board.Game_Board;


/**
 * Class to simulate all possible moves from a given disk position
 */
public class GameSimulation {

    private ArrayList<ArrayList<Move>> allPossibleMoves;

    /**
     * Initializing GameSimulation class
     */
    public GameSimulation(){
        allPossibleMoves= new ArrayList<ArrayList<Move>>();
    }

    /**
     * Method to simulate all the moves from a given position
     * 
     * @param board board of the game 2d array of vertices
     * @param xRingPosition x position of the disk
     * @param yRingPosition y position of the disk
     */

    // instead of the x&y distances we will use disk obj and its atributes
    public void startSimulation(Vertex[][] board, int xRingPosition, int yRingPosition){
        allPossibleMoves.clear();
        int[] diskPositions= {xRingPosition, yRingPosition};

        for (Direction direction : Direction.values()) {
            Diagonal currDiagonal= new Diagonal(direction, diskPositions);
            allPossibleMoves.add(currDiagonal.moveAlongDiagonal(board));
        }
    }

    /**
     * getter for the possible moves
     * 
     * @return ArrayList with all of the possible moves
     */
    public ArrayList<ArrayList<Move>> getAllPossibleMoves(){
        return allPossibleMoves;
    }

    /**
     * method to find all available places to put a ring
     * @param board
     * @return
     */
    public ArrayList<Vertex> getAllPossibleStartingRingPlaces(Vertex[][] board){
        ArrayList<Vertex> allPossibleStartingPlaces = new ArrayList<>();
        for(int i = 0; i<board.length;i++){
            for(int j = 0; j<board[i].length;j++){
                if(board[i][j]!=null){
                    if(board[i][j].getPlayObject()[0]==null){
                        allPossibleStartingPlaces.add(board[i][j]);
                    } else{
                        allPossibleStartingPlaces.add(null);
                    }

                }
            }
        }
        return allPossibleStartingPlaces;
    }

    /**
     * Determines the Direction between two vertices.
     * 
     * @param startX The x-coordinate of the start vertex.
     * @param startY The y-coordinate of the start vertex.
     * @param targetX The x-coordinate of the target vertex.
     * @param targetY The y-coordinate of the target vertex.
     * @return The Direction from startVertex to targetVertex, or null if not aligned.
     */
    public Direction getDirectionBetween(int startX, int startY, int targetX, int targetY) {
        int deltaX = targetX - startX;
        int deltaY = targetY - startY;

        for (Direction direction : Direction.values()) {
            int dirDeltaX = direction.getDeltaX();
            int dirDeltaY = direction.getDeltaY();

            if (dirDeltaX == 0 && deltaX != 0) continue;
            if (dirDeltaY == 0 && deltaY != 0) continue;

            int stepsX = (dirDeltaX != 0) ? deltaX / dirDeltaX : 0;
            int stepsY = (dirDeltaY != 0) ? deltaY / dirDeltaY : 0;

            if (dirDeltaX == 0) {
                if (deltaX == 0 && stepsY > 0 && deltaY % dirDeltaY == 0) {
                    return direction;
                }
            } else if (dirDeltaY == 0) {
                if (deltaY == 0 && stepsX > 0 && deltaX % dirDeltaX == 0) {
                    return direction;
                }
            } else {
                if (stepsX == stepsY && stepsX > 0 && deltaX % dirDeltaX == 0 && deltaY % dirDeltaY == 0) {
                    return direction;
                }
            }
        }

        return null; // No matching direction found
    }

    // simulate a move from the given start/target positions
    public Move simulateMove(Game_Board board, Vertex startVertex, Vertex targetVertex){

        int[] targetPosition = board.getVertexPositionByNumber(targetVertex.getVertextNumber());
        int[] startPosition = board.getVertexPositionByNumber(startVertex.getVertextNumber());

        if (targetPosition == null || startPosition==null) {
            System.out.println("Invalid vertex number: " + targetVertex.getVertextNumber() + startVertex.getVertextNumber());
            return null;
        }

        Direction moveDirection = getDirectionBetween(startPosition[0], startPosition[1], targetPosition[0], targetPosition[1]);
        if (moveDirection == null) {
            System.out.println("The move is not in a straight line direction.");
            return null;
        } 
        Diagonal diagonal = new Diagonal(moveDirection, startPosition);
        ArrayList<Move> possibleMoves = diagonal.moveAlongDiagonal(board.getBoard());
        for (Move move : possibleMoves) {
            if (move.getXposition() == targetPosition[0] && move.getYposition() == targetPosition[1]) {
                return move;
            }
        }
        System.out.println("The target position is not a valid move in this direction.");
        return null;
    }
}