package com.example.ProtoDeliveryApp.listeners;

import com.example.ProtoDeliveryApp.modelsportal.Orders;

import java.util.ArrayList;

public interface IOrderItemsListener {
    void onDetailBtnPressed(int fk, String cltName,String address1, int orderStatus);
    void onDeliverBtnPressed(ArrayList<Orders> orders, int customernum);
}
