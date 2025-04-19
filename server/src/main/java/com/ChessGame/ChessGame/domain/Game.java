package com.ChessGame.ChessGame.domain;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name="games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Auto-generated game id

    @ManyToOne
    private User whitePlayer;

    @ManyToOne
    private User blackPlayer;

    private boolean promotionPending; //True if a pawn is ready to promote

    @Enumerated(EnumType.STRING)
    private EndReason endReason; //Reason to end the game

    private String currentTurn;

    @Column(name = "is_draw", nullable = false, columnDefinition = "tinyint(1) default 0")
    private boolean draw =false;

    @ElementCollection
    private List<String> moves = new ArrayList<>();

    @Transient
    private String[][] boardState;

    private int movecount; //Counts how many moves were played

    private boolean finished = false; //True if game is finished

    private boolean whiteDrawOffered;

    private boolean blackDrawOffered;

    private boolean isReady =false;//True if both players have joined
    private String winner;
    private boolean opponentDrawOffered=false; //True if opponent offered a draw






    public Game() { }

    public Game(User white, User black) {
        this.whitePlayer = white;
        this.blackPlayer = black;
        this.promotionPending = false;
        this.currentTurn = white.getUsername();
        this.movecount=0;

    }

    public void setReady(boolean bol){
        this.isReady=bol;
    } //Game is ready to start
    public boolean isReady(){
        return this.isReady;
    } //Checks if both players have ready to start
    public boolean isWhiteDrawOffered() {
        return whiteDrawOffered;
    }
    public boolean isBlackDrawOffered() {
        return blackDrawOffered;
    }

    //Getters and Setters
    public int getMovecount(){

        return movecount;
    }
    public void setMovecount(int movecount){

        this.movecount=movecount;
    }
    public String[][] getBoardState() {

        return boardState;
    }
    public void setBoardState(String[][] boardState) {

        this.boardState = boardState;
    }
    public Long getId() {
        return id;
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
    public boolean isDraw() {

        return draw;
    }
    public void setDraw(boolean isDraw) {
        this.draw = isDraw;
    }
    public boolean isFinished() {
        return finished;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
    public void setWhiteDrawOffered(boolean whiteDrawOffered) {
        this.whiteDrawOffered = whiteDrawOffered;
    }
    public void setBlackDrawOffered(boolean blackDrawOffered) {
        this.blackDrawOffered = blackDrawOffered;
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
    public boolean isOpponentDrawOffered() {
        return opponentDrawOffered;
    }
    public void setOpponentDrawOffered(boolean opponentDrawOffered) {
        this.opponentDrawOffered = opponentDrawOffered;
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