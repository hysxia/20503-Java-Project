package com.ChessGame.ChessGame.Game.Pieces;
import com.ChessGame.ChessGame.Game.Board.*;


import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    private boolean hasMoved = false;

    public Pawn(boolean W) {
        super(W);
    }

    @Override
    public String getType() {
        return "Pawn";
    }

    @Override
    public List<Square> getValidMoves(Square[][] board, int row, int col) {
        List<Square> moves = new ArrayList<>();
        //the direction that a pawn can move to a white move up the board
        //and black moves down the board
        int direction = (isWhite()) ? -1 : 1;

        //checks the movment of the pawn moving forward
        int forwardRow = row + direction;
        if (isInsideBoard(forwardRow, col) && board[forwardRow][col].getPiece() == null) {
            moves.add(board[forwardRow][col]);

            // if its the first move of the pawn it can move two sqaures
            int twoStepRow = row + (2 * direction);
            if (!hasMoved && isInsideBoard(twoStepRow, col) && board[twoStepRow][col].getPiece() == null) {
                moves.add(board[twoStepRow][col]);
            }
        }

        // checks the capture moves of the pawn
        int[][] captureMoves = { {direction, -1}, {direction, 1} };
        for (int[] capture : captureMoves) {
            int captureRow = row + capture[0];
            int captureCol = col + capture[1];
            if (isInsideBoard(captureRow, captureCol)) {
                Piece destinationPiece = board[captureRow][captureCol].getPiece();
                if (destinationPiece != null && destinationPiece.isWhite() != this.isWhite()) {
                    moves.add(board[captureRow][captureCol]);
                }
            }
        }

        // Check if there is an en passant target square set on the board.
        Square enPassant = Board.getEnPassantTarget();
        if (enPassant != null) {
            // checks that the target sqare is one move forward of the pawn in the row and the col is diffrent in one
            //if it is add the move to the legal moves
            if (enPassant.getRow() == row + direction &&
                    (enPassant.getCol() == col - 1 || enPassant.getCol() == col + 1)) {
                int capturedPawnRow = enPassant.getRow() + (this.isWhite() ? 1 : -1);
                // Check if that square contains an enemy pawn.
                Piece potentialPawn = board[capturedPawnRow][enPassant.getCol()].getPiece();
                if (potentialPawn != null && potentialPawn.getType().equals("Pawn") && potentialPawn.isWhite() != this.isWhite()) {
                    moves.add(enPassant);
                }
            }
        }
        return moves;
    }
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
    private boolean isInsideBoard(int row, int col) {
        return (row >= 0 && row < 8 && col >= 0 && col < 8);
    }

    //check the promotion of the pawn
    public Piece promote(String promotionChoice) {
        switch (promotionChoice) {
            case "Queen":
                return new Queen(isWhite());
            case "Rook":
                return new Rook(isWhite());
            case "Bishop":
                return new Bishop(isWhite());
            case "Knight":
                return new Knight(isWhite());
            default:
                return new Queen(isWhite());
        }
    }
}