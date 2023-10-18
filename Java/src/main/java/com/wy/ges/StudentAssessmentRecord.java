package com.wy.ges;

// region imported libs
import java.text.DecimalFormat;
import java.util.ArrayList;
// endregion

/**
 * This is the object class for the record of the student's score for an assessment
 */
public class StudentAssessmentRecord {
    private String assessmentName; // name of the assessment

    private float score; // percentage
    private String stScore; // string value of the percentage
    private String grade; // grade letter

    private float mean; // mean percentage
    private String stMean; // string value of the mean percentage

    private float recentMean; // recent mean percentage
    private String stRecentMean; // string value of the recent mean percentage

    // region getters and setters

    /**
     * This method is used to get the string of the percentage score
     * @return percentage score string
     */
    public String getStScore() {
        return stScore;
    }

    /**
     * This method is used to set the string of the percentage score
     * @param stScore - string of the percentage score
     */
    public void setStScore(String stScore) {
        this.stScore = stScore;
    }

    /**
     * This method is used to get the string of the percentage mean
     * @return string of the percentage mean
     */
    public String getStMean() {
        return stMean;
    }

    /**
     * This method is used to set the string of the percentage mean
     * @param stMean - string of the percentage mean
     */
    public void setStMean(String stMean) {
        this.stMean = stMean;
    }

    /**
     * This method is used to get the string of the recent mean
     * @return string of the recent mean
     */
    public String getStRecentMean() {
        return stRecentMean;
    }

    /**
     * This method is used to set the string of the recent mean
     * @param stRecentMean - string of the recent mean
     */
    public void setStRecentMean(String stRecentMean) {
        this.stRecentMean = stRecentMean;
    }

    /**
     * This method is used to get the float value of the percentage score
     * @return float percentage score
     */
    public float getScore() {
        return score;
    }

    /**
     * This method is used to set the float value of the percentage score
     * @param score float value of the percentage score
     */
    public void setScore(float score) {
        this.score = score;
    }

    /**
     * This method is used to get the grade letter
     * @return grade letter
     */
    public String getGrade() {
        return grade;
    }

    /**
     * This method is used to set the grade letter
     * @param grade - grade letter
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * This method is used to get the float mean percentage
     * @return float mean percentage
     */
    public float getMean() {
        return mean;
    }

    /**
     * This method is used to set the float mean percentage
     * @param mean - float mean percentage
     */
    public void setMean(float mean) {
        this.mean = mean;
    }

    /**
     * This method is used to get the float recent mean
     * @return float recent mean
     */
    public float getRecentMean() {
        return recentMean;
    }

    /**
     * This method is used to set the float recent mean
     * @param recentMean - float recent mean
     */
    public void setRecentMean(float recentMean) {
        this. recentMean = recentMean;
    }
    // endregion

    /**
     * This constructor is used to instantiate a new object representing the record
     * @param assessment - object for the assessment
     * @param result - result for the assessment
     */
    public StudentAssessmentRecord(AssessmentData assessment, AssessmentResult result) {

        assessmentName = assessment.getAssessmentName();
        score = result.getPercentage();

        if (result.getPercentage() >= assessment.getAxScore()) {
            grade = "A*";
        } else if (result.getPercentage() >= assessment.getAScore()) {
            grade = "A";
        } else if (result.getPercentage() >= assessment.getBScore()) {
            grade = "B";
        } else if (result.getPercentage() >= assessment.getCScore()) {
            grade = "C";
        } else if (result.getPercentage() >= assessment.getDScore()) {
            grade = "D";
        } else if (result.getPercentage() >= assessment.getEScore()) {
            grade = "E";
        } else if (result.getPercentage() >= assessment.getUScore()) {
            grade = "U";
        } else {
            grade = "U";
        }

        // format the percentage score
        DecimalFormat formatter = new DecimalFormat("0.#");
        stScore = formatter.format(score) + "%";

    }

    /**
     * This method is used to get the assessment name
     * @return assessment name
     */
    public String getAssessmentName() {
        return assessmentName;
    }

    /**
     * This method is used to set the assessment name
     * @param assessmentName - name of the assessment
     */
    public void setAssessmentName(String assessmentName) {
        this.assessmentName = assessmentName;
    }
}
