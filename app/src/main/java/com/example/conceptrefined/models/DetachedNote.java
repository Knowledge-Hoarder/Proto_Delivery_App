package com.example.ProtoDeliveryApp.models;

import android.util.Log;

import com.example.ProtoDeliveryApp.modelsportal.Orders;

import org.json.JSONException;
import org.json.JSONObject;

public class DetachedNote {

    int id, customernum,isSync;
    String note;

    public DetachedNote(int id, int customernum, int isSync) {
        this.id = id;
        this.customernum = customernum;
        this.isSync = isSync;
    }
    public DetachedNote(int id, int customernum, int isSync, String note) {
        this.id = id;
        this.customernum = customernum;
        this.isSync = isSync;
        this.note = note;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomernum() {
        return customernum;
    }

    public void setCustomernum(int customernum) {
        this.customernum = customernum;
    }

    public int getIsSync() {
        return isSync;
    }

    public void setIsSync(int isSync) {
        this.isSync = isSync;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("customernum", customernum);
            object.put("isSync", isSync);
            object.put("note", note);
        } catch (JSONException e) {
            Log.e("DetachedNote", "toJSON: ", e);
        }
        return object;
    }

    public static DetachedNote fromJSON(JSONObject obj) {
        DetachedNote note = null;
        try {
            note = new DetachedNote(
                    obj.getInt("id"),
                    obj.getInt("customernum"),
                    obj.getInt("isSync"),
                    obj.getString("note")
            );
        } catch (JSONException e) {
            Log.e("DetachedNote", "fromJSON: ", e);
        }
        return note;
    }
}
