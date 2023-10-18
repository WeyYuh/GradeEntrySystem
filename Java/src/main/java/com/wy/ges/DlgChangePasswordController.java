package com.wy.ges;

// region import libs
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
 * This is the controller class for change password popup window
 */
public class DlgChangePasswordController implements Initializable {

    // region FXML elements
    @FXML
    private PasswordField inputOldPassword;

    @FXML
    private PasswordField inputNewPassword;

    @FXML
    private PasswordField inputConfirmPassword;

    @FXML
    private Label labelOldEmpty;

    @FXML
    private Label labelNewEmpty;

    @FXML
    private Label labelConfirmUnmatched;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnUpdate;

    @FXML
    private Label labelError;
    // endregion

    private String serverDomain = "http://wy.kukupgoldenbay.com";
    private DashboardController parentController;

    /**
     * This method is called when the cancel button is clicked. Nothing will change and the window will close
     */
    @FXML
    void btnCancelClick() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * This method is called to save the new password to the database. It communicates with the PHP so that
     * updating SQL database can happen. If save successfully, the data will be added to the PHP. Java will then
     * update the table with the new information. If an error occurs, application will show an error connection window
     */
    @FXML
    void btnUpdateClick() {
        // check if there are any errors
        if (!labelOldEmpty.isVisible() && !labelNewEmpty.isVisible() && !labelConfirmUnmatched.isVisible()) {
            try {
                // set up connection to PHP server for posting JSON
                URL url = new URL(serverDomain + "/App/changePassword.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                UserData u = SessionData.getInstance().getUserData();
                ServerNewPasswordData data = new ServerNewPasswordData();

                data.username = u.getUsername();
                data.password = u.getPassword();
                data.newPassword = inputNewPassword.getText();

                if (!data.password.equals(inputOldPassword.getText())) {
                    // check if password is correct
                    labelError.setText("Old password incorrect");
                    return;
                }

                String jsonInputString = new ObjectMapper().writeValueAsString(data); // write object as JSON string

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
                    System.out.println(response.toString());

                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode actualObj = mapper.readTree(response.toString()); // map the JSON string as JsonNode object

                    if (actualObj.get("status").asText().equals("invalid")) {
                        labelError.setText(actualObj.get("msg").asText()); // show error message returned from PHP
                    } else {

                        labelError.setText(""); // no error message
                        btnCancelClick(); // close window

                        // update user data
                        u.setId(actualObj.get("id").asInt());
                        u.setUsername(actualObj.get("username").asText());
                        u.setPassword(actualObj.get("password").asText());
                        u.setEmail(SessionData.getInstance().getUserData().getEmail());

                        SessionData.getInstance().setUserData(u); // save user data to session
                    }
                }
            } catch (Exception e) {
                // show no connection message
                parentController.showNoConnection((Stage) btnUpdate.getScene().getWindow());
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is called in the parent controller to parse the controller to this class. This will allow this
     * class to reuse the show no connection method created in the previous class
     * @param dashboardController - the parent controller
     */
    public void initVar(DashboardController dashboardController) {
        parentController = dashboardController;
    }

    /**
     * This is the initialize method that will be called when the window starts. This will set all listeners
     * to all text inputs to show or hide error message
     * @param location - the location used to resolve relative paths for the root object, or null if the location is not known
     * @param resources - the resources used to localize the root object, or null if the root object was not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // add listeners to all text inputs
        inputOldPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBlank()) {
                labelOldEmpty.setVisible(true);
            } else {
                labelOldEmpty.setVisible(false);
            }
        });

        inputNewPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBlank()) {
                labelNewEmpty.setVisible(true);
            } else {
                labelNewEmpty.setVisible(false);
            }
        });

        inputConfirmPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(inputNewPassword.getText()) && !newValue.isBlank()) {
                labelConfirmUnmatched.setVisible(false);
            } else {
                labelConfirmUnmatched.setVisible(true);
            }
        });

    }
}
