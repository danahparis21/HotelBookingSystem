package hotelbookingsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;

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
        getContentPane().setBackground(Color.WHITE); // Set background color

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
        panel.setBounds(20, 100, 1200, 300);
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);
        panel.setBorder(new LineBorder(Color.GRAY, 1));
        add(panel);

        // Modernized Labels & Inputs
        JLabel lblCheckIn = createLabel("Check-in Date:", 30, 30, panel);
        checkInDate = new JDateChooser();
        checkInDate.setBounds(180, 30, 200, 35);
        checkInDate.setFont(new Font("Times New Roman", Font.BOLD, 20));
        panel.add(checkInDate);

        JLabel lblDuration = createLabel("Duration (days):", 450, 30, panel);
        duration = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        duration.setFont(new Font("Times New Roman", Font.BOLD, 20));
        duration.setBounds(600, 30, 100, 35);
        
        panel.add(duration);

        JLabel lblCheckOut = createLabel("Check-out Date:", 750, 30, panel);
        checkOutDate = new JTextField();
        checkOutDate.setBounds(900, 30, 200, 35);
        checkOutDate.setEditable(false);
        checkOutDate.setFont(new Font("Times New Roman", Font.BOLD, 20));
        panel.add(checkOutDate);

        JLabel lblAdults = createLabel("Adults:", 30, 100, panel);
        adults = createTextField("1", 180, 100, panel);

        JLabel lblChildren = createLabel("Children:", 450, 100, panel);
        children = createTextField("0", 600, 100, panel);

        JLabel lblRooms = createLabel("Rooms:", 30, 170, panel);
        rooms = createTextField("1", 180, 170, panel);

        searchRooms = createButton("Search Rooms", 400, 240, panel);
        bookRoom = createButton("Book Room", 650, 240, panel);
        

        String[] columns = {"Room ID", "Type", "Price", "Availability"};
        tableModel = new DefaultTableModel(columns, 0);
        roomsTable = new JTable(tableModel);
        styleTable(roomsTable);
        
        

        JScrollPane scrollPane = new JScrollPane(roomsTable);
        scrollPane.setBounds(20, 420, 1200, 360);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove ScrollPane border
        add(scrollPane);
        
        JPanel panel2 = new JPanel();
        panel2.setBounds(1250, 100, 250, 690);
        panel2.setBackground(Color.WHITE);
        panel2.setLayout(null);
        panel2.setBorder(new LineBorder(Color.GRAY, 1));
        add(panel2);
        
        
        viewBookings = createButton("View My Bookings", 25, 50, panel2);
       
        logOut = createButton("Log Out", 25, 120, panel2);
        
       

        
        // Calculate check-out date based on duration
        duration.addChangeListener(e -> updateCheckOutDate());
        checkInDate.getDateEditor().addPropertyChangeListener(evt -> updateCheckOutDate());

        // Search button action
        searchRooms.addActionListener(e -> searchRooms());
        bookRoom.addActionListener(e -> bookSelectedRoom());
        viewBookings.addActionListener(e -> new ViewBookings(loggedInCustomerID));
        logOut.addActionListener(e -> logOut());
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
