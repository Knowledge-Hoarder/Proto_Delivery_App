package com.example.ProtoDeliveryApp.listeners;

import com.google.mlkit.vision.barcode.common.Barcode;

public interface IBarcodeScanner {
    void onCodeScanned(Barcode barcode);

}
