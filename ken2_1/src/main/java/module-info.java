module ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;

    opens com.ken2_1.ui to javafx.fxml;
    exports com.ken2_1.ui;
}
