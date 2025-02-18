package hotelbookingsystem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;

public class BookedRooms extends JFrame {
    private JTable bookedRooms;
    private DefaultTableModel tableModel;
    private JLabel lblCustomerId, lblCustomerName, lblEmail, lblPhone, lblUserId;
    private JTextField txtCustomerId, txtCustomerName, txtEmail, txtPhone, txtUserId;

    public BookedRooms() {
        setTitle("Booked Rooms");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE); // Set background color

        JLabel titleLabel = new JLabel("Booked Rooms", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        titleLabel.setBounds(350, 10, 200, 30);
        add(titleLabel);

        // Table setup
        String[] columns = {"Booking ID", "Customer ID", "Room ID", "Room Type", "Price", "Check-In", "Check-Out", "Status", "Room Status"};
        tableModel = new DefaultTableModel(columns, 0);
        bookedRooms = new JTable(tableModel);
        styleTable(bookedRooms);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(bookedRooms);
        scrollPane.setBounds(30, 50, 650, 300);
        add(scrollPane);

        // Customer Information Panel
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(null);
        customerPanel.setBounds(700, 50, 270, 200);
        customerPanel.setBackground(Color.WHITE);
      
        customerPanel.setBorder(new LineBorder(Color.GRAY, 1));
        add(customerPanel);

        
        lblCustomerId = createLabel("Customer ID:", 10, 10, customerPanel);
         txtCustomerId = createTextField("", 130, 10, customerPanel);
        
         lblCustomerName = createLabel("Customer Name:", 10, 40, customerPanel);
         txtCustomerName = createTextField("", 130, 40, customerPanel);
        
         lblEmail = createLabel("Email:", 10, 70, customerPanel);
         txtEmail = createTextField("", 130, 70, customerPanel);
        
         lblPhone = createLabel("Phone:", 10, 100, customerPanel);
         txtPhone = createTextField("", 130, 100, customerPanel);
        
         lblUserId = createLabel("User ID:", 10, 130, customerPanel);
         txtUserId = createTextField("", 130, 130, customerPanel);

        txtCustomerId.setEditable(false);
        txtCustomerName.setEditable(false);
        txtEmail.setEditable(false);
        txtPhone.setEditable(false);
        txtUserId.setEditable(false);

        

        // Close Button
        JButton closeButton = createButton("Close", 450, 400);
       
        
        closeButton.addActionListener(e -> dispose());

        // Fetch bookings from the database
        fetchBookings();
        
        // Check-In Button
        JButton checkInButton = createButton("Check In", 700, 270);
        
        checkInButton.setEnabled(false); // Disabled until a row is selected
        
        

        // Check-Out Button
       
        JButton checkOutButton = createButton("Check Out", 820, 270);
       
        checkOutButton.setEnabled(false); 


        // Table row selection listener
        bookedRooms.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = bookedRooms.getSelectedRow();
                if (selectedRow != -1) {
                    String customerId = tableModel.getValueAt(selectedRow, 1).toString();
                    loadCustomerDetails(customerId);
                    // Get booking and room status
                    String bookingStatus = tableModel.getValueAt(selectedRow, 7).toString();
                    String roomStatus = tableModel.getValueAt(selectedRow, 8).toString();

                    // Enable Check-In only if booking is pending
                    checkInButton.setEnabled(bookingStatus.equalsIgnoreCase("Confirmed"));

                    // Enable Check-Out only if checked in
                    checkOutButton.setEnabled(bookingStatus.equalsIgnoreCase("Confirmed"));
                        }
                    }
        });
        
        checkInButton.addActionListener(e -> {
            int selectedRow = bookedRooms.getSelectedRow();
            if (selectedRow != -1) {
                int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
                int roomId = (int) tableModel.getValueAt(selectedRow, 2);

                try (Connection conn = Database.connect();)
                {

                    JOptionPane.showMessageDialog(this, "Guest Checked In Successfully!");
                    fetchBookings(); // Refresh table
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error during check-in!", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        checkOutButton.addActionListener(e -> {
            int selectedRow = bookedRooms.getSelectedRow();
            if (selectedRow != -1) {
                int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
                int roomId = (int) tableModel.getValueAt(selectedRow, 2);

                String updateBooking = "UPDATE bookings SET status = 'Completed' WHERE booking_id = ?";
                String updateRoom = "UPDATE rooms SET availability_status = 'Under Maintenance' WHERE room_id = ?";

                try (Connection conn = Database.connect();
                     PreparedStatement stmt = conn.prepareStatement(updateBooking);
                     PreparedStatement stmtRoom = conn.prepareStatement(updateRoom)) {

                    stmt.setInt(1, bookingId);
                    stmt.executeUpdate();

                    stmtRoom.setInt(1, roomId);
                    stmtRoom.executeUpdate();

                    generateReceipt(selectedRow); // Generate and display receipt
                    JOptionPane.showMessageDialog(this, "Guest Checked Out Successfully!");
                    fetchBookings(); // Refresh table
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error during check-out!", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


    }
    private JLabel createLabel(String text, int x, int y, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Times New Roman", Font.BOLD, 16));
        label.setBounds(x, y, 150, 30);
        panel.add(label);
        return label;
    }

    private JTextField createTextField(String text, int x, int y, JPanel panel) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        textField.setBounds(x, y, 100, 30);
        panel.add(textField);
        return textField;
    }

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBounds(x, y, 100, 40);

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

       add(button);
        return button;
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
    
    private void generateReceipt(int selectedRow) {
        String receipt = "========== Receipt ==========\n"
                + "Booking ID: " + tableModel.getValueAt(selectedRow, 0) + "\n"
                + "Customer ID: " + tableModel.getValueAt(selectedRow, 1) + "\n"
                + "Room ID: " + tableModel.getValueAt(selectedRow, 2) + "\n"
                + "Room Type: " + tableModel.getValueAt(selectedRow, 3) + "\n"
                + "Price: $" + tableModel.getValueAt(selectedRow, 4) + "\n"
                + "Check-In Date: " + tableModel.getValueAt(selectedRow, 5) + "\n"
                + "Check-Out Date: " + tableModel.getValueAt(selectedRow, 6) + "\n"
                + "Status: Completed\n"
                + "===================================";

        JOptionPane.showMessageDialog(this, receipt, "Booking Receipt", JOptionPane.INFORMATION_MESSAGE);
    }


     private void fetchBookings() {
        tableModel.setRowCount(0);
        String query = "SELECT b.booking_id, c.customer_id, r.room_id, r.room_type, r.price, " +
                       "b.check_in_date, b.check_out_date, b.status, r.availability_status " +
                       "FROM bookings b " +
                       "JOIN customers c ON b.customer_id = c.customer_id " +
                       "JOIN rooms r ON b.room_id = r.room_id WHERE r.availability_status = 'Booked' and b.status = 'Confirmed'";
      

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("room_id"),
                        rs.getString("room_type"),
                        rs.getDouble("price"),
                        rs.getString("check_in_date"),
                        rs.getString("check_out_date"),
                        rs.getString("status"),
                        rs.getString("availability_status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading booked rooms.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
     
    private void loadCustomerDetails(String customerId) {
        String query = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtCustomerId.setText(rs.getString("customer_id"));
                txtCustomerName.setText(rs.getString("name"));
                txtEmail.setText(rs.getString("email"));
                txtPhone.setText(rs.getString("phone_number"));
                txtUserId.setText(rs.getString("user_id"));
            } else {
                txtCustomerId.setText("");
                txtCustomerName.setText("");
                txtEmail.setText("");
                txtPhone.setText("");
                txtUserId.setText("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customer details.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new BookedRooms().setVisible(true);
    }
}
