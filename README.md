# Secure Data Transmission Application

This project is a Java 17 desktop application for hiding encrypted messages inside digital images. It combines AES-256 style password-based encryption using `PBKDF2WithHmacSHA256` and `AES/GCM/NoPadding` with least significant bit (LSB) steganography on RGB image channels.

## Features

- Password-protected message encryption before embedding.
- LSB-based hiding of the encrypted payload inside an image.
- PNG output to preserve the hidden data without lossy recompression.
- Swing-based graphical interface for encoding and decoding.
- CLI mode for quick testing and automated verification.
- Capacity checking to prevent overflow before encoding.

## Project Structure

- `src/com/securetransmission/AppLauncher.java` launches the Swing desktop application.
- `src/com/securetransmission/cli/StegoCli.java` provides command-line encode, decode, and capacity commands.
- `src/com/securetransmission/core` contains the encryption and steganography logic.
- `src/com/securetransmission/ui/MainFrame.java` contains the user interface.
- `scripts` contains simple PowerShell commands for compiling and running the app.
- `Lab_Project.tex` contains the project report.

## Requirements

- Windows PowerShell
- Java 17 or newer

## Build and Run

Compile the project:

```powershell
.\scripts\compile.ps1
```

Launch the GUI:

```powershell
.\scripts\run-gui.ps1
```

Run the CLI:

```powershell
.\scripts\run-cli.ps1 capacity Figures\CEP.png
.\scripts\run-cli.ps1 encode Figures\CEP.png output\stego.png StrongPass1 "This is a hidden message."
.\scripts\run-cli.ps1 decode output\stego.png StrongPass1
```

## Notes

- Use PNG as the final output format. Lossy formats such as JPG can destroy the hidden data.
- The password must be at least 8 characters long.
- Larger images can hold more hidden data.
