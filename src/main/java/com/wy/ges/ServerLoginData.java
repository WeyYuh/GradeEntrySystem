package com.wy.ges;

/**
 * This is the object class for login data that will be sent to server. It is the superclass of all other server object
 * classes since it has username and password that will be required to make changes to the database. Every attribute is
 * public since this is a temporary class that will only be instantiated when posting JSON to server
 */
public class ServerLoginData {
    public String username; // application login username
    public String password; // application login password
}
