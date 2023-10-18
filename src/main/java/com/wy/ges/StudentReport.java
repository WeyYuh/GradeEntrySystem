package com.wy.ges;

// region imported libs
import java.util.ArrayList;
import java.util.Collections;
// endregion

/**
 * This is the object class that stores all the necessary information about the student to show up in the report screen
 */
public class StudentReport {
    private ArrayList<ReportRecord> reportRecords; // list of records
    private int studentId; // id of the student
    private ArrayList<AssessmentResult> results; // list of scores for the assessment
    private ArrayList<AssessmentData> assessments; // list of assessments student did

    private float mean; // mean percentage
    private float median; // median percentage
    private float recentMean; // recent mean percentage
    private int badCount = 0; // times below recent mean percentage in a row
    private int goodCount = 0; // times above recent mean percentage in a row
    private float lastPercentage; // percentage score on the latest assessment

    private int totalAssessment; // total assessments student did

    /**
     * This method is used to get the percentage score on the latest assessment
     * @return percentage score on the latest assessment
     */
    public float getLastPercentage() {
        return lastPercentage;
    }

    /**
     * This method is used to get the times above recent mean percentage in a row
     * @return times above recent mean percentage in a row
     */
    public int getGoodCount() {
        return goodCount;
    }

    /**
     * This method is used to set the times above recent mean percentage in a row
     * @param goodCount - times above recent mean percentage in a row
     */
    public void setGoodCount(int goodCount) {
        this.goodCount = goodCount;
    }

    /**
     * This method is used to get the times below recent mean percentage in a row
     * @return times below recent mean percentage in a row
     */
    public int getBadCount() {
        return badCount;
    }

    /**
     * This method is used to set the times below recent mean percentage in a row
     * @param badCount - times below recent mean percentage in a row
     */
    public void setBadCount(int badCount) {
        this.badCount = badCount;
    }

    /**
     * This method is used to set the percentage score on the latest assessment
     * @param lastPercentage - percentage score on the latest assessment
     */
    public void setLastPercentage(float lastPercentage) {
        this.lastPercentage = lastPercentage;
    }

    /**
     * This method is used to get the recent mean percentage
     * @return recent mean percentage
     */
    public float getRecentMean() {
        return recentMean;
    }

    /**
     * This method is used to set the recent mean percentage
     * @param recentMean - recent mean percentage
     */
    public void setRecentMean(float recentMean) {
        this.recentMean = recentMean;
    }

    /**
     * This method is used to get the mean percentage
     * @return mean percentage
     */
    public float getMean() {
        return mean;
    }

    /**
     * This method is used to set the mean percentage
     * @param mean - mean percentage
     */
    public void setMean(float mean) {
        this.mean = mean;
    }

    /**
     * This method is used to get the median percentage
     * @return median percentage
     */
    public float getMedian() {
        return median;
    }

    /**
     * This method is used to set the median percentage
     * @param median - median percentage
     */
    public void setMedian(float median) {
        this.median = median;
    }

    /**
     * This method is used to get the total assessments student did
     * @return total assessments student did
     */
    public int getTotalAssessment() {
        return totalAssessment;
    }

    /**
     * This method is used to get the total assessments student did
     * @param totalAssessment - total assessments student did
     */
    public void setTotalAssessment(int totalAssessment) {
        this.totalAssessment = totalAssessment;
    }

    /**
     * This method is used to get the assessment object by its id
     * @param aid - assessment id
     * @return assessment object
     */
    public AssessmentData getAssessmentById(int aid) {
        if (assessments != null) {
            for (AssessmentData assessment: assessments) {
                if (assessment.getId() == aid) {
                    return assessment;
                }
            }
        }

        return null;
    }

    /**
     * This method is used to get the list of records in the report
     * @return list of records in the report
     */
    public ArrayList<ReportRecord> getReportRecords() {
        return reportRecords;
    }

    /**
     * This method is used to set the list of records in the report
     * @param reportRecords - list of records in the report
     */
    public void setReportRecords(ArrayList<ReportRecord> reportRecords) {
        this.reportRecords = reportRecords;
    }

    /**
     * This method is used to get the id of the student
     * @return id of the student
     */
    public int getStudentId() {
        return studentId;
    }

    /**
     * This method is used to set the id of the student
     * @param studentId - id of the student
     */
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    /**
     * This method is used to get the list of results
     * @return list of results
     */
    public ArrayList<AssessmentResult> getResults() {
        return results;
    }

    /**
     * This method is used to set the list of results
     * @param results - list of results
     */
    public void setResults(ArrayList<AssessmentResult> results) {
        this.results = results;
    }

    /**
     * This method is used to get the list of assessments
     * @return list of assessments
     */
    public ArrayList<AssessmentData> getAssessments() {
        return assessments;
    }

    /**
     * This method is used to set the list of assessments
     * @param assessments - list of assessments
     */
    public void setAssessments(ArrayList<AssessmentData> assessments) {
        this.assessments = assessments;
    }

    /**
     * This constructor is used to instantiate an object for one report. It also calculates the mean, median, and recent mean
     * @param studentId - id of the student
     * @param results - the list of results
     * @param assessments - the list of assessments
     */
    public StudentReport(int studentId, ArrayList<AssessmentResult> results, ArrayList<AssessmentData> assessments) {
        reportRecords = new ArrayList<>(); // create a new list
        // set attributes to the parameters parsed
        this.studentId = studentId;
        this.results = results;
        this.assessments = assessments;

        ArrayList<Float> sortedScores = new ArrayList<>(); // list of percentage scores

        for (AssessmentResult result:
             results) {
            // for each result in the results...
            if (result.getSid() == this.studentId && result.getPercentage() > 0) {
                // student id matched and it has a percentage greater than 0

                reportRecords.add(new ReportRecord(result.getPercentage())); // add the report record object to the list
                sortedScores.add(result.getPercentage()); // add the percentage to the list of averages
                mean += result.getPercentage(); // add percentage to mean
            }
        }

        totalAssessment = reportRecords.size(); // get the total number of assessments student did that has a percentage greater than 0

        if (totalAssessment != 0) { // cannot divide something by 0
            mean = mean / totalAssessment;
        }

        for (int i = 0; i < totalAssessment; i++) {
            // for every percentage scores in the list of assessments excluding zero scores...

            if (i == 0) { // the recent mean for the first assessment is just the percentage
                recentMean = reportRecords.get(i).getPercentage();

            } else if (i == 1) { // the recent mean for two assessments is the total of the score divided by 2
                recentMean = (reportRecords.get(i).getPercentage() + reportRecords.get(i - 1).getPercentage()) / 2;

            } else {
                // recent mean is calculated by summing up three latest percentage score and dividing it by 3
                recentMean = (reportRecords.get(i).getPercentage() + reportRecords.get(i - 1).getPercentage() + reportRecords.get(i - 2).getPercentage()) / 3;


                if (reportRecords.get(i).getPercentage() <= recentMean) {
                    // if score is less than recent mean, times below recent mean in a row + 1
                    badCount = badCount + 1;
                } else {
                    // score is greater than recent mean, times below recent mean is reset
                    badCount = 0;
                }

                if (reportRecords.get(i).getPercentage() >= recentMean) {
                    // if score is greater than recent mean, times above recent mean in a row + 1
                    goodCount = goodCount + 1;
                } else {
                    // score is less than recent mean, times below recent mean is reset
                    goodCount = 0;
                }

            }
            reportRecords.get(i).setRecentMean(recentMean);
        }

        if (totalAssessment > 0) {
            // latest percentage score is the percentage score in the last index
            lastPercentage = reportRecords.get(totalAssessment - 1).getPercentage();
        } else {
            // every assessment has no score, setting the latest percentage score to 0
            lastPercentage = 0;
        }


        Collections.sort(sortedScores); // sort the list of percentage scores

        if (totalAssessment == 0) {
            // no median if there are no assessments
            median = 0;
        } else {
            if (totalAssessment % 2 == 0) {
                // number of assessments taken is even
                int firstIndex = (totalAssessment / 2) - 1;
                median = (sortedScores.get(firstIndex) + sortedScores.get(firstIndex + 1)) / 2; // add the two elements in the middle and divide it by 2
            } else {
                // number of assessments taken is odd
                int firstIndex = ((totalAssessment + 1) / 2) - 1;
                median = sortedScores.get(firstIndex); // the element in the middle is the median
            }
        }



    }
}
