package com.example.ProtoDeliveryApp.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.ProtoDeliveryApp.R;
import com.example.ProtoDeliveryApp.models.DeliveryDone;
import com.example.ProtoDeliveryApp.models.DetachedNote;
import com.example.ProtoDeliveryApp.models.Report;
import com.example.ProtoDeliveryApp.modelsportal.ImgB64;
import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.utils.ApiManager;
import com.example.ProtoDeliveryApp.utils.DataJsonParser;
import com.example.ProtoDeliveryApp.utils.LocalStorageManager;
import com.example.ProtoDeliveryApp.utils.ValidationsManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrackerService extends Service {

    private static final long CHECK_INTERVAL_Min = 5 * 60000; // Check every 5 min
    private static final String channelId = "LocationService";
    private static final int NOTIFICATION_ID = 6468;
    private final Handler handler = new Handler();

    @SuppressLint("WakelockTimeout")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//            handler.postDelayed(locationRunnable, 0);
        handler.postDelayed(connectionRunnable, 0); // Start the task immediately

        NotificationChannel chNot = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(chNot);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentText(getString(R.string.notification_text))
                .setContentTitle(getString(R.string.notification_title))
                .setSmallIcon(R.drawable.smalllogo)
                .build();
        startForeground(NOTIFICATION_ID, notification);
        return START_STICKY;
    }
//    private final Runnable locationRunnable = new Runnable() {
//        @SuppressLint("MissingPermission")
//        @Override
//        public void run() {
//            FusedLocationProviderClient providerClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
//            providerClient.getLastLocation().addOnSuccessListener(location -> {
//                if (location != null) {
//                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//                    try {
//                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                        JSONArray decodedAdresses= DataJsonParser.decodeAdresses(addresses);
//                        ApiManager.getInstance(getApplicationContext())
//                                .notifyLocation(getApplicationContext(),decodedAdresses);
//                        Log.w("TrackerService", addresses.toString());
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            });
//            handler.postDelayed(this, CHECK_INTERVAL_Min); // Repeat the task after the specified delay
//        }
//    };

    private final Runnable connectionRunnable = new Runnable() {
        @Override
        public void run() {
            Context context = getApplicationContext();
            LocalStorageManager localManager= new LocalStorageManager(context);

            ArrayList<DeliveryDone> toSync = localManager.getUnsyncEntries();
            boolean isConnected = ValidationsManager.isConnectionInternet(context);
            if (isConnected && !toSync.isEmpty()) {
                ApiManager.getInstance(context).syncMyDelDone(context,toSync);
            }

            ArrayList<Packs> sPacks = localManager.getUnsyncPacks();
            if (isConnected && !sPacks.isEmpty()) {
                for (Packs pack:sPacks) {
                    ApiManager.getInstance(context).syncMyPack(context,pack);
                }
            }

            ArrayList<ImgB64> sImgs = localManager.getUnsyncPics(context);
            if (isConnected && !sImgs.isEmpty()) {
                for (ImgB64 img:sImgs) {
                    ApiManager.getInstance(context).syncMyPics(context,img);
                }
            }

            ArrayList<DetachedNote> sNotes = localManager.getUnsyncDetachedNotes();
            if (isConnected && !sNotes.isEmpty()) {
                for (DetachedNote note:sNotes) {
                    ApiManager.getInstance(context).syncMyDetachedNotes(context,note);
                }
            }
            ArrayList<Report> sRep = localManager.getUnsyncReports();
            if (isConnected && !sRep.isEmpty()) {
                for (Report report:sRep) {
                    ApiManager.getInstance(context).syncMyReport(context,report);
                }
            }
            handler.postDelayed(this, CHECK_INTERVAL_Min); // Repeat the task after the specified delay
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        handler.removeCallbacks(locationRunnable);
        handler.removeCallbacks(connectionRunnable);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime(),
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }
}
