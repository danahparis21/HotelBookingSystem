
package hotelbookingsystem;


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Hotel Booking System");
        System.out.println("1. Sign Up");
        System.out.println("2. Log In");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            // Sign Up Process
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            System.out.print("Enter role (admin/customer): ");
            String role = scanner.nextLine().toLowerCase();

            if (role.equals("admin") || role.equals("customer")) {
                //UserManager.signUp(username, password, role);
            } else {
                System.out.println("Invalid role. Please choose 'admin' or 'customer'.");
            }
        } else if (choice == 2) {
            // Log In Process
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            String role = UserManager.login(username, password);

            if (role != null) {
                System.out.println("Login successful! Welcome, " + username);
                if (role.equals("admin")) {
                    System.out.println("Opening Admin Features...");
                    // Call admin menu (add/view rooms, manage bookings)
                } else {
                    System.out.println("Opening Customer Features...");
                    // Call customer menu (search rooms, book, cancel)
                }
            } else {
                System.out.println("Login failed. Incorrect username or password.");
            }
        } else {
            System.out.println("Invalid choice.");
        }

        scanner.close();
    }

}
