package com.example.ProtoDeliveryApp.listeners;

import com.example.ProtoDeliveryApp.modelsportal.Packs;

import java.util.ArrayList;

public interface PackIdInputListener {
    void onInput(String pie);
    void onScan(ArrayList<Packs> pie, ArrayList<Packs> controlPie);
    void afterInput(Packs p);
}
