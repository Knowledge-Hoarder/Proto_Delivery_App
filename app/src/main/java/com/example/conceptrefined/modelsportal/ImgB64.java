package com.example.ProtoDeliveryApp.modelsportal;

import org.json.JSONException;
import org.json.JSONObject;

public class ImgB64 {
    int id,packId;
    private String orderNum, imageB64;

    public ImgB64(int id, String orderNum, String imageB64) {
        this.id = id;
        this.orderNum = orderNum;
        this.imageB64 = imageB64;
    }
    public ImgB64(int id, String orderNum, String imageB64, int packId) {
        this.id = id;
        this.orderNum = orderNum;
        this.imageB64 = imageB64;
        this.packId = packId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getImageB64() {
        return imageB64;
    }

    public void setImageB64(String imageB64) {
        this.imageB64 = imageB64;
    }

    public int getPackId() {
        return packId;
    }

    public void setPackId(int packId) {
        this.packId = packId;
    }

    public JSONObject toJSON(){
        JSONObject object = new JSONObject();
        try {
            object.put("id",getId());
            object.put("ordernum", orderNum);
            object.put("imgb64", imageB64);
            object.put("packId", packId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return object;
    }

}
