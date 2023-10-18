package com.wy.ges;

// region Imported lib
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
// endregion

/**
 * This is the controller class of the window for adding or editing student details
 */
public class DlgAddEditStudentController {

    // region FXML elements
    @FXML
    private TextField inputStudentName;

    @FXML
    private TextField inputTargetGrade;

    @FXML
    private TextField inputAspirationalGrade;

    @FXML
    private TextField inputCurrentGrade;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    private Label labelErrorMsg;

    @FXML
    private ImageView ivStudent;

    @FXML
    private Button btnUploadImg;
    // endregion

    private boolean fNewStudent; // indicator if user adds new student (true - add operation | false - edit operation)
    private StudentData selectedStudent;
    private ClassroomData selectedClassroom;
    private DashboardController parentController;

    /**
     * This method is called to upload an image for the student
     */
    @FXML
    private void btnUploadImgClick() {
        FileChooser fileChooser = new FileChooser(); // open file explorer window to select a file to upload
        fileChooser.setTitle("Open Resource File");

        // only accept image in JPG format
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPEG file (*.jpg)", "*.jpg"));
        File file = fileChooser.showOpenDialog(btnUploadImg.getScene().getWindow());
        if (file != null) {
            try {
                uploadImageToServer(file.getAbsolutePath());
                ivStudent.setImage(new Image("http://wy.kukupgoldenbay.com/App/imgstudent.php?id=" + selectedStudent.getId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is called when the upload image button is clicked
     * @param fileLocation - the image's file path
     * @throws IOException - failed/interrupted I/O exception
     */
    public void uploadImageToServer(String fileLocation) throws IOException {

        // Translates username and password into application/x-www-form-urlencoded format using standard charsets.
        String username = URLEncoder.encode(SessionData.getInstance().getUserData().getUsername(), StandardCharsets.UTF_8);
        String password = URLEncoder.encode(SessionData.getInstance().getUserData().getPassword(), StandardCharsets.UTF_8);

        // connect to the php script posting the username and password so that php can use get[''] method
        HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL("http://wy.kukupgoldenbay.com/App/uploadStudent.php?id=" + selectedStudent.getId() + "&u=" + username + "&p=" + password).openConnection();
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

    /**
     * This method is call to initialize the stage based on whether the user is creating a new student or existing
     * student. This method is called in the previous stage so that initialization can happen
     * @param fInd - indicator for new student (true - new student | false - edit existing student)
     * @param student - the object representing the student
     * @param classroom - the object representing the classroom
     * @param p - the parent controller class
     */
    public void initNewStudentInd(boolean fInd, StudentData student, ClassroomData classroom, DashboardController p) {
        labelErrorMsg.setVisible(false);
        // set the variables to the parameters parsed in
        fNewStudent = fInd;
        selectedStudent = student;
        selectedClassroom = classroom;

        // define imageUrl as a string
        String imageUrl;

        if (fNewStudent) {
            // all inputs are empty
            inputStudentName.setText("");
            inputTargetGrade.setText("");
            inputAspirationalGrade.setText("");
            inputCurrentGrade.setText("");

            // user not allow to upload img because student id is not define yet, show default image
            btnUploadImg.setVisible(false);
            imageUrl = "http://wy.kukupgoldenbay.com/App/imgstudent.php?id=0";
        } else {
            // fill inputs with the student's attributes
            inputStudentName.setText(selectedStudent.getStudentName());
            inputTargetGrade.setText(selectedStudent.getTargetGrade());
            inputAspirationalGrade.setText(selectedStudent.getAspirationalGrade());
            inputCurrentGrade.setText(selectedStudent.getCurrentGrade());

            // user allow to upload img because student id is given by database
            btnUploadImg.setVisible(true);
            // get the image for the student based on their id
            imageUrl = "http://wy.kukupgoldenbay.com/App/imgstudent.php?id=" + selectedStudent.getId();

        }

        ivStudent.setImage(new Image(imageUrl));

        parentController = p;
    }

    /**
     * This method is called when the cancel button is called. It will close the add edit student controller.
     */
    @FXML
    void btnCancelClick() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * This method is called to save the new data to the database. It communicates with the PHP so that inserting or
     * updating SQL database can happen. If save successfully, the data will be added to the PHP. Java will then
     * update the table with the new information. If an error occurs, application will show an error connection window
     */
    @FXML
    void btnSaveClick() {
        String studentName = inputStudentName.getText();

        if (studentName.isBlank()) {
            labelErrorMsg.setVisible(true);
        } else {
            labelErrorMsg.setVisible(false);
            System.out.println("posting to php");

            try {

                // set up a connection to PHP server for posting JSON string
                URL url = new URL("http://wy.kukupgoldenbay.com/App/student.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                UserData u = SessionData.getInstance().getUserData();

                // create a temporary server data to be converted into JSON string
                ServerStudentData data = new ServerStudentData();

                data.username = u.getUsername();
                data.password = u.getPassword();

                if (fNewStudent) {
                    // user adding new student
                    data.cmd = "add";
                    data.studentId = 0;
                } else {
                    // user editing existing user
                    data.cmd = "edit";
                    data.studentId = selectedStudent.getId();
                }

                // set the public attributes to the input
                data.studentName = studentName;
                data.targetGrade = inputTargetGrade.getText();
                data.aspirationalGrade = inputAspirationalGrade.getText();
                data.currentGrade = inputCurrentGrade.getText();
                data.cid = selectedClassroom.getId();

                String jsonInputString = new ObjectMapper().writeValueAsString(data); // write the object as JSON

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

                    if (actualObj.get("status").asText().equals("valid")) {
                        SessionData.getInstance().setStudents(actualObj.get("students")); // save the list of students to current session
                    }
                }

            } catch (Exception e) {
                // error occurred, show no connection window
                parentController.showNoConnection((Stage) btnSave.getScene().getWindow());
                e.printStackTrace();
            }

            parentController.initStudents(); // update the students table

            // close the add / edit student window
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();
        }
    }
}
