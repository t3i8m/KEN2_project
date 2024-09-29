package com.ken2.ui;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainApp extends Application {
    int fieldDimension = 470;
    int chipsRemaining = 0;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Compare Players with Yinsh"); 
        primaryStage.setWidth(800);              
        primaryStage.setHeight(600); 
        primaryStage.setResizable(false);            
        
        GridPane root = new GridPane();
        root.setPadding(new Insets(5, 5, 5, 5)); 
        root.setVgap(10); 
        root.setHgap(10); 

        Scene scene = new Scene(root, 800, 600, Color.GRAY);

        // game field elements
        Rectangle fieldPlaceHolder = new Rectangle();
        fieldPlaceHolder.setWidth(fieldDimension);
        fieldPlaceHolder.setHeight(fieldDimension);

        // info elements
        ComboBox whitePlayerComboBox = new ComboBox<>();
        whitePlayerComboBox.getItems().add("W Human Player");
        whitePlayerComboBox.getItems().add("W AI Player");

        ComboBox blackPlayerComboBox = new ComboBox<>();
        blackPlayerComboBox.getItems().add("B Human Player");
        blackPlayerComboBox.getItems().add("B AI Player");

        Text whitePlayerLabel = new Text();
        whitePlayerLabel.setText("White");
        
        Text blackPlayerLabel = new Text();
        blackPlayerLabel.setText("Black");

        Text chipsRemainText = new Text();
        chipsRemainText.setText("Chips Remaining: " + chipsRemaining);

        Button resetButton = new Button("Reset");
        

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

    private Circle makeScoreCircle(){
        Circle circle = new Circle(35);
        circle.setStroke(Color.rgb(200, 200, 200));
        circle.setFill(null);
        circle.setStrokeWidth(10);

        return circle;
    }

    public static void main(String[] args) {
        launch(args); 
    }
}
