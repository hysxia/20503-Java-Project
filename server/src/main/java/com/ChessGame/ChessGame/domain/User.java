package com.ChessGame.ChessGame.domain;

import jakarta.persistence.*;

import java.util.*;
@Entity
public class User {

    @Id
    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String email;
    private int wins;
    @Column(name = "losses")  
    private int losses;

    @Column(name = "draws")
    private int draws;;

    public User() {}

    public User(String username,String email, String password){
        this.username=username;
        this.email=email;
        this.password=password;
        this.wins=0;
        this.losses=0;
        this.draws=0;
    }

    public void addWin(){
        wins++;
    }
    public void addLosses(){
        losses++;
    }
    public void addDraws(){
        draws++;
    }
    public int getWins() {
        return wins;
    }
    public int getLosses() {
        return losses;
    }
    public int getDraws() {
        return draws;
    }
    @Override
    public String toString(){
        return this.username;
    }
    public String getUsername(){
        return username;
    }
    public String getEmail(){
        return email;
    }
    public String getPassword() {
        return password;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public void setPassword(String password){
        this.password=password;
    }
}
