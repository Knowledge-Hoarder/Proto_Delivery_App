package com.example.ProtoDeliveryApp.listeners;

import android.content.Context;

import com.example.ProtoDeliveryApp.models.Driver;

public interface ILoginListener {
    void onValidateLogin(Context context, Driver driver);
}
