module com.example.pavelevtesttask {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.apache.commons.lang3;

    opens com.example.pavelevtesttask to javafx.fxml;
    exports com.example.pavelevtesttask;
}