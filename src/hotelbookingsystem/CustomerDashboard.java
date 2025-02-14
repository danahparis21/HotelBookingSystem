package hotelbookingsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.border.EmptyBorder;

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
        getContentPane().setBackground(new Color(248, 247, 246)); // Set background color

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

        JLabel lblCheckIn = createLabel("Check-in Date:", 30, 100);
        checkInDate = new JDateChooser();
        checkInDate.setBounds(140, 100, 150, 30);
        add(checkInDate);

        JLabel lblDuration = createLabel("Duration (days):", 30, 150);
        duration = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        duration.setBounds(140, 150, 50, 30);
        add(duration);

        JLabel lblCheckOut = createLabel("Check-out Date:", 30, 200);
        checkOutDate = new JTextField();
        checkOutDate.setBounds(140, 200, 150, 30);
        checkOutDate.setEditable(false);
        add(checkOutDate);

        JLabel lblAdults = createLabel("Adults:", 30, 250);
        adults = createTextField("1", 80, 250);

        JLabel lblChildren = createLabel("Children:", 150, 250);
        children = createTextField("0", 220, 250);

        JLabel lblRooms = createLabel("Rooms:", 30, 300);
        rooms = createTextField("1", 80, 300);

        searchRooms = createButton("Search Rooms", 30, 350);
        bookRoom = createButton("Book Room", 200, 350);
        

        String[] columns = {"Room ID", "Type", "Price", "Availability"};
        tableModel = new DefaultTableModel(columns, 0);
        roomsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        scrollPane.setBounds(30, 420, 700, 300);
        add(scrollPane);
        
        viewBookings = createButton("View My Bookings", 1000, 350);
        logOut = createButton("Log Out", 1000, 400);
        
        // Calculate check-out date based on duration
        duration.addChangeListener(e -> updateCheckOutDate());
        checkInDate.getDateEditor().addPropertyChangeListener(evt -> updateCheckOutDate());

        // Search button action
        searchRooms.addActionListener(e -> searchRooms());
        bookRoom.addActionListener(e -> bookSelectedRoom());
        viewBookings.addActionListener(e -> new ViewBookings(loggedInCustomerID));
        logOut.addActionListener(e -> logOut());
    }
    
    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 120, 30);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(new Color(50, 50, 50));
        panel.add(label);
        return label;
    }

    private JTextField createTextField(String text, int x, int y) {
        JTextField textField = new JTextField(text);
        textField.setBounds(x, y, 50, 30);
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(textField);
        return textField;
    }

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 170, 40);
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(5, 10, 5, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(button);
        return button;
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
