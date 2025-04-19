package com.ChessGame.ChessGame.repository;
import com.ChessGame.ChessGame.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GameRepository extends JpaRepository<Game, Long>{

}
