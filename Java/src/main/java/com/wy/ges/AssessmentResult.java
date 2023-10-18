package com.wy.ges;

/**
 * This class is instantiated to store student's score for a specific assessment
 */
public class AssessmentResult {

    private final int sid; // student id

    private String studentName; // student name

    private final int aid; // assessment id

    private final int cid; // class id

    private float score; // student score

    private float percentage; // student percentage score

    //region Getters and Setters - These are the getters and setters for AssessmentResult object
    public int getSid() {
        return sid;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getAid() {
        return aid;
    }

    public int getCid() {
        return cid;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }
    //endregion

    // region Constructor

    /**
     * This is the constructor for the class AssessmentResult
     * @param sid - student id on the database
     * @param studentName - the student's name
     * @param aid - assessment id on the database
     * @param cid - classroom id on the database
     * @param score - the score student achieved for the assessment
     * @param percentage - the percentage student achieved for the assessment
     */
    public AssessmentResult(int sid, String studentName, int aid, int cid, float score, float percentage) {
        this.sid = sid;
        this.studentName = studentName;
        this.aid = aid;
        this.cid = cid;
        this.score = score;
        this.percentage = percentage;
    }
    // endregion
}
