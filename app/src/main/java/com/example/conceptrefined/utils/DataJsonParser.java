package com.example.ProtoDeliveryApp.utils;

import android.location.Address;
import android.util.Log;

import com.example.ProtoDeliveryApp.models.DeliveryDone;
import com.example.ProtoDeliveryApp.models.Driver;
import com.example.ProtoDeliveryApp.models.Report;
import com.example.ProtoDeliveryApp.modelsportal.ImgB64;
import com.example.ProtoDeliveryApp.modelsportal.MasterPacks;
import com.example.ProtoDeliveryApp.modelsportal.OrderItems;
import com.example.ProtoDeliveryApp.modelsportal.Orders;
import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.modelsportal.Runs;
import com.example.ProtoDeliveryApp.modelsportal.SyncResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataJsonParser {

    public static JSONArray requestParserDeliveryDone(ArrayList<DeliveryDone> list) {
        int nItems = 0;
        JSONArray items = new JSONArray();
        while (nItems < list.size()){
            JSONObject object = new JSONObject();
            object = list.get(nItems).toJSON();
            items.put(object);
            nItems++;
        }

        return items;
    }

    public static ArrayList<Runs> availableRunsParser(JSONArray response) {
        ArrayList<Runs> pie = new ArrayList<>();
        try {
            for(int i =0; i<response.length();i++){

                JSONObject arSon = (JSONObject) response.get(i);
                String runid = arSon.getString("runid");
                String shipbydate = arSon.getString("shipbydate");
                Runs piece = new Runs(i, runid, shipbydate );
                pie.add(piece);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return pie;
    }

    public static ArrayList<Runs> driverRunsParser(JSONArray response) {
        ArrayList<Runs> pie = new ArrayList<>();
        try {
            for(int i =0; i<response.length();i++){
                JSONObject arSon = (JSONObject) response.get(i);
                String runid = arSon.getString("runid");
                String shipbydate = arSon.getString("shipbydate");
                ArrayList<Orders> orders= Orders.fromJSONArray(arSon.getString("orders"));
                Runs piece = new Runs(i, runid, shipbydate, orders );
                pie.add(piece);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return pie;
    }

    public static ArrayList<MasterPacks> masterpackParser(JSONArray response) {
        ArrayList<MasterPacks> pie = new ArrayList<>();
        try {
            for(int i =0; i<response.length();i++){
                JSONObject arSon = (JSONObject) response.get(i);
                int masterPackId = arSon.getInt("masterPackId");
                MasterPacks  piece = new MasterPacks(i,masterPackId+"", arSon.getJSONArray("packs"));
                pie.add(piece);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return pie;
    }
    public static ArrayList<Packs> packParser(JSONArray response) {
        ArrayList<Packs> pie = new ArrayList<>();
        try {
            for(int i =0; i<response.length();i++){
                JSONObject arSon = (JSONObject) response.get(i);
                String packid = arSon.getString("packid");
                String routeSeq = arSon.getString("route_seq");
                int orderLine = arSon.getInt("orderLine");
                String partDesc = arSon.getString("partDesc");
                int orderQty = arSon.getInt("orderQty");
                Packs  piece = new Packs(i,orderLine,packid,routeSeq,partDesc, orderQty);
                pie.add(piece);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return pie;
    }
    public static ArrayList<OrderItems> orderitemsParser(JSONArray response) {
        ArrayList<OrderItems> pie = new ArrayList<>();
        try {
            for(int i =0; i<response.length();i++){
                JSONObject arSon = (JSONObject) response.get(i);
                int orderLine = arSon.getInt("orderLine");
                String itemLenght = arSon.getString("itemLenght");
                int itemQty = arSon.getInt("itemQty");
                String itemDesc = arSon.getString("itemDesc");
                OrderItems piece = new OrderItems(i,orderLine,itemDesc,itemQty,itemLenght );
                pie.add(piece);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return pie;
    }
    public static ArrayList<SyncResult> syncResult(JSONArray response) {
        ArrayList<SyncResult> pie = new ArrayList<>();
        try {
            for(int i =0; i<response.length();i++){
                JSONObject arSon = (JSONObject) response.get(i);
                String id="";
                if(response.toString().contains("\"id\":")){
                    id = arSon.getString("id");
                }
                String orderNum = arSon.getString("orderNum");
                int status = arSon.getInt("status");
                String message = arSon.getString("message");
                SyncResult piece = new SyncResult(id,orderNum,status,message);
                pie.add(piece);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return pie;
    }

    public static JSONArray decodeAdresses(List<Address> addresses) {
        JSONArray decoded = new JSONArray();
        for(int i =0; i<addresses.size();i++){
            JSONObject object = new JSONObject();
            try {
                object.put("street", addresses.get(i).getSubThoroughfare()+" "+addresses.get(i).getThoroughfare());
                object.put("city", addresses.get(i).getLocality());
                object.put("state", addresses.get(i).getAdminArea());
                object.put("zip", addresses.get(i).getPostalCode());
                object.put("country", addresses.get(i).getCountryCode());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            decoded.put(object);
        }
        return decoded;
    }
    public static String parseRequestBody(String token, String value){
        return "{\"token\":\""+token+"\",\"value\":"+value+"}";
    }
    public static String parseRequestBodySpec(String dailyToken, String json, String objectDriv) {
        return "{\"token\":\""+dailyToken+"\",\"driverinfo\":"+objectDriv+",\"value\":"+json+"}";
    }
    public static JSONArray requestParserItemsToSubmit(List<Packs> list) {
        int nItems = 0;
        JSONArray items = new JSONArray();
        while (nItems < list.size()){
            JSONObject object = new JSONObject();
            object = list.get(nItems).toJSON();
            items.put(object);
            nItems++;
        }
        return items;
    }

    public static Object requestParserPacks(ArrayList<Packs> list) {
        int nItems = 0;
        JSONArray items = new JSONArray();
        while (nItems < list.size()){
            JSONObject object = new JSONObject();
            object = list.get(nItems).toJSON();
            items.put(object);
            nItems++;
        }
        return items;

    }

    public static Object requestParserImgB64(ArrayList<ImgB64> list) {
        int nItems = 0;
        JSONArray items = new JSONArray();
        while (nItems < list.size()){
            JSONObject object = new JSONObject();
            object = list.get(nItems).toJSON();
            items.put(object);
            nItems++;
        }
        return items;
    }

    public static String parseOrdersList(ArrayList<Orders> list) {
        int nItems = 0;
        JSONArray items = new JSONArray();
        while (nItems < list.size()){
            JSONObject object = new JSONObject();
            object = list.get(nItems).toJSON();
            items.put(object);
            nItems++;
        }
        return items.toString();

    }

    public static ArrayList<Orders> ordersFromJson(String sOrders) {
        ArrayList<Orders> orders = new ArrayList<>();
        JSONArray jsonArr = null;
        try {
            jsonArr = new JSONArray(sOrders);
        } catch (JSONException e) {
            Log.e("ordersFromJson", "JSONArray "+e.getMessage() );
        }
        try {
            for(int i = 0; i< (jsonArr != null ? jsonArr.length() : 0); i++){

                JSONObject arSon = (JSONObject) jsonArr.get(i);
                String orderNum = arSon.getString("ordernum");
                Orders piece = new Orders(i, orderNum);
                orders.add(piece);
            }
        }
        catch (JSONException e) {
            Log.e("ordersFromJson", e.getMessage() );
        }
        return  orders;
    }

    public static String parsePacksToSync(Runs run, Packs toSync) {
        JSONObject object = new JSONObject();
        try {
            object.put("packId", toSync.getPackId());
            object.put("notes", toSync.getNotes());
            object.put("orderNum", toSync.getOrdernum());
            object.put("orderLine", toSync.getOrderLine());
            object.put("hasMpack", toSync.getHasMPack());
            object.put("reqDate", run.getShipByDate());
            object.put("runId", run.getRunid());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return object.toString();
    }

    public static Driver driverFromJson(String response) {
        Driver driver = null;
        try{
            String json = response.replace("\\\"", "'");
            JSONObject son = new JSONObject(json);
            int empid = son.getInt("empid");
            String drivername= son.getString("name");
            driver = new Driver(empid,drivername);
        } catch (JSONException e) {
            Log.e("Driver", "fromJson: ", e);
        }
        return driver;
    }
    public static Packs packParserFromJson(String str){
        Packs packs = null;
        String json = str.replace("\\\"", "'");
        try{
            JSONObject son = new JSONObject(json);
            String packId= son.getString("packId");
            String notes= son.getString("notes");
            String orderNum= son.getString("orderNum");
            packs = new Packs(packId,notes,orderNum);
        } catch (JSONException e) {
            Log.e("Driver", "fromJson: ", e);
        }
        return packs;
    }
//    public static ArrayList<ListSubmitResponse> listSubmitParser(String response){
//        ArrayList<ListSubmitResponse> pie = new ArrayList<>();
//        try {
//            JSONArray arSon = new JSONArray(response.replace("ï»¿",""));
//            for(int i =0; i<arSon.length();i++){
//                JSONObject oSon = (JSONObject) arSon.get(i);
//                String packId = oSon.getString("packId");
//                int status = 200;
//                if (!oSon.isNull("status")){
//                    status = oSon.getInt("status");
//                }
//                String message = "Sent";
//                if (!oSon.isNull("message")){
//                    message = oSon.getString("message");
//                }
//
//                ListSubmitResponse piece = new ListSubmitResponse(i,packId,status,message);
//                pie.add(piece);
//            }
//        }
//        catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return pie;
//    }
//

//    public static ArrayList<Sentinel> parserSentinelMain(JSONArray response){
//        ArrayList<Sentinel> api = new ArrayList<>();
//        for (int i = 0; i < response.length(); i++) {
//
//        }
//        return api;
//    }
//
//    public static ArrayList<Sentinel> parserSentinelLoads(JSONArray response){
//        ArrayList<Sentinel> api = new ArrayList<>();
//        for (int i = 0; i < response.length(); i++) {
//
//        }
//        return api;
//    }
//
//    public static ArrayList<Sentinel> parserSentinelDeliveries(JSONArray response){
//        ArrayList<Sentinel> api = new ArrayList<>();
//        try {
//            for (int i = 0; i < response.length(); i++) {
//                JSONObject son = (JSONObject) response.get(i);
//                String run = son.getString("run");
//                String departure = son.getString("departure");
//
//                Sentinel sentinel = new Sentinel(run,departure);
//                api.add(sentinel);
//            }
//        }catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        return api;
//    }
}