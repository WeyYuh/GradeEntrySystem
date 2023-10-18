package com.wy.ges;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * This is the controller class for the student report screen containing the performance of that student.
 */
public class StudentReportScreenController {

    // region FXML elements
    @FXML
    private CategoryAxis barX;

    @FXML
    private NumberAxis barY;

    @FXML
    private NumberAxis lineY;

    @FXML
    private CategoryAxis lineX;

    @FXML
    private ImageView ivStudentImg;

    @FXML
    private Label labelStudentName;

    @FXML
    private Label labelClassroomName;

    @FXML
    private Label labelGrades;

    @FXML
    private Label labelLastScore;

    @FXML
    private Label labelMedian;

    @FXML
    private Label labelMean;

    @FXML
    private Label labelRecentMean;

    @FXML
    private TableView tableResults;

    @FXML
    private BarChart barchartAverage;

    @FXML
    private LineChart<?, ?> linechartMainScores;

    @FXML
    private Button btnClose;

    @FXML
    private StackPane stackPane;
    // endregion

    private DecimalFormat format = new DecimalFormat("0.#");

    /**
     * This method is called when the escape key is pressed.
     */
    @FXML
    void btnCloseClick() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    /**
     * This method is used to convert float values to string
     * @param value - float values
     * @return string of the float values
     */
    private String floatToString(float value) {
        DecimalFormat formatter = new DecimalFormat("0.#"); // prevent trailing zeroes
        return String.valueOf(formatter.format(value));
    }

    /**
     * This method is called to initialize the report screen and parsed in variables from the parent controller
     * @param classroomName - name of the classroom
     * @param sid - the id of the student
     */
    public void init(String classroomName, int sid) {

        StudentData student = SessionData.getInstance().getStudentById(sid); // get the student object by id

        String imageUrl = "http://wy.kukupgoldenbay.com/App/imgstudent.php?id=" + sid; // link to the student's profile photo
        ivStudentImg.setImage(new Image(imageUrl)); // set the image in the image view

        // texts inside the labels
        labelStudentName.setText(student.getStudentName());
        labelClassroomName.setText(classroomName);
        labelGrades.setText(student.getTargetGrade() + " / " + student.getAspirationalGrade() + " / " + student.getCurrentGrade());
        labelLastScore.setText(floatToString(student.getLastPercentage()) + "%");
        labelMedian.setText(floatToString(student.getMedian()) + "%");
        labelMean.setText(floatToString(student.getMean()) + "%");
        labelRecentMean.setText(floatToString(student.getRecentMean()) + "%");

        initTableResults(sid);

    }

    /**
     * This method is used to set the width of a column in a table as a percentage of the table's width
     * @param table - table
     * @param column - column of the table
     * @param percentage - width percentage of the whole table's width
     */
    public void setupColumnProperties(TableView table, TableColumn column, float percentage) {
        column.setResizable(true); // allow user to resize columns if overflow
        column.setReorderable(false); // user cannot reorder the columns around
        column.prefWidthProperty().bind(table.widthProperty().multiply(percentage / 100)); // set width to percentage width of whole table
    }

    /**
     * This method is used to convert percentage to the default grade letter
     * @param percentage - percentage score
     * @return grade letter
     */
    private String defaultPercentageToGrade(float percentage) {
        if (percentage >= 90) {
            return "A*";
        } else if (percentage >= 80) {
            return "A";
        } else if (percentage >= 70) {
            return "B";
        } else if (percentage >= 60) {
            return "C";
        } else if (percentage >= 50) {
            return "D";
        } else if (percentage >= 40) {
            return "E";
        } else {
            return "U";
        }
    }

    /**
     * This method is used to initialize the table containing the results of the student
     * @param sid - student's id no.
     */
    public void initTableResults(int sid) {
        ArrayList<StudentAssessmentRecord> records = new ArrayList<>(); // create a new list containing the records

        StudentReport report = SessionData.getInstance().getStudentReportById(sid); // get student report object from session data
        if (report == null) {
            // there is no report, return
            return;
        }


        for (AssessmentResult result:
                report.getResults()) {
            // for each result in the list of results in the student report object...

            AssessmentData a = report.getAssessmentById(result.getAid()); // get assessment object by the assessment id
            if (a != null && result.getSid() == sid && result.getPercentage() != 0) {
                // assessment object is not null, student id matched and percentage is not 0:
                StudentAssessmentRecord ar = new StudentAssessmentRecord(a, result); // create a new object using that assessment and result

                records.add(ar); // add this record to the list of records

                float totalScore = 0;
                for (int i = 0; i < records.size(); i++) {
                    totalScore += records.get(i).getScore();
                }

                float mean = totalScore / records.size(); // find the mean

                ar.setMean(mean); // set the mean in the assessment record object
                ar.setStMean(format.format(mean) + "% (" + defaultPercentageToGrade(mean) + ")");

                for (int i = 0; i < records.size(); i++) {
                    if (i == 0) {
                        // recent mean is the percentage score if there is only one assessment taken
                        ar.setRecentMean(records.get(i).getScore());
                    } else if (i == 1) {
                        // recent mean is the sum of the two percentage score divided by 2 if there are only two assessments taken
                        ar.setRecentMean((records.get(i).getScore() + records.get(i - 1).getScore()) / 2);
                    } else {
                        // recent mean is the sum of the last three percentage score divided by 3 if there are more than or equal to 3 assessments taken
                        ar.setRecentMean((records.get(i).getScore() + records.get(i - 1).getScore() + records.get(i-2).getScore()) / 3);
                    }
                }

                ar.setStRecentMean(format.format(ar.getRecentMean()) + "% (" + defaultPercentageToGrade(ar.getRecentMean()) + ")"); // set the string value of the recent mean


            }
        }

        // clear everything in the table
        tableResults.getColumns().clear();
        tableResults.getItems().clear();

        // region adding columns to the table and formatting them
        TableColumn assessmentName = new TableColumn("Assessment Name");
        assessmentName.setCellValueFactory(new PropertyValueFactory<>("assessmentName"));
        setupColumnProperties(tableResults, assessmentName, 30);

        TableColumn score = new TableColumn("Score");
        score.setCellValueFactory(new PropertyValueFactory<>("stScore"));
        setupColumnProperties(tableResults, score, 17);

        TableColumn grade = new TableColumn("Grade");
        grade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        setupColumnProperties(tableResults, grade, 10);

        TableColumn mean = new TableColumn("Mean");
        mean.setCellValueFactory(new PropertyValueFactory<>("stMean"));
        setupColumnProperties(tableResults, mean, 18);

        TableColumn recentMean = new TableColumn("Recent Mean");
        recentMean.setCellValueFactory(new PropertyValueFactory<>("stRecentMean"));
        setupColumnProperties(tableResults, recentMean, 20);

        tableResults.getColumns().addAll(assessmentName, score, grade, mean, recentMean);
        // endregion

        // plotting performance in line and bar chart
        XYChart.Series average = new XYChart.Series();
        average.setName("Average Score");

        XYChart.Series individual = new XYChart.Series();
        individual.setName("Individual Score");

        XYChart.Series recentAverage = new XYChart.Series();
        recentAverage.setName("Recent Average");

        for (StudentAssessmentRecord record:
             records) {
            // add data to respective items
            tableResults.getItems().add(record);
            average.getData().add(new XYChart.Data<>(record.getAssessmentName(), record.getMean()));
            individual.getData().add(new XYChart.Data<>(record.getAssessmentName(), record.getScore()));
            recentAverage.getData().add(new XYChart.Data<>(record.getAssessmentName(), record.getRecentMean()));
        }
        barchartAverage.getData().add(average);
        linechartMainScores.getData().addAll(individual, recentAverage);


        // set up bar chart properties
        barchartAverage.setLegendVisible(true);
        barchartAverage.setAnimated(true);

        // set up line chart properties
        linechartMainScores.setLegendVisible(true);
        linechartMainScores.setAnimated(true);
        linechartMainScores.setCreateSymbols(true);
        linechartMainScores.setAlternativeRowFillVisible(false);
        linechartMainScores.setAlternativeColumnFillVisible(false);
        linechartMainScores.setHorizontalGridLinesVisible(false);
        linechartMainScores.setVerticalGridLinesVisible(false);
        linechartMainScores.getXAxis().setVisible(false);
        linechartMainScores.getYAxis().setVisible(false);

        // style line chart with css file
        linechartMainScores.getStylesheets().addAll(getClass().getResource("style.css").toExternalForm());



    }

}
