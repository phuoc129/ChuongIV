package Server.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int userId;
    private String username;
    private LocalDateTime orderDate;
    private double totalAmount;
    private String status;
    private List<OrderItem> items;

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(int id, int userId, String username, double totalAmount) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.totalAmount = totalAmount;
        this.items = new ArrayList<>();
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    
    public void addItem(OrderItem item) {
        this.items.add(item);
    }
}