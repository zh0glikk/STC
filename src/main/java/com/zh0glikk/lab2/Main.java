package com.zh0glikk.lab2;

import com.zh0glikk.lab2.services.ComputerDataGather;
import com.zh0glikk.lab2.services.Installer;

import java.io.IOException;
import java.util.Scanner;
import java.util.prefs.Preferences;

public class Main {
    private static String computerData;

    public static void main(String[] args) {
        computerData = ComputerDataGather.getData();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your secret key: ");
        int secretkey = scanner.nextInt();

        int result = (int) Math.pow(computerData.hashCode(), secretkey) % computerData.hashCode();

        Preferences userPrefs = Preferences.userRoot().node("zhoglik");
        userPrefs.putInt("SIGNATURE", result);


        System.out.println("Enter installation path: ");
        String path = scanner.next();

        Installer installer = new Installer();

        try {
            installer.install(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
