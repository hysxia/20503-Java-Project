package com.gamechess.chessboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private EditText editTextPasswordAgain;
    private Button btnRegister;
    private Button btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize input fields and buttons (ensure these IDs exist in your XML layout)
        editTextUsername = findViewById(R.id.et_username);
        editTextEmail = findViewById(R.id.et_email);
        editTextPassword = findViewById(R.id.et_password);
        editTextPasswordAgain = findViewById(R.id.et_password_again);
        btnRegister = findViewById(R.id.btn_register);
        btnReturn = findViewById(R.id.btn_return);

        // Set click listener for the register button
        btnRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String password_again = editTextPasswordAgain.getText().toString().trim();



            if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
                Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!password.equals(password_again)){
                Toast.makeText(RegisterActivity.this, "Passwords don't match, please enter the passwords again", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(RegisterActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call the register API
            new UserService().register(username, email, password, new UserService.UserCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    // Show success popup instead of Toast
                    new android.app.AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("Registration Successful!")
                            .setMessage("Your account has been created. You can now log in.")
                            .setPositiveButton("OK", (dialog, which) -> {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .setCancelable(false) // Prevent dismissing by clicking outside
                            .show();
                }


                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Return button navigates back to the start screen
        btnReturn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, StartScreenActivity.class);
            startActivity(intent);
            finish();
        });
    }
}