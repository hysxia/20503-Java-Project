package com.ChessGame.ChessGame.service;


import com.ChessGame.ChessGame.domain.Game;
import com.ChessGame.ChessGame.domain.User;
import com.ChessGame.ChessGame.repository.GameRepository;
import com.ChessGame.ChessGame.Game.Board.*;
import com.ChessGame.ChessGame.Game.Pieces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class GameService {
    @Autowired
    private  GameRepository gameRepository;
    @Autowired
    private  UserService userService;
    @Autowired
    private  ResultService resultService;

    private  Map<Long, Board> gameBoards = new HashMap<>();
    private Map<Long, Set<String>> drawOffers = new HashMap<>();

    @Autowired
    public GameService(GameRepository gameRepository, UserService userService, ResultService resultService) {
        this.gameRepository = gameRepository;
        this.userService = userService;
        this.resultService = resultService;
    }

    //create a new game
    public Game createGame(String whiteUsername) {
        User white = userService.findByUsername(whiteUsername);
        // create a new Game
        Game game = new Game(white, null);
        game.setCurrentTurn(whiteUsername);
        gameRepository.save(game);
        // create the board
        Board board = new Board();
        gameBoards.put(game.getId(), board);
        return game;
    }
    //join a game that is waiting for second player
    public Game joinGame(Long gameId, String blackUsername) {
        Game game = getGame(gameId);;
        User black = userService.findByUsername(blackUsername);
        game.setBlackPlayer(black);
        game.setReady(true);
        gameRepository.save(game);

        return game;
    }
    //function that checks which function do we need to use the join or create function
    public Game joinOrCreateGame(String username) {
        // find a game that is waiting for an opponent
        Optional<Game> waitingGame = gameRepository.findAll().stream().filter(game -> game.getBlackPlayer() == null).findFirst();
        if (waitingGame.isPresent()) {
            // join the game as the opponent

            System.out.println("the joining user is "+username);
            return joinGame(waitingGame.get().getId(),username);
        } else {
            // there are no game waiting for an opponents so create a new game
            System.out.println("the creating user is "+username);
            Game newGame = createGame(username);


            return newGame;
        }
    }

    // function of moving a piece on the board
    public String makeMove(Long gameId, String username, int startRow, int startCol, int destRow, int destCol) {


        Game game = getGame(gameId);
        if (game == null) {

            return "GAME_NOT_FOUND";
        }
        if (!game.getCurrentTurn().equals(username)) {
            return "NOT_YOUR_TURN";
        }
        Board board = gameBoards.get(gameId);
        if (board == null) {
            return "BOARD_NOT_FOUND";
        }
        boolean isWhitePlayer = game.getWhitePlayer() != null && game.getWhitePlayer().getUsername().equals(username);

        // Save the moved piece BEFORE moving
        Piece movedPieceBeforeMove = board.getBoard()[startRow][startCol].getPiece();
        Piece targetPieceBeforeMove = board.getBoard()[destRow][destCol].getPiece();
        // Make the actual move
        String moveResult = board.movePiece(startRow, startCol, destRow, destCol);


        // Check if it was a pawn move or a capture safely after the move

        boolean pawnOrCapture = movedPieceBeforeMove instanceof Pawn || (targetPieceBeforeMove != null && targetPieceBeforeMove != movedPieceBeforeMove);

        switch (moveResult) {
            case "NO_PIECE":
                return "NO_PIECE";
            case "INVALID_MOVE":
                return "INVALID_MOVE";
            case "PROMOTION_REQUIRED":
                game.setPromotionPending(true);
                gameRepository.save(game);
                recordMove(game, startRow, startCol, destRow, destCol);
                return "PROMOTION_REQUIRED";
            case "MOVE_OK":
                if (pawnOrCapture) {
                    game.setMovecount(0);
                } else {
                    game.setMovecount(game.getMovecount() + 1);
                }
                gameRepository.save(game);
                recordMove(game, startRow, startCol, destRow, destCol);

                if (game.getMovecount() >=50) {
                    handleDraw50(game);
                    return "DRAW_BY_50_MOVE_RULE";
                }

                boolean isWhiteMoving = (game.getWhitePlayer() != null && username.equals(game.getWhitePlayer().getUsername()));
                boolean opponentIsWhite = !isWhiteMoving;

                if (board.isCheckmate(opponentIsWhite)) {
                    handleGameOver(game, isWhiteMoving ? "White" : "Black",Game.EndReason.CHECKMATE);
                    return "CHECKMATE";
                }

                if (isStalemate(gameId, opponentIsWhite)) {
                    handleDraw(game);
                    return "STALEMATE";
                }

                if (board.isInCheck(opponentIsWhite)) {
                    switchTurn(game);
                    return "CHECK";
                }

                switchTurn(game);
                return "MOVE_OK";
            default:
                return "UNKNOWN_RESULT";
        }
    }
    //get the all the drawoffer for the game
    public Set<String> getDrawOffers(Long gameId) {
        return drawOffers.get(gameId);
    }

    // function for propsoing a draw
    public String proposeDraw(Long gameId, String username) {
        Game game = getGame(gameId);
        if (game == null) return "GAME_NOT_FOUND";

        drawOffers.putIfAbsent(gameId, new HashSet<>());
        Set<String> offers = drawOffers.get(gameId);
        offers.add(username);

        if (offers.size() == 2) { // Both players agreed
            handleDraw(game);
            clearDrawOffers(gameId);
            return "DRAW_AGREED";
        }

        return "DRAW_OFFERED";
    }
    //Delete all the draw offers for this game
    public void clearDrawOffers(Long gameId) {
        drawOffers.remove(gameId);
    }
    //function for resigning from the game
    public String resignGame(Long gameId, String username) {

        Game game = getGame(gameId);

        if (game == null) return "GAME_NOT_FOUND";

        // Determine winner

        String winner = game.getWhitePlayer().getUsername().equals(username) ? "Black" : "White";


        handleGameOver(game, winner, Game.EndReason.RESIGNATION);



        return "PLAYER_RESIGNED";

    }
    // function for handling a draw from stalemate
    private void handleDraw(Game game) {

        handleGameOver(game,"Draw", Game.EndReason.STALEMATE);
    }
    //function for handling a draw from 50 moves
    private void handleDraw50(Game game) {

        handleGameOver(game,"Draw", Game.EndReason.DRAW_50_MOVES);
    }
    //funvtion for prompting pawn to a diffrent piece
    public String promotePawn(Long gameId, String username, String promotionPieceType, int pawnRow, int pawnCol) {

        Game game = getGame(gameId);
        // checks if a promotion is needed
        if (!game.isPromotionPending()) {
            return "NO_PROMOTION_NEEDED";
        }
        // checks that the board exists
        Board board = gameBoards.get(gameId);
        if (board == null) {
            return "BOARD_NOT_FOUND";
        }
        // checks that the piece is a pawn
        Piece piece = board.getBoard()[pawnRow][pawnCol].getPiece();
        if (piece == null || !piece.getType().equals("Pawn")) {
            return "NO_PAWN";
        }

        Pawn pawn = (Pawn) piece;
        // promote the pawn
        Piece newPiece = pawn.promote(promotionPieceType);
        board.getBoard()[pawnRow][pawnCol].setPiece(newPiece);
        // set the pending to false
        game.setPromotionPending(false);
        gameRepository.save(game);
        switchTurn(game);
        return "PROMOTION_OK";
    }
    // function that record a move of a piece
    private void recordMove(Game game, int startRow, int startCol, int destRow, int destCol) {
        String moveNotation = String.format("%d,%d -> %d,%d", startRow, startCol, destRow, destCol);
        game.getMoves().add(moveNotation);
        gameRepository.save(game);
    }
    //fucntion that switches the turn of the players
    private void switchTurn(Game game) {
        if (game.getWhitePlayer() == null || game.getBlackPlayer() == null) {
            return;
        }

        if (game.getCurrentTurn().equals(game.getWhitePlayer().getUsername())) {
            game.setCurrentTurn(game.getBlackPlayer().getUsername());
        } else {
            game.setCurrentTurn(game.getWhitePlayer().getUsername());
        }
        gameRepository.save(game);
    }
    //function that handle the gameover
    private void handleGameOver(Game game, String result, Game.EndReason endReason) {
        // Mark game as finished
        game.setFinished(true);
        game.setWinner(result);
        game.setEndReason(endReason);
        gameRepository.save(game);

        // Get players
        User white = game.getWhitePlayer();
        User black = game.getBlackPlayer();

        //Handling the stats for the Users
        if ("White".equalsIgnoreCase(result)) {
            white.addWin();
            black.addLosses();
        } else if ("Black".equalsIgnoreCase(result)) {
            black.addWin();
            white.addLosses();
        } else if ("Draw".equalsIgnoreCase(result)) {
            game.setDraw(true);
            white.addDraws();
            black.addDraws();
            gameRepository.save(game);
        }

        // Save players with updated stats
        userService.saveUser(white);
        userService.saveUser(black);

        // Save result entry
        resultService.createResult(game.getId().intValue(),
                white.getUsername(),
                black.getUsername(),
                result);

        // Cleanup board
        gameBoards.remove(game.getId());
    }
    //function that return the game from gameid
    public Game getGame(Long gameId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        return gameOptional.orElse(null);
    }
    //function that checks if the game is in stalemate
    public boolean isStalemate(Long gameId, boolean forWhite) {
        Board board = gameBoards.get(gameId);
        if (board == null) return false;

        // If the player is not in check and has no legal moves, it's stalemate
        return !board.isInCheck(forWhite) && !hasAnyLegalMove(board, forWhite);
    }
    //function that checks if there are any legal moves
    private boolean hasAnyLegalMove(Board board, boolean forWhite) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getBoard()[row][col].getPiece();
                if (piece != null && piece.isWhite() == forWhite) {
                    List<Square> moves = board.getLegalMovesForPieceWithoutRecursion(row, col,forWhite);
                    if (!moves.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //function that returns all legal moves
    public List<Square> getLegalMoves(Long gameId, int row, int col, boolean forWhite) {
        Board board = gameBoards.get(gameId);
        if (board == null) {
            // No board found => return empty
            return new ArrayList<>();
        }

        // If the (row,col) is out of bounds, also bail out
        if (row < 0 || row >= 8 || col < 0 || col >= 8) {
            return Collections.emptyList();
        }

        Piece piece = board.getBoard()[row][col].getPiece();
        if (piece == null || piece.isWhite() != forWhite) {
            return Collections.emptyList();
        }

        // Otherwise, now you can safely call .isWhite() or pass piece to your next method
        return board.getLegalMovesForPieceWithoutRecursion(row, col,forWhite);
    }
    //returns the board of a Gameid
    public Board getBoardByGameId(Long gameId) {
        return gameBoards.get(gameId);
    }
}





