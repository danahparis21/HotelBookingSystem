package hotelbookingsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

public class LogInForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, signUpButton;
    private int customerID = -1; // Initialize to an invalid ID
    private JLabel background;
    private ImageIcon[] bgImages;
    private int imageIndex = 0; // Track current image
    private Timer backgroundTimer;
    private ImageIcon bgIcon;

    public LogInForm() {
        setTitle("Hotel Booking - Login");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

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
        JPanel loginPanel = new JPanel();
        loginPanel.setBounds((getWidth() - 400) / 2, (int) (getHeight() * 0.55), 100, 100);
        loginPanel.setBackground(new Color(255, 255, 255, 150)); // Semi-transparent white
        loginPanel.setLayout(null);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        background.add(loginPanel);

        // Define Colors & Font
        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Color normalGold = new Color(186, 156, 96); // #ba9c60
        Color hoverGold = new Color(164, 123, 52); // Slightly darker gold
        Color pressedGold = new Color(129, 96, 38); // Even darker gold for pressing effect
        Color textFieldBg = new Color(245, 245, 245); // Light gray background for input fields

        // 📝 Username Label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 40, 100, 30);
        userLabel.setFont(labelFont);
        loginPanel.add(userLabel);

        // ✍️ Styled Username Field
        usernameField = new JTextField();
        usernameField.setBounds(50, 70, 300, 40);
        usernameField.setBackground(textFieldBg);
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        usernameField.setOpaque(true);
        loginPanel.add(usernameField);

        // 🔒 Password Label
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 120, 100, 30);
        passLabel.setFont(labelFont);
        loginPanel.add(passLabel);

        // 🔑 Styled Password Field
        passwordField = new JPasswordField();
        passwordField.setBounds(50, 150, 300, 40);
        passwordField.setBackground(textFieldBg);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        passwordField.setOpaque(true);
        loginPanel.add(passwordField);

        // ✅ Login Button with Modern Look
        loginButton = new JButton("Login");
        loginButton.setBounds(50, 210, 140, 40);
        styleButton(loginButton, normalGold, hoverGold, pressedGold);
        loginPanel.add(loginButton);

        // 🚀 Sign Up Button
        signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(210, 210, 140, 40);
        styleButton(signUpButton, normalGold, hoverGold, pressedGold);
        loginPanel.add(signUpButton);

        // 🎯 Login Action
        loginButton.addActionListener(e -> {
            System.out.println("Customer ID: " + customerID);
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            customerID = UserManager.getCustomerID(username);
            String role = UserManager.login(username, password);

            if (role == null) {
                JOptionPane.showMessageDialog(null, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (role.equals("admin")) {
                new AdminDashboard().setVisible(true);
                dispose();
            } else if (role.equals("customer")) {
                new CustomerDashboard(customerID).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid role assigned.");
            }
        });

        signUpButton.addActionListener(e -> {
            new SignupForm();
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
        new LogInForm(); // Start Login Form
    }
}
