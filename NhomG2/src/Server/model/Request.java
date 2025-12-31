package Server.model;

import java.io.Serializable;
import java.util.Map;

/**
 * REQUEST MODEL - Gói tin gửi từ Client -> Server
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String action;  // LOGIN, GET_ALL_PRODUCTS, CREATE_ORDER, etc.
    private Map<String, Object> data;  // Dữ liệu đi kèm

    public Request() {}

    public Request(String action, Map<String, Object> data) {
        this.action = action;
        this.data = data;
    }

    // Getters & Setters
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Request{action='" + action + "', data=" + data + "}";
    }
}