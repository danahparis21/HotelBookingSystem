package hotelbookingsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ViewBookings extends JFrame {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JButton cancelButton;
    private int customerID;
    private int selectedBookingID = -1; // Store selected booking ID

    public ViewBookings(int customerID) {
        this.customerID = customerID;
        setTitle("My Bookings");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setLayout(null);

        // Title Label
        JLabel titleLabel = new JLabel("My Bookings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(230, 10, 200, 30);
        add(titleLabel);

        // Table setup
        String[] columns = {"Booking ID", "Customer Name", "Room Type", "Price", "Check-In", "Check-Out", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        bookingsTable = new JTable(tableModel);
        bookingsTable.removeColumn(bookingsTable.getColumnModel().getColumn(0)); // Hide Booking ID

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBounds(30, 50, 540, 250);
        add(scrollPane);

        // Cancel Booking Button
        cancelButton = new JButton("Cancel Booking");
        cancelButton.setBounds(150, 320, 150, 30);
        cancelButton.setEnabled(false); // Disabled by default
        add(cancelButton);

        // Close Button
        JButton closeButton = new JButton("Close");
        closeButton.setBounds(350, 320, 100, 30);
        add(closeButton);

        // Close Action
        closeButton.addActionListener(e -> dispose());

        // Fetch bookings
        fetchBookings();

        // Table row selection listener
        bookingsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = bookingsTable.getSelectedRow();
                if (row != -1) {
                    selectedBookingID = (int) tableModel.getValueAt(row, 0);
                    String status = (String) tableModel.getValueAt(row, 6);
                    cancelButton.setEnabled(status.equals("Confirmed"));
                }
            }
        });

        // Cancel Booking Action
        cancelButton.addActionListener(e -> cancelBooking());

        setVisible(true);
    }

    private void fetchBookings() {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT b.booking_id, c.name, r.room_type, r.price, b.check_in_date, b.check_out_date, b.status " +
                         "FROM bookings b " +
                         "JOIN customers c ON b.customer_id = c.customer_id " +
                         "JOIN rooms r ON b.room_id = r.room_id " +
                         "WHERE b.customer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0); // Clear table before adding new data

            while (rs.next()) {
                int bookingID = rs.getInt("booking_id");
                String name = rs.getString("name");
                String roomType = rs.getString("room_type");
                String price = rs.getString("price");
                String checkIn = rs.getString("check_in_date");
                String checkOut = rs.getString("check_out_date");
                String status = rs.getString("status"); // "Pending" or "Checked In"

                tableModel.addRow(new Object[]{bookingID, name, roomType, price, checkIn, checkOut, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + e.getMessage());
        }
    }

    private void cancelBooking() {
    if (selectedBookingID == -1) {
        JOptionPane.showMessageDialog(this, "Please select a booking to cancel.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this booking?", 
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        try (Connection conn = Database.connect()) {
            conn.setAutoCommit(false); // Start transaction
            
            // Get room_id before deleting the booking
            String getRoomIdSQL = "SELECT room_id FROM bookings WHERE booking_id = ?";
            PreparedStatement getRoomStmt = conn.prepareStatement(getRoomIdSQL);
            getRoomStmt.setInt(1, selectedBookingID);
            ResultSet rs = getRoomStmt.executeQuery();
            
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Error: Room not found for this booking.");
                return;
            }
            int roomID = rs.getInt("room_id");
            
            // Delete the booking
            String deleteBookingSQL = "DELETE FROM bookings WHERE booking_id = ? AND status = 'Confirmed'";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteBookingSQL);
            deleteStmt.setInt(1, selectedBookingID);
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected > 0) {
                // Update room availability_status
                String updateRoomSQL = "UPDATE rooms SET availability_status = 'Available' WHERE room_id = ?";
                PreparedStatement updateRoomStmt = conn.prepareStatement(updateRoomSQL);
                updateRoomStmt.setInt(1, roomID);
                updateRoomStmt.executeUpdate();

                conn.commit(); // Commit transaction

                JOptionPane.showMessageDialog(this, "Booking canceled successfully. Room is now available.");
                fetchBookings(); // Refresh table
                cancelButton.setEnabled(false);
            } else {
                conn.rollback(); // Rollback if no rows affected
                JOptionPane.showMessageDialog(this, "Booking cannot be canceled. It may have already been checked in.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error canceling booking: " + e.getMessage());
        }
    }
}

}
