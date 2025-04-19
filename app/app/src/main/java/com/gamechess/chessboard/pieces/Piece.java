package com.gamechess.chessboard.pieces;

public abstract class Piece {
    private String id;
    private int row;
    private int col;

    public Piece(String id, int row, int col) {
        this.id = id;
        this.row = row;
        this.col = col;
    }

    public String getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
}