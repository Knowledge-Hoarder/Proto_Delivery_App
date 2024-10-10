package com.example.ProtoDeliveryApp.utils;

import android.os.Environment;
import android.provider.MediaStore;

import com.example.ProtoDeliveryApp.modelsportal.Orders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PicIndex {
    public static String StorageFolder(String orderNum) {
        File file = new File(Environment.getExternalStorageDirectory(),"Pictures/"+orderNum);
        if(!file.exists()){
            file.mkdir();
        }
        return file.getPath();
    }

    public static List<String> getAllPics(String orderNum) {
        List<String> allpics = new ArrayList<>();
            File folder = new File(StorageFolder(orderNum));
            File[] fList = folder.listFiles();

            if(fList!=null){
                for (File file : fList) {
                    if (file.isFile()) {
                        try {
                            allpics.add(file.getCanonicalPath());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        }

        return allpics;
    }
}
