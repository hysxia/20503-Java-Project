package com.gamechess.chessboard;

public class Results {
    private long gameNumber;
    private User whitePlayer;
    private User blackPlayer;
    private String result;
    public void setGameNumber(long gameNumber) {
        this.gameNumber = gameNumber;
    }

    public long getGameNumber() {
        return gameNumber;
    }
    public User getWhitePlayer(){
        return this.whitePlayer;
    }
    public User getBlackPlayer(){
        return this.blackPlayer;
    }
    public String getResult(){
        return this.result;
    }
}


