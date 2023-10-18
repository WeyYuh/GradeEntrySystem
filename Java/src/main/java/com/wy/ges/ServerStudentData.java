package com.wy.ges;

/**
 * This is the object class for student data that will be sent to server. It will inherit from ServerLoginData to have
 * the username and password attribute. Every attribute is public since this is a temporary class that will only be
 * instantiated when posting JSON to server
 */
public class ServerStudentData extends ServerLoginData {
    public String cmd; // request term (refer to PHP)

    public int studentId; // student id given by SQL database
    public String studentName; // name of student
    public String targetGrade; // student's target grade
    public String aspirationalGrade; // student's aspirational grade
    public String currentGrade; // student's current grade

    public int cid; // classroom id given by SQL database
}
