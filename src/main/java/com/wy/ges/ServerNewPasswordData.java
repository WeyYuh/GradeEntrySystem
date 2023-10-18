package com.wy.ges;

/**
 * This is the object class for new password data that will be sent to server. It will inherit from ServerLoginData to
 * have the username and password attribute. Every attribute is public since this is a temporary class that will only be
 * instantiated when posting JSON to server
 */
public class ServerNewPasswordData extends ServerLoginData{
    public String newPassword;
}
