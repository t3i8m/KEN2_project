package com.ken2.ui;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class GameAlerts {
    /**
     * Displays an alert with the specified title and message.
     *
     * @param title   The title of the alert window.
     * @param message The content text to be displayed in the alert.
     * @paramtype    The type of the alert (e.g., INFORMATION, WARNING, ERROR).
     */
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
    public static void alertInvalidMove() {
        showAlert("Invalid Move", "You cannot move your ring to this position. Please select a highlited position.");
    }

    public static void alertNoRing() {
        showAlert("Rule transgression", "There are no ring here.");
    }

    // Alert for completing a row
    public static void alertRowCompletion(String playerColor) {
        showAlert("Row Completed", "Congratulations! " + playerColor + " completed a row. Select any " + playerColor + " ring and remove 5 winning chips.");
    }

    // Alert for the end of the game
    public static void alertGameEnd(String winningPlayer) {
        showAlert("WINNER WINNER CHICKEN DINNER", "The game has ended. Winner: " + winningPlayer);
    }

}

