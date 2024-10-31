package com.ken2.engine;

import java.util.ArrayList;

import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.engine.Diagonal;

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
     * @param xDiskPosition x position of the disk
     * @param yDiskPosition y position of the disk
     */
    // instead of the x&y distances we will use disk obj and its atributes
    public void startSimulation(Vertex[][] board, int xDiskPosition, int yDiskPosition){
        allPossibleMoves.clear();
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
}