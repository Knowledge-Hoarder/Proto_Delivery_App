package com.example.ProtoDeliveryApp.modelsportal;

public class SyncResult {
    int status;
    String orderNum,message, id;

    public SyncResult(String orderNum, int status, String message) {
        this.status = status;
        this.orderNum = orderNum;
        this.message = message;
    }

    public SyncResult(String id, String orderNum, int status, String message) {
        this.status = status;
        this.orderNum = orderNum;
        this.id = id;
        this.message = message;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setPackid(String id) {
        this.id = id;
    }
}
