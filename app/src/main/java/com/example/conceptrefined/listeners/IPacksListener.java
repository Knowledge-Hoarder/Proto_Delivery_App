package com.example.ProtoDeliveryApp.listeners;

import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.modelsportal.Runs;

import java.util.ArrayList;

public interface IPacksListener {
    void onGetPacks(ArrayList<Packs> pie,String orders);
    void onGetMasterPack(int i);
}
