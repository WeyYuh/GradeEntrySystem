package com.wy.ges;

// region import libs
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
// endregion

/**
 * This is the controller class for no connection message.
 */
public class DlgNoConnectionController {

    @FXML
    private Button btnClose;

    /**
     * This method is called when the close button is clicked or when the escape key is pressed. It closes the window
     */
    @FXML
    void btnCloseClick() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}
