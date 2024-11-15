package com.ken2.ui;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class GameAlerts {

    public static void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
    // 2
    public static void alertTest(){ //GameAlerts.alertTest()
        showAlert("Test", "Test");
    }

    // 1
    public static void alertWrongRingColor() {
        showAlert("Invalid Selection", "Please select a ring of your color.");
    }


    // 2
    public static void alertInvalidMove() {
        showAlert("Invalid Move", "You cannot move your ring to this position. Please select a highlited position.");
    }

    // 1
    public static void alertOutOfBounds() {
        showAlert("Out of Bounds", "This move is outside the board limits.");
    }

    // 1
    public static void alertOccupiedSpace() {
        showAlert("Occupied Space", "This space is already occupied by another piece.");
    }

    // 2
    public static void alertRingPlacement() {
        showAlert("Rule transgression", "You cannot place a ring on another one.");
    }

    //2
    public static void alertPositionHasChip() {
        showAlert("Rule transgression", "There is already a chip here.");
    }

    //1
    public static void alertNoRing() {
        showAlert("Rule transgression", "There are no ring here.");
    }

    // Alert for completing a row
    public static void alertRowCompletion(String playerColor) {
        showAlert("Row Completed", "Congratulations! " + playerColor + " completed a row. Select any " + playerColor + " ring.");
    }

    // Alert for the end of the game
    public static void alertGameEnd(String winningPlayer) {
        showAlert("Congratulations", "The game has ended. Winner: " + winningPlayer);
    }

    public static void alertGameEndDraw(String winningPlayer) {
        showAlert(":/", "The game has ended on a draw");
    }

}

