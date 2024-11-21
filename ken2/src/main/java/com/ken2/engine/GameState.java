package com.ken2.engine;

import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.PlayObj;
import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.bots.Bot;
import com.ken2.bots.RuleBased.RuleBasedBot;
import com.ken2.utils.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A class for holding all the variables needed by the Game engine to call simulation on
 * Basically a class for the current state of the game
 */
public class GameState implements Cloneable{

    public int ringsWhite;
    public int ringsBlack;
    public int chipsRemaining;
    public int chipsWhite;
    public int chipsBlack;

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
    public boolean isChipRemovalMode;
    public boolean isRingSelectionMode;
    private ArrayList<Vertex >vertexesOfFlippedCoins;
    private List<Integer> allPossibleCoinsToRemove;


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
        vertexesOfFlippedCoins=new ArrayList<>();
        allPossibleCoinsToRemove = new ArrayList<>();
    }

    public void setAllPossibleCoinsToRemove(List<Integer> llPossibleCoinsToRemove){
        this.allPossibleCoinsToRemove = llPossibleCoinsToRemove;
    }

    public List<Integer> getAllPossibleCoinsToRemove(){
        return this.allPossibleCoinsToRemove;
    }

    public void setVertexesOfFlippedCoins(ArrayList<Vertex> newVertexesOfFlippedCoins){
        this.vertexesOfFlippedCoins=newVertexesOfFlippedCoins;
    }

    public ArrayList<Vertex> getVertexesOfFlippedCoins(){
        return this.vertexesOfFlippedCoins;
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

    public int getChipsCountForColor(String color){
        this.updateChipsRingCountForEach();
        if(color.toLowerCase().equals("white")){
            return this.chipsWhite;
        }else{
            return this.chipsBlack;
        }
    }

    public int getRingCountForColor(String color){
        this.updateChipsRingCountForEach();
        if(color.toLowerCase().equals("white")){
            return this.ringsWhite;
        }else{
            return this.ringsBlack;
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

        public void updateChipsRingCountForEach(){
            Game_Board board = this.getGameBoard();
            Vertex[][] gboard = board.getBoard();
            this.chipsWhite=0;
            this.chipsBlack=0;
            this.ringsWhite=0;
            this.ringsBlack=0;
            for(int i = 0; i<gboard.length;i++){
                for(int j = 0; j<gboard[i].length;j++){
                    if(gboard[i][j]!=null){
                        if(gboard[i][j].getPlayObject()[0]!=null){
                            PlayObj currRing = gboard[i][j].getPlayObject()[0];
                            if(currRing.getColour().toLowerCase().equals("white")){
                                this.ringsWhite++;
                            } else{
                                this.ringsBlack++;
                        }
                        }
                        if(gboard[i][j].getPlayObject()[1]!=null){
                            PlayObj currCoin = gboard[i][j].getPlayObject()[1];
                            if(currCoin.getColour().toLowerCase().equals("white")){
                                this.chipsWhite++;
                            } else{
                                this.chipsBlack++;
                            }
                        } 
    
                        }
                        }
                        
                }
            }
    

    public int calculateStrength(String color) {
        return this.getRingCountForColor(color) * this.getChipsCountForColor(color);
    }

    /**
     * Gives a given state a value for pruning later
     * @param state current game state
     * @param ge game engine
     * @param color current player color
     * @return value of the function
     */
    public double evaluate(GameState state, GameEngine ge, String color){
        double valuation = 0;
        String opponentColor = color.equals("white") ? "black" : "white";

        /** Purpose of the variables below
         * number of coins of our colour
         * number of coins in a row of our colour

         * number of coins of opposite colour
         * number of coins in a row of opposite colour

         * Same with the rings

         * if we will get a win if we move
         * Then bonus points will be awarded LESSGOOOOO
         */
        double inOurfavour = 0.50;
        double notOurfavour= -0.25;
        double ourWin = 1;
        double theirWin = -1;

        // Calculating the chip calculation
        valuation += state.getChipsCountForColor(color)*inOurfavour + state.getChipsCountForColor(opponentColor)*notOurfavour;

        // Calculating the ring calculation
        valuation += state.getRingCountForColor(color)*inOurfavour + state.getRingCountForColor(opponentColor)*notOurfavour;

        // Calculating the win calculation
        valuation += ge.win(ge.currentState.getVertexesOfFlippedCoins()) ? ourWin : theirWin;

        return valuation;
    }


    @Override
    public GameState clone() {
        try {
            GameState copy = (GameState) super.clone();
            copy.gameBoard = new Game_Board(this.gameBoard); 
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