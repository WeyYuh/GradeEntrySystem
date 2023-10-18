module com.wy.ges {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;


    opens com.wy.ges to javafx.fxml;
    exports com.wy.ges;
}