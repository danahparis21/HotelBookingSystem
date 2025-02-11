package hotelbookingsystem;

import javax.swing.*;
import java.awt.*;

public class SignupForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton, backButton;
    private JTextField nameField, emailField, phoneField;

    public SignupForm() {
        setTitle("Hotel Booking - Sign Up");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // 🌟 Load Background Image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/icons/hotelbg.jpg"));
        Image bgImage = bgIcon.getImage().getScaledInstance(1920, 1080, Image.SCALE_SMOOTH);
        bgIcon = new ImageIcon(bgImage);
        JLabel background = new JLabel(bgIcon);
        background.setBounds(0, 0, 1920, 1080);
        add(background);

        // 🏳️ White Panel for Signup Form
        JPanel signupPanel = new JPanel();
        signupPanel.setBounds(600, 250, 400, 300); // Centered horizontally
        signupPanel.setBackground(new Color(255, 255, 255, 200)); // Transparent White
        signupPanel.setLayout(null);
        background.add(signupPanel);

        // 📝 Username Label & Field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 25);
        signupPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 50, 200, 25);
        signupPanel.add(usernameField);
        
               
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 80, 100, 25);
        signupPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 80, 200, 25);
        signupPanel.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 110, 100, 25);
        signupPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 110, 200, 25);
        signupPanel.add(emailField);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(50, 140, 100, 25);
        signupPanel.add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(150, 140, 200, 25);
        signupPanel.add(phoneField);

        // 🔒 Password Label & Field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 170, 100, 25);
        signupPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 170, 200, 25);
        signupPanel.add(passwordField);
        

        // ✅ Register Button
        registerButton = new JButton("Register");
        registerButton.setBounds(50, 210, 120, 30);
        signupPanel.add(registerButton);

        // 🔙 Back Button
        backButton = new JButton("Back");
        backButton.setBounds(200, 210, 120, 30);
        signupPanel.add(backButton);

        // 🎟️ Register Action
        registerButton.addActionListener(e -> {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields.");
            return;
        }

        UserManager.signUp(username, password, name, email, phone);
        JOptionPane.showMessageDialog(null, "Registration Successful! Please Login.");
        new LogInForm();
        dispose();
});


        // 🔙 Back to Login
        backButton.addActionListener(e -> {
            new LogInForm();
            dispose();
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new SignupForm(); 
    }
}
