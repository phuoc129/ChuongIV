package Client.gui;

import Client.ClientConnection;
import Server.model.*;
import javax.swing.*;
import java.awt.*;

/**
 * DIALOG THÊM/SỬA USER
 */
public class UserDialog extends JDialog {
    private ClientConnection connection;
    private User user;
    private boolean isUpdate;
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtFullName;
    private JComboBox<String> cboRole;
    private JCheckBox chkActive;
    private JButton btnSave;
    private JButton btnCancel;
    
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);

    public UserDialog(Frame parent, ClientConnection connection, User user) {
        super(parent, user == null ? "➕ Thêm người dùng mới" : "✏️ Cập nhật người dùng", true);
        
        this.connection = connection;
        this.user = user;
        this.isUpdate = (user != null);
        
        initComponents();
        
        if (isUpdate) {
            fillData();
            txtUsername.setEditable(false);
            txtUsername.setBackground(new Color(236, 240, 241));
        }
    }

    private void initComponents() {
        setSize(500, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(236, 240, 241));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTitle = new JLabel(isUpdate ? "✏️ CẬP NHẬT NGƯỜI DÙNG" : "➕ THÊM NGƯỜI DÙNG MỚI");
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

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblUsername, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtUsername, gbc);

        // Password (chỉ hiện khi thêm mới)
        if (!isUpdate) {
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0;
            JLabel lblPassword = new JLabel("Mật khẩu:");
            lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
            formPanel.add(lblPassword, gbc);
            
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            txtPassword = new JPasswordField(20);
            txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            formPanel.add(txtPassword, gbc);
        }

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = isUpdate ? 1 : 2;
        gbc.weightx = 0;
        JLabel lblFullName = new JLabel("Họ và tên:");
        lblFullName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblFullName, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtFullName = new JTextField(20);
        txtFullName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtFullName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        formPanel.add(txtFullName, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = isUpdate ? 2 : 3;
        gbc.weightx = 0;
        JLabel lblRole = new JLabel("Vai trò:");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblRole, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cboRole = new JComboBox<>(new String[]{"USER", "ADMIN"});
        cboRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cboRole, gbc);

        // Active
        gbc.gridx = 0;
        gbc.gridy = isUpdate ? 3 : 4;
        gbc.weightx = 0;
        JLabel lblActive = new JLabel("Trạng thái:");
        lblActive.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblActive, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        chkActive = new JCheckBox("Kích hoạt tài khoản");
        chkActive.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkActive.setBackground(Color.WHITE);
        chkActive.setSelected(true);
        formPanel.add(chkActive, gbc);

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
        txtUsername.setText(user.getUsername());
        txtFullName.setText(user.getFullName());
        cboRole.setSelectedItem(user.getRole());
        chkActive.setSelected(user.isActive());
    }

    private void save() {
        try {
            String username = txtUsername.getText().trim();
            String fullName = txtFullName.getText().trim();
            String role = (String) cboRole.getSelectedItem();
            boolean active = chkActive.isSelected();
            
            // Validate
            if (username.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "⚠️ Vui lòng nhập đầy đủ thông tin!", 
                    "Cảnh báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!isUpdate) {
                String password = new String(txtPassword.getPassword()).trim();
                
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "⚠️ Vui lòng nhập mật khẩu!", 
                        "Cảnh báo", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(this, 
                        "⚠️ Mật khẩu phải có ít nhất 6 ký tự!", 
                        "Cảnh báo", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Create new user
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setFullName(fullName);
                newUser.setRole(role);
                newUser.setActive(active);
                
                Response response = connection.addUser(newUser, password);
                
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, 
                        "✅ Thêm người dùng thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "❌ " + response.getMessage(), 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } else {
                // Update existing user
                user.setFullName(fullName);
                user.setRole(role);
                user.setActive(active);
                
                Response response = connection.updateUser(user);
                
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this, 
                        "✅ Cập nhật người dùng thành công!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "❌ " + response.getMessage(), 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "❌ Lỗi: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}