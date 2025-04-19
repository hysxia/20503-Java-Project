package com.gamechess.chessboard;

import java.util.List;

public class Game {
    private Long id; //Auto-generated game id
    private User whitePlayer;
    private User blackPlayer;
    private boolean promotionPending;//True if a pawn is ready to promote
    private String currentTurn;
    private List<String> moves;
    // if not stored in DB directly
    private String[][] boardState;
    private String gameStatus;
    private boolean finished;//True if game is finished
    private boolean isReady;//True if both players have joined
    private boolean opponentDrawOffered=false;//True if opponent offered a draw
    private boolean draw;
    private EndReason endReason;//Reason to end the game
    private String winner;


    public boolean isReady() {
        return isReady;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isDraw() {
        return draw;
    }
    public boolean isOpponentDrawOffered() {
        return opponentDrawOffered;
    }
    private boolean whiteDrawOffered=false;
    private boolean blackDrawOffered=false;

    public boolean isWhiteDrawOffered() {
        return whiteDrawOffered;
    }

    public void setWhiteDrawOffered(boolean whiteDrawOffered) {
        this.whiteDrawOffered = whiteDrawOffered;
    }

    public boolean isBlackDrawOffered() {
        return blackDrawOffered;
    }


    //Getters and Setters
    public void setBlackDrawOffered(boolean blackDrawOffered) {
        this.blackDrawOffered = blackDrawOffered;
    }
    public void setOpponentDrawOffered(boolean opponentDrawOffered) {
        this.opponentDrawOffered = opponentDrawOffered;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(User whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public User getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(User blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public boolean isPromotionPending() {
        return promotionPending;
    }

    public void setPromotionPending(boolean promotionPending) {
        this.promotionPending = promotionPending;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    public List<String> getMoves() {
        return moves;
    }

    public void setMoves(List<String> moves) {
        this.moves = moves;
    }
    public String[][] getBoardState() {
        return boardState;
    }
    public void setBoardState(String[][] boardState) {
        this.boardState = boardState;
    }

    public void setEndReason(EndReason endReason) {
        this.endReason = endReason;
    }
    public EndReason getEndReason() {
        return endReason;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWinner() {
        return winner;
    }



    //Reasons to end the game
    public enum EndReason{
        CHECKMATE,
        DRAW,
        STALEMATE,
        DRAW_50_MOVES,
        RESIGNATION
    }

}