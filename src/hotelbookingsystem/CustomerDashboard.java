package hotelbookingsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomerDashboard extends JFrame {
    private JDateChooser checkInDate;
    private JTextField checkOutDate;
    private JSpinner duration;
    private JTextField adults, children, rooms;
    private JButton searchRooms, bookRoom, viewBookings,logOut;
    private JTable roomsTable;
    private DefaultTableModel tableModel;
    private int customerID = 1;

    public CustomerDashboard(int loggedInCustomerID) {
        if (loggedInCustomerID > 1) {
        this.customerID = loggedInCustomerID;
    }
    
        setTitle("Customer Dashboard");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        


        JLabel lblCheckIn = new JLabel("Check-in Date:");
        lblCheckIn.setBounds(30, 30, 100, 25);
        add(lblCheckIn);
        
        checkInDate = new JDateChooser();
        checkInDate.setBounds(140, 30, 150, 25);
        add(checkInDate);
        
        JLabel lblDuration = new JLabel("Duration (days):");
        lblDuration.setBounds(30, 70, 100, 25);
        add(lblDuration);
        
        duration = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        duration.setBounds(140, 70, 50, 25);
        add(duration);
        
        JLabel lblCheckOut = new JLabel("Check-out Date:");
        lblCheckOut.setBounds(30, 110, 100, 25);
        add(lblCheckOut);
        
        checkOutDate = new JTextField();
        checkOutDate.setBounds(140, 110, 150, 25);
        checkOutDate.setEditable(false);
        add(checkOutDate);
        
        JLabel lblAdults = new JLabel("Adults:");
        lblAdults.setBounds(30, 150, 50, 25);
        add(lblAdults);
        
        adults = new JTextField("1");
        adults.setBounds(80, 150, 50, 25);
        add(adults);
        
        JLabel lblChildren = new JLabel("Children:");
        lblChildren.setBounds(150, 150, 60, 25);
        add(lblChildren);
        
        children = new JTextField("0");
        children.setBounds(220, 150, 50, 25);
        add(children);
        
        JLabel lblRooms = new JLabel("Rooms:");
        lblRooms.setBounds(30, 190, 50, 25);
        add(lblRooms);
        
        rooms = new JTextField("1");
        rooms.setBounds(80, 190, 50, 25);
        add(rooms);
        
        searchRooms = new JButton("Search Rooms");
        searchRooms.setBounds(30, 230, 150, 30);
        add(searchRooms);

        bookRoom = new JButton("Book Room");
        bookRoom.setBounds(200, 230, 150, 30);
        add(bookRoom);
        
        viewBookings = new JButton("View My Bookings");
        viewBookings.setBounds(1000, 230, 150, 30);
        add(viewBookings);
        
        logOut = new JButton("Log Out");
        logOut.setBounds(1000, 300, 150, 30);
        add(logOut);
        
        String[] columns = {"Room ID", "Type", "Price", "Availability"};
        tableModel = new DefaultTableModel(columns, 0);
        roomsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        scrollPane.setBounds(30, 280, 700, 250);
        add(scrollPane);
        
        // Calculate check-out date based on duration
        duration.addChangeListener(e -> updateCheckOutDate());
        checkInDate.getDateEditor().addPropertyChangeListener(evt -> updateCheckOutDate());

        // Search button action
        searchRooms.addActionListener(e -> searchRooms());
        bookRoom.addActionListener(e -> bookSelectedRoom());
        viewBookings.addActionListener(e -> new ViewBookings(loggedInCustomerID));
        logOut.addActionListener(e -> logOut());
    }
    
    private void logOut(){
        new LogInForm();
        dispose();
    }

    private void updateCheckOutDate() {
        if (checkInDate.getDate() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(checkInDate.getDate());
            cal.add(Calendar.DAY_OF_MONTH, (Integer) duration.getValue());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            checkOutDate.setText(sdf.format(cal.getTime()));
        }
    }

    private void searchRooms() {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT room_id, room_type, price, availability_status FROM rooms WHERE availability_status = 'available'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            tableModel.setRowCount(0); // Clear table before adding new data
            
            while (rs.next()) {
                String roomID = rs.getString("room_id");
                String roomType = rs.getString("room_type");
                String price = rs.getString("price");
                String status = rs.getString("availability_status");
                tableModel.addRow(new Object[]{roomID, roomType, price, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching rooms: " + e.getMessage());
        }
    }

    private void bookSelectedRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room first.");
            return;
        }

        String roomID = tableModel.getValueAt(selectedRow, 0).toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String checkIn = sdf.format(checkInDate.getDate());
        String checkOut = checkOutDate.getText();

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Confirm booking?\n\nRoom: " + roomID + "\nCheck-in: " + checkIn + "\nCheck-out: " + checkOut, 
            "Confirm Booking", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Database.connect()) {
                String sql = "INSERT INTO bookings (customer_id, room_id, check_in_date, check_out_date, status) VALUES (?, ?, ?, ?, 'Confirmed')";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, customerID);  // Use the correct customerID
                stmt.setString(2, roomID);
                stmt.setString(3, checkIn);
                stmt.setString(4, checkOut);
                stmt.executeUpdate();

                // Update room status
                String updateSQL = "UPDATE rooms SET availability_status = 'booked' WHERE room_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                updateStmt.setString(1, roomID);
                updateStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Room booked successfully!");
                searchRooms(); // Refresh room list
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Booking failed: " + e.getMessage());
            }
        }
    }


    public static void main(String[] args) {
        int loggedInCustomerID = 1; 
        SwingUtilities.invokeLater(() -> new CustomerDashboard(loggedInCustomerID).setVisible(true));
    }

}
