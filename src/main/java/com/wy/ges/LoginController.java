package com.wy.ges;

// region import libs
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
// endregion

/**
 * TODO
 * UnknownHostException check connection print line 74
 */


public class LoginController implements Initializable {

    // region FXML elements
    @FXML
    private TextField inputUsername;

    @FXML
    private PasswordField inputPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnReset;

    @FXML
    private CheckBox checkSave;

    @FXML
    private Label errMsg;

    @FXML
    private Pane paneLogin;
    // endregion

    /**
     * This method is called when the log in button is clicked. Connection to PHP is established and the input is
     * written as JSON. This allows PHP to check if the login details is correct
     */
    @FXML
    void login() {
        try {
            // set up PHP connection for posting JSON string
            URL url = new URL("http://wy.kukupgoldenbay.com/App/login.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            // set object's public accessors to inputs
            ServerLoginData data = new ServerLoginData();
            data.username = inputUsername.getText();
            data.password = inputPassword.getText();

            String jsonInputString = new ObjectMapper().writeValueAsString(data); // write server object as JSON string

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
                JsonNode actualObj = mapper.readTree(response.toString()); // map JSON string to JsonNode

                if (actualObj.get("status").asText().equals("valid")) {
                    errMsg.setText(""); // no error message

                    // create a new user object and set its attributes
                    UserData u = new UserData();
                    u.setId(actualObj.get("id").asInt());
                    u.setUsername(actualObj.get("username").asText());
                    u.setPassword(actualObj.get("password").asText());
                    u.setEmail(actualObj.get("email").asText());

                    SessionData.getInstance().setUserData(u); // save user to current session

                    if (checkSave.isSelected()) {
                        try {
                            ResourceManager.save(u, "login.sav");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        // load dashboard window
                        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dashboard.fxml")));
                        Stage stage = (Stage) btnLogin.getScene().getWindow();
                        stage.getScene().setRoot(root);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    // show error message returned from PHP
                    errMsg.setText(actualObj.get("msg").asText());
                }
            }
        } catch (Exception e) {
            // error occurred, show no connection window

            e.printStackTrace();
            try {
                final Stage popupNoConnection = new Stage();
                popupNoConnection.initModality(Modality.APPLICATION_MODAL);
                popupNoConnection.initOwner(btnLogin.getScene().getWindow());

                FXMLLoader loader = new FXMLLoader(getClass().getResource("dlgNoConnection.fxml"));
                Scene dialogScene = new Scene(loader.load());

                popupNoConnection.setScene(dialogScene);
                popupNoConnection.setResizable(false);
                popupNoConnection.initStyle(StageStyle.UNDECORATED);
                popupNoConnection.show();
            } catch (Exception ignored) {

            }

        }

    }

    /**
     * This method is called when the window is loaded. It will initialize and play an animation for the login pane.
     * It also adds a listener to the input password because the size of dots for the password is too big
     * @param location - location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources - resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // define animation and play it
        Timeline loginAnimation = new Timeline();
        loginAnimation.getKeyFrames().addAll(
                new KeyFrame(new Duration(0), new KeyValue(paneLogin.opacityProperty(), 0)),
                new KeyFrame(new Duration(500), new KeyValue(paneLogin.opacityProperty(), 0)),
                new KeyFrame(new Duration(1000), new KeyValue(paneLogin.opacityProperty(), 0.88))
        );
        loginAnimation.play();

        // add listener for password field
        inputPassword.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                inputPassword.setFont(Font.font(7));
            } else {
                inputPassword.setFont(Font.font(13));
            }
        }));


    }
}
