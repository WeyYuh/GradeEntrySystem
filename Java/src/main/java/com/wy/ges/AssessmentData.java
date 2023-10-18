package com.wy.ges;

/**
 * This class is instantiated when the user either makes changes to an existing assessment or editing an existing
 * assessment
 */
public class AssessmentData {

    private final int id; // assessment id

    // region User Input Data
    private final String assessmentName;

    private final String topic; // assessment topic

    private final String category; // assessment category

    private final int maxScore; // out of score for the assessment

    private final int axScore; // minimum percentage score to get A*

    private final int aScore; // minimum percentage score to get A

    private final int bScore; // minimum percentage score to get B

    private final int cScore; // minimum percentage score to get C

    private final int dScore; // minimum percentage score to get D

    private final int eScore; // minimum percentage score to get E

    private final int uScore; // minimum percentage score to get U
    // endregion

    // region Calculated Data
    private float highest; // highest percentage score for the assessment

    private float median; // median percentage score for the assessment

    private float mean; // mean percentage score for the assessment

    private float lowest; // lowest percentage score for the assessment
    // endregion

    // region Getters and Setters - These are the getters and setters for AssessmentData object

    public int getId() {
        return id;
    }

    public String getAssessmentName() {
        return assessmentName;
    }

    public String getTopic() {
        return topic;
    }

    public String getCategory() {
        return category;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public int getAxScore() {
        return axScore;
    }

    public int getAScore() {
        return aScore;
    }

    public int getBScore() {
        return bScore;
    }

    public int getCScore() {
        return cScore;
    }

    public int getDScore() {
        return dScore;
    }

    public int getEScore() {
        return eScore;
    }

    public int getUScore() {
        return uScore;
    }

    public float getHighest() {
        return highest;
    }

    public void setHighest(float highest) {
        this.highest = highest;
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

    public float getLowest() {
        return lowest;
    }

    public void setLowest(float lowest) {
        this.lowest = lowest;
    }
    // endregion

    //region Constructors

    /**
     * This constructor is used to update an existing assessment with new details
     * @param id - the assessment id in the database
     * @param assessmentName - the name of the assessment
     * @param topic - the topic of the assessment
     * @param category - the category of the assessment
     * @param maxScore - value of "out of"
     * @param highest - the highest score for the assessment
     * @param median - the median score for the assessment
     * @param mean - the average score for the assessment
     * @param lowest - the lowest score for the assessment
     * @param axScore - the minimum score to get A*
     * @param aScore - the minimum score to get A
     * @param bScore - the minimum score to get B
     * @param cScore - the minimum score to get C
     * @param dScore - the minimum score to get D
     * @param eScore - the minimum score to get E
     * @param uScore - the minimum score to get U
     */
    public AssessmentData(int id, String assessmentName, String topic, String category, int maxScore, float highest,
                          float median, float mean, float lowest, int axScore, int aScore, int bScore, int cScore,
                          int dScore, int eScore, int uScore) {
        // Use this Constructor if editing an assessment
        this.id = id;
        this.assessmentName = assessmentName;
        this.topic = topic;
        this.category = category;
        this.maxScore = maxScore;
        this.highest = highest;
        this.median = median;
        this.mean = mean;
        this.lowest = lowest;
        this.axScore = axScore;
        this.aScore = aScore;
        this.bScore = bScore;
        this.cScore = cScore;
        this.dScore = dScore;
        this.eScore = eScore;
        this.uScore = uScore;
    }

    /**
     * This constructor is used when user is adding a new assessment
     */
    public AssessmentData() {
        // Use this Constructor if adding a new assessment
        this.id = 0; // assessment id will be assigned by the database
        this.assessmentName = "";
        this.topic = "";
        this.category = "";
        this.maxScore = 0;
        this.axScore = 0;
        this.aScore = 0;
        this.bScore = 0;
        this.cScore = 0;
        this.dScore = 0;
        this.eScore = 0;
        this.uScore = 0;
    }
    // endregion
}
