package Client.gui;

import Server.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * DIALOG XEM ĐƠN HÀNG - CẢI TIẾN
 */
public class OrdersDialogImproved extends JDialog {
    private List<Order> orders;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormat;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color WARNING_COLOR = new Color(243, 156, 18);

    public OrdersDialogImproved(Frame parent, List<Order> orders, 
                               NumberFormat currencyFormat, DateTimeFormatter dateFormat) {
        super(parent, "📋 Đơn hàng của tôi", true);
        
        this.orders = orders;
        this.currencyFormat = currencyFormat;
        this.dateFormat = dateFormat;
        
        initComponents();
    }

    private void initComponents() {
        setSize(900, 650);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== TOP PANEL - TITLE =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel lblTitle = new JLabel("📋 LỊCH SỬ ĐƠN HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(PRIMARY_COLOR);
        
        JLabel lblCount = new JLabel(orders.size() + " đơn hàng");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCount.setForeground(new Color(127, 140, 141));
        
        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(lblCount, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ===== CENTER PANEL - ORDERS LIST =====
        if (orders.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setBackground(Color.WHITE);
            emptyPanel.setBorder(new EmptyBorder(100, 50, 100, 50));
            
            JLabel lblEmpty = new JLabel("📦");
            lblEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 80));
            lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel lblEmptyText = new JLabel("Bạn chưa có đơn hàng nào");
            lblEmptyText.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            lblEmptyText.setForeground(new Color(127, 140, 141));
            lblEmptyText.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            emptyPanel.add(lblEmpty);
            emptyPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            emptyPanel.add(lblEmptyText);
            
            mainPanel.add(emptyPanel, BorderLayout.CENTER);
        } else {
            JPanel ordersPanel = new JPanel();
            ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
            ordersPanel.setBackground(new Color(236, 240, 241));
            
            for (Order order : orders) {
                ordersPanel.add(createOrderCard(order));
                ordersPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
            
            JScrollPane scrollPane = new JScrollPane(ordersPanel);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            
            mainPanel.add(scrollPane, BorderLayout.CENTER);
        }

        // ===== BOTTOM PANEL - BUTTON =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(236, 240, 241));
        
        JButton btnClose = createStyledButton("✅ Đóng", PRIMARY_COLOR, 150);
        btnClose.addActionListener(e -> dispose());
        
        buttonPanel.add(btnClose);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createOrderCard(Order order) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.BLACK);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        // Top - Order info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.BLACK);
        
        JPanel leftInfo = new JPanel(new GridLayout(2, 1, 5, 5));
        leftInfo.setBackground(Color.BLACK);
        
        JLabel lblOrderId = new JLabel("📦 Đơn hàng #" + order.getId());
        lblOrderId.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblOrderId.setForeground(PRIMARY_COLOR);
        
        JLabel lblDate = new JLabel("📅 " + order.getOrderDate().format(dateFormat));
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDate.setForeground(new Color(127, 140, 141));
        
        leftInfo.add(lblOrderId);
        leftInfo.add(lblDate);
        
        JLabel lblStatus = new JLabel(getStatusText(order.getStatus()));
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatus.setForeground(getStatusColor(order.getStatus()));
        lblStatus.setOpaque(true);
        lblStatus.setBackground(getStatusBgColor(order.getStatus()));
        lblStatus.setBorder(new EmptyBorder(5, 15, 5, 15));
        
        topPanel.add(leftInfo, BorderLayout.WEST);
        topPanel.add(lblStatus, BorderLayout.EAST);
        
        card.add(topPanel, BorderLayout.NORTH);
        
        // Center - Items
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(new Color(248, 249, 250));
        itemsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        for (OrderItem item : order.getItems()) {
            JPanel itemRow = new JPanel(new BorderLayout());
            itemRow.setBackground(new Color(248, 249, 250));
            itemRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            
            JLabel lblItem = new JLabel("• " + item.getProductName() + " x" + item.getQuantity());
            lblItem.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            
            JLabel lblPrice = new JLabel(currencyFormat.format(item.getSubtotal()));
            lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblPrice.setForeground(PRIMARY_COLOR);
            
            itemRow.add(lblItem, BorderLayout.WEST);
            itemRow.add(lblPrice, BorderLayout.EAST);
            
            itemsPanel.add(itemRow);
            itemsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        card.add(itemsPanel, BorderLayout.CENTER);
        
        // Bottom - Total
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(Color.BLACK);
        
        JLabel lblTotalLabel = new JLabel("TỔNG TIỀN: ");
        lblTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        JLabel lblTotal = new JLabel(currencyFormat.format(order.getTotalAmount()));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(SUCCESS_COLOR);
        
        totalPanel.add(lblTotalLabel);
        totalPanel.add(lblTotal);
        
        card.add(totalPanel, BorderLayout.SOUTH);
        
        return card;
    }

    private String getStatusText(String status) {
        switch (status) {
            case "PENDING": return "⏳ Đang xử lý";
            case "CONFIRMED": return "✅ Đã xác nhận";
            case "SHIPPING": return "🚚 Đang giao";
            case "COMPLETED": return "✅ Hoàn thành";
            case "CANCELLED": return "❌ Đã hủy";
            default: return status;
        }
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "PENDING": return new Color(243, 156, 18);
            case "CONFIRMED": return new Color(52, 152, 219);
            case "SHIPPING": return new Color(155, 89, 182);
            case "COMPLETED": return new Color(39, 174, 96);
            case "CANCELLED": return new Color(231, 76, 60);
            default: return Color.GRAY;
        }
    }

    private Color getStatusBgColor(String status) {
        switch (status) {
            case "PENDING": return new Color(243, 156, 18, 30);
            case "CONFIRMED": return new Color(52, 152, 219, 30);
            case "SHIPPING": return new Color(155, 89, 182, 30);
            case "COMPLETED": return new Color(39, 174, 96, 30);
            case "CANCELLED": return new Color(231, 76, 60, 30);
            default: return new Color(127, 140, 141, 30);
        }
    }

    private JButton createStyledButton(String text, Color color, int width) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, 40));
        
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
}