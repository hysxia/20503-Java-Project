package com.gamechess.chessboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    private TextView gameResults;
    private ProgressBar progressBar;
    private Button btnReturn;
    private ResultsService resultsService;
    private TextView winsText, lossesText, drawsText;
    private UserService userService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        gameResults = findViewById(R.id.game_results);
        progressBar = findViewById(R.id.progress_bar);
        btnReturn = findViewById(R.id.btn_return);
        winsText = findViewById(R.id.winsText);
        lossesText = findViewById(R.id.lossesText);
        drawsText = findViewById(R.id.drawsText);
        resultsService = new ResultsService();
        userService = new UserService();
        // Get username from intent
        String username = UserSession.getUsername();
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Error: No username provided", Toast.LENGTH_SHORT).show();
            return;
        }

        fetchResults(username);

        // Back button to return to HomeScreenActivity
        btnReturn.setOnClickListener(v -> {
            Intent intent = new Intent(ResultsActivity.this, HomeScreenActivity.class);
            startActivity(intent);
            finish();
        });
    }
    //getting all the result for the user
    private void fetchResults(String username) {
        progressBar.setVisibility(View.VISIBLE);
        resultsService.getResultsForUser(username, new ResultsService.ResultsCallback<List<Results>>() {
            @Override
            public void onSuccess(List<Results> results) {
                progressBar.setVisibility(View.GONE);
                if (results.isEmpty()) {
                    gameResults.setText("No results found.");
                } else {
                    StringBuilder resultText = new StringBuilder();
                    for (Results result : results) {


                        resultText.append("Game: ").append(result.getGameNumber()).append("\n")
                                .append("White: ").append(result.getWhitePlayer().getUsername()).append(" vs Black: ")
                                .append(result.getBlackPlayer().getUsername()).append("\n")
                                .append("Result: ").append(result.getResult()).append("\n\n");
                    }
                    gameResults.setText(resultText.toString());
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ResultsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });//getting the wins for the user
        userService.getWins(username, new UserService.UserCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {

                winsText.setText(String.valueOf(result));
            }

            @Override
            public void onFailure(String errorMessage) {

                winsText.setText("0");
            }
        });

        //getting the draws for the user
        userService.getDraws(username, new UserService.UserCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {

                drawsText.setText(String.valueOf(result));
            }

            @Override
            public void onFailure(String errorMessage) {
                drawsText.setText("0");
            }
        });
        //getting the losses for the user
        userService.getLoses(username, new UserService.UserCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {

                lossesText.setText(String.valueOf(result));
            }

            @Override
            public void onFailure(String errorMessage) {
                lossesText.setText("0");
            }
        });
    }
}
