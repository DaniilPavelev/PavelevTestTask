module com.example.pavelevtesttask {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.example.pavelevtesttask to javafx.fxml;
    exports com.example.pavelevtesttask;
}