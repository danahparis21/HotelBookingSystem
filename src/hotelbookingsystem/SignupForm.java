package hotelbookingsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

public class SignupForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton, backButton;
    private JTextField nameField, emailField, phoneField;
    private JLabel background;
    private ImageIcon[] bgImages;
    private int imageIndex = 0; // Track current image
    private Timer backgroundTimer;
    private ImageIcon bgIcon;

    public SignupForm() {
        setTitle("Hotel Booking - Sign Up");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // 🌟 Background Image (Without Scaling)
        bgImages = new ImageIcon[]{
            new ImageIcon(getClass().getResource("/icons/homebg.png")),
            new ImageIcon(getClass().getResource("/icons/homebg2.png")),
            new ImageIcon(getClass().getResource("/icons/homebg3.png"))
        };
        
        background = new JLabel(bgImages[0]); // Set first image
        background.setBounds(0, 0, getWidth(), getHeight());
        getContentPane().add(background);
        
        // 🕒 Set Timer to Change Background
        backgroundTimer = new Timer(5000, new ActionListener() { // Change every 5 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                imageIndex = (imageIndex + 1) % bgImages.length; // Cycle through images
                updateBackground();
            }
        });
        backgroundTimer.start(); // Start timer

        // 🎨 Modern Panel for Login Form
        JPanel signupPanel = new JPanel();
        signupPanel.setBounds((getWidth() - 400) / 2, (int) (getHeight() * 0.55), 100, 100);
        signupPanel.setBackground(new Color(255, 255, 255, 150)); // Semi-transparent white
        signupPanel.setLayout(null);
        signupPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        background.add(signupPanel);

        // Define Colors & Font
        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Color normalGold = new Color(186, 156, 96); // #ba9c60
        Color hoverGold = new Color(164, 123, 52); // Slightly darker gold
        Color pressedGold = new Color(129, 96, 38); // Even darker gold for pressing effect
        Color textFieldBg = new Color(245, 245, 245); // Light gray background for input fields


        // 📝 Username Label & Field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 30, 100, 30);
        userLabel.setFont(labelFont);
        signupPanel.add(userLabel);

        // ✍️ Styled Username Field
        usernameField = new JTextField();
        usernameField.setBounds(150, 30, 200, 30);
        usernameField.setBackground(textFieldBg);
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        usernameField.setOpaque(true);
        signupPanel.add(usernameField);
        
               
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 70, 100, 30);
        nameLabel.setFont(labelFont);
        signupPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 70, 200, 30);
        nameField.setBackground(textFieldBg);
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        nameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        nameField.setOpaque(true);
        signupPanel.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 110, 100, 30);
        emailLabel.setFont(labelFont);
        signupPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 110, 200, 30);
        emailField.setBackground(textFieldBg);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emailField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        emailField.setOpaque(true);
        signupPanel.add(emailField);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(50, 150, 100, 30);
        phoneLabel.setFont(labelFont);
        signupPanel.add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(150, 150, 200, 30);
        phoneField.setBackground(textFieldBg);
        phoneField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        phoneField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        phoneField.setOpaque(true);
        signupPanel.add(phoneField);

        // 🔒 Password Label & Field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 190, 100, 30);
        passLabel.setFont(labelFont);
        signupPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 190, 200, 30);
        passwordField.setBackground(textFieldBg);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        passwordField.setOpaque(true);
        signupPanel.add(passwordField);
        

        // ✅ Register Button
        registerButton = new JButton("Register");
        registerButton.setBounds(50, 230, 140, 40);
        styleButton(registerButton, normalGold, hoverGold, pressedGold);
        signupPanel.add(registerButton);

        // 🔙 Back Button
        backButton = new JButton("Back");
        backButton.setBounds(210, 230, 140, 40);
        styleButton(backButton, normalGold, hoverGold, pressedGold);
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
        
        // 🛠 Adjust background centering when window resizes
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeBackground();
            }
        });

        setVisible(true);
        resizeBackground();
    }
    
    // 🖼️ Update Background with Scaling
    private void updateBackground() {
        int width = getWidth();
        int height = getHeight();

        Image scaledImage = bgImages[imageIndex].getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        background.setIcon(new ImageIcon(scaledImage));
        background.setBounds(0, 0, width, height);
    }

    // 🎨 Button Styling
       private void styleButton(JButton button, Color normalColor, Color hoverColor, Color pressedColor) {
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setFocusPainted(false); // Disable default blue focus border
        button.setBorder(BorderFactory.createLineBorder(new Color(110, 80, 34), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setContentAreaFilled(false); // ❗ FIX: Removes default button blue effect
        button.setOpaque(true); // ❗ FIX: Ensures custom colors apply correctly

        // 🔄 Hover & Click Effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(pressedColor); // Custom pressed color
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(hoverColor); // Restore hover color after release
            }
        });
    }


    private void resizeBackground() {
        int width = getWidth();
        int height = getHeight();

        // Scale image smoothly
       Image scaledImage = bgImages[imageIndex].getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

        background.setIcon(new ImageIcon(scaledImage));
        background.setBounds(0, 0, width, height);

        // Reposition login panel
        Component[] components = background.getComponents();
        // Reposition login panel dynamically
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                comp.setBounds((width - 400) / 2, (int) (height * 0.5), 400, 300);
            }
        }

    }

    public static void main(String[] args) {
        new SignupForm(); 
    }
}
