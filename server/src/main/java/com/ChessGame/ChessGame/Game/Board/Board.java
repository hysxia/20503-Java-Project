package com.ChessGame.ChessGame.Game.Board;
import com.ChessGame.ChessGame.Game.Pieces.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;


public class Board {
    private Square[][] board;
    private static Square enPassantTarget = null;
    public Board(){
        board= new Square[8][8];
        initializeBoard();
    }

// create the backend board
    public void initializeBoard(){
        for(int i=0;i< board.length;i++){
            for(int j=0;j<board[0].length;j++){
                board[i][j]=new Square(i,j,null);
            }
        }
        for(int col=0;col<board[0].length;col++){
            board[1][col].setPiece(new Pawn(false));
            board[6][col].setPiece(new Pawn(true));
        }
        board[0][0].setPiece(new Rook(false));
        board[0][7].setPiece(new Rook(false));
        board[7][0].setPiece(new Rook(true));
        board[7][7].setPiece(new Rook(true));

        board[0][1].setPiece(new Knight(false));
        board[0][6].setPiece(new Knight(false));
        board[7][1].setPiece(new Knight(true));
        board[7][6].setPiece(new Knight(true));

        board[0][2].setPiece(new Bishop(false));
        board[0][5].setPiece(new Bishop(false));
        board[7][2].setPiece(new Bishop(true));
        board[7][5].setPiece(new Bishop(true));

        board[0][3].setPiece(new Queen(false));
        board[7][3].setPiece(new Queen(true));

        board[0][4].setPiece(new King(false));
        board[7][4].setPiece(new King(true));
    }

    public Square[][] getBoard(){
        return board;
    }

    /* try to check if a move is valid and returns a String
        move-ok- the move is possible
        no piece- the sqaure that we want to move from is empty
        invalid move- move cannot be played
        promotion- pawn need to be promoted to continue
     */
    public String movePiece(int startRow, int startCol, int destRow, int destCol) {
        Square startSquare = board[startRow][startCol];
        Piece piece = startSquare.getPiece();
        // check that square from which we want to move is empty.
        if (piece == null) {
            return "NO_PIECE";
        }

        // Get legal moves for the piece in the square.
        boolean iswhite = board[startRow][startCol].getPiece().isWhite();
        List<Square> legalMoves = getLegalMovesForPieceWithoutRecursion(startRow, startCol,iswhite);
        Square destinationSquare = board[destRow][destCol];
        // Check that the destination is in the legal moves.
        if (legalMoves.contains(destinationSquare)) {
            // For pawn, check en passant.
            if (piece instanceof Pawn) {
                int direction = (piece.isWhite()) ? -1 : 1;
                // If pawn is moving diagonally into an empty square, it must be an en passant capture.
                if (Math.abs(destCol - startCol) == 1 && board[destRow][destCol].getPiece() == null) {
                    Square epTarget = Board.getEnPassantTarget();
                    // If no en passant target exists or it doesn't match the destination, the move is invalid.
                    if (epTarget == null || !epTarget.equals(destinationSquare)) {
                        return "INVALID_MOVE";
                    } else {
                        int capturedRow = destRow + (piece.isWhite() ? 1 : -1);
                        Piece capturedPawn = board[capturedRow][destCol].getPiece();
                        // Only capture if the piece to be captured is an enemy pawn.
                        if (capturedPawn == null || !capturedPawn.getType().equals("Pawn") ||
                                capturedPawn.isWhite() == piece.isWhite()) {
                            return "INVALID_MOVE";
                        }
                        board[capturedRow][destCol].setPiece(null);
                    }
                }
            }
            // Move the piece to its destination.
            destinationSquare.setPiece(piece);
            startSquare.setPiece(null);

            // Handle castling.
            if (piece instanceof King && Math.abs(destCol - startCol) == 2) {
                // Castling move detected.
                if (destCol > startCol) {
                    // King side castling: move the rook from column 7 to column startCol+1.
                    Piece rook = board[startRow][7].getPiece();
                    board[startRow][7].setPiece(null);
                    board[startRow][startCol + 1].setPiece(rook);
                    if (rook != null) {
                        rook.setMoved();
                    }
                } else {
                    // Queen side castling: move the rook from column 0 to column startCol-1.
                    Piece rook = board[startRow][0].getPiece();
                    board[startRow][0].setPiece(null);
                    board[startRow][startCol - 1].setPiece(rook);
                    if (rook != null) {
                        rook.setMoved();
                    }
                }
            }

            // Pawn promotion and en passant target update.
            if (piece instanceof Pawn) {
                Pawn pawn = (Pawn) piece;
                // Pawn promotion: if pawn reaches the end of the board.
                if ((pawn.isWhite() && destRow == 0) || (!pawn.isWhite() && destRow == 7)) {
                    return "PROMOTION_REQUIRED";
                }
                // Mark the pawn as having moved.
                pawn.setHasMoved(true);
                // If the pawn moved two steps, set the en passant target.
                if (Math.abs(destRow - startRow) == 2) {
                    int enPassantRow = (startRow + destRow) / 2;
                    Board.setEnPassantTarget(board[enPassantRow][startCol]);
                }
            } else {
                // For non-pawn moves, clear the en passant target.
                Board.setEnPassantTarget(null);
            }

            piece.setMoved();
            return "MOVE_OK";
        } else {
            return "INVALID_MOVE";
        }
    }
    // checks to find if the player is in check
    public boolean isInCheck(boolean forWhite) {
        // Find your King’s position
        Square kingSquare = findKingSquare(forWhite);
        if (kingSquare == null) {
            // Should not happen if your king is on the board
            return false;
        }
        int kingRow = kingSquare.getRow();
        int kingCol = kingSquare.getCol();

        // For each piece belonging to the other color, see if it attacks kingSquare
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece enemyPiece = board[row][col].getPiece();
                if (enemyPiece == null) continue;
                if (enemyPiece.isWhite() == forWhite) continue;
                // skip pieces that are same color as the king

                // SPECIAL CASE: If the enemy piece is a King,
                // just check adjacency instead of calling any "full move" method
                if (enemyPiece instanceof King) {
                    if (isAdjacentKing(row, col, kingRow, kingCol)) {
                        return true;
                    }
                } else {
                    // gathers the squares that piece can attack
                    List<Square> attackedSquares = getAttackedSquares(enemyPiece, row, col);
                    // if kingSquare is in that list, we are in check
                    for (Square sq : attackedSquares) {
                        if (sq.getRow() == kingRow && sq.getCol() == kingCol) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // we check and find all squares that are threatened
    public List<Square> getAttackedSquares(Piece piece, int row, int col) {
        List<Square> attacked = new ArrayList<>();
        String type = piece.getType();  // checks the type of the piece

        switch (type) {
            case "Pawn":
                if (piece.isWhite()) {
                    int newRow = row - 1;
                    // Diagonally left
                    int newCol = col - 1;
                    if (isOnBoard(newRow, newCol)) {
                        attacked.add(board[newRow][newCol]);
                    }
                    // Diagonally right
                    newCol = col + 1;
                    if (isOnBoard(newRow, newCol)) {
                        attacked.add(board[newRow][newCol]);
                    }
                } else {
                    int newRow = row + 1;
                    int newCol = col - 1;
                    if (isOnBoard(newRow, newCol)) {
                        attacked.add(board[newRow][newCol]);
                    }
                    newCol = col + 1;
                    if (isOnBoard(newRow, newCol)) {
                        attacked.add(board[newRow][newCol]);
                    }
                }
                break;

            default:
                //for the Queen, Rook,Knight,Bishop
                attacked.addAll(piece.getValidMoves(board, row, col));
                break;
        }

        return attacked;
    }
    //checks all possible moves that the king is able to do while he isn't in check
    public List<Square> getSafeKingMoves(Square[][] board, King king, int kingRow, int kingCol) {
        // Get candidate moves from the king's raw move generation.
        List<Square> candidateMoves = king.getValidMoves(board, kingRow, kingCol);
        List<Square> safeMoves = new ArrayList<>();

        // Save the original king square and piece.
        Square originalSquare = board[kingRow][kingCol];
        Piece kingPiece = originalSquare.getPiece();

        for (Square candidate : candidateMoves) {
            int targetRow = candidate.getRow();
            int targetCol = candidate.getCol();
            // Save the piece (if any) in the candidate square.
            Piece capturedPiece = board[targetRow][targetCol].getPiece();

            // Simulate moving the king to the candidate square.
            board[targetRow][targetCol].setPiece(kingPiece);
            originalSquare.setPiece(null);

            // Debug logging: you can print out the candidate and the result of isInCheck.
            boolean inCheckAfterMove = isInCheck(kingPiece.isWhite());
            System.out.println("Simulating move to (" + targetRow + "," + targetCol + "): isInCheck = " + inCheckAfterMove);

            // If the king is not in check after the move, add the candidate.
            if (!inCheckAfterMove) {
                safeMoves.add(candidate);
            }

            // Revert the simulation by restoring the original state.
            originalSquare.setPiece(kingPiece);
            board[targetRow][targetCol].setPiece(capturedPiece);
        }

        return safeMoves;
    }


    // one king can attack the other
    public boolean isAdjacentKing(int kingRow, int kingCol, int myKingRow, int myKingCol) {
        // If the distance is at most 1 in row/col, that means
        // one King can "attack" the other.
        return (Math.abs(kingRow - myKingRow) <= 1
                && Math.abs(kingCol - myKingCol) <= 1);
    }

    // find the square that the king is for the color
    public Square findKingSquare(boolean forWhite) {
        for (int row = 0; row <8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = board[row][col].getPiece();
                if (p != null && p.isWhite() == forWhite && p instanceof King) {
                    return new Square(row, col);
                }
            }
        }
        return null; // should never happen if your King is on the board
    }

    // Simple boundary check
    private boolean isOnBoard(int r, int c) {
        return (r >= 0 && r < 8 && c >= 0 && c < 8);
    }
    //checks that they are no further possible moves so that the king isn't in check
    public boolean isCheckmate(boolean isWhite) {
        if (!isInCheck(isWhite)) {
            return false; // If the player is not in check, it can't be checkmate.
        }

        // Iterate over all pieces of the current player to find at least one legal move.
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col].getPiece();
                if (piece != null && piece.isWhite() == isWhite) {
                    List<Square> legalMoves = getLegalMovesForPieceWithoutRecursion(row, col, isWhite);
                    if (!legalMoves.isEmpty()) {
                        return false; // Found at least one legal move: not checkmate.
                    }
                }
            }
        }

        // No legal moves found, and the king is in check: checkmate!
        return true;
    }
    //get all legal moves for a piece
    public List<Square> getLegalMovesForPieceWithoutRecursion(int row, int col, boolean forWhite) {
        Square[][] squares = this.getBoard();
        Piece piece = squares[row][col].getPiece();

        if (piece == null || piece.isWhite() != forWhite) {
            return Collections.emptyList();
        }

        List<Square> candidateMoves = piece.getValidMoves(squares, row, col);
        List<Square> legalMoves = new ArrayList<>();

        for (Square target : candidateMoves) {
            // Save current state
            Piece capturedPiece = target.getPiece();
            Square originalSquare = squares[row][col];
            Piece originalPiece = originalSquare.getPiece();

            // Perform the move temporarily
            target.setPiece(originalPiece);
            originalSquare.setPiece(null);

            // Check for king's safety after this hypothetical move
            boolean moveLeavesKingInCheck = isInCheck(forWhite);

            // Revert the move
            originalSquare.setPiece(originalPiece);
            target.setPiece(capturedPiece);

            // If the king is safe after this hypothetical move, it’s legal
            if (!moveLeavesKingInCheck) {
                legalMoves.add(target);
            }
        }

        return legalMoves;
    }
    public static Square getEnPassantTarget() {
        return enPassantTarget;
    }
    public static void setEnPassantTarget(Square s) {
        enPassantTarget = s;
    }
}
