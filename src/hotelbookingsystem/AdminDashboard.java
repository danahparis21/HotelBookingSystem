package hotelbookingsystem;

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

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(900, 650);
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

        searchRoomIdField = new JTextField();
        searchRoomIdField.setBounds(100, 50, 80, 25);
        searchRoomIdField.setToolTipText("Room ID");
        add(searchRoomIdField);

        searchTypeCombo = new JComboBox<>(new String[]{"All", "Single", "Double", "Suite"});
        searchTypeCombo.setBounds(190, 50, 100, 25);
        add(searchTypeCombo);

        searchPriceField = new JTextField();
        searchPriceField.setBounds(300, 50, 80, 25);
        searchPriceField.setToolTipText("Price");
        add(searchPriceField);

        searchStatusCombo = new JComboBox<>(new String[]{"All", "Available", "Booked", "Under Maintenance"});
        searchStatusCombo.setBounds(390, 50, 140, 25);
        add(searchStatusCombo);

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(550, 50, 100, 25);
        add(searchButton);

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

        // Button Listeners
        addButton.addActionListener(e -> new AddRoomForm().setVisible(true));
        updateButton.addActionListener(e -> updateRoom());
        deleteButton.addActionListener(e -> deleteRoom());
        refreshButton.addActionListener(e -> loadRooms());
        searchButton.addActionListener(e -> searchRooms());

        // Load rooms on start
        loadRooms();
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

    private void searchRooms() {
        tableModel.setRowCount(0);
        String query = "SELECT * FROM rooms WHERE 1=1";

        if (!searchRoomIdField.getText().isEmpty()) query += " AND room_id = ?";
        if (!searchTypeCombo.getSelectedItem().equals("All")) query += " AND room_type = ?";
        if (!searchPriceField.getText().isEmpty()) query += " AND price <= ?";
        if (!searchStatusCombo.getSelectedItem().equals("All")) query += " AND availability_status = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            int index = 1;
            if (!searchRoomIdField.getText().isEmpty()) stmt.setInt(index++, Integer.parseInt(searchRoomIdField.getText()));
            if (!searchTypeCombo.getSelectedItem().equals("All")) stmt.setString(index++, searchTypeCombo.getSelectedItem().toString());
            if (!searchPriceField.getText().isEmpty()) stmt.setDouble(index++, Double.parseDouble(searchPriceField.getText()));
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
