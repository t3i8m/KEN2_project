package com.ken2.engine;

import java.util.ArrayList;

import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.engine.Diagonal;

/**
 * Class to simulate all possible moves from a given disk position
 */
public class GameSimulation {

    private ArrayList<ArrayList> allPossibleMoves;

    /**
     * Initializing GameSimulation class
     */
    public GameSimulation(){
        allPossibleMoves= new ArrayList<ArrayList>();
    }

    /**
     * Method to simulate all the moves from a given position
     * 
     * @param board board of the game 2d array of vertices
     * @param xDiskPosition x position of the disk
     * @param yDiskPosition y position of the disk
     */

    // instead of the x&y distances we will use disk obj and its atributes
    public void startSimulation(Vertex[][] board, int xDiskPosition, int yDiskPosition){

        int[] diskPositions= {xDiskPosition, yDiskPosition};

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
    public ArrayList<ArrayList> getAllPossibleMoves(){
        return allPossibleMoves;
    }
}
