package com.gamechess.chessboard;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerConnection {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    // Ensure only one Retrofit instance is created
    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Game API
    public static GameApi getGameApi() {
        return getRetrofitInstance().create(GameApi.class);
    }

    // User API
    public static UserApi getUserApi() {
        return getRetrofitInstance().create(UserApi.class);
    }

    public static ResultsApi getResultsApi() {
        return getRetrofitInstance().create(ResultsApi.class);
    }


}
