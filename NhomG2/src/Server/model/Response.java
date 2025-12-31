package Server.model;

import java.io.Serializable;
import java.util.Map;

/**
 * RESPONSE MODEL - Gói tin trả về từ Server -> Client
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean success;  // Thành công hay thất bại
    private String message;   // Thông điệp mô tả
    private Map<String, Object> data;  // Dữ liệu trả về

    public Response() {}

    public Response(boolean success, String message, Map<String, Object> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters & Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{success=" + success + ", message='" + message + "'}";
    }
}