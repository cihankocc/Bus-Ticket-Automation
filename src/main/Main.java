package main;

import gui.LoginGui;

import javax.swing.*;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        // Modern Nimbus görünümünü kullan (buton renklerini destekler)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Başarısız olursa varsayılan Metal stili devam eder
        }

        SwingUtilities.invokeLater(() -> {
            LoginGui frame = new LoginGui();
            frame.setVisible(true);
        });
    }
}
