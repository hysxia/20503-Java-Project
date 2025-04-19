package com.gamechess.chessboard;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {
    //api call for logging
    @POST("api/users/login")
    Call<User> login(
            @Query("username") String username,
            @Query("password") String password
    );
    //api call for registering
    @POST("api/users/register")
    Call<User> register(
            @Query("username") String username,
            @Query("email") String email,
            @Query("password") String password
    );
    //retrieving user from username
    @GET("api/users/{username}")
    Call<User> getUser(@Path("username") String username);

    @GET("api/users/{username}/wins")
    Call<Integer> getWins(@Path("username") String username);
    @GET("api/users/{username}/draws")
    Call<Integer> getDraws(@Path("username") String username);
    @GET("api/users/{username}/losses")
    Call<Integer> getLosses(@Path("username") String username);
}
