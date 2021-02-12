package com.zh0glikk.lab2.services;

import com.zh0glikk.lab2.config.Config;

import java.io.*;
import java.nio.channels.FileChannel;

public class Installer {

    public void install(String path) throws IOException {
        FileChannel source = new FileInputStream(Config.APPLICATION).getChannel();
        FileChannel dest = new FileOutputStream(new File(path + "STC_l1.jar")).getChannel();
        try {
            source.transferTo(0, source.size(), dest);
        } finally {
            source.close();
            dest.close();
        }
        System.out.println("Installation completed");
    }

}