package hotelbookingsystem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateRoomForm extends JFrame {
    private int roomId;
    private JTextField priceField;
    private JComboBox<String> roomTypeBox;
    private JComboBox<String> statusBox;

    public UpdateRoomForm(int roomId) {
        this.roomId = roomId;
        setTitle("Update Room");
        setSize(400, 300);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel typeLabel = new JLabel("Room Type:");
        typeLabel.setBounds(30, 30, 100, 25);
        add(typeLabel);

        roomTypeBox = new JComboBox<>(new String[]{"Single", "Double", "Suite"});
        roomTypeBox.setBounds(150, 30, 200, 25);
        add(roomTypeBox);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setBounds(30, 70, 100, 25);
        add(priceLabel);

        priceField = new JTextField();
        priceField.setBounds(150, 70, 200, 25);
        add(priceField);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(30, 110, 100, 25);
        add(statusLabel);

        statusBox = new JComboBox<>(new String[]{"Available", "Booked", "Under Maintenance"});
        statusBox.setBounds(150, 110, 200, 25);
        add(statusBox);

        JButton updateButton = new JButton("Update Room");
        updateButton.setBounds(150, 160, 200, 30);
        add(updateButton);

        updateButton.addActionListener(this::updateRoom);

        setVisible(true);
    }

    private void updateRoom(ActionEvent e) {
        String roomType = (String) roomTypeBox.getSelectedItem();
        String status = (String) statusBox.getSelectedItem();
        String priceText = priceField.getText();

        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Price cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            try (Connection conn = Database.connect();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE rooms SET room_type = ?, price = ?, availability_status = ? WHERE room_id = ?")) {
                stmt.setString(1, roomType);
                stmt.setDouble(2, price);
                stmt.setString(3, status);
                stmt.setInt(4, roomId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Room updated successfully!");
                dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating room.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
