package com.ChessGame.ChessGame.Game.Board;
import com.ChessGame.ChessGame.Game.Pieces.*;


public class Square {
    private int row; //0-7
    private int col; //0-7
    private Piece piece; //Kind of piece that on square (Null if nothing on square)
    public Square ( int row , int col){//Creates empty square
        this.row=row;
        this.col=col;
        this.piece=null;
    }
    public Square ( int row , int col, Piece piece){//Creates a square with a piece
        this.row=row;
        this.col=col;
        this.piece=piece;
    }
    //Getters and Setters
    public Piece getPiece(){
        return this.piece;
    }
    public void setPiece(Piece piece){
        this.piece=piece;
    }
    public int getRow(){
        return this.row;
    }
    public int getCol(){
        return this.col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;       // same object
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        Square other = (Square) o;
        return (this.row == other.row && this.col == other.col);
    }
    //Checks if square is empty
    public boolean isEmpty() {
        return piece == null;
    }
}