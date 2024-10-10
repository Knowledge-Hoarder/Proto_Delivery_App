package com.example.ProtoDeliveryApp.modelsportal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderItems {
        int id, orderLine, itemQty;
        String itemDesc, itemLenght;

        public OrderItems(int id, int orderLine, String itemDesc, int itemQty, String itemLenght) {
            this.id = id;
            this.orderLine = orderLine;
            this.itemQty = itemQty;
            this.itemDesc = itemDesc;
            this.itemLenght = itemLenght;
        }
    public OrderItems(int orderLine, String itemDesc, int itemQty, String itemLenght) {
        this.orderLine = orderLine;
        this.itemQty = itemQty;
        this.itemDesc = itemDesc;
        this.itemLenght = itemLenght;
    }

    public static ArrayList<OrderItems> fromJSONArray(String str) throws JSONException {
        ArrayList<OrderItems> oi =  new ArrayList<>();
        JSONArray OrdersJSON = new JSONArray(str);
        for(int x =0; x<OrdersJSON.length();x++){
            JSONObject orderJson = (JSONObject) OrdersJSON.get(x);
            OrderItems items = new OrderItems(
                    orderJson.getInt("orderLine"),
                    orderJson.getString("itemDesc"),
                    orderJson.getInt("itemQty"),
                    orderJson.getString("itemLenght"));
            oi.add(items);
        }
        return oi;
    }

    public JSONObject toJSON() {
                JSONObject object = new JSONObject();
                try {
                        object.put("orderLine", orderLine);
                        object.put("itemQty", itemQty);
                        object.put("itemDesc", itemDesc);
                        object.put("itemLenght", itemLenght);
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

        public int getOrderLine() {
                return orderLine;
        }

        public void setOrderLine(int orderLine) {
                this.orderLine = orderLine;
        }

        public int getItemQty() {
                return itemQty;
        }

        public void setItemQty(int itemQty) {
                this.itemQty = itemQty;
        }

        public String getItemDesc() {
                return itemDesc;
        }

        public void setItemDesc(String itemDesc) {
                this.itemDesc = itemDesc;
        }

        public String getItemLenght() {
                return itemLenght;
        }

        public void setItemLenght(String itemLenght) {
                this.itemLenght = itemLenght;
        }
}
