package Client.gui;

import Client.ClientConnection;
import Server.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * FORM ĐĂNG NHẬP
 */
public class LoginFrame extends JFrame {
    private ClientConnection connection;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;

    public LoginFrame() {
        this.connection = new ClientConnection();
        initComponents();
        
        // Kết nối server
        if (!connection.connect()) {
            JOptionPane.showMessageDialog(this, 
                "Không thể kết nối đến server!\nVui lòng kiểm tra server đã chạy chưa.",
                "Lỗi kết nối", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void initComponents() {
        setTitle("Đăng nhập - Hệ thống quản lý bán hàng");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Header
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP HỆ THỐNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(25, 118, 210));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(lblUsername, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(lblPassword, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(txtPassword, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(76, 175, 80));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(120, 35));
        btnLogin.addActionListener(e -> handleLogin());

        btnExit = new JButton("Thoát");
        btnExit.setFont(new Font("Arial", Font.BOLD, 14));
        btnExit.setBackground(new Color(244, 67, 54));
        btnExit.setForeground(Color.BLACK);
        btnExit.setFocusPainted(false);
        btnExit.setPreferredSize(new Dimension(120, 35));
        btnExit.addActionListener(e -> System.exit(0));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Enter to login
        txtPassword.addActionListener(e -> handleLogin());
        
        // Default account hint
       
        
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập đầy đủ thông tin!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Disable button
        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        // Login
        Response response = connection.login(username, password);

        if (response.isSuccess()) {
            User user = connection.getCurrentUser();
            
            JOptionPane.showMessageDialog(this,
                "Đăng nhập thành công!\nXin chào, " + user.getFullName(),
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);

            // Mở main frame
            if (user.getRole().equals("ADMIN")) {
                new AdminMainFrame(connection).setVisible(true);
            } else {
                new UserMainFrame(connection).setVisible(true);
            }
            
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                response.getMessage(),
                "Đăng nhập thất bại",
                JOptionPane.ERROR_MESSAGE);
            
            btnLogin.setEnabled(true);
            btnLogin.setText("Đăng nhập");
            txtPassword.setText("");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}