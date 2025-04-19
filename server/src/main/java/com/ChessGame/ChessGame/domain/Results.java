package com.ChessGame.ChessGame.domain;

import jakarta.persistence.*;

import java.util.*;

@Entity
public class Results {
    @Id
    private long gameNumber;
    @ManyToOne
    private User whitePlayer;
    @ManyToOne
    private User blackPlayer;
    private String result;
    public Results() {
    }
    public Results(User W1,User B2,String result){
        this.whitePlayer=W1;
        this.blackPlayer=B2;
        this.result=result;
    }

    //Getters and Setters
    public void setGameNumber(long gameNumber) {
        this.gameNumber = gameNumber;
    }
    public long getGameNumber() {
        return gameNumber;
    }
    public User getWhitePlayer(){
        return this.whitePlayer;
    }
    public void setBlackPlayer(User blackPlayer) {
        this.blackPlayer = blackPlayer;
    }
    public void setWhitePlayer(User whitePlayer) {
        this.whitePlayer = whitePlayer;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public User getBlackPlayer(){
        return this.blackPlayer;
    }
    public String getResult(){
        return this.result;
    }
}