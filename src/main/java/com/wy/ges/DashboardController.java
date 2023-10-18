package com.wy.ges;

//region Import Libraries
import com.fasterxml.jackson.databind.JsonNode; // allow accessing JSON attributes like Java object
import com.fasterxml.jackson.databind.ObjectMapper; // Serialize Java objects into JSON and Deserialize JSON into Java objects
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
// endregion

/**
 * This is the controller for the dashboard window, the main window of the application.
 */
public class DashboardController implements Initializable {

    // region Side Menu FXML element IDs
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnQuit;
    @FXML
    private Button btnMain;
    @FXML
    private Button btnAssessment;
    @FXML
    private Button btnSettings;
    @FXML
    private Button btnClassroom;
    @FXML
    private ImageView ivUser;
    @FXML
    private Label labelID;
    // endregion

    // region Main Page FXML element IDs
    @FXML
    private AnchorPane pageMain;

    @FXML
    private TableView tableWell;

    @FXML
    private TableView tableNotWell;
    // endregion

    // region Classroom Page FXML element IDs
    @FXML
    private AnchorPane pageClassroom;
    @FXML
    private Button btnAddClassroom;
    @FXML
    private Button btnEditClassroom;
    @FXML
    private Button btnDeleteClassroom;
    @FXML
    private Button btnViewStudents;
    @FXML
    private TableView tableClassroom;

    private ClassroomData selectedClassroom;
    // endregion

    private int count = 0; // temporary debug

    // region Students Page FXML element IDs
    @FXML
    private Label labelClassroom;
    @FXML
    private AnchorPane pageStudent;
    @FXML
    private Button btnViewReport;
    @FXML
    private Button btnAddStudent;

    @FXML
    private Button btnEditStudent;

    @FXML
    private Button btnDeleteStudent;
    @FXML
    private Button btnBack;
    @FXML
    private TableView tableStudents;
    @FXML
    private ChoiceBox<String> choiceClassrooms;
    @FXML
    private Button btnRefreshRes;
    private StudentData selectedStudent;
    private ArrayList<AssessmentResult> classroomResults;
    // endregion

    // region Assessment Page FXML element IDs
    @FXML
    private AnchorPane pageAssessment;
    @FXML
    private TableView tableAssessments;
    @FXML
    private ChoiceBox<String> choiceAssessmentClassrooms;
    @FXML
    private Label labelAssessmentClassroom;
    @FXML
    private Button btnAddAssessment;

    @FXML
    private Button btnEditAssessment;

    @FXML
    private Button btnDeleteAssessment;
    private ClassroomData selectedAssessmentClassroom;
    private AssessmentData selectedAssessment;
    private ArrayList<AssessmentData> classroomAssessments;
    // endregion

    // region Settings Page FXML element IDs
    @FXML
    private AnchorPane pageSettings;

    @FXML
    private Button btnAddNewUser;

    @FXML
    private Button btnChangePassword;

    @FXML
    private Button btnUploadImg;
    // endregion

    private final String serverDomain = "http://wy.kukupgoldenbay.com"; // php server domain

    private int screenCode; // the number code for each page

    /**
     * This method shows a popup window indicating that there is no connection
     * @param currentStage - the stage of the current window, this is required to set the window owner to lock the
     *                     parent stage until the message is dismissed
     */
    public void showNoConnection(Stage currentStage) {
        try {
            final Stage popupNoConnection = new Stage();
            popupNoConnection.initModality(Modality.APPLICATION_MODAL);
            popupNoConnection.initOwner(currentStage);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("dlgNoConnection.fxml"));
            Scene dialogScene = new Scene(loader.load());

            popupNoConnection.setScene(dialogScene);
            popupNoConnection.setResizable(false);
            popupNoConnection.initStyle(StageStyle.UNDECORATED);
            popupNoConnection.show();
        } catch (Exception ignored) {

        }
    }

    /**
     * This method is used to change the screen in the dashboard. Number code for each page is below:<br>
     * 0: Main Page<br>
     * 1: Student Page<br>
     * 2: Classroom Page<br>
     * 3: Settings Page<br>
     * 4: Assessment Page<br>
     * @param screenCode The number code for the page desired to show
     */
    private void setScreen(int screenCode) {
        this.screenCode = screenCode;

        // hide all the pages at the start
        pageMain.setVisible(false);
        pageStudent.setVisible(false);
        pageSettings.setVisible(false);
        pageClassroom.setVisible(false);
        pageAssessment.setVisible(false);

        // set all button style to style not in hover
        btnMainExit();
        btnClassroomExit();
        btnAssessmentExit();
        btnSettingsExit();
        btnLogoutExit();
        btnQuitExit();

        // show the page based on the screen code parsed into the method
        switch (screenCode) {
            case 0 -> {
                btnMain.setStyle("-fx-background-color: #164f30; -fx-border-color: white; -fx-border-width: 2px;"); // highlight the main page button for indication purpose
                pageMain.setVisible(true); // show main page
            }
            case 1 -> {
                btnClassroom.setStyle("-fx-background-color: #164f30; -fx-border-color: white; -fx-border-width: 2px;"); // highlight the classroom page button for indication purpose
                pageStudent.setVisible(true); // show student page
            }
            case 2 -> {
                btnClassroom.setStyle("-fx-background-color: #164f30; -fx-border-color: white; -fx-border-width: 2px;"); // highlight the classroom page button for indication purpose
                pageClassroom.setVisible(true); // show classroom page
            }
            case 3 -> {
                btnSettings.setStyle("-fx-background-color: #164f30; -fx-border-color: white; -fx-border-width: 2px;"); // highlight the settings page button for indication purpose
                pageSettings.setVisible(true); // show settings page
            }
            case 4 -> {
                btnAssessment.setStyle("-fx-background-color: #164f30; -fx-border-color: white; -fx-border-width: 2px;"); // highlight the assessment page button for indication purpose
                pageAssessment.setVisible(true); // show assessment page
            }
        }
    }

    /**
     * This method is used to make the columns in the table resizable, not able to reorder, and set the width to the
     * percentage of the table.
     *
     * @param table This is the TableView JavaFX element
     * @param column This is the column that needs to be set
     * @param percentage This is the width percentage that the column will take
     */
    public void setupColumnProperties(TableView table, TableColumn column, float percentage) {
        column.setResizable(true); // allow user to resize the table column size if text overflow
        column.setReorderable(false); // disable user ability to reorder the table columns
        column.prefWidthProperty().bind(table.widthProperty().multiply(percentage / 100)); // set the column width to the percentage of the table's width
    }

    /**
     * This method is used to get all the students in the selected classroom based on the classroom's id saved on the
     * database. It encodes the data which includes username, password, classroomId and the command word in JSON format
     * and post it to PHP, where SQL query is done. Username and password is used for authentication purpose to prevent
     * unauthorised people gain access to the database. PHP will return the data obtained from SQL in JSON format and
     * this method will convert it into a Java Object, which allow easy access of the attributes.
     *
     * @param classroomId The id of the classroom, this is the id generated by the SQL database
     */
    public void getStudents(int classroomId) {
        SessionData.getInstance().clearStudents();
        try {
            URL url = new URL(serverDomain + "/App/student.php"); // php url to send JSON
            HttpURLConnection con = (HttpURLConnection) url.openConnection(); // open a connection to the php url
            con.setRequestMethod("POST"); // post the JSON to the php file
            con.setRequestProperty("Content-Type", "application/json"); // tell the server the file format is JSON
            con.setRequestProperty("Accept", "application/json"); // set the keyword for the request to "Accept"
            con.setDoOutput(true);

            UserData u = SessionData.getInstance().getUserData(); // Get user data from Session Data
            ServerStudentData data = new ServerStudentData(); // create an instance of ServerStudentData to post to PHP

            data.username = u.getUsername(); // the username used for login
            data.password = u.getPassword(); // the password used for login
            data.cid = classroomId; // id number for the classroom
            data.cmd = "getList"; // command word post to the PHP file to tell the PHP file what to do

            String jsonInputString = new ObjectMapper().writeValueAsString(data); // write the data as JSON string

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // get the response from PHP file
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) { // repeat until end of JSON
                    response.append(responseLine.trim()); // add line x from JSON to String
                }
                System.out.println(response.toString()); // output the JSON string in terminal
                ObjectMapper mapper = new ObjectMapper(); // create an instance of ObjectMapper to convert String to JSON
                JsonNode actualObj = mapper.readTree(response.toString()); // convert JSON string into JsonNode object

                if (actualObj.get("status").asText().equals("valid")) { // check if the data given for the PHP is valid
                    SessionData.getInstance().setStudents(actualObj.get("students")); // save the student list to current session
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // region Side Menu

    /**
     * This method is used to set the style of the button when the mouse cursor is hovering above in order to make
     * the button darker
     * @param button JavaFX Button class, the button that needs to change color
     */
    private void hoverButton(Button button) {
        button.setStyle("-fx-background-color: #164f30;"); // darkens the button
    }

    /**
     * This method is used to set the style of the button when the mouse cursor is not hovering above in order to make
     * the button transparent
     * @param button JavaFX Button class, the button that needs to be transparent
     */
    private void exitButton(Button button) {
        button.setStyle("-fx-background-color: transparent;"); // set the button to transparent background
    }


    /**
     * This method is assigned to the Main Page button in dashboard.fxml. It is called when the mouse cursor is hovering
     * above the Main Page button
     */
    @FXML
    void btnMainHover() {
        if (screenCode != 0) { // only change style when user is not on the Main Page
            hoverButton(btnMain);
        }
    }

    /**
     * This method is assigned to the Main Page button in dashboard.fxml. It is called when the mouse cursor is exiting
     * the Main Page button
     */
    @FXML
    void btnMainExit() {
        if (screenCode != 0) { // only change style when user is not on the Main Page
            exitButton(btnMain);
        }
    }

    /**
     * This method is assigned to the Classroom button in dashboard.fxml. It is called when the mouse cursor is hovering
     * above the Classroom button
     */
    @FXML
    void btnClassroomHover() {
        if (screenCode != 1 && screenCode != 2) { // only change style when user is not on the Classroom or the Students page
            hoverButton(btnClassroom);
        }

    }

    /**
     * This method is assigned to the Classroom button in dashboard.fxml. It is called when the mouse cursor is exiting
     * the Classroom button
     */
    @FXML
    void btnClassroomExit() {
        if (screenCode != 1 && screenCode != 2) { // only change style when user is not on the Classroom or the Students page
            exitButton(btnClassroom);
        }
    }

    /**
     * This method is assigned to the Assessment button in dashboard.fxml. It is called when the mouse cursor is hovering
     * above the Assessment button
     */
    @FXML
    void btnAssessmentHover() {
        if (screenCode != 4) { // only change style when user is not on the Assessment page
            hoverButton(btnAssessment);
        }

    }

    /**
     * This method is assigned to the Assessment button in dashboard.fxml. It is called when the mouse cursor is exiting
     * the Assessment button
     */
    @FXML
    void btnAssessmentExit() {
        if (screenCode != 4) { // only change style when user is not on the Assessment page
            exitButton(btnAssessment);
        }

    }

    /**
     * This method is assigned to the Settings button in dashboard.fxml. It is called when the mouse cursor is hovering
     * above the Settings button
     */
    @FXML
    void btnSettingsHover() {
        if (screenCode != 3) { // only change style when user is not on the Settings page
            hoverButton(btnSettings);
        }

    }

    /**
     * This method is assigned to the Settings button in dashboard.fxml. It is called when the mouse cursor is exiting
     * the Settings button
     */
    @FXML
    void btnSettingsExit() {
        if (screenCode != 3) { // only change style when user is not on the Assessment page
            exitButton(btnSettings);
        }

    }

    /**
     * This method is assigned to the Logout button in dashboard.fxml. It is called when the mouse cursor is hovering
     * above the Logout button
     */
    @FXML
    void btnLogoutHover() {
        hoverButton(btnLogout);
    }

    /**
     * This method is assigned to the Logout button in dashboard.fxml. It is called when the mouse cursor is exiting
     * the Logout button
     */
    @FXML
    void btnLogoutExit() {
        exitButton(btnLogout);
    }

    /**
     * This method is assigned to the Quit button in dashboard.fxml. It is called when the mouse cursor is hovering
     * above the Quit button
     */
    @FXML
    void btnQuitHover() {
        hoverButton(btnQuit);
    }

    /**
     * This method is assigned to the Quit button in dashboard.fxml. It is called when the mouse cursor is exiting
     * the Quit button
     */
    @FXML
    void btnQuitExit() {
        exitButton(btnQuit);
    }

    /**
     * This method is assigned to the main page button in dashboard.fxml. It is called when the button is clicked
     */
    @FXML
    void btnMainClick() {
        setScreen(0);
        try {
            // set up a connection to php server
            URL url = new URL(serverDomain + "/App/student.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            UserData u = SessionData.getInstance().getUserData(); // get current user details
            ServerStudentData data = new ServerStudentData(); // instantiate a java object to store data posted to PHP

            data.username = u.getUsername(); // the username used for login
            data.password = u.getPassword(); // the password used for login
            data.cmd = "getWatch"; // command word telling PHP what the request is

            String jsonInputString = new ObjectMapper().writeValueAsString(data); // write data as JSON string

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // get the response from PHP server
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) { // repeat until end of JSON
                    response.append(responseLine.trim());
                }
                ObjectMapper mapper = new ObjectMapper();
                JsonNode actualObj = mapper.readTree(response.toString()); // convert JSON string into JsonNode object

                if (actualObj.get("status").asText().equals("valid")) { // check if status returned is valid
                    SessionData.getInstance().setWatchStudents(actualObj.get("watchStudents")); // save watchlist student to current session
                }
            }

        } catch (Exception e) {
            // show no connection window
            e.printStackTrace();
            showNoConnection((Stage) btnMain.getScene().getWindow());
        }

        // pointer to the watchlist students in session data
        ArrayList<StudentData> watchStudents = SessionData.getInstance().getWatchStudents();

        // clear all contents in both table
        tableWell.getColumns().clear();
        tableWell.getItems().clear();
        tableNotWell.getColumns().clear();
        tableNotWell.getItems().clear();

        // region columns set up
        TableColumn<StudentData, String> goodStudentName = new TableColumn<>("Student Name");
        goodStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        setupColumnProperties(tableWell, goodStudentName, 40);

        TableColumn<StudentData, String> badStudentName = new TableColumn<>("Student Name");
        badStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        setupColumnProperties(tableNotWell, badStudentName, 40);

        TableColumn<StudentData, Integer> goodCount = new TableColumn<>("Count");
        goodCount.setCellValueFactory(new PropertyValueFactory<>("goodCount"));
        setupColumnProperties(tableWell, goodCount, 20);

        TableColumn<StudentData, Integer> badCount = new TableColumn<>("Count");
        badCount.setCellValueFactory(new PropertyValueFactory<>("badCount"));
        setupColumnProperties(tableNotWell, badCount, 20);

        TableColumn<StudentData, String> goodClassroomName = new TableColumn<>("Class");
        goodClassroomName.setCellValueFactory(new PropertyValueFactory<>("classroomName"));
        setupColumnProperties(tableWell, goodClassroomName, 40);

        TableColumn<StudentData, String> badClassroomName = new TableColumn<>("Class");
        badClassroomName.setCellValueFactory(new PropertyValueFactory<>("classroomName"));
        setupColumnProperties(tableNotWell, badClassroomName, 40);
        // endregion

        // add columns to the table
        tableWell.getColumns().addAll(goodStudentName, goodCount, goodClassroomName);
        tableNotWell.getColumns().addAll(badStudentName, badCount, badClassroomName);

        for (StudentData watchStudent: watchStudents) {
            if (watchStudent.getGoodCount() >= 3) {
                tableWell.getItems().add(watchStudent);
            } else if (watchStudent.getBadCount() >= 3) {
                tableNotWell.getItems().add(watchStudent);
            }
        }
    }

    /**
     * This method is assigned to the class button in dashboard.fxml. It is called when the button is clicked
     */
    @FXML
    void btnClassroomClick() {
        getClassrooms(); // get the classrooms from the database

        if (SessionData.getInstance().getClassrooms() == null) {
            showNoConnection((Stage) btnClassroom.getScene().getWindow());
        } else {
            initClassrooms(); // initialize the classrooms table, adding columns etc.
            setScreen(2); // show classroom screen
        }
    }

    /**
     * This method initialize the classroom drop-down menu in assessment page
     */
    private void initAssessmentClassroomSelections() {
        int lastSelectedId = choiceAssessmentClassrooms.getSelectionModel().getSelectedIndex();

        choiceAssessmentClassrooms.getItems().clear();
        for (ClassroomData classroom: SessionData.getInstance().getClassrooms()) {
            choiceAssessmentClassrooms.getItems().add(classroom.getClassroomName());
        }

        if (lastSelectedId >= 0 && lastSelectedId < choiceAssessmentClassrooms.getItems().size()) {
            choiceAssessmentClassrooms.getSelectionModel().select(lastSelectedId); // set selection to the selected classroom based on lastSelectedId
        } else if (choiceAssessmentClassrooms.getItems().size() > 0) {
            choiceAssessmentClassrooms.getSelectionModel().select(0); // if lastSelectedId was invalid, default selection to the first classroom
        }

        initAssessments();
    }

    /**
     * This method is assigned to the assessment button in dashboard.fxml. It is called when the button is clicked
     */
    @FXML
    void btnAssessmentClick() {


        if (SessionData.getInstance().getClassrooms() == null) {
            getClassrooms();
        }

        if (SessionData.getInstance().getClassrooms() != null) {
            setScreen(4);
            initAssessmentClassroomSelections();
            initAssessments();
        }

    }

    /**
     * This method is assigned to the settings button in dashboard.fxml. It is called when the button is clicked
     */
    @FXML
    void btnSettingsClick() {
        setScreen(3);
    }

    /**
     * This method is assigned to the logout button in dashboard.fxml. It is called when the button is clicked
     */
    @FXML
    void logout() {
        SessionData.newInstance();

        try {
            UserData u = new UserData();
            ResourceManager.save(u, "login.sav"); // save null user to local file after log out

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method is assigned to the quit button in dashboard.fxml. It is called when the button is clicked
     */
    @FXML
    void quit() {
        Stage stage = (Stage) btnQuit.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    // endregion

    // region Main Page (screen code 0)
    // endregion

    // region Classroom Page (screen code 2)

    /**
     * This method is used to get all the classrooms under the teacher's username classroom saved on the
     * database. It encodes the data which includes username, password, and the command word in JSON format
     * and post it to PHP, where SQL query is done. Username and password is used for authentication purpose to prevent
     * unauthorised people gain access to the database. PHP will return the data obtained from SQL in JSON format to be
     * converted into a Java Object, which allow easy access of the attributes.
     */
    private void getClassrooms() {
        SessionData.getInstance().clearClassrooms();
        try {
            URL url = new URL(serverDomain + "/App/classroom.php"); // the PHP server address

            // set up the connection to the PHP server address
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            UserData u = SessionData.getInstance().getUserData(); // retrieve login data from current session
            ServerClassroomData data = new ServerClassroomData(); // create a temporary java object for JSON conversion

            data.username = u.getUsername();
            data.password = u.getPassword();
            data.cmd = "getList";

            String jsonInputString = new ObjectMapper().writeValueAsString(data); // write object as JSON string

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // get the response from PHP file
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) { //  repeat until end of JSON
                    response.append(responseLine.trim());
                }
                ObjectMapper mapper = new ObjectMapper();
                JsonNode actualObj = mapper.readTree(response.toString()); // convert JSON string to JSON object

                if (actualObj.get("status").asText().equals("valid")) {
                    SessionData.getInstance().setClassrooms(actualObj.get("classrooms")); // save list of classroom in session
                }
            }

        } catch (Exception e) {
            // No internet connection, show require connection window
            e.printStackTrace();
        }
    }


    /**
     * This method sets up the table of classrooms.
     */
    public void initClassrooms() {

        ArrayList<ClassroomData> classrooms = SessionData.getInstance().getClassrooms(); // set classrooms to the list of classrooms in session data

        if (classrooms == null) { // if classrooms are unset
            getClassrooms(); // get classrooms and save it in session

            if (SessionData.getInstance().getClassrooms() == null) {
                showNoConnection((Stage) btnClassroom.getScene().getWindow());
                return;
            }
            classrooms = SessionData.getInstance().getClassrooms(); // reinitialize classrooms
        }

        // Clear all data in table
        tableClassroom.getColumns().clear();
        tableClassroom.getItems().clear();

        TableColumn<ClassroomData, String> classroomName = new TableColumn<>("Classroom Name");
        classroomName.setCellValueFactory(new PropertyValueFactory<>("classroomName"));
        setupColumnProperties(tableClassroom, classroomName, 20);

        TableColumn<ClassroomData, String> classroomStudentsCount = new TableColumn<>("No. students");
        classroomStudentsCount.setCellValueFactory(new PropertyValueFactory<>("sTotal"));
        setupColumnProperties(tableClassroom, classroomStudentsCount, 10);


        TableColumn<ClassroomData, String> classroomNote = new TableColumn<>("Note");
        classroomNote.setCellValueFactory(new PropertyValueFactory<>("classroomNote"));
        setupColumnProperties(tableClassroom, classroomNote, 35);


        TableColumn<StudentData, Float> median = new TableColumn<>("Median");
        median.setCellValueFactory(new PropertyValueFactory<>("median"));
        median.setCellFactory(c-> new TableCell<>() {
            /**
             * This method rounds the median to one decimal place and remove trailing zeroes
             *
             * @param item The new item for the cell.
             * @param empty whether or not this cell represents data from the list. If it
             *        is empty, then it does not represent any domain data, but is a cell
             *        being used to render an "empty" row.
             */
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    DecimalFormat df = new DecimalFormat("0.#");
                    setText(df.format(item));
                }
            }
        });
        setupColumnProperties(tableStudents, median, 7);
//
        TableColumn<StudentData, Float> mean = new TableColumn<>("Mean");
        mean.setCellValueFactory(new PropertyValueFactory<>("mean"));
        mean.setCellFactory(c-> new TableCell<>() {
            /**
             * This method rounds the mean to one decimal place and remove trailing zeroes
             *
             * @param item The new item for the cell.
             * @param empty whether or not this cell represents data from the list. If it
             *        is empty, then it does not represent any domain data, but is a cell
             *        being used to render an "empty" row.
             */
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    DecimalFormat df = new DecimalFormat("0.#");
                    setText(df.format(item));
                }
            }
        });
        setupColumnProperties(tableStudents, mean, 5);
//
        TableColumn<StudentData, Float> lastRecentMean = new TableColumn<>("Last 3 Mean");
        lastRecentMean.setCellValueFactory(new PropertyValueFactory<>("recentMean"));
        lastRecentMean.setCellFactory(c-> new TableCell<>() {
            /**
             * This method rounds the last 3 mean to one decimal place and remove trailing zeroes
             *
             * @param item The new item for the cell.
             * @param empty whether or not this cell represents data from the list. If it
             *        is empty, then it does not represent any domain data, but is a cell
             *        being used to render an "empty" row.
             */
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    DecimalFormat df = new DecimalFormat("0.#");
                    setText(df.format(item));
                }
            }
        });
        setupColumnProperties(tableStudents, lastRecentMean, 9);

        TableColumn<StudentData, Float> lastPercentage = new TableColumn<>("Last Score");
        lastPercentage.setCellValueFactory(new PropertyValueFactory<>("lastPercentage"));
        lastPercentage.setCellFactory(c-> new TableCell<>() {
            /**
             * This method rounds the last assessment score to one decimal place and remove trailing zeroes
             *
             * @param item The new item for the cell.
             * @param empty whether or not this cell represents data from the list. If it
             *        is empty, then it does not represent any domain data, but is a cell
             *        being used to render an "empty" row.
             */
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    DecimalFormat df = new DecimalFormat("0.#");
                    setText(df.format(item));
                }
            }
        });
        setupColumnProperties(tableStudents, lastPercentage, 9);

        tableClassroom.getColumns().addAll(classroomName, classroomStudentsCount, lastPercentage, median, mean, lastRecentMean, classroomNote);

        for (ClassroomData classroom: classrooms) {
            System.out.println("class last score:"+classroom.getLastPercentage());
            tableClassroom.getItems().add(classroom);
        }
    }

    /**
     * This function is called when add new classroom button is clicked.
     * @throws Exception - error if the fxml file is unable to load
     */
    @FXML
    void btnAddClassroomClick() throws Exception {

        final Stage popupAdd = new Stage();
        popupAdd.initModality(Modality.APPLICATION_MODAL);
        popupAdd.initOwner(btnAddClassroom.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dlgAddEditClassroom.fxml"));
        Scene dialogScene = new Scene(loader.load());

        final DlgAddEditClassroomController controller = loader.getController();
        controller.initNewClassroomInd(true, selectedClassroom, this);

        popupAdd.setScene(dialogScene);
        popupAdd.setResizable(false);
        popupAdd.show();
    }

    /**
     * This function is called when edit an existing classroom button is clicked
     * @throws Exception - error if the fxml file is unable to load
     */
    @FXML
    void btnEditClassroomClick() throws Exception {

        selectedClassroom = (ClassroomData) tableClassroom.getSelectionModel().getSelectedItem();

        if (selectedClassroom != null) { // check if the user has selected a classroom to edit

            // popup window for editing classroom
            final Stage popupEdit = new Stage();
            popupEdit.initModality(Modality.APPLICATION_MODAL);
            popupEdit.initOwner(btnEditClassroom.getScene().getWindow());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dlgAddEditClassroom.fxml"));
            Scene dialogScene = new Scene(loader.load());

            final DlgAddEditClassroomController controller = loader.getController();
            controller.initNewClassroomInd(false, selectedClassroom, this);
            popupEdit.setScene(dialogScene);

            // setting properties of the popup window
            popupEdit.setResizable(false);
            popupEdit.show();
        } else {
            // show a warning message telling user to select a classroom before editing
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Please select a classroom to edit");
            alert.show();
        }
    }

    /**
     * This function is called when delete a classroom button is clicked
     */
    @FXML
    void btnDeleteClassroomClick() {
        selectedClassroom = (ClassroomData) tableClassroom.getSelectionModel().getSelectedItem();

        if (selectedClassroom != null) { // check if the user has selected a classroom to delete

            // show a confirmation delete window before executing the request
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Are you sure you want to delete this classroom?");
            alert.setContentText("You are about to delete " + selectedClassroom.getClassroomName() + ". Click OK to proceed");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) { // check if user select okay
                try {
                    // set up the connection to the php server for posting
                    URL url = new URL(serverDomain + "/App/classroom.php");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);

                    UserData u = SessionData.getInstance().getUserData();
                    ServerClassroomData data = new ServerClassroomData(); // create a temporary object to convert to JSON

                    // set username and password to the username and password stored in current session
                    data.username = u.getUsername();
                    data.password = u.getPassword();

                    // send command word with the classroom id to PHP
                    data.cmd = "delete";
                    data.classroomId = selectedClassroom.getId();

                    String jsonInputString = new ObjectMapper().writeValueAsString(data);

                    try (OutputStream os = con.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    try (BufferedReader br = new BufferedReader(
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
                            // save classrooms' data in current session
                            SessionData.getInstance().setClassrooms(actualObj.get("classrooms"));
                        } else {
                            // show an error window with a message
                            Alert errorDelete = new Alert(Alert.AlertType.ERROR);
                            errorDelete.setTitle("Error");
                            errorDelete.setHeaderText(actualObj.get("msg").asText());
                            errorDelete.show();
                        }
                    }

                } catch (Exception e) {
                    // error posting information to the php server
                    showNoConnection((Stage) btnDeleteClassroom.getScene().getWindow());
                    e.printStackTrace();
                }

                initClassrooms(); // update classroom table with new information
            }
        } else {
            // tell user to select a classroom to delete
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Please select a classroom to delete");
            alert.show();
        }
    }

    /**
     * This method is called when view students button in the classroom page is clicked. It will change the screen
     * to the students page and show details of each student in the classroom
     */
    @FXML
    void btnViewStudentsClick() {

        // get the selected classroom from the table
        selectedClassroom = (ClassroomData) tableClassroom.getSelectionModel().getSelectedItem();

        if (selectedClassroom != null) { // check if user has selected classroom

            // reset the drop-down menu on the left hand corner of the student page
            choiceClassrooms.getItems().clear();
            for (ClassroomData classroom: SessionData.getInstance().getClassrooms()) {
                // for each classroom append its classroom to the drop-down menu
                choiceClassrooms.getItems().add(classroom.getClassroomName());
            }

            labelClassroom.setText("Classroom: ");

            // set the selected classroom to the classroom selected in the previous page
            choiceClassrooms.getSelectionModel().select(selectedClassroom.getClassroomName());
            // get all students under the classroom id
            getStudents(selectedClassroom.getId());

            if (SessionData.getInstance().getStudents() == null) { // check if data is fetched from PHP
                showNoConnection((Stage) btnViewStudents.getScene().getWindow()); // no connection window
            } else {
                setScreen(1); // hide other pages and show the student page
                initStudents(); // set up student table
                btnRefreshResClick(); // automatic reload and calculate student's score
            }

        } else {
            // user has not selected a classroom before viewing students, show warning message box
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Please select a classroom to view students");
            alert.show();
        }
    }

    // endregion

    // region Students Page (screen code 1)

    /**
     * This method is used to set up the student table in the students page
     */
    public void initStudents() {
        ArrayList<StudentData> students = SessionData.getInstance().getStudents();

        if (selectedClassroom != null) {
            for (StudentData student:
                    students) {
                // format all student's percentage
                student.setStMean(selectedClassroom.getMean());
                student.setStMedian(selectedClassroom.getMedian());
                student.setStLastPercentage(selectedClassroom.getLastPercentage());
                student.setStRecentMean(selectedClassroom.getRecentMean());
            }
        }

        // clear students table
        tableStudents.getColumns().clear();
        tableStudents.getItems().clear();

        // region set up columns for the students table
        TableColumn<StudentData, String> studentName = new TableColumn<>("Name");
        studentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        setupColumnProperties(tableStudents, studentName, 15);

        TableColumn<StudentData, String> targetGrade = new TableColumn<>("TG");
        targetGrade.setCellValueFactory(new PropertyValueFactory<>("targetGrade"));
        setupColumnProperties(tableStudents, targetGrade, 5);

        TableColumn<StudentData, String> aspirationalGrade = new TableColumn<>("AG");
        aspirationalGrade.setCellValueFactory(new PropertyValueFactory<>("aspirationalGrade"));
        setupColumnProperties(tableStudents, aspirationalGrade, 5);

        TableColumn<StudentData, String> currentGrade = new TableColumn<>("CG");
        currentGrade.setCellValueFactory(new PropertyValueFactory<>("currentGrade"));
        setupColumnProperties(tableStudents, currentGrade, 5);

        // region sub columns for student scores column
        TableColumn<StudentData, String> median = new TableColumn<>("Median");
        median.setCellValueFactory(new PropertyValueFactory<>("stMedian"));
        setupColumnProperties(tableStudents, median, 17);

        TableColumn<StudentData, String> mean = new TableColumn<>("Mean");
        mean.setCellValueFactory(new PropertyValueFactory<>("stMean"));
        setupColumnProperties(tableStudents, mean, 17);

        TableColumn<StudentData, String> lastRecentMean = new TableColumn<>("Last 3 Mean");
        lastRecentMean.setCellValueFactory(new PropertyValueFactory<>("stRecentMean"));
        setupColumnProperties(tableStudents, lastRecentMean, 17);

        TableColumn<StudentData, String> lastPercentage = new TableColumn<>("Last Score");
        lastPercentage.setCellValueFactory(new PropertyValueFactory<>("stLastPercentage"));
        setupColumnProperties(tableStudents, lastPercentage, 17);
        // endregion

        TableColumn studentCol = new TableColumn("Student / Classroom");
        studentCol.setResizable(true);
        studentCol.setReorderable(false);
        // endregion

        studentCol.getColumns().addAll(lastPercentage, median, mean, lastRecentMean); // add sub columns


        tableStudents.getColumns().addAll(studentName, targetGrade, aspirationalGrade, currentGrade, studentCol); // add all columns to the table

        for (StudentData student: students) {
            tableStudents.getItems().add(student); // for each student append it to the table
        }

    }

    /**
     * This method is used to find the last percentage, mean, median, last 3 average of classroom average score
     * @param reports - an arraylist of student reports
     * @param classroom - classroom to calculate
     */
    private void calcClassroomReport(ArrayList<StudentReport> reports, ClassroomData classroom) {
        // initialize integers
        float totalLastScore = 0;
        float totalMedian = 0;
        float totalMean = 0;
        float totalRecentMean = 0;
        int n = reports.size();

        if (n <= 0) {
            return; // don't execute below if there are no reports
        }

        for (StudentReport report: reports) {
            totalLastScore += report.getLastPercentage(); // sum of all students' last scores
            totalMedian += report.getMedian();  // sum of all students' median
            totalMean += report.getMean(); // sum of all students' mean
            totalRecentMean += report.getRecentMean(); // sum of all students' recent mean
        }

        // divide total by number of students to find classroom average
        classroom.setLastPercentage(totalLastScore / n);
        classroom.setMean(totalMean / n);
        classroom.setMedian(totalMedian / n);
        classroom.setRecentMean(totalRecentMean / n);

    }

    /**
     * This method converts JsonNode deserialized from JSON string to Java object
     * @param results - the results returned from PHP
     */
    private void setClassroomResults(JsonNode results) {
        classroomResults = new ArrayList<>(); // instantiate a new arraylist
        for (int i = 0; i < results.size(); i++) {
            // for each result create a new result object and append it to the list
            classroomResults.add(new AssessmentResult(
                    results.get(i).get("sid").asInt(),
                    "",
                    results.get(i).get("aid").asInt(),
                    results.get(i).get("cid").asInt(),
                    (float) results.get(i).get("score").asDouble(),
                    (float) results.get(i).get("percentage").asDouble()
            ));
        }
    }

    /**
     * This method converts JsonNode deserialized from JSON string to Java object
     * @param assessments - the assessments returned from PHP
     */
    private void setClassroomAssessments(JsonNode assessments) {
        classroomAssessments = new ArrayList<>(); // instantiate a new arraylist
        for (int i = 0; i < assessments.size(); i++) {
            // for each assessment create a new assessment object and append it to the list
            classroomAssessments.add(new AssessmentData(
                    assessments.get(i).get("id").asInt(),
                    assessments.get(i).get("name").asText(),
                    assessments.get(i).get("topic").asText(),
                    assessments.get(i).get("category").asText(),
                    assessments.get(i).get("maxScore").asInt(),
                    (float) assessments.get(i).get("highest").asDouble(),
                    (float) assessments.get(i).get("median").asDouble(),
                    (float) assessments.get(i).get("mean").asDouble(),
                    (float) assessments.get(i).get("lowest").asDouble(),
                    assessments.get(i).get("axScore").asInt(),
                    assessments.get(i).get("aScore").asInt(),
                    assessments.get(i).get("bScore").asInt(),
                    assessments.get(i).get("cScore").asInt(),
                    assessments.get(i).get("dScore").asInt(),
                    assessments.get(i).get("eScore").asInt(),
                    assessments.get(i).get("uScore").asInt()
            ));
        }
    }

    /**
     * This method is called when the user clicks on the add student button. It will create a popup window and
     * allows user to input information.
     * @throws Exception - fxml file cannot load
     */
    @FXML
    void btnAddStudentClick() throws Exception {
        final Stage popupAdd = new Stage();
        popupAdd.initModality(Modality.APPLICATION_MODAL);
        popupAdd.initOwner(btnAddStudent.getScene().getWindow()); // set ownership of the popup window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dlgAddEditStudent.fxml"));
        Scene dialogScene = new Scene(loader.load());

        final DlgAddEditStudentController controller = loader.getController();
        controller.initNewStudentInd(true, selectedStudent, selectedClassroom, this); // custom init function

        popupAdd.setScene(dialogScene);
        popupAdd.setResizable(false);
        popupAdd.show();
    }

    /**
     * This method is called when the user clicks on the edit student button. It will create a popup window and
     * allows user to input information
     * @throws Exception - fxml file cannot load
     */
    @FXML
    void btnEditStudentClick() throws Exception {
        selectedStudent = (StudentData) tableStudents.getSelectionModel().getSelectedItem(); // get the selected student from table

        if (selectedStudent != null) {
            final Stage popupEdit = new Stage();
            popupEdit.initModality(Modality.APPLICATION_MODAL);
            popupEdit.initOwner(btnEditStudent.getScene().getWindow()); // set ownership of popup window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dlgAddEditStudent.fxml"));
            Scene dialogScene = new Scene(loader.load());

            final DlgAddEditStudentController controller = loader.getController();
            controller.initNewStudentInd(false, selectedStudent, selectedClassroom, this); // custom init function

            popupEdit.setScene(dialogScene);
            popupEdit.setResizable(false);
            popupEdit.show();
        } else {
            // user didn't select student before editing, show warning message box
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Please select a student to edit");
            alert.show();
        }
    }

    /**
     * This method is called when the delete button in students page is clicked. It will send a request to PHP and
     * delete the student and their related information from the database. The new list of students is then sent
     * back to Java application so that the table can be updated
     */
    @FXML
    void btnDeleteStudentClick() {
        selectedStudent = (StudentData) tableStudents.getSelectionModel().getSelectedItem(); // get selected student for deletion

        if (selectedStudent != null) {
            // show a confirmation message before deleting to prevent mis-click, deletion is permanent and irreversible
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Are you sure you want to delete this student?");
            alert.setContentText("You are about to delete " + selectedStudent.getStudentName() + ". Click OK to proceed");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                // user clicks ok in the message box
                try {
                    // set up connection to php server
                    URL url = new URL(serverDomain + "/App/student.php");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    con.setDoOutput(true);

                    UserData u = SessionData.getInstance().getUserData(); // get current user details
                    ServerStudentData data = new ServerStudentData(); // instantiate a java object to store data posted to PHP

                    data.username = u.getUsername(); // the username used for login
                    data.password = u.getPassword(); // the password used for login
                    data.cmd = "delete"; // the command word telling PHP what the request is
                    data.studentId = selectedStudent.getId(); // send the deleting student's id

                    String jsonInputString = new ObjectMapper().writeValueAsString(data); // write data as JSON string

                    try (OutputStream os = con.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    // get the response from PHP server
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) { // repeat until end of JSON
                            response.append(responseLine.trim());
                        }
                        System.out.println(response.toString());
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode actualObj = mapper.readTree(response.toString()); // convert JSON string into JsonNode object

                        if (actualObj.get("status").asText().equals("valid")) {
                            SessionData.getInstance().setStudents(actualObj.get("students")); // save list of students to current session
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // get all students under selected classroom
                getStudents(selectedClassroom.getId());

                if (SessionData.getInstance().getStudents() == null) {
                    showNoConnection((Stage) btnViewStudents.getScene().getWindow());
                } else {
                    initStudents();
                }

            }
        } else {
            // show a message telling user to select a student to delete
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Please select a student to delete");
            alert.show();
        }
    }

    /**
     * This method is called when the refresh button is called to calculate mean, median, last percentage, recent mean
     * with new data.
     */
    @FXML
    void btnRefreshResClick() {
        ArrayList<StudentReport> reports = new ArrayList<>();
        try {
            // set up a connection to PHP
            URL url = new URL(serverDomain + "/App/assessment.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            UserData u = SessionData.getInstance().getUserData(); // get current user details
            ServerAssessmentData data = new ServerAssessmentData(); // instantiate a java object to store data posted to PHP

            data.username = u.getUsername(); // the username used for login
            data.password = u.getPassword(); // the password used for login
            data.cmd = "getClassroomResult"; // command word telling PHP what the request is
            data.cid = selectedClassroom.getId(); // the classroom id for the selected classroom

            String jsonInputString = new ObjectMapper().writeValueAsString(data); // write data as JSON string

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // get the response from PHP server
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) { // repeat until end of JSON
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
                ObjectMapper mapper = new ObjectMapper();
                JsonNode actualObj = mapper.readTree(response.toString()); // convert JSON string into JsonNode object

                if (actualObj.get("status").asText().equals("valid")) { // check if status returned is valid
                    setClassroomAssessments(actualObj.get("assessments"));
                    setClassroomResults(actualObj.get("results"));

                    ArrayList<StudentData> students = SessionData.getInstance().getStudents(); // get lists of students from session data
                    for (StudentData student:
                            students) {
                        StudentReport r = new StudentReport(student.getId(), classroomResults, classroomAssessments);
                        // save mean, recent mean, last percentage, median, bad count, good count to the corresponding student
                        student.setMean(r.getMean());
                        student.setRecentMean(r.getRecentMean());
                        student.setMedian(r.getMedian());
                        student.setLastPercentage(r.getLastPercentage());
                        student.setBadCount(r.getBadCount());
                        student.setGoodCount(r.getGoodCount());
                        reports.add(r);
                    }

                    calcClassroomReport(reports, selectedClassroom);

                    SessionData.getInstance().setCurrentClassroomReports(reports);

                }
            }

        } catch (Exception e) {
            // show no connection window
            showNoConnection((Stage) btnRefreshRes.getScene().getWindow());
            e.printStackTrace();
        }

        try {

            URL url = new URL(serverDomain + "/App/student.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            UserData u = SessionData.getInstance().getUserData();
            ServerResultsData data = new ServerResultsData();

            data.username = u.getUsername();
            data.password = u.getPassword();
            data.cmd = "update";

            data.mean = selectedClassroom.getMean();
            data.median = selectedClassroom.getMedian();
            data.recentMean = selectedClassroom.getRecentMean();
            data.lastScore = selectedClassroom.getLastPercentage();
            data.reports = reports;
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
                ObjectMapper mapper = new ObjectMapper();
                JsonNode actualObj = mapper.readTree(response.toString());

                if (actualObj.get("status").asText().equals("valid")) {
                    SessionData.getInstance().setStudents(actualObj.get("students"));
                }
            }

        } catch (Exception e) {
            // show no connection message
            showNoConnection((Stage) btnRefreshRes.getScene().getWindow());
            e.printStackTrace();
        }

        initStudents();

    }

    /**
     * This method is called when the view report button is clicked. It will create a new window styled with the
     * fxml file.
     * @throws Exception - fxml file cannot load
     */
    @FXML
    void btnViewReportClick() throws Exception {
        selectedStudent = (StudentData) tableStudents.getSelectionModel().getSelectedItem(); // get the selected student

        if (selectedStudent != null) { // check if a student is selected before creating the window
            // set up the window for the report
            final Stage popupReport = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentReportScreen.fxml"));
            Scene reportScene = new Scene(loader.load());

            final StudentReportScreenController controller = loader.getController();
            controller.init(selectedClassroom.getClassroomName(), selectedStudent.getId()); // parse classroom name and student id to the controller

            popupReport.setScene(reportScene);
            popupReport.setResizable(false);
            popupReport.show();

        } else {
            // show warning to tell user to select a student first
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Please select a student to view his/her report");
            alert.show();
        }
    }

    /**
     * This method is called when the back button in the student page is clicked, it will go back to the classroom
     * page.
     */
    @FXML
    void btnBackClick(){
        getClassrooms(); // get classrooms from server and save it to session

        if (SessionData.getInstance().getClassrooms() == null) {
            // failed to connect to server, show no connection window
            showNoConnection((Stage) btnBack.getScene().getWindow());
        } else {
            // show classroom page
            initClassrooms();
            setScreen(2);
        }
    }

    // endregion

    // region Assessment Page (screen code 4)

    /**
     * This method gets the list of assessments from the PHP server and save it to current session.
     */
    private void getAssessments() {
        try {
            // set up connection to PHP server
            URL url = new URL(serverDomain + "/App/assessment.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            // Setup Java objects for JSON writing later
            UserData u = SessionData.getInstance().getUserData();
            ServerAssessmentData data = new ServerAssessmentData();
            data.username = u.getUsername();
            data.password = u.getPassword();

            if (selectedAssessmentClassroom == null) { // check if classroom is selected in assessment screen
                return;
            }

            data.cid = selectedAssessmentClassroom.getId();
            data.cmd = "getList"; // request for the PHP (refer to cases in PHP)

            String jsonInputString = new ObjectMapper().writeValueAsString(data); // write Java object to JSON

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
                JsonNode actualObj = mapper.readTree(response.toString()); // Convert JSON string to JsonNode object

                if (actualObj.get("status").asText().equals("valid")) { // check if the status is valid
                    SessionData.getInstance().setAssessments(actualObj.get("assessments")); // set assessments based on JsonNode data
                }
            }

        } catch (Exception e) {
            // some error occurs when fetching the assessments from the server
            e.printStackTrace();
        }
    }

    /**
     * This method is called to get the students results for the assessment. This assessment is identified by the unique
     * assessment id it has.
     * @param assessmentId - a unique integer id given by SQL database to each assessment added
     */
    private void getResults(int assessmentId) {
        try {
            // set up connection to PHP server
            URL url = new URL(serverDomain + "/App/assessment.php");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            // set up Java object for JSON conversion later
            UserData u = SessionData.getInstance().getUserData();
            ServerAssessmentData data = new ServerAssessmentData();

            data.username = u.getUsername();
            data.password = u.getPassword();
            data.cmd = "getResults"; // request word (refer to switch case in PHP)
            data.assessmentId = assessmentId;

            String jsonInputString = new ObjectMapper().writeValueAsString(data); // write Java object to JSON string

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                ObjectMapper mapper = new ObjectMapper();
                JsonNode actualObj = mapper.readTree(response.toString()); // convert JSON string to JsonNode object

                if (actualObj.get("status").asText().equals("valid")) {
                    SessionData.getInstance().setStudentResults(actualObj.get("results")); // set students results with data in JsonNode
                }
            }

        } catch (Exception e) {
            // some error occurred when obtaining data from PHP server
            e.printStackTrace();
        }
    }

    /**
     * This method is called to set up the assessments table and fill it with the array list of assessments.
     */
    public void initAssessments() {
        ArrayList<AssessmentData> assessments = SessionData.getInstance().getAssessments(); // get assessments from current session

        if (assessments == null) {
            // session data doesn't have assessments in it, fetching assessments from PHP
            getAssessments();

            // check if the session data has assessment list after the method
            if (SessionData.getInstance().getAssessments() != null) {
                // set assessments to the assessment list stored in current session
                assessments = SessionData.getInstance().getAssessments();
            } else {
                return;
            }
        }

        // clear everything in the table
        tableAssessments.getColumns().clear();
        tableAssessments.getItems().clear();


        // region set up columns for the table
        TableColumn<AssessmentData, String> assessmentName = new TableColumn<>("Assessment Name");
        assessmentName.setCellValueFactory(new PropertyValueFactory<>("assessmentName"));
        setupColumnProperties(tableAssessments, assessmentName, 20);

        TableColumn<AssessmentData, String> assessmentTopic = new TableColumn<>("Topic");
        assessmentTopic.setCellValueFactory(new PropertyValueFactory<>("topic"));
        setupColumnProperties(tableAssessments, assessmentTopic, 5);

        TableColumn<AssessmentData, String> assessmentCategory = new TableColumn<>("Category");
        assessmentCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        setupColumnProperties(tableAssessments, assessmentCategory, 8);

        TableColumn<AssessmentData, String> assessmentMaxScore = new TableColumn<>("Out of");
        assessmentMaxScore.setCellValueFactory(new PropertyValueFactory<>("maxScore"));
        setupColumnProperties(tableAssessments, assessmentMaxScore, 5);

        TableColumn<AssessmentData, Float> assessmentHighScore = new TableColumn<>("Highest");
        assessmentHighScore.setCellValueFactory(new PropertyValueFactory<>("highest"));
        assessmentHighScore.setCellFactory(c-> new TableCell<>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    DecimalFormat df = new DecimalFormat("0.#"); // format float so that it doesn't have trailing zeroes
                    setText(df.format(item));
                }
            }
        });
        setupColumnProperties(tableAssessments, assessmentHighScore, 8);

        TableColumn<AssessmentData, Float> assessmentMedianScore = new TableColumn<>("Median");
        assessmentMedianScore.setCellValueFactory(new PropertyValueFactory<>("median"));
        assessmentMedianScore.setCellFactory(c-> new TableCell<>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    DecimalFormat df = new DecimalFormat("0.#"); // format float so that it doesn't have trailing zeroes
                    setText(df.format(item));
                }
            }
        });
        setupColumnProperties(tableAssessments, assessmentMedianScore, 8);

        TableColumn<AssessmentData, Float> assessmentMeanScore = new TableColumn<>("Mean");
        assessmentMeanScore.setCellValueFactory(new PropertyValueFactory<>("mean"));
        assessmentMeanScore.setCellFactory(c-> new TableCell<>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    DecimalFormat df = new DecimalFormat("0.#"); // format float so that it doesn't have trailing zeroes
                    setText(df.format(item));
                }
            }
        });
        setupColumnProperties(tableAssessments, assessmentMeanScore, 5);

        TableColumn<AssessmentData, Float> assessmentLowScore = new TableColumn<>("Lowest");
        assessmentLowScore.setCellValueFactory(new PropertyValueFactory<>("lowest"));
        assessmentLowScore.setCellFactory(c-> new TableCell<>() {
            @Override
            protected void updateItem(Float item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    DecimalFormat df = new DecimalFormat("0.#"); // format float so that it doesn't have trailing zeroes
                    setText(df.format(item));
                }
            }
        });
        setupColumnProperties(tableAssessments, assessmentLowScore, 8);


        TableColumn<AssessmentData, String> assessmentAxScore = new TableColumn<>("A*");
        assessmentAxScore.setCellValueFactory(new PropertyValueFactory<>("axScore"));
        setupColumnProperties(tableAssessments, assessmentAxScore, 5);

        TableColumn<AssessmentData, String> assessmentAScore = new TableColumn<>("A");
        assessmentAScore.setCellValueFactory(new PropertyValueFactory<>("aScore"));
        setupColumnProperties(tableAssessments, assessmentAScore, 5);

        TableColumn<AssessmentData, String> assessmentBScore = new TableColumn<>("B");
        assessmentBScore.setCellValueFactory(new PropertyValueFactory<>("bScore"));
        setupColumnProperties(tableAssessments, assessmentBScore, 5);

        TableColumn<AssessmentData, String> assessmentCScore = new TableColumn<>("C");
        assessmentCScore.setCellValueFactory(new PropertyValueFactory<>("cScore"));
        setupColumnProperties(tableAssessments, assessmentCScore, 5);

        TableColumn<AssessmentData, String> assessmentDScore = new TableColumn<>("D");
        assessmentDScore.setCellValueFactory(new PropertyValueFactory<>("dScore"));
        setupColumnProperties(tableAssessments, assessmentDScore, 5);

        TableColumn<AssessmentData, String> assessmentEScore = new TableColumn<>("E");
        assessmentEScore.setCellValueFactory(new PropertyValueFactory<>("eScore"));
        setupColumnProperties(tableAssessments, assessmentEScore, 5);
        // endregion

        // add all columns to the table
        tableAssessments.getColumns().addAll(
                assessmentName,
                assessmentTopic,
                assessmentCategory,
                assessmentMaxScore,
                assessmentHighScore,
                assessmentMedianScore,
                assessmentMeanScore,
                assessmentLowScore,
                assessmentAxScore,
                assessmentAScore,
                assessmentBScore,
                assessmentCScore,
                assessmentDScore,
                assessmentEScore
        );

        for (AssessmentData assessment: assessments) {
            // for each assessment in the list, add it to the table
            tableAssessments.getItems().add(assessment);
        }

    }

    /**
     * This method is called when add assessment button is clicked. It will create a window loaded with the fxml
     * @throws Exception - the fxml cannot load
     */
    @FXML
    void btnAddAssessmentClick() throws Exception {
        if (selectedAssessmentClassroom != null) { // check if a classroom is selected

            getStudents(selectedAssessmentClassroom.getId()); // get students under the classroom in the database

            if (SessionData.getInstance().getStudents() == null) { // check if students are stored in the session
                // error fetching students from PHP server, show no connection message
                showNoConnection((Stage) btnAddAssessment.getScene().getWindow());

            } else {

                // create a popup window loaded with the fxml file
                final Stage popupAdd = new Stage();
                popupAdd.initModality(Modality.APPLICATION_MODAL);
                popupAdd.initOwner(btnAddAssessment.getScene().getWindow());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("dlgAddEditAssessment.fxml"));
                Scene dialogScene = new Scene(loader.load());

                final DlgAddEditAssessmentController controller = loader.getController();
                ArrayList<AssessmentResult> assessmentResults = new ArrayList<>(); // initialize an arraylist to store all results
                for (StudentData student: SessionData.getInstance().getStudents()) {
                    assessmentResults.add(new AssessmentResult(student.getId(), student.getStudentName(), 0, selectedAssessmentClassroom.getId(), 0, 0));
                }
                controller.initNewAssessmentInd(true, selectedAssessment, selectedAssessmentClassroom, assessmentResults, this); // init function to enter parameters from its parent window

                // show popup window
                popupAdd.setScene(dialogScene);
                popupAdd.setResizable(false);
                popupAdd.show();
            }

        } else {
            // user has not selected a classroom, show warning window
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Please select a classroom before adding a new assessment");
            alert.show();
        }

    }

    /**
     * This method is called when the edit assessment button is called. This method is similar to the btnAddAssessmentClick()
     * above. It will show a popup window to edit the assessment.
     * @throws Exception - fxml file cannot load
     */
    @FXML
    void btnEditAssessmentClick() throws Exception {
        selectedAssessment = (AssessmentData) tableAssessments.getSelectionModel().getSelectedItem(); // get selected assessment

        if (selectedAssessmentClassroom != null) {
            // execute code below if classroom is selected
            if (selectedAssessment != null) {
                // execute code below if classroom is selected


                getStudents(selectedAssessmentClassroom.getId()); // get all students belonged to the selected classroom
                if (SessionData.getInstance().getStudents() == null) {
                    // error fetching students from PHP server, show no connection window
                    showNoConnection((Stage) btnEditAssessment.getScene().getWindow());
                    return; // stop executing code below
                }

                getResults(selectedAssessment.getId()); // get all students results for the assessment
                if (SessionData.getInstance().getStudentResults() == null) {
                    // error fetching students from PHP server, show no connection window
                    showNoConnection((Stage) btnEditAssessment.getScene().getWindow());
                    return; // stop executing code below
                }

                ArrayList<AssessmentResult> results = SessionData.getInstance().getStudentResults(); // pointer pointing towards the results stored in the session

                for (int i = 0; i < SessionData.getInstance().getStudents().size(); i++) {
                    StudentData student = SessionData.getInstance().getStudents().get(i);

                    boolean oldInd = false; // boolean indicator if student is new to the class (true - old student | false - new student)
                    for (AssessmentResult result : results) {

                        if (result.getSid() == student.getId()) {
                            // not a new student
                            result.setStudentName(student.getStudentName());
                            oldInd = true;
                            break;
                        }
                    }

                    if (!oldInd) {
                        // student is new
                        results.add(new AssessmentResult(SessionData.getInstance().getStudents().get(i).getId(), SessionData.getInstance().getStudents().get(i).getStudentName(), selectedAssessment.getId(), selectedAssessmentClassroom.getId(), 0, 0));
                    }
                }

                // create a popup window loaded with fxml
                final Stage popupEdit = new Stage();
                popupEdit.initModality(Modality.APPLICATION_MODAL);
                popupEdit.initOwner(btnEditAssessment.getScene().getWindow());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("dlgAddEditAssessment.fxml"));
                Scene dialogScene = new Scene(loader.load());

                final DlgAddEditAssessmentController controller = loader.getController();

                controller.initNewAssessmentInd(false, selectedAssessment, selectedAssessmentClassroom, results, this); // call init function in the controller and parse in parameters

                // show the popup window
                popupEdit.setScene(dialogScene);
                popupEdit.setResizable(false);
                popupEdit.show();
            } else {
                // user didn't select an assessment to edit, show warning message
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Please select an assessment to edit");
                alert.show();
            }
        } else {
            // user didn't select a classroom, show warning message
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Please select a classroom before editing an assessment");
            alert.show();
        }
    }

    /**
     * This method is called when the delete assessment button is clicked. It will delete the selected assessment in
     * the table and the database. If no assessment is selected, a warning message will show. Before proceeding to
     * delete the assessment, a confirmation dialog is required to confirm the deletion. Once confirmed by user, post the
     * assessment id to PHP. Multiple records related to the assessment will be deleted (refer to PHP files).
     */
    @FXML
    void btnDeleteAssessmentClick() {
        selectedAssessment = (AssessmentData) tableAssessments.getSelectionModel().getSelectedItem(); // get the selected assessment in the table

        if (selectedAssessmentClassroom != null) {
            if (selectedAssessment != null) {
                // show delete confirmation message
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("Are you sure you want to delete this assessment? All students' results for this assessment will be lost forever");
                alert.setContentText("You are about to delete " + selectedAssessment.getAssessmentName() + ". Click OK to proceed");


                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    // user proceeds
                    try {
                        // set up connection to the PHP server
                        URL url = new URL(serverDomain + "/App/assessment.php");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Content-Type", "application/json");
                        con.setRequestProperty("Accept", "application/json");
                        con.setDoOutput(true);

                        // set up java object for JSON conversion
                        UserData u = SessionData.getInstance().getUserData();
                        ServerAssessmentData data = new ServerAssessmentData();

                        data.username = u.getUsername();
                        data.password = u.getPassword();
                        data.cmd = "delete"; // request word for PHP server (refer to PHP)
                        data.assessmentId = selectedAssessment.getId();

                        String jsonInputString = new ObjectMapper().writeValueAsString(data); // write Java object as JSON string

                        try (OutputStream os = con.getOutputStream()) {
                            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }

                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                            StringBuilder response = new StringBuilder();
                            String responseLine = null;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode actualObj = mapper.readTree(response.toString()); // map JSON string to JsonNode object

                            if (actualObj.get("status").asText().equals("valid")) {
                                SessionData.getInstance().setAssessments(actualObj.get("assessments")); // save assessments as an arraylist of Java objects using data from JsonNode
                            }
                        }

                    } catch (Exception e) {
                        showNoConnection((Stage) btnDeleteAssessment.getScene().getWindow());
                        e.printStackTrace();
                    }
                    getAssessments(); // get assessments from PHP

                    if (SessionData.getInstance().getAssessments() == null) {
                        // error occurred posting data to PHP, show no connection window
                        showNoConnection((Stage) btnDeleteAssessment.getScene().getWindow());
                    } else {
                        initAssessments(); // update assessments table
                    }
                }
            } else {
                // user didn't select an assessment to delete, show warning message
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Please select an assessment to delete");
                alert.show();
            }
        } else {
            // user didn't select a classroom, show warning message
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Please select a classroom before deleting an assessment");
            alert.show();
        }
    }

    // endregion

    // region Settings Page (screen code 3)

    /**
     * This method is called when the add new user button is clicked. It creates a new window loaded with fxml, allowing
     * user to input the details of the new user.
     * @throws Exception - error occurred when loading the fxml file
     */
    @FXML
    void btnAddNewUserClick() throws Exception{
        final Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(btnAddNewUser.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dlgAddNewUser.fxml"));
        Scene scene = new Scene(loader.load());

        final DlgAddNewUserController controller = loader.getController();
        controller.initVars(this);
        popup.setScene(scene);
        popup.setResizable(false);
        popup.show();
    }

    /**
     * This method is called when the change password button is clicked. It creates a new window loaded with fxml, allowing
     * user to change their password
     * @throws Exception - error occurred when loading the fxml
     */
    @FXML
    void btnChangePasswordClick() throws Exception {
        final Stage popup  = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner(btnChangePassword.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dlgChangePassword.fxml"));
        Scene scene = new Scene(loader.load());

        final DlgChangePasswordController controller = loader.getController();
        controller.initVar(this);

        popup.setScene(scene);
        popup.setResizable(false);
        popup.show();
    }

    /**
     * This method is called when upload image button is clicked. It opens the file explorer allowing the user to upload
     * an image as their profile photo.
     */
    @FXML
    void btnUploadImgClick() {
        FileChooser fileChooser = new FileChooser(); // open the file explorer so that user can select a file
        fileChooser.setTitle("Open Resource File");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG file (*.jpg)", "*.jpg")); // only accept image in jpg form
        File file = fileChooser.showOpenDialog(btnUploadImg.getScene().getWindow());
        if (file != null) {
            try {
                uploadImageToServer(file.getAbsolutePath());
                ivUser.setImage(new Image("http://wy.kukupgoldenbay.com/App/imgstaff.php?id=" + SessionData.getInstance().getUserData().getId()));
            } catch (Exception e) {
                // error occurred when communicating with the PHP server, show no connection window
                showNoConnection((Stage) btnUploadImg.getScene().getWindow());
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is called to upload an image on the user's computer to the server.
     * @param fileLocation - the file path of the selected image
     * @throws IOException - failed input/output exceptions when inputting
     */
    public void uploadImageToServer(String fileLocation) throws IOException {

        // converting Strings to the application/x-www-form-urlencoded MIME format
        String username = URLEncoder.encode(SessionData.getInstance().getUserData().getUsername(), StandardCharsets.UTF_8);
        String password = URLEncoder.encode(SessionData.getInstance().getUserData().getPassword(), StandardCharsets.UTF_8);

        // set up connection to the php server
        HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL("http://wy.kukupgoldenbay.com/App/upload.php?id=" +
                SessionData.getInstance().getUserData().getId() + "&u=" + username + "&p=" + password).openConnection();
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setRequestMethod("POST");
        OutputStream os = httpUrlConnection.getOutputStream();
        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fileLocation));

        long totalByte = fis.available();
        long byteTrasferred = 0;
        for (int i = 0; i < totalByte; i++) {
            os.write(fis.read());
            byteTrasferred = i + 1;
        }

        os.close();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        httpUrlConnection.getInputStream()));

        String s = null;
        while ((s = in.readLine()) != null) {
            System.out.println(s);
        }
        in.close();
        fis.close();

    }


    // endregion

    /**
     * Initialize function from the Initializable interface. This function is called to initialize the controller
     * @param location - location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources - resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // set up the text in the label
        labelClassroom.setText("");
        labelID.setText("User ID: " + SessionData.getInstance().getUserData().getUsername());

        // go to main page
        btnMainClick();

        // clear all items in the assessment table
        tableAssessments.getColumns().clear();
        tableAssessments.getItems().clear();

        // set the profile picture in the top left corner to the user's profile picture
        String imgSource = "http://wy.kukupgoldenbay.com/App/imgstaff.php?id=" + SessionData.getInstance().getUserData().getId();
        ivUser.setImage(new Image(imgSource));

        // add listener to choice box to get the selected classroom in students page
        choiceClassrooms.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            for (ClassroomData classroom: SessionData.getInstance().getClassrooms()) {
                if (newValue != null && newValue.equals(classroom.getClassroomName())) {
                    selectedClassroom = classroom;
                    break;
                }
            }
            getStudents(selectedClassroom.getId()); // get students in the selected classroom

            if (SessionData.getInstance().getStudents() == null) {
                // error retrieving data from PHP, show no connection message
                showNoConnection((Stage) choiceClassrooms.getScene().getWindow());
            } else {
                initStudents();
                btnRefreshResClick();
            }
        });

        // add listener to choice box to get the selected classroom in the assessment page
        choiceAssessmentClassrooms.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            for (ClassroomData classroom: SessionData.getInstance().getClassrooms()) {
                if (newValue != null && newValue.equals(classroom.getClassroomName())) {
                    selectedAssessmentClassroom = classroom;
                    break;
                }
            }
            getAssessments(); // get assessments created for the classroom

            if (SessionData.getInstance().getAssessments() == null) {
                // error retrieving data from PHP, show no connection message
                showNoConnection((Stage) choiceAssessmentClassrooms.getScene().getWindow());
            } else {
                initAssessments();
            }
        });

        tableAssessments.setRowFactory(tv -> {
            TableRow<ClassroomData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    // user double-click on a row, show edit student's score window
                    try {
                        btnEditAssessmentClick();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });


        tableClassroom.setRowFactory( tv -> {
            TableRow<ClassroomData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    // user double-click on a row, show students page
                    try {
                        btnViewStudentsClick();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            return row ;
        });

        tableStudents.setRowFactory( tv -> {
            TableRow<StudentData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    // user double-click on a row, create new window showing student's report
                    try {
                        btnViewReportClick();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            return row ;
        });

    }
}
