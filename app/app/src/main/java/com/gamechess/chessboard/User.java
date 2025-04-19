package com.gamechess.chessboard;

//class the represent a User in the app
public class User {
    private String username;
    private String email;
    //get username of the user
    public String getUsername() {
        return username;
    }
    //get the email of the user
    public String getEmail() {
        return email;
    }
    //set the username of the user
    public void setUsername(String username) {
        this.username = username;
    }
}