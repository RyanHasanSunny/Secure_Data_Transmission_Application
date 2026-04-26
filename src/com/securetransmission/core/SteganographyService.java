package com.securetransmission.core;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import javax.imageio.ImageIO;

public final class SteganographyService {
    private static final String OUTPUT_FORMAT = "png";

    public void encode(Path inputImagePath, Path outputImagePath, String message, char[] password)
            throws SecureTransmissionException {
        BufferedImage sourceImage = readImage(inputImagePath);
        BufferedImage workingImage = copyToArgb(sourceImage);

        EncryptedData encryptedData = CryptoUtils.encrypt(message, password);
        StegoPayload payload =
                new StegoPayload(
                        encryptedData.salt(), encryptedData.iv(), encryptedData.cipherText());
        byte[] payloadBytes = payload.toBytes();

        int availablePayloadBytes = getCapacityBytes(workingImage);
        if (payloadBytes.length > availablePayloadBytes) {
            throw new SecureTransmissionException(
                    "The selected image is too small. Available payload capacity is "
                            + availablePayloadBytes
                            + " bytes.");
        }

        byte[] framedPayload = framePayload(payloadBytes);
        embedBytes(workingImage, framedPayload);
        writeImage(workingImage, outputImagePath);
    }

    public String decode(Path imagePath, char[] password) throws SecureTransmissionException {
        BufferedImage image = readImage(imagePath);
        int maximumPayloadBytes = getCapacityBytes(image);
        byte[] lengthPrefix = extractBytes(image, Integer.BYTES);
        int payloadLength = ByteBuffer.wrap(lengthPrefix).getInt();

        if (payloadLength < StegoPayload.minimumPayloadSize() || payloadLength > maximumPayloadBytes) {
            throw new SecureTransmissionException("No valid hidden payload was found in the image.");
        }

        byte[] framedPayload = extractBytes(image, Integer.BYTES + payloadLength);
        byte[] payloadBytes = Arrays.copyOfRange(framedPayload, Integer.BYTES, framedPayload.length);
        StegoPayload payload = StegoPayload.fromBytes(payloadBytes);
        return CryptoUtils.decrypt(
                new EncryptedData(payload.salt(), payload.iv(), payload.cipherText()), password);
    }

    public int getCapacityBytes(Path imagePath) throws SecureTransmissionException {
        BufferedImage image = readImage(imagePath);
        return getCapacityBytes(image);
    }

    private int getCapacityBytes(BufferedImage image) {
        int totalEmbeddableBytes = (image.getWidth() * image.getHeight() * 3) / 8;
        return Math.max(0, totalEmbeddableBytes - Integer.BYTES);
    }

    private BufferedImage readImage(Path imagePath) throws SecureTransmissionException {
        if (imagePath == null || !Files.exists(imagePath)) {
            throw new SecureTransmissionException("Image file not found: " + imagePath);
        }

        try {
            BufferedImage image = ImageIO.read(imagePath.toFile());
            if (image == null) {
                throw new SecureTransmissionException("Unsupported image format: " + imagePath);
            }
            return image;
        } catch (IOException ex) {
            throw new SecureTransmissionException("Unable to read the image file.", ex);
        }
    }

    private BufferedImage copyToArgb(BufferedImage sourceImage) {
        BufferedImage converted =
                new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = converted.createGraphics();
        try {
            graphics.drawImage(sourceImage, 0, 0, null);
        } finally {
            graphics.dispose();
        }
        return converted;
    }

    private byte[] framePayload(byte[] payloadBytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + payloadBytes.length);
        buffer.putInt(payloadBytes.length);
        buffer.put(payloadBytes);
        return buffer.array();
    }

    private void writeImage(BufferedImage image, Path outputImagePath) throws SecureTransmissionException {
        try {
            Path parent = outputImagePath.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            boolean written = ImageIO.write(image, OUTPUT_FORMAT, outputImagePath.toFile());
            if (!written) {
                throw new SecureTransmissionException("Unable to write the output image as PNG.");
            }
        } catch (IOException ex) {
            throw new SecureTransmissionException("Unable to save the output image.", ex);
        }
    }

    private void embedBytes(BufferedImage image, byte[] data) {
        int totalBits = data.length * 8;
        int bitIndex = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);
                int alpha = (argb >>> 24) & 0xFF;
                int red = (argb >>> 16) & 0xFF;
                int green = (argb >>> 8) & 0xFF;
                int blue = argb & 0xFF;

                if (bitIndex < totalBits) {
                    red = setLeastSignificantBit(red, getBit(data, bitIndex++));
                }
                if (bitIndex < totalBits) {
                    green = setLeastSignificantBit(green, getBit(data, bitIndex++));
                }
                if (bitIndex < totalBits) {
                    blue = setLeastSignificantBit(blue, getBit(data, bitIndex++));
                }

                int updatedArgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                image.setRGB(x, y, updatedArgb);

                if (bitIndex >= totalBits) {
                    return;
                }
            }
        }
    }

    private byte[] extractBytes(BufferedImage image, int byteCount) throws SecureTransmissionException {
        int totalBits = byteCount * 8;
        byte[] data = new byte[byteCount];
        int bitIndex = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);
                int red = (argb >>> 16) & 0xFF;
                int green = (argb >>> 8) & 0xFF;
                int blue = argb & 0xFF;

                if (bitIndex < totalBits) {
                    setBit(data, bitIndex++, red & 1);
                }
                if (bitIndex < totalBits) {
                    setBit(data, bitIndex++, green & 1);
                }
                if (bitIndex < totalBits) {
                    setBit(data, bitIndex++, blue & 1);
                }

                if (bitIndex >= totalBits) {
                    return data;
                }
            }
        }

        throw new SecureTransmissionException("Image data ended before the payload could be extracted.");
    }

    private int getBit(byte[] data, int bitIndex) {
        int byteIndex = bitIndex / 8;
        int shift = 7 - (bitIndex % 8);
        return (data[byteIndex] >>> shift) & 1;
    }

    private void setBit(byte[] data, int bitIndex, int bit) {
        int byteIndex = bitIndex / 8;
        int shift = 7 - (bitIndex % 8);
        data[byteIndex] =
                (byte) ((data[byteIndex] & ~(1 << shift)) | ((bit & 1) << shift));
    }

    private int setLeastSignificantBit(int value, int bit) {
        return (value & 0xFE) | (bit & 1);
    }
}
