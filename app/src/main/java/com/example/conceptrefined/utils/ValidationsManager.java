package com.example.ProtoDeliveryApp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.services.TrackerService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ValidationsManager {

    public static String sharedName(){
        return "UserInfo";
    }
    public static String[] permissions = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Manifest.permission.WAKE_LOCK
    };

    public static boolean isServiceRunning(ActivityManager activity) {
        //noinspection deprecation
        for (ActivityManager.RunningServiceInfo serviceInfo:
                activity.getRunningServices(Integer.MAX_VALUE)){
            if (TrackerService.class.getName().equals(serviceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }
    public static boolean isConnectionInternet(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    public static void askPermissions(Activity activity, String[] permissionNeeded ,int REQUEST_CODE){
        ActivityCompat.requestPermissions(activity, permissionNeeded, REQUEST_CODE);
    }

    public static String getMyToken() {
        String s = LocalDate.now(ZoneId.of("US/Eastern")).toString();
        return Integer.toHexString(Integer.parseInt(s.replace("-","")));
    }
    public static List<Packs> RemoveDuplicates(ArrayList<Packs> toSync) {
        Map<String, Packs> cleanMap = new LinkedHashMap<String, Packs>();
        for (int i = 0; i < toSync.size(); i++) {
            cleanMap.put(String.valueOf(toSync.get(i).getPackId()), toSync.get(i));
        }
        List<Packs> list = new ArrayList<Packs>(cleanMap.values());
        return list;
    }

    public static String getB(){
        String c = String.format("%s:%s","USERNAME","PASSWORD");
        return "Basic " + Base64.encodeToString(c.getBytes(), Base64.DEFAULT);

    }
    public static List<String> convertImgStringToList(String input) {
        List<String> pathList = new ArrayList<>();

        // Check if the input is not null or empty
        if (input != null && !input.isEmpty()) {
            // Remove brackets and whitespace
            String cleanedInput = input.replaceAll("[\\[\\]\\s]", "");

            // Split the string by comma and optional space
            String[] paths = cleanedInput.split(",\\s*");

            pathList.addAll(Arrays.asList(paths));
        }

        return pathList;
    }

    public static String convertBase64(Context context, String lastFileTaken){
        Uri imageUri = Uri.fromFile(new File(lastFileTaken));
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            Log.e("ImageToBase64Converter", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.e("ImageToBase64Converter", "Error reading image: " + e.getMessage());
        }
        return null;
    }
}