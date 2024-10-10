package com.example.ProtoDeliveryApp.listeners;

import com.example.ProtoDeliveryApp.models.ListSubmitResponse;

import java.util.ArrayList;

public interface ListSubmitListener {
    void onResponse(ArrayList<ListSubmitResponse> response);
}
