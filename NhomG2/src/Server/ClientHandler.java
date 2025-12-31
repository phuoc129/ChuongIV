package Server;

import Server.model.*;
import Server.service.ServiceHandler;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * CLIENT HANDLER - Xử lý request từ mỗi client (chạy trên thread riêng)
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ServiceHandler service;
    private boolean running = true;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.service = new ServiceHandler();
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
            
            String clientInfo = clientSocket.getInetAddress().getHostAddress() + 
                              ":" + clientSocket.getPort();
            
            System.out.println("→ Bắt đầu phục vụ client: " + clientInfo);

            while (running) {
                try {
                    Request request = (Request) in.readObject();
                    
                    System.out.println("\n[" + clientInfo + "] Request: " + request.getAction());
                    
                    Response response = processRequest(request);
                    
                    out.writeObject(response);
                    out.flush();
                    
                    System.out.println("[" + clientInfo + "] Response: " + 
                                     (response.isSuccess() ? "✓" : "✗") + " " + response.getMessage());
                    
                } catch (EOFException e) {
                    System.out.println("✗ Client " + clientInfo + " đã ngắt kết nối");
                    break;
                } catch (SocketException e) {
                    System.out.println("✗ Client " + clientInfo + " đã ngắt kết nối");
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("✗ Lỗi đọc request: " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            System.err.println("✗ Lỗi I/O: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Xử lý request theo action (CONTROLLER)
     */
    private Response processRequest(Request request) {
        String action = request.getAction();
        
        try {
            switch (action) {
                // ========== AUTHENTICATION ==========
                case "LOGIN":
                    return service.handleLogin(request.getData());
                    
                case "LOGOUT":
                    return service.handleLogout();
                
                // ========== PRODUCTS ==========
                case "GET_ALL_PRODUCTS":
                    return service.handleGetAllProducts();
                    
                case "SEARCH_PRODUCTS":
                    return service.handleSearchProducts(request.getData());
                    
                case "ADD_PRODUCT":
                    return service.handleAddProduct(request.getData());
                    
                case "UPDATE_PRODUCT":
                    return service.handleUpdateProduct(request.getData());
                    
                case "DELETE_PRODUCT":
                    return service.handleDeleteProduct(request.getData());
                
                // ========== ORDERS ==========
                case "CREATE_ORDER":
                    return service.handleCreateOrder(request.getData());
                    
                case "GET_USER_ORDERS":
                    return service.handleGetUserOrders();
                    
                case "GET_ALL_ORDERS":
                    return service.handleGetAllOrders();
                
                // ========== USERS - MỚI THÊM ==========
                case "GET_ALL_USERS":
                    return service.handleGetAllUsers();
                    
                case "ADD_USER":
                    return service.handleAddUser(request.getData());
                    
                case "UPDATE_USER":
                    return service.handleUpdateUser(request.getData());
                    
                case "DELETE_USER":
                    return service.handleDeleteUser(request.getData());
                    
                case "RESET_PASSWORD":
                    return service.handleResetPassword(request.getData());
                case "GET_ALL_DISCOUNTS":
                    return service.handleGetAllDiscounts();
                    
                case "GET_ACTIVE_DISCOUNTS":
                    return service.handleGetActiveDiscounts();
                    
                case "VALIDATE_DISCOUNT":
                    return service.handleValidateDiscount(request.getData());
                    
                case "ADD_DISCOUNT":
                    return service.handleAddDiscount(request.getData());
                    
                case "UPDATE_DISCOUNT":
                    return service.handleUpdateDiscount(request.getData());
                    
                case "DELETE_DISCOUNT":
                    return service.handleDeleteDiscount(request.getData());
                
                // ========== STATISTICS ==========
                case "GET_STATISTICS":
                    return service.handleGetStatistics();
                
                default:
                    return new Response(false, "Action không hợp lệ: " + action, null);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Lỗi server: " + e.getMessage(), null);
        }
    }

    /**
     * Dọn dẹp tài nguyên
     */
    private void cleanup() {
        running = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}