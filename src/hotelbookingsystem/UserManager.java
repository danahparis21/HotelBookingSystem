package hotelbookingsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {

    // Sign Up Method
      public static void signUp(String username, String password, String name, String email, String phone) {
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = Database.connect(); // Call Database.connect()
             PreparedStatement pstmt = conn.prepareStatement(query)) {

           String userQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, 'customer')";
            PreparedStatement userStmt = conn.prepareStatement(userQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, username);
            userStmt.setString(2, password); // 🔒 Hash this password later for security
            userStmt.executeUpdate();
            
            // Get the generated user_id
            ResultSet rs = userStmt.getGeneratedKeys();
            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

             // 2️⃣ Insert into customers table
            if (userId != -1) {
                String customerQuery = "INSERT INTO customers (name, email, phone_number, user_id) VALUES (?, ?, ?, ?)";
                PreparedStatement customerStmt = conn.prepareStatement(customerQuery);
                customerStmt.setString(1, name);
                customerStmt.setString(2, email);
                customerStmt.setString(3, phone);
                customerStmt.setInt(4, userId);
                customerStmt.executeUpdate();
            }

            System.out.println("User registered successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Log In Method
    public static String login(String username, String password) {
        String query = "SELECT role FROM users WHERE username = ? AND password = ?";

        try (Connection conn = Database.connect(); // Call Database.connect()
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role"); // Returns "admin" or "customer"
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Login failed
    }

    static int getCustomerID(String username) {
        int customerID = -1; // Default invalid ID

        String query = "SELECT c.customer_id FROM customers c " +
                       "JOIN users u ON c.user_id = u.user_id " +
                       "WHERE u.username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                customerID = rs.getInt("customer_id"); // Retrieve customer ID from database
                System.out.println("✅ Customer ID found: " + customerID);
        } else {
            System.out.println("No customer found for username: " + username);
        }
            

        } catch (SQLException e) {
            e.printStackTrace(); // Log errors properly in production
        }

        return customerID; 
    }
}
