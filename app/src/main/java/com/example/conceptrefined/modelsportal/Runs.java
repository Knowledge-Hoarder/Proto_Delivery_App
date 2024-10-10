package com.example.ProtoDeliveryApp.modelsportal;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Runs {
    int id;
    String runid, shipByDate;
    ArrayList<Orders> orders;

    public Runs(int id, String runid, String shipByDate, ArrayList<Orders> orders) {
        this.id = id;
        this.runid = runid;
        this.shipByDate = shipByDate;
        this.orders = orders;
    }
    public Runs(int id, String runid, String shipByDate) {
        this.id = id;
        this.runid = runid;
        this.shipByDate = shipByDate;
    }
    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("runid", runid);
            object.put("shipByDate", shipByDate);
            object.put("orders", orders);
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

    public String getRunid() {
        return runid;
    }

    public void setRunid(String runid) {
        this.runid = runid;
    }

    public String getShipByDate() {
        return shipByDate;
    }

    public void setShipByDate(String shipByDate) {
        this.shipByDate = shipByDate;
    }

    public ArrayList<Orders> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Orders> orders) {
        this.orders = orders;
    }
}
