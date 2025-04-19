package com.ChessGame.ChessGame.Game.Pieces;
import com.ChessGame.ChessGame.Game.Board.Board;
import com.ChessGame.ChessGame.Game.Board.Square;
import java.util.ArrayList;
import java.util.List;

public class King extends Piece{
    //has moved for casling
    private boolean hasMoved=false;
    public King(boolean W) {
        super(W);
    }
    @Override
    public String getType(){
        return "King";
    }
    @Override
    public List<Square> getValidMoves(Square[][] board, int row, int col) {
        List<Square> moves = new ArrayList<>();

        // direction that the king can move a sqaure around him
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };
        //checks each direction if sqaure is a valid target for movement
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (isInsideBoard(newRow, newCol)) {
                Piece destinationPiece = board[newRow][newCol].getPiece();
                if (destinationPiece == null || destinationPiece.isWhite()!=board[row][col].getPiece().isWhite()) {
                    moves.add(board[newRow][newCol]);
                }
            }
        }
        if (!hasMoved) {
            moves.addAll(getCastlingMoves(board, row, col));
        }
        return moves;
    }

    private List<Square> getCastlingMoves(Square[][] board, int row, int col) {
        List<Square> castlingMoves = new ArrayList<>();
       //checks if casling is possible with the rook close to the king side
        if (canCastle(board, row, col, 7)) {
            castlingMoves.add(board[row][col + 2]);
        }
        //checks if casling is possible with the rook close to the queen side
        if (canCastle(board, row, col, 0)) {
            castlingMoves.add(board[row][col - 2]);
        }
        return castlingMoves;
    }

    private boolean canCastle(Square[][] board, int row, int kingCol, int rookCol) {
        Piece rook = board[row][rookCol].getPiece();
        // the sqaure in to have a rook of the same color that hasn't moved
        if (!(rook instanceof Rook) || ((Rook) rook).getMoved()) {
            return false;
        }
        // checks the direction of the rook queen or king side
        int direction = (rookCol > kingCol) ? 1 : -1;
        //checks that the sqaure between them are empty
        for (int c = kingCol + direction; c != rookCol; c += direction) {
            if (board[row][c].getPiece() != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isInsideBoard(int row, int col) {
        return (row >= 0 && row < 8 && col >= 0 && col < 8);
    }


    public void setMoved(boolean moved) {
        this.hasMoved = moved;
    }
}