package com.securetransmission.core;

import java.nio.ByteBuffer;
import java.util.Arrays;

public record StegoPayload(byte[] salt, byte[] iv, byte[] cipherText) {
    private static final byte[] MAGIC = new byte[] {'S', 'D', 'T', 'A'};
    private static final byte VERSION = 1;

    public StegoPayload {
        salt = Arrays.copyOf(salt, salt.length);
        iv = Arrays.copyOf(iv, iv.length);
        cipherText = Arrays.copyOf(cipherText, cipherText.length);
    }

    public byte[] toBytes() throws SecureTransmissionException {
        if (salt.length != CryptoUtils.getSaltLength()) {
            throw new SecureTransmissionException("Unexpected salt length in payload.");
        }
        if (iv.length != CryptoUtils.getIvLength()) {
            throw new SecureTransmissionException("Unexpected IV length in payload.");
        }
        if (cipherText.length == 0) {
            throw new SecureTransmissionException("Ciphertext cannot be empty.");
        }

        ByteBuffer buffer =
                ByteBuffer.allocate(
                        MAGIC.length
                                + 1
                                + CryptoUtils.getSaltLength()
                                + CryptoUtils.getIvLength()
                                + Integer.BYTES
                                + cipherText.length);
        buffer.put(MAGIC);
        buffer.put(VERSION);
        buffer.put(salt);
        buffer.put(iv);
        buffer.putInt(cipherText.length);
        buffer.put(cipherText);
        return buffer.array();
    }

    public static StegoPayload fromBytes(byte[] payloadBytes) throws SecureTransmissionException {
        if (payloadBytes == null || payloadBytes.length < minimumPayloadSize()) {
            throw new SecureTransmissionException("Embedded payload is incomplete.");
        }

        ByteBuffer buffer = ByteBuffer.wrap(payloadBytes);
        byte[] magic = new byte[MAGIC.length];
        buffer.get(magic);

        if (!Arrays.equals(magic, MAGIC)) {
            throw new SecureTransmissionException("This image does not contain a valid secure payload.");
        }

        byte version = buffer.get();
        if (version != VERSION) {
            throw new SecureTransmissionException("Unsupported payload version.");
        }

        byte[] salt = new byte[CryptoUtils.getSaltLength()];
        byte[] iv = new byte[CryptoUtils.getIvLength()];
        buffer.get(salt);
        buffer.get(iv);

        int cipherLength = buffer.getInt();
        if (cipherLength <= 0 || cipherLength > buffer.remaining()) {
            throw new SecureTransmissionException("Embedded payload length is invalid.");
        }

        byte[] cipherText = new byte[cipherLength];
        buffer.get(cipherText);
        return new StegoPayload(salt, iv, cipherText);
    }

    public static int minimumPayloadSize() {
        return MAGIC.length + 1 + CryptoUtils.getSaltLength() + CryptoUtils.getIvLength() + Integer.BYTES;
    }
}
