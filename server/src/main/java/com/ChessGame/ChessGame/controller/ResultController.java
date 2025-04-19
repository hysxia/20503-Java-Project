package com.ChessGame.ChessGame.controller;

import com.ChessGame.ChessGame.domain.Results;
import com.ChessGame.ChessGame.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    //Connection to the client to get a result of a game
    @GetMapping("/{gameNumber}")
    public Results getResultByGameNumber(@PathVariable int gameNumber) {

        return resultService.getResultByGameNumber(gameNumber);
    }

    //Connection to the client to create a new result in the database
    @PostMapping("/create")
    public Results createResult(@RequestParam int gameNumber, @RequestParam String whiteUserName, @RequestParam String blackUserName, @RequestParam String result) {

        return resultService.createResult(gameNumber, whiteUserName, blackUserName, result);
    }

    //Connection to the client to return all the results of the specific user
    @GetMapping("/user/{username}")
    public List<Results> getResultsForUser(@PathVariable String username) {

        return resultService.getResultsForUser(username);
    }
}