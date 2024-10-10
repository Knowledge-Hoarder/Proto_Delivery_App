package com.example.ProtoDeliveryApp.modelsportal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Orders {
    int id, customernum;
    String ordernum, name, address1, address2, address3, city, state, zip, country, routeSeq;

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("ordernum", ordernum);
            object.put("name", name);
            object.put("address1", address1);
            object.put("address2", address2);
            object.put("address3", address3);
            object.put("city", city);
            object.put("state", state);
            object.put("zip", zip);
            object.put("country", country);
            object.put("customernum", customernum);
        } catch (JSONException e) {
            Log.e("Orders", "toJSON: ", e);
        }
        return object;
    }
    public Orders(int id, String ordernum) {
        this.id = id;
        this.ordernum = ordernum;
    }

    public Orders(int id, String name, String address1, String address2,
                  String address3, String city, String state, String zip, String country) {
        this.id = id;
        this.name = name;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
    }
    public Orders(int id, String ordernum, String name, String address1, String address2,
                  String address3, String city, String state, String zip, String country) {
        this.id = id;
        this.ordernum = ordernum;
        this.name = name;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
    }
    public Orders(int id, String ordernum, String name, String address1, String address2,
                  String address3, String city, String state, String zip, String country,
                  String routeSeq, int customernum) {
        this.id = id;
        this.ordernum = ordernum;
        this.name = name;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
        this.routeSeq = routeSeq;
        this.customernum = customernum;
    }
    public int getId() {
        return customernum;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setCustomernum(int customernum) {
        this.customernum = customernum;
    }
    public int getCustomernum() {
        return customernum;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRouteSeq() {
        return routeSeq;
    }

    public void setRouteSeq(String routeSeq) {
        this.routeSeq = routeSeq;
    }

    public static Orders fromJSON(JSONObject obj) {
        Orders orders = null;
        try {
            String name = obj.getString("shipToName").replace("'","")
                    .replace("-"," ");
            orders = new Orders(
                    0,
                    obj.getString("ordernum"),
                    name,
                    obj.getString("address1"),
                    obj.getString("address2"),
                    obj.getString("address3"),
                    obj.getString("city"),
                    obj.getString("state"),
                    obj.getString("zip"),
                    obj.getString("country"),
                    obj.getString("route_seq"),
                    obj.getInt("customernum")
            );
        } catch (JSONException e) {
            Log.e("Error", "fromJSON: ", e);
        }
        return orders;
    }

    public static ArrayList<Orders> fromJSONArray(String str) throws JSONException {
        ArrayList<Orders> orders=new ArrayList<>();
        JSONArray OrdersJSON = new JSONArray(str);
        for(int x =0; x<OrdersJSON.length();x++){
            JSONObject orderJson = (JSONObject) OrdersJSON.get(x);
            Orders order = Orders.fromJSON(orderJson);
            orders.add(order);
        }
        return orders;
    }
}
