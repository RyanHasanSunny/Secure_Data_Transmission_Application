package com.securetransmission.core;

public class SecureTransmissionException extends Exception {
    public SecureTransmissionException(String message) {
        super(message);
    }

    public SecureTransmissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
