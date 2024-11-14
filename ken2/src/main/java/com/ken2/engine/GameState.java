package com.ken2.engine;

import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.bots.Bot;
import com.ken2.bots.RuleBased.RuleBasedBot;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A class for holding all the variables needed by the Game engine to call simulation on
 * Basically a class for the current state of the game
 */
public class GameState implements Cloneable{

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
    public int chipRingVertex;
    public int totalChipsFlipped;


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
        totalChipsFlipped = 0;
        chipNumber=new ArrayList<>();
        gameBoard=new Game_Board();
    }

    /**
     * Get current player color
     * @return
     */
    public String currentPlayerColor() {
        return isWhiteTurn ? "White" : "Black";
    }

    public void setIsWhite(boolean newVal){
        isWhiteTurn=newVal;
    }

    /**
     * Get current gameBoard
     * @return gameBoard
     */
    public Game_Board getGameBoard(){
        return this.gameBoard;
    }

    // public void switchTurn() {
    //     isWhiteTurn = !isWhiteTurn;
    //     chipPlacement = true;
    //     chipPlaced = false;
    //     selectedRingVertex = -1;
    //     selectedChipVertex = -1;
    // }
    

    /**
     * Update the ring of the
     * @param playerColor color of the player to update ring count
     */
    public void updateRingCount(String playerColor){
        playerColor = playerColor.toLowerCase();
        if (playerColor.equals("white")) {
            this.ringsWhite--;
        } else {
            this.ringsBlack--;
        }
    }

    /**
     * To reset variables for every turn
     */
    public void resetTurn() {
        // isWhiteTurn = !isWhiteTurn;
        // chipPlacement = true;
        // chipPlaced = false;
    }

    public ArrayList<Vertex> getAllVertexOfColor(String color){
        Vertex[][] board = gameBoard.getBoard().clone();
        ArrayList<Vertex> allPositions = new ArrayList<>();
        for(Vertex row[]: board){
            for(Vertex v: row){
                if(v!=null){
                    if(v.hasRing()){
                        Ring currR = (Ring) v.getRing();
                        String rColor = currR.getColour().toLowerCase();
                        if(rColor.equals(color.toLowerCase())){
                            allPositions.add(v);
                        }
                    }
                } 
            }
        }
        return allPositions;
    }

    @Override
    public GameState clone() {
        try {
            GameState copy = (GameState) super.clone();
            copy.gameBoard = new Game_Board(this.gameBoard); // Клонируем доску
            copy.chipNumber = this.chipNumber != null ? new ArrayList<>(this.chipNumber) : new ArrayList<>();
            copy.ringVertexNumbers = this.ringVertexNumbers != null ? new ArrayList<>(this.ringVertexNumbers) : new ArrayList<>();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
    

    public boolean isBotTurn(ArrayList<Bot> bots) {
        boolean isWhiteTurn = this.isWhiteTurn;
        for (Bot bot : bots) {
            if (("White".equals(bot.getColor()) && isWhiteTurn) || ("Black".equals(bot.getColor()) && !isWhiteTurn)) {
                return true;
            }
        }
        return false;
    }



    
}