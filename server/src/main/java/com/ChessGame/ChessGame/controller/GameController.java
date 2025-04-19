package com.ChessGame.ChessGame.controller;

import com.ChessGame.ChessGame.Game.Board.Board;
import com.ChessGame.ChessGame.Game.Board.Square;
import com.ChessGame.ChessGame.Game.Pieces.Piece;
import com.ChessGame.ChessGame.domain.Game;
import com.ChessGame.ChessGame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;


@RestController
@RequestMapping("/api/chess")
public class GameController {

    @Autowired
    private GameService gameService;
    //connection to the client for creating or joining a game
    @PostMapping("/joinOrCreate")
    public Game joinOrCreateGame(@RequestParam String username) {

        return gameService.joinOrCreateGame(username);
    }
    //connection to the client for moving a piece
    @PostMapping("/{gameId}/move")
    public String makeMove(@PathVariable Long gameId, @RequestParam String username, @RequestParam int startRow, @RequestParam int startCol, @RequestParam int destRow, @RequestParam int destCol) {

        String result = gameService.makeMove(gameId, username, startRow, startCol, destRow, destCol);
        return result;
    }
    //connection to the client for promoting a pawn
    @PostMapping("/{gameId}/promote")
    public ResponseEntity<String> promotePawn(@PathVariable Long gameId, @RequestParam String username, @RequestParam String promotionPieceType, @RequestParam int pawnRow, @RequestParam int pawnCol) {

        String result = gameService.promotePawn(gameId, username, promotionPieceType, pawnRow, pawnCol);
        if ("PROMOTION_OK".equals(result)) {
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(result);
        } else {
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body(result);
        }
    }
    //connection to the client for getting game
    @GetMapping("/{gameId}")
    public  ResponseEntity<Game> getGame(@PathVariable Long gameId) {
        Game game = gameService.getGame(gameId);
        Board board = gameService.getBoardByGameId(gameId);
        String[][] serializedBoard = serializeBoard(board);
        game.setBoardState(serializedBoard);
        Set<String> offers = gameService.getDrawOffers(gameId);
        if (offers != null && game.getWhitePlayer() != null && game.getBlackPlayer() != null) {
            game.setWhiteDrawOffered(offers.contains(game.getWhitePlayer().getUsername()));
            game.setBlackDrawOffered(offers.contains(game.getBlackPlayer().getUsername()));
        } else {
            game.setWhiteDrawOffered(false);
            game.setBlackDrawOffered(false);
        }
        return ResponseEntity.ok(game);
    }

    private String[][] serializeBoard(Board board) {
        if(board == null) {
            return new String[8][8];}
        String[][] result = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getBoard()[i][j].getPiece();
                if (piece == null) {
                    result[i][j] = "";
                } else {
                    String color = piece.isWhite() ? "w" : "b";
                    result[i][j] = color + piece.getType().charAt(0); // wP, bR, etc.
                }
            }
        }
        return result;
    }
    //connection to the client for getting legal moves
    @GetMapping("/{gameId}/legalmoves/{row}/{col}")
    public List<Square> getLegalMoves(@PathVariable Long gameId,@PathVariable int row, @PathVariable int col, @RequestParam boolean forWhite) {
        return gameService.getLegalMoves(gameId,row,col, forWhite);
    }
    //connection to the client for proposing a draw
    @PostMapping("/{gameId}/draw")
    public ResponseEntity<Map<String,String>> proposeDraw(
            @PathVariable Long gameId,
            @RequestParam String username
    ) {
        String result = gameService.proposeDraw(gameId, username);

        Map<String,String> body = new HashMap<>();
        body.put("result", result);

        return ResponseEntity.ok(body);
    }
    //connection to the client for resigning
    @PostMapping("/{gameId}/resign")
    public ResponseEntity<Map<String, String>> resignGame(
            @PathVariable Long gameId,
            @RequestParam String username
    ) {
        String result = gameService.resignGame(gameId, username);

        Map<String, String> responseJson = new HashMap<>();
        responseJson.put("result", result);

        return ResponseEntity.ok(responseJson);
    }
    //connection to the client for declining a draw
    @PostMapping("/{gameId}/declineDraw")
    public ResponseEntity<Map<String,String>> declineDraw(
            @PathVariable Long gameId,
            @RequestParam String username
    ) {
        //If declines then delete all the draw offers from the game
        gameService.clearDrawOffers(gameId);
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("result", "DRAW_DECLINED");

        return ResponseEntity.ok(responseJson);
    }
    //connection to the client for finding if a game is ready
    @GetMapping("/{gameId}/status")
    public boolean isGameReady(@PathVariable Long gameId) {
        Game game = gameService.getGame(gameId);
        boolean status = game != null && game.isReady();
        System.out.println("Returning game ready status for game " + gameId + ": " + status);
        return status;
    }
}
