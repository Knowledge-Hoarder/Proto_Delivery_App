package com.example.ProtoDeliveryApp.modelsportal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MasterPacks {
    int id;
    JSONArray packs;
    String masterPackId, orderNum;


    public MasterPacks(int id, String masterPackId, JSONArray packs) {
        this.id = id;
        this.masterPackId = masterPackId;
        this.packs = packs;
    }
    public MasterPacks(int id, String masterPackId) {
        this.id = id;
        this.masterPackId = masterPackId;
    }
    public MasterPacks(int id, String masterPackId, String orderNum) {
        this.id = id;
        this.masterPackId = masterPackId;
        this.orderNum = orderNum;
    }
    public MasterPacks(String masterPackId, JSONArray packs) {
        this.masterPackId = masterPackId;
        this.packs = packs;
    }

    public static ArrayList<MasterPacks> fromJSONArray(String str) throws JSONException {
        ArrayList<MasterPacks> mp =  new ArrayList<>();
        JSONArray OrdersJSON = new JSONArray(str);
        for(int x =0; x<OrdersJSON.length();x++){
            JSONObject orderJson = (JSONObject) OrdersJSON.get(x);

            MasterPacks mstp = MasterPacks.fromJSON(orderJson);
            mp.add(mstp);
        }
        return mp;
    }

    private static MasterPacks fromJSON(JSONObject orderJson) throws JSONException {
        JSONArray p = orderJson.getJSONArray("packs");
        return new MasterPacks(orderJson.getString("masterPackId"), p);
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("masterPackId", masterPackId);
            object.put("packs", packs);
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

    public String getMasterPackId() {
        return masterPackId;
    }

    public void setMasterPackId(String masterPackId) {
        this.masterPackId = masterPackId;
    }

    public JSONArray getPacks() {
        return packs;
    }

    public void setPacks(JSONArray packs) {
        this.packs = packs;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }
}
