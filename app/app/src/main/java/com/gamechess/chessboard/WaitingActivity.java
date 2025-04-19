package com.gamechess.chessboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class WaitingActivity extends AppCompatActivity {

    private TextView waitingText;
    private Handler handler = new Handler();
    private Runnable checkReadyRunnable;
    private GameService gameService;
    private Long gameId;
    private static final String TAG = "WaitingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        waitingText = findViewById(R.id.waiting_text);
        waitingText.setText("Creating or joining a game...");
        gameService = new GameService();

        // Call joinOrCreateGame from the backend
        String username = UserSession.getUsername();
        gameService.joinOrCreateGame(username, new GameService.GameCallback<Game>() {
            @Override
            public void onSuccess(Game game) {
                gameId = game.getId();
                Log.d(TAG, "joinOrCreateGame successful. Game ID: " + gameId);
                waitingText.setText("Waiting for an opponent to join...");

                // Start polling for the game readiness
                startPollingGameReady();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Error in joinOrCreateGame: " + errorMessage);
                Toast.makeText(WaitingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startPollingGameReady() {
        checkReadyRunnable = new Runnable() {
            @Override
            public void run() {
                // Use the isGameReady API to check if the game is ready.
                gameService.isGameReady(gameId, new GameService.GameCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isReady) {
                        if (isReady) {
                            // Once ready, start MainActivity and pass along the game ID.
                            Intent intent = new Intent(WaitingActivity.this, MainActivity.class);
                            intent.putExtra("GAME_ID", gameId);
                            startActivity(intent);
                            finish();
                        } else {
                            handler.postDelayed(checkReadyRunnable, 1000);
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        handler.postDelayed(checkReadyRunnable, 1000);
                    }
                });
            }
        };
        handler.post(checkReadyRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(checkReadyRunnable);
    }
}
