package com.ChessGame.ChessGame;

import com.ChessGame.ChessGame.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // Check if "alice" exists; if not, register her.
        if (userService.findByUsername("alice") == null) {
            userService.registerUser("alice", "alice@example.com", "password1");
            System.out.println("User 'alice' created.");
        } else {
            System.out.println("User 'alice' already exists.");
        }

        // Check if "bob" exists; if not, register him.
        if (userService.findByUsername("bob") == null) {
            userService.registerUser("bob", "bob@example.com", "password2");
            System.out.println("User 'bob' created.");
        } else {
            System.out.println("User 'bob' already exists.");
        }
    }
}
