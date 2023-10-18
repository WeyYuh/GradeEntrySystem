package com.wy.ges;

import java.text.DecimalFormat;

/**
 * This is the object class to represent each student.
 */
public class StudentData {

    private int id; // student id no. given by SQL database

    private final String studentName; // name of the student

    private String targetGrade; // target grade
    private String aspirationalGrade; // aspirational grade
    private String currentGrade; // current grade

    // Classroom data not saved to server
    private String stMean;
    private String stMedian;
    private String stLastPercentage;
    private String stRecentMean;

    private String classroomName; // name of the classroom student belongs to

    private int tid; // teacher id of the teacher created the student (user id)

    private int cid; // classroom id the student belongs to

    private float median; // median percentage for all assessments
    private float mean; // mean percentage for all assessments
    private float recentMean; // recent mean percentage for all assessments
    private int badCount; // number of times results is below recent mean percentage in a row
    private int goodCount; // number of times results is above recent mean percentage in a row

    private float lastPercentage; // percentage score on the latest assessment
    private int totalTest; // total assessment taken

    private final DecimalFormat formatter = new DecimalFormat("0.#"); // formatter to prevent trailing zeroes

    /**
     * This method is used to convert percentage parameter to respective grade letter
     * @param percentage - percentage float
     * @return grade letter
     */
    public String percentageToGrade(float percentage) {
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
        } else if (percentage >= 30) {
            return "U";
        } else {
            return "U";
        }
    }

    /**
     * This method is used to get the classroom name student belongs to
     * @return classroom name
     */
    public String getClassroomName() {
        return classroomName;
    }

    /**
     * This method is used to set the classroom name student belongs to
     * @param classroomName - name of the classroom
     */
    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    /**
     * This method is used to get the good count of the student
     * @return times score greater than recent mean in a row
     */
    public int getGoodCount() {
        return goodCount;
    }

    /**
     * This method is used to set the good count of the student
     * @param goodCount - times score greater than recent mean in a row
     */
    public void setGoodCount(int goodCount) {
        this.goodCount = goodCount;
    }

    /**
     * This method is used to get the bad count of the student
     * @return times score lesser than recent mean in a row
     */
    public int getBadCount() {
        return badCount;
    }

    /**
     * This method is used to set the bad count of the student
     * @param badCount - times score lesser than recent mean in a row
     */
    public void setBadCount(int badCount) {
        this.badCount = badCount;
    }

    /**
     * This method is used to get the string value of the mean percentage
     * @return string value of the mean percentage
     */
    public String getStMean() {
        return stMean;
    }

    /**
     * This method is used to set the string value of the mean percentage
     * @param crMean - classroom mean percentage
     */
    public void setStMean(float crMean) {
        this.stMean = formatter.format(mean) + "% (" + percentageToGrade(mean) + ") / " + formatter.format(crMean) + "% (" + percentageToGrade(crMean) + ")";
    }

    /**
     * This method is used to get the string value of the median percentage
     * @return string value of the median percentage
     */
    public String getStMedian() {
        return stMedian;
    }

    /**
     * This method is used to set the string value of the median percentage
     * @param crMedian - classroom median percentage
     */
    public void setStMedian(float crMedian) {
        this.stMedian = formatter.format(median) + "% (" + percentageToGrade(median) + ") / " + formatter.format(crMedian) + "% (" + percentageToGrade(crMedian) + ")";
    }

    /**
     * This method is used to get the string value of the last percentage
     * @return string value of the last percentage
     */
    public String getStLastPercentage() {
        return stLastPercentage;
    }

    /**
     * This method is used to set the string value of the last percentage
     * @param crLastPercentage - classroom last percentage
     */
    public void setStLastPercentage(float crLastPercentage) {
        this.stLastPercentage = formatter.format(lastPercentage) + "% (" + percentageToGrade(lastPercentage) + ") / " + formatter.format(crLastPercentage) + "% (" + percentageToGrade(crLastPercentage) + ")";
    }

    /**
     * This method is used to get the string value of the recent mean percentage
     * @return string value of the recent mean percentage
     */
    public String getStRecentMean() {
        return stRecentMean;
    }

    /**
     * This method is used to set the string value of the recent mean percentage
     * @param crRecentMean - classroom recent mean percentage
     */
    public void setStRecentMean(float crRecentMean) {
        this.stRecentMean = formatter.format(recentMean) + "% (" + percentageToGrade(recentMean) + ") / " + formatter.format(crRecentMean) + "% (" + percentageToGrade(crRecentMean) + ")";
    }

    /**
     * This method is used to get the float value of the last percentage
     * @return float value of the last percentage
     */
    public float getLastPercentage() {
        return lastPercentage;
    }

    /**
     * This method is used to set the float value of the last percentage
     * @param lastPercentage - float value of the last percentage
     */
    public void setLastPercentage(float lastPercentage) {
        this.lastPercentage = lastPercentage;
    }

    /**
     * This method is used to get the float value of the recent mean percentage
     * @return float value of the recent mean percentage
     */
    public float getRecentMean() {
        return recentMean;
    }

    /**
     * This method is used to set the float value of the recent mean percentage
     * @param recentMean - float value of the recent mean percentage
     */
    public void setRecentMean(float recentMean) {
        this.recentMean = recentMean;
    }

    /**
     * This method is used to get the total number of tests student enrolled
     * @return total number of tests student enrolled
     */
    public int getTotalTest() {
        return totalTest;
    }

    /**
     * This method is used to set the total number of tests student enrolled
     * @param totalTest - total number of tests student enrolled
     */
    public void setTotalTest(int totalTest) {
        this.totalTest = totalTest;
    }

    /**
     * This method is used to get the target grade of the student
     * @return target grade of the student
     */
    public String getTargetGrade() {
        return targetGrade;
    }

    /**
     * This method is used to set the target grade of the student
     * @param targetGrade - target grade of the student
     */
    public void setTargetGrade(String targetGrade) {
        this.targetGrade = targetGrade;
    }

    /**
     * This method is used to get the aspirational grade of the student
     * @return aspirational grade of the student
     */
    public String getAspirationalGrade() {
        return aspirationalGrade;
    }

    /**
     * This method is used to set the aspirational grade of the student
     * @param aspirationalGrade - aspirational grade of the student
     */
    public void setAspirationalGrade(String aspirationalGrade) {
        this.aspirationalGrade = aspirationalGrade;
    }

    /**
     * This method is used to get the current grade of the student
     * @return current grade of the student
     */
    public String getCurrentGrade() {
        return currentGrade;
    }

    /**
     * This method is used to set the current grade of the student
     * @param currentGrade - current grade of the student
     */
    public void setCurrentGrade(String currentGrade) {
        this.currentGrade = currentGrade;
    }

    /**
     * This method is used to get the student id
     * @return student id
     */
    public int getId() {
        return id;
    }

    /**
     * This method is used to get the teacher id (user id)
     * @return teacher id (user id)
     */
    public int getTid() {
        return tid;
    }

    /**
     * This method is used to get the name of the student
     * @return name of the student
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * This method is used to get the classroom id
     * @return classroom id
     */
    public int getCid() {
        return cid;
    }

    /**
     * This method is used to get the median percentage score
     * @return median percentage score
     */
    public float getMedian() {
        return median;
    }

    /**
     * This method is used to set the median percentage score
     * @param median - median percentage score
     */
    public void setMedian(float median) {
        this.median = median;
    }

    /**
     * This method is used to get the mean percentage score
     * @return mean percentage score
     */
    public float getMean() {
        return mean;
    }

    /**
     * This method is used to set the mean percentage score
     * @param mean - mean percentage score
     */
    public void setMean(float mean) {
        this.mean = mean;
    }

    /**
     * This constructor is used to instantiate a student object with name, bad count, good count and classroom name only
     * @param studentName - name of the student
     * @param badCount - times results worse than recent mean percentage in a row
     * @param goodCount - times results better than recent mean percentage in a row
     * @param classroomName - name of the classroom
     */
    public StudentData(String studentName, int badCount, int goodCount, String classroomName) {
        // set attributes to the parameters
        this.studentName = studentName;
        this.badCount = badCount;
        this.goodCount = goodCount;
        this.classroomName = classroomName;
    }

    /**
     * This constructor is used to instantiate a student object with all necessary details
     * @param id - id of student
     * @param studentName - name of student
     * @param targetGrade - target grade of student
     * @param aspirationalGrade - aspirational grade of student
     * @param currentGrade - current grade of student
     * @param median - median percentage of student
     * @param mean - mean percentage of student
     * @param recentMean - recent mean percentage of student
     * @param lastPercentage - last percentage of student
     * @param tid - teacher id teaching the student
     * @param cid - classroom id student belongs to
     * @param badCount - times results worse than recent mean percentage in a row
     * @param goodCount - times results better than recent mean percentage in a row
     */
    public StudentData(int id, String studentName, String targetGrade, String aspirationalGrade, String currentGrade, float median, float mean, float recentMean,
                       float lastPercentage, int tid, int cid, int badCount, int goodCount) {
        // set attributes to the parameter
        this.id = id;
        this.studentName = studentName;
        this.targetGrade = targetGrade;
        this.aspirationalGrade = aspirationalGrade;
        this.currentGrade = currentGrade;
        this.mean = mean;
        this.median = median;
        this.recentMean = recentMean;
        this.lastPercentage = lastPercentage;
        this.tid = tid;
        this.cid = cid;
        this.badCount = badCount;
        this.goodCount = goodCount;
    }
}
