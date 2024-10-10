package com.example.ProtoDeliveryApp.utils;

import android.content.Context;

import com.example.ProtoDeliveryApp.models.Clients;
import com.example.ProtoDeliveryApp.models.DeliveryDone;
import com.example.ProtoDeliveryApp.models.DetachedNote;
import com.example.ProtoDeliveryApp.models.LocalDbHelper;
import com.example.ProtoDeliveryApp.models.Report;
import com.example.ProtoDeliveryApp.modelsportal.ImgB64;
import com.example.ProtoDeliveryApp.modelsportal.OrderItems;
import com.example.ProtoDeliveryApp.modelsportal.Orders;
import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.modelsportal.Runs;
import com.example.ProtoDeliveryApp.modelsportal.SyncResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LocalStorageManager {
    private static LocalStorageManager instance = null;
    private final LocalDbHelper helper;

    //singleton constructor
    public static synchronized LocalStorageManager getInstance(Context context) {
        if (instance == null) {
            instance = new LocalStorageManager(context);
        }
        return instance;
    }
    public LocalStorageManager(Context context) {
        helper = new LocalDbHelper(context);
    }

    public Boolean addDelivery(ArrayList<Orders> orders,String date, String sign, String address, int isSync) {
        boolean checker = false;
        for (Orders order:
             orders) {
            DeliveryDone del = new DeliveryDone(0,order.getOrdernum(), date, sign,address,isSync);
            checker = helper.addDelivery(del);
        }
        return checker;
    }

    public void addDetachedNote(int custNum, String note){
        helper.addDetachedNote(custNum, note);
    }
    public List<Packs> getPacksPerOrder(ArrayList<Orders> orders, int cmd) {
        List<Packs> packs = new ArrayList<>();
        for (Orders order:
             orders) {
            packs.addAll(helper.getPacksPerOrder(order.getOrdernum(), cmd));
        }
        return packs;
    }
    public ArrayList<DetachedNote> getUnsyncDetachedNotes() {
        ArrayList<DetachedNote> unsyncItems;
        unsyncItems = helper.getUnsyncDetachedNotes();
        return unsyncItems;
    }
    public ArrayList<DeliveryDone>getUnsyncEntries(){
        ArrayList<DeliveryDone> unsyncItems;
        unsyncItems = helper.getUnsyncEntries();
        return unsyncItems;
    }
    public ArrayList<Packs>getUnsyncPacks(){
        ArrayList<Packs> unsyncItems;
        unsyncItems = helper.getUnsyncPacks();
        return unsyncItems;
    }
    public ArrayList<ImgB64>getUnsyncPics(Context context){
        ArrayList<ImgB64> unsyncItems;
        unsyncItems = helper.getUnsyncPics(context);
        return unsyncItems;
    }
    public int orderCount(int id) {
        int orderCount;
        orderCount =  helper.getClients(id,0).size();
        return orderCount;
    }
    public ArrayList<Clients> showOrders(int fkRun,int flip){
        return helper.getClients(fkRun,flip);
    }

    public boolean addRuns(ArrayList<Runs> listRuns) {
        boolean bool = true;
        for (Runs run:
             listRuns) {
           if ( !helper.addRun(run)){
               return false;
           }
        }
        return bool;
    }
    public Boolean addExtraPack(Packs p){
        return helper.addExtraPack(p);
    }
    public ArrayList<Packs> getExtraPacks(){
        return helper.getExtraPacks();
    }
    public ArrayList<Runs> getRuns(String currDate) {

        return helper.loadActiveRun(currDate);
    }

    public ArrayList<OrderItems> getOrderItems(String orderNum) {
        return helper.getOrderItems(orderNum);
    }
    public OrderItems getOrderItem(String fkrun) {
        return helper.getOrderItem(fkrun);
    }

    public void updateSyncEntries(ArrayList<SyncResult> pie) {
        for (SyncResult piece:
             pie) {
            if(Objects.equals(piece.getMessage(), "success")){
                helper.updateDeliverySync(piece);
            }
        }
    }
    public void updateDetachedNoteSync(ArrayList<SyncResult> pie){
        for (SyncResult piece:
                pie) {
            if(Objects.equals(piece.getMessage(), "success")){
                helper.updateDetachedNote(Integer.parseInt(piece.getOrderNum()));
            }
        }
    }

    public void updateSyncPacks(ArrayList<SyncResult> pie) {
        for (SyncResult piece:
                pie) {
            if(Objects.equals(piece.getMessage(), "success")){
                helper.updateSyncPacks(piece);
            }
        }
    }

    public void updateSyncPics(ArrayList<SyncResult> pie) {
        for (SyncResult piece:
                pie) {
            if(Objects.equals(piece.getMessage(), "success")){
                helper.updateSyncPics(piece);
            }
        }
    }
    public ArrayList<Packs> saveScannedPacks(ArrayList<Packs> items) {
        ArrayList<Packs> controlGroup = new ArrayList<>();
        ArrayList<Orders> orders = new ArrayList<>();
        int i=0;
        for (Packs pack:
             items) {
            int check = helper.isPackOrMPack(pack.getPackId());
            switch (check){
                case 0://is master pack
                    ArrayList<Packs> hasMasterPack = helper.getPacks(pack.getPackId());
                    controlGroup.addAll(hasMasterPack);
                    break;
                case 1://is pack
                    boolean controlVar =  helper.addScannedPack(pack);
                    if(!controlVar){
                        controlGroup.add(pack);
                    }
                    break;
            }
            Packs hPack = helper.getFullPack(pack.getPackId());
            orders.add(new Orders(i,hPack.getOrdernum()));
            i++;
        }
        for (Orders order:
                orders) {
            if (helper.evaluateOrderScan(order.getOrdernum())) {
                helper.updateOrderCompleteness(order.getOrdernum());
            }
        }
        return controlGroup;
    }

    public boolean checkDeliveryStatus(String ordernum) {
        return helper.checkDeliveryStatus(ordernum);
    }
    public Packs getPack(String packId) {
        return helper.getPack(packId);
    }
    public boolean checkMasterPackScanStatus(String packId, String orderNum) {
        return helper.checkMasterPackScanStatus(packId, orderNum);
    }

    public boolean addNoteToPack(String id, String note) {
        return helper.addNoteToPack(id,note);
    }
    public boolean addDriverNote(String id, String note) {
        return helper.addDriverNote(id,note);
    }

    public void addImages(String orderNum, ArrayList<String> imgsToWrite) {
        for (String img : imgsToWrite) {
            helper.addPics(orderNum, img);
        }
    }

    public void addOrderItems(ArrayList<Packs> pie, String orderNum) {
        for (Packs piece:
             pie) {
            if(helper.checkOrderItems(piece, orderNum)) {
                helper.addOrderItems(piece, orderNum);
            }
        }

    }

    public void addPack(Packs pie, String orders, String masterpackNum) {
        if(helper.checkForPack(pie,orders, masterpackNum)){
            helper.addSoloPack(pie,orders, masterpackNum);
        }
        helper.addOrderItems(pie, orders);
    }

    public boolean checkOwnership(ArrayList<Orders> orders, String response) {
        boolean checker = false;
        for (Orders order : orders){
            checker = helper.checkOwnership(order.getOrdernum(),response);
            if (checker){
                return checker;
            }
        }
        return checker;
    }

    public ArrayList<Orders> getOrders(int run, String cltName,int flip) {
        return helper.getOrdersPerClt(run,cltName, flip);
    }
    public ArrayList<Orders> getOrdersPerCltDone(int run, String cltName) {
        return helper.getOrdersPerCltDone(run,cltName);
    }

    public Runs getRun(String ordernum) {
        int order = helper.getOrder(ordernum);
        return helper.getRun(order);
    }

    public Clients getClientWNum(int custNum) {
        return helper.getClient(custNum);
    }


    public void updateOrdersCompleteness(ArrayList<Orders> orders) {
        helper.updateOrdersCompleteness(orders);
    }

    public Boolean addReport(Packs p, ArrayList<String> imageUris,String note) {
        Boolean bool =false;
        for (String img : imageUris) {
            helper.addPicsForIssue(p.getOrdernum(), img, p.getPackId());
        }
        bool = helper.addIssueReport(p,note);
        return bool;
    }

    public ArrayList<Report> getUnsyncReports() {
        return helper.getUnsyncReports();
    }

    public void updateSyncReport(ArrayList<SyncResult> pie) {
        for (SyncResult piece:
             pie) {
            helper.updateSyncReport(piece);
        }
    }

    public boolean hideRun(Runs item) {
        return helper.hideRun(item);
    }
}