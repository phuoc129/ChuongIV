package Client.gui;

import Server.model.*;
import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * DIALOG XEM ĐƠN HÀNG
 */
public class OrdersDialog extends JDialog {
    private List<Order> orders;
    private NumberFormat currencyFormat;
    private DateTimeFormatter dateFormat;

    public OrdersDialog(Frame parent, List<Order> orders, NumberFormat currencyFormat, DateTimeFormatter dateFormat) {
        super(parent, "Đơn hàng của tôi", true);
        
        this.orders = orders;
        this.currencyFormat = currencyFormat;
        this.dateFormat = dateFormat;
        
        initComponents();
    }

    private void initComponents() {
        setSize(800, 600);
        setLocationRelativeTo(getParent());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel lblTitle = new JLabel("DANH SÁCH ĐƠN HÀNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 150, 243));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Content
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        
        StringBuilder sb = new StringBuilder();
        
        if (orders.isEmpty()) {
            sb.append("\n\n          Bạn chưa có đơn hàng nào.\n");
        } else {
            for (Order order : orders) {
                sb.append("\n");
                sb.append("═══════════════════════════════════════════════════════════════\n");
                sb.append("  ĐƠN HÀNG #").append(order.getId()).append("\n");
                sb.append("═══════════════════════════════════════════════════════════════\n");
                sb.append("  Ngày đặt: ").append(order.getOrderDate().format(dateFormat)).append("\n");
                sb.append("  Trạng thái: ").append(order.getStatus()).append("\n");
                sb.append("───────────────────────────────────────────────────────────────\n");
                sb.append("  Chi tiết:\n");
                
                for (OrderItem item : order.getItems()) {
                    sb.append(String.format("    • %-30s x%-3d = %15s\n",
                        item.getProductName(),
                        item.getQuantity(),
                        currencyFormat.format(item.getSubtotal())));
                }
                
                sb.append("───────────────────────────────────────────────────────────────\n");
                sb.append(String.format("  TỔNG TIỀN: %48s\n", currencyFormat.format(order.getTotalAmount())));
                sb.append("═══════════════════════════════════════════════════════════════\n");
                sb.append("\n");
            }
            
            sb.append("\n  Tổng số đơn hàng: ").append(orders.size()).append("\n");
        }
        
        textArea.setText(sb.toString());
        textArea.setCaretPosition(0);
        
        mainPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(new Font("Arial", Font.BOLD, 13));
        btnClose.setBackground(new Color(158, 158, 158));
        btnClose.setForeground(Color.BLACK);
        btnClose.setPreferredSize(new Dimension(120, 35));
        btnClose.addActionListener(e -> dispose());
        
        buttonPanel.add(btnClose);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}