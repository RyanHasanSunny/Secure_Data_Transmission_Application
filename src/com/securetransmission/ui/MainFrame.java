package com.securetransmission.ui;

import com.securetransmission.core.SecureTransmissionException;
import com.securetransmission.core.SteganographyService;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class MainFrame extends JFrame {
    private final SteganographyService steganographyService = new SteganographyService();

    private final JTextField inputImageField = new JTextField(28);
    private final JTextField outputImageField = new JTextField(28);
    private final JPasswordField encodePasswordField = new JPasswordField(28);
    private final JTextArea messageArea = new JTextArea(8, 28);
    private final JLabel capacityLabel = new JLabel("Capacity: Select an image to calculate.");

    private final JTextField decodeImageField = new JTextField(28);
    private final JPasswordField decodePasswordField = new JPasswordField(28);
    private final JTextArea decodedMessageArea = new JTextArea(10, 28);

    private final JLabel statusLabel = new JLabel("Ready.");

    public MainFrame() {
        super("Secure Data Transmission Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);

        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        decodedMessageArea.setLineWrap(true);
        decodedMessageArea.setWrapStyleWord(true);
        decodedMessageArea.setEditable(false);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Encode", buildEncodePanel());
        tabs.addTab("Decode", buildDecodePanel());

        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        container.add(tabs, BorderLayout.CENTER);
        container.add(statusLabel, BorderLayout.SOUTH);

        setContentPane(container);
        pack();
    }

    private JPanel buildEncodePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Hide Encrypted Message"));

        GridBagConstraints gbc = baseConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Input image:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(inputImageField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JButton browseInputButton = new JButton("Browse");
        browseInputButton.addActionListener(e -> chooseImage(inputImageField, false, true));
        panel.add(browseInputButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Output image:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(outputImageField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JButton browseOutputButton = new JButton("Save As");
        browseOutputButton.addActionListener(e -> chooseImage(outputImageField, true, false));
        panel.add(browseOutputButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(encodePasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Secret message:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        panel.add(messageScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0;
        panel.add(capacityLabel, gbc);

        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JButton capacityButton = new JButton("Check Capacity");
        capacityButton.addActionListener(e -> updateCapacity());
        panel.add(capacityButton, gbc);

        gbc.gridx = 1;
        JButton encodeButton = new JButton("Encode and Save");
        encodeButton.addActionListener(e -> handleEncode());
        panel.add(encodeButton, gbc);

        gbc.gridx = 2;
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(
                e -> {
                    inputImageField.setText("");
                    outputImageField.setText("");
                    encodePasswordField.setText("");
                    messageArea.setText("");
                    capacityLabel.setText("Capacity: Select an image to calculate.");
                    setStatus("Encode form cleared.");
                });
        panel.add(clearButton, gbc);

        return panel;
    }

    private JPanel buildDecodePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Recover Hidden Message"));

        GridBagConstraints gbc = baseConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Stego image:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(decodeImageField, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JButton browseDecodeButton = new JButton("Browse");
        browseDecodeButton.addActionListener(e -> chooseImage(decodeImageField, false, true));
        panel.add(browseDecodeButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(decodePasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Recovered message:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JScrollPane outputScrollPane = new JScrollPane(decodedMessageArea);
        panel.add(outputScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        JButton decodeButton = new JButton("Decode Message");
        decodeButton.addActionListener(e -> handleDecode());
        panel.add(decodeButton, gbc);

        gbc.gridx = 1;
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(
                e -> {
                    decodeImageField.setText("");
                    decodePasswordField.setText("");
                    decodedMessageArea.setText("");
                    setStatus("Decode form cleared.");
                });
        panel.add(clearButton, gbc);

        return panel;
    }

    private GridBagConstraints baseConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void chooseImage(JTextField field, boolean saveDialog, boolean updateCapacityAfterSelection) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(
                new FileNameExtensionFilter(
                        "Image files (*.png, *.jpg, *.jpeg, *.bmp)", "png", "jpg", "jpeg", "bmp"));

        int result =
                saveDialog ? chooser.showSaveDialog(this) : chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            Path selectedPath = chooser.getSelectedFile().toPath();
            field.setText(selectedPath.toString());

            if (updateCapacityAfterSelection && field == inputImageField) {
                updateCapacity();
            }
        }
    }

    private void updateCapacity() {
        String imageText = inputImageField.getText().trim();
        if (imageText.isEmpty()) {
            showError("Select an input image before checking capacity.");
            return;
        }

        try {
            int capacity = steganographyService.getCapacityBytes(Path.of(imageText));
            capacityLabel.setText("Capacity: Approximately " + capacity + " encrypted payload bytes.");
            setStatus("Image capacity calculated.");
        } catch (SecureTransmissionException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleEncode() {
        try {
            Path inputPath = requirePath(inputImageField.getText(), "input image");
            Path outputPath = normalizePngPath(requirePath(outputImageField.getText(), "output image"));
            String message = messageArea.getText();
            char[] password = encodePasswordField.getPassword();

            steganographyService.encode(inputPath, outputPath, message, password);
            outputImageField.setText(outputPath.toString());
            decodeImageField.setText(outputPath.toString());
            setStatus("Message embedded successfully into " + outputPath);
            JOptionPane.showMessageDialog(
                    this,
                    "Message embedded successfully into:\n" + outputPath,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SecureTransmissionException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleDecode() {
        try {
            Path imagePath = requirePath(decodeImageField.getText(), "stego image");
            char[] password = decodePasswordField.getPassword();

            String recoveredMessage = steganographyService.decode(imagePath, password);
            decodedMessageArea.setText(recoveredMessage);
            setStatus("Message recovered successfully.");
        } catch (SecureTransmissionException ex) {
            showError(ex.getMessage());
        }
    }

    private Path requirePath(String value, String label) throws SecureTransmissionException {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            throw new SecureTransmissionException("Please select a " + label + ".");
        }
        return Paths.get(trimmed);
    }

    private Path normalizePngPath(Path path) {
        String rawPath = path.toString();
        if (rawPath.toLowerCase(Locale.ROOT).endsWith(".png")) {
            return path;
        }
        return Paths.get(rawPath + ".png");
    }

    private void showError(String message) {
        setStatus("Operation failed.");
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }
}
