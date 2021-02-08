package com.zh0glikk;

import com.zh0glikk.config.Config;
import com.zh0glikk.exceptions.*;
import com.zh0glikk.models.User;
import com.zh0glikk.services.Authorization;
import com.zh0glikk.services.Registration;
import com.zh0glikk.services.Validation;
import com.zh0glikk.states.State;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main{
    private static State currentState = State.Menu;

    private static int attemptsBeforeExit = 3;

    public static void main(String[] args) {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));

        String login = "";
        String password = "";
        String newPassword = "";
        String newPasswordRepeated = "";
        String state = "";

        while (true) {
            switch (currentState) {
                case Menu -> {
                    System.out.print("Menu Page\n1.Sign in.\n2.Sign up.\n");
                    try {
                        state = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (state.equals("1")) {
                        currentState = State.SignIn;
                    } else if (state.equals("2")) {
                        currentState = State.SignUp;
                    }
                }
                case User -> {
                    System.out.println("Logined as " + Config.currentUserName);
                    System.out.print("1.Change password.\n2.Exit\n");
                    try {
                        state = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (state.equals("2")) {
                        currentState = State.SignIn;
                        break;
                    }
                    try {
                        System.out.print("Old password: ");
                        password = reader.readLine();

                        System.out.print("New password: ");
                        newPassword = reader.readLine();

                        System.out.print("Repeat new password: ");
                        newPasswordRepeated = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    currentState = changeUserPassword(password, newPassword, newPasswordRepeated);
                }
                case Admin -> {
                    System.out.println("Admin panel");
                    System.out.println("1.Change password.\n2.Exit\n3.Get all users.\n4.Block/unblock user.\n5.Set/unset password pattern.");
                    try {
                        state = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    switch (state) {
                        case "1" -> {
                            try {
                                System.out.print("Old password: ");
                                password = reader.readLine();

                                System.out.print("New password: ");
                                newPassword = reader.readLine();

                                System.out.print("Repeat new password: ");
                                newPasswordRepeated = reader.readLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            changeUserPassword(password, newPassword, newPasswordRepeated);
                        }
                        case "2" -> currentState = State.SignIn;
                        case "3" -> System.out.println(User.all());
                        case "4" -> {
                            System.out.print("Enter user's login to block/unblock: ");
                            String log = "";
                            try {
                                log = reader.readLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            User.update( log, !Objects.requireNonNull(User.get(log)).isBlocked());
                        }
                        case "5" -> {
                            System.out.print("Enter user's login to block/unblock: ");
                            String log = "";
                            try {
                                log = reader.readLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            User._update(log, !Objects.requireNonNull(User.get(log)).isPasswordPatternEnabled());
                        }
                    }
                }
                case SignIn -> {
                    System.out.println("Login page");
                    System.out.print("1.Sign in.\n2.Go to menu.\n");
                    try {
                        state = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (state.equals("2")) {
                        currentState = State.Menu;
                        break;
                    }
                    try {
                        System.out.print("Login: ");
                        login = reader.readLine();

                        System.out.print("Password: ");
                        password = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    currentState = signIn(login, password);
                    if (attemptsBeforeExit == 0) {
                        System.exit(1);
                    }
                }
                case SignUp -> {
                    System.out.println("Registration page");
                    System.out.print("1.Sign up.\n2.Go to menu.\n");
                    try {
                        state = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (state.equals("2")) {
                        currentState = State.Menu;
                        break;
                    }
                    try {
                        System.out.print("Login: ");
                        login = reader.readLine();

                        System.out.print("Password: ");
                        password = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    currentState = signUp(login, password);
                }
            }
        }
    }


    private static State signIn(String login, String password) {
        Authorization authorization = new Authorization(new User(
                login,
                password,
                Objects.requireNonNull(User.get(login)).isBlocked(),
                Objects.requireNonNull(User.get(login)).isPasswordPatternEnabled()
        ));

        State result = State.SignIn;

        attemptsBeforeExit -= 1;

        try {
            authorization.authorize();
        } catch (BlockedUser e) {
            System.out.println("User is blocked. Can't sign in.");
        } catch (UserAuthorization e) {
            result = State.User;
            attemptsBeforeExit = 3;
        } catch (AdminAuthorization e) {
            result = State.Admin;
            attemptsBeforeExit = 3;
        } catch (Throwable e) {
            System.out.println("Wrong data");
        }

        return result;
    }

    private static State signUp(String login, String password) {
        Registration registration = new Registration(new User(login, password));

        State state = State.SignUp;

        try {
            registration.registrate();
        } catch (WrongRegistrationData wrongRegistrationData) {
            state = State.SignUp;
            System.out.println("Wrong data. Try Again.");
        } catch (UserRegistration userRegistration) {
            state = State.SignIn;
            System.out.println("Successfully created new user " + login);
        }

        return state;
    }

    private static State changeUserPassword(String oldPassword, String newPassword, String newPasswordRepeated) {
        String replyInfo = "";
        boolean isRightData = true;
        State state = State.User;
        User user = User.get(Config.currentUserName);

        if ( !oldPassword.equals(Config.currentUserPassword) ) {
            isRightData = false;
            replyInfo += "Wrong old password. ";
        }

        if ( user.isPasswordPatternEnabled() ) {
            if (newPassword.length() < Config.passwordLength && Validation.validate(newPassword) ) {
                isRightData = false;
                replyInfo += "Wrong new password. ";
            }
            if (!newPassword.equals(newPasswordRepeated)) {
                isRightData = false;
                replyInfo += "Passwords don`t equal.";
            }
        }

        if ( isRightData ) {
            User.update(Config.currentUserName, newPassword);
            state = State.User;
            replyInfo += "Password Changed";
        }

        System.out.println(replyInfo);

        return state;
    }


}
