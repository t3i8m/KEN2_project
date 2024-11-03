package com.ken2.engine;

import com.ken2.Game_Components.Board.Coin;
import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Vertex;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class GameEngine {

    private int chipsRemaining = 51;
    private int ringBlack = 5;
    private int ringWhite = 5;

    private int ringsPlaced;

    private boolean isWhiteTurn;

    private Game_Board gameBoard;
    private GameSimulation gameSimulation;

    private int[][] vertexCoordinates = new int[85][2];

    private boolean chipPlacement = false;
    private ArrayList<Integer> chipNumber = new ArrayList<>();


    /**
     * Constructor
     */
    public GameEngine(){
        gameBoard = new Game_Board();
        gameSimulation = new GameSimulation();
        initialiseVertex();
    }

    /**
     * return the closest vertex closest to the mouse clicks
     * @param x x-coordinates
     * @param y y-coordinates
     * @return the vertex index
     */
    public int findClosestVertex(double x, double y){

        System.out.println();

        for(int i = 0 ; i < 85; i++){
            double vX = vertexCoordinates[i][0] + 18;
            double vY = vertexCoordinates[i][1] + 18;

            double xDist = Math.abs(x - vX);
            double yDist = Math.abs(y - vY);

            if(xDist<=10 && yDist <=10){
                System.out.println("Vertex Clicled: " + i);
                return i;
            }
        }

        return -1;
    }

    public ArrayList<ArrayList<Move>> getAllPossibleMoves(int vertexPositionX, int vertexPositionY){
        Vertex[][] board = gameBoard.getBoard();
        this.gameSimulation.startSimulation(board, vertexPositionX, vertexPositionY);
        return this.gameSimulation.getAllPossibleMoves();
    }

    /**
     * Fill the vertex coordinates array
     */
    public void initialiseVertex() {
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
     * Display alerts for the
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
     * Find the available places for the start rings
     * @param vertex from where we want
     * @return
     */
    public ArrayList<Vertex> AvailablePlacesForStartingRings(int vertex) {
        Vertex[][] board = this.gameBoard.getBoard();
        ArrayList<Vertex> availablePlaces = this.gameSimulation.getAllPossibleStartingRingPlaces(board);
        return availablePlaces;
    }



    /**
     * updates the number of
     * @param chipsRemainText
     */
    public void updateChipsRemaining(Text chipsRemainText) {
        if (chipsRemaining > 0) {
            chipsRemaining--;  // Decrement chips remaining
            chipsRemainText.setText("Chips Remaining: " + chipsRemaining);  // Update display text
        }
    }

    /**
     * updates the number of black rings
     * @param ringBlackRemainingText
     */
    public void updateBlackRing(Text ringBlackRemainingText){
        if(ringBlack >0){
            ringBlack--;
            ringBlackRemainingText.setText("Black Ring Remaining: " + ringBlack);
        }
    }

    /**
     * updates the number of white rings
     * @param ringWhiteRemainingText
     */
    public void updateWhiteRing(Text ringWhiteRemainingText){
        if(ringWhite >0){
            ringWhite--;
            ringWhiteRemainingText.setText("White Ring Remaining: " + ringWhite);
        }
    }

    public void updateChipNumber(int vertex){chipNumber.add(vertex);}

    public void increamentRingsPlaced(){ringsPlaced++;}

    public void toggleTurn(){isWhiteTurn = !isWhiteTurn;}

    public void updateChipPlacement(boolean bool){chipPlacement=bool;}

    public boolean getisWhiteTurn(){ return isWhiteTurn;}

    public Game_Board getGameBoard(){
        return gameBoard;
    }

    public int getRingBlack(){return ringBlack;}

    public int getRingWhite(){return ringWhite;}

    public int getRingsPlaced(){return ringsPlaced;}

    public int getChipsRemaining() {return chipsRemaining;}

    public int[][] getVertexCoordinates(){return vertexCoordinates;}

    public ArrayList<Integer> getChipNumber() {return chipNumber;}

    public boolean getChipPalcement(){return chipPlacement;}

    
}