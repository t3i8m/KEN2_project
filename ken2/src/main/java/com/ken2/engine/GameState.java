package com.ken2.engine;

import com.ken2.Game_Components.Board.Game_Board;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A class for holding all the variables needed by the Game engine to call simulation on
 * Basically a class for the current state of the game
 */
public class GameState {

    public int ringsWhite;
    public int ringsBlack;
    public int chipsRemaining;

    public int ringsPlaced;
    public boolean isWhiteTurn;
    public Game_Board gameBoard;
    public GameSimulation gameSimulation;
    public ArrayList<Integer> ringVertexNumbers;

    public boolean chipPlacement;
    public ArrayList<Integer> chipNumber;
    public int selectedRingVertex;
    public int selectedChipVertex;
    public boolean chipPlaced;
    public boolean isFirstClick;
    public int chipRingVertex;
    public ArrayList<Integer> highlightedVertices;
    public HashSet<Integer> s;

    /**
     * Constructor
     */
    public GameState(){
        ringsWhite=5;
        ringsBlack=5;
        chipsRemaining=51;
        ringsPlaced=0;
        isWhiteTurn=true;
        chipPlacement=false;
        selectedRingVertex=-1;
        selectedChipVertex=-1;
        chipPlaced=false;
        isFirstClick=true;
        gameBoard=new Game_Board();
        ringVertexNumbers=new ArrayList<>();
        highlightedVertices=new ArrayList<>();
        s=new HashSet<>();
    }

    /**
     * Get current player color
     * @return
     */
    public String currentPlayerColor() {
        return isWhiteTurn ? "White" : "Black";
    }

    /**
     * Update the ring of the
     * @param playerColor color of the player to update ring count
     */
    public void updateRingCount(String playerColor){
        if (playerColor.equals("White")) {
            ringsWhite--;
        } else {
            ringsBlack--;
        }
    }

    /**
     * To reset variables for every turn
     */
    public void resetTurn() {
        isWhiteTurn = !isWhiteTurn;
        chipPlacement = true;
        chipPlaced = false;
        isFirstClick = true;
    }
}