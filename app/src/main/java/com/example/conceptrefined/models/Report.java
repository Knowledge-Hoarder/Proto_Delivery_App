package com.example.ProtoDeliveryApp.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Report {
    int id;
    String packId, orderNum, driverNote;
    Boolean isSync;

    public Report(int id, String packId, String orderNum, String driverNote) {
        this.id = id;
        this.packId = packId;
        this.orderNum = orderNum;
        this.driverNote = driverNote;
    }

    public Report(int id, String packId, String orderNum, String driverNote, Boolean isSync) {
        this.id = id;
        this.packId = packId;
        this.orderNum = orderNum;
        this.driverNote = driverNote;
        this.isSync = isSync;
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

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getDriverNote() {
        return driverNote;
    }

    public void setDriverNote(String driverNote) {
        this.driverNote = driverNote;
    }

    public Boolean getSync() {
        return isSync;
    }

    public void setSync(Boolean sync) {
        isSync = sync;
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("packId", packId);
            object.put("note", driverNote);
            object.put("orderNum", orderNum);
            object.put("isSync", isSync);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}
