package com.example.ProtoDeliveryApp.camera;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.ProtoDeliveryApp.listeners.IBarcodeScanner;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class ScannerUtil {
    private static ScannerUtil instance = null;
    private static IBarcodeScanner scannerListener;

    public ScannerUtil(Context context) {
    }

    public static synchronized ScannerUtil getInstance(Context context) {
        if (instance == null){
            instance = new ScannerUtil(context);
        }
        return instance;
    }
    public void setScannerlistener(IBarcodeScanner scannerListener){
        this.scannerListener = scannerListener;
    }

   public static void scanThis(InputImage image){
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .enableAllPotentialBarcodes() // Optional
                        .build();
       com.google.mlkit.vision.barcode.BarcodeScanner scanner = BarcodeScanning.getClient(options);
       Task<List<Barcode>> scannedCodes = scanner.process( image)
               .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                   @Override
                   public void onSuccess(List<Barcode> barcodes) {
                       for (Barcode barcode: barcodes) {
                           if (scannerListener != null){
                               scannerListener.onCodeScanned(barcode);
                           }
                       }
                   }

               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       // Task failed with an exception
                       // ...
                   }
               });
    }

}
