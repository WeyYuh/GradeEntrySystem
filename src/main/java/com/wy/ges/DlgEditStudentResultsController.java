package com.wy.ges;

// region import libs
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
// endregion

/**
 * This is the controller class for editing student's results window
 */
public class DlgEditStudentResultsController implements Initializable {

    // region FXML elements
    @FXML
    private TextField inputName;

    @FXML
    private TextField inputScore;

    @FXML
    private TextField inputGrade;

    @FXML
    private TextField inputPercentage;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnPrev;

    @FXML
    private Button btnNext;
    // endregion


    private ArrayList<AssessmentResult> results;
    private int resultsIndex;
    private ClassroomData selectedClassroom;
    private int maxScore;

    private DlgAddEditAssessmentController parentController;
    /**
     * This method converts string to float
     * @param text - the text to be converted into float
     * @return - value of text as float, if cannot parse float, then return 0
     */
    private float toFloat(String text) {
        try {
            return Float.parseFloat(text);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * This method is called to save the students' results to the ArrayList
     */
    private void saveResult() {
        results.get(resultsIndex).setScore(toFloat(inputScore.getText()));
        results.get(resultsIndex).setPercentage(toFloat(inputPercentage.getText()));
    }

    /**
     * This method is called to initialize the text field values
     */
    private void initTextFieldValues() {
        inputName.setText(results.get(resultsIndex).getStudentName()); //  set the name as student's name
        DecimalFormat df = new DecimalFormat("0.#"); // formating so no trailing zeroes

        // fill input with score and percentage
        inputScore.setText(String.valueOf(df.format(results.get(resultsIndex).getScore())));
        inputGrade.setText("");
        inputPercentage.setText(String.valueOf(df.format(results.get(resultsIndex).getPercentage())));

        // default focus on score
        inputScore.requestFocus();

        // if input score is 0, highlight the whole text for more user-friendly interactions
        if (inputScore.getText().equals("0")) {
            inputScore.selectAll();
        }
    }

    /**
     * This method is called in the previous controller so that parameters can be parsed into this class
     * @param res - the list of student's results
     * @param resIndex - the index for the result
     * @param classroom - the object representing the classroom
     * @param p - the parent controller
     * @param maxScore - the max score
     */
    public void init(ArrayList<AssessmentResult> res, int resIndex, ClassroomData classroom, DlgAddEditAssessmentController p, int maxScore) {
        results = res;
        resultsIndex = resIndex;
        selectedClassroom = classroom;
        parentController = p;
        this.maxScore = maxScore;

        initTextFieldValues();
    }

    /**
     * This method is called when calculate score button is clicked, it will fill the text field for percentage with
     * the calculated percentage.
     */
    @FXML
    void calcScore() {
        DecimalFormat df = new DecimalFormat("0.#"); // format so no trailing zeroes
        inputPercentage.setText(String.valueOf(df.format(toFloat(inputScore.getText()) / (float) maxScore * 100)));

    }

    /**
     * This method is called when calculate grade button is clicked, it will fill the text field for percentage with the
     * calculated percentage
     */
    @FXML
    void calcGrade() {
        DecimalFormat df = new DecimalFormat("0.#"); // format so no trailing zeroes
        inputPercentage.setText(String.valueOf(df.format(gradeToPercentage(inputGrade.getText()))));
    }

    /**
     * This method is called when the next button is clicked or when the enter key is pressed.
     */
    @FXML
    void btnNextClick() {

        // quickly losing focus on input score and regaining focus on input score so that percentage can be automatically calculated when the enter key is pressed
        btnNext.requestFocus();
        inputScore.requestFocus();

        saveResult();

        if (resultsIndex != results.size() - 1) {
            resultsIndex++; // not the last student, go next by 1
            initTextFieldValues();
        } else {
            resultsIndex = 0; // last student, loop back to the first student
            initTextFieldValues();
        }
    }

    /**
     * This method is called when the previous button is clicked
     */
    @FXML
    void btnPrevClick() {
        saveResult();
        if (resultsIndex > 0 ) {
            resultsIndex--; // not the first student, go back by 1
            initTextFieldValues();
        } else {
            resultsIndex = results.size() - 1; // first student, loop back to the last student
            initTextFieldValues();
        }
    }

    /**
     * This method is called when the save button is clicked. The parent controller will refresh the table with the
     * new data. This window is then closed
     */
    @FXML
    void btnSaveClick() {
        // quickly losing and regaining focus on input score so that automatic conversion from score to percentage can happen
        btnSave.requestFocus();
        inputScore.requestFocus();

        saveResult();

        parentController.initStudentResults();
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    /**
     * This method converts grade letter to percentage. It is currently set to default values (check switch statement
     * below).
     * @param grade - the grade letter
     * @return percentage score
     */
    public static float gradeToPercentage(String grade) {
        switch (grade.trim().toUpperCase()) {
            case "A*" -> {
                return 90;
            }
            case "A" -> {
                return 80;
            }
            case "B" -> {
                return 70;
            }
            case "C" -> {
                return 60;
            }
            case "D" -> {
                return 50;
            }
            case "E" -> {
                return 40;
            }
            case "U" -> {
                return 30;
            }
            default -> {
                // grade letter is unrecognised, return 0
                return 0;
            }
        }
    }

    /**
     * This method is called when the controller is loaded. It adds listeners to both input so that automatic conversion
     * can happen
     * @param location - location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources - resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inputScore.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && toFloat(inputPercentage.getText()) == 0) {
                calcScore();
            }
        });

        inputGrade.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && toFloat(inputPercentage.getText()) == 0) {
                calcGrade();
            }
        });
    }
}
