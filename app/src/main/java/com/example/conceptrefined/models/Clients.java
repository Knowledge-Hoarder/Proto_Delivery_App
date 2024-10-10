package com.example.ProtoDeliveryApp.models;

import com.example.ProtoDeliveryApp.modelsportal.Orders;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Clients {
    int id, customernum;
    String name, address1, address2, address3, city, state, zip, country;
    ArrayList<Orders> orders;

    public Clients(int id, String name, String address1, String address2, String address3, String city, String state, String zip, String country, int customernum) {
        this.id = id;
        this.name = name;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
        this.customernum = customernum;
    }

    public Clients(int id, String name, String address1, String address2, String address3, String city, String state, String zip, String country, ArrayList<Orders> orders) {
        this.id = id;
        this.name = name;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
        this.orders = orders;
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

    public ArrayList<Orders> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Orders> orders) {
        this.orders = orders;
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
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
            throw new RuntimeException(e);
        }
        return object;
    }

}
