package Server;

import Server.database.DatabaseConnection;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * SERVER - Xử lý kết nối từ nhiều client
 */
public class Server {
    private static final int PORT = 8888;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private volatile boolean running = true;

    public Server() {
        threadPool = Executors.newCachedThreadPool();
    }

    public void start() {
        try {
            // Khởi tạo database trước khi start server
            System.out.println("\n[KHỞI TẠO DATABASE]");
            DatabaseConnection.initDatabase();
            
            serverSocket = new ServerSocket(PORT);
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║   SERVER ĐÃ KHỞI ĐỘNG                ║");
            System.out.println("║   Port: " + PORT + "                          ║");
            System.out.println("║   Đang chờ client kết nối...          ║");
            System.out.println("╚════════════════════════════════════════╝\n");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("✓ Client mới kết nối: " + 
                        clientSocket.getInetAddress().getHostAddress());
                    
                    // Tạo thread mới xử lý client
                    ClientHandler handler = new ClientHandler(clientSocket);
                    threadPool.execute(handler);
                    
                } catch (SocketException e) {
                    if (!running) break;
                    System.err.println("✗ Lỗi socket: " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            System.err.println("✗ Lỗi khởi động server: " + e.getMessage());
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
            System.out.println("\n✓ Server đã đóng");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        
        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n→ Đang tắt server...");
            server.shutdown();
        }));
        
        server.start();
    }
}