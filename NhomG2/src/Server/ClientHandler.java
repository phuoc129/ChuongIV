package Server;

import Server.model.*;
import Server.service.ServiceHandler;
import java.io.*;
import java.net.Socket;

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
            // Khởi tạo streams
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(clientSocket.getInputStream());
            
            String clientInfo = clientSocket.getInetAddress().getHostAddress() + 
                              ":" + clientSocket.getPort();
            
            System.out.println("→ Bắt đầu phục vụ client: " + clientInfo);

            // Vòng lặp xử lý request
            while (running) {
                try {
                    Request request = (Request) in.readObject();
                    
                    System.out.println("\n[" + clientInfo + "] Request: " + request.getAction());
                    
                    Response response = processRequest(request);
                    
                    out.writeObject(response);
                    out.flush();
                    
                    System.out.println("[" + clientInfo + "] Response: " + 
                                     (response.isSuccess() ? "✓" : "✗") + " " + response.getMessage());
                    
                } catch (EOFException | SocketException e) {
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
                
                // ========== PRODUCTS (MySQL - Có cấu trúc) ==========
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
                
                // ========== ORDERS (JSON - Không có cấu trúc) ==========
                case "CREATE_ORDER":
                    return service.handleCreateOrder(request.getData());
                    
                case "GET_USER_ORDERS":
                    return service.handleGetUserOrders();
                    
                case "GET_ALL_ORDERS":
                    return service.handleGetAllOrders();
                
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