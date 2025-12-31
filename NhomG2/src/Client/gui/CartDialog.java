package Client.gui;

import Client.ClientConnection;
import Server.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/**
 * DIALOG GIỎ HÀNG
 */
public class CartDialog extends JDialog {
    private ClientConnection connection;
    private Map<String, Integer> cart;
    private List<Product> allProducts;
    private NumberFormat currencyFormat;
    
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    private double totalAmount;

    public CartDialog(Frame parent, ClientConnection connection, Map<String, Integer> cart, 
                      List<Product> allProducts, NumberFormat currencyFormat) {
        super(parent, "Giỏ hàng", true);
        
        this.connection = connection;
        this.cart = cart;
        this.allProducts = allProducts;
        this.currencyFormat = currencyFormat;
        
        initComponents();
        loadCart();
    }

    private void initComponents() {
        setSize(700, 500);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Table
        String[] columns = {"Sản phẩm", "Giá", "Số lượng", "Thành tiền"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        // Total
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Tổng tiền: 0 ₫");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(new Color(244, 67, 54));
        totalPanel.add(lblTotal);
        
        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton btnRemove = new JButton("Xóa sản phẩm");
        btnRemove.setFont(new Font("Arial", Font.BOLD, 13));
        btnRemove.setBackground(new Color(255, 152, 0));
        btnRemove.setForeground(Color.BLACK);
        btnRemove.setPreferredSize(new Dimension(140, 35));
        btnRemove.addActionListener(e -> removeItem());
        
        JButton btnOrder = new JButton("Đặt hàng");
        btnOrder.setFont(new Font("Arial", Font.BOLD, 13));
        btnOrder.setBackground(new Color(76, 175, 80));
        btnOrder.setForeground(Color.BLACK);
        btnOrder.setPreferredSize(new Dimension(140, 35));
        btnOrder.addActionListener(e -> placeOrder());
        
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Arial", Font.BOLD, 13));
        btnClose.setBackground(new Color(158, 158, 158));
        btnClose.setForeground(Color.BLACK);
        btnClose.setPreferredSize(new Dimension(140, 35));
        btnClose.addActionListener(e -> dispose());
        
        buttonPanel.add(btnRemove);
        buttonPanel.add(btnOrder);
        buttonPanel.add(btnClose);
        
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadCart() {
        tableModel.setRowCount(0);
        totalAmount = 0;
        
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();
            
            Product product = findProduct(productId);
            
            if (product != null) {
                double subtotal = product.getPrice() * quantity;
                totalAmount += subtotal;
                
                tableModel.addRow(new Object[] {
                    product.getName(),
                    currencyFormat.format(product.getPrice()),
                    quantity,
                    currencyFormat.format(subtotal)
                });
            }
        }
        
        lblTotal.setText("Tổng tiền: " + currencyFormat.format(totalAmount));
    }

    private Product findProduct(String id) {
        for (Product p : allProducts) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    private void removeItem() {
        int selectedRow = table.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String productName = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Find product id
        String productId = null;
        for (Product p : allProducts) {
            if (p.getName().equals(productName)) {
                productId = p.getId();
                break;
            }
        }
        
        if (productId != null) {
            cart.remove(productId);
            loadCart();
        }
    }

    private void placeOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận đặt hàng với tổng tiền: " + currencyFormat.format(totalAmount) + "?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Order order = new Order();
            order.setTotalAmount(totalAmount);
            
            for (Map.Entry<String, Integer> entry : cart.entrySet()) {
                Product product = findProduct(entry.getKey());
                
                if (product != null) {
                    OrderItem item = new OrderItem(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        entry.getValue()
                    );
                    order.addItem(item);
                }
            }
            
            Response response = connection.createOrder(order);
            
            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Đặt hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                cart.clear();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}