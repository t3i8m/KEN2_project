module ui {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.ken2.ui to javafx.fxml;
    exports com.ken2.ui;
}
