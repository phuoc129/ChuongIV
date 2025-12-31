package Client.gui;

import Client.ClientConnection;
import Server.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * GIAO DIỆN ADMIN - CÓ QUẢN LÝ USER
 */
public class AdminMainFrame extends JFrame {
    private ClientConnection connection;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormat;
    
    private JTabbedPane tabbedPane;
    private JTable tableProducts;
    private DefaultTableModel tableModelProducts;
    private JTextField txtSearch;
    private List<Product> currentProducts;
    
    // USER MANAGEMENT
    private JTable tableUsers;
    private DefaultTableModel tableModelUsers;
    private List<User> currentUsers;
    
    private JTable tableDiscounts;
    private DefaultTableModel tableModelDiscounts;
    private List<Discount> currentDiscounts;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color INFO_COLOR = new Color(52, 152, 219);

    public AdminMainFrame(ClientConnection connection) {
        this.connection = connection;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.currentProducts = new ArrayList<>();
        this.currentUsers = new ArrayList<>();
        this.currentDiscounts = new ArrayList<>();
        
        initComponents();
    }

    private void initComponents() {
        setTitle("⚙️ Quản trị hệ thống - " + connection.getCurrentUser().getFullName());
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(236, 240, 241));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        
        tabbedPane.addTab(" Quản lý sản phẩm", createProductPanel());
        tabbedPane.addTab(" Quản lý người dùng", createUserPanel());
        tabbedPane.addTab(" Quản lý đơn hàng", createOrderPanel());
        tabbedPane.addTab(" Thống kê", createStatisticsPanel());
        tabbedPane.addTab(" Quản lý giảm giá", createDiscountPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    // ============================================================
    // TAB QUẢN LÝ SẢN PHẨM
    // ============================================================
    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top - Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel lblSearch = new JLabel("🔍 Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        txtSearch = new JTextField(30);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        JButton btnSearch = createButton("Tìm", INFO_COLOR, 100, 35);
        btnSearch.addActionListener(e -> searchProducts());
        
        JButton btnViewAll = createButton("Xem tất cả", new Color(149, 165, 166), 120, 35);
        btnViewAll.addActionListener(e -> loadProducts());
        
        topPanel.add(lblSearch);
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnViewAll);
        
        panel.add(topPanel, BorderLayout.NORTH);

        // Center - Table
        String[] columns = {"Mã SP", "Tên", "Danh mục", "Giá", "Số lượng", "Mô tả"};
        tableModelProducts = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableProducts = new JTable(tableModelProducts);
        tableProducts.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableProducts.setRowHeight(30);
        tableProducts.setSelectionBackground(new Color(52, 152, 219, 50));
        tableProducts.setGridColor(new Color(189, 195, 199));
        
        tableProducts.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableProducts.getTableHeader().setBackground(PRIMARY_COLOR);
        tableProducts.getTableHeader().setForeground(Color.BLACK);
        tableProducts.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableProducts.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tableProducts.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(tableProducts);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Right - Actions
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 12, 15, 12)
        ));
        rightPanel.setPreferredSize(new Dimension(190, 0));
        
        JButton btnAdd = createActionButton("➕ Thêm SP", SUCCESS_COLOR);
        btnAdd.addActionListener(e -> addProduct());
        
        JButton btnUpdate = createActionButton("✏️ Cập nhật", INFO_COLOR);
        btnUpdate.addActionListener(e -> updateProduct());
        
        JButton btnDelete = createActionButton("🗑️ Xóa SP", DANGER_COLOR);
        btnDelete.addActionListener(e -> deleteProduct());
        
        JButton btnRefresh = createActionButton("🔄 Làm mới", new Color(149, 165, 166));
        btnRefresh.addActionListener(e -> loadProducts());
        
        JButton btnLogout = createActionButton("🚪 Đăng xuất", new Color(231, 76, 60));
        btnLogout.addActionListener(e -> logout());
        
        rightPanel.add(btnAdd);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightPanel.add(btnUpdate);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightPanel.add(btnDelete);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightPanel.add(btnRefresh);
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(btnLogout);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        loadProducts();
        
        return panel;
    }

    @SuppressWarnings("unchecked")
    private void loadProducts() {
        Response response = connection.getAllProducts();
        
        if (response.isSuccess()) {
            currentProducts = (List<Product>) response.getData().get("products");
            updateProductTable(currentProducts);
        } else {
            showError(response.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void searchProducts() {
        String keyword = txtSearch.getText().trim();
        
        if (keyword.isEmpty()) {
            loadProducts();
            return;
        }
        
        Response response = connection.searchProducts(keyword);
        
        if (response.isSuccess()) {
            currentProducts = (List<Product>) response.getData().get("products");
            updateProductTable(currentProducts);
        }
    }

    private void updateProductTable(List<Product> products) {
        tableModelProducts.setRowCount(0);
        
        for (Product p : products) {
            tableModelProducts.addRow(new Object[] {
                p.getId(),
                p.getName(),
                p.getCategory(),
                currencyFormat.format(p.getPrice()),
                p.getQuantity(),
                p.getDescription() != null ? p.getDescription() : ""
            });
        }
    }

    private void addProduct() {
        ProductDialog dialog = new ProductDialog(this, connection, null);
        dialog.setVisible(true);
        loadProducts();
    }

    private void updateProduct() {
        int selectedRow = tableProducts.getSelectedRow();
        
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn sản phẩm!");
            return;
        }
        
        Product product = currentProducts.get(selectedRow);
        ProductDialog dialog = new ProductDialog(this, connection, product);
        dialog.setVisible(true);
        loadProducts();
    }

    private void deleteProduct() {
        int selectedRow = tableProducts.getSelectedRow();
        
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn sản phẩm!");
            return;
        }
        
        Product product = currentProducts.get(selectedRow);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa sản phẩm:\n" + product.getName() + "?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Response response = connection.deleteProduct(product.getId());
            
            if (response.isSuccess()) {
                showSuccess("Xóa thành công!");
                loadProducts();
            } else {
                showError(response.getMessage());
            }
        }
    }

    // ============================================================
    // TAB QUẢN LÝ NGƯỜI DÙNG
    // ============================================================
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top - Title
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel lblTitle = new JLabel("👥 QUẢN LÝ NGƯỜI DÙNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(PRIMARY_COLOR);
        
        JButton btnRefreshUsers = createButton("🔄 Làm mới", new Color(149, 165, 166), 120, 35);
        btnRefreshUsers.addActionListener(e -> loadUsers());
        
        topPanel.add(lblTitle);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(btnRefreshUsers);
        
        panel.add(topPanel, BorderLayout.NORTH);

        // Center - Table
        String[] columns = {"ID", "Tên đăng nhập", "Họ tên", "Vai trò", "Trạng thái", "Ngày tạo"};
        tableModelUsers = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableUsers = new JTable(tableModelUsers);
        tableUsers.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableUsers.setRowHeight(30);
        tableUsers.setSelectionBackground(new Color(52, 152, 219, 50));
        tableUsers.setGridColor(new Color(189, 195, 199));
        
        tableUsers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableUsers.getTableHeader().setBackground(PRIMARY_COLOR);
        tableUsers.getTableHeader().setForeground(Color.BLACK);
        tableUsers.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableUsers.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableUsers.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tableUsers.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(tableUsers);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Right - Actions
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 12, 15, 12)
        ));
        rightPanel.setPreferredSize(new Dimension(190, 0));
        
        JButton btnAddUser = createActionButton("➕ Thêm User", SUCCESS_COLOR);
        btnAddUser.addActionListener(e -> addUser());
        
        JButton btnUpdateUser = createActionButton("✏️ Sửa User", INFO_COLOR);
        btnUpdateUser.addActionListener(e -> updateUser());
        
        JButton btnDeleteUser = createActionButton("🗑️ Xóa User", DANGER_COLOR);
        btnDeleteUser.addActionListener(e -> deleteUser());
        
        JButton btnResetPassword = createActionButton("🔑 Reset MK", WARNING_COLOR);
        btnResetPassword.addActionListener(e -> resetPassword());
        
        rightPanel.add(btnAddUser);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightPanel.add(btnUpdateUser);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightPanel.add(btnDeleteUser);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightPanel.add(btnResetPassword);
        rightPanel.add(Box.createVerticalGlue());
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        loadUsers();
        
        return panel;
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() {
        Response response = connection.getAllUsers();
        
        if (response.isSuccess()) {
            currentUsers = (List<User>) response.getData().get("users");
            updateUserTable(currentUsers);
        } else {
            showError(response.getMessage());
        }
    }

    private void updateUserTable(List<User> users) {
        tableModelUsers.setRowCount(0);
        
        for (User u : users) {
            tableModelUsers.addRow(new Object[] {
                u.getId(),
                u.getUsername(),
                u.getFullName(),
                u.getRole(),
                u.isActive() ? "✅ Active" : "❌ Inactive",
                u.getCreatedAt() != null ? u.getCreatedAt().format(dateFormat) : ""
            });
        }
    }

    private void addUser() {
        UserDialog dialog = new UserDialog(this, connection, null);
        dialog.setVisible(true);
        loadUsers();
    }

    private void updateUser() {
        int selectedRow = tableUsers.getSelectedRow();
        
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn người dùng!");
            return;
        }
        
        User user = currentUsers.get(selectedRow);
        UserDialog dialog = new UserDialog(this, connection, user);
        dialog.setVisible(true);
        loadUsers();
    }

    private void deleteUser() {
        int selectedRow = tableUsers.getSelectedRow();
        
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn người dùng!");
            return;
        }
        
        User user = currentUsers.get(selectedRow);
        
        if (user.getId() == connection.getCurrentUser().getId()) {
            showError("Không thể xóa chính mình!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa người dùng:\n" + user.getFullName() + "?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Response response = connection.deleteUser(user.getId());
            
            if (response.isSuccess()) {
                showSuccess("Xóa người dùng thành công!");
                loadUsers();
            } else {
                showError(response.getMessage());
            }
        }
    }

    private void resetPassword() {
        int selectedRow = tableUsers.getSelectedRow();
        
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn người dùng!");
            return;
        }
        
        User user = currentUsers.get(selectedRow);
        
        String newPassword = JOptionPane.showInputDialog(this, 
            "Nhập mật khẩu mới cho user: " + user.getUsername(), 
            "Reset mật khẩu", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            Response response = connection.resetPassword(user.getId(), newPassword.trim());
            
            if (response.isSuccess()) {
                showSuccess("Reset mật khẩu thành công!");
            } else {
                showError(response.getMessage());
            }
        }
    }

    // ============================================================
    // TAB QUẢN LÝ ĐƠN HÀNG
    // ============================================================
    @SuppressWarnings("unchecked")
    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JButton btnLoad = createButton("📥 Tải đơn hàng", PRIMARY_COLOR, 150, 40);
        btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnLoad.addActionListener(e -> {
            Response response = connection.getAllOrders();
            
            if (response.isSuccess()) {
                List<Order> orders = (List<Order>) response.getData().get("orders");
                
                StringBuilder sb = new StringBuilder();
                sb.append("╔══════════════════════════════════════════════════════════════╗\n");
                sb.append("║              DANH SÁCH TẤT CẢ ĐƠN HÀNG                      ║\n");
                sb.append("╚══════════════════════════════════════════════════════════════╝\n\n");
                
                if (orders.isEmpty()) {
                    sb.append("          Chưa có đơn hàng nào.\n");
                } else {
                    for (Order order : orders) {
                        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                        sb.append(String.format("  📦 Đơn hàng #%-5d | 👤 %-15s\n", 
                            order.getId(), order.getUsername()));
                        sb.append(String.format("  📅 %s | 📊 %s\n",
                            order.getOrderDate().format(dateFormat), order.getStatus()));
                        sb.append("  ─────────────────────────────────────────────────────\n");
                        
                        for (OrderItem item : order.getItems()) {
                            sb.append(String.format("    • %-30s x%-3d = %12s\n",
                                item.getProductName(),
                                item.getQuantity(),
                                currencyFormat.format(item.getSubtotal())));
                        }
                        
                        sb.append("  ─────────────────────────────────────────────────────\n");
                        sb.append(String.format("  💰 TỔNG TIỀN: %36s\n", 
                            currencyFormat.format(order.getTotalAmount())));
                        sb.append("\n");
                    }
                    
                    sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                    sb.append(String.format("  📊 Tổng số đơn hàng: %d\n", orders.size()));
                }
                
                textArea.setText(sb.toString());
                textArea.setCaretPosition(0);
            } else {
                showError(response.getMessage());
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        
        panel.add(btnLoad, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    // ============================================================
    // TAB THỐNG KÊ
    // ============================================================
    @SuppressWarnings("unchecked")
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JButton btnLoad = createButton("📊 Tải thống kê", PRIMARY_COLOR, 150, 40);
        btnLoad.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(Color.WHITE);
        
        btnLoad.addActionListener(e -> {
            Response response = connection.getStatistics();
            
            if (response.isSuccess()) {
                Map<String, Object> stats = (Map<String, Object>) response.getData().get("statistics");
                
                statsPanel.removeAll();
                statsPanel.add(Box.createRigidArea(new Dimension(0, 30)));
                
                JLabel lblTitle = new JLabel("📊 THỐNG KÊ HỆ THỐNG");
                lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
                lblTitle.setForeground(PRIMARY_COLOR);
                lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
                statsPanel.add(lblTitle);
                
                statsPanel.add(Box.createRigidArea(new Dimension(0, 50)));
                
                addStatCard(statsPanel, " Tổng số sản phẩm ", 
                    stats.get("totalProducts").toString(), INFO_COLOR);
                addStatCard(statsPanel, " Tổng số đơn hàng ", 
                    stats.get("totalOrders").toString(), WARNING_COLOR);
                addStatCard(statsPanel, " Tổng doanh thu", 
                    currencyFormat.format(stats.get("totalRevenue")), SUCCESS_COLOR);
                
                statsPanel.revalidate();
                statsPanel.repaint();
            } else {
                showError(response.getMessage());
            }
        });
        
        panel.add(btnLoad, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void addStatCard(JPanel panel, String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        card.setMaximumSize(new Dimension(600, 100));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(color);
        
        card.add(lblLabel, BorderLayout.WEST);
        card.add(lblValue, BorderLayout.EAST);
        
        panel.add(card);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    // ============================================================
    // HELPER
    // ============================================================
    private JButton createButton(String text, Color color, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, height));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(170, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn đăng xuất?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            connection.logout();
            connection.disconnect();
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }
    
    private JPanel createDiscountPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top - Title
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel lblTitle = new JLabel("🎁 QUẢN LÝ MÃ GIẢM GIÁ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(PRIMARY_COLOR);
        
        JButton btnRefresh = createButton("🔄 Làm mới", new Color(149, 165, 166), 120, 35);
        btnRefresh.addActionListener(e -> loadDiscounts());
        
        topPanel.add(lblTitle);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(btnRefresh);
        
        panel.add(topPanel, BorderLayout.NORTH);

        // Center - Table
        String[] columns = {"ID", "Mã giảm giá", "Giảm (%)", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái", "Còn hiệu lực"};
        tableModelDiscounts = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableDiscounts = new JTable(tableModelDiscounts);
        tableDiscounts.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableDiscounts.setRowHeight(30);
        tableDiscounts.setSelectionBackground(new Color(52, 152, 219, 50));
        tableDiscounts.setGridColor(new Color(189, 195, 199));
        
        tableDiscounts.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableDiscounts.getTableHeader().setBackground(PRIMARY_COLOR);
        tableDiscounts.getTableHeader().setForeground(Color.BLACK);
        tableDiscounts.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableDiscounts.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableDiscounts.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tableDiscounts.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        tableDiscounts.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(tableDiscounts);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Right - Actions
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 12, 15, 12)
        ));
        rightPanel.setPreferredSize(new Dimension(190, 0));
        
        JButton btnAdd = createActionButton("➕ Thêm mã", SUCCESS_COLOR);
        btnAdd.addActionListener(e -> addDiscount());
        
        JButton btnUpdate = createActionButton("✏️ Cập nhật", INFO_COLOR);
        btnUpdate.addActionListener(e -> updateDiscount());
        
        JButton btnDelete = createActionButton("🗑️ Xóa mã", DANGER_COLOR);
        btnDelete.addActionListener(e -> deleteDiscount());
        
        rightPanel.add(btnAdd);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightPanel.add(btnUpdate);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightPanel.add(btnDelete);
        rightPanel.add(Box.createVerticalGlue());
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        loadDiscounts();
        
        return panel;
    }

    @SuppressWarnings("unchecked")
    private void loadDiscounts() {
        Response response = connection.getAllDiscounts();
        
        if (response.isSuccess()) {
            currentDiscounts = (List<Discount>) response.getData().get("discounts");
            updateDiscountTable(currentDiscounts);
        } else {
            showError(response.getMessage());
        }
    }

    private void updateDiscountTable(List<Discount> discounts) {
        tableModelDiscounts.setRowCount(0);
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Discount d : discounts) {
            String startDate = d.getStartDate() != null ? d.getStartDate().format(dateFormatter) : "";
            String endDate = d.getEndDate() != null ? d.getEndDate().format(dateFormatter) : "";
            String status = d.isActive() ? "✅ Active" : "❌ Inactive";
            String valid = d.isValid() ? "✅ Còn hiệu lực" : "⏰ Hết hạn";
            
            tableModelDiscounts.addRow(new Object[] {
                d.getId(),
                d.getCode(),
                d.getPercentage() + "%",
                startDate,
                endDate,
                status,
                valid
            });
        }
    }

    private void addDiscount() {
        DiscountDialog dialog = new DiscountDialog(this, connection, null);
        dialog.setVisible(true);
        loadDiscounts();
    }

    private void updateDiscount() {
        int selectedRow = tableDiscounts.getSelectedRow();
        
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn mã giảm giá!");
            return;
        }
        
        Discount discount = currentDiscounts.get(selectedRow);
        DiscountDialog dialog = new DiscountDialog(this, connection, discount);
        dialog.setVisible(true);
        loadDiscounts();
    }

    private void deleteDiscount() {
        int selectedRow = tableDiscounts.getSelectedRow();
        
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn mã giảm giá!");
            return;
        }
        
        Discount discount = currentDiscounts.get(selectedRow);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn xóa mã giảm giá:\n" + discount.getCode() + "?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Response response = connection.deleteDiscount(discount.getId());
            
            if (response.isSuccess()) {
                showSuccess("Xóa mã giảm giá thành công!");
                loadDiscounts();
            } else {
                showError(response.getMessage());
            }
        }
    }
}