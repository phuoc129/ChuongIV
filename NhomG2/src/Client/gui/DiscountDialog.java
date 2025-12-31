package Client.gui;

import Client.ClientConnection;
import Server.model.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * DIALOG THÊM / SỬA MÃ GIẢM GIÁ (UI ĐẸP + JSPINNER)
 */
public class DiscountDialog extends JDialog {

    private ClientConnection connection;
    private Discount discount;
    private boolean isUpdate;

    private JTextField txtCode;
    private JTextField txtPercentage;
    private JSpinner spinnerStartDate;
    private JSpinner spinnerEndDate;
    private JCheckBox chkActive;
    private JButton btnSave;
    private JButton btnCancel;

    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color CANCEL_COLOR = new Color(149, 165, 166);

    public DiscountDialog(Frame parent, ClientConnection connection, Discount discount) {
        super(parent, discount == null ? "➕ Thêm mã giảm giá" : "✏️ Cập nhật mã giảm giá", true);
        this.connection = connection;
        this.discount = discount;
        this.isUpdate = discount != null;

        initComponents();

        if (isUpdate) {
            fillData();
            txtCode.setEditable(false);
            txtCode.setBackground(new Color(236, 240, 241));
        }
    }

    // ===================== UI =====================
    private void initComponents() {
        setSize(520, 480);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(245, 246, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== TITLE =====
        JLabel lblTitle = new JLabel(
                isUpdate ? "✏️ CẬP NHẬT MÃ GIẢM GIÁ" : "➕ THÊM MÃ GIẢM GIÁ",
                SwingConstants.CENTER
        );
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(PRIMARY_COLOR);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // ===== FORM =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 221, 225)),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 13);

        // ===== CODE =====
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel lblCode = new JLabel("Mã giảm giá:");
        lblCode.setFont(labelFont);
        formPanel.add(lblCode, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        txtCode = createTextField(inputFont);
        formPanel.add(txtCode, gbc);

        // ===== PERCENTAGE =====
        gbc.gridx = 0; gbc.gridy++;
        gbc.weightx = 0;
        JLabel lblPercent = new JLabel("Phần trăm giảm (%):");
        lblPercent.setFont(labelFont);
        formPanel.add(lblPercent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        txtPercentage = createTextField(inputFont);
        formPanel.add(txtPercentage, gbc);

        // ===== START DATE =====
        gbc.gridx = 0; gbc.gridy++;
        JLabel lblStart = new JLabel("Ngày bắt đầu:");
        lblStart.setFont(labelFont);
        formPanel.add(lblStart, gbc);

        gbc.gridx = 1;
        spinnerStartDate = createDateSpinner();
        formPanel.add(spinnerStartDate, gbc);

        // ===== END DATE =====
        gbc.gridx = 0; gbc.gridy++;
        JLabel lblEnd = new JLabel("Ngày kết thúc:");
        lblEnd.setFont(labelFont);
        formPanel.add(lblEnd, gbc);

        gbc.gridx = 1;
        spinnerEndDate = createDateSpinner();
        formPanel.add(spinnerEndDate, gbc);

        // ===== ACTIVE =====
        gbc.gridx = 0; gbc.gridy++;
        JLabel lblActive = new JLabel("Trạng thái:");
        lblActive.setFont(labelFont);
        formPanel.add(lblActive, gbc);

        gbc.gridx = 1;
        chkActive = new JCheckBox("Kích hoạt mã giảm giá");
        chkActive.setFont(inputFont);
        chkActive.setBackground(Color.WHITE);
        chkActive.setSelected(true);
        formPanel.add(chkActive, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // ===== BUTTONS =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(new Color(245, 246, 250));

        btnSave = createButton(isUpdate ? "✅ Cập nhật" : "➕ Thêm", SUCCESS_COLOR);
        btnSave.addActionListener(e -> save());

        btnCancel = createButton("❌ Hủy", CANCEL_COLOR);
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    // ===================== COMPONENTS =====================
    private JTextField createTextField(Font font) {
        JTextField txt = new JTextField();
        txt.setFont(font);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return txt;
    }

    private JSpinner createDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinner.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        return spinner;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(color);
            }
        });
        return btn;
    }

    // ===================== DATA =====================
    private void fillData() {
        txtCode.setText(discount.getCode());
        txtPercentage.setText(String.valueOf(discount.getPercentage()));

        if (discount.getStartDate() != null) {
            spinnerStartDate.setValue(Date.from(
                    discount.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
            ));
        }

        if (discount.getEndDate() != null) {
            spinnerEndDate.setValue(Date.from(
                    discount.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
            ));
        }

        chkActive.setSelected(discount.isActive());
    }

    // ===================== SAVE =====================
    private void save() {
        try {
            String code = txtCode.getText().trim().toUpperCase();
            String percentStr = txtPercentage.getText().trim();

            if (code.isEmpty() || percentStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "⚠️ Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            double percent = Double.parseDouble(percentStr);
            if (percent <= 0 || percent > 100) {
                JOptionPane.showMessageDialog(this, "⚠️ Phần trăm giảm phải từ 1–100!");
                return;
            }

            LocalDate start = ((Date) spinnerStartDate.getValue()).toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate end = ((Date) spinnerEndDate.getValue()).toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();

            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(this, "⚠️ Ngày kết thúc phải sau ngày bắt đầu!");
                return;
            }

            Discount d = new Discount(code, percent, start, end);
            d.setActive(chkActive.isSelected());

            Response res;
            if (isUpdate) {
                d.setId(discount.getId());
                res = connection.updateDiscount(d);
            } else {
                res = connection.addDiscount(d);
            }

            JOptionPane.showMessageDialog(this, res.getMessage());
            if (res.isSuccess()) dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Phần trăm giảm không hợp lệ!");
        }
    }
}
