package com.ken2.ui;

import com.ken2.Game_Components.Board.PlayObj;
import com.ken2.Game_Components.Board.Vertex;
import com.ken2.engine.GameEngine;
import com.ken2.engine.GameState;
import javafx.application.Application;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;


public class MainApp2 extends Application {

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

    // COMBOBOXES
    private ComboBox whitePlayerComboBox;
    private ComboBox blackPlayerComboBox;


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
        root.add(ringWhiteRemainingText, 1,0);
        root.add(ringBlackRemainingText, 5,0);
        root.add(scoreRingeW1, 0, 2);
        root.add(scoreRingeW2, 0, 3);
        root.add(scoreRingeW3, 0, 4);
        root.add(scoreRingeB1, 7, 2);
        root.add(scoreRingeB2, 7, 3);
        root.add(scoreRingeB3, 7, 4);
        root.add(resetButton, 0 , 7);
        primaryStage.setScene(scene);
        primaryStage.show();

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
            handleChipandRingPlacement(vertex, gc);
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
     * Removes the highlighters
     */
    private void removeCircleIndicators(){
        fieldPane.getChildren().removeIf(node -> node instanceof Circle);
    }

    // F I X     T H I S    XD
    private void handleChipandRingPlacement(int vertex, GraphicsContext gc){
        System.out.println("\n\n\n\n\n\nFuck you ");
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
        Color highLighterColor = (availability) ? Color.GREEN : Color.RED;
        Circle availableCircle = new Circle();
        availableCircle.setCenterX(gameEngine.getcoordinates(vertex)[0] + pieceDimension / 2);
        availableCircle.setCenterY(gameEngine.getcoordinates(vertex)[1] + pieceDimension / 2);
        availableCircle.setRadius(7);
        availableCircle.setFill(highLighterColor);
        fieldPane.getChildren().add(availableCircle);
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
    private void resetBoard() {
        System.out.println();
        System.out.println(whitePlayerComboBox.getValue());
        System.out.println(blackPlayerComboBox.getValue());
        System.out.println("Reset!");
    }

    /**
     * Updates onscreen text
     */
    private void updateOnscreenText(){
        chipsRemainText.setText("Chips Remaining: " + gameEngine.currentState.chipsRemaining);
        ringWhiteRemainingText.setText("White Rings Remaining: " + gameEngine.currentState.ringsWhite);
        ringBlackRemainingText.setText("Black Rings Remaining: " + gameEngine.currentState.ringsBlack);
    }

}