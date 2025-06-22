package com.kca_2sem_project.digitalob.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final byte[] SECRET_KEY = "d2cbe74a1b5f745e4aef2ff9f6b6b643".getBytes();

    public static String encrypt(String data) {
        try {
            SecretKey key = new SecretKeySpec(SECRET_KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            String encryptedString = Base64.getEncoder().encodeToString(encryptedData);
            return encryptedString;
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting data", e);
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            SecretKey key = new SecretKeySpec(SECRET_KEY, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            String decryptedString = new String(cipher.doFinal(decodedData));
            return decryptedString;
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting data", e);
        }
    }

}

