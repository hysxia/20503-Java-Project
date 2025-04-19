package com.gamechess.chessboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;



public class HomeScreenActivity extends AppCompatActivity {
    private Button startGameButton;

    private Button resultsButton;
    private String username;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_activity);
        // Retrieve username from intent
        username = UserSession.getUsername();

        // Initialize the buttons
        startGameButton = findViewById(R.id.btn_start_game);
        resultsButton = findViewById(R.id.btn_user_results);


        // Set the click listeners for each button
        startGameButton.setOnClickListener(v -> startGame());
        resultsButton.setOnClickListener(v -> showStats());

    }
    //navigating to the waiting screen to start a game
    public void startGame() {
        Intent intent = new Intent(HomeScreenActivity.this, WaitingActivity.class);
        startActivity(intent);
    }
    //navigating to the user stats screen
    public void showStats(){
        Intent intent = new Intent(HomeScreenActivity.this, ResultsActivity.class);
         // Pass username to ResultsActivity
        startActivity(intent);
    }


}
