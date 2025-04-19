package com.ChessGame.ChessGame.Game.Pieces;
import com.ChessGame.ChessGame.Game.Board.*;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece{
    public Knight(boolean W) {
        super(W);
    }
    @Override
    public String getType(){
        return "N";
    }
    @Override
    public List<Square> getValidMoves(Square[][] board, int row, int col) {
        List<Square> moves = new ArrayList<>();
        //all possible moves the knight can move
        int[][] movesPattern = {
                {-2, -1}, {-2, 1}, {2, -1}, {2, 1},
                {-1, -2}, {-1, 2}, {1, -2}, {1, 2}
        };
        // checks for each iterates each possbile move and checks that its in the board
        for (int[] move : movesPattern) {
            int newRow = row + move[0];
            int newCol = col + move[1];
            //checks that a new coordinates are in the board
            if (isInsideBoard(newRow, newCol)) {
                Piece destinationPiece = board[newRow][newCol].getPiece();
                // a knight can move into this square only its free or an enemy piece is in there
                if (destinationPiece == null || destinationPiece.isWhite()!=board[row][col].getPiece().isWhite()) {
                    moves.add(board[newRow][newCol]);
                }
            }
        }
        return moves;
    }
    private boolean isInsideBoard(int row, int col) {

        return (row >= 0 && row < 8 && col >= 0 && col < 8);
    }
}
