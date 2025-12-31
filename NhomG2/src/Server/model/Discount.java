package Server.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DISCOUNT MODEL - Mã giảm giá
 */
public class Discount implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String code;           // Mã giảm giá (VD: SUMMER2025)
    private double percentage;     // Phần trăm giảm (0-100)
    private LocalDate startDate;   // Ngày bắt đầu
    private LocalDate endDate;     // Ngày kết thúc
    private boolean active;        // Còn hiệu lực hay không

    public Discount() {}

    public Discount(String code, double percentage, LocalDate startDate, LocalDate endDate) {
        this.code = code;
        this.percentage = percentage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    /**
     * Kiểm tra mã giảm giá còn hiệu lực
     */
    public boolean isValid() {
        if (!active) return false;
        
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }
    
    /**
     * Tính số tiền giảm
     */
    public double calculateDiscount(double amount) {
        if (!isValid()) return 0;
        return amount * (percentage / 100.0);
    }

    @Override
    public String toString() {
        return "Discount{" +
                "code='" + code + '\'' +
                ", percentage=" + percentage +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", active=" + active +
                '}';
    }
}