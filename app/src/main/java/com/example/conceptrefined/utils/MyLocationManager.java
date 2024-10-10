package com.example.ProtoDeliveryApp.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.ProtoDeliveryApp.listeners.ILocationListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyLocationManager {
    private static MyLocationManager instance = null;
    private static FusedLocationProviderClient providerClient;
    private ILocationListener locationListener;

    public void setLocationListener(ILocationListener locationListener){
        this.locationListener = locationListener;
    }

    public MyLocationManager(Context context) {
    }
    public static synchronized MyLocationManager getInstance(Context context) {
        if (instance == null) {
            instance = new MyLocationManager(context);
        }
        return instance;
    }

    public void getLastLocation(Context context, String dayTime, String signXml) {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                providerClient = LocationServices.getFusedLocationProviderClient(context);
            final List[] addresses = new List[]{null};
                providerClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location !=null){
                            Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
                            try {
                                addresses[0] = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                            } catch (IOException e) {
                                Log.e("GPSError", "getLastLocation: "+e.getMessage());
                            }
                        }
                        try {
                            if (locationListener != null){

                                locationListener.onLocationGetter(addresses[0],dayTime,signXml);
                            }
                        } catch (Exception e) {
                            Log.e("EXCEPtION", e.getMessage());
                        }
                    }
                });
        }
    }
}
