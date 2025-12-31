package Server.dao;

import Server.database.DatabaseConnection;
import Server.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PRODUCT DAO - Truy xuất MySQL (Dữ liệu CÓ cấu trúc)
 */
public class ProductDAO {

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Product product = extractProduct(rs);
                products.add(product);
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi getAllProducts: " + e.getMessage());
        }
        
        return products;
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ? OR category LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Product product = extractProduct(rs);
                products.add(product);
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi searchProducts: " + e.getMessage());
        }
        
        return products;
    }

    public boolean insertProduct(Product product) {
        String sql = "INSERT INTO products (id, name, category, price, quantity, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getId());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getCategory());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getQuantity());
            pstmt.setString(6, product.getDescription());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi insertProduct: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, category = ?, price = ?, " +
                     "quantity = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getCategory());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setInt(4, product.getQuantity());
            pstmt.setString(5, product.getDescription());
            pstmt.setString(6, product.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi updateProduct: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProduct(String id) {
        String sql = "DELETE FROM products WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi deleteProduct: " + e.getMessage());
            return false;
        }
    }

    public int getProductCount() {
        String sql = "SELECT COUNT(*) as total FROM products";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi getProductCount: " + e.getMessage());
        }
        
        return 0;
    }

    private Product extractProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getString("id"));
        product.setName(rs.getString("name"));
        product.setCategory(rs.getString("category"));
        product.setPrice(rs.getDouble("price"));
        product.setQuantity(rs.getInt("quantity"));
        product.setDescription(rs.getString("description"));
        return product;
    }
}