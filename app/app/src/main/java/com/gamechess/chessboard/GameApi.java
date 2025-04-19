package com.gamechess.chessboard;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.util.List;
import java.util.Map;
public interface GameApi {
    //api call for joining a new game
    @POST("api/chess/joinOrCreate")
    Call<Game> joinOrCreateGame(@Query("username") String username);
    //api call for moving a piece
    @POST("api/chess/{gameId}/move")
    Call<ResponseBody> makeMove(
            @Path("gameId") long gameId,
            @Query("username") String username,
            @Query("startRow") int startRow,
            @Query("startCol") int startCol,
            @Query("destRow") int destRow,
            @Query("destCol") int destCol
    );
    //api call for promoting a pawn
    @POST("api/chess/{gameId}/promote")
    Call<ResponseBody> promotePawn(
            @Path("gameId") Long gameId,
            @Query("username") String username,
            @Query("promotionPieceType") String promotionPieceType,
            @Query("pawnRow") int pawnRow,
            @Query("pawnCol") int pawnCol
    );
    //api call for getting game
    @GET("/api/chess/{gameId}")
    Call<Game> getGame(@Path("gameId") Long gameId);
    //api call for getting legal moves
    @GET("/api/chess/{gameId}/legalmoves/{row}/{col}")
    Call<List <Square>> getLegalMoves(
            @Path("gameId") Long gameId,
            @Path("row") int row,
            @Path("col") int col,
            @Query("forWhite") boolean forWhite
    );
    //api call for proposing a draw
    @POST("api/chess/{gameId}/draw")
    Call<Map<String, String>> proposeDraw(
            @Path("gameId") Long gameId,
            @Query("username") String username
    );
    //api call for resigning from the game
    @POST("api/chess/{gameId}/resign")
    Call<Map<String, String>> resignGame(
            @Path("gameId") Long gameId,
            @Query("username") String username
    );
    //api call for declining the draw
    @POST("api/chess/{gameId}/declineDraw")
    Call<Map<String,String>> declineDraw(
            @Path("gameId") Long gameId,
            @Query("username") String username
    );
    //api call for getting if the game is ready
    @GET("/api/chess/{gameId}/status")
    Call<Boolean> isGameReady(@Path("gameId") Long gameId);
}
