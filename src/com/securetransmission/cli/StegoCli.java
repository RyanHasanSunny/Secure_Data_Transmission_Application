package com.securetransmission.cli;

import com.securetransmission.core.SecureTransmissionException;
import com.securetransmission.core.SteganographyService;
import java.nio.file.Path;

public final class StegoCli {
    private StegoCli() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        SteganographyService service = new SteganographyService();

        try {
            switch (args[0].toLowerCase()) {
                case "encode" -> runEncode(service, args);
                case "decode" -> runDecode(service, args);
                case "capacity" -> runCapacity(service, args);
                default -> printUsage();
            }
        } catch (SecureTransmissionException ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(1);
        }
    }

    private static void runEncode(SteganographyService service, String[] args)
            throws SecureTransmissionException {
        if (args.length < 5) {
            printUsage();
            return;
        }

        Path inputPath = Path.of(args[1]);
        Path outputPath = Path.of(args[2]);
        char[] password = args[3].toCharArray();
        String message = args[4];

        service.encode(inputPath, outputPath, message, password);
        System.out.println("Message embedded successfully into: " + outputPath.toAbsolutePath());
    }

    private static void runDecode(SteganographyService service, String[] args)
            throws SecureTransmissionException {
        if (args.length < 3) {
            printUsage();
            return;
        }

        Path imagePath = Path.of(args[1]);
        char[] password = args[2].toCharArray();

        String message = service.decode(imagePath, password);
        System.out.println("Recovered message:");
        System.out.println(message);
    }

    private static void runCapacity(SteganographyService service, String[] args)
            throws SecureTransmissionException {
        if (args.length < 2) {
            printUsage();
            return;
        }

        Path imagePath = Path.of(args[1]);
        int capacity = service.getCapacityBytes(imagePath);
        System.out.println("Estimated text capacity (payload only): " + capacity + " bytes");
    }

    private static void printUsage() {
        System.out.println("Secure Data Transmission Application CLI");
        System.out.println("Usage:");
        System.out.println("  encode <input-image> <output-image> <password> <message>");
        System.out.println("  decode <stego-image> <password>");
        System.out.println("  capacity <image>");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  encode Figures/CEP.png output/stego.png StrongPass1 \"Hidden message\"");
        System.out.println("  decode output/stego.png StrongPass1");
    }
}
