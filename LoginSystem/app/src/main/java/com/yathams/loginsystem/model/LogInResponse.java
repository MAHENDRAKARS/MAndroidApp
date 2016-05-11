package com.yathams.loginsystem.model;

/**
 * Created by vyatham on 14/03/16.
 */
public class LogInResponse extends Response {
    public User user = null;
    public static class User{
        public String userID = "";
        public String userEmail = "";
        public String AnonymousID = "";
    }
}

