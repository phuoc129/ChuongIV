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
 * GIAO DIỆN NGƯỜI DÙNG - CẢI TIẾN ĐẸP HƠN
 */
public class UserMainFrame extends JFrame {
    private ClientConnection connection;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormat;
    
    private JTable tableProducts;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JLabel lblCartCount;
    private List<Product> currentProducts;
    private Map<String, Integer> cart;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color INFO_COLOR = new Color(52, 152, 219);

    public UserMainFrame(ClientConnection connection) {
        this.connection = connection;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.currentProducts = new ArrayList<>();
        this.cart = new HashMap<>();
        
        initComponents();
        loadProducts();
    }

    private void initComponents() {
        setTitle("🛒 Hệ thống bán hàng - " + connection.getCurrentUser().getFullName());
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ===== TOP PANEL - HEADER =====
        JPanel topPanel = new JPanel(new BorderLayout(15, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Left - Welcome
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.setBackground(Color.WHITE);
        
        JLabel lblWelcome = new JLabel("Xin chào, " + connection.getCurrentUser().getFullName());
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWelcome.setForeground(PRIMARY_COLOR);
        welcomePanel.add(lblWelcome);
        
        topPanel.add(welcomePanel, BorderLayout.WEST);
        
        // Center - Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        
        JLabel lblSearch = new JLabel("");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        txtSearch = new JTextField(35);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtSearch.addActionListener(e -> searchProducts());
        
        JButton btnSearch = createStyledButton("Tìm kiếm", INFO_COLOR, 120);
        btnSearch.addActionListener(e -> searchProducts());
        
        JButton btnViewAll = createStyledButton("Xem tất cả", new Color(149, 165, 166), 120);
        btnViewAll.addActionListener(e -> loadProducts());
        
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnViewAll);
        
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ===== CENTER PANEL - TABLE =====
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(236, 240, 241));
        
        // Table header label
        JLabel lblTableTitle = new JLabel("📦 DANH SÁCH SẢN PHẨM");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTableTitle.setForeground(PRIMARY_COLOR);
        lblTableTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        centerPanel.add(lblTableTitle, BorderLayout.NORTH);
        
        String[] columns = {"Mã SP", "Tên sản phẩm", "Danh mục", "Giá", "Số lượng", "Mô tả"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableProducts = new JTable(tableModel);
        tableProducts.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableProducts.setRowHeight(35);
        tableProducts.setSelectionBackground(new Color(52, 152, 219, 50));
        tableProducts.setGridColor(new Color(189, 195, 199));
        tableProducts.setShowGrid(true);
        
        tableProducts.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableProducts.getTableHeader().setBackground(PRIMARY_COLOR);
        tableProducts.getTableHeader().setForeground(Color.BLACK);
        tableProducts.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        // Center align columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableProducts.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableProducts.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tableProducts.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tableProducts.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        // Column widths
        tableProducts.getColumnModel().getColumn(0).setPreferredWidth(80);
        tableProducts.getColumnModel().getColumn(1).setPreferredWidth(250);
        tableProducts.getColumnModel().getColumn(2).setPreferredWidth(120);
        tableProducts.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableProducts.getColumnModel().getColumn(4).setPreferredWidth(80);
        tableProducts.getColumnModel().getColumn(5).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(tableProducts);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ===== RIGHT PANEL - ACTIONS =====
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));
        rightPanel.setPreferredSize(new Dimension(220, 0));
        
        // Cart info panel
        JPanel cartInfoPanel = new JPanel();
        cartInfoPanel.setLayout(new BoxLayout(cartInfoPanel, BoxLayout.Y_AXIS));
        cartInfoPanel.setBackground(new Color(241, 196, 15, 30));
        cartInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(WARNING_COLOR, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        cartInfoPanel.setMaximumSize(new Dimension(190, 120));
        cartInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblCartIcon = new JLabel("🛒", SwingConstants.CENTER);
        lblCartIcon.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        lblCartIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        lblCartCount = new JLabel("0 sản phẩm", SwingConstants.CENTER);
        lblCartCount.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCartCount.setForeground(WARNING_COLOR);
        lblCartCount.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        cartInfoPanel.add(lblCartIcon);
        cartInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        cartInfoPanel.add(lblCartCount);
        
        rightPanel.add(cartInfoPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Buttons
        JButton btnAddToCart = createActionButton("🛍️ Thêm vào giỏ", SUCCESS_COLOR);
        btnAddToCart.addActionListener(e -> addToCart());
        
        JButton btnViewCart = createActionButton("🛒 Xem giỏ hàng", WARNING_COLOR);
        btnViewCart.addActionListener(e -> viewCart());
        
        JButton btnMyOrders = createActionButton("📋 Đơn hàng của tôi", INFO_COLOR);
        btnMyOrders.addActionListener(e -> viewMyOrders());
        
        JButton btnRefresh = createActionButton("🔄 Làm mới", new Color(52, 73, 94));
        btnRefresh.addActionListener(e -> loadProducts());
        
        JButton btnLogout = createActionButton("🚪 Đăng xuất", DANGER_COLOR);
        btnLogout.addActionListener(e -> logout());
        
        rightPanel.add(btnAddToCart);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightPanel.add(btnViewCart);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightPanel.add(btnMyOrders);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightPanel.add(btnRefresh);
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(btnLogout);
        
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color color, int width) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, 35));
        
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
        button.setMaximumSize(new Dimension(190, 45));
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

    @SuppressWarnings("unchecked")
    private void loadProducts() {
        Response response = connection.getAllProducts();
        
        if (response.isSuccess()) {
            currentProducts = (List<Product>) response.getData().get("products");
            updateTable(currentProducts);
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
            updateTable(currentProducts);
            
            if (currentProducts.isEmpty()) {
                showInfo("Không tìm thấy sản phẩm nào!");
            }
        } else {
            showError(response.getMessage());
        }
    }

    private void updateTable(List<Product> products) {
        tableModel.setRowCount(0);
        
        for (Product p : products) {
            tableModel.addRow(new Object[] {
                p.getId(),
                p.getName(),
                p.getCategory(),
                currencyFormat.format(p.getPrice()),
                p.getQuantity(),
                p.getDescription() != null ? p.getDescription() : ""
            });
        }
    }

    private void addToCart() {
        int selectedRow = tableProducts.getSelectedRow();
        
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn sản phẩm!");
            return;
        }
        
        Product product = currentProducts.get(selectedRow);
        
        if (product.getQuantity() <= 0) {
            showWarning("Sản phẩm đã hết hàng!");
            return;
        }
        
        String input = JOptionPane.showInputDialog(this, 
            "Nhập số lượng cần mua:", 
            "Thêm vào giỏ hàng", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (input == null || input.trim().isEmpty()) return;
        
        try {
            int quantity = Integer.parseInt(input.trim());
            
            if (quantity <= 0) {
                showError("Số lượng phải lớn hơn 0!");
                return;
            }
            
            int currentInCart = cart.getOrDefault(product.getId(), 0);
            
            if (currentInCart + quantity > product.getQuantity()) {
                showError("Không đủ hàng! Chỉ còn " + product.getQuantity());
                return;
            }
            
            cart.put(product.getId(), currentInCart + quantity);
            updateCartCount();
            
            showSuccess("Đã thêm " + quantity + " " + product.getName() + " vào giỏ hàng!");
            
        } catch (NumberFormatException ex) {
            showError("Số lượng không hợp lệ!");
        }
    }

    private void viewCart() {
        if (cart.isEmpty()) {
            showInfo("Giỏ hàng trống!");
            return;
        }
        
        new CartDialogImproved(this, connection, cart, currentProducts, currencyFormat).setVisible(true);
        
        updateCartCount();
        
        if (cart.isEmpty()) {
            loadProducts();
        }
    }

    @SuppressWarnings("unchecked")
    private void viewMyOrders() {
        Response response = connection.getUserOrders();
        
        if (response.isSuccess()) {
            List<Order> orders = (List<Order>) response.getData().get("orders");
            new OrdersDialogImproved(this, orders, currencyFormat, dateFormat).setVisible(true);
        } else {
            showError(response.getMessage());
        }
    }

    private void updateCartCount() {
        int totalItems = 0;
        for (int qty : cart.values()) {
            totalItems += qty;
        }
        lblCartCount.setText(totalItems + " sản phẩm");
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn đăng xuất?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
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

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}