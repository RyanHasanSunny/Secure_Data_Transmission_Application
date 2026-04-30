# Secure Data Transmission Application Presentation

## Slide 1: Project Title and Overview

### Slide Content
- Secure Data Transmission Application
- Group Members:
- Md Kawsar Ahmed - 222002131
- Ryan Hasan Sunny - 213002183
- Java 17 desktop application
- AES-GCM encryption + RGB LSB steganography

### Speaker Script
Good morning everyone. Our project title is `Secure Data Transmission Application`. This project was completed by `Md Kawsar Ahmed, ID 222002131` and `Ryan Hasan Sunny, ID 213002183`. The main idea of this project is to send a secret text message securely by using two protection layers. First, the message is encrypted using AES-GCM, and then the encrypted data is hidden inside an image using LSB steganography. The project was implemented as a Java 17 desktop application with both GUI and CLI support.

## Slide 2: Problem Statement and Motivation

### Slide Content
- Plaintext transmission is insecure
- Encryption hides content, but not the presence of communication
- Steganography hides the existence of the message
- Combining both improves security

### Speaker Script
The problem we wanted to solve is that normal text transmission is insecure, because if someone intercepts the message, they can read it immediately. Encryption solves part of that problem by making the content unreadable, but it still shows that secret communication is taking place. Steganography adds another layer by hiding the existence of the message inside a normal image. So, by combining cryptography and steganography, the system protects both the content and the visibility of the communication.

## Slide 3: Objectives and Design Goals

### Slide Content
- Security
- Usability
- Efficiency
- Suitable for academic demonstration and confidential communication

### Speaker Script
This project had three main objectives: security, usability, and efficiency. For security, the message should remain unreadable without the correct password. For usability, the system should be simple enough for normal users to encode and decode messages through a desktop interface. For efficiency, the message should be hidden without noticeable delay or visible image distortion.

## Slide 4: System Architecture

### Slide Content
- User interface layer
- Cryptography layer
- Payload layer
- Steganography layer

### Speaker Script
The system is divided into four layers. The first is the user interface layer, built with Swing, which handles encoding and decoding interaction. The second is the cryptography layer, which performs password validation, key derivation, encryption, and decryption. The third is the payload layer, which organizes the hidden data into a structured format. The fourth is the steganography layer, which embeds and extracts the payload using least significant bits of the RGB channels.

## Slide 5: Security and Payload Design

### Slide Content
- AES-GCM for confidentiality and integrity
- PBKDF2WithHmacSHA256 for key derivation
- Random 16-byte salt and 12-byte IV
- Structured payload with header and metadata

### Speaker Script
For encryption, we used AES-GCM, which provides both confidentiality and integrity. The AES key is derived from the user password using PBKDF2 with HMAC-SHA256, and each operation uses a random 16-byte salt and a 12-byte IV. The hidden payload is not just raw ciphertext. It contains a magic header, a version field, the salt, the IV, the ciphertext length, and the ciphertext itself. This structure helps the decoder verify that the extracted data is valid and not just random image noise.

## Slide 6: Encoding and Decoding Workflow

### Slide Content
- Select cover image, message, and password
- Encrypt and frame the payload
- Check image capacity
- Embed into RGB LSBs and save as PNG
- Reverse the process to decode

### Speaker Script
In the encoding process, the user selects a cover image, enters a secret message and password, and the application generates a salt and IV. Then it derives the AES key, encrypts the message, builds the payload, checks the image capacity, and embeds the bits into the RGB channels before saving the result as a PNG image. In the decoding process, the application reads the stego image, extracts the hidden payload, validates the header and version, derives the same key from the password, and decrypts the message. PNG format is important here because lossy formats like JPEG can destroy the embedded bits.

## Slide 7: Implementation Details

### Slide Content
- `CryptoUtils.java`
- `SteganographyService.java`
- `MainFrame.java`
- Input validation and error handling

### Speaker Script
The project was implemented in Java 17. The cryptographic logic is handled in `CryptoUtils.java`, the steganography logic is handled in `SteganographyService.java`, and the GUI is implemented in `MainFrame.java`. The system also handles common error cases such as empty messages, short passwords, unsupported images, insufficient capacity, invalid payloads, and incorrect passwords. This makes the application more practical and reliable for users.

## Slide 8: Testing Setup and Sample Case

### Slide Content
- Tested on Windows with Java 17
- GUI and CLI both validated
- Cover image: `CEP.png`
- Hidden message size: 161 bytes
- Output image: `stego_sample.png`

### Speaker Script
For testing, we used a Windows environment with Java 17 installed. We tested both the GUI and command-line version of the application. The sample cover image used in the demonstration was `CEP.png`, and the sample hidden message size was 161 bytes. The generated output image was saved as `stego_sample.png`. We also tested decoding with both the correct and incorrect password to verify the security behavior.

## Slide 9: Results and Performance Analysis

### Slide Content
- Resolution: 823 x 506 pixels
- Payload capacity: 156160 bytes
- Message length: 161 bytes
- Changed pixels: 508
- Changed RGB channels: 896
- Visual difference is negligible

### Speaker Script
The results were successful. The cover image resolution was 823 by 506 pixels, and the available payload capacity was 156160 bytes, which is much larger than the 161-byte test message. Encoding completed successfully, and decoding recovered the exact original plaintext when the correct password was used. When the wrong password was entered, the application correctly rejected the decryption. In terms of image quality, only 508 pixels changed, which is about 0.122 percent of the image, and only 896 RGB channels changed, which is 0.0717 percent of all RGB values. This shows that the visual difference between the original and stego image is negligible.

## Slide 10: Conclusion, Limitations, and Future Work

### Slide Content
- Project goals achieved
- Current limitations
- Future improvement areas

### Speaker Script
In conclusion, this project successfully combined AES-based encryption and image steganography in one secure Java application. It achieved the main goals of security, usability, and low visual distortion. However, the current system only supports text messages, uses simple sequential LSB embedding, and is vulnerable if the image is converted to a lossy format like JPEG. In the future, this project can be improved by supporting file embedding, using more advanced hiding techniques, measuring image quality with PSNR and SSIM, and adding direct sender-receiver communication features.

## Closing

### Speaker Script
Thank you. That was the overview of our Secure Data Transmission Application project. We are ready for any questions.
