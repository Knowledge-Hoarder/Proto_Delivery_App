package com.example.ProtoDeliveryApp.modelsportal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Packs {
    int id, orderLine,orderQty, isScanned;
    String notes, packId, ordernum, hasMPack, routeSeq, partDesc;
    boolean flag;

    public Packs() {
    }
    public Packs(String packId, String notes, String ordernum) {
        this.packId = packId;
        this.notes = notes;
        this.ordernum = ordernum;
    }
    public Packs(int id, String packId) {
        this.id = id;
        this.packId = packId;
    }
    public Packs(int id, String packId, Boolean flag
    ) {
        this.id = id;
        this.packId = packId;
        this.flag = flag;
    }
    public Packs(int id,int orderLine, String packId) {
        this.id = id;
        this.orderLine = orderLine;
        this.packId = packId;
    }
    public Packs(int id, String packId,String routeSeq) {
        this.id = id;
        this.packId = packId;
        this.routeSeq = routeSeq;
    }
    public Packs(int id,int orderLine, String packId,String routeSeq) {
        this.id = id;
        this.packId = packId;
        this.orderLine = orderLine;
        this.routeSeq = routeSeq;
    }
    public Packs(int id,int orderLine, String packId,String routeSeq, String partDesc, int orderQty) {
        this.id = id;
        this.packId = packId;
        this.orderLine = orderLine;
        this.routeSeq = routeSeq;
        this.partDesc = partDesc;
        this.orderQty = orderQty;
    }
    public Packs(int id, String packId, String notes, String ordernum,String hasMPack) {
        this.id = id;
        this.packId = packId;
        this.notes = notes;
        this.ordernum = ordernum;
        this.hasMPack = hasMPack;
    }

    public Packs(int id, String packId, String ordernum, int bool, String hasMPack) {
        this.id = id;
        this.packId = packId;
        this.isScanned = bool;
        this.ordernum = ordernum;
        this.hasMPack = hasMPack;
    }

    public Packs(String packId, int bool) {
        this.packId = packId;
        this.isScanned = bool;
    }

    public Packs(int id, String notes, String packId, String ordernum, String hasMPack, int isScanned) {
        this.id = id;
        this.notes = notes;
        this.packId = packId;
        this.ordernum = ordernum;
        this.hasMPack = hasMPack;
        this.isScanned = isScanned;
    }
    public Packs(int id, String notes, String packId, String ordernum, String hasMPack, int isScanned, String routeseq) {
        this.id = id;
        this.notes = notes;
        this.packId = packId;
        this.ordernum = ordernum;
        this.hasMPack = hasMPack;
        this.isScanned = isScanned;
        this.routeSeq = routeseq;
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("packId", packId);
            object.put("notes", notes);
            object.put("orderNum", ordernum);
            object.put("orderLine", orderLine);
            object.put("hasMpack", hasMPack);
            object.put("partDesc", partDesc);
            object.put("orderQty", orderQty);
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
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public int getIdItemsToSub() {
        return id;
    }

    public int getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(int orderLine) {
        this.orderLine = orderLine;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum;
    }
    public int getIsScanned() {
        return isScanned;
    }
    public String getHasMPack() {
        return hasMPack;
    }
    public void setHasMPack(String hasMPack) {
        this.hasMPack = hasMPack;
    }
    public String getRouteSeq() {
        return routeSeq;
    }
    public void setRouteSeq(String routeSeq) {
        this.routeSeq = routeSeq;
    }
    public void setIsScanned(int isScanned) {
        this.isScanned = isScanned;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }

    public String getPartDesc() {
        return partDesc;
    }

    public void setPartDesc(String partDesc) {
        this.partDesc = partDesc;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
