package com.ChessGame.ChessGame.Game.Pieces;

import com.ChessGame.ChessGame.Game.Board.*;
import java.util.List;
import java.util.ArrayList;

public class Bishop extends Piece {

    public Bishop(boolean W) {
        super(W);
    }

    @Override
    public String getType() {
        return "Bishop";
    }

    @Override
    public List<Square> getValidMoves(Square[][] board, int row, int col) {
        List<Square> moves = new ArrayList<>();
        //direction that the bishop can move into all diagonals
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        // for each direction we check if we reach the end or a diffrent piece
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
                // checks that if in the square there is a piece
                Piece piece = board[newRow][newCol].getPiece();
                // if the sqaure is empty than add it to the list
                if (piece == null) {
                    moves.add(board[newRow][newCol]);
                } else {
                    //checks that if there is a piece in the square than it's of a different color than adds it
                    if (piece.isWhite()!=board[row][col].getPiece().isWhite()) {
                        moves.add(board[newRow][newCol]);
                    }
                    break;
                }
            }
        }

        return moves;
    }
    private boolean isInsideBoard(int row, int col) {

        return (row >= 0 && row < 8 && col >= 0 && col < 8);
    }
}