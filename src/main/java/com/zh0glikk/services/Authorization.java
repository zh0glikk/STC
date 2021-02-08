package com.zh0glikk.services;

import com.zh0glikk.config.Config;
import com.zh0glikk.exceptions.AdminAuthorization;
import com.zh0glikk.exceptions.BlockedUser;
import com.zh0glikk.exceptions.UserAuthorization;
import com.zh0glikk.exceptions.WrongLoginData;
import com.zh0glikk.models.User;

import java.util.List;

public class Authorization {
    private User user;

    public Authorization(User user) {
        this.user = user;
    }

    public void authorize() throws Throwable {
        User admin = User.get("ADMIN");

        if ( user.isBlocked() ) {
            throw new BlockedUser();
        } else if ( user.getUserName().equals(admin.getUserName()) &&
             user.getPassword().equals(admin.getPassword()) ) {
            Config.currentUserName = admin.getUserName();
            Config.currentUserPassword = admin.getPassword();
            throw new AdminAuthorization();
        } else if ( isCorrectLoginData(user.getUserName(), user.getPassword()) ) {
            Config.currentUserName = user.getUserName();
            Config.currentUserPassword = user.getPassword();
            throw new UserAuthorization();
        } else {
            throw new WrongLoginData();
        }

    }

    private boolean isCorrectLoginData(String login, String password) {
        List<User> users = User.all();

        for ( User user : users ) {
            if ( this.user.equals(user) ) {
                return true;
            }
        }
        return false;
    }
}
