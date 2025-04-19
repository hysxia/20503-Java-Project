package com.gamechess.chessboard;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ResultsService {
    private ResultsApi resultsApi;
    public ResultsService(){
        resultsApi = ServerConnection.getResultsApi();
    }
    // Get a result by game number
    public void getResultByGameNumber(int gameNumber, final ResultsCallback<Results> callback) {
        Call<Results> call = resultsApi.getResultByGameNumber(gameNumber);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Failed to retrieve game result");
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    // Create a new result
    public void createResult(Long gameNumber, String whiteUser, String blackUser, String result, final ResultsCallback<Results> callback) {
        Call<Results> call = resultsApi.createResult(gameNumber, whiteUser, blackUser, result);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Failed to create result");
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }

    // Get all results for a user
    public void getResultsForUser(String username, final ResultsCallback<List<Results>> callback) {
        Call<List<Results>> call = resultsApi.getResultsForUser(username);

        call.enqueue(new Callback<List<Results>>() {
            @Override
            public void onResponse(Call<List<Results>> call, Response<List<Results>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Results> results = response.body();
                    callback.onSuccess(results);
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        callback.onFailure("Failed to retrieve user results: " + errorBody);
                    } catch (IOException e) {
                        callback.onFailure("Failed to retrieve user results: Unknown error");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Results>> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }




    // Define a callback interface
    public interface ResultsCallback<T> {
        void onSuccess(T result);
        void onFailure(String errorMessage);
    }
}