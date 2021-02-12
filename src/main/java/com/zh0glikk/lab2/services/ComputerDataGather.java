package com.zh0glikk.lab2.services;

import java.awt.*;
import java.io.File;

public class ComputerDataGather {
    public static final String USERNAME = System.getProperty("user.name");
    private static final String COMPUTER_NAME = System.getProperty("os.name");

    private static final double WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final double HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

    private static final int MOUSE_BUTTONS = java.awt.MouseInfo.getNumberOfButtons();

    private static final int DRIVE_SPACE = (int) (new File(".").getTotalSpace() / (1024 * 1024 * 1024));
    private static final File[] DRIVES = File.listRoots();

    public static String getData() {
        StringBuilder data = new StringBuilder();

        data.append("USERNAME:").append(USERNAME).append("\n");
        data.append("COMPUTER_NAME:").append(COMPUTER_NAME).append("\n");
        data.append("WIDTH:").append(WIDTH).append("\n");
        data.append("HEIGHT:").append(HEIGHT).append("\n");
        data.append("MOUSE_BUTTONS:").append(MOUSE_BUTTONS).append("\n");
        data.append("DRIVE_SPACE:").append(DRIVE_SPACE).append("\n");

        data.append("DRIVES:");
        for ( File file : DRIVES ) {
            data.append(file);
        }
        data.append("\n");

        return data.toString();
    }



}
