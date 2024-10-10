package com.example.ProtoDeliveryApp.models;

import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.utils.DataJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DeliveryDone {

    private int idDelivery;
    private String orderNum, dateDelivered,signature,address,note;
    private int isSync;
    public DeliveryDone(int idDelivery, String orderNum, String dateDelivered, String signature,
                        String address, int isSync) {
        this.idDelivery = idDelivery;
        this.dateDelivered = dateDelivered;
        this.orderNum = orderNum;
        this.signature = signature;
        this.address = address;
        this.isSync = isSync;
    }
    public DeliveryDone(int idDelivery, int isSync) {
        this.idDelivery = idDelivery;
        this.isSync = isSync;
    }
    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", idDelivery);
            object.put("dateDelivered", dateDelivered);
            object.put("orderNum", orderNum);
            object.put("address", address);
            object.put("isSync", isSync);
            object.put("signature", signature);
            object.put("driverNote", note);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return object;
    }

    public int getIdDelivery() {
        return idDelivery;
    }
    public void setIdDelivery(int idDelivery) {
        this.idDelivery = idDelivery;
    }
    public String getOrderNum() {
        return orderNum;
    }
    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }
    public String getDateDelivered() {
        return dateDelivered;
    }
    public void setDateDelivered(String dateDelivered) {
        this.dateDelivered = dateDelivered;
    }
    public String getSignature() {
        return signature;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public int getSync() {
        return isSync;
    }
    public void setSync(int sync) {
        isSync = sync;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
}
