package com.zh0glikk.lab3;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class DesEncryptor {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private final Cipher encoder;
    private final Cipher decoder;

    public DesEncryptor(SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        encoder = Cipher.getInstance("DES");
        decoder = Cipher.getInstance("DES");

        encoder.init(Cipher.ENCRYPT_MODE, key);
        decoder.init(Cipher.DECRYPT_MODE, key);
    }

    public String encrypt(String str) throws IllegalBlockSizeException, BadPaddingException {
        byte[] utf8 = str.getBytes(StandardCharsets.UTF_8);
        byte[] enc = encoder.doFinal(utf8);

        return bytesToHex(enc);
    }

    public String decrypt(String str) throws IllegalBlockSizeException, BadPaddingException {
        byte[] dec = hexToBytes(str);
        byte[] utf8 = decoder.doFinal(dec);

        return new String(utf8, StandardCharsets.UTF_8);
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }

        return new String(hexChars);
    }

    public static byte[] hexToBytes(String hex) {
        int l = hex.length();
        byte[] data = new byte[l / 2];

        for (int i = 0; i < l; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }

        return data;
    }
}
