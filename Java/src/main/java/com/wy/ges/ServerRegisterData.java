package com.wy.ges;

/**
 * This is the object class for new user data that will be sent to server. It will inherit from ServerLoginData to have
 * the username and password attribute. Every attribute is public since this is a temporary class that will only be
 * instantiated when posting JSON to server
 */
public class ServerRegisterData extends ServerLoginData {
    public String newUserName; // username for the new user
    public String newUserPassword; // password for the new user
    public String newUserEmail; // email for the new user
}
