package hotelbookingsystem;

import javax.swing.*;
import java.awt.*;

public class LogInForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, signUpButton;

    public LogInForm() {
        setTitle("Hotel Booking - Login");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // 🌟 Load Background Image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/icons/hotelbg.jpg"));
        Image bgImage = bgIcon.getImage().getScaledInstance(1920, 1080, Image.SCALE_SMOOTH);
        bgIcon = new ImageIcon(bgImage);
        JLabel background = new JLabel(bgIcon);

        // Calculate Center
        int bgX = (getWidth() - bgIcon.getIconWidth()) / 2;
        int bgY = (getHeight() - bgIcon.getIconHeight()) / 2;
        background.setBounds(bgX, bgY, bgIcon.getIconWidth(), bgIcon.getIconHeight());

        add(background);


        // 🏳️ White Panel for Login Form
        JPanel loginPanel = new JPanel();
        loginPanel.setBounds(600, 300, 400, 300);
        loginPanel.setBackground(new Color(255, 255, 255, 200)); // Transparent White
        loginPanel.setLayout(null);
        background.add(loginPanel);

        // 📝 Username Label & Field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 25);
        loginPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 50, 200, 25);
        loginPanel.add(usernameField);

        // 🔒 Password Label & Field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 100, 100, 25);
        loginPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 200, 25);
        loginPanel.add(passwordField);

        // ✅ Login Button
        loginButton = new JButton("Login");
        loginButton.setBounds(50, 180, 120, 30);
        loginPanel.add(loginButton);

        // 🆕 Sign-Up Button
        signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(200, 180, 120, 30);
        loginPanel.add(signUpButton);

        // 🎯 Login Action
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = UserManager.login(username, password);

            if (role == null) {
                JOptionPane.showMessageDialog(null, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (role.equals("admin")) {
                // Redirect to admin dashboard
                new AdminDashboard().setVisible(true);
                dispose();
            } else if (role.equals("customer")) {
                // Redirect to customer dashboard
                new CustomerDashboard().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid role assigned.");
            }

        });

        // 🎟️ Sign-Up Action
        signUpButton.addActionListener(e -> {
            new SignupForm();
            dispose();
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LogInForm(); // Start Login Form
    }
}
