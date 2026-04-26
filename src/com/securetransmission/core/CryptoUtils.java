package com.securetransmission.core;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoUtils {
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12;
    private static final int ITERATIONS = 65_536;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int AUTH_TAG_LENGTH_BITS = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private CryptoUtils() {
    }

    public static EncryptedData encrypt(String plainText, char[] password)
            throws SecureTransmissionException {
        validatePlainText(plainText);
        validatePassword(password);

        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        SECURE_RANDOM.nextBytes(iv);

        PBEKeySpec keySpec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH_BITS);

        try {
            SecretKey secretKey = deriveKey(keySpec);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(AUTH_TAG_LENGTH_BITS, iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return new EncryptedData(salt, iv, cipherText);
        } catch (GeneralSecurityException ex) {
            throw new SecureTransmissionException("Unable to encrypt the message.", ex);
        } finally {
            keySpec.clearPassword();
        }
    }

    public static String decrypt(EncryptedData encryptedData, char[] password)
            throws SecureTransmissionException {
        validatePassword(password);

        PBEKeySpec keySpec = new PBEKeySpec(password, encryptedData.salt(), ITERATIONS, KEY_LENGTH_BITS);

        try {
            SecretKey secretKey = deriveKey(keySpec);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    new GCMParameterSpec(AUTH_TAG_LENGTH_BITS, encryptedData.iv()));
            byte[] plainBytes = cipher.doFinal(encryptedData.cipherText());
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException ex) {
            throw new SecureTransmissionException(
                    "Unable to decrypt the message. The password may be incorrect or the image may be damaged.",
                    ex);
        } finally {
            keySpec.clearPassword();
        }
    }

    public static int getSaltLength() {
        return SALT_LENGTH;
    }

    public static int getIvLength() {
        return IV_LENGTH;
    }

    private static SecretKey deriveKey(PBEKeySpec keySpec) throws GeneralSecurityException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = secretKeyFactory.generateSecret(keySpec).getEncoded();

        try {
            return new SecretKeySpec(keyBytes, "AES");
        } finally {
            Arrays.fill(keyBytes, (byte) 0);
        }
    }

    private static void validatePlainText(String plainText) throws SecureTransmissionException {
        if (plainText == null || plainText.isBlank()) {
            throw new SecureTransmissionException("The message cannot be empty.");
        }
    }

    private static void validatePassword(char[] password) throws SecureTransmissionException {
        if (password == null || password.length < 8) {
            throw new SecureTransmissionException("The password must contain at least 8 characters.");
        }
    }
}
