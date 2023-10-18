package com.wy.ges;

// region import libs
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
// endregion

/**
 * This is the controller class for registering new user window
 */
public class DlgAddNewUserController implements Initializable {

    // region FXML elements
    @FXML
    private TextField inputUsername;

    @FXML
    private PasswordField inputPassword;

    @FXML
    private PasswordField inputConfirmPassword;

    @FXML
    private TextField inputEmail;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private Label labelUsernameEmpty;

    @FXML
    private Label labelPasswordEmpty;

    @FXML
    private Label labelConfirmUnmatched;

    @FXML
    private Label labelEmailEmpty;

    @FXML
    private Label labelError;
    // endregion

    private String serverDomain = "http://wy.kukupgoldenbay.com";
    private DashboardController parentController;

    /**
     * This method is called in its parent class so that the show no connection method can be used here
     * @param dashboardController - the parent controller
     */
    public void initVars(DashboardController dashboardController) {
        parentController = dashboardController;
    }

    /**
     * This method is called when the cancel button is clicked. Nothing will change and the window will close
     */
    @FXML
    void btnCancelClick() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * This method is called to save the new data to the database. It communicates with the PHP so that inserting or
     * updating SQL database can happen. If save successfully, the data will be added to the PHP. If an error occurs,
     * application will show an error connection window
     */
    @FXML
    void btnSaveClick() {
        // check if there are any error inputs
        if (!labelUsernameEmpty.isVisible() && !labelPasswordEmpty.isVisible() && !labelConfirmUnmatched.isVisible() && !labelEmailEmpty.isVisible()) {

            try {
                // set up connection to php server to post a JSON string
                URL url = new URL(serverDomain + "/App/register.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                UserData u = SessionData.getInstance().getUserData();
                ServerRegisterData data = new ServerRegisterData();

                // set the public attributes of the server object to the inputs
                data.username = u.getUsername();
                data.password = u.getPassword();
                data.newUserName = inputUsername.getText();
                data.newUserPassword = inputPassword.getText();
                data.newUserEmail = inputEmail.getText();

                // write the server object as JSON
                String jsonInputString = new ObjectMapper().writeValueAsString(data);

                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode actualObj = mapper.readTree(response.toString()); // map JSON string to JsonNode object

                    if (actualObj.get("status").asText().equals("invalid")) {
                        labelError.setText(actualObj.get("msg").asText()); // show the error message returned from PHP
                    } else {
                        labelError.setText(""); // no error message if valid

                        // close the current window
                        Stage stage = (Stage) btnSave.getScene().getWindow();
                        stage.close();
                    }
                }
            } catch (Exception e) {
                // error occurred, show no connection message
                parentController.showNoConnection((Stage) btnSave.getScene().getWindow());
            }


        }
    }

    /**
     * This method is called to initialize the window.
     * @param location - the location used to resolve relative paths for the root object, or null if the location is not known
     * @param resources - the resources used to localize the root object, or null if the root object was not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // add listeners to each text input, if input is blank, show message telling user it cannot be blank

        // region listeners for text inputs
        inputUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBlank()) {
                labelUsernameEmpty.setVisible(true);
            } else {
                labelUsernameEmpty.setVisible(false);
            }
        });

        inputPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBlank()) {
                labelPasswordEmpty.setVisible(true);
            } else {
                labelPasswordEmpty.setVisible(false);
            }
        });

        inputConfirmPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(inputPassword.getText()) && !newValue.isBlank()) {
                labelConfirmUnmatched.setVisible(false);
            } else {
                labelConfirmUnmatched.setVisible(true);
            }
        });

        inputEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBlank()) {
                labelEmailEmpty.setVisible(true);
            } else {
                labelEmailEmpty.setVisible(false);
            }
        });
        // endregion
    }
}
