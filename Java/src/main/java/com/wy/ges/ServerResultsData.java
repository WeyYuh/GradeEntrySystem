package com.wy.ges;

import java.util.ArrayList;

/**
 * This is the object class for classroom and students' results data that will be sent to server. It will inherit from
 * ServerLoginData to have the username and password attribute. Every attribute is public since this is a temporary class that will only be
 * instantiated when posting JSON to server
 */
public class ServerResultsData extends ServerLoginData {
    public String cmd; // request term (refer to PHP switch case)
    public float mean; // classroom mean percentage
    public float median; // classroom median percentage
    public float recentMean; // classroom recent mean percentage
    public float lastScore; // classroom last percentage
    public int cid; // classroom id given by SQL database

    public ArrayList<StudentReport> reports; // the list of individual student stats
}
