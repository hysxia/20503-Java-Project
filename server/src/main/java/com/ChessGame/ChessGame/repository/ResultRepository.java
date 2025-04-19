package com.ChessGame.ChessGame.repository;

import com.ChessGame.ChessGame.domain.Results;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResultRepository extends JpaRepository<Results, Long> {

    List<Results> findByWhitePlayerUsernameOrBlackPlayerUsername(String whiteUsername, String blackUsername);
    Results findByGameNumber(long gameNumber);
}
