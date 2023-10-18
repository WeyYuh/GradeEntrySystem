package com.wy.ges;

import java.io.Serial;
import java.io.Serializable;

/**
 * This is the object class for representing a user (teacher).
 */
public class UserData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id; // user id given by database
    private String username; // login username
    private String password; // login password
    private String email; // email used for registration

    /**
     * This method is used to get the user id
     * @return user id
     */
    public int getId() {
        return id;
    }

    /**
     * This method is used to set the user id
     * @param id - user id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * This method is used to get the login username
     * @return login username
     */
    public String getUsername() {
        return username;
    }

    /**
     * This method is used to set the login username
     * @param username - login username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * This method is used to get the login password
     * @return login password
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method is used to set the login password
     * @param password - login password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * This method is used to get the registered email
     * @return the registered email
     */
    public String getEmail() {
        return email;
    }

    /**
     * This method is used to set the registered email
     * @param email -
     *              the registered email
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
