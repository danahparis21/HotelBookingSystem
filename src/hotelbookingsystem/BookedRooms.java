package hotelbookingsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookedRooms extends JFrame {
    private JTable bookedRooms;
    private DefaultTableModel tableModel;
    private JLabel lblCustomerId, lblCustomerName, lblEmail, lblPhone, lblUserId;
    private JTextField txtCustomerId, txtCustomerName, txtEmail, txtPhone, txtUserId;

    public BookedRooms() {
        setTitle("Booked Rooms");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel titleLabel = new JLabel("Booked Rooms", SwingConstants.CENTER);
        titleLabel.setBounds(250, 10, 400, 30);
        add(titleLabel);

        // Table setup
        String[] columns = {"Booking ID", "Customer ID", "Room ID", "Room Type", "Price", "Check-In", "Check-Out", "Status", "Room Status"};
        tableModel = new DefaultTableModel(columns, 0);
        bookedRooms = new JTable(tableModel);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(bookedRooms);
        scrollPane.setBounds(30, 50, 540, 300);
        add(scrollPane);

        // Customer Information Panel
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(null);
        customerPanel.setBounds(600, 50, 270, 200);
        add(customerPanel);

        lblCustomerId = new JLabel("Customer ID:");
        lblCustomerName = new JLabel("Customer Name:");
        lblEmail = new JLabel("Email:");
        lblPhone = new JLabel("Phone:");
        lblUserId = new JLabel("User ID:");

        txtCustomerId = new JTextField();
        txtCustomerName = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();
        txtUserId = new JTextField();

        lblCustomerId.setBounds(10, 10, 100, 20);
        txtCustomerId.setBounds(120, 10, 130, 20);
        lblCustomerName.setBounds(10, 40, 100, 20);
        txtCustomerName.setBounds(120, 40, 130, 20);
        lblEmail.setBounds(10, 70, 100, 20);
        txtEmail.setBounds(120, 70, 130, 20);
        lblPhone.setBounds(10, 100, 100, 20);
        txtPhone.setBounds(120, 100, 130, 20);
        lblUserId.setBounds(10, 130, 100, 20);
        txtUserId.setBounds(120, 130, 130, 20);

        txtCustomerId.setEditable(false);
        txtCustomerName.setEditable(false);
        txtEmail.setEditable(false);
        txtPhone.setEditable(false);
        txtUserId.setEditable(false);

        customerPanel.add(lblCustomerId);
        customerPanel.add(txtCustomerId);
        customerPanel.add(lblCustomerName);
        customerPanel.add(txtCustomerName);
        customerPanel.add(lblEmail);
        customerPanel.add(txtEmail);
        customerPanel.add(lblPhone);
        customerPanel.add(txtPhone);
        customerPanel.add(lblUserId);
        customerPanel.add(txtUserId);

        // Close Button
        JButton closeButton = new JButton("Close");
        closeButton.setBounds(350, 370, 100, 30);
        add(closeButton);
        closeButton.addActionListener(e -> dispose());

        // Fetch bookings from the database
        fetchBookings();
        
        // Check-In Button
        JButton checkInButton = new JButton("Check In");
        checkInButton.setBounds(600, 270, 120, 30);
        add(checkInButton);
        checkInButton.setEnabled(false); // Disabled until a row is selected
        
        

        // Check-Out Button
        JButton checkOutButton = new JButton("Check Out");
        checkOutButton.setBounds(750, 270, 120, 30);
        add(checkOutButton);
        checkOutButton.setEnabled(false); // Disabled until a room is checked in


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
                       "JOIN rooms r ON b.room_id = r.room_id WHERE r.availability_status = 'Booked'";
      

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
