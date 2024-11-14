package com.ken2.ui;
import java.util.ArrayList;
import java.util.HashSet;

import com.ken2.Game_Components.Board.*;
import com.ken2.engine.Diagonal;
import com.ken2.engine.Direction;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//add a bunch of mouse events that communcates with the game
//on every mouse event call update board

public class MainAppOLD extends Application {

    private Image ringBImage = new Image("file:ken2\\assets\\black ring.png");
    private static Image chipBImage = new Image("file:ken2\\assets\\black chip.png");
    private Image ringWImage = new Image("file:ken2\\assets\\white ring.png");
    private static Image chipWImage = new Image("file:ken2\\assets\\white chip.png");
    private static int fieldDimension = 470;
    private static int pieceDimension = 37;
    private int chipsRemaining = 51;
    private int ringBlack = 5;
    private int ringWhite = 5;

    private Color inactiveScoreRingColor = Color.rgb(200, 200, 200);
    private Color activeScoreRingColor = Color.rgb(90, 150, 220);

    private static int[][] vertexCoordinates = new int[85][2];
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
    private Canvas playObjectCanvas;

    private boolean chipPlacement = false;
    private ArrayList<Integer> chipNumber = new ArrayList<>();
    private int selectedRingVertex = -1;
    private int selectedChipVertex = -1;
    private boolean chipPlaced = false;
    private boolean isFirstClick = true;
    private int chipRingVertex = -1;
    private ArrayList<Integer> highlightedvertices = new ArrayList<>();
    private HashSet<Integer> s = new HashSet<>();
    private Text turnIndicator = new Text("White's Turn");


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

        this.playObjectCanvas = new Canvas();

        playObjectCanvas.setWidth(fieldDimension);
        playObjectCanvas.setHeight(fieldDimension);


        GraphicsContext gcB = gameBoardCanvas.getGraphicsContext2D();
        GraphicsContext gcP = playObjectCanvas.getGraphicsContext2D();

        initialiseVertex();
        drawBoard(gcB);
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

        Button undoButton = new Button("Undo");
        //undoButton.setOnAction(e -> undoToLastState());

        // Create a Text object to display whose turn it is
        turnIndicator.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        /*turnIndicator.setTranslateX(0);
        turnIndicator.setTranslateY(0);*/

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
        root.add(undoButton, 1, 7);
        root.add(turnIndicator, 3, 7);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
        displayAvailablePlacesForStartingRings(0);
    }

    private void handleFieldClick(MouseEvent e, GraphicsContext gc) {
        int vertex = findClosestVertex(e.getX(), e.getY());
        if (vertex < 0) return;

        if (ringsPlaced < 10) {
            placeStartingRing(vertex, gc);
            displayAvailablePlacesForStartingRings(vertex);
            if(ringsPlaced==10){
                removeCircleIndicators();
            }
        } else {
            removeCircleIndicators();
            handleChipAndRingPlacement(vertex, gc);
        }
    }

    private void handleChipAndRingPlacement(int vertex, GraphicsContext gc) {
        Vertex boardVertex = this.gameBoard.getVertex(vertex);
        if (boardVertex.hasRing() && boardVertex.getRing().getColour().equals(currentPlayerColor())) {
            selectedChipVertex = vertex;//store chip coordinate
            removeCircleIndicators();
            if (vertex == selectedChipVertex) {
                highlightPossibleMoves(boardVertex);
            }

            else{showAlert("Invalid move", "Please select a highlighted vertex");}

        } else if (vertex != selectedChipVertex) {
            int[] Move_Valid = new int[1];
            Move currentMove = this.gameSimulation.simulateMove(this.gameBoard,
                    this.gameBoard.getVertex(selectedChipVertex),
                    this.gameBoard.getVertex(vertex));
            moveRing(selectedChipVertex, vertex, gc, Move_Valid, currentMove);      // ring ring ring MOVE RING
            if(Move_Valid[0] == 1) resetTurn();
        } else {
            showAlert("Invalid Move", "Please select a valid ring or cell to move.");
        }
    }



    private void moveChipToRing(int fromVertex, int toVertex, GraphicsContext gc) {
        Vertex sourceVertex = this.gameBoard.getVertex(fromVertex);
        Vertex targetVertex = this.gameBoard.getVertex(toVertex);

        if (sourceVertex != null && targetVertex != null && sourceVertex.hasCoin()) {
            Coin currentChip = (Coin) sourceVertex.getCoin();
            if (targetVertex.hasRing() && !targetVertex.hasCoin() && targetVertex.getRing().getColour().equalsIgnoreCase(currentChip.getColour())) {
                sourceVertex.setPlayObject(null); //delete the chip from its place
                gc.clearRect(vertexCoordinates[fromVertex][0] + pieceDimension / 4,
                        vertexCoordinates[fromVertex][1] + pieceDimension / 4, pieceDimension / 2, pieceDimension / 2);

                targetVertex.setPlayObject(currentChip); //chips new position
                selectedChipVertex = toVertex;

                Image chipImage = currentChip.getColour().equals("White") ? chipWImage : chipBImage;
                gc.drawImage(chipImage, vertexCoordinates[toVertex][0] + pieceDimension / 4,
                        vertexCoordinates[toVertex][1] + pieceDimension / 4, pieceDimension / 2, pieceDimension / 2);
            } else {
                showAlert("Invalid Move", "You can only move the chip to an empty ring of the same color.");
            }
        }
    }

    private void moveRing(int fromVertex, int toVertex, GraphicsContext gc, int[] Move_Valid, Move currentMove) {
        Vertex sourceVertex = this.gameBoard.getVertex(fromVertex);
        Vertex targetVertex = this.gameBoard.getVertex(toVertex);
        boolean isMoveValid = true;

        if (!highlightedvertices.contains(toVertex)){
            showAlert("INVALID", "JJDJDJD");
            Move_Valid[0] = 0;
            return;
        }

        else if (targetVertex.hasRing() || chipNumber.contains(toVertex)) {
            showAlert("Invalid Move", "Cannot move ring here as it already has an object.");
            isMoveValid = false;
        }

        // After we check is the vertices are clear for moveRing
        if(isMoveValid) {
            placeChip(fromVertex, gc);

            Move_Valid[0] = 1;
            Ring ringToMove = (Ring) sourceVertex.getRing();
            if (ringToMove != null) {
                sourceVertex.setRing(null);

                gc.clearRect(vertexCoordinates[fromVertex][0], vertexCoordinates[fromVertex][1], pieceDimension, pieceDimension);

                if (sourceVertex.hasCoin()) {
                    Coin existingChip = (Coin) sourceVertex.getCoin();
                    Image chipImage = existingChip.getColour().equals("white") ? chipWImage : chipBImage;
                    gc.drawImage(chipImage, vertexCoordinates[fromVertex][0] + pieceDimension / 4,
                            vertexCoordinates[fromVertex][1] + pieceDimension / 4, pieceDimension / 2, pieceDimension / 2);
                }

                // flip coins if array is not empty
                if(!currentMove.getFlippedCoins().isEmpty()){
                    this.gameSimulation.flipCoins(currentMove.getFlippedCoins(), gameBoard);

                    for(Coin coinToFlip: currentMove.getFlippedCoins()){
                        Vertex currVertex = this.gameBoard.getVertexByCoin(coinToFlip);
                        Image chipImage = coinToFlip.getColour().equals("white") ? chipWImage : chipBImage;
                        gc.drawImage(chipImage, vertexCoordinates[currVertex.getVertextNumber()][0] + pieceDimension / 4,
                                vertexCoordinates[currVertex.getVertextNumber()][1] + pieceDimension / 4, pieceDimension / 2, pieceDimension / 2);

                    }
                }

                targetVertex.setRing(ringToMove);
                Image ringImage = ringToMove.getColour().equals("White") ? ringWImage : ringBImage;
                gc.drawImage(ringImage, vertexCoordinates[toVertex][0], vertexCoordinates[toVertex][1], pieceDimension, pieceDimension);

                chipRingVertex = -1;
                chipPlaced = false;
                selectedRingVertex = -1;
                System.out.println(this.gameBoard.strMaker());

            } else {
                showAlert("Invalid Move", "Cannot move ring here.");

            }
        }
    }


    // Method to place chips on the board
    private void placeChip(int vertex, GraphicsContext gc) {
        Vertex boardVertex = this.gameBoard.getVertex(vertex);


        if (boardVertex != null && boardVertex.hasRing() && !boardVertex.hasCoin()) {
            String chipColor = currentPlayerColor();
            Image chipImage = chipColor.equals("White") ? chipWImage : chipBImage;

            //condition for same ring colors
            if (!boardVertex.getRing().getColour().equalsIgnoreCase(chipColor)) {
                showAlert("Warning", "Cannot place a " + chipColor + " chip in a ring of a different color!");
                return;
            }
            Coin newChip = new Coin(chipColor.toLowerCase());
            boardVertex.setPlayObject(newChip);
            chipRingVertex = vertex;

            gc.drawImage(chipImage, vertexCoordinates[vertex][0] + pieceDimension / 4,vertexCoordinates[vertex][1] + pieceDimension / 4, pieceDimension / 2, pieceDimension / 2);
            System.out.println("After setting chip, hasCoin at (" + boardVertex.getXposition() + ", " + boardVertex.getYposition() + "): " + boardVertex.hasCoin());
            chipNumber.add(vertex);
            updateChipsRemaining();
            chipPlaced = true;//after chip placement we can move the ring
        } else {
            showAlert("Warning", "Cannot place a chip here");
        }
    }

    private void highlightPossibleMoves(Vertex boardVertex) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        highlightedvertices.clear();
        for (Direction direction : Direction.values()) {
            Diagonal diagonal = new Diagonal(direction, new int[]{boardVertex.getXposition(), boardVertex.getYposition()}, gameBoard);
            possibleMoves.addAll(diagonal.moveAlongDiagonal(gameBoard.getBoard()));
        }
        removeCircleIndicators();

        for (Move move : possibleMoves) {
            int vertexX = move.getXposition();
            int vertexY = move.getYposition();

            int vertexNumber = this.gameBoard.getVertexNumberFromPosition(vertexX, vertexY);
            if (vertexNumber== -1)
                continue;
            highlightedvertices.add(vertexNumber);

            int pixelX = vertexCoordinates[vertexNumber][0];
            int pixelY = vertexCoordinates[vertexNumber][1];
            Circle highlightIndicator = new Circle();
            highlightIndicator.setCenterX(pixelX + pieceDimension / 2);
            highlightIndicator.setCenterY(pixelY + pieceDimension / 2);
            highlightIndicator.setRadius(7);
            highlightIndicator.setFill(Color.GREEN);
            fieldPane.getChildren().add(highlightIndicator);
        }
    }
    private void removeCircleIndicators(){
        fieldPane.getChildren().removeIf(node -> node instanceof Circle);
    }



    private void checkWinningPosition(int vertex) {
        ///five in a row
    }
    private void updateChipsRemaining() {
        if (chipsRemaining > 0) {
            chipsRemaining--;
            chipsRemainText.setText("Chips Remaining: " + chipsRemaining);
        }
    }
    private void updateRingCount(String color) {
        if (color.equals("White")) {
            ringWhite--;
            ringWhiteRemainingText.setText("White Rings Remaining: " + ringWhite);
        } else {
            ringBlack--;
            ringBlackRemainingText.setText("Black Rings Remaining: " + ringBlack);
        }
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

    private void resetTurn() {
        isWhiteTurn = !isWhiteTurn;
        chipPlacement = true;
        chipPlaced = false;
        isFirstClick = true;

        // Update the turn indicator
        turnIndicator.setText(isWhiteTurn ? "White's Turn" : "Black's Turn");
    }

    private String currentPlayerColor() {
        return isWhiteTurn ? "White" : "Black";
    }

    private void placeStartingRing(int vertex, GraphicsContext gc) {
        Vertex boardVertex = this.gameBoard.getVertex(vertex);
        if (boardVertex != null && !boardVertex.hasRing()) {
            String ringColor = currentPlayerColor();
            Ring newRing = new Ring(ringColor);
            boardVertex.setPlayObject(newRing);
            ringsPlaced++;

            Image ringImage = ringColor.equals("White") ? ringWImage : ringBImage;
            gc.drawImage(ringImage, vertexCoordinates[vertex][0], vertexCoordinates[vertex][1], pieceDimension, pieceDimension);

            updateRingCount(ringColor);
            if (ringsPlaced >= 10) chipPlacement= true;
            resetTurn();
        } else {
            showAlert("Invalid Placement", "Cannot place ring here.");
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
                System.out.println("Vertex Clicked: " + i);
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