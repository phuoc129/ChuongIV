package Server.dao;

import Server.model.*;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ORDER DAO - Xử lý JSON File (Dữ liệu KHÔNG có cấu trúc)
 */
public class OrderDAO {
    private static final String ORDERS_FILE = "orders.json";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private Gson gson;
    private int nextOrderId;

    public OrderDAO() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        
        // Đọc ID cao nhất từ file
        this.nextOrderId = getMaxOrderId() + 1;
    }

    /**
     * Tạo đơn hàng mới
     */
    public boolean createOrder(Order order) {
        try {
            List<Order> orders = getAllOrders();
            
            // Gán ID và thông tin cho order
            order.setId(nextOrderId++);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PENDING");
            
            orders.add(order);
            
            return saveOrders(orders);
            
        } catch (Exception e) {
            System.err.println("Lỗi createOrder: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy đơn hàng theo user ID
     */
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> allOrders = getAllOrders();
        List<Order> userOrders = new ArrayList<>();
        
        for (Order order : allOrders) {
            if (order.getUserId() == userId) {
                userOrders.add(order);
            }
        }
        
        return userOrders;
    }

    /**
     * Lấy tất cả đơn hàng
     */
    public List<Order> getAllOrders() {
        try {
            File file = new File(ORDERS_FILE);
            
            if (!file.exists()) {
                return new ArrayList<>();
            }
            
            Reader reader = new FileReader(file);
            Order[] ordersArray = gson.fromJson(reader, Order[].class);
            reader.close();
            
            if (ordersArray == null) {
                return new ArrayList<>();
            }
            
            return new ArrayList<>(Arrays.asList(ordersArray));
            
        } catch (Exception e) {
            System.err.println("Lỗi getAllOrders: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Lưu danh sách đơn hàng vào file
     */
    private boolean saveOrders(List<Order> orders) {
        try {
            Writer writer = new FileWriter(ORDERS_FILE);
            gson.toJson(orders, writer);
            writer.close();
            return true;
            
        } catch (IOException e) {
            System.err.println("Lỗi saveOrders: " + e.getMessage());
            return false;
        }
    }

    /**
     * Đếm số đơn hàng
     */
    public int getOrderCount() {
        return getAllOrders().size();
    }

    /**
     * Tính tổng doanh thu
     */
    public double getTotalRevenue() {
        List<Order> orders = getAllOrders();
        double total = 0;
        
        for (Order order : orders) {
            total += order.getTotalAmount();
        }
        
        return total;
    }

    /**
     * Lấy ID cao nhất từ file để tránh trùng lặp
     */
    private int getMaxOrderId() {
        List<Order> orders = getAllOrders();
        int maxId = 0;
        
        for (Order order : orders) {
            if (order.getId() > maxId) {
                maxId = order.getId();
            }
        }
        
        return maxId;
    }

    /**
     * Adapter để serialize/deserialize LocalDateTime với Gson
     */
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(formatter));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String dateStr = in.nextString();
            return LocalDateTime.parse(dateStr, formatter);
        }
    }
}