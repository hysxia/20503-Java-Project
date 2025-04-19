package com.gamechess.chessboard;

public class UserSession {
    private static User loggedInUser;
    //set the current logged user
    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }
    //getting the logged user
    public static User getLoggedInUser() {
        return loggedInUser;
    }
    //getting the usernmae of the logged user
    public static String getUsername() {
        return loggedInUser != null ? loggedInUser.getUsername() : null;
    }
    //clear the current user session
    public static void clear() {
        loggedInUser = null;
    }
}