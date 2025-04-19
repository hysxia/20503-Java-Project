package com.gamechess.chessboard;

import com.gamechess.chessboard.pieces.Piece;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class GameService {
    private GameApi gameApi;

    public GameService(){
        gameApi = ServerConnection.getGameApi();
    }

    //handles the joinOrCreateGame POST from the server side.
    public void joinOrCreateGame(String username, final GameCallback<Game> callback) {
        Call<Game> call = gameApi.joinOrCreateGame(username);
        call.enqueue(new Callback<Game>() {
            @Override
            public void onResponse(Call<Game> call, Response<Game> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Failed to join or create game");
                }
            }
            @Override
            public void onFailure(Call<Game> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    //handles the isGameReady POST from the server
    public void isGameReady(Long gameId, final GameCallback<Boolean> callback) {
        Call<Boolean> call = gameApi.isGameReady(gameId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Failed to check game readiness");
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    public interface GameCallback<T> {
        void onSuccess(T result);
        void onFailure(String errorMessage);
    }
    //handles the makeMove POST in the server side.
    public void makeMove(long gameId, String username, int startRow, int startCol, int destRow, int destCol, final GameCallback<String> callback) {
        Call<ResponseBody> call = gameApi.makeMove(gameId, username, startRow, startCol, destRow, destCol);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String result = response.body().string();
                        callback.onSuccess(result);
                    } catch (IOException e) {
                        callback.onFailure("Failed to parse response: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("Failed to make move");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    //handles the ProposeDraw POST from the server side
    public void proposeDraw(Long gameId, String username, final GameCallback<String> callback) {
        Call<Map<String, String>> call = gameApi.proposeDraw(gameId, username);
        call.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                if (response.isSuccessful()) {
                    Map<String,String> body = response.body();
                    if (body != null) {
                        String result = body.get("result");
                        callback.onSuccess(result);
                    } else {
                        callback.onFailure("Response body is null.");
                    }
                } else {
                    callback.onFailure("Failed to propose draw. Server responded with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String,String>> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    //handles the resignGame POST from the server
    public void resignGame(long gameId, String username, final GameCallback<String> callback) {
        //  parse as Map<String,String>
        Call<Map<String,String>> call = gameApi.resignGame(gameId, username);

        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String,String> body = response.body();

                    String result = body.get("result");
                    callback.onSuccess(result);
                } else {
                    callback.onFailure("Failed to resign");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    //handles the declineDraw POST from the server
    public void declineDraw(long gameId, String username, final GameCallback<String> callback) {

        Call<Map<String,String>> call = gameApi.declineDraw(gameId, username);

        call.enqueue(new Callback<Map<String,String>>() {
            @Override
            public void onResponse(Call<Map<String,String>> call, Response<Map<String,String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String,String> responseBody = response.body();

                    String result = responseBody.get("result");

                    callback.onSuccess(result);

                } else {
                    callback.onFailure("Failed to decline draw");
                }
            }

            @Override
            public void onFailure(Call<Map<String,String>> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    //handles the promotePawn POST from the server
    public void promotePawn(long gameId, String username, String promotionPieceType, int pawnRow, int pawnCol, final GameCallback<String> callback) {
        Call<ResponseBody> call = gameApi.promotePawn(gameId, username, promotionPieceType, pawnRow, pawnCol);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        callback.onSuccess(result);
                    } else {
                        String errorResult = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        callback.onFailure("Server error: " + errorResult);
                    }
                } catch (IOException e) {
                    callback.onFailure("Parsing error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    //handles the getGame POST from the server
    public void getGame(Long gameId, final GameCallback<Game> callback) {
        Call<Game> call = gameApi.getGame(gameId);
        call.enqueue(new Callback<Game>() {
            @Override
            public void onResponse(Call<Game> call, Response<Game> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Failed to fetch game state");
                }
            }

            @Override
            public void onFailure(Call<Game> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }
    //handles the getLegalMoves POST from the server
    public void getLegalMoves(Long gameId, int row, int col, boolean forWhite, final GameCallback<List<Square>> callback) {
        Call<List<Square>> call = gameApi.getLegalMoves(gameId, row, col, forWhite);
        call.enqueue(new Callback<List<Square>>() {
            @Override
            public void onResponse(Call<List<Square>> call, Response<List<Square>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    if(!response.isSuccessful()){
                        callback.onFailure("Response isn't succefull");
                    }
                    if(response.body()== null)
                        callback.onFailure("Body is null");
                    callback.onFailure("Failed to fetch legal moves");
                }
            }

            @Override
            public void onFailure(Call<List<Square>> call, Throwable t) {
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }


}
