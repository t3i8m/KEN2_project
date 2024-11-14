package com.ken2.ui;

import com.ken2.Game_Components.Board.Coin;
import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.PlayObj;
import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.bots.BotAbstract;
import com.ken2.bots.RuleBased.RuleBasedBot;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameState;
import com.ken2.engine.Move;
import javafx.application.Application;
import javafx.css.Rule;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

import java.util.ArrayList;


public class MainApp extends Application {

    private GameEngine gameEngine;

    // ASSETS
    private Image ringBImage = new Image("file:ken2\\assets\\black ring.png");
    private static Image chipBImage = new Image("file:ken2\\assets\\black chip.png");
    private Image ringWImage = new Image("file:ken2\\assets\\white ring.png");
    private static Image chipWImage = new Image("file:ken2\\assets\\white chip.png");

    // COMPONENTS
    private static int fieldDimension = 470;
    private static int pieceDimension = 37;

    private Color inactiveScoreRingColor = Color.rgb(200, 200, 200);
    private Color activeScoreRingColor = Color.rgb(90, 150, 220);

    private GridPane root;
    private Canvas playObjectCanvas;
    private Pane fieldPane;

    // TEXTS
    private Text ringBlackRemainingText;
    private Text ringWhiteRemainingText;
    private Text chipsRemainText;
    private Text turnIndicator = new Text("White's Turn");

    // COMBOBOXES
    private ComboBox whitePlayerComboBox;
    private ComboBox blackPlayerComboBox;

    private ArrayList<Integer> highlightedVertices;

    // BOT COMPONENTS
    ArrayList<RuleBasedBot> bots = new ArrayList<>();
    Button botTurnButton;


    @Override
    public void start(Stage primaryStage) throws Exception {

        gameEngine = new GameEngine();

        primaryStage.setTitle("Compare Players with Yinsh");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setResizable(false);

        root = new GridPane();
        root.setPadding(new Insets(5, 5, 5, 5));
        root.setVgap(10);
        root.setHgap(10);

        Scene scene = new Scene(root, 800, 600, Color.GRAY);
        Canvas gameBoardCanvas = new Canvas();

        gameBoardCanvas.setWidth(fieldDimension);
        gameBoardCanvas.setHeight(fieldDimension);

        this.playObjectCanvas = new Canvas();

        playObjectCanvas.setWidth(fieldDimension);
        playObjectCanvas.setHeight(fieldDimension);


        GraphicsContext gcB = gameBoardCanvas.getGraphicsContext2D();
        GraphicsContext gcP = playObjectCanvas.getGraphicsContext2D();

        drawBoard(gcB);
        playObjectCanvas.setOnMouseClicked((MouseEvent e)-> handleFieldClick(e, gcP)); // GAAAME ENGIIIINE

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
        ringBlackRemainingText = new Text();
        ringWhiteRemainingText = new Text();
        updateOnscreenText();

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> resetBoard(gcP));

        Button undoButton = new Button("Undo");
        //undoButton.setOnAction(e -> undoToLastState());

        botTurnButton = new Button("Next Bot Move");
        botTurnButton.setOnAction(e -> nextBotTurn(gcP));
        botTurnButton.setDisable(bots.size()==0);

        turnIndicator.setFont(Font.font("Arial", FontWeight.BOLD, 24));

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
        root.add(ringWhiteRemainingText, 1,0);
        root.add(ringBlackRemainingText, 5,0);
        root.add(scoreRingeW1, 0, 2);
        root.add(scoreRingeW2, 0, 3);
        root.add(scoreRingeW3, 0, 4);
        root.add(scoreRingeB1, 7, 2);
        root.add(scoreRingeB2, 7, 3);
        root.add(scoreRingeB3, 7, 4);
        root.add(resetButton, 0 , 7);
        root.add(undoButton, 1, 7);
        root.add(turnIndicator, 3, 7);
        root.add(botTurnButton, 7, 7);
        primaryStage.setScene(scene);
        primaryStage.show();

        highlightedVertices=new ArrayList<>();
        GUIavailablePlacesForStartRings(gcP);
    }

    /**
     * Handle mouse clicks
     * @param e MouseEvent
     * @param gc GraphicsContext
     */
    private void handleFieldClick(MouseEvent e, GraphicsContext gc) {
        int vertex = gameEngine.findClosestVertex(e.getX(), e.getY());
        if (vertex < 0) return;

        if (gameEngine.currentState.ringsPlaced < 10) {
            GUIplaceStartingRing(vertex,gc);
            GUIavailablePlacesForStartRings(gc);
            if(gameEngine.currentState.ringsPlaced==10){
                removeCircleIndicators();
            }
        } else {
            removeCircleIndicators();
            handleChipAndRingPlacement(vertex, gc);
        }
        updateOnscreenText();
    }

    /**
     * To place rings at the start of the game
     * @param vertex vertex number
     */
    private void GUIplaceStartingRing(int vertex,GraphicsContext gc){
        String ringColor = (gameEngine.currentState.isWhiteTurn) ? "white" : "black";
        if(gameEngine.placeStartingRing(vertex,ringColor)){
            Image ringImage = ringColor.equals("white") ? ringWImage : ringBImage;
            drawImage(ringImage,vertex,gc);
            drawHighlighter(vertex,false);
        }
    }

    /**
     * Displays available vertices for the starting rings to be placed
     * @param gc GraphicsContext
     */
    private void GUIavailablePlacesForStartRings(GraphicsContext gc){
        ArrayList<Vertex> aVertices =gameEngine.availablePlacesForStartingRings();
        for(Vertex v : aVertices){
            boolean isVnull = (v!=null);
            if(isVnull) {
                drawHighlighter(v.getVertextNumber(),true);
            }
        }
    }

    /**
     * Handles game logic between chips and rings placement
     * @param vertex vertex clicked
     * @param gc GraphicsContext
     */
    private void handleChipAndRingPlacement(int vertex, GraphicsContext gc) {

        Vertex boardVertex = gameEngine.currentState.gameBoard.getVertex(vertex);

        ArrayList<Move> possibleMoves = new ArrayList<>();

        if (boardVertex.hasRing() && boardVertex.getRing().getColour().equals(gameEngine.currentState.currentPlayerColor())) {
            gameEngine.currentState.selectedChipVertex = vertex;//store chip coordinate
            removeCircleIndicators();
            if (vertex == gameEngine.currentState.selectedChipVertex) {
                possibleMoves=gameEngine.possibleMoves(boardVertex);
                highlightPossibleMoves(possibleMoves);
            }
            else{gameEngine.showAlert("Invalid move", "Please select a highlighted vertex");}

        }
        else if (vertex != gameEngine.currentState.selectedChipVertex) {
            int[] Move_Valid = new int[1];
            Move currentMove = gameEngine.gameSimulation.simulateMove(gameEngine.currentState.gameBoard,
                    gameEngine.currentState.gameBoard.getVertex(gameEngine.currentState.selectedChipVertex),
                    gameEngine.currentState.gameBoard.getVertex(vertex));
            moveRing(gameEngine.currentState.selectedChipVertex, vertex, gc, Move_Valid, currentMove,possibleMoves);
            if(Move_Valid[0] == 1) resetTurn();
        } else {
            gameEngine.showAlert("Invalid Move", "Please select a valid ring or cell to move.");
        }
    }

    /**
     * Highlight the possible moves of the ring
     * @param possibleMoves posiible moves
     */
    private void highlightPossibleMoves(ArrayList<Move> possibleMoves) {
        highlightedVertices.clear();
        for (Move move : possibleMoves) {
            int vertexNumber = gameEngine.currentState.gameBoard.getVertexNumberFromPosition(move.getXposition(), move.getYposition());
            if (vertexNumber == -1) {
                continue;
            }
            drawHighlighter(vertexNumber,true);
        }
    }

    /**
     * Logic for the Move ring method
     * @param fromVertex
     * @param toVertex
     * @param gc
     * @param Move_Valid
     * @param currentMove
     * @param possibleMoves
     */
    private void moveRing(int fromVertex, int toVertex, GraphicsContext gc, int[] Move_Valid, Move currentMove, ArrayList<Move> possibleMoves) {
        Vertex sourceVertex = gameEngine.currentState.gameBoard.getVertex(fromVertex);
        Vertex targetVertex = gameEngine.currentState.gameBoard.getVertex(toVertex);
        boolean isMoveValid = true;


        if (!highlightedVertices.contains(toVertex)){
            gameEngine.showAlert("INVALID", "JJDJDJD");
            Move_Valid[0] = 0;
            return;
        }

        else if (targetVertex.hasRing() || gameEngine.currentState.chipNumber.contains(toVertex)) {
            gameEngine.showAlert("Invalid Move", "Cannot move ring here as it already has an object.");
            isMoveValid = false;
        }

        // After we check is the vertices are clear for moveRing
        if(isMoveValid) {
            gameEngine.placeChip(fromVertex, gc);

            Move_Valid[0] = 1;
            Ring ringToMove = (Ring) sourceVertex.getRing();
            if (ringToMove != null) {
                sourceVertex.setRing(null);

                gc.clearRect(gameEngine.vertexCoordinates[fromVertex][0], gameEngine.vertexCoordinates[fromVertex][1], pieceDimension, pieceDimension);

                if (sourceVertex.hasCoin()) {
                    Coin existingChip = (Coin) sourceVertex.getCoin();
                    Image chipImage = existingChip.getColour().equals("white") ? chipWImage : chipBImage;
                    gc.drawImage(chipImage, gameEngine.vertexCoordinates[fromVertex][0] + pieceDimension / 4,
                            gameEngine.vertexCoordinates[fromVertex][1] + pieceDimension / 4, pieceDimension / 2, pieceDimension / 2);
                }

                // flip coins if array is not empty
                if(!currentMove.getFlippedCoins().isEmpty()){
                    gameEngine.gameSimulation.flipCoins(currentMove.getFlippedCoins(), gameEngine.currentState.gameBoard);

                    for(Coin coinToFlip: currentMove.getFlippedCoins()){
                        Vertex currVertex = gameEngine.currentState.gameBoard.getVertexByCoin(coinToFlip);
                        Image chipImage = coinToFlip.getColour().equals("white") ? chipWImage : chipBImage;
                        gc.drawImage(chipImage, gameEngine.vertexCoordinates[currVertex.getVertextNumber()][0] + pieceDimension / 4,
                                gameEngine.vertexCoordinates[currVertex.getVertextNumber()][1] + pieceDimension / 4, pieceDimension / 2,
                                pieceDimension / 2);

                    }
                }

                targetVertex.setRing(ringToMove);
                Image ringImage = ringToMove.getColour().equals("White") ? ringWImage : ringBImage;
                gc.drawImage(ringImage, gameEngine.vertexCoordinates[toVertex][0], gameEngine.vertexCoordinates[toVertex][1], pieceDimension, pieceDimension);

                gameEngine.currentState.chipRingVertex = -1;
                gameEngine.currentState.chipPlaced = false;
                gameEngine.currentState.selectedRingVertex = -1;
                System.out.println(gameEngine.currentState.gameBoard.strMaker());

            } else {
                gameEngine.showAlert("Invalid Move", "Cannot move ring here.");

            }
        }
    }

    /**
     * Draw the game board for the GUI
     * @param gc GraphicsContext object for drawing the board
     */
    private void drawBoard(GraphicsContext gc) {
        //gc.drawImage(boardImage, 0, 0, fieldDimension, fieldDimension);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        int[][] vertexCoordinates = gameEngine.getVertexCoordinates();

        // up slant lines
        gc.strokeLine(vertexCoordinates[4][0]+ pieceDimension /2, vertexCoordinates[4][1]+pieceDimension/2,
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

    /**
     * Method to draw rings and coins
     * @param img image to draw
     * @param vertex vertex to draw image at
     * @param gc GraphicsContext
     */
    private void drawImage(Image img, int vertex, GraphicsContext gc){
        gc.drawImage(img, gameEngine.vertexCoordinates[vertex][0], gameEngine.vertexCoordinates[vertex][1], pieceDimension, pieceDimension);
    }

    /**
     * Draws the highlighter for the available moves
     * @param vertex vertex number
     * @param availability true for green and false for red
     */
    public void drawHighlighter( int vertex, boolean availability){
        if (availability){highlightedVertices.add(vertex);}
        Color highLighterColor = (availability) ? Color.GREEN : Color.RED;
        Circle availableCircle = new Circle();
        availableCircle.setCenterX(gameEngine.getcoordinates(vertex)[0] + pieceDimension / 2);
        availableCircle.setCenterY(gameEngine.getcoordinates(vertex)[1] + pieceDimension / 2);
        availableCircle.setRadius(7);
        availableCircle.setFill(highLighterColor);
        fieldPane.getChildren().add(availableCircle);
    }


    /**
     * Removes the highlighters
     */
    private void removeCircleIndicators(){
        fieldPane.getChildren().removeIf(node -> node instanceof Circle);
    }


    /**
     * Draw the inactive score circles
     * @return a circle object
     */
    private Circle makeScoreCircle() {
        Circle circle = new Circle(35);
        circle.setStroke(inactiveScoreRingColor);
        circle.setFill(null);
        circle.setStrokeWidth(10);

        return circle;
    }

    /**
     * Reset the Board for a new game
     */
    private void resetBoard(GraphicsContext gc) {
        System.out.println();
        System.out.println(whitePlayerComboBox.getValue());
        System.out.println(blackPlayerComboBox.getValue());

        // clear all chips
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // check player selection
        selectPlayer("White", whitePlayerComboBox);
        selectPlayer("Black", blackPlayerComboBox);
        
        botTurnButton.setDisable(bots.size()==0);
        
        // get a new game
        gameEngine.resetGame();

        // update highlights
        GUIavailablePlacesForStartRings(gc);
        

    }

    /**
     * created bot for the player if bot is selected
     * @param color
     * @param comboBox
     */
    private void selectPlayer(String color, ComboBox comboBox){

        switch (comboBox.getSelectionModel().getSelectedIndex()) {
            case 1: //ai player
                RuleBasedBot ruleBasedBot = new RuleBasedBot(color);
                bots.add(ruleBasedBot);
                break;
        
            default: // human
            
                break;
        }
    }

    private void nextBotTurn(GraphicsContext gc){
        Game_Board gameBoard = gameEngine.currentState.gameBoard;
        boolean ringsPlaced = gameEngine.currentState.ringsPlaced >= 10;
        System.out.println(ringsPlaced);

        // find out whos turn it is
        Boolean isWhiteTurn = gameEngine.currentState.isWhiteTurn;

        // check the bots if its their turn
        for (RuleBasedBot ruleBasedBot : bots) {
            Boolean isWhite = "White".equals(ruleBasedBot.getColor());
            if (isWhite == isWhiteTurn){
                Move move = ruleBasedBot.makeMove(gameBoard, !ringsPlaced);
                updateGameBoard(gameBoard, gc);
            }
        }

        updateOnscreenText();

        System.out.println("Next Bot Turn");
    }

    private void updateGameBoard(Game_Board game_Board, GraphicsContext gc){

        //clear canvas
        
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        //run through every vertex and put image there if there is an image
        for (int i = 0; i <85;i++){
            Vertex vertex = game_Board.getVertex(i);

            //vertex has ring
            if (vertex.hasRing()){
                PlayObj ring = vertex.getRing();

                String colour = ring.getColour();

                if (colour.equals("White")){
                    drawImage(ringWImage, i, gc);
                } else {
                    drawImage(ringBImage, i, gc);
                }

            }

            //vertex has coin
            if (vertex.hasCoin()){
                PlayObj coin = vertex.getCoin();

                String colour = coin.getColour();

                if (colour.equals("White")){
                    drawImage(chipWImage, i, gc);
                } else {
                    drawImage(chipBImage, i, gc);
                }

            }
            
        }
        


    }

    /**
     * reset the turn for the next player
     */
    private void resetTurn(){
        gameEngine.currentState.resetTurn();
        turnIndicator.setText(gameEngine.currentState.isWhiteTurn ? "White's Turn" : "Black's Turn");
    }
    /**
     * Updates onscreen text
     */
    private void updateOnscreenText(){
        chipsRemainText.setText("Chips Remaining: " + gameEngine.currentState.chipsRemaining);
        ringWhiteRemainingText.setText("White Rings Remaining: " + gameEngine.currentState.ringsWhite);
        ringBlackRemainingText.setText("Black Rings Remaining: " + gameEngine.currentState.ringsBlack);
    }

    
    public static void main(String[] args) {
        launch(args);
    }

}