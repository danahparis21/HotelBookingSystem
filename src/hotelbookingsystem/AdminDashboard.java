package hotelbookingsystem;

import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;

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
        getContentPane().setBackground(Color.decode("#2c2d2f")); // Set background color

        // Header Image
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/header1.png"));
        JLabel header = new JLabel(icon);
        header.setBounds(0, 0, 1920, 70);
        add(header);
        
        addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            header.setBounds((getWidth() - icon.getIconWidth()) / 2, 0, icon.getIconWidth(), icon.getIconHeight());

        }
    });

        JPanel panel = new JPanel();
        panel.setBounds(20, 100, 1200, 90);
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        panel.setBorder(new LineBorder(Color.GRAY, 1));
        add(panel);
        
        // Modernized Labels & Inputs
       
        // Search Panel
        
        JLabel searchLabel = createLabel("Search", 20, 30, panel);
        searchRoomIdField = createTextField("Room ID", 90, 30, panel);
        

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

        

        
        

        createLabel("Type:", 220, 30, panel);
        searchTypeCombo = new JComboBox<>(new String[]{"All", "Single", "Double", "Suite"});
        searchTypeCombo.setBounds(290, 30, 100, 30);
        searchTypeCombo.setFont(new Font("Times New Roman", Font.BOLD, 18));
        panel.add(searchTypeCombo);

        searchPriceField = createTextField("Price", 450, 30, panel);
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
     

        createLabel("Status:", 600, 30, panel);
        searchStatusCombo = new JComboBox<>(new String[]{"All", "Available", "Booked", "Under Maintenance"});
        searchStatusCombo.setBounds(680, 30, 140, 30);
        searchStatusCombo.setFont(new Font("Times New Roman", Font.BOLD, 18));
        panel.add(searchStatusCombo);

        JButton searchButton = createButton("Search", 950, 20, panel);
        
        
        // Statistics Panel
        JPanel statsPanel = new JPanel();
        statsPanel.setBounds(20, 660, 1200, 90);
        statsPanel.setLayout(null);
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                "Today's Summary",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("Times New Roman", Font.BOLD, 14), 
                Color.DARK_GRAY
        ));
        add(statsPanel);

        // Labels with Elegant Font
        Font labelFont = new Font("Times New Roman", Font.BOLD, 20);

        bookedRoomsLabel = new JLabel("Booked Rooms: Loading...");
        bookedRoomsLabel.setFont(labelFont);
        bookedRoomsLabel.setBounds(30, 30, 250, 25);
        statsPanel.add(bookedRoomsLabel);

        availableRoomsLabel = new JLabel("Available Rooms: Loading...");
        availableRoomsLabel.setFont(labelFont);
        availableRoomsLabel.setBounds(300, 30, 250, 25);
        statsPanel.add(availableRoomsLabel);

        guestCountLabel = new JLabel("Guests: Loading...");
        guestCountLabel.setFont(labelFont);
        guestCountLabel.setBounds(600, 30, 200, 25);
        statsPanel.add(guestCountLabel);

        JButton refreshStatsButton = createButton("Refresh Stats", 950, 20, statsPanel);
        
        refreshStatsButton.addActionListener(e -> loadStatistics());

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"Room ID", "Type", "Price", "Status"}, 0);
        roomTable = new JTable(tableModel);
        styleTable(roomTable);
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBounds(20, 200, 1200, 350);
        add(scrollPane);

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(20, 560, 1200, 90);
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(null);
        buttonPanel.setBorder(new LineBorder(Color.GRAY, 1));
        add(buttonPanel);
        
        // Buttons
        JButton addButton = createButton("Add Room", 50, 20, buttonPanel);
        JButton updateButton = createButton("Update Room", 350, 20, buttonPanel);
        JButton deleteButton = createButton("Delete Room", 650, 20, buttonPanel);
        JButton refreshButton = createButton("Refresh", 950, 20, buttonPanel);
        
        

        
        //OTHER BUTTONS
        JPanel panel2 = new JPanel();
        panel2.setBounds(1250, 100, 250, 690);
        panel2.setBackground(Color.WHITE);
        panel2.setLayout(null);
        panel2.setBorder(new LineBorder(Color.GRAY, 1));
        add(panel2);
        
        
        JButton bookedRooms = createButton("Booked Rooms", 25, 50, panel2);
       
        JButton bookingHistory = createButton("Booking History", 25, 120, panel2);
        JButton performance = createButton("Performance", 25, 190, panel2);
        JButton logOut = createButton("Log Out", 25, 260, panel2);
        

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
    
    private void styleTable(JTable table) {
        // Table Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setBackground(new Color(0x393939)); // Dark Gray
        header.setForeground(Color.WHITE);
        header.setOpaque(true);

        // Table Body Styling
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(0x393939));
        table.setGridColor(new Color(0xD3D3D3)); // Light Gray Grid
        table.setShowGrid(false); // Hide default grid lines
        table.setIntercellSpacing(new Dimension(0, 0)); // Remove default spacing

        // Selection Styling
        table.setSelectionBackground(new Color(0x393939));
        table.setSelectionForeground(Color.WHITE);

        // Borderless Look
        table.setBorder(BorderFactory.createEmptyBorder());
    }
    
    private JLabel createLabel(String text, int x, int y, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Times New Roman", Font.BOLD, 20));
        label.setBounds(x, y, 150, 30);
        panel.add(label);
        return label;
    }

    private JTextField createTextField(String text, int x, int y, JPanel panel) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        textField.setBounds(x, y, 100, 35);
        panel.add(textField);
        return textField;
    }

    private JButton createButton(String text, int x, int y, JPanel panel) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBounds(x, y, 200, 50);

        // Default colors
        Color defaultBg = new Color(0x393939);
        Color defaultFg = Color.WHITE;
        Color hoverBg = Color.WHITE;
        Color hoverFg = new Color(0x393939);

        // Apply default styling
        button.setBackground(defaultBg);
        button.setForeground(defaultFg);
        button.setBorder(BorderFactory.createLineBorder(defaultBg, 2));
        button.setFocusPainted(false);
        button.setOpaque(true);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverBg);
                button.setForeground(hoverFg);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(defaultBg);
                button.setForeground(defaultFg);
            }
        });

        panel.add(button);
        return button;
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
