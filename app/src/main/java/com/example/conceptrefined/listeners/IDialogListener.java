package com.example.ProtoDeliveryApp.listeners;

import android.location.GnssAntennaInfo;

import com.example.ProtoDeliveryApp.modelsportal.Runs;

import java.time.LocalDate;
import java.util.ArrayList;

public interface IDialogListener {
    void onShowDialog(String packid, String mode);
    void onResult(String date, String runs);
    void onAddDriverNote(String note);
}
