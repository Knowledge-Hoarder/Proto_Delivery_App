package com.example.ProtoDeliveryApp.listeners;

import com.example.ProtoDeliveryApp.modelsportal.MasterPacks;
import com.example.ProtoDeliveryApp.modelsportal.OrderItems;
import com.example.ProtoDeliveryApp.modelsportal.Runs;

import java.util.ArrayList;

public interface IDriverRunsListener {
    void onUpdateRuns(ArrayList<Runs> list);
    void onLoadingRuns(Boolean bool);

//    void onUpdateMasterPacks(ArrayList<MasterPacks> list);
//    void onUpdateOrderItems(ArrayList<OrderItems> list);
}
