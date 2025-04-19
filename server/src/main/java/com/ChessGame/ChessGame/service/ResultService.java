package com.ChessGame.ChessGame.service;

import com.ChessGame.ChessGame.domain.Results;
import com.ChessGame.ChessGame.domain.User;
import com.ChessGame.ChessGame.repository.ResultRepository;
import com.ChessGame.ChessGame.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private UserRepository userRepository;
    //Create a new result
    public Results createResult(int gameNumber, String whiteUsername, String blackUsername, String result) {
        User white = userRepository.findByUsername(whiteUsername);
        User black = userRepository.findByUsername(blackUsername);

        Results results = new Results();
        results.setGameNumber(gameNumber);
        results.setWhitePlayer(white);
        results.setBlackPlayer(black);
        results.setResult(result);

        return resultRepository.save(results);
    }
    //Find result by the gamenumber
    public Results getResultByGameNumber(long gameNumber) {
        Results result = resultRepository.findByGameNumber(gameNumber);

        return result;
    }
    // Find all the results for a user
    public List<Results> getResultsForUser(String username) {
        return resultRepository.findByWhitePlayerUsernameOrBlackPlayerUsername(username, username);
    }
}