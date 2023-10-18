package com.wy.ges;

import java.util.ArrayList;

/**
 * This is the object class for assessment data that will be sent to server. It will inherit from ServerLoginData to have
 * the username and password attribute. Every attribute is public since this is a temporary class that will only be
 * instantiated when posting JSON to server
 */
public class ServerAssessmentData extends ServerLoginData {

    public String cmd; // request term (refer to PHP switch case)
    public int assessmentId; // the assessment id given by SQL database
    public String assessmentName; // the name of the assessment
    public String topic; // the topic of the assessment
    public String category; // the category of the assessment
    public int maxScore; // the total score for the assessment

    public float highest; // the highest score for the assessment
    public float median; // the median for the assessment
    public float mean; // the mean score for the assessment
    public float lowest; // the lowest score for the assessment

    public int axScore; // minimum score required to get A*
    public int aScore; // minimum score required to get A
    public int bScore; // minimum score required to get B
    public int cScore; // minimum score required to get C
    public int dScore; // minimum score required to get D
    public int eScore; // minimum score required to get E
    public int uScore; // minimum score required to get U

    public int cid; // classroom id given by SQL database
    public ArrayList<AssessmentResult> results; // the list of results for the assessment
}
