package com.wy.ges;

/**
 * This class is instantiated to represent a classroom
 */
public class ClassroomData {

    private final int id; // classroom id

    // region User Input Data
    private final String classroomName; // classroom name

    private final String classroomNote; // classroom note
    // endregion

    // region Calculated Data
    private float lastPercentage; // latest classroom average percentage score

    private float median; // median for classroom average percentage scores

    private float mean; // mean for classroom average percentage scores

    private float recentMean; // last 3 assessment mean for classroom average percentage scores

    private final int sTotal; // number of students in a classroom
    // endregion

    private final int tid; // teacher id (login id)

    // region Getters and Setters - These are the getters and setters for ClassroomData
    public int getId() {
        return id;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public String getClassroomNote() {
        return classroomNote;
    }

    public float getLastPercentage() {
        return lastPercentage;
    }

    public void setLastPercentage(float lastPercentage) {
        this.lastPercentage = lastPercentage;
    }

    public float getMedian() {
        return median;
    }

    public void setMedian(float median) {
        this.median = median;
    }

    public float getMean() {
        return mean;
    }

    public void setMean(float mean) {
        this.mean = mean;
    }

    public float getRecentMean() {
        return recentMean;
    }

    public void setRecentMean(float recentMean) {
        this.recentMean = recentMean;
    }

    public int getSTotal() {
        return sTotal;
    }

    public int getTid() {
        return tid;
    }
    // endregion

    // region Constructor

    /**
     * This is the constructor to create a new ClassroomData object with parameters
     * @param id - the classroom id on the database
     * @param classroomName - the name of the classroom
     * @param classroomNote - an optional note for the classroom
     * @param lastPercentage - the classroom average percentage for the most recent assessment
     * @param mean - the mean of classroom average percentage from all assessments
     * @param median - the median classroom average percentage for all assessments
     * @param recentMean - the mean of classroom average percentage for the recent 3 assessments
     * @param sTotal - the total number of students in the classroom
     * @param tid - the teacher id that creates the classroom (user id)
     */
    public ClassroomData(int id, String classroomName, String classroomNote, float lastPercentage, float mean, float median, float recentMean, int sTotal, int tid) {
        this.id = id;
        this.classroomName = classroomName;
        this.classroomNote = classroomNote;
        this.lastPercentage = lastPercentage;
        this.mean = mean;
        this.median = median;
        this.recentMean = recentMean;
        this.sTotal = sTotal;
        this.tid = tid;
    }
    // endregion
}
