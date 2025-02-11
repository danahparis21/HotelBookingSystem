package hotelbookingsystem;

import javax.swing.*;

public class CustomerDashboard extends JFrame {
    public CustomerDashboard() {
        setTitle("Customer Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Welcome, Customer!", SwingConstants.CENTER);
        add(label);

        setVisible(true);
    }
}
