package dev.kropholler.demo.model;

import java.io.Serializable;

public class NotificationEvent implements Serializable {
    private String userId;
    private String message;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationEvent() {

    }
}
