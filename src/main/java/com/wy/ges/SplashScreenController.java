package com.wy.ges;

// region imported libs
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.ResourceBundle;
// endregion

/**
 * This is the controller class for splashscreen.fxml. It plays an animation of the custom GES logo
 */
public class SplashScreenController implements Initializable {

    public static UserData userData = new UserData(); // Create new instance of UserData class

    @FXML
    private ImageView ivLogo;

    /**
     * This method is called when the controller is loaded so that the splash screen animation can play
     * @param location - location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources - resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Timeline logoAnimation = new Timeline(); // create a new timeline animation

        // add key frames to the timeline
        logoAnimation.getKeyFrames().addAll(
                new KeyFrame(new Duration(0), new KeyValue(ivLogo.opacityProperty(), 0)),
                new KeyFrame(new Duration(1000), new KeyValue(ivLogo.opacityProperty(), 0)),
                new KeyFrame(new Duration(3000), new KeyValue(ivLogo.opacityProperty(), 1))
        );

        logoAnimation.play(); // play the animation

        // when animation is finished, go to the login screen
        logoAnimation.setOnFinished(event -> {

            try {
                userData = (UserData) ResourceManager.load("login.sav"); // Load the file containing username and password
            } catch (Exception e) {
                userData = null; // make data null for now
            }
            if (userData != null) { // check if user data exists in the file

                try {
                    // set up PHP connection for posting JSON string
                    URL url = new URL("http://wy.kukupgoldenbay.com/App/login.php");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);

                    // set object's public accessors to data from local file
                    ServerLoginData data = new ServerLoginData();
                    data.username = userData.getUsername();
                    data.password = userData.getPassword();

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

                            // create a new user object and set its attributes
                            UserData u = new UserData();
                            u.setId(actualObj.get("id").asInt());
                            u.setUsername(actualObj.get("username").asText());
                            u.setPassword(actualObj.get("password").asText());
                            u.setEmail(actualObj.get("email").asText());

                            SessionData.getInstance().setUserData(u); // save user to current session

                            try {
                                // load dashboard window
                                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dashboard.fxml")));
                                Stage stage = (Stage) ivLogo.getScene().getWindow();
                                stage.getScene().setRoot(root);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {
                            // username or password expired, go to log in screen
                            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
                            Stage stage = (Stage) ivLogo.getScene().getWindow();
                            stage.getScene().setRoot(root);
                        }
                    }
                } catch (Exception e) {
                    try {
                        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
                        Stage stage = (Stage) ivLogo.getScene().getWindow();
                        stage.getScene().setRoot(root);
                    } catch (IOException ignored) {
                    }
                }
            } else {
                try {
                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
                    Stage stage = (Stage) ivLogo.getScene().getWindow();
                    stage.getScene().setRoot(root);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
