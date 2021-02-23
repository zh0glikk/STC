package com.zh0glikk.lab3;

import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class DesEncryptorTest {

    private String message = "Hello world";
    private String secretKey = "12345678";

    private String cipherText = "7A5E6B32A1FD82425D82E3681C83BB77";
    private String openText = "";

    @Test
    void encrypt() {
        byte[] b = secretKey.getBytes(Charset.forName("UTF-8"));
        SecretKey firstKey = new SecretKeySpec(b, 0, b.length, "DES");

        DesEncryptor desEncryptor = null;

        try {
            desEncryptor = new DesEncryptor(firstKey);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        String result = "";
        try {
            result = desEncryptor.encrypt(message);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        assertEquals(result, cipherText);
    }

    @Test
    void decrypt() {
        byte[] b = secretKey.getBytes(Charset.forName("UTF-8"));
        SecretKey firstKey = new SecretKeySpec(b, 0, b.length, "DES");

        DesEncryptor desEncryptor = null;

        try {
            desEncryptor = new DesEncryptor(firstKey);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        try {
            openText = desEncryptor.decrypt(cipherText);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        assertEquals(openText, message);
    }
}