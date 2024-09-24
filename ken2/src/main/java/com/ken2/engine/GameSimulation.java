package com.ken2.engine;

import java.util.ArrayList;

import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.engine.Diagonal;


public class GameSimulation {

    private ArrayList<ArrayList> allPossibleMoves;

    public GameSimulation(){
        allPossibleMoves= new ArrayList<ArrayList>();
    }

    // instead of the x&y distances we will use disk obj and its atributes
    public void startSimulation(Game_Board boardObj, int xDiskPosition, int yDiskPosition){

        int[] diskPositions= {xDiskPosition, yDiskPosition};
        Vertex[][] board = boardObj.getBoard();
        // Vertex currVertex = board[xDiskPosition][yDiskPosition];

        for (Direction direction : Direction.values()) {
            Diagonal currDiagonal= new Diagonal(direction, diskPositions);
            allPossibleMoves.add(currDiagonal.moveAlongDiagonal(board));
        }
    }

    public ArrayList<ArrayList> getAllPossibleMoves(){
        return allPossibleMoves;
    }
}
