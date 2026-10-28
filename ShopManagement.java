import java.sql.*;
import java.util.Scanner;

public class ShopManagement {

    static final String URL =
        "jdbc:mysql://localhost:3306/shopdb";
    static final String USER = "root";
    static final String PASSWORD = "your_password";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try (Connection con = DriverManager.getConnection(
                URL, USER, PASSWORD)) {

            while (true) {
                System.out.println("\n--- SHOP MANAGEMENT ---");
                System.out.println("1. Add Product");
                System.out.println("2. Sell Product");
                System.out.println("3. Display All Products");
                System.out.println("4. Low Stock Products");
                System.out.println("5. Search Product");
                System.out.println("6. Exit");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1: {
                        System.out.print("Product ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Product Name: ");
                        String name = sc.nextLine();

                        System.out.print("Price: ");
                        double price = sc.nextDouble();

                        System.out.print("Quantity: ");
                        int qty = sc.nextInt();

                        if (price < 0 || qty < 0) {
                            System.out.println(
                                "Price and quantity cannot be negative.");
                            break;
                        }

                        String sql =
                            "INSERT INTO products VALUES (?, ?, ?, ?)";

                        try (PreparedStatement ps =
                                 con.prepareStatement(sql)) {
                            ps.setInt(1, id);
                            ps.setString(2, name);
                            ps.setDouble(3, price);
                            ps.setInt(4, qty);
                            ps.executeUpdate();

                            System.out.println("Product added successfully!");
                        }
                        break;
                    }

                    case 2: {
                        System.out.print("Enter Product ID: ");
                        int id = sc.nextInt();

                        System.out.print("Quantity sold: ");
                        int sold = sc.nextInt();

                        if (sold <= 0) {
                            System.out.println(
                                "Sale quantity must be positive.");
                            break;
                        }

                        String sql =
                            "UPDATE products SET quantity = quantity - ? " +
                            "WHERE id = ? AND quantity >= ?";

                        try (PreparedStatement ps =
                                 con.prepareStatement(sql)) {
                            ps.setInt(1, sold);
                            ps.setInt(2, id);
                            ps.setInt(3, sold);

                            if (ps.executeUpdate() > 0) {
                                System.out.println(
                                    "Sale successful! Stock updated.");
                            } else {
                                System.out.println(
                                    "Product not found or insufficient stock.");
                            }
                        }
                        break;
                    }

                    case 3: {
                        String sql = "SELECT * FROM products";

                        try (Statement st = con.createStatement();
                             ResultSet rs = st.executeQuery(sql)) {

                            while (rs.next()) {
                                System.out.println(
                                    rs.getInt("id") + " | " +
                                    rs.getString("name") + " | Rs." +
                                    rs.getDouble("price") + " | Qty: " +
                                    rs.getInt("quantity"));
                            }
                        }
                        break;
                    }

                    case 4: {
                        String sql =
                            "SELECT * FROM products WHERE quantity < 10";

                        try (Statement st = con.createStatement();
                             ResultSet rs = st.executeQuery(sql)) {

                            boolean found = false;

                            while (rs.next()) {
                                found = true;
                                System.out.println(
                                    rs.getInt("id") + " | " +
                                    rs.getString("name") + " | Qty: " +
                                    rs.getInt("quantity"));
                            }

                            if (!found) {
                                System.out.println(
                                    "No low-stock products.");
                            }
                        }
                        break;
                    }

                    case 5: {
                        System.out.print("Enter ID or product name: ");
                        String key = sc.nextLine();

                        String sql =
                            "SELECT * FROM products WHERE " +
                            "CAST(id AS CHAR) = ? OR name LIKE ?";

                        try (PreparedStatement ps =
                                 con.prepareStatement(sql)) {
                            ps.setString(1, key);
                            ps.setString(2, "%" + key + "%");

                            try (ResultSet rs = ps.executeQuery()) {
                                while (rs.next()) {
                                    System.out.println(
                                        rs.getInt("id") + " | " +
                                        rs.getString("name") + " | Rs." +
                                        rs.getDouble("price") + " | Qty: " +
                                        rs.getInt("quantity"));
                                }
                            }
                        }
                        break;
                    }

                    case 6:
                        System.out.println("Application closed.");
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}