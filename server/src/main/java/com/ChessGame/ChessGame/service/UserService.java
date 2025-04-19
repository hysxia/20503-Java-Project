package com.ChessGame.ChessGame.service;


import com.ChessGame.ChessGame.domain.Results;
import com.ChessGame.ChessGame.domain.User;
import com.ChessGame.ChessGame.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
//server side of the user operations
@Service
public class UserService  {
    @Autowired
    private UserRepository userRepository;

    //funcion to create a new user
    public User createUser(String username, String email, String rawPassword) {


        // create a user
        User user = new User(username, email, rawPassword);
        return user;
    }
    //function to save the user in the database
    public void saveUser(User user) {

        userRepository.save(user);
    }
    // function to register a user
    public User registerUser(String username, String email, String rawPassword) {
        if (userRepository.findByUsername(username) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"USERNAME_EXISTS");
        }
        if (!isValidEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // Duplicate email check
        if (userRepository.findByEmail(email) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"EMAIL_EXISTS");
        }
        User user = createUser(username, email, rawPassword);
        saveUser(user);
        return user;
    }
    //checks if the email is valid
    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(regex);
    }
    //function to check login for the user
    public User login(String userName, String passWord) {
        User user = userRepository.findByUsername(userName);
        //checks that if the user exists or the password is wrong
        if (user == null || !user.getPassword().equals(passWord)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        return user;
    }
    //query to find user by username
    public User findByUsername(String username) {

        return userRepository.findByUsername(username);
    }
    //Get number of wins
    public int getWins(String username) {
        User user = userRepository.findByUsername(username);
        return (user != null) ? user.getWins() : 0;
    }

    // Get number of draws
    public int getDraws(String username) {
        User user = userRepository.findByUsername(username);
        return (user != null) ? user.getDraws() : 0;
    }

    // Get number of losses
    public int getLosses(String username) {
        User user = userRepository.findByUsername(username);
        return (user != null) ? user.getLosses() : 0;
    }
}
