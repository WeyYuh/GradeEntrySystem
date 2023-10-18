package com.wy.ges;

/**
 * This is the object class for classroom data that will be sent to server. It will inherit from ServerLoginData to have
 * the username and password attribute. Every attribute is public since this is a temporary class that will only be
 * instantiated when posting JSON to server
 */
public class ServerClassroomData extends ServerLoginData{
    public String cmd; // request term (refer to PHP switch case)
    public int classroomId; // the classroom id given by the database
    public String classroomName; // the name of the classroom
    public String classroomNote; // the optional note for the classroom
}
