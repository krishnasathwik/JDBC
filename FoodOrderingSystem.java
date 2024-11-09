import java.sql.*;
import java.util.Scanner;

public class FoodOrderingSystem {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/project?useSSL=false";
    static final String USER = "root";
    static final String PASSWORD = "password";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        Scanner scanner = new Scanner(System.in);
        String createCustomersTable = "CREATE TABLE customers (" +
                "customer_id INT PRIMARY KEY," +
                "first_name VARCHAR(50) NOT NULL," +
                "last_name VARCHAR(50) NOT NULL," +
                "email VARCHAR(100) NOT NULL," +
                "phone_number VARCHAR(20)," +
                "address VARCHAR(255)" +
                ");";

        String createFoodItemsTable = "CREATE TABLE food_items (" +
                "item_id INT PRIMARY KEY," +
                "item_name VARCHAR(255) NOT NULL," +
                "price DECIMAL(10, 2) NOT NULL" +
                ");";

        String createFoodTypesTable = "CREATE TABLE food_types (" +
                "type_id INT PRIMARY KEY," +
                "type_name VARCHAR(50) NOT NULL" +
                ");";

        String createCustomerOrdersTable = "CREATE TABLE customer_orders (" +
                "customer_id INT NOT NULL," +
                "order_date DATE NOT NULL," +
                "total_amount DECIMAL(10,2) NOT NULL," +
                "PRIMARY KEY (customer_id, order_date)" +  // Using customer_id and order_date as the primary key
                ");";

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            stmt = conn.createStatement();
            conn.setAutoCommit(false);
            // Determine user type
            System.out.println("Are you a:");
            System.out.println("1. Customer");
            System.out.println("2. Manager");
            int userType = scanner.nextInt();

            // Main menu based on user type
            if (userType == 1) {
                System.out.println("Enter your customer-id: ");
                int customerId = scanner.nextInt();
                if (isCustomerExist(conn, customerId)) {
                    System.out.println("Welcome back!");
                } else {
                    System.out.println("Welcome. Please register.");
                    registerCustomer(conn, scanner);
                }
                // Customer menu
                System.out.println("1. View Menu");
                System.out.println("2. Place Order");
                System.out.println("3. Exit");
                System.out.println("4. Ordered Menu");

                // User input
                int choice = getUserChoice(scanner);

                switch (choice) {
                    case 1:
                        displayMenu(stmt);
                        break;
                    case 2:
                        placeOrder(conn, stmt, scanner);
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        break;
                    case 4:
                        orderbypricing(conn);
                        break;   
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } else if (userType == 2) {
                // Manager menu
                System.out.println("1. View Menu");
                System.out.println("2. Place Order");
                System.out.println("3. Update Menu");
                System.out.println("4. Delete Menu");
                System.out.println("5. Exit");
                System.out.println("6.Add Dish");
                System.out.println("7. Order History");

                // User input
                int choice = getUserChoice(scanner);

                switch (choice) {
                    case 1:
                        displayMenu(stmt);
                        break;
                    case 2:
                        placeOrder(conn, stmt, scanner);
                        break;
                    case 3:
                        updateMenu(conn, stmt, scanner);
                        break;
                    case 4:
                        deleteMenu(conn, stmt, scanner);
                        break;
                    case 6:
                        addDish(conn, stmt, scanner);
                        break;
                    case 7:
                        viewOrders(conn, stmt);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } else {
                System.out.println("Invalid user type. Exiting...");
            }
        } catch (SQLException se) {
            se.printStackTrace();
            if (conn != null) {
                try {
                    System.out.println("Rolling back changes...");
                    conn.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.out.println("Rolling back changes...");
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
                scanner.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static int getUserChoice(Scanner scanner) {
        return scanner.nextInt();
    }

    public static void displayMenu(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT * FROM food_items");
        System.out.println("Menu:");
        System.out.println("-----------------------------------------------------");
        System.out.printf("%-10s %-30s %-10s\n", "Item ID", "Item Name", "Price");
        System.out.println("-----------------------------------------------------");
        while (rs.next()) {
            int itemId = rs.getInt("item_id");
            String itemName = rs.getString("item_name");
            double price = rs.getDouble("price");
            System.out.printf("%-10d %-30s %-10.2f\n", itemId, itemName, price);
        }
        System.out.println("-----------------------------------------------------");

        // Close the ResultSet and statement
        rs.close();
        stmt.close();
    }


    public static void registerCustomer(Connection conn, Scanner scanner) throws SQLException {
        try {
            System.out.print("Enter your first name: ");
            String firstName = scanner.next();
            System.out.print("Enter your last name: ");
            String lastName = scanner.next();
            System.out.print("Enter your email: ");
            String email = scanner.next();
            System.out.print("Enter your phone number: ");
            String phoneNumber = scanner.next();
            System.out.print("Enter your address: ");
            String address = scanner.next();
    
            String insertCustomerSql = "INSERT INTO customers (customer_id, first_name, last_name, email, phone_number, address) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertCustomerSql);
            pstmt.setInt(1, getNextCustomerId(conn)); // You need a method to get the next available customer ID
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, email);
            pstmt.setString(5, phoneNumber);
            pstmt.setString(6, address);
    
            int rowsAffected = pstmt.executeUpdate();
    
            if (rowsAffected > 0) {
                System.out.println("Registration successful. Please note your customer ID.");
                conn.commit(); // Committing changes if registration is successful
            } else {
                System.out.println("Failed to register. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback(); // Rolling back changes if an error occurs during registration
        }
    }
    

    public static int getNextCustomerId(Connection conn) throws SQLException {
        String query = "SELECT MAX(customer_id) AS max_id FROM customers";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            int maxId = rs.getInt("max_id");
            return maxId + 1;
        }
        return 1; // If no customers exist yet
    }
    public static void orderbypricing(Connection conn) throws SQLException {
        String orderquery = "SELECT * from food_items order by price";
        PreparedStatement pstmt = conn.prepareStatement(orderquery);
        ResultSet rs = pstmt.executeQuery(); // Use executeQuery() for SELECT queries

        System.out.println("Here is the menu in the order of price: ");
        System.out.println("-----------------------------------------------------");
        System.out.printf("%-10s %-30s %-10s\n", "Item ID", "Item Name", "Price");
        System.out.println("-----------------------------------------------------");

        // Process each row of the result set if needed
        // For example, you can print out the details of each menu item
        while (rs.next()) {
            int itemId = rs.getInt("item_id");
            String itemName = rs.getString("item_name");
            double price = rs.getDouble("price");
            System.out.printf("%-10d %-30s %.2f\n", itemId, itemName, price); // Always display two decimal places
        }

        System.out.println("-----------------------------------------------------");

        conn.commit();
        System.out.println("Commit completed.");

        // Close resources
        rs.close();
        pstmt.close();
    }




    public static void updateMenu(Connection conn, Statement stmt, Scanner scanner) throws SQLException {
        // Display current menu
        displayMenu(stmt);

        // Get item ID and new price from user
        System.out.print("Enter the ID of the item you want to update: ");
        int itemId = scanner.nextInt();
        System.out.print("Enter the new price for the item: ");
        double newPrice = scanner.nextDouble();

        // Update the menu item
        String sql = "UPDATE food_items SET price = ? WHERE item_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setDouble(1, newPrice);
        pstmt.setInt(2, itemId);
        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Menu item updated successfully.");
        } else {
            System.out.println("Failed to update menu item. Please check the item ID.");
        }
        displayMenu(pstmt);
        conn.commit();
        System.out.println("Com");
        pstmt.close();
    }

    public static void addDish(Connection conn, Statement stmt, Scanner scanner) throws SQLException {
        System.out.print("Enter the item_id: ");
        int itemId = scanner.nextInt();
        System.out.print("Enter the name of the new dish: ");
        String itemName = scanner.next();
        System.out.print("Enter the price of the new dish: ");
        double price = scanner.nextDouble();

        String insertDishSql = "INSERT INTO food_items (item_id, item_name, price) VALUES (?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(insertDishSql);
        pstmt.setInt(1, itemId);
        pstmt.setString(2, itemName);
        pstmt.setDouble(3, price);

        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Dish added successfully.");
        } else {
            System.out.println("Failed to add dish.");
        }
        displayMenu(stmt);
        conn.commit();
        System.out.println("Com");
        pstmt.close();
    }

    public static void deleteMenu(Connection conn, Statement stmt, Scanner scanner) throws SQLException {
        // Display current menu
        displayMenu(stmt);

        // Get item ID from user
        System.out.print("Enter the ID of the item you want to delete: ");
        int itemId = scanner.nextInt();

        // Delete the menu item
        String sql = "DELETE FROM food_items WHERE item_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, itemId);
        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Menu item deleted successfully.");
        } else {
            System.out.println("Failed to delete menu item. Please check the item ID.");
        }
        displayMenu(pstmt);
        conn.commit();
        System.out.println("Com");
        pstmt.close();
    }

    public static void placeOrder(Connection conn, Statement stmt, Scanner scanner) throws SQLException {
        // Display current menu
        displayMenu(stmt);

        // Get customer ID, order date, and items from user
        System.out.print("Enter your customer ID: ");
        int customerId = scanner.nextInt();
        System.out.print("Enter the order date (YYYY-MM-DD): ");
        String orderDate = scanner.next();
        System.out.print("Enter the total amount: ");
        double totalAmount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline character

        // Insert order into customer_orders table
        String insertOrderSql = "INSERT INTO customer_orders (customer_id, order_date, total_amount) VALUES (?, ?, ?)";

        PreparedStatement pstmt = conn.prepareStatement(insertOrderSql);

        // Assuming you have orderId value available
        pstmt.setInt(1, customerId);
        pstmt.setString(2, orderDate);
        pstmt.setDouble(3, totalAmount);

        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Order placed successfully.");
        } else {
            System.out.println("Failed to place order.");
        }
        conn.commit();
        System.out.println("Com");
        pstmt.close();

    }

    public static boolean isCustomerExist(Connection conn, int customerId) throws SQLException {
        String query = "SELECT * FROM customers WHERE customer_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, customerId);
        ResultSet rs = pstmt.executeQuery();
        return rs.next();
    }

    public static void viewOrders(Connection conn, Statement stmt) throws SQLException {
        String joinQuery = "SELECT c.customer_id, c.first_name, c.last_name, o.order_date, o.total_amount " +
                "FROM customer_orders o " +
                "JOIN customers c ON o.customer_id = c.customer_id";
        ResultSet rs = stmt.executeQuery(joinQuery);
        System.out.println("Orders:");
        System.out.println("-----------------------------------------------------");
        System.out.printf("%-10s %-15s %-15s %-15s %-10s\n", "Customer ID", "First Name", "Last Name", "Order Date",
                "Total Amount");
        System.out.println("-----------------------------------------------------");
        while (rs.next()) {
            int customerId = rs.getInt("customer_id");
            String firstName = rs.getString("first_name");
            String lastName = rs.getString("last_name");
            String orderDate = rs.getString("order_date");
            double totalAmount = rs.getDouble("total_amount");
            System.out.printf("%-10d %-15s %-15s %-15s %-10.2f\n", customerId, firstName, lastName, orderDate,
                    totalAmount);
        }
        System.out.println("-----------------------------------------------------");
    }

}
