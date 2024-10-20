package com.ken2.ui;
import com.ken2.Game_Components.Board.Game_Board;
import com.ken2.Game_Components.Board.PlayObj;
import com.ken2.Game_Components.Board.Ring;
import com.ken2.Game_Components.Board.Vertex;

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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

//add a bunch of mouse events that communcates with the game
//on every mouse event call update board

public class MainApp extends Application {
    
    private Image boardImage = new Image("file:ken2\\assets\\temp-board-image.jpg");
    private Image ringBImage = new Image("file:ken2\\assets\\black ring.png");
    private Image chipBImage = new Image("file:ken2\\assets\\black chip.png");
    private Image ringWImage = new Image("file:ken2\\assets\\white ring.png");
    private Image chipWImage = new Image("file:ken2\\assets\\white chip.png");
    private int fieldDimension = 470;
    private int pieceDimension = 37;
    private int chipsRemaining = 0;
    private Color inactiveScoreRingColor = Color.rgb(200, 200, 200);
    private Color activeScoreRingColor = Color.rgb(90, 150, 220);
    private int[][] vertexCoordinates = new int[85][2];
    private ComboBox whitePlayerComboBox;
    private ComboBox blackPlayerComboBox;
    private int ringsPlaced;
    private boolean isWhiteTurn;
    private Game_Board gameBoard;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Compare Players with Yinsh"); 
        primaryStage.setWidth(800);              
        primaryStage.setHeight(600); 
        primaryStage.setResizable(false);    
        
        // we create a game board obj here 
        this.gameBoard = new Game_Board();
        
        GridPane root = new GridPane();
        root.setPadding(new Insets(5, 5, 5, 5)); 
        root.setVgap(10); 
        root.setHgap(10); 

        Scene scene = new Scene(root, 800, 600, Color.GRAY);

        // game field elements
        isWhiteTurn = true;
        ringsPlaced = 0;
        Canvas fieldPlaceHolder = new Canvas();
        fieldPlaceHolder.setWidth(fieldDimension);
        fieldPlaceHolder.setHeight(fieldDimension);

        GraphicsContext gc = fieldPlaceHolder.getGraphicsContext2D();

        initialiseVertex();
        drawBoard(gc);

        fieldPlaceHolder.setOnMouseClicked((MouseEvent e) -> {
            double x = e.getX();
            double y = e.getY();
            int vertex = findClosestVertex(x,y);
            
            if ((ringsPlaced < 10) && (vertex >= 0)){
                placeStartingRing(vertex, gc);
            }
        });


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

        Text chipsRemainText = new Text();
        chipsRemainText.setText("Chips Remaining: " + chipsRemaining);

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> resetBoard());
        

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
        root.add(chipsRemainText, 1, 0);
        root.add(scoreRingeW1, 0, 2);
        root.add(scoreRingeW2, 0, 3);
        root.add(scoreRingeW3, 0, 4);
        root.add(scoreRingeB1, 7, 2);
        root.add(scoreRingeB2, 7, 3);
        root.add(scoreRingeB3, 7, 4);
        root.add(resetButton, 0 , 7);

        
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    private void placeStartingRing(int vertex, GraphicsContext gc){
        
        System.out.println("placeStartingRing called");

        Image currentImage;

        PlayObj currentRing = new Ring("");
        if(isWhiteTurn){
            currentImage = ringWImage;
            System.out.println("current image is white ring");
            currentRing.setColour("White");
        } else {
            currentImage = ringBImage;
            currentRing.setColour("Black");

            System.out.println("current image is white ring");
        }
        System.out.println(this.gameBoard.strMaker());

        System.out.println("Current ring colour is: "+currentRing.getColour());
        System.out.println(vertex);
        this.gameBoard.updateBoard(vertex, currentRing);
        System.out.println("The board was updated");
        gc.drawImage(currentImage, vertexCoordinates[vertex][0], vertexCoordinates[vertex][1], pieceDimension, pieceDimension);
        isWhiteTurn = !isWhiteTurn;
        ringsPlaced++;
        System.out.println(this.gameBoard.strMaker());
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

        
        gc.setStroke(Color.NAVY);
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
            gc.setFill(Color.GREEN);
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
