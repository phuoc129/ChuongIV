package Server.dao;

import Server.database.DatabaseConnection;
import Server.model.Discount;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DISCOUNT DAO - Quản lý mã giảm giá trong MySQL
 */
public class DiscountDAO {

    /**
     * Lấy tất cả mã giảm giá
     */
    public List<Discount> getAllDiscounts() {
        List<Discount> discounts = new ArrayList<>();
        String sql = "SELECT * FROM discounts ORDER BY id DESC";

        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                discounts.add(extractDiscount(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi getAllDiscounts: " + e.getMessage());
        }
        
        return discounts;
    }

    /**
     * Lấy các mã giảm giá còn hiệu lực
     */
    public List<Discount> getActiveDiscounts() {
        List<Discount> discounts = new ArrayList<>();
        String sql = "SELECT * FROM discounts WHERE active = TRUE " +
                     "AND start_date <= CURDATE() AND end_date >= CURDATE() " +
                     "ORDER BY percentage DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                discounts.add(extractDiscount(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi getActiveDiscounts: " + e.getMessage());
        }
        
        return discounts;
    }

    /**
     * Tìm mã giảm giá theo code
     */
    public Discount getDiscountByCode(String code) {
        String sql = "SELECT * FROM discounts WHERE code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractDiscount(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi getDiscountByCode: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Kiểm tra mã giảm giá có hợp lệ không
     */
    public boolean validateDiscount(String code) {
        Discount discount = getDiscountByCode(code);
        return discount != null && discount.isValid();
    }

    /**
     * Thêm mã giảm giá mới
     */
    public boolean insertDiscount(Discount discount) {
        String sql = "INSERT INTO discounts (code, percentage, start_date, end_date, active) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, discount.getCode());
            pstmt.setDouble(2, discount.getPercentage());
            pstmt.setDate(3, Date.valueOf(discount.getStartDate()));
            pstmt.setDate(4, Date.valueOf(discount.getEndDate()));
            pstmt.setBoolean(5, discount.isActive());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi insertDiscount: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật mã giảm giá
     */
    public boolean updateDiscount(Discount discount) {
        String sql = "UPDATE discounts SET code = ?, percentage = ?, " +
                     "start_date = ?, end_date = ?, active = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, discount.getCode());
            pstmt.setDouble(2, discount.getPercentage());
            pstmt.setDate(3, Date.valueOf(discount.getStartDate()));
            pstmt.setDate(4, Date.valueOf(discount.getEndDate()));
            pstmt.setBoolean(5, discount.isActive());
            pstmt.setInt(6, discount.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi updateDiscount: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa mã giảm giá
     */
    public boolean deleteDiscount(int id) {
        String sql = "DELETE FROM discounts WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi deleteDiscount: " + e.getMessage());
            return false;
        }
    }

    /**
     * Kích hoạt/vô hiệu hóa mã giảm giá
     */
    public boolean toggleActive(int id) {
        String sql = "UPDATE discounts SET active = NOT active WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi toggleActive: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extract Discount from ResultSet
     */
    private Discount extractDiscount(ResultSet rs) throws SQLException {
        Discount discount = new Discount();
        discount.setId(rs.getInt("id"));
        discount.setCode(rs.getString("code"));
        discount.setPercentage(rs.getDouble("percentage"));
        
        Date startDate = rs.getDate("start_date");
        if (startDate != null) {
            discount.setStartDate(startDate.toLocalDate());
        }
        
        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            discount.setEndDate(endDate.toLocalDate());
        }
        
        discount.setActive(rs.getBoolean("active"));
        
        return discount;
    }
}