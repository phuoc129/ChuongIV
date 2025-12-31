package Client;

import Server.model.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * CLIENT CONNECTION - Quản lý kết nối và giao tiếp với Server
 */
public class ClientConnection {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8888;
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private User currentUser;

    public ClientConnection() {
    }

    /**
     * Kết nối tới server
     */
    public boolean connect() {
        try {
            System.out.println("\n→ Đang kết nối tới server " + SERVER_HOST + ":" + SERVER_PORT + "...");
            
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            
            in = new ObjectInputStream(socket.getInputStream());
            
            System.out.println("✓ Kết nối thành công!");
            return true;
            
        } catch (IOException e) {
            System.err.println("✗ Lỗi kết nối: " + e.getMessage());
            return false;
        }
    }

    /**
     * Ngắt kết nối
     */
    public void disconnect() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("✓ Đã ngắt kết nối");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gửi request và nhận response
     */
    private Response sendRequest(Request request) {
        try {
            out.writeObject(request);
            out.flush();
            
            Response response = (Response) in.readObject();
            return response;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("✗ Lỗi giao tiếp với server: " + e.getMessage());
            return new Response(false, "Lỗi kết nối server", null);
        }
    }

    // ============================================================
    // AUTHENTICATION
    // ============================================================
    
    public Response login(String username, String password) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);
        
        Request request = new Request("LOGIN", data);
        Response response = sendRequest(request);
        
        if (response.isSuccess()) {
            currentUser = (User) response.getData().get("user");
        }
        
        return response;
    }

    public Response logout() {
        Request request = new Request("LOGOUT", null);
        Response response = sendRequest(request);
        currentUser = null;
        return response;
    }

    // ============================================================
    // PRODUCTS
    // ============================================================
    
    public Response getAllProducts() {
        Request request = new Request("GET_ALL_PRODUCTS", null);
        return sendRequest(request);
    }

    public Response searchProducts(String keyword) {
        Map<String, Object> data = new HashMap<>();
        data.put("keyword", keyword);
        
        Request request = new Request("SEARCH_PRODUCTS", data);
        return sendRequest(request);
    }

    public Response addProduct(Product product) {
        Map<String, Object> data = new HashMap<>();
        data.put("product", product);
        
        Request request = new Request("ADD_PRODUCT", data);
        return sendRequest(request);
    }

    public Response updateProduct(Product product) {
        Map<String, Object> data = new HashMap<>();
        data.put("product", product);
        
        Request request = new Request("UPDATE_PRODUCT", data);
        return sendRequest(request);
    }

    public Response deleteProduct(String productId) {
        Map<String, Object> data = new HashMap<>();
        data.put("productId", productId);
        
        Request request = new Request("DELETE_PRODUCT", data);
        return sendRequest(request);
    }

    // ============================================================
    // ORDERS
    // ============================================================
    
    public Response createOrder(Order order) {
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        
        Request request = new Request("CREATE_ORDER", data);
        return sendRequest(request);
    }

    public Response getUserOrders() {
        Request request = new Request("GET_USER_ORDERS", null);
        return sendRequest(request);
    }

    public Response getAllOrders() {
        Request request = new Request("GET_ALL_ORDERS", null);
        return sendRequest(request);
    }

    // ============================================================
    // USERS - MỚI THÊM
    // ============================================================
    
    public Response getAllUsers() {
        Request request = new Request("GET_ALL_USERS", null);
        return sendRequest(request);
    }

    public Response addUser(User user, String password) {
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("password", password);
        
        Request request = new Request("ADD_USER", data);
        return sendRequest(request);
    }

    public Response updateUser(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        
        Request request = new Request("UPDATE_USER", data);
        return sendRequest(request);
    }

    public Response deleteUser(int userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        
        Request request = new Request("DELETE_USER", data);
        return sendRequest(request);
    }

    public Response resetPassword(int userId, String newPassword) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("newPassword", newPassword);
        
        Request request = new Request("RESET_PASSWORD", data);
        return sendRequest(request);
    }

    // ============================================================
    // STATISTICS
    // ============================================================
    
    public Response getStatistics() {
        Request request = new Request("GET_STATISTICS", null);
        return sendRequest(request);
    }

    // ============================================================
    // GETTERS
    // ============================================================
    
    public User getCurrentUser() {
        return currentUser;
    }
    public Response getAllDiscounts() {
        Request request = new Request("GET_ALL_DISCOUNTS", null);
        return sendRequest(request);
    }

    /**
     * Lấy mã giảm giá còn hiệu lực
     */
    public Response getActiveDiscounts() {
        Request request = new Request("GET_ACTIVE_DISCOUNTS", null);
        return sendRequest(request);
    }

    /**
     * Kiểm tra mã giảm giá
     */
    public Response validateDiscount(String code) {
        Map<String, Object> data = new HashMap<>();
        data.put("code", code);
        
        Request request = new Request("VALIDATE_DISCOUNT", data);
        return sendRequest(request);
    }

    /**
     * Thêm mã giảm giá (Admin)
     */
    public Response addDiscount(Discount discount) {
        Map<String, Object> data = new HashMap<>();
        data.put("discount", discount);
        
        Request request = new Request("ADD_DISCOUNT", data);
        return sendRequest(request);
    }

    /**
     * Cập nhật mã giảm giá (Admin)
     */
    public Response updateDiscount(Discount discount) {
        Map<String, Object> data = new HashMap<>();
        data.put("discount", discount);
        
        Request request = new Request("UPDATE_DISCOUNT", data);
        return sendRequest(request);
    }

    /**
     * Xóa mã giảm giá (Admin)
     */
    public Response deleteDiscount(int discountId) {
        Map<String, Object> data = new HashMap<>();
        data.put("discountId", discountId);
        
        Request request = new Request("DELETE_DISCOUNT", data);
        return sendRequest(request);
    }
}