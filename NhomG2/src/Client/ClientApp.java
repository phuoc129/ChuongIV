package Client;

import Server.model.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * CLIENT APPLICATION - Console-based UI
 */
public class ClientApp {
    private ClientConnection connection;
    private Scanner scanner;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormat;

    public ClientApp() {
        this.connection = new ClientConnection();
        this.scanner = new Scanner(System.in);
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    }

    public void start() {
        printHeader();
        
        // Kết nối tới server
        if (!connection.connect()) {
            System.out.println("\n✗ Không thể kết nối. Thoát chương trình.");
            return;
        }

        // Đăng nhập
        if (!login()) {
            System.out.println("\n✗ Đăng nhập thất bại. Thoát chương trình.");
            connection.disconnect();
            return;
        }

        // Menu chính
        if (connection.getCurrentUser().getRole().equals("ADMIN")) {
            adminMenu();
        } else {
            userMenu();
        }

        // Đăng xuất và ngắt kết nối
        connection.logout();
        connection.disconnect();
        
        System.out.println("\n👋 Cảm ơn bạn đã sử dụng hệ thống!");
    }

    // ============================================================
    // LOGIN
    // ============================================================
    private boolean login() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           ĐĂNG NHẬP HỆ THỐNG          ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        for (int i = 0; i < 3; i++) {
            System.out.print("\nTên đăng nhập: ");
            String username = scanner.nextLine().trim();
            
            System.out.print("Mật khẩu: ");
            String password = scanner.nextLine().trim();
            
            Response response = connection.login(username, password);
            
            if (response.isSuccess()) {
                User user = connection.getCurrentUser();
                System.out.println("\n✓ " + response.getMessage());
                System.out.println("  Xin chào, " + user.getFullName() + " (" + user.getRole() + ")");
                return true;
            } else {
                System.out.println("\n✗ " + response.getMessage());
                if (i < 2) {
                    System.out.println("  Bạn còn " + (2 - i) + " lần thử");
                }
            }
        }
        
        return false;
    }

    // ============================================================
    // USER MENU
    // ============================================================
    private void userMenu() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║            MENU NGƯỜI DÙNG            ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  1. Xem danh sách sản phẩm            ║");
            System.out.println("║  2. Tìm kiếm sản phẩm                 ║");
            System.out.println("║  3. Đặt hàng                          ║");
            System.out.println("║  4. Xem đơn hàng của tôi              ║");
            System.out.println("║  0. Đăng xuất                         ║");
            System.out.println("╚════════════════════════════════════════╝");
            
            System.out.print("\nChọn chức năng: ");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1": viewProducts(); break;
                case "2": searchProducts(); break;
                case "3": createOrder(); break;
                case "4": viewMyOrders(); break;
                case "0": return;
                default: System.out.println("✗ Lựa chọn không hợp lệ!");
            }
        }
    }

    // ============================================================
    // ADMIN MENU
    // ============================================================
    private void adminMenu() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║           MENU QUẢN TRỊ VIÊN          ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  1. Xem danh sách sản phẩm            ║");
            System.out.println("║  2. Tìm kiếm sản phẩm                 ║");
            System.out.println("║  3. Thêm sản phẩm                     ║");
            System.out.println("║  4. Cập nhật sản phẩm                 ║");
            System.out.println("║  5. Xóa sản phẩm                      ║");
            System.out.println("║  6. Xem tất cả đơn hàng               ║");
            System.out.println("║  7. Xem thống kê                      ║");
            System.out.println("║  0. Đăng xuất                         ║");
            System.out.println("╚════════════════════════════════════════╝");
            
            System.out.print("\nChọn chức năng: ");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1": viewProducts(); break;
                case "2": searchProducts(); break;
                case "3": addProduct(); break;
                case "4": updateProduct(); break;
                case "5": deleteProduct(); break;
                case "6": viewAllOrders(); break;
                case "7": viewStatistics(); break;
                case "0": return;
                default: System.out.println("✗ Lựa chọn không hợp lệ!");
            }
        }
    }

    // ============================================================
    // CHỨC NĂNG XEM SẢN PHẨM
    // ============================================================
    @SuppressWarnings("unchecked")
    private void viewProducts() {
        Response response = connection.getAllProducts();
        
        if (response.isSuccess()) {
            List<Product> products = (List<Product>) response.getData().get("products");
            
            System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                      DANH SÁCH SẢN PHẨM (MySQL)                   ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝");
            
            if (products.isEmpty()) {
                System.out.println("  Chưa có sản phẩm nào.");
            } else {
                System.out.printf("\n%-10s %-25s %-15s %15s %10s\n", 
                                "Mã SP", "Tên sản phẩm", "Danh mục", "Giá", "Số lượng");
                System.out.println("─".repeat(80));
                
                for (Product p : products) {
                    System.out.printf("%-10s %-25s %-15s %15s %10d\n",
                        p.getId(),
                        truncate(p.getName(), 25),
                        p.getCategory(),
                        currencyFormat.format(p.getPrice()),
                        p.getQuantity());
                }
                
                System.out.println("\n  Tổng: " + products.size() + " sản phẩm");
            }
        } else {
            System.out.println("\n✗ " + response.getMessage());
        }
    }

    // ============================================================
    // CHỨC NĂNG TÌM KIẾM SẢN PHẨM
    // ============================================================
    @SuppressWarnings("unchecked")
    private void searchProducts() {
        System.out.print("\nNhập từ khóa tìm kiếm: ");
        String keyword = scanner.nextLine().trim();
        
        Response response = connection.searchProducts(keyword);
        
        if (response.isSuccess()) {
            List<Product> products = (List<Product>) response.getData().get("products");
            
            System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    KẾT QUẢ TÌM KIẾM: " + keyword);
            System.out.println("╚════════════════════════════════════════════════════════════════════╝");
            
            if (products.isEmpty()) {
                System.out.println("  Không tìm thấy sản phẩm nào.");
            } else {
                System.out.printf("\n%-10s %-25s %-15s %15s %10s\n", 
                                "Mã SP", "Tên sản phẩm", "Danh mục", "Giá", "Số lượng");
                System.out.println("─".repeat(80));
                
                for (Product p : products) {
                    System.out.printf("%-10s %-25s %-15s %15s %10d\n",
                        p.getId(),
                        truncate(p.getName(), 25),
                        p.getCategory(),
                        currencyFormat.format(p.getPrice()),
                        p.getQuantity());
                }
                
                System.out.println("\n  Tìm thấy: " + products.size() + " sản phẩm");
            }
        } else {
            System.out.println("\n✗ " + response.getMessage());
        }
    }

    // ============================================================
    // CHỨC NĂNG ĐẶT HÀNG
    // ============================================================
    @SuppressWarnings("unchecked")
    private void createOrder() {
        Response response = connection.getAllProducts();
        if (!response.isSuccess()) {
            System.out.println("\n✗ " + response.getMessage());
            return;
        }
        
        List<Product> products = (List<Product>) response.getData().get("products");
        if (products.isEmpty()) {
            System.out.println("\n✗ Không có sản phẩm để đặt hàng!");
            return;
        }
        
        Order order = new Order();
        double totalAmount = 0;
        
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║              TẠO ĐƠN HÀNG              ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        while (true) {
            System.out.print("\nNhập mã sản phẩm (hoặc 0 để hoàn tất): ");
            String productId = scanner.nextLine().trim();
            
            if (productId.equals("0")) break;
            
            Product product = findProductById(products, productId);
            if (product == null) {
                System.out.println("✗ Không tìm thấy sản phẩm!");
                continue;
            }
            
            System.out.print("Nhập số lượng: ");
            try {
                int quantity = Integer.parseInt(scanner.nextLine().trim());
                
                if (quantity <= 0) {
                    System.out.println("✗ Số lượng phải > 0!");
                    continue;
                }
                
                if (quantity > product.getQuantity()) {
                    System.out.println("✗ Không đủ hàng! Chỉ còn " + product.getQuantity());
                    continue;
                }
                
                OrderItem item = new OrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    quantity
                );
                
                order.addItem(item);
                totalAmount += item.getSubtotal();
                
                System.out.println("✓ Đã thêm: " + product.getName() + 
                                 " x" + quantity + " = " + 
                                 currencyFormat.format(item.getSubtotal()));
                
            } catch (NumberFormatException e) {
                System.out.println("✗ Số lượng không hợp lệ!");
            }
        }
        
        if (order.getItems().isEmpty()) {
            System.out.println("\n✗ Đơn hàng trống!");
            return;
        }
        
        order.setTotalAmount(totalAmount);
        
        System.out.println("\n" + "─".repeat(50));
        System.out.println("Tổng tiền: " + currencyFormat.format(totalAmount));
        System.out.print("\nXác nhận đặt hàng? (y/n): ");
        
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            Response orderResponse = connection.createOrder(order);
            System.out.println("\n" + (orderResponse.isSuccess() ? "✓" : "✗") + 
                             " " + orderResponse.getMessage());
        } else {
            System.out.println("\n✗ Đã hủy đơn hàng");
        }
    }

    // ============================================================
    // XEM ĐƠN HÀNG CỦA TÔI
    // ============================================================
    @SuppressWarnings("unchecked")
    private void viewMyOrders() {
        Response response = connection.getUserOrders();
        
        if (response.isSuccess()) {
            List<Order> orders = (List<Order>) response.getData().get("orders");
            
            System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                  ĐƠN HÀNG CỦA TÔI (JSON File)                     ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝");
            
            if (orders.isEmpty()) {
                System.out.println("  Bạn chưa có đơn hàng nào.");
            } else {
                for (Order order : orders) {
                    printOrder(order);
                }
            }
        } else {
            System.out.println("\n✗ " + response.getMessage());
        }
    }

    // ============================================================
    // ADMIN: XEM TẤT CẢ ĐƠN HÀNG
    // ============================================================
    @SuppressWarnings("unchecked")
    private void viewAllOrders() {
        Response response = connection.getAllOrders();
        
        if (response.isSuccess()) {
            List<Order> orders = (List<Order>) response.getData().get("orders");
            
            System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                  TẤT CẢ ĐƠN HÀNG (JSON File)                      ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝");
            
            if (orders.isEmpty()) {
                System.out.println("  Chưa có đơn hàng nào.");
            } else {
                for (Order order : orders) {
                    printOrder(order);
                }
            }
        } else {
            System.out.println("\n✗ " + response.getMessage());
        }
    }

    // ============================================================
    // ADMIN: THÊM SẢN PHẨM
    // ============================================================
    private void addProduct() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           THÊM SẢN PHẨM MỚI            ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        try {
            System.out.print("\nMã sản phẩm: ");
            String id = scanner.nextLine().trim();
            
            System.out.print("Tên sản phẩm: ");
            String name = scanner.nextLine().trim();
            
            System.out.print("Danh mục: ");
            String category = scanner.nextLine().trim();
            
            System.out.print("Giá: ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            
            System.out.print("Số lượng: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Mô tả: ");
            String description = scanner.nextLine().trim();
            
            Product product = new Product(id, name, category, price, quantity);
            product.setDescription(description);
            
            Response response = connection.addProduct(product);
            System.out.println("\n" + (response.isSuccess() ? "✓" : "✗") + 
                             " " + response.getMessage());
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Dữ liệu không hợp lệ!");
        }
    }

    // ============================================================
    // ADMIN: CẬP NHẬT SẢN PHẨM
    // ============================================================
    private void updateProduct() {
        System.out.print("\nNhập mã sản phẩm cần cập nhật: ");
        String id = scanner.nextLine().trim();
        
        try {
            System.out.print("Tên sản phẩm: ");
            String name = scanner.nextLine().trim();
            
            System.out.print("Danh mục: ");
            String category = scanner.nextLine().trim();
            
            System.out.print("Giá: ");
            double price = Double.parseDouble(scanner.nextLine().trim());
            
            System.out.print("Số lượng: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            
            System.out.print("Mô tả: ");
            String description = scanner.nextLine().trim();
            
            Product product = new Product(id, name, category, price, quantity);
            product.setDescription(description);
            
            Response response = connection.updateProduct(product);
            System.out.println("\n" + (response.isSuccess() ? "✓" : "✗") + 
                             " " + response.getMessage());
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Dữ liệu không hợp lệ!");
        }
    }

    // ============================================================
    // ADMIN: XÓA SẢN PHẨM
    // ============================================================
    private void deleteProduct() {
        System.out.print("\nNhập mã sản phẩm cần xóa: ");
        String id = scanner.nextLine().trim();
        
        System.out.print("Xác nhận xóa? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            Response response = connection.deleteProduct(id);
            System.out.println("\n" + (response.isSuccess() ? "✓" : "✗") + 
                             " " + response.getMessage());
        } else {
            System.out.println("\n✗ Đã hủy thao tác");
        }
    }

    // ============================================================
    // ADMIN: XEM THỐNG KÊ
    // ============================================================
    @SuppressWarnings("unchecked")
    private void viewStatistics() {
        Response response = connection.getStatistics();
        
        if (response.isSuccess()) {
            Map<String, Object> stats = (Map<String, Object>) 
                                       response.getData().get("statistics");
            
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║           THỐNG KÊ HỆ THỐNG            ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println();
            System.out.println("  Tổng sản phẩm (MySQL):  " + stats.get("totalProducts"));
            System.out.println("  Tổng đơn hàng (JSON):   " + stats.get("totalOrders"));
            System.out.println("  Doanh thu (JSON):       " + 
                             currencyFormat.format(stats.get("totalRevenue")));
            System.out.println();
        } else {
            System.out.println("\n✗ " + response.getMessage());
        }
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================
    private void printOrder(Order order) {
        System.out.println("\n─────────────────────────────────────────");
        System.out.println("Đơn hàng #" + order.getId() + " - " + order.getUsername());
        System.out.println("Ngày: " + order.getOrderDate().format(dateFormat));
        System.out.println("Trạng thái: " + order.getStatus());
        
        for (OrderItem item : order.getItems()) {
            System.out.printf("  • %s x%d = %s\n",
                item.getProductName(),
                item.getQuantity(),
                currencyFormat.format(item.getSubtotal()));
        }
        
        System.out.println("Tổng tiền: " + currencyFormat.format(order.getTotalAmount()));
    }

    private Product findProductById(List<Product> products, String id) {
        return products.stream()
                      .filter(p -> p.getId().equals(id))
                      .findFirst()
                      .orElse(null);
    }

    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    private void printHeader() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   HỆ THỐNG QUẢN LÝ BÁN HÀNG          ║");
        System.out.println("║   Client-Server Architecture          ║");
        System.out.println("║   MVC + MySQL + JSON                  ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    // ============================================================
    // MAIN
    // ============================================================
    public static void main(String[] args) {
        ClientApp app = new ClientApp();
        app.start();
    }
}