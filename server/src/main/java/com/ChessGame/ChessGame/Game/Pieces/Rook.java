package com.ChessGame.ChessGame.Game.Pieces;

import com.ChessGame.ChessGame.Game.Board.*;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    private boolean hasMoved=false;
    public Rook(boolean W) {
        super(W);
    }
    @Override
    public String getType(){
        return "Rook";
    }
    @Override
    public List<Square> getValidMoves(Square[][] board, int row, int col) {
        List<Square> moves = new ArrayList<>();
        // movement of the rook piece up,down,left,right
        int[][] directions = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
        // checks each direction and search it until it reaches the end of the board or piece
        for (int[] dir : directions) {
            int newRow = row;
            int newCol = col;

            while (true) {
                //moves a single square in a spiefic direction
                newRow += dir[0];
                newCol += dir[1];
                // checks that we are out of bounds
                if (!isInsideBoard(newRow, newCol)) {
                    break;
                }
                //checks if there is a piece in the square
                Piece piece = board[newRow][newCol].getPiece();
                //if the square is empty than add it to valid moves
                if (piece == null) {
                    moves.add(board[newRow][newCol]);
                } else {
                    //if the sqaure isn't empty than check it's an enemy piece and if it is add to valid moves.
                    if (piece.isWhite()!=board[row][col].getPiece().isWhite()) {
                        moves.add(board[newRow][newCol]);
                    }
                    break;
                }
            }
        }

        return moves;
    }
    public boolean getMoved(){
        return this.hasMoved;
    }
    private boolean isInsideBoard(int row, int col) {
        return (row >= 0 && row < 8 && col >= 0 && col < 8);
    }
}