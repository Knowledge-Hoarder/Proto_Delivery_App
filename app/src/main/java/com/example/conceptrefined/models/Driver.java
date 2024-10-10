package com.example.ProtoDeliveryApp.models;

import android.util.Log;

import com.example.ProtoDeliveryApp.modelsportal.Runs;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Driver {
    int EmpId;
    String DriverName;

    public Driver(int empId, String driverName) {
        EmpId = empId;
        DriverName = driverName;
    }

    public int getEmpId() {
        return EmpId;
    }

    public void setEmpId(int empId) {
        EmpId = empId;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("name", getDriverName());
            object.put("empid", getEmpId());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}
