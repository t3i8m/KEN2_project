package com.ken2.ui;
import java.util.ArrayList;

import com.ken2.Game_Components.Board.*;
import com.ken2.engine.GameEngine;
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

public class MainApp2 extends Application {

    private Image boardImage = new Image("file:ken2\\assets\\temp-board-image.jpg");
    private Image ringBImage = new Image("file:ken2\\assets\\black ring.png");
    private Image chipBImage = new Image("file:ken2\\assets\\black chip.png");
    private Image ringWImage = new Image("file:ken2\\assets\\white ring.png");
    private Image chipWImage = new Image("file:ken2\\assets\\white chip.png");
    private int fieldDimension = 470;
    private int pieceDimension = 37;

    private Color inactiveScoreRingColor = Color.rgb(200, 200, 200);
    private Color activeScoreRingColor = Color.rgb(90, 150, 220);
    private int[][] vertexCoordinates = new int[85][2];
    private ComboBox whitePlayerComboBox;
    private ComboBox blackPlayerComboBox;


    private GridPane root;
    private Pane fieldPane;
    private ArrayList<Integer> ringVertexNumbers;
    private Text chipsRemainText;
    private Text ringWhiteRemainingText;
    private Text ringBlackRemainingText;

    private GameEngine gameEngine;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Compare Players with Yinsh");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);

        // we create a game board obj here
        this.ringVertexNumbers = new ArrayList<>();

        gameEngine = new GameEngine();


        root = new GridPane();
        root.setPadding(new Insets(5, 5, 5, 5));
        root.setVgap(10);
        root.setHgap(10);

        Scene scene = new Scene(root, 800, 600, Color.GRAY);

        // game field elements
        Canvas fieldPlaceHolder = new Canvas();
        fieldPlaceHolder.setWidth(fieldDimension);
        fieldPlaceHolder.setHeight(fieldDimension);

        GraphicsContext gc = fieldPlaceHolder.getGraphicsContext2D();

        drawBoard(gc);

        fieldPlaceHolder.setOnMouseClicked((MouseEvent e) -> {
            double x = e.getX();
            double y = e.getY();
            int vertex = gameEngine.findClosestVertex(x,y);

            if ((gameEngine.getRingsPlaced() < 10) && (vertex >= 0)){
                placeStartingRing(vertex, gc);
                displayAvailablePlacesForStartingRings(vertex);
            } else if (gameEngine.getRingsPlaced()>=10){
                removeCircleIndicators();

                if(this.ringVertexNumbers.contains(vertex)){
                    System.out.println("CONTAINS");
                }

                int[] vertexPosition = gameEngine.getGameBoard().getVertexPositionByNumber(vertex);
                System.out.println(vertexPosition[0]);
                ArrayList<ArrayList<Move>> allPossibleMoves = gameEngine.getAllPossibleMoves(vertexPosition[0],vertexPosition[1]);
                displayPossibleMoves(allPossibleMoves);
            }

        });
        fieldPlaceHolder.setOnMouseClicked((MouseEvent e)-> handleFieldClick(e, gc));


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
        chipsRemainText.setText("Chips Remaining: " + gameEngine.getChipsRemaining());

        ringBlackRemainingText = new Text();
        ringBlackRemainingText.setText("Black Rings Remaining: " + gameEngine.getRingBlack());

        ringWhiteRemainingText = new Text();
        ringWhiteRemainingText.setText("White Rings Remaining: " + gameEngine.getRingWhite());

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
        root.add(fieldPlaceHolder, 1, 1, 5, 5);
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
        int vertex = gameEngine.findClosestVertex(x, y);

        if (vertex >= 0) {
            if (gameEngine.getRingsPlaced() < 10) {
                // Ring placement phase
                placeStartingRing(vertex, gc);
                displayAvailablePlacesForStartingRings(vertex);
            } else {
                removeCircleIndicators();

                if (gameEngine.getChipPalcement()) {
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
    }
    private void selectRingForMovement(int vertex) {
        Vertex boardVertex = gameEngine.getGameBoard().getVertex(vertex);

        // Check if the selected vertex has a ring and is of the correct turn color
        if (boardVertex != null && boardVertex.hasRing() && boardVertex.getRing().getColour().equals(gameEngine.getisWhiteTurn() ? "White" : "Black")) {
            selectedRingVertex = vertex; // Track the selected ring's location
            displayPossibleMovesForRing(boardVertex); // Display possible moves for selected ring
        } else {
            gameEngine.showAlert("Invalid Selection", "Please select a valid ring to move.");
        }
    }


    public void moveRing(int fromVertex, int toVertex, GraphicsContext gc) {
        Vertex sourceVertex = gameEngine.getGameBoard().getVertex(fromVertex);
        Vertex targetVertex = gameEngine.getGameBoard().getVertex(toVertex);

        // Ensure the target position is unoccupied and does not contain a chip
        if (targetVertex != null && !targetVertex.hasRing() && !gameEngine.getChipNumber().contains(toVertex)) {
            // Clear the original ring position on the canvas
            gc.clearRect(vertexCoordinates[fromVertex][0], vertexCoordinates[fromVertex][1], pieceDimension, pieceDimension);

            // Move the ring to the new target vertex
            Ring ringToMove = (Ring) sourceVertex.getRing();
            targetVertex.setPlayObject(ringToMove); // Place ring in target vertex
            sourceVertex.setPlayObject(null); // Clear ring from original position

            // Draw ring at the new position
            Image ringImage = gameEngine.getisWhiteTurn() ? ringWImage : ringBImage;
            gc.drawImage(ringImage, vertexCoordinates[toVertex][0], vertexCoordinates[toVertex][1], pieceDimension, pieceDimension);

            // Toggle the turn and switch to chip placement phase
            gameEngine.toggleTurn();
            gameEngine.updateChipPlacement(true);
        } else {
            gameEngine.showAlert("Invalid Move", "The selected position is not valid for movement.");
        }
    }

    // Method to place chips on the board
    private void placeChip(int vertex, GraphicsContext gc) {
        Vertex boardVertex = gameEngine.getGameBoard().getVertex(vertex);
        if (boardVertex != null && boardVertex.hasRing() && !boardVertex.hasCoin()) {
            String chipColor = gameEngine.getisWhiteTurn() ? "White" : "Black";
            Image chipImage = gameEngine.getisWhiteTurn() ? chipWImage : chipBImage;

            Coin newChip = new Coin(chipColor);
            boardVertex.setPlayObject(newChip);


            gc.drawImage(chipImage, vertexCoordinates[vertex][0] + pieceDimension / 4,
                    vertexCoordinates[vertex][1] + pieceDimension / 4, pieceDimension / 2, pieceDimension / 2);

            gameEngine.updateChipNumber(vertex);
            gameEngine.updateChipsRemaining(chipsRemainText);
            gameEngine.updateChipPlacement(false);
            displayPossibleMovesForRing(boardVertex);

            //isWhiteTurn = !isWhiteTurn;

        } else {
            gameEngine.showAlert("Warning", "Cannot place a chip on top of another chip or an empty space!");
            System.out.println("Attempted to place a chip on an invalid vertex " + vertex);  // Debug statement
        }
    }

    /**
     * This for the start of the game to show the vertexes to store the rings
     * @param boardVertex
     */
    private void displayPossibleMovesForRing(Vertex boardVertex) {
        int[] ringPosition = {boardVertex.getXposition(), boardVertex.getYposition()};
        Vertex[][] board = gameEngine.getGameBoard().getBoard();

        // Start simulation to find eligible moves
        ArrayList<ArrayList<Move>> allPossibleMoves = gameEngine.getAllPossibleMoves(ringPosition[0],ringPosition[1]);

        // Indicate possible moves
        for (ArrayList<Move> directionMoves : allPossibleMoves) {
            for (Move move : directionMoves) {
                int x = move.getXposition();
                int y = move.getYposition();
                int moveVertexNumber = gameEngine.getGameBoard().getVertexNumberFromPosition(x, y);

                // Only proceed if moveVertexNumber is valid
                if (moveVertexNumber != -1 && !gameEngine.getChipNumber().contains(moveVertexNumber)) {
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


    private void displayPossibleMoves(ArrayList<ArrayList<Move>> allPossibleMoves){
        removeCircleIndicators();
        Vertex[][] board = gameEngine.getGameBoard().getBoard();
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



    private void placeStartingRing(int vertex, GraphicsContext gc) {
        Vertex boardVertex = gameEngine.getGameBoard().getVertex(vertex);

        if (boardVertex != null && !boardVertex.hasRing()) {
            String ringColor = gameEngine.getisWhiteTurn() ? "White" : "Black";
            Image ringImage = gameEngine.getisWhiteTurn() ? ringWImage : ringBImage;
            Ring newRing = new Ring(ringColor);

            boardVertex.setPlayObject(newRing);
            gc.drawImage(ringImage, vertexCoordinates[vertex][0], vertexCoordinates[vertex][1], pieceDimension, pieceDimension);

            gameEngine.increamentRingsPlaced();
            ringVertexNumbers.add(vertex);
            //update rings count
            if ("White".equals(ringColor)) {
                gameEngine.updateWhiteRing(ringWhiteRemainingText);
            } else {
                gameEngine.updateBlackRing(ringBlackRemainingText);
            }
            if(gameEngine.getRingsPlaced()>=10){
                gameEngine.updateChipPlacement(true);
            }
            gameEngine.toggleTurn();  // Switch to the other player’s turn

        } else {
            gameEngine.showAlert("Warning", "Cannot place a ring on top of another ring!");
            System.out.println("Attempted to place a ring on an occupied vertex.");
        }
    }


    /**
     * Displays the available vertexes for placing rings initially
     * @param vertex
     */
    private void displayAvailablePlacesForStartingRings(int vertex){
        ArrayList<Vertex> availablePlaces = gameEngine.AvailablePlacesForStartingRings(vertex);
        for (Vertex v : availablePlaces) {
            Circle availableCircle = makeMoveCircle(vertex,(v != null));
            fieldPane.getChildren().add(availableCircle);
        }
    }

    /**
     * Check availability and makes the circle according to that
     * @param vertex the vertex where we want to place the circle
     * @param availability tells if it is an available move
     * @return
     */
    private Circle makeMoveCircle(int vertex, boolean availability){
        Color color;
        if (availability)color = Color.LIGHTGREEN;
        else color = Color.RED;
        Circle circle = new Circle();
        circle.setCenterX(vertexCoordinates[vertex][0] + pieceDimension / 2);
        circle.setCenterY(vertexCoordinates[vertex][1] + pieceDimension / 2);
        circle.setRadius(7);
        circle.setFill(color);
        return circle;
    }

    private void drawBoard(GraphicsContext gc) {
        //gc.drawImage(boardImage, 0, 0, fieldDimension, fieldDimension);

        vertexCoordinates=gameEngine.getVertexCoordinates();

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
