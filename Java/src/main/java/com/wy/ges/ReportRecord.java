package com.wy.ges;

/**
 * This is the object for the record in the report screen
 */
public class ReportRecord {
    private float percentage;
    private float recentMean;
    private float mean;
    private float median;

    // region getters and setters
    public float getMean() {
        return mean;
    }

    public void setMean(float mean) {
        this.mean = mean;
    }

    public float getMedian() {
        return median;
    }

    public void setMedian(float median) {
        this.median = median;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public float getRecentMean() {
        return recentMean;
    }

    public void setRecentMean(float recentMean) {
        this.recentMean = recentMean;
    }
    // endregion

    /**
     * This is the constructor for the ReportRecord class. It sets the percentage attribute to the parameter parsed in
     * @param percentage - the percentage for an assessment
     */
    public ReportRecord(float percentage) {
        this.percentage = percentage;
    }
}
