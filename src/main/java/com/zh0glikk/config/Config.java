package com.zh0glikk.config;

import java.util.Objects;

public class Config {
    public static int passwordLength = 10;

    public static String currentUserName;
    public static String currentUserPassword;

    public static final String dataPath = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource("data.txt")).getPath();

}
