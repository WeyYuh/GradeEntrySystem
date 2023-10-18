package com.wy.ges;

// region Import Libraries
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
// endregion

/**
 * The Grade Entry System (GES) implements an application that allows teachers to enter student scores for each
 * individual exams and generate mean, median and recent average percentage scores (last 3 assessments). It also
 * allows teacher to generate individual student report which contains their name, photo, grades (target grade,
 * current grade, aspirational grade), their performance for each exam and output the student's performance
 * in a bar and line chart.
 *
 */
public class Main extends Application {

    /**
     * This method is called to start the JavaFX application
     * @param stage - the JavaFX window
     * @throws IOException - some sort of I/O exception occurred
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("splashScreen.fxml")); // load the animated intro screen
        Scene scene = new Scene(loader.load(), 1000, 600); // set window size for the application

        stage.setResizable(false); // disable user ability to resize the window
        stage.setTitle("Grade Entry System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
