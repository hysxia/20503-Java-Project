package com.gamechess.chessboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class StartScreenActivity extends AppCompatActivity {


    private Button registerButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen_activity);

        // Initialize the buttons

        registerButton = findViewById(R.id.btn_register);
        loginButton = findViewById(R.id.btn_login);

        // Set the click listeners for each button

        registerButton.setOnClickListener(v -> goToRegister());
        loginButton.setOnClickListener(v -> goToLogin());
    }


    // Method to navigate to RegisterActivity
    public void goToRegister() {
        Intent intent = new Intent(StartScreenActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    // Method to navigate to LoginActivity
    public void goToLogin() {
        Intent intent = new Intent(StartScreenActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
