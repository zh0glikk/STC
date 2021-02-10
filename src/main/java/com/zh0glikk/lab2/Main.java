package com.zh0glikk.lab2;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Main {
    private static final String USERNAME = System.getProperty("user.name");
    private static final String COMPUTER_NAME = System.getProperty("os.name");
    private static final double WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final double HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

    private static final int MOUSE_BUTTONS = java.awt.MouseInfo.getNumberOfButtons();

    private static final int DRIVE_SPACE = (int) (new File(".").getTotalSpace() / (1024 * 1024 * 1024));
    private static final File[] DRIVES = File.listRoots();


    public static void main(String[] args) {
        System.out.println(System.getProperties());

        System.out.println(USERNAME);
        System.out.println(COMPUTER_NAME);
        System.out.println(WIDTH);
        System.out.println(HEIGHT);

        System.out.println(MOUSE_BUTTONS);
        System.out.println(DRIVE_SPACE);

        for ( File file : DRIVES ) {
            System.out.println(file);
        }

    }
}
