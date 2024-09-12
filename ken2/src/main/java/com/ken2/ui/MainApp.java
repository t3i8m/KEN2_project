package com.ken2.ui;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello, JavaFX!"); 
        primaryStage.setWidth(400);              
        primaryStage.setHeight(300);             
        primaryStage.show();                     
    }

    public static void main(String[] args) {
        launch(args); 
    }
}
