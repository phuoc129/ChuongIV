package Client.gui;

import Client.ClientConnection;
import Server.model.*;
import javax.swing.*;
import java.awt.*;

/**
 * DIALOG THÊM/SỬA SẢN PHẨM - ĐÃ SỬA
 */
public class ProductDialog extends JDialog {
    private ClientConnection connection;
    private Product product;
    private boolean isUpdate;
    
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtCategory;
    private JTextField txtPrice;
    private JTextField txtQuantity;
    private JTextArea txtDescription;
    private JButton btnSave;
    private JButton btnCancel;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);

    public ProductDialog(Frame parent, ClientConnection connection, Product product) {
        super(parent, product == null ? "➕ Thêm sản phẩm mới" : "✏️ Cập nhật sản phẩm", true);
        
        this.connection = connection;
        this.product = product;
        this.isUpdate = (product != null);
        
        initComponents();
        
        if (isUpdate) {
            fillData();
            txtId.setEditable(false);
            txtId.setBackground(new Color(236, 240, 241));
        }
    }

    private void initComponents() {
        setSize(550, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTitle = new JLabel(isUpdate ? "✏️ CẬP NHẬT SẢN PHẨM" : "➕ THÊM SẢN PHẨM MỚI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(PRIMARY_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Mã sản phẩm
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel lblId = new JLabel("Mã sản phẩm:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblId, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtId = new JTextField(20);
        txtId.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtId.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtId, gbc);

        // Tên sản phẩm
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel lblName = new JLabel("Tên sản phẩm:");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblName, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtName = new JTextField(20);
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtName, gbc);

        // Danh mục
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel lblCategory = new JLabel("Danh mục:");
        lblCategory.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblCategory, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtCategory = new JTextField(20);
        txtCategory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCategory.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtCategory, gbc);

        // Giá
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel lblPrice = new JLabel("Giá:");
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblPrice, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtPrice = new JTextField(20);
        txtPrice.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPrice.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtPrice, gbc);

        // Số lượng
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        JLabel lblQuantity = new JLabel("Số lượng:");
        lblQuantity.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblQuantity, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtQuantity = new JTextField(20);
        txtQuantity.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtQuantity.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtQuantity, gbc);

        // Mô tả
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblDescription = new JLabel("Mô tả:");
        lblDescription.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblDescription, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtDescription = new JTextArea(5, 20);
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        formPanel.add(scrollPane, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(new Color(236, 240, 241));
        
        btnSave = createStyledButton(isUpdate ? "✅ Cập nhật" : "➕ Thêm", SUCCESS_COLOR, 140);
        btnSave.addActionListener(e -> save());
        
        btnCancel = createStyledButton("❌ Hủy", new Color(149, 165, 166), 140);
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

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

    private void fillData() {
        txtId.setText(product.getId());
        txtName.setText(product.getName());
        txtCategory.setText(product.getCategory());
        txtPrice.setText(String.valueOf(product.getPrice()));
        txtQuantity.setText(String.valueOf(product.getQuantity()));
        txtDescription.setText(product.getDescription() != null ? product.getDescription() : "");
    }

    private void save() {
        try {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String category = txtCategory.getText().trim();
            String priceStr = txtPrice.getText().trim();
            String quantityStr = txtQuantity.getText().trim();
            String description = txtDescription.getText().trim();
            
            // Validate
            if (id.isEmpty() || name.isEmpty() || category.isEmpty() || 
                priceStr.isEmpty() || quantityStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "⚠️ Vui lòng nhập đầy đủ thông tin!", 
                    "Cảnh báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);
            
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "⚠️ Giá phải lớn hơn 0!", 
                    "Cảnh báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, 
                    "⚠️ Số lượng phải >= 0!", 
                    "Cảnh báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Create product object
            Product p = new Product(id, name, category, price, quantity);
            p.setDescription(description);
            
            Response response;
            
            if (isUpdate) {
                // ✅ GỌI UPDATE
                response = connection.updateProduct(p);
            } else {
                // GỌI INSERT
                response = connection.addProduct(p);
            }
            
            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, 
                    "✅ " + response.getMessage(), 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "❌ " + response.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "❌ Giá hoặc số lượng không hợp lệ!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}