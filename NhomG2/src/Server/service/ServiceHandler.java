package Server.service;

import Server.dao.*;
import Server.model.*;
import java.util.*;

/**
 * SERVICE LAYER - Xử lý Business Logic
 */
public class ServiceHandler {
    private UserDAO userDAO;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    private DiscountDAO discountDAO;
    
    private User currentUser;

    public ServiceHandler() {
        this.userDAO = new UserDAO();
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();
        this.discountDAO = new DiscountDAO();
    }

    // ============================================================
    // AUTHENTICATION
    // ============================================================
    
    public Response handleLogin(Map<String, Object> data) {
        try {
            String username = (String) data.get("username");
            String password = (String) data.get("password");
            
            User user = userDAO.login(username, password);
            
            if (user != null) {
                currentUser = user;
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("user", user);
                
                System.out.println("  ✓ User '" + username + "' đã đăng nhập (Role: " + user.getRole() + ")");
                return new Response(true, "Đăng nhập thành công!", responseData);
            } else {
                System.out.println("  ✗ Đăng nhập thất bại: " + username);
                return new Response(false, "Sai tên đăng nhập hoặc mật khẩu!", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Lỗi server: " + e.getMessage(), null);
        }
    }

    public Response handleLogout() {
        if (currentUser != null) {
            System.out.println("  ✓ User '" + currentUser.getUsername() + "' đã đăng xuất");
            currentUser = null;
        }
        return new Response(true, "Đăng xuất thành công!", null);
    }

    // ============================================================
    // PRODUCTS
    // ============================================================
    
    public Response handleGetAllProducts() {
        try {
            List<Product> products = productDAO.getAllProducts();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("products", products);
            
            System.out.println("  ✓ Trả về " + products.size() + " sản phẩm từ MySQL");
            return new Response(true, "Lấy danh sách sản phẩm thành công!", responseData);
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    public Response handleSearchProducts(Map<String, Object> data) {
        try {
            String keyword = (String) data.get("keyword");
            List<Product> products = productDAO.searchProducts(keyword);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("products", products);
            
            System.out.println("  ✓ Tìm thấy " + products.size() + " sản phẩm với từ khóa: " + keyword);
            return new Response(true, "Tìm kiếm thành công!", responseData);
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    public Response handleAddProduct(Map<String, Object> data) {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            Product product = (Product) data.get("product");
            boolean success = productDAO.insertProduct(product);
            
            if (success) {
                System.out.println("  ✓ Đã thêm sản phẩm: " + product.getName());
                return new Response(true, "Thêm sản phẩm thành công!", null);
            } else {
                return new Response(false, "Thêm sản phẩm thất bại!", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    public Response handleUpdateProduct(Map<String, Object> data) {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            Product product = (Product) data.get("product");
            boolean success = productDAO.updateProduct(product);
            
            if (success) {
                System.out.println("  ✓ Đã cập nhật sản phẩm: " + product.getName());
                return new Response(true, "Cập nhật sản phẩm thành công!", null);
            } else {
                return new Response(false, "Cập nhật sản phẩm thất bại!", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    public Response handleDeleteProduct(Map<String, Object> data) {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            String productId = (String) data.get("productId");
            boolean success = productDAO.deleteProduct(productId);
            
            if (success) {
                System.out.println("  ✓ Đã xóa sản phẩm ID: " + productId);
                return new Response(true, "Xóa sản phẩm thành công!", null);
            } else {
                return new Response(false, "Xóa sản phẩm thất bại!", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    // ============================================================
    // ORDERS
    // ============================================================
    
    public Response handleCreateOrder(Map<String, Object> data) {
        if (currentUser == null) {
            return new Response(false, "Vui lòng đăng nhập để đặt hàng!", null);
        }
        
        try {
            Order order = (Order) data.get("order");
            order.setUserId(currentUser.getId());
            order.setUsername(currentUser.getUsername());
            
            boolean success = orderDAO.createOrder(order);
            
            if (success) {
                System.out.println("  ✓ User '" + currentUser.getUsername() + 
                                 "' đã tạo đơn hàng: " + order.getTotalAmount() + " VND");
                return new Response(true, "Đặt hàng thành công!", null);
            } else {
                return new Response(false, "Đặt hàng thất bại!", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    public Response handleGetUserOrders() {
        if (currentUser == null) {
            return new Response(false, "Vui lòng đăng nhập!", null);
        }
        
        try {
            List<Order> orders = orderDAO.getOrdersByUserId(currentUser.getId());
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("orders", orders);
            
            System.out.println("  ✓ Trả về " + orders.size() + " đơn hàng của user: " + currentUser.getUsername());
            return new Response(true, "Lấy danh sách đơn hàng thành công!", responseData);
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    public Response handleGetAllOrders() {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            List<Order> orders = orderDAO.getAllOrders();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("orders", orders);
            
            System.out.println("  ✓ Trả về tất cả " + orders.size() + " đơn hàng từ JSON");
            return new Response(true, "Lấy danh sách đơn hàng thành công!", responseData);
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    // ============================================================
    // USERS - MỚI THÊM
    // ============================================================
    
    public Response handleGetAllUsers() {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            List<User> users = userDAO.getAllUsers();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("users", users);
            
            System.out.println("  ✓ Trả về " + users.size() + " người dùng");
            return new Response(true, "Lấy danh sách người dùng thành công!", responseData);
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    public Response handleAddUser(Map<String, Object> data) {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            User user = (User) data.get("user");
            String password = (String) data.get("password");
            
            // Kiểm tra username đã tồn tại
            if (userDAO.usernameExists(user.getUsername())) {
                return new Response(false, "Tên đăng nhập đã tồn tại!", null);
            }
            
            boolean success = userDAO.insertUser(user, password);
            
            if (success) {
                System.out.println("  ✓ Đã thêm user: " + user.getUsername());
                return new Response(true, "Thêm người dùng thành công!", null);
            } else {
                return new Response(false, "Thêm người dùng thất bại!", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    public Response handleUpdateUser(Map<String, Object> data) {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            User user = (User) data.get("user");
            boolean success = userDAO.updateUser(user);
            
            if (success) {
                System.out.println("  ✓ Đã cập nhật user: " + user.getUsername());
                return new Response(true, "Cập nhật người dùng thành công!", null);
            } else {
                return new Response(false, "Cập nhật người dùng thất bại!", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    public Response handleDeleteUser(Map<String, Object> data) {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            int userId = (int) data.get("userId");
            
            // Không cho phép xóa chính mình
            if (currentUser.getId() == userId) {
                return new Response(false, "Không thể xóa chính mình!", null);
            }
            
            boolean success = userDAO.deleteUser(userId);
            
            if (success) {
                System.out.println("  ✓ Đã xóa user ID: " + userId);
                return new Response(true, "Xóa người dùng thành công!", null);
            } else {
                return new Response(false, "Xóa người dùng thất bại!", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    public Response handleResetPassword(Map<String, Object> data) {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            int userId = (int) data.get("userId");
            String newPassword = (String) data.get("newPassword");
            
            boolean success = userDAO.resetPassword(userId, newPassword);
            
            if (success) {
                System.out.println("  ✓ Đã reset mật khẩu cho user ID: " + userId);
                return new Response(true, "Reset mật khẩu thành công!", null);
            } else {
                return new Response(false, "Reset mật khẩu thất bại!", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    // ============================================================
    // STATISTICS
    // ============================================================
    
    public Response handleGetStatistics() {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalProducts", productDAO.getProductCount());
            stats.put("totalOrders", orderDAO.getOrderCount());
            stats.put("totalRevenue", orderDAO.getTotalRevenue());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("statistics", stats);
            
            System.out.println("  ✓ Đã tạo báo cáo thống kê");
            return new Response(true, "Lấy thống kê thành công!", responseData);
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    // ============================================================
    // HELPER
    // ============================================================
    
    private boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    public User getCurrentUser() {
        return currentUser;
    }
    public Response handleGetAllDiscounts() {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            List<Discount> discounts = discountDAO.getAllDiscounts();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("discounts", discounts);
            
            System.out.println("  ✓ Trả về " + discounts.size() + " mã giảm giá");
            return new Response(true, "Lấy danh sách mã giảm giá thành công!", responseData);
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    /**
     * Lấy mã giảm giá còn hiệu lực (User có thể xem)
     */
    public Response handleGetActiveDiscounts() {
        try {
            List<Discount> discounts = discountDAO.getActiveDiscounts();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("discounts", discounts);
            
            System.out.println("  ✓ Trả về " + discounts.size() + " mã giảm giá còn hiệu lực");
            return new Response(true, "Lấy danh sách mã giảm giá thành công!", responseData);
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    /**
     * Kiểm tra mã giảm giá
     */
    public Response handleValidateDiscount(Map<String, Object> data) {
        try {
            String code = (String) data.get("code");
            Discount discount = discountDAO.getDiscountByCode(code);
            
            if (discount == null) {
                return new Response(false, "Mã giảm giá không tồn tại!", null);
            }
            
            if (!discount.isValid()) {
                return new Response(false, "Mã giảm giá đã hết hạn hoặc chưa có hiệu lực!", null);
            }
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("discount", discount);
            
            System.out.println("  ✓ Mã giảm giá '" + code + "' hợp lệ: " + discount.getPercentage() + "%");
            return new Response(true, "Mã giảm giá hợp lệ!", responseData);
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    /**
     * Thêm mã giảm giá (Admin)
     */
    public Response handleAddDiscount(Map<String, Object> data) {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            Discount discount = (Discount) data.get("discount");
            
            // Kiểm tra code đã tồn tại
            if (discountDAO.getDiscountByCode(discount.getCode()) != null) {
                return new Response(false, "Mã giảm giá đã tồn tại!", null);
            }
            
            boolean success = discountDAO.insertDiscount(discount);
            
            if (success) {
                System.out.println("  ✓ Đã thêm mã giảm giá: " + discount.getCode());
                return new Response(true, "Thêm mã giảm giá thành công!", null);
            } else {
                return new Response(false, "Thêm mã giảm giá thất bại!", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    /**
     * Cập nhật mã giảm giá (Admin)
     */
    public Response handleUpdateDiscount(Map<String, Object> data) {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            Discount discount = (Discount) data.get("discount");
            boolean success = discountDAO.updateDiscount(discount);
            
            if (success) {
                System.out.println("  ✓ Đã cập nhật mã giảm giá: " + discount.getCode());
                return new Response(true, "Cập nhật mã giảm giá thành công!", null);
            } else {
                return new Response(false, "Cập nhật mã giảm giá thất bại!", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    /**
     * Xóa mã giảm giá (Admin)
     */
    public Response handleDeleteDiscount(Map<String, Object> data) {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            int discountId = (int) data.get("discountId");
            boolean success = discountDAO.deleteDiscount(discountId);
            
            if (success) {
                System.out.println("  ✓ Đã xóa mã giảm giá ID: " + discountId);
                return new Response(true, "Xóa mã giảm giá thành công!", null);
            } else {
                return new Response(false, "Xóa mã giảm giá thất bại!", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }
}