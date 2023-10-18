package com.wy.ges;

// region Imported libraries
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
// endregion

/**
 * This is the controller class of the window for changing or adding a new classroom
 */
public class DlgAddEditClassroomController {


    // region FXML elements
    @FXML
    private TextField inputClassroomName;

    @FXML
    private TextField inputClassroomNote;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    private Label labelErrorMsg;
    // endregion

    private boolean fNewClassroom; // indicator for user creating new classroom (true - add | false - edit)
    private ClassroomData selectedClassroom;
    private DashboardController parentController;


    /**
     * This method is called in the parent controller to act as an initializing method, allowing it to separate between
     * adding and editing classrooms.
     * @param fInd - boolean for new classroom (true = add new classroom | false = edit classroom)
     * @param classroom - selected classroom
     * @param p - parent controller (dashboard controller)
     */
    public void initNewClassroomInd(boolean fInd, ClassroomData classroom, DashboardController p) {
        labelErrorMsg.setVisible(false);
        fNewClassroom = fInd;
        selectedClassroom = classroom;
        if (fNewClassroom) {
            inputClassroomName.setText("");
            inputClassroomNote.setText("");
        } else {
            // fill the text field with current data
            inputClassroomName.setText(selectedClassroom.getClassroomName());
            inputClassroomNote.setText(selectedClassroom.getClassroomNote());
        }

        parentController = p;
    }

    /**
     * This method is called when the cancel button is clicked. It will close the current window
     */
    @FXML
    void btnCancelClick() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * This method is called when the save button is clicked. It will post data from the text field to php server so
     * that the table in the database is updated.
     */
    @FXML
    void btnSaveClick() {
        // get user input from text field
        String classroomName = inputClassroomName.getText();
        String classroomNote = inputClassroomNote.getText();

        if (classroomName.isBlank()) { // check if classroom is blank
            labelErrorMsg.setVisible(true); // show the error message
        } else {
            labelErrorMsg.setVisible(false); // hide error message

            try {
                // set up connection to php server
                URL url = new URL("http://wy.kukupgoldenbay.com/App/classroom.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                UserData u = SessionData.getInstance().getUserData();
                ServerClassroomData data = new ServerClassroomData(); // create a temporary Java object for JSON conversion

                data.username = u.getUsername();
                data.password = u.getPassword();

                if (fNewClassroom) { // adding new classroom
                    data.cmd = "add";
                    data.classroomId = 0;
                } else { // editing an existing classroom
                    data.cmd = "edit";
                    data.classroomId = selectedClassroom.getId();
                }

                data.classroomName = classroomName;
                data.classroomNote = classroomNote;

                String jsonInputString = new ObjectMapper().writeValueAsString(data);

                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode actualObj = mapper.readTree(response.toString());

                    if (actualObj.get("status").asText().equals("valid")) {
                        SessionData.getInstance().setClassrooms(actualObj.get("classrooms")); // save the classroom data return from PHP to current session
                    }
                }

            } catch (Exception e) {
                // show no connection window
                parentController.showNoConnection((Stage) btnSave.getScene().getWindow());
                e.printStackTrace();
            }

            parentController.initClassrooms();
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();
        }
    }
}
