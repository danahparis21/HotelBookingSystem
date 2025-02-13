package hotelbookingsystem;

import java.awt.Color;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboard extends JFrame {
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JTextField searchRoomIdField, searchPriceField;
    private JComboBox<String> searchTypeCombo, searchStatusCombo;
    private JLabel bookedRoomsLabel, availableRoomsLabel, guestCountLabel;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setBounds(250, 10, 400, 30);
        add(titleLabel);

        // Search Panel
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setBounds(50, 50, 100, 25);
        add(searchLabel);

        searchRoomIdField = new JTextField("Room ID");
        searchRoomIdField.setBounds(100, 50, 80, 25);
        searchRoomIdField.setForeground(Color.GRAY);
        

        searchRoomIdField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchRoomIdField.getText().equals("Room ID")) {
                    searchRoomIdField.setText("");
                    searchRoomIdField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchRoomIdField.getText().isEmpty()) {
                    searchRoomIdField.setText("Room ID");
                    searchRoomIdField.setForeground(Color.GRAY);
                }
            }
        });

        add(searchRoomIdField);

        
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setBounds(190, 50, 50, 25);
        add(typeLabel);

        searchTypeCombo = new JComboBox<>(new String[]{"All", "Single", "Double", "Suite"});
        searchTypeCombo.setBounds(230, 50, 100, 25);
        add(searchTypeCombo);

        searchPriceField = new JTextField();
        searchPriceField.setBounds(340, 50, 80, 25);
        searchPriceField.setToolTipText("Price");
        searchPriceField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchPriceField.getText().equals("Price")) {
                    searchPriceField.setText("");
                    searchPriceField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchPriceField.getText().isEmpty()) {
                    searchPriceField.setText("Price");
                    searchPriceField.setForeground(Color.GRAY);
                }
            }
        });
        add(searchPriceField);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(430, 50, 50, 25);
        add(statusLabel);

        searchStatusCombo = new JComboBox<>(new String[]{"All", "Available", "Booked", "Under Maintenance"});
        searchStatusCombo.setBounds(480, 50, 140, 25);
        add(searchStatusCombo);


        JButton searchButton = new JButton("Search");
        searchButton.setBounds(640, 50, 100, 25);
        add(searchButton);
        
        // Statistics Panel
        JPanel statsPanel = new JPanel();
        statsPanel.setBounds(50, 600, 800, 80);
        statsPanel.setLayout(null);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Today's Summary"));
        add(statsPanel);

        bookedRoomsLabel = new JLabel("Booked Rooms: Loading...");
        bookedRoomsLabel.setBounds(20, 20, 200, 25);
        statsPanel.add(bookedRoomsLabel);

        availableRoomsLabel = new JLabel("Available Rooms: Loading...");
        availableRoomsLabel.setBounds(250, 20, 200, 25);
        statsPanel.add(availableRoomsLabel);

        guestCountLabel = new JLabel("Guests: Loading...");
        guestCountLabel.setBounds(500, 20, 200, 25);
        statsPanel.add(guestCountLabel);

        JButton refreshStatsButton = new JButton("Refresh Stats");
        refreshStatsButton.setBounds(650, 20, 120, 30);
        statsPanel.add(refreshStatsButton);
        refreshStatsButton.addActionListener(e -> loadStatistics());

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"Room ID", "Type", "Price", "Status"}, 0);
        roomTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBounds(50, 100, 800, 300);
        add(scrollPane);

        // Buttons
        JButton addButton = new JButton("Add Room");
        JButton updateButton = new JButton("Update Room");
        JButton deleteButton = new JButton("Delete Room");
        JButton refreshButton = new JButton("Refresh");

        addButton.setBounds(50, 450, 150, 30);
        updateButton.setBounds(220, 450, 150, 30);
        deleteButton.setBounds(390, 450, 150, 30);
        refreshButton.setBounds(560, 450, 150, 30);

        add(addButton);
        add(updateButton);
        add(deleteButton);
        add(refreshButton);
        
        //OTHER BUTTONS
        JButton bookedRooms = new JButton("Booked Rooms & Customer");
        JButton checkIn = new JButton("Check - In");
        JButton checkOut = new JButton("Check - Out");
        JButton bookingHistory = new JButton("Booking History");
        JButton performance = new JButton("Performance");
        JButton logOut = new JButton("Log Out");
        
        bookedRooms.setBounds(1000, 100, 200, 30);
        bookingHistory.setBounds(1000, 400, 200, 30);
        performance.setBounds(1000, 500, 200, 30);
        logOut.setBounds(1000, 600, 200, 30);
        
        add(bookedRooms);
        add(bookingHistory);
        add(performance);
        add(logOut);
        
        

        // Button Listeners
        addButton.addActionListener(e -> new AddRoomForm().setVisible(true));
        updateButton.addActionListener(e -> updateRoom());
        deleteButton.addActionListener(e -> deleteRoom());
        refreshButton.addActionListener(e -> loadRooms());
        searchButton.addActionListener(e -> searchRooms());

        
        bookedRooms.addActionListener(e -> new BookedRooms().setVisible(true));
        logOut.addActionListener(e -> logOut());
        bookingHistory.addActionListener(e -> new BookingHistory().setVisible(true));
        performance.addActionListener(e -> new Performance().setVisible(true));
        // Load rooms on start
        loadRooms();
        loadStatistics();
    }
    private void logOut(){
        new LogInForm();
        dispose();
    }
       


    private void loadRooms() {
        tableModel.setRowCount(0);
        String query = "SELECT * FROM rooms";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("room_id"),
                        rs.getString("room_type"),
                        rs.getDouble("price"),
                        rs.getString("availability_status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading rooms.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStatistics() {
    try (Connection conn = Database.connect()) {
        // Get booked rooms count
        String bookedQuery = "SELECT COUNT(*) FROM rooms WHERE availability_status = 'Booked'";
        try (PreparedStatement stmt = conn.prepareStatement(bookedQuery);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                bookedRoomsLabel.setText("Booked Rooms: " + rs.getInt(1));
            }
        }

        // Get available rooms count
        String availableQuery = "SELECT COUNT(*) FROM rooms WHERE availability_status = 'Available'";
        try (PreparedStatement stmt = conn.prepareStatement(availableQuery);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                availableRoomsLabel.setText("Available Rooms: " + rs.getInt(1));
            }
        }

        // Get guest count
        String guestQuery = "SELECT COUNT(*) FROM customers";
        try (PreparedStatement stmt = conn.prepareStatement(guestQuery);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                guestCountLabel.setText("Guests: " + rs.getInt(1));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading statistics.", "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
    

    private void searchRooms() {
        tableModel.setRowCount(0);
        String query = "SELECT * FROM rooms WHERE 1=1";

        boolean hasRoomId = !searchRoomIdField.getText().isEmpty() && !searchRoomIdField.getText().equals("Room ID");
        boolean hasPrice = !searchPriceField.getText().isEmpty() && !searchPriceField.getText().equals("Price");

        if (hasRoomId) query += " AND room_id = ?";
        if (!searchTypeCombo.getSelectedItem().equals("All")) query += " AND room_type = ?";
        if (hasPrice) query += " AND price <= ?";
        if (!searchStatusCombo.getSelectedItem().equals("All")) query += " AND availability_status = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int index = 1;
            if (hasRoomId) stmt.setInt(index++, Integer.parseInt(searchRoomIdField.getText()));
            if (!searchTypeCombo.getSelectedItem().equals("All")) stmt.setString(index++, searchTypeCombo.getSelectedItem().toString());
            if (hasPrice) stmt.setDouble(index++, Double.parseDouble(searchPriceField.getText()));
            if (!searchStatusCombo.getSelectedItem().equals("All")) stmt.setString(index++, searchStatusCombo.getSelectedItem().toString());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("room_id"),
                        rs.getString("room_type"),
                        rs.getDouble("price"),
                        rs.getString("availability_status")
                });
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input for Room ID or Price. Please enter numbers only.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching rooms.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void updateRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow != -1) {
            int roomId = (int) tableModel.getValueAt(selectedRow, 0);
            new UpdateRoomForm(roomId).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Select a room to update.");
        }
    }

    private void deleteRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow != -1) {
            int roomId = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this room?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = Database.connect();
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM rooms WHERE room_id = ?")) {
                    stmt.setInt(1, roomId);
                    stmt.executeUpdate();
                    loadRooms();
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting room.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a room to delete.");
        }
    }

    public static void main(String[] args) {
    new AdminDashboard().setVisible(true);
}

}
