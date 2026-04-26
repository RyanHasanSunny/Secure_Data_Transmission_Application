package com.securetransmission.core;

import java.util.Arrays;

public record EncryptedData(byte[] salt, byte[] iv, byte[] cipherText) {
    public EncryptedData {
        salt = Arrays.copyOf(salt, salt.length);
        iv = Arrays.copyOf(iv, iv.length);
        cipherText = Arrays.copyOf(cipherText, cipherText.length);
    }
}
