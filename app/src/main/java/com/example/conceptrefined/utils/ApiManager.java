package com.example.ProtoDeliveryApp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ProtoDeliveryApp.listeners.IDialogListener;
import com.example.ProtoDeliveryApp.listeners.IDriverRunsListener;
import com.example.ProtoDeliveryApp.listeners.IHideRun;
import com.example.ProtoDeliveryApp.listeners.ILoginListener;
import com.example.ProtoDeliveryApp.listeners.IOrderItemsListener;
import com.example.ProtoDeliveryApp.listeners.IPacksListener;
import com.example.ProtoDeliveryApp.listeners.ITestConnectionListener;
import com.example.ProtoDeliveryApp.listeners.ListSubmitListener;
import com.example.ProtoDeliveryApp.listeners.PackIdInputListener;
import com.example.ProtoDeliveryApp.models.DeliveryDone;
import com.example.ProtoDeliveryApp.models.DetachedNote;
import com.example.ProtoDeliveryApp.models.Driver;
import com.example.ProtoDeliveryApp.models.LocalDbHelper;
import com.example.ProtoDeliveryApp.models.Report;
import com.example.ProtoDeliveryApp.modelsportal.ImgB64;
import com.example.ProtoDeliveryApp.modelsportal.OrderItems;
import com.example.ProtoDeliveryApp.modelsportal.Orders;
import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.modelsportal.Runs;
import com.example.ProtoDeliveryApp.modelsportal.SyncResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;

public class ApiManager {
    private static RequestQueue volleyQueue = null;
    private static ApiManager instance = null;
    private static LocalStorageManager manager = null;
//region listeners

    private ILoginListener loginListener;
    public void setLoginListener(ILoginListener loginListener) {
        this.loginListener = loginListener;
    }

    private ITestConnectionListener connectionListener;

    public void setConnectionListener(ITestConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    private ListSubmitListener listSubmitListener;

    public void setListSubmitListener(ListSubmitListener listSubmitListener) {
        this.listSubmitListener = listSubmitListener;
    }

    private PackIdInputListener packIdInputListener;

    public void setPackIdInputListener(PackIdInputListener packIdInputListener) {
        this.packIdInputListener = packIdInputListener;
    }

    private IDriverRunsListener driverRunsListener;

    public void setDriverRunsListener(IDriverRunsListener driverRunsListener) {
        this.driverRunsListener = driverRunsListener;
    }

    private IOrderItemsListener orderItemsListener;
    public void setOIListener(IOrderItemsListener orderItemsListener) {
        this.orderItemsListener = orderItemsListener;
    }

    private IPacksListener packsListener;
    public void setDbPacks(IPacksListener packsListener) {
        this.packsListener = packsListener;
    }
    private IDialogListener dialogListener;

    public void setDialogListener(IDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }
    private IHideRun hideRun;

    public void setHideRunListener(IHideRun hideRun) {
        this.hideRun = hideRun;
    }
    //endregion
//region request strings
    private static final String azureHomeUrl = "https://driversapp.azurewebsites.net/";
//    private static final String azureHomeUrl = "https://localhost/";
    private static final String action_getDeliveries = "deliveries/get";//done
    private static final String action_postDeliveries = "deliveries/post";//done
    private static final String action_getLogin = "get_driver/";
    private static final String action_location = "sub/location";
    private static final String action_requestruns = "getrunperdriver/";//done
    private static final String action_requestavailableruns = "availableruns/";//done
    private static final String action_requestOrderItems = "app_get_order_items/";//done
    private static final String action_requestPacks = "get_packs/";//done
    private static final String action_requestCheckMasterPack = "check_masterpacks/";//done
    private static final String action_requestMasterPacks = "get_masterpacks/";//done

    //endregion
    public ApiManager(Context context) {
    }

    public static synchronized ApiManager getInstance(Context context) {
        if (instance == null) {
            instance = new ApiManager(context);
            volleyQueue = Volley.newRequestQueue(context);
            manager = new LocalStorageManager(context);
        }
        return instance;
    }
    public void onNewInput(String string) {
        if (packIdInputListener != null) {
            packIdInputListener.onInput(string);
        }
    }
    public void onShowDialog(String packid, String mode) {
        if (dialogListener != null) {
            dialogListener.onShowDialog(packid, mode);
        }
    }
    public void onAddDriverNote(String note) {
        if (dialogListener != null) {
            dialogListener.onAddDriverNote(note);
        }
    }

    public void afterInput(Packs p) {
        if (packIdInputListener != null) {
            packIdInputListener.afterInput(p);
        }
    }
    public void loginDriver(Context context, String number) {
        String dailyToken = ValidationsManager.getMyToken();
        StringRequest req = new StringRequest(Request.Method.GET,
                String.format("%s%s%s", azureHomeUrl, action_getLogin, dailyToken+"&"+number),
                response -> {
                    Driver driver = DataJsonParser.driverFromJson(response);
            if (loginListener != null) {
                loginListener.onValidateLogin(context, driver);
            }
        }, error ->{
            Driver driver = new Driver(1,"");
            if (loginListener != null) {
                loginListener.onValidateLogin(context, driver);
            }
            Log.e("loginDriver", "onErrorResponse: " + error.getMessage());
        });
        volleyQueue.add(req);
    }

    public void requestrunsApi(Context context, int driverId, LocalDate date, String runid) {
        driverRunsListener.onLoadingRuns(true);
        if (ValidationsManager.isConnectionInternet(context)) {
            String dailyToken = ValidationsManager.getMyToken();
            String url = azureHomeUrl + action_requestruns +dailyToken +"&"+ runid+"&"+date;
            JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url,
                    null, response -> {
                ArrayList<Runs> pie = DataJsonParser.driverRunsParser(response);
                if (driverRunsListener != null) {
                    driverRunsListener.onUpdateRuns(pie);
                }
            }, error -> {
                Log.e("requestrunsApi", "onErrorResponse: " + error.getMessage());
                driverRunsListener.onLoadingRuns(false);
            });
            req.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 60000;
                }
                @Override
                public int getCurrentRetryCount() {
                    return 4;
                }
                @Override
                public void retry(VolleyError error) {
                    Log.e("VolleyTimeout", "retry: ");
                }
            });
            volleyQueue.add(req);
        }
    }
    public void requestAvailableRuns(Context context, String date, int empId) {
        if (ValidationsManager.isConnectionInternet(context)){
            String dailyToken = ValidationsManager.getMyToken();
            String url = azureHomeUrl + action_requestavailableruns +dailyToken +"&"+date+"&"+empId;
            JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url,
                    null, response -> {
//                ArrayList<Runs> pie = DataJsonParser.driverRunsParser(response);
                if (dialogListener != null) {
                    dialogListener.onResult(date,response.toString());
                }
            }, error -> {
                driverRunsListener.onLoadingRuns(false);
                Log.e("requestrunsApi", "onErrorResponse: " + error.getMessage());
            });
            req.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 60000;
                }
                @Override
                public int getCurrentRetryCount() {
                    return 4;
                }
                @Override
                public void retry(VolleyError error) {
                    Log.e("VolleyTimeout", "retry: ");
                }
            });
            volleyQueue.add(req);
        }
    }
//    public void requestOrderItemsApi(Context context, String orders, int driverId) {
//        if (ValidationsManager.isConnectionInternet(context)) {
//            String dailyToken = ValidationsManager.getMyToken();
//            String url = azureHomeUrl + action_requestOrderItems + dailyToken +"&"+ orders + "&" + driverId;
//            JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url,
//                    null, response -> {
//                ArrayList<OrderItems> pie = DataJsonParser.orderitemsParser(response);
//                manager.addOrderItems(pie, orders);
//            }, error -> Log.e("requestrunsApi", "onErrorResponse: " + error.getMessage()));
//            req.setRetryPolicy(new RetryPolicy() {
//                @Override
//                public int getCurrentTimeout() {
//                    return 60000;
//                }
//                @Override
//                public int getCurrentRetryCount() {
//                    return 4;
//                }
//                @Override
//                public void retry(VolleyError error) {
//                    Log.e("VolleyTimeout", "retry: ");
//                }
//            });
//            volleyQueue.add(req);
//        }
//    }

    public void requestPacksApi(Context context, String orders) {
        if (ValidationsManager.isConnectionInternet(context)) {
            String dailyToken = ValidationsManager.getMyToken();
            String url = azureHomeUrl + action_requestPacks +dailyToken +"&"+ orders;
            JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url,
                    null, response -> {
                ArrayList<Packs> pie = DataJsonParser.packParser(response);
                if (packsListener!= null){
                    packsListener.onGetPacks(pie, orders);
                }

            }, error -> {
                driverRunsListener.onLoadingRuns(false);
                Log.e("requestrunsApi", "onErrorResponse: " + error.getMessage());
            });
            req.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 60000;
                }
                @Override
                public int getCurrentRetryCount() {
                    return 4;
                }
                @Override
                public void retry(VolleyError error) {
                    Log.e("VolleyTimeout", "requestPacksApi");
                }
            });
            volleyQueue.add(req);
        }
    }

    public void checkforMasterPack(Packs pie, String ordernum) {
        String dailyToken = ValidationsManager.getMyToken();
        String url = azureHomeUrl + action_requestCheckMasterPack +dailyToken +"&"+ pie.getPackId();
        StringRequest req = new StringRequest(Request.Method.GET, url,
                response -> {
                    manager.addPack(pie, ordernum, response);
                    if (packsListener!= null){
                        packsListener.onGetMasterPack(1);
                    }
                },
                error -> {
                    if (error.networkResponse.statusCode == 500) {
                        manager.addPack(pie, ordernum, "");
                        if (packsListener!= null){
                            packsListener.onGetMasterPack(1);
                        }
                    } else {
                        driverRunsListener.onLoadingRuns(false);
                        Log.e("checkforMasterPack", "onErrorResponse: " + error.getMessage());
                    }
                });
        req.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 60000;
            }
            @Override
            public int getCurrentRetryCount() {
                return 4;
            }
            @Override
            public void retry(VolleyError error) {
                Log.e("VolleyTimeout", "checkforMasterPack");
            }
        });
        volleyQueue.add(req);
    }

    public void trstOrderLines(int id, String ctlName, String address1, int orderStatus) {
        if (orderItemsListener != null) {
            orderItemsListener.onDetailBtnPressed(id, ctlName,address1,orderStatus);
        }
    }

    public void trstSign(ArrayList<Orders> orders, int customernum) {
        if (orderItemsListener != null) {
            orderItemsListener.onDeliverBtnPressed(orders, customernum);
        }
    }
    public void hideRun(Runs item,Context context) {
        boolean bool = false;
        bool = LocalStorageManager.getInstance(context).hideRun(item);
        if (hideRun != null) {
            hideRun.onHideRun(bool);
        }
    }

    public void notifyLocation(Context context, JSONArray decodedAdresses) {
        if (ValidationsManager.isConnectionInternet(context)) {
            String dailyToken = ValidationsManager.getMyToken();
            StringRequest req = new StringRequest(Request.Method.POST, azureHomeUrl + action_location,
                    response -> {

                    }, error -> Log.e("notifyLocation", "onErrorResponse: " + error.getMessage())) {
                @Override
                public byte[] getBody() {
                    byte[] body;
                    String jSon = DataJsonParser.parseRequestBody(dailyToken, decodedAdresses.toString());
                    body = jSon.getBytes(StandardCharsets.UTF_8);
                    return body;
                }
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            volleyQueue.add(req);
        }
    }

    public void syncMyDelDone(Context context, ArrayList<DeliveryDone> toSync) {
        String name = ValidationsManager.sharedName();
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        int empid = Integer.parseInt(sharedPreferences.getString("empId",""));
        String nameD = sharedPreferences.getString("driverName","");
        Driver driver = new Driver(empid,nameD);

        String objectDriv = driver.toJSON().toString();
        if (ValidationsManager.isConnectionInternet(context)) {
            String json = DataJsonParser.requestParserDeliveryDone(toSync).toString();
            String dailyToken = ValidationsManager.getMyToken();
            JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST,
                    azureHomeUrl + action_postDeliveries,
                    null, response -> {
                ArrayList<SyncResult> pie = DataJsonParser.syncResult(response);
                manager.updateSyncEntries(pie);
                Log.w("syncMyDelDone", "Sent to base");
            }, error -> Log.e("syncMyDelDone", String.valueOf(error))) {
                @Override
                public byte[] getBody() {
                    byte[] body;
                    String jSon = DataJsonParser.parseRequestBodySpec(dailyToken, json,objectDriv);
                    body = jSon.getBytes(StandardCharsets.UTF_8);
                    return body;
                }
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            req.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 60000;
                }
                @Override
                public int getCurrentRetryCount() {
                    return 4;
                }
                @Override
                public void retry(VolleyError error) {
                    Log.e("VolleyTimeout", "retry: syncMyDelDone");
                }
            });
            volleyQueue.add(req);
        }
    }

    public void syncMyPack(Context context, Packs toSync) {
        if (ValidationsManager.isConnectionInternet(context)) {
            Runs run = manager.getRun(toSync.getOrdernum());
//            String json = toSync.toJSON().toString();
            String json = DataJsonParser.parsePacksToSync(run,toSync);

//            String json = DataJsonParser.sendSyncPacks(runId,reqDate,toSync);
            String dailyToken = ValidationsManager.getMyToken();
            JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST,
                    azureHomeUrl + action_postDeliveries + "pack",
                    null, response -> {
                ArrayList<SyncResult> pie = DataJsonParser.syncResult(response);
                manager.updateSyncPacks(pie);
                Log.w("syncMyPacks", "Sent to base");
            }, error -> Log.e("syncMyPacks", String.valueOf(error))) {
                @Override
                public byte[] getBody() {
                    byte[] body;
                    String jSon = DataJsonParser.parseRequestBody(dailyToken, json);
                    body = jSon.getBytes(StandardCharsets.UTF_8);
                    return body;
                }
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            req.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 60000;
                }
                @Override
                public int getCurrentRetryCount() {
                    return 4;
                }
                @Override
                public void retry(VolleyError error) {
                    Log.e("VolleyTimeout", "retry: syncMyPack");
                }
            });
            volleyQueue.add(req);
        }
    }

    public void syncMyPics(Context context, ImgB64 toSync) {
        if (ValidationsManager.isConnectionInternet(context)) {
            String json = toSync.toJSON().toString();
            String dailyToken = ValidationsManager.getMyToken();
            JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST,
                    azureHomeUrl + action_postDeliveries + "pic",
                    null, response -> {
                ArrayList<SyncResult> pie = DataJsonParser.syncResult(response);
                manager.updateSyncPics(pie);
                Log.w("syncMyPics", "Sent to base");
            }, error -> Log.e("syncMyPics", String.valueOf(error))) {
                @Override
                public byte[] getBody() {
                    byte[] body;
                    String jSon = DataJsonParser.parseRequestBody(dailyToken, json);
                    body = jSon.getBytes(StandardCharsets.UTF_8);
                    return body;
                }
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            req.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 60000;
                }
                @Override
                public int getCurrentRetryCount() {
                    return 4;
                }
                @Override
                public void retry(VolleyError error) {
                    Log.e("VolleyTimeout", "retry: syncMyPics");
                }
            });
            volleyQueue.add(req);
        }
    }
    public void syncMyDetachedNotes(Context context, DetachedNote note) {
        if (ValidationsManager.isConnectionInternet(context)) {
            String json = note.toJSON().toString();
            String dailyToken = ValidationsManager.getMyToken();
            JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST,
                    azureHomeUrl + action_postDeliveries + "detachednotes",
                    null, response -> {
                ArrayList<SyncResult> pie = DataJsonParser.syncResult(response);
                manager.updateDetachedNoteSync(pie);
                Log.w("syncMyDetachedNotes", "Sent to base");
            }, error -> Log.e("syncMyDetachedNotes", String.valueOf(error))) {
                @Override
                public byte[] getBody() {
                    byte[] body;
                    String jSon = DataJsonParser.parseRequestBody(dailyToken, json);
                    body = jSon.getBytes(StandardCharsets.UTF_8);
                    return body;
                }
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            req.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 60000;
                }
                @Override
                public int getCurrentRetryCount() {
                    return 4;
                }
                @Override
                public void retry(VolleyError error) {
                    Log.e("VolleyTimeout", "retry: syncMyDetachedNotes");
                }
            });
            volleyQueue.add(req);
        }
    }

    public void syncMyReport(Context context, Report report) {
        if (ValidationsManager.isConnectionInternet(context)) {
            String json = report.toJSON().toString();
            String dailyToken = ValidationsManager.getMyToken();
            JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST,
                    azureHomeUrl + action_postDeliveries + "report",
                    null, response -> {
                ArrayList<SyncResult> pie = DataJsonParser.syncResult(response);
                manager.updateSyncReport(pie);
                Log.w("syncMyReport", "Sent to base");
            }, error -> Log.e("syncMyReport", String.valueOf(error))) {
                @Override
                public byte[] getBody() {
                    byte[] body;
                    String jSon = DataJsonParser.parseRequestBody(dailyToken, json);
                    body = jSon.getBytes(StandardCharsets.UTF_8);
                    return body;
                }
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            req.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 60000;
                }
                @Override
                public int getCurrentRetryCount() {
                    return 4;
                }
                @Override
                public void retry(VolleyError error) {
                    Log.e("VolleyTimeout", "retry: syncMyDetachedNotes");
                }
            });
            volleyQueue.add(req);
        }
    }
    public void saveScanedPacks(ArrayList<Packs> items) {
        ArrayList<Packs> controlGroup = new ArrayList<>();
        if (!items.isEmpty()) {
            controlGroup = manager.saveScannedPacks(items);
        }
        if (packIdInputListener != null) {
            packIdInputListener.onScan(items, controlGroup);
        }
    }

    public boolean checkDeliveryStatus(ArrayList<Orders> orders) {
        boolean checker = true;
        int i = 0;
        while(i<orders.size()){
            for (Orders order:
                    orders) {
                checker = manager.checkDeliveryStatus(order.getOrdernum());
                if (!checker){
                    break;
                }
            }
            i++;
        }
        return checker;
    }



}
