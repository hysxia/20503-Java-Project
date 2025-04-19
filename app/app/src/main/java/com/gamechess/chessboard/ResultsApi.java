package com.gamechess.chessboard;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.util.List;
public interface ResultsApi {
    @GET("api/results/{gameNumber}")
    Call<Results> getResultByGameNumber(
            @Path("gameNumber") int gameNumber
    );

    @POST("api/results/create")
    Call<Results> createResult(
            @Query("gameNumber") Long gameNumber,
            @Query("whiteUserName") String whiteUserName,
            @Query("blackUserName") String blackUserName,
            @Query("result") String result
    );

    @GET("api/results/user/{username}")
    Call<List<Results>> getResultsForUser(
            @Path("username") String username
    );




}
