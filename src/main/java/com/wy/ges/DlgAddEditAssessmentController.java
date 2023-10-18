package com.wy.ges;

// region Imported Libraries
import com.fasterxml.jackson.databind.JsonNode; // allow accessing JSON attributes like Java object
import com.fasterxml.jackson.databind.ObjectMapper; // Serialize Java Objects into JSON and Deserialize JSON into Java Objects
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
//endregion

/**
 * This is the controller for the popup window of adding or editing an existing assessment
 */
public class DlgAddEditAssessmentController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private Label labelErrorName;

    @FXML
    private TextField inputAssessmentName;

    @FXML
    private TextField inputAxScore;

    @FXML
    private TextField inputAScore;

    @FXML
    private TextField inputBScore;

    @FXML
    private TextField inputCScore;

    @FXML
    private TextField inputDScore;

    @FXML
    private TextField inputEScore;

    @FXML
    private TextField inputUScore;

    @FXML
    private Label labelErrorTopic;

    @FXML
    private TextField inputAssessmentTopic;

    @FXML
    private Label labelErrorCategory;

    @FXML
    private TextField inputCategory;

    @FXML
    private Label labelErrorMaxScore;

    @FXML
    private TextField inputMaxScore;

    @FXML
    private Label labelErrorGradeScores;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    private TableView tableStudents;

    private boolean fNewAssessment = true;
    private AssessmentData selectedAssessment;
    private ClassroomData selectedClassroom;
    private DashboardController parentController;
    private ArrayList<AssessmentResult> results;

    private AssessmentResult selectedStudentResult;

    /**
     * This method is called to initialize the window for either adding or editing assessment
     * @param fInd - true if creating assessment and vice versa
     * @param assessment - the assessment selected
     * @param classroom - the classroom the assessment is under
     * @param results - the students' results
     * @param p - parent controller
     */
    public void initNewAssessmentInd(boolean fInd, AssessmentData assessment, ClassroomData classroom, ArrayList<AssessmentResult> results, DashboardController p) {
        fNewAssessment = fInd;
        selectedAssessment = assessment;
        selectedClassroom = classroom;
        this.results = results;

        if (fNewAssessment) {
            labelErrorName.setVisible(true);
            labelErrorCategory.setVisible(true);
            labelErrorTopic.setVisible(true);
            labelErrorMaxScore.setVisible(true);

            // assessment name, topic, category and max score is empty
            inputAssessmentName.setText("");
            inputAssessmentTopic.setText("");
            inputCategory.setText("");
            inputMaxScore.setText("");

            // set score boundaries to default scores in the Excel spreadsheet
            inputAxScore.setText("90");
            inputAScore.setText("80");
            inputBScore.setText("70");
            inputCScore.setText("60");
            inputDScore.setText("50");
            inputEScore.setText("40");
            inputUScore.setText("0");

            selectedAssessment = new AssessmentData();
        } else {

            labelErrorName.setVisible(false);
            labelErrorCategory.setVisible(false);
            labelErrorTopic.setVisible(false);
            labelErrorMaxScore.setVisible(false);

            // set text field values to existing values of the assessment
            inputAssessmentName.setText(selectedAssessment.getAssessmentName());
            inputAssessmentTopic.setText(selectedAssessment.getTopic());
            inputCategory.setText(selectedAssessment.getCategory());
            inputMaxScore.setText(String.valueOf(selectedAssessment.getMaxScore()));
            inputAxScore.setText(String.valueOf(selectedAssessment.getAxScore()));
            inputAScore.setText(String.valueOf(selectedAssessment.getAScore()));
            inputBScore.setText(String.valueOf(selectedAssessment.getBScore()));
            inputCScore.setText(String.valueOf(selectedAssessment.getCScore()));
            inputDScore.setText(String.valueOf(selectedAssessment.getDScore()));
            inputEScore.setText(String.valueOf(selectedAssessment.getEScore()));
            inputUScore.setText(String.valueOf(selectedAssessment.getUScore()));
        }

        parentController = p;
        initStudentResults();
    }

    /**
     * This method initialize the table for the students results in the assessment window
     */
    public void initStudentResults() {
        // clear all contents in the table
        tableStudents.getColumns().clear();
        tableStudents.getItems().clear();

        // set up column for student's name
        TableColumn<AssessmentResult, String> studentName = new TableColumn<>("Student Name");
        studentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        parentController.setupColumnProperties(tableStudents, studentName, 40);

        // set up column for student's score
        TableColumn<AssessmentResult, Float> studentScore = new TableColumn<>("Score");
        studentScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        studentScore.setCellFactory(c-> new TableCell<>() {
            /**
             * This method is used to format the decimal points of scores
             * @param item - the float to do formatting
             * @param empty - check if empty
             */
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    DecimalFormat df = new DecimalFormat("0.#"); // no trailing zeroes
                    setText(df.format(item));
                }
            }
        });
        parentController.setupColumnProperties(tableStudents, studentScore, 25);

        // set up column for student's percentage
        TableColumn<AssessmentResult, Float> studentPercent = new TableColumn<>("Percentage");
        studentPercent.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        studentPercent.setCellFactory(c-> new TableCell<>() {
            /**
             * This method is used to format the decimal points of percentage
             * @param item - the float to do formatting
             * @param empty - check if empty
             */
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    DecimalFormat df = new DecimalFormat("0.#"); // no trailing zeroes
                    setText(df.format(item));
                }
            }
        });

        parentController.setupColumnProperties(tableStudents, studentPercent, 25);

        tableStudents.getColumns().addAll(studentName, studentScore, studentPercent);

        for (AssessmentResult result: results) { // add all results to the table
            tableStudents.getItems().add(result);
        }
    }

    /**
     * This method is called when the cancel button is clicked. It closes the popup window
     */
    @FXML
    void btnCancelClick() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * This method convert a string into an integer
     * @param text - string
     * @param fGrade - the boolean indicating if this is a grade boundary score
     * @return - the integer value of a string
     */
    private int toInt(String text, boolean fGrade) {
        if (text.isBlank()) {
            return 0;
        } else {
            return Integer.parseInt(text);
        }
    }

    /**
     * This sets up the comparator used to compare different student scores
     */
    private Comparator<AssessmentResult> studentPercentageComparator = (s1, s2) -> {
        float studentPercentage1 = s1.getPercentage();
        float studentPercentage2 = s2.getPercentage();

        //ascending order
        return (int) ((studentPercentage1-studentPercentage2)*100);

    };

    /**
     * This sorts the list of results in ascending percentage score
     * @param results - the arraylist of students' results for the assessment
     */
    private void sortPercentage(ArrayList<AssessmentResult> results) {
        Collections.sort(results, studentPercentageComparator);
    }


    /**
     * This method calculates lowest, highest, median and mean percentage for an assessment
     * @param results - the arraylist of students' results for the assessment
     */
    private void statsCalc(ArrayList<AssessmentResult> results) {
        int size = results.size();
        if (size == 0) { // check if there is results for assessment
            return;
        }

        sortPercentage(results);

        // return a list without zeroes
        int zeroCount = 0;
        for (int i = 0; i < size; i++) {
            if (results.get(i).getPercentage() == 0) {
                zeroCount++;
            } else {
                break;
            }
        }

        int trueSize = size - zeroCount;
        if (trueSize == 0) {
            return;
        }

        if (selectedAssessment != null) {

            selectedAssessment.setLowest(results.get(zeroCount).getPercentage()); // the first non-zero index is lowest
            selectedAssessment.setHighest(results.get(size - 1).getPercentage()); // the last index is highest

            float total = 0;
            for (int i = zeroCount; i < size; i++) {
                total = total + results.get(i).getPercentage();
            }
            float mean = total / trueSize;
            selectedAssessment.setMean(mean);

            float median;
            if (trueSize % 2 == 0) { // event number of elements
                int firstIndex = (trueSize / 2) - 1;
                median = (results.get(zeroCount + firstIndex).getPercentage() + results.get(zeroCount + firstIndex + 1).getPercentage()) / 2;
            } else { // odd number of elements
                int firstIndex = ((trueSize + 1) / 2) - 1;
                median = results.get(zeroCount + firstIndex).getPercentage();
            }
            selectedAssessment.setMedian(median);
        }
    }


    /**
     * This function is called when the save button is clicked. If inputs are valid, the data will be sent to php server
     * to be inserted/updated tables on the database
     */
    @FXML
    void btnSaveClick() {

        // hide all error message
        labelErrorName.setVisible(false);
        labelErrorTopic.setVisible(false);
        labelErrorCategory.setVisible(false);
        labelErrorMaxScore.setVisible(false);
        labelErrorGradeScores.setVisible(false);


        // read input data from text field
        String assessmentName = inputAssessmentName.getText();
        String assessmentTopic = inputAssessmentTopic.getText();
        String assessmentCategory = inputCategory.getText();
        int assessmentMaxScore = toInt(inputMaxScore.getText(), false);
        int assessmentAxScore = toInt(inputAxScore.getText(), true);
        int assessmentAScore = toInt(inputAScore.getText(), true);
        int assessmentBScore = toInt(inputBScore.getText(), true);
        int assessmentCScore = toInt(inputCScore.getText(), true);
        int assessmentDScore = toInt(inputDScore.getText(), true);
        int assessmentEScore = toInt(inputEScore.getText(), true);
        int assessmentUScore = toInt(inputUScore.getText(), true);

        // default valid state to true
        boolean valid = true;

        // return false if any inputs is blank
        if (assessmentName.isBlank()) {
            labelErrorName.setVisible(true);
            valid = false;
        }

        if (assessmentTopic.isBlank()) {
            labelErrorTopic.setVisible(true);
            valid = false;
        }

        if (assessmentCategory.isBlank()) {
            labelErrorCategory.setVisible(true);
            valid = false;
        }

        if (assessmentMaxScore == 0) {
            labelErrorMaxScore.setVisible(true);
            valid = false;
        }


        if (valid) {

            statsCalc(results);

            try {
                // set up the connection to the php server for hosting
                URL url = new URL("http://wy.kukupgoldenbay.com/App/assessment.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                UserData u = SessionData.getInstance().getUserData();
                ServerAssessmentData data = new ServerAssessmentData(); // create a temporary Java object for JSON conversion

                data.username = u.getUsername();
                data.password = u.getPassword();

                if (fNewAssessment) { // adding assessment
                    data.cmd = "add";
                    data.assessmentId = 0;
                } else { // editing assessment
                    data.cmd = "edit";
                    data.assessmentId = selectedAssessment.getId();
                }

                // save local data in Java object
                data.results = results;
                data.assessmentName = assessmentName;
                data.topic = assessmentTopic;
                data.category = assessmentCategory;
                data.maxScore = assessmentMaxScore;
                data.highest = selectedAssessment.getHighest();
                data.median = selectedAssessment.getMedian();
                data.mean = selectedAssessment.getMean();
                data.lowest = selectedAssessment.getLowest();
                data.axScore = assessmentAxScore;
                data.aScore = assessmentAScore;
                data.bScore = assessmentBScore;
                data.cScore = assessmentCScore;
                data.dScore = assessmentDScore;
                data.eScore = assessmentEScore;
                data.uScore = assessmentUScore;
                data.cid = selectedClassroom.getId();

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
                    System.out.println(response.toString());
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode actualObj = mapper.readTree(response.toString());

                    if (actualObj.get("status").asText().equals("valid")) {
                        SessionData.getInstance().setAssessments(actualObj.get("assessments")); // save the assessments return from server to current session
                    }
                }

            } catch (Exception e) {
                // show no connection window
                parentController.showNoConnection((Stage) btnSave.getScene().getWindow());
                e.printStackTrace();
            }

            parentController.initAssessments(); // reinitialize assessments table with new data
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close(); // close current window
        }
    }

    /**
     * This method shows a window for teachers to change student's score for this assessment
     * @throws Exception - fxml file cannot load
     */
    private void editStudentScore() throws Exception {
        final Stage popupEdit = new Stage();
        popupEdit.initModality(Modality.APPLICATION_MODAL);
        popupEdit.initOwner(tableStudents.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dlgEditStudentResults.fxml"));
        Scene dialogScene = new Scene(loader.load());

        // send parent controller to popup
        final DlgEditStudentResultsController controller = loader.getController();
        controller.init(results, tableStudents.getSelectionModel().getSelectedIndex(), selectedClassroom, this, Integer.parseInt(inputMaxScore.getText()));

        popupEdit.setScene(dialogScene);
        popupEdit.setResizable(false);
        popupEdit.show();
    }


    /**
     * Initialize function from the Initializable interface. This function is called to initialize the controller
     * @param location - location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources - resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getText();

            if (text.matches("[0-9]*")) {
                return change;
            }

            return null;
        };

        // prevents user to enter non-number characters
        inputMaxScore.setTextFormatter(new TextFormatter<String>(filter));
        inputAxScore.setTextFormatter(new TextFormatter<String>(filter));
        inputAScore.setTextFormatter(new TextFormatter<String>(filter));
        inputBScore.setTextFormatter(new TextFormatter<String>(filter));
        inputCScore.setTextFormatter(new TextFormatter<String>(filter));
        inputDScore.setTextFormatter(new TextFormatter<String>(filter));
        inputEScore.setTextFormatter(new TextFormatter<String>(filter));
        inputUScore.setTextFormatter(new TextFormatter<String>(filter));


        // hide error message if text field is not empty
        inputAssessmentName.textProperty().addListener((obs, oldName, newName) -> {
            if (newName.isBlank()) {
                labelErrorName.setVisible(true);
            } else {
                labelErrorName.setVisible(false);
            }
        });

        // hide error message if text field is not empty
        inputAssessmentTopic.textProperty().addListener((obs, oldName, newName) -> {
            if (newName.isBlank()) {
                labelErrorTopic.setVisible(true);
            } else {
                labelErrorTopic.setVisible(false);
            }
        });

        // hide error message if text field is not empty
        inputCategory.textProperty().addListener((obs, oldName, newName) -> {
            if (newName.isBlank()) {
                labelErrorCategory.setVisible(true);
            } else {
                labelErrorCategory.setVisible(false);
            }
        });

        // hide error message if text field is not empty
        inputMaxScore.textProperty().addListener((obs, oldName, newName) -> {
            if (newName.isBlank()) {
                labelErrorMaxScore.setVisible(true);
            } else {
                labelErrorMaxScore.setVisible(false);
            }
        });

        tableStudents.setRowFactory( tv -> {
            TableRow<StudentData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) { // user double click on a row
                    try {
                        if (!inputMaxScore.getText().isBlank()) { // check if out of is zero
                            editStudentScore();
                        } else { // don't show edit score window to prevent dividing zero
                            Alert errorEditScore = new Alert(Alert.AlertType.ERROR);
                            errorEditScore.setTitle("Error");
                            errorEditScore.setHeaderText("Please set max score before editing student's score");
                            errorEditScore.show();
                        }

                    } catch (Exception e) {
                        // fxml file cannot load
                        e.printStackTrace();
                    }

                }
            });
            return row ;
        });

    }
}
