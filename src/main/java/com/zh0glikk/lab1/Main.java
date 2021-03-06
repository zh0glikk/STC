package com.zh0glikk.lab1;

import com.zh0glikk.lab1.config.Config;
import com.zh0glikk.lab1.exceptions.*;
import com.zh0glikk.lab1.models.User;
import com.zh0glikk.lab1.services.Authorization;
import com.zh0glikk.lab1.services.Registration;
import com.zh0glikk.lab1.services.Validation;
import com.zh0glikk.lab1.states.State;
import com.zh0glikk.lab2.services.ComputerDataGather;
import com.zh0glikk.lab3.DesEncryptor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Scanner;
import java.util.prefs.Preferences;

public class Main{
    private static State currentState = State.Menu;

    private static int attemptsBeforeExit = 3;

    public static void main(String[] args) {
        String computerData = ComputerDataGather.getData();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your secret key: ");
        int secretkey = scanner.nextInt();

        int result = (int) Math.pow(computerData.hashCode(), secretkey) % computerData.hashCode();

        if ( result != Preferences.userRoot().node("zhoglik").getInt("SIGNATURE", 0) ) {
            System.out.println("Wrong secret key");
            System.exit(1);
        }

        decryptData();
        createFile();

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
                    System.out.print("Menu Page\n1.Sign in.\n2.Sign up.\n3.Exit\n");
                    try {
                        state = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (state.equals("1")) {
                        currentState = State.SignIn;
                    } else if (state.equals("2")) {
                        currentState = State.SignUp;
                    } else if (state.equals("3")) {
                        encryptData();

                        System.gc();

                        File file = new File(Config.dataPath);
                        if (file.delete()) {
                            System.out.println(Config.dataPath + "File deleted from Project root directory");
                        } else {
                            System.out.println("File not deleted");
                        }

                        System.exit(1);
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

                            User.updateBlocked( log, !Objects.requireNonNull(User.get(log)).isBlocked());
                        }
                        case "5" -> {
                            System.out.print("Enter user's login to block/unblock: ");
                            String log = "";
                            try {
                                log = reader.readLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            User.updatePasswordPatternEnabled(log, !Objects.requireNonNull(User.get(log)).isPasswordPatternEnabled());
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
                password));

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
            User.updatePassword(Config.currentUserName, newPassword);
            state = State.User;
            replyInfo += "Password Changed";
        }

        System.out.println(replyInfo);

        return state;
    }

    private static void createFile() {
        File myObj = new File(Config.dataPath);
        myObj.deleteOnExit();
        System.out.println(myObj.getAbsolutePath());
        try {
            Scanner myReader = new Scanner(myObj);
            if ( myReader.hasNextLine() ) {
                return;
            }
        } catch (FileNotFoundException e) {
            System.out.println("First run. Creating file with data.");
        }

        try (FileWriter writer = new FileWriter(Config.dataPath, false)) {
            writer.write("ADMIN::false:true");
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static String readFile(String path) {
        File myObj = new File(path);
        String result = "";

        try {
            Scanner reader = new Scanner(myObj);
            if ( reader.hasNextLine() ) {
                result += reader.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void writeFile(String path, String text) {
        try (FileWriter writer = new FileWriter(path, false)) {
            writer.write(text);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void decryptData() {
        File myObj = new File(Config.cipherPath);

        try {
            Scanner myReader = new Scanner(myObj);
            if ( myReader.hasNextLine() ) {
                Scanner scanner = new Scanner(System.in);

                System.out.println("Enter your session key: ");

                String key = scanner.next();

                byte[] b = key.getBytes(StandardCharsets.UTF_8);
                SecretKey firstKey = new SecretKeySpec(b, 0, b.length, "DES");

                DesEncryptor desEncryptor = new DesEncryptor(firstKey);

                String cipherText = readFile(Config.cipherPath);

                String openText =  desEncryptor.decrypt(cipherText);

                writeFile(Config.dataPath, openText);
            }
        } catch (FileNotFoundException e) {
            return;
        } catch (BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    private static void encryptData() {
        String plainText = readFile(Config.dataPath);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your session key: ");

        String key = scanner.next();

        byte[] b = key.getBytes(StandardCharsets.UTF_8);
        SecretKey firstKey = new SecretKeySpec(b, 0, b.length, "DES");

        DesEncryptor desEncryptor;
        String cipherText = null;

        try {
            desEncryptor = new DesEncryptor(firstKey);
            cipherText = desEncryptor.encrypt(plainText);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        writeFile(Config.cipherPath, cipherText);
    }

}
