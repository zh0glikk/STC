package com.zh0glikk.services;

import com.zh0glikk.config.Config;
import com.zh0glikk.exceptions.UserRegistration;
import com.zh0glikk.exceptions.WrongRegistrationData;
import com.zh0glikk.models.User;

import java.util.List;

public class Registration {
    private User user;

    public Registration(User user) {
        this.user = user;
    }

    public void registrate() throws WrongRegistrationData, UserRegistration {
        if ( isLoginAvailable(user.getUserName()) && isCorrectPassword(user.getPassword()) ) {
            User.add(this.user);
            throw new UserRegistration();
        } else {
            throw new WrongRegistrationData();
        }
    }

    private boolean isLoginAvailable(String login) {
        List<User> users = User.all();

        for ( User user : users ) {
            if ( login.equals(user.getUserName()) ) {
                return false;
            }
        }

        return true;
    }

    private boolean isCorrectPassword(String password) {
        return password.length() > Config.passwordLength;
    }
}
