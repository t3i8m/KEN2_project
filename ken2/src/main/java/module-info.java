module ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;

    opens com.ken2.ui to javafx.fxml;
    exports com.ken2.ui;
}
