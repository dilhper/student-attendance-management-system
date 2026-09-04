package com.sams.service;

import com.sams.dao.UserDAO;
import com.sams.model.User;

/**
 * Service layer for user authentication.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Attempts to authenticate the user with given credentials.
     * @return the authenticated User, or null if invalid.
     */
    public User login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        return userDAO.authenticate(username.trim(), password);
    }
}
