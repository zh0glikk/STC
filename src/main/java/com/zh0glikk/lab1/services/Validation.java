package com.zh0glikk.lab1.services;

public class Validation {

    public static boolean isCyrillic(char a) {
        return Character.UnicodeBlock.of(a) == Character.UnicodeBlock.CYRILLIC;
    }

    public static boolean isLatin(char a) {
        return Character.UnicodeBlock.of(a) == Character.UnicodeBlock.BASIC_LATIN;
    }

    public static boolean isPunctuationMark(char a) {
        boolean result = false;

        char[] marks = new char[] {',', '.', '_', '/', '|', '!', '?'};

        for ( char s : marks ) {
            if ( a == s ) {
                result = !result;
                break;
            }
        }

        return result;
    }

    public static boolean validate(String str) {
        boolean result = true;

        for ( char s : str.toCharArray() ) {
            if ( !(isCyrillic(s) || isLatin(s) || isPunctuationMark(s)) ) {
                result = !result;
                break;
            }
        }
        return result;
    }
}
