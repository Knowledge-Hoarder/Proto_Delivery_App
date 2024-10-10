package com.example.ProtoDeliveryApp.models;

import org.json.JSONException;
import org.json.JSONObject;

public class ListSubmitResponse {
    private String packId, message;
    private int id,status;


    public ListSubmitResponse(int id, String packId, int status, String message) {
        this.id = id;
        this.packId = packId;
        this.status = status;
        this.message = message;
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("packId", packId);
            object.put("status", status);
            object.put("message", message);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return object;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPackId() {
        return packId;
    }
    public void setPackId(String packId) {
        this.packId = packId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
}
