package gui;

import controllers.UserController;
import models.Owner;
import models.User;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginGui extends JFrame {
    private static final long serialVersionUID = 1L;

    static String loggedInEmail;

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;
    private JPasswordField registerPasswordField;
    private final UserController userController;

    public LoginGui() {
        this.userController = new UserController();
        setTitle("Bilet Al");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 320);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab("Giriş", createLoginPanel());
        tabbedPane.addTab("Kaydol", createRegisterPanel());
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(messageLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("E-Posta:"), gbc);
        loginEmailField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(loginEmailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Şifre:"), gbc);
        loginPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(loginPasswordField, gbc);

        JButton loginBtn = new JButton("Giriş Yap");
        loginBtn.setBackground(new Color(23, 145, 255));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> performLogin(messageLabel));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(messageLabel, gbc);
        gbc.gridwidth = 1;

        String[] labels = { "Ad:", "Soyad:", "E-Posta:", "Şifre:" };
        JTextField[] fields = new JTextField[4];
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            panel.add(new JLabel(labels[i]), gbc);
            fields[i] = (i == 3) ? new JPasswordField(15) : new JTextField(15);
            gbc.gridx = 1;
            panel.add(fields[i], gbc);
        }
        firstNameField = fields[0];
        lastNameField = fields[1];
        emailField = fields[2];
        registerPasswordField = (JPasswordField) fields[3];

        JRadioButton adminRadio = new JRadioButton("Yönetici Kaydı");
        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(adminRadio, gbc);

        JButton registerBtn = new JButton("Kaydol");
        registerBtn.setBackground(new Color(23, 145, 255));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.addActionListener(e -> performRegister(adminRadio.isSelected(), messageLabel));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(registerBtn, gbc);

        return panel;
    }

    private void performLogin(JLabel messageLabel) {
        String email = loginEmailField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("E-posta ve şifre boş olamaz.");
            return;
        }
        try {
            User user = userController.login(email, password);
            if (user != null) {
                loggedInEmail = email;
                if (user instanceof Owner)
                    new AdminPanel().setVisible(true);
                else
                    new DptList().setVisible(true);
                dispose();
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Geçersiz e-posta veya şifre.");
            }
        } catch (Exception ex) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText(ex.getMessage());
        }
    }

    private void performRegister(boolean isAdmin, JLabel messageLabel) {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(registerPasswordField.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Tüm alanları doldurunuz.");
            return;
        }
        try {
            String result = isAdmin
                    ? userController.registerAdmin(firstName, lastName, email, password)
                    : userController.register(firstName, lastName, email, password);
            messageLabel.setForeground(new Color(0, 170, 80));
            messageLabel.setText(result);
        } catch (Exception ex) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText(ex.getMessage());
        }
    }
}