package com.zh0glikk.lab1.models;


import com.zh0glikk.lab1.config.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;


public class User {
    private String userName;
    private String password;
    private boolean isBlocked;
    private boolean isPasswordPatternEnabled;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public boolean isPasswordPatternEnabled() {
        return isPasswordPatternEnabled;
    }

    public void setPasswordPatternEnabled(boolean passwordPatternEnabled) {
        isPasswordPatternEnabled = passwordPatternEnabled;
    }

    public User(String userName, String password, boolean isBlocked, boolean isPasswordPatternEnabled) {
        this.userName = userName;
        this.password = password;
        this.isBlocked = isBlocked;
        this.isPasswordPatternEnabled = isPasswordPatternEnabled;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;

        this.isBlocked = false;
        this.isPasswordPatternEnabled = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userName, user.userName) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password);
    }

    public static List<User> all() {
        List<User> result = new ArrayList<>();

        try {
            File myObj = new File(Config.dataPath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String[] line = myReader.nextLine().split(":");

                boolean blocked = false;
                boolean patternEnabled = false;

                if ( line[2].equals("true") ) {
                    blocked = true;
                }
                if ( line[3].equals("true") ) {
                    patternEnabled = true;
                }

                User user = new User(line[0], line[1], blocked, patternEnabled);

                result.add(user);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return result;
    }

    public static User get(String login) {
        List<User> users = User.all();

        for ( User user : users ) {
            if ( user.getUserName().equals(login) ) {
                return user;
            }
        }
        return null;
    }

    public static void update(String login, String password) {
        List<User> users = User.all();
        String result = "";

        for ( User user : users ) {
            if ( user.userName.equals(login) ) {
                user.password = password;
            }
        }

        for ( User user : users ) {
            result += user.toString();
        }


        try (FileWriter writer = new FileWriter(Config.dataPath, false)) {
            writer.write(result);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void update(String login, boolean isBlocked) {
        List<User> users = User.all();
        String result = "";

        for ( User user : users ) {
            if ( user.userName.equals(login) ) {
                user.isBlocked = isBlocked;
            }
        }

        for ( User user : users ) {
            result += user.toString();
        }


        try (FileWriter writer = new FileWriter(Config.dataPath, false)) {
            writer.write(result);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void _update(String login, boolean isPasswordPatternEnabled) {
        List<User> users = User.all();
        String result = "";

        for ( User user : users ) {
            if ( user.userName.equals(login) ) {
                user.isPasswordPatternEnabled = isPasswordPatternEnabled;
            }
        }

        for ( User user : users ) {
            result += user.toString();
        }


        try (FileWriter writer = new FileWriter(Config.dataPath, false)) {
            writer.write(result);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void add(User user) {
        try (FileWriter writer = new FileWriter(Config.dataPath, true)) {
            writer.write(user.toString());
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public String toString() {
        return this.userName + ":"
                + this.password + ":"
                + this.isBlocked + ":"
                + this.isPasswordPatternEnabled + "\n";
    }
}