package com.ken2.engine;

import com.ken2.Game_Components.Board.Coin;
import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.PlayObj;
import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.bots.RuleBased.RuleBasedBot;
import com.ken2.ui.GameAlerts;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * All decision-making of the game
 */
public class GameEngine {

    public GameState currentState;
    public GameSimulation gameSimulation;

    public static int[][] vertexCoordinates;



    /**
     * Contructor
     */
    public GameEngine(){
        currentState=new GameState();
        gameSimulation=new GameSimulation();
        vertexCoordinates = new int[85][2];
        initialiseVertex();
    }

    /**
     * Method for placing the Starting rings
     * @param vertex vertex we want to place the rings
     * @return returns true or false for ring placement in GUI
     */
    public boolean placeStartingRing(int vertex,String ringColor) {
        Vertex boardVertex = currentState.gameBoard.getVertex(vertex);
        if (boardVertex != null && !boardVertex.hasRing()) {
            // ringColor = currentState.currentPlayerColor();
            // ringColor = ringColor;
            // if(ringColor.equals("White")){
            //     currentState.updateRingCount("white");

            // } else{
            //     ringColor="Black";
            //     currentState.updateRingCount("black");
            // }

            Ring newRing = new Ring(ringColor);
            boardVertex.setPlayObject(newRing); // add ring to the board data structure
            currentState.ringsPlaced++;

            if (currentState.ringsPlaced > 10) currentState.chipPlacement= true;
            currentState.resetTurn();

            return true;
        } else {
            // GameAlerts.alertRingPlacement(); // Alert for when you try to place a ring on another one during the starting phase. Does not work if bot is white
        }
        return false;
    }

    /**
     * Returns available places for the starting ring
     * @return available vertices for starting ring placement
     */
    public ArrayList<Vertex> availablePlacesForStartingRings() {
        Vertex[][] board = currentState.gameBoard.getBoard();
        return this.gameSimulation.getAllPossibleStartingRingPlaces(board);
    }


    /**
     * Place chip logic checker
     * @param vertex vertex where we check for chip placement
     * @param gc GraphicsContext
     * @return true if rules are conditions are right and false if not
     */
    public boolean placeChip(int vertex, GraphicsContext gc) {
        Vertex boardVertex = currentState.gameBoard.getVertex(vertex);


        if (boardVertex != null && boardVertex.hasRing() && !boardVertex.hasCoin()) {
            String chipColor = boardVertex.getRing().getColour();

            //condition for same ring colors
            if (!boardVertex.getRing().getColour().equalsIgnoreCase(chipColor)) {
                showAlert("Warning", "Cannot place a " + chipColor + " chip in a ring of a different color!");
                return false;
            }

            Coin newChip = new Coin(chipColor.toLowerCase());
            boardVertex.setPlayObject(newChip);
            currentState.chipRingVertex = vertex;
            System.out.println("After setting chip, hasCoin at (" + boardVertex.getXposition() + ", " + boardVertex.getYposition() + "): " + boardVertex.hasCoin());
            currentState.chipNumber.add(vertex);
            currentState.chipsRemaining--;
            currentState.chipPlaced = true;//after chip placement we can move the ring
            currentState.resetTurn();
            return true;
        } else {
            showAlert("Warning", "Cannot place a chip here");
        }
        return false;
    
    }

    /**
     * Initial check if the vertices are valid for ringMove method
     * @param frmVertex initial vertex
     * @param toVertex target vertex
     * @param availableMoves list of available moves
     * @return true if vertices valid for ringMove and false otherwise
     */
    public boolean checkPlaceRingVertex(int frmVertex,int toVertex,ArrayList<Integer> availableMoves) {
        Vertex targetVertex = currentState.gameBoard.getVertex(toVertex);
        if (!availableMoves.contains(toVertex)){
            showAlert("INVALID", "JJDJDJD");
            return false;
        }
        else if (targetVertex.hasRing() || currentState.chipNumber.contains(toVertex)) {
            showAlert("Invalid Move", "Cannot move ring here as it already has an object.");
            return false;
        }
        return true;
    }


    /**
     * simulate and get the possible vertices
     * @param boardVertex the starting vertex
     */
    public ArrayList<Move> possibleMoves(Vertex boardVertex) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            Diagonal diagonal = new Diagonal(direction, new int[]{boardVertex.getXposition(), boardVertex.getYposition()}, currentState.gameBoard);
            possibleMoves.addAll(diagonal.moveAlongDiagonal(currentState.gameBoard.getBoard()));
        }
        return possibleMoves;
    }

    // gets ring positions and returns all of the moves from these positions
    public HashMap<Vertex, ArrayList<Move>> getAllMovesFromAllPositions(ArrayList<Vertex> allRingPositions){
        HashMap<Vertex, ArrayList<Move>> allMoves = new HashMap<Vertex, ArrayList<Move>>();
        for(Vertex v: allRingPositions){
            allMoves.put(v, this.possibleMoves(v));
        }
        return allMoves;
    }

    public GameState getGameState(){
       
        return this.currentState;
    }







    /**
     * Initializes the coordinates of the vertices, Maps the vertex number with coordinates
     */
    private void initialiseVertex() {
        int index = 0;
        // a
        for (int i = 0; i < 4; i++) {
            vertexCoordinates[index] = new int[] { 175 - 38 * 4, (i * 44) + 19 + 44*3};
            index++;
        }
        // b
        for (int i = 0; i < 7; i++) {
            vertexCoordinates[index] = new int[] { 175 - 38 * 3, (i * 44) + 19 + 66 };
            index++;
        }
        // c
        for (int i = 0; i < 8; i++) {
            vertexCoordinates[index] = new int[] { 175 - 38 * 2, ((i * 44) + 19 + 44)};
            index++;
        }
        // d
        for (int i = 0; i < 9; i++) {
            vertexCoordinates[index] = new int[] { 175 - 38, ((i * 44) + 19 + 22)};
            index++;
        }
        // e
        for (int i = 0; i < 10; i++) {
            vertexCoordinates[index] = new int[] { 175, (i * 44) + 19 };
            index++;
        }
        // f
        for (int i = 0; i < 9; i++) {
            vertexCoordinates[index] = new int[] { 175 + 38, (i * 44) + 19 + 22};
            index++;
        }
        // g
        for (int i = 0; i < 10; i++) {
            vertexCoordinates[index] = new int[] { 175 + 38*2, (i * 44) + 19};
            index++;
        }
        // h
        for (int i = 0; i < 9; i++) {
            vertexCoordinates[index] = new int[] { 175 + 38 * 3, (i * 44) + 19 + 22};
            index++;
        }
        // i
        for (int i = 0; i < 8; i++) {
            vertexCoordinates[index] = new int[] { 175 + 38 * 4, (i * 44) + 19 + 44};
            index++;
        }
        // j
        for (int i = 0; i < 7; i++) {
            vertexCoordinates[index] = new int[] { 175 + 38 * 5, (i * 44) + 19 + 66};
            index++;
        }
        // k
        for (int i = 0; i < 4; i++) {
            vertexCoordinates[index] = new int[] { 175 + 38 * 6, (i * 44) + 19 + 44 * 3};
            index++;
        }
    }

    /**
     * A function to select a vertex when give player Clicks the screen
     * @param xCoordinate X coordinate of the clicked location
     * @param yCoordinate Y coordinate of the clicked location
     * @return vertex number as a integer
     */
    public int findClosestVertex(double xCoordinate, double yCoordinate){

        System.out.println();

        for(int i = 0 ; i < 85; i++){
            double vX = vertexCoordinates[i][0] + 18;
            double vY = vertexCoordinates[i][1] + 18;

            double xDist = Math.abs(xCoordinate - vX);
            double yDist = Math.abs(yCoordinate - vY);

            if(xDist<=10 && yDist <=10){
                System.out.println("Vertex Clicked: " + i);
                return i;
            }
        }
        return -1;
    }

    /**
     * A helper function for pushing alerts during gameplay
     * @param title
     * @param message
     */
    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Return the vertex coordinates array
     * @return integer matrix
     */
    public int[][] getVertexCoordinates() {
        return vertexCoordinates;
    }


    public int[] getcoordinates(int vertex){
        return vertexCoordinates[vertex];
    }

    /**
     * Set the parameters of the game
     */
    public void resetGame(){
        currentState=new GameState();
        vertexCoordinates = new int[85][2];
        initialiseVertex();
    }


}