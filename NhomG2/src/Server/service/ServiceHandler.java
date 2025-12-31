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
    
    // Lưu session đang đăng nhập
    private User currentUser;

    public ServiceHandler() {
        this.userDAO = new UserDAO();
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();
    }

    /**
     * Xử lý LOGIN
     */
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

    /**
     * Xử lý LOGOUT
     */
    public Response handleLogout() {
        if (currentUser != null) {
            System.out.println("  ✓ User '" + currentUser.getUsername() + "' đã đăng xuất");
            currentUser = null;
        }
        return new Response(true, "Đăng xuất thành công!", null);
    }

    /**
     * Lấy tất cả sản phẩm (MySQL - Có cấu trúc)
     */
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

    /**
     * Tìm kiếm sản phẩm (MySQL)
     */
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

    /**
     * Thêm sản phẩm (Admin only - MySQL)
     */
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

    /**
     * Cập nhật sản phẩm (Admin only - MySQL)
     */
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

    /**
     * Xóa sản phẩm (Admin only - MySQL)
     */
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

    /**
     * Tạo đơn hàng (JSON - Không có cấu trúc)
     */
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

    /**
     * Lấy đơn hàng của user hiện tại (JSON)
     */
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

    /**
     * Lấy tất cả đơn hàng (Admin only - JSON)
     */
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

    /**
     * Lấy thống kê (Admin only)
     */
    public Response handleGetStatistics() {
        if (!isAdmin()) {
            return new Response(false, "Bạn không có quyền thực hiện thao tác này!", null);
        }
        
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalProducts", productDAO.getProductCount());    // MySQL
            stats.put("totalOrders", orderDAO.getOrderCount());          // JSON
            stats.put("totalRevenue", orderDAO.getTotalRevenue());       // JSON
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("statistics", stats);
            
            System.out.println("  ✓ Đã tạo báo cáo thống kê");
            return new Response(true, "Lấy thống kê thành công!", responseData);
            
        } catch (Exception e) {
            return new Response(false, "Lỗi: " + e.getMessage(), null);
        }
    }

    /**
     * Kiểm tra quyền Admin
     */
    private boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    public User getCurrentUser() {
        return currentUser;
    }
}