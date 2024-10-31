package com.ken2.ui;
import java.util.ArrayList;

import com.ken2.Game_Components.Board.*;
import com.ken2.engine.GameSimulation;
import com.ken2.engine.Move;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//add a bunch of mouse events that communcates with the game
//on every mouse event call update board

public class MainApp extends Application {
    
    private Image ringBImage = new Image("file:ken2\\assets\\black ring.png");
    private Image chipBImage = new Image("file:ken2\\assets\\black chip.png");
    private Image ringWImage = new Image("file:ken2\\assets\\white ring.png");
    private Image chipWImage = new Image("file:ken2\\assets\\white chip.png");
    private int fieldDimension = 470;
    private int pieceDimension = 37;
    private int chipsRemaining = 51;
    private int ringBlack = 5;
    private int ringWhite = 5;
    private Color inactiveScoreRingColor = Color.rgb(200, 200, 200);
    private Color activeScoreRingColor = Color.rgb(90, 150, 220);
    private int[][] vertexCoordinates = new int[85][2];
    private ComboBox whitePlayerComboBox;
    private ComboBox blackPlayerComboBox;
    private int ringsPlaced;
    private boolean isWhiteTurn;
    private Game_Board gameBoard;
    private GameSimulation gameSimulation;
    private GridPane root;
    private Pane fieldPane;
    private ArrayList<Integer> ringVertexNumbers;
    private Text chipsRemainText;
    private Text ringWhiteRemainingText;
    private Text ringBlackRemainingText;

    private boolean chipPlacement = false;
    private ArrayList<Integer> chipNumber = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Compare Players with Yinsh");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);

        // we create a game board obj here
        this.gameBoard = new Game_Board();
        this.gameSimulation = new GameSimulation();
        this.ringVertexNumbers = new ArrayList<>();


        root = new GridPane();
        root.setPadding(new Insets(5, 5, 5, 5));
        root.setVgap(10);
        root.setHgap(10);

        Scene scene = new Scene(root, 800, 600, Color.GRAY);

        // game field elements
        isWhiteTurn = true;
        ringsPlaced = 0;
        Canvas gameBoardCanvas = new Canvas();
        gameBoardCanvas.setWidth(fieldDimension);
        gameBoardCanvas.setHeight(fieldDimension);

        
        Canvas playObjectCanvas = new Canvas();
        playObjectCanvas.setWidth(fieldDimension);
        playObjectCanvas.setHeight(fieldDimension);

        GraphicsContext gcB = gameBoardCanvas.getGraphicsContext2D();
        GraphicsContext gcP = playObjectCanvas.getGraphicsContext2D();

        initialiseVertex();
        drawBoard(gcB);

        // gameBoardCanvas.setOnMouseClicked((MouseEvent e) -> {
        //     double x = e.getX();
        //     double y = e.getY();
        //     int vertex = findClosestVertex(x,y);

        //     if ((ringsPlaced < 10) && (vertex >= 0)){
        //         placeStartingRing(vertex, gcP);
        //         displayAvailablePlacesForStartingRings(vertex);
        //     } else if (ringsPlaced>=10){
        //         removeCircleIndicators();

        //         if(this.ringVertexNumbers.contains(vertex)){
        //             System.out.println("CONTAINS");
        //         }

        //         int[] vertexPosition = this.gameBoard.getVertexPositionByNumber(vertex);
        //         System.out.println(vertexPosition[0]);
        //         Vertex[][] board = this.gameBoard.getBoard();
        //         this.gameSimulation.startSimulation(board, vertexPosition[0], vertexPosition[1]);
        //         ArrayList<ArrayList<Move>> allPossibleMoves = this.gameSimulation.getAllPossibleMoves();
        //         displayPossibleMoves(allPossibleMoves);
        //     }

        // });
        playObjectCanvas.setOnMouseClicked((MouseEvent e)-> handleFieldClick(e, gcP));


        // info elements
        whitePlayerComboBox = new ComboBox<>();
        whitePlayerComboBox.getItems().add("Human Player");
        whitePlayerComboBox.getItems().add("AI Player");
        whitePlayerComboBox.getSelectionModel().selectFirst();

        blackPlayerComboBox = new ComboBox<>();
        blackPlayerComboBox.getItems().add("Human Player");
        blackPlayerComboBox.getItems().add("AI Player");
        blackPlayerComboBox.getSelectionModel().selectFirst();

        Text whitePlayerLabel = new Text();
        whitePlayerLabel.setText("White");

        Text blackPlayerLabel = new Text();
        blackPlayerLabel.setText("Black");

        chipsRemainText = new Text();
        chipsRemainText.setText("Chips Remaining: " + chipsRemaining);

        ringBlackRemainingText = new Text();
        ringBlackRemainingText.setText("Black Rings Remaining: " + ringBlack);

        ringWhiteRemainingText = new Text();
        ringWhiteRemainingText.setText("White Rings Remaining: " + ringWhite);

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> resetBoard());
        fieldPane = new Pane();
        fieldPane.setPrefSize(fieldDimension, fieldDimension);
        root.add(fieldPane, 1, 1, 5, 5);

        // score elements
        Circle scoreRingeW1 = makeScoreCircle();
        Circle scoreRingeW2 = makeScoreCircle();
        Circle scoreRingeW3 = makeScoreCircle();
        Circle scoreRingeB1 = makeScoreCircle();
        Circle scoreRingeB2 = makeScoreCircle();
        Circle scoreRingeB3 = makeScoreCircle();

        root.add(whitePlayerLabel,0, 0);
        root.add(blackPlayerLabel, 7, 0);
        root.add(whitePlayerComboBox, 0, 1);
        root.add(blackPlayerComboBox, 7, 1);
        root.add(gameBoardCanvas, 1, 1, 5, 5);
        root.add(playObjectCanvas, 1, 1, 5, 5);
        root.add(chipsRemainText, 3, 0);
        root.add(ringBlackRemainingText, 5,0);
        root.add(ringWhiteRemainingText, 1,0);
        root.add(scoreRingeW1, 0, 2);
        root.add(scoreRingeW2, 0, 3);
        root.add(scoreRingeW3, 0, 4);
        root.add(scoreRingeB1, 7, 2);
        root.add(scoreRingeB2, 7, 3);
        root.add(scoreRingeB3, 7, 4);
        root.add(resetButton, 0 , 7);


        primaryStage.setScene(scene);
        primaryStage.show();
        displayAvailablePlacesForStartingRings(0);
    }
    private int selectedRingVertex = -1; // To track the ring selected for movement


    private void handleFieldClick(MouseEvent e, GraphicsContext gc) {
        double x = e.getX();
        double y = e.getY();
        int vertex = findClosestVertex(x, y);

        if (vertex >= 0) {
            if (ringsPlaced < 10) {
                // Ring placement phase
                placeStartingRing(vertex, gc);
                displayAvailablePlacesForStartingRings(vertex);
            } else {
                removeCircleIndicators();

                if (chipPlacement) {
                    placeChip(vertex, gc);  // Place chip and remain in place
                } else {
                    // Handle selecting and moving a ring
                    if (selectedRingVertex == -1) {
                        // First click: select the ring to move
                        selectRingForMovement(vertex);
                    } else {
                        // Second click: attempt to move the selected ring to the new vertex
                        moveRing(selectedRingVertex, vertex, gc);
                        selectedRingVertex = -1; // Reset selection after moving
                    }
                }
            }
        }
        System.out.println(this.gameBoard.strMaker());
    }
    private void selectRingForMovement(int vertex) {
        Vertex boardVertex = this.gameBoard.getVertex(vertex);

        // Check if the selected vertex has a ring and is of the correct turn color
        if (boardVertex != null && boardVertex.hasRing() && boardVertex.getRing().getColour().equals(isWhiteTurn ? "White" : "Black")) {
            selectedRingVertex = vertex; // Track the selected ring's location
            displayPossibleMovesForRing(boardVertex); // Display possible moves for selected ring
        } else {
            showAlert("Invalid Selection", "Please select a valid ring to move.");
        }
    }

    private void moveRing(int fromVertex, int toVertex, GraphicsContext gc) {
        Vertex sourceVertex = this.gameBoard.getVertex(fromVertex);
        Vertex targetVertex = this.gameBoard.getVertex(toVertex);

        // Ensure the target position is unoccupied and does not contain a chip
        if (targetVertex != null && !targetVertex.hasRing() && !chipNumber.contains(toVertex)) {
            // Clear the original ring position on the canvas
            gc.clearRect(vertexCoordinates[fromVertex][0], vertexCoordinates[fromVertex][1], pieceDimension, pieceDimension);

            // Move the ring to the new target vertex
            Ring ringToMove = (Ring) sourceVertex.getRing();
            targetVertex.setPlayObject(ringToMove); // Place ring in target vertex
            sourceVertex.setPlayObject(null); // Clear ring from original position

            // Draw ring at the new position
            Image ringImage = isWhiteTurn ? ringWImage : ringBImage;
            gc.drawImage(ringImage, vertexCoordinates[toVertex][0], vertexCoordinates[toVertex][1], pieceDimension, pieceDimension);

            // Toggle the turn and switch to chip placement phase
            isWhiteTurn = !isWhiteTurn;
            chipPlacement = true;
        } else {
            showAlert("Invalid Move", "The selected position is not valid for movement.");
        }
    }

    // Method to place chips on the board
    private void placeChip(int vertex, GraphicsContext gc) {
        Vertex boardVertex = this.gameBoard.getVertex(vertex);
        if (boardVertex != null && boardVertex.hasRing() && !boardVertex.hasCoin()) {
            String chipColor = isWhiteTurn ? "White" : "Black";
            Image chipImage = isWhiteTurn ? chipWImage : chipBImage;

            Coin newChip = new Coin(chipColor);
            boardVertex.setPlayObject(newChip);


            gc.drawImage(chipImage, vertexCoordinates[vertex][0] + pieceDimension / 4,
                    vertexCoordinates[vertex][1] + pieceDimension / 4, pieceDimension / 2, pieceDimension / 2);

            chipNumber.add(vertex);
            updateChipsRemaining();
            chipPlacement = false;
            displayPossibleMovesForRing(boardVertex);

            //isWhiteTurn = !isWhiteTurn;

        } else {
            showAlert("Warning", "Cannot place a chip on top of another chip or an empty space!");
            System.out.println("Attempted to place a chip on an invalid vertex " + vertex);  // Debug statement
        }
    }

    private void displayPossibleMovesForRing(Vertex boardVertex) {
        int[] ringPosition = {boardVertex.getXposition(), boardVertex.getYposition()};
        Vertex[][] board = this.gameBoard.getBoard();

        // Start simulation to find eligible moves
        this.gameSimulation.startSimulation(board, ringPosition[0], ringPosition[1]);
        ArrayList<ArrayList<Move>> allPossibleMoves = this.gameSimulation.getAllPossibleMoves();

        // Indicate possible moves
        for (ArrayList<Move> directionMoves : allPossibleMoves) {
            for (Move move : directionMoves) {
                int x = move.getXposition();
                int y = move.getYposition();
                int moveVertexNumber = this.gameBoard.getVertexNumberFromPosition(x, y);

                // Only proceed if moveVertexNumber is valid
                if (moveVertexNumber != -1 && !chipNumber.contains(moveVertexNumber)) {
                    int pixelX = vertexCoordinates[moveVertexNumber][0];
                    int pixelY = vertexCoordinates[moveVertexNumber][1];

                    Circle possibleMoveIndicator = new Circle();
                    possibleMoveIndicator.setCenterX(pixelX + pieceDimension / 2);
                    possibleMoveIndicator.setCenterY(pixelY + pieceDimension / 2);
                    possibleMoveIndicator.setRadius(7);
                    possibleMoveIndicator.setFill(Color.GREEN);

                    fieldPane.getChildren().add(possibleMoveIndicator);
                }
            }
        }
    }
    private void checkWinningPosition(int vertex) {
        ///five in a row
    }
    private void updateChipsRemaining() {
        if (chipsRemaining > 0) {
            chipsRemaining--;  // Decrement chips remaining
            chipsRemainText.setText("Chips Remaining: " + chipsRemaining);  // Update display text
        }
    }
    private void updateBlackRing(){
        if(ringBlack >0){
            ringBlack--;
            ringBlackRemainingText.setText("Black Ring Remaining: " + ringBlack);
        }
    }
    private void updateWhiteRing(){
        if(ringWhite >0){
            ringWhite--;
            ringWhiteRemainingText.setText("White Ring Remaining: " + ringWhite);
        }

    }

    private void displayPossibleMoves(ArrayList<ArrayList<Move>> allPossibleMoves){
        removeCircleIndicators();
        Vertex[][] board = this.gameBoard.getBoard();
        for(ArrayList<Move> currentDirectionMoves: allPossibleMoves){
            if(!currentDirectionMoves.isEmpty()){
                for(Move currentMove: currentDirectionMoves){
                    int[] currentMovesCoordinates = {currentMove.getXposition(), currentMove.getYposition()};
                    int vertexNumber = board[currentMovesCoordinates[0]][currentMovesCoordinates[1]].getVertextNumber();
                    // System.out.println(currentMove.getXposition());
                    // System.out.println(currentMove.getYposition());
                    // System.out.println("Vertex number: "+Integer.toString(board[currentMovesCoordinates[0]][currentMovesCoordinates[1]].getVertextNumber()));
                    Circle availableCircle = new Circle();
                    availableCircle.setCenterX(vertexCoordinates[vertexNumber][0] + pieceDimension / 2);
                    availableCircle.setCenterY(vertexCoordinates[vertexNumber][1] + pieceDimension / 2);
                    availableCircle.setRadius(7);
                    availableCircle.setFill(Color.LIGHTGREEN);
                    fieldPane.getChildren().add(availableCircle);
                }

            }
        }

    }

    private void removeCircleIndicators(){
        fieldPane.getChildren().removeIf(node -> node instanceof Circle);
    }

    private void displayAvailablePlacesForStartingRings(int vertex) {
        Vertex[][] board = this.gameBoard.getBoard();
        ArrayList<Vertex> availablePlaces = this.gameSimulation.getAllPossibleStartingRingPlaces(board);
    
        for (Vertex v : availablePlaces) {
           
            if (v != null) {
                Circle availableCircle = new Circle();
                availableCircle.setCenterX(vertexCoordinates[v.getVertextNumber()][0] + pieceDimension / 2);
                availableCircle.setCenterY(vertexCoordinates[v.getVertextNumber()][1] + pieceDimension / 2);
                availableCircle.setRadius(7);
                availableCircle.setFill(Color.LIGHTGREEN);
                fieldPane.getChildren().add(availableCircle);
            } else{
                Circle unAvailableCircle = new Circle();
                unAvailableCircle.setCenterX(vertexCoordinates[vertex][0] + pieceDimension / 2);
                unAvailableCircle.setCenterY(vertexCoordinates[vertex][1] + pieceDimension / 2);
                unAvailableCircle.setRadius(7);
                unAvailableCircle.setFill(Color.RED);
                fieldPane.getChildren().add(unAvailableCircle);

            }
        }
    }

    private void placeStartingRing(int vertex, GraphicsContext gc) {
        Vertex boardVertex = this.gameBoard.getVertex(vertex);

        if (boardVertex != null && !boardVertex.hasRing()) {
            String ringColor = isWhiteTurn ? "White" : "Black";
            Image ringImage = isWhiteTurn ? ringWImage : ringBImage;
            Ring newRing = new Ring(ringColor);

            boardVertex.setPlayObject(newRing);
            gc.drawImage(ringImage, vertexCoordinates[vertex][0], vertexCoordinates[vertex][1], pieceDimension, pieceDimension);

            ringsPlaced++;
            ringVertexNumbers.add(vertex);
            //update rings count
            if ("White".equals(ringColor)) {
                updateWhiteRing();
            } else {
                updateBlackRing();
            }
            if(ringsPlaced>=10){
                chipPlacement = true;
            }
            isWhiteTurn = !isWhiteTurn;  // Switch to the other player’s turn

        } else {
            showAlert("Warning", "Cannot place a ring on top of another ring!");
            System.out.println("Attempted to place a ring on an occupied vertex.");
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private int findClosestVertex(double x, double y){

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

    private void drawBoard(GraphicsContext gc) {
        //gc.drawImage(boardImage, 0, 0, fieldDimension, fieldDimension);

        
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);


        // up slant lines
        gc.strokeLine(vertexCoordinates[4][0]+pieceDimension/2, vertexCoordinates[4][1]+pieceDimension/2, 
        vertexCoordinates[28][0]+pieceDimension/2, vertexCoordinates[28][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[0][0]+pieceDimension/2, vertexCoordinates[0][1]+pieceDimension/2, 
        vertexCoordinates[47][0]+pieceDimension/2, vertexCoordinates[47][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[1][0]+pieceDimension/2, vertexCoordinates[1][1]+pieceDimension/2, 
        vertexCoordinates[57][0]+pieceDimension/2, vertexCoordinates[57][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[2][0]+pieceDimension/2, vertexCoordinates[2][1]+pieceDimension/2, 
        vertexCoordinates[66][0]+pieceDimension/2, vertexCoordinates[66][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[3][0]+pieceDimension/2, vertexCoordinates[3][1]+pieceDimension/2, 
        vertexCoordinates[74][0]+pieceDimension/2, vertexCoordinates[74][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[9][0]+pieceDimension/2, vertexCoordinates[9][1]+pieceDimension/2, 
        vertexCoordinates[75][0]+pieceDimension/2, vertexCoordinates[75][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[10][0]+pieceDimension/2, vertexCoordinates[10][1]+pieceDimension/2, 
        vertexCoordinates[81][0]+pieceDimension/2, vertexCoordinates[81][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[18][0]+pieceDimension/2, vertexCoordinates[18][1]+pieceDimension/2, 
        vertexCoordinates[82][0]+pieceDimension/2, vertexCoordinates[82][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[27][0]+pieceDimension/2, vertexCoordinates[27][1]+pieceDimension/2, 
        vertexCoordinates[83][0]+pieceDimension/2, vertexCoordinates[83][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[37][0]+pieceDimension/2, vertexCoordinates[37][1]+pieceDimension/2, 
        vertexCoordinates[84][0]+pieceDimension/2, vertexCoordinates[84][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[56][0]+pieceDimension/2, vertexCoordinates[56][1]+pieceDimension/2, 
        vertexCoordinates[80][0]+pieceDimension/2, vertexCoordinates[80][1]+pieceDimension/2);

        // down slant lines
        gc.strokeLine(vertexCoordinates[47][0]+pieceDimension/2, vertexCoordinates[47][1]+pieceDimension/2, 
        vertexCoordinates[74][0]+pieceDimension/2, vertexCoordinates[74][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[28][0]+pieceDimension/2, vertexCoordinates[28][1]+pieceDimension/2, 
        vertexCoordinates[81][0]+pieceDimension/2, vertexCoordinates[81][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[19][0]+pieceDimension/2, vertexCoordinates[19][1]+pieceDimension/2, 
        vertexCoordinates[82][0]+pieceDimension/2, vertexCoordinates[82][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[11][0]+pieceDimension/2, vertexCoordinates[11][1]+pieceDimension/2, 
        vertexCoordinates[83][0]+pieceDimension/2, vertexCoordinates[83][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[4][0]+pieceDimension/2, vertexCoordinates[4][1]+pieceDimension/2, 
        vertexCoordinates[84][0]+pieceDimension/2, vertexCoordinates[84][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[5][0]+pieceDimension/2, vertexCoordinates[5][1]+pieceDimension/2, 
        vertexCoordinates[79][0]+pieceDimension/2, vertexCoordinates[79][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[0][0]+pieceDimension/2, vertexCoordinates[0][1]+pieceDimension/2, 
        vertexCoordinates[80][0]+pieceDimension/2, vertexCoordinates[80][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[1][0]+pieceDimension/2, vertexCoordinates[1][1]+pieceDimension/2, 
        vertexCoordinates[73][0]+pieceDimension/2, vertexCoordinates[73][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[2][0]+pieceDimension/2, vertexCoordinates[2][1]+pieceDimension/2, 
        vertexCoordinates[65][0]+pieceDimension/2, vertexCoordinates[65][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[3][0]+pieceDimension/2, vertexCoordinates[3][1]+pieceDimension/2, 
        vertexCoordinates[56][0]+pieceDimension/2, vertexCoordinates[56][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[10][0]+pieceDimension/2, vertexCoordinates[10][1]+pieceDimension/2, 
        vertexCoordinates[37][0]+pieceDimension/2, vertexCoordinates[37][1]+pieceDimension/2);

        // // vertical lines
        gc.strokeLine(vertexCoordinates[0][0]+pieceDimension/2, vertexCoordinates[0][1]+pieceDimension/2, 
        vertexCoordinates[3][0]+pieceDimension/2, vertexCoordinates[3][1]+pieceDimension/2);
             
        gc.strokeLine(vertexCoordinates[4][0]+pieceDimension/2, vertexCoordinates[4][1]+pieceDimension/2, 
        vertexCoordinates[10][0]+pieceDimension/2, vertexCoordinates[10][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[11][0]+pieceDimension/2, vertexCoordinates[11][1]+pieceDimension/2, 
        vertexCoordinates[18][0]+pieceDimension/2, vertexCoordinates[18][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[19][0]+pieceDimension/2, vertexCoordinates[19][1]+pieceDimension/2, 
        vertexCoordinates[27][0]+pieceDimension/2, vertexCoordinates[27][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[28][0]+pieceDimension/2, vertexCoordinates[28][1]+pieceDimension/2, 
        vertexCoordinates[37][0]+pieceDimension/2, vertexCoordinates[37][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[38][0]+pieceDimension/2, vertexCoordinates[38][1]+pieceDimension/2, 
        vertexCoordinates[46][0]+pieceDimension/2, vertexCoordinates[46][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[47][0]+pieceDimension/2, vertexCoordinates[47][1]+pieceDimension/2, 
        vertexCoordinates[56][0]+pieceDimension/2, vertexCoordinates[56][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[57][0]+pieceDimension/2, vertexCoordinates[57][1]+pieceDimension/2, 
        vertexCoordinates[65][0]+pieceDimension/2, vertexCoordinates[65][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[66][0]+pieceDimension/2, vertexCoordinates[66][1]+pieceDimension/2, 
        vertexCoordinates[73][0]+pieceDimension/2, vertexCoordinates[73][1]+pieceDimension/2);
        
        gc.strokeLine(vertexCoordinates[74][0]+pieceDimension/2, vertexCoordinates[74][1]+pieceDimension/2, 
        vertexCoordinates[80][0]+pieceDimension/2, vertexCoordinates[80][1]+pieceDimension/2);

        gc.strokeLine(vertexCoordinates[81][0]+pieceDimension/2, vertexCoordinates[81][1]+pieceDimension/2, 
        vertexCoordinates[84][0]+pieceDimension/2, vertexCoordinates[84][1]+pieceDimension/2);

        // // for demo
        // gc.drawImage(ringBImage, vertexCoordinates[39][0], vertexCoordinates[39][1], pieceDimension, pieceDimension);
        // gc.drawImage(ringWImage, vertexCoordinates[61][0], vertexCoordinates[61][1], pieceDimension, pieceDimension);
        // gc.drawImage(chipBImage, vertexCoordinates[43][0], vertexCoordinates[43][1], pieceDimension, pieceDimension);
        // gc.drawImage(chipWImage, vertexCoordinates[41][0], vertexCoordinates[41][1], pieceDimension, pieceDimension);

        // //hit box
        // gc.setStroke(Color.MAGENTA);
        // gc.strokeLine(vertexCoordinates[40][0]+pieceDimension/2 - 10, vertexCoordinates[40][1]+pieceDimension/2 + 10, 
        // vertexCoordinates[40][0]+pieceDimension/2 + 10, vertexCoordinates[40][1]+pieceDimension/2 + 10);
        // gc.strokeLine(vertexCoordinates[40][0]+pieceDimension/2 - 10, vertexCoordinates[40][1]+pieceDimension/2 - 10, 
        // vertexCoordinates[40][0]+pieceDimension/2 + 10, vertexCoordinates[40][1]+pieceDimension/2 - 10);
        // gc.strokeLine(vertexCoordinates[40][0]+pieceDimension/2 + 10, vertexCoordinates[40][1]+pieceDimension/2 - 10, 
        // vertexCoordinates[40][0]+pieceDimension/2 + 10, vertexCoordinates[40][1]+pieceDimension/2 + 10);
        // gc.strokeLine(vertexCoordinates[40][0]+pieceDimension/2 - 10, vertexCoordinates[40][1]+pieceDimension/2 - 10, 
        // vertexCoordinates[40][0]+pieceDimension/2 - 10, vertexCoordinates[40][1]+pieceDimension/2 + 10);

        //vertex numbers 
        for (int i = 0 ; i < 85 ; i++){
            gc.setFill(Color.BLACK);
            gc.fillText(""+i, vertexCoordinates[i][0] + 10 + pieceDimension/2, vertexCoordinates[i][1] + 5 + pieceDimension/2);
        }
    }

    private Circle makeScoreCircle() {
        Circle circle = new Circle(35);
        circle.setStroke(inactiveScoreRingColor);
        circle.setFill(null);
        circle.setStrokeWidth(10);

        return circle;
    }

    private void resetBoard() {
        System.out.println();
        System.out.println(whitePlayerComboBox.getValue());
        System.out.println(blackPlayerComboBox.getValue());
        System.out.println("Reset!");
    }

        public static void main(String[] args) {
        launch(args);
    }
}