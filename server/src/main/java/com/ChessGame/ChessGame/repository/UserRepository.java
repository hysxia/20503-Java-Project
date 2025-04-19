package com.ChessGame.ChessGame.repository;
import com.ChessGame.ChessGame.domain.*;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    User findByUsername(String username);
    User findByEmail(String email);
}
