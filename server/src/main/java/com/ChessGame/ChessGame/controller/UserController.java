package com.ChessGame.ChessGame.controller;


import com.ChessGame.ChessGame.domain.User;
import com.ChessGame.ChessGame.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    //Connection to the client for login
    @PostMapping("/login")
    public User login(@RequestParam String username, @RequestParam String password) {

        return userService.login(username, password);
    }
    //Connection to the client for getting the user
    @GetMapping("/{username}")
    public User getUser(@PathVariable String username) {

        return userService.findByUsername(username);
    }
    //Connection to the client for registering
    @PostMapping("/register")
    public User register(@RequestParam String username, @RequestParam String email, @RequestParam String password) {

        return userService.registerUser(username, email, password);
    }
    //Connection to the client to get the number of wins
    @GetMapping("/{username}/wins")
    public int getWins(@PathVariable String username) {
        return userService.getWins(username);
    }

    //Connection to the client to get the number of draws
    @GetMapping("/{username}/draws")
    public int getDraws(@PathVariable String username) {
        return userService.getDraws(username);
    }

    //Connection to the client to get number of losses
    @GetMapping("/{username}/losses")
    public int getLosses(@PathVariable String username) {
        return userService.getLosses(username);
    }


}