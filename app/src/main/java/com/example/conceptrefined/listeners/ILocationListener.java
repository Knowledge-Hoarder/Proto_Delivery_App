package com.example.ProtoDeliveryApp.listeners;


import android.location.Address;

import java.util.List;

public interface ILocationListener {
    void onLocationGetter(List<Address> addresses, String dayTime, String signXml);
}
