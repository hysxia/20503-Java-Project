package com.gamechess.chessboard;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button btnLogin;
    private Button btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize input fields and buttons (make sure these IDs exist in your layout XML)
        editTextUsername = findViewById(R.id.et_username);
        editTextPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnReturn = findViewById(R.id.btn_return);

        // Set click listener for the login button
        btnLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if(username.isEmpty() || password.isEmpty()){
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call the login API
            UserService userService = new UserService();
            userService.login(username, password, new UserService.UserCallback<User>(){
                @Override
                public void onSuccess(User user) {
                    // Show success popup instead of Toast
                    new android.app.AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Login Successful!")
                            .setMessage("Welcome back :)")
                            .setPositiveButton("OK", (dialog, which) -> {
                                Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                                UserSession.setLoggedInUser(user);
                                startActivity(intent);
                                finish();
                            })
                            .setCancelable(false)
                            .show();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });


        // Return button navigates back to the start screen
        btnReturn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, StartScreenActivity.class);
            startActivity(intent);
            finish();
        });
    }
}