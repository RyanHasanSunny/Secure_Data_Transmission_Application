package com.securetransmission;

import com.securetransmission.ui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class AppLauncher {
    private AppLauncher() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setSystemLookAndFeel();
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // The application still works with the default Swing look and feel.
        }
    }
}
