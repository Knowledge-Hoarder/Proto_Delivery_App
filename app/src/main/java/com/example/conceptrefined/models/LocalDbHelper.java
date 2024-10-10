package com.example.ProtoDeliveryApp.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.ProtoDeliveryApp.modelsportal.ImgB64;
import com.example.ProtoDeliveryApp.modelsportal.OrderItems;
import com.example.ProtoDeliveryApp.modelsportal.Orders;
import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.modelsportal.Runs;
import com.example.ProtoDeliveryApp.modelsportal.SyncResult;
import com.example.ProtoDeliveryApp.utils.ValidationsManager;

import java.util.ArrayList;
import java.util.List;

public class LocalDbHelper extends SQLiteOpenHelper {

//region DB variables
    private final SQLiteDatabase db;
    private static final String DB_NAME = "bd_DriversLittleHelper";
    private static final int DB_VERSION = 11;
    //Table Names
    private static  final String tableDeliveries = "Deliveries";
    private static  final String tableDetachedNotes = "DetachedNotes";
    private static  final String tableIssuesReport = "IssuesReport";

    private static final String tableRuns="Runs";
    private static final String tableOrders="Orders",fkRunsId="fkRunsId";
    private static final String tableMasterPacks="MasterPacks",fkOMP="fkOrdersIdMP";
    private static final String tableOrderItems="OrderItems",fkOOI="fkOrdersIdItems";
    private static final String tablePacks="Packs";
    private static final String tablePics="Pics";
    //Field Names
    private static final String idDelivery="id", dateDelivered = "date", signature = "signature",
            address = "address",imageB64 = "imageB64",isSync = "isSync", packId = "packId",
            address1="address1",address2="address2",address3="address3",zip="zip",state="state",
            country="country",id="id", runid="runid", shipbydate="shipbydate", orderNum="ordernum",
            shipToName="shipToName",city="city",itemLenght="itemLenght", orderComplete="complete",
            itemDesc="itemDesc",itemQty="itemQty",itemOI="orderLine", driverNote="driverNote",
            packNotes="packnotes", isPackScanned="isScanned", hasMPack = "hasMPack",
            routeSeq ="route_seq", customernum = "customernum", hiddenRun = "hidden";

//endregion
//region Table String Builders
    String createTableRuns="create table " + tableRuns + " ( "+
            id+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            runid+ " text not null, "+
            hiddenRun+ " BOOLEAN DEFAULT 0 , "+
            shipbydate+ " text not null) ;";

    String createTableOrders="create table " + tableOrders + " ( "+
            id+" INTEGER DEFAULT 0 , " +
            orderNum+ " text PRIMARY KEY NOT NULL, "+
            customernum+" INTEGER DEFAULT 0,"+
            shipToName+ " text not null, "+
            address1+ " text , "+
            address2+ " text , "+
            address3+ " text , "+
            city    + " text, "+
            state+ " text , "+
            zip    + " text , "+
            country    + " text , "+
            routeSeq    + " text , "+
            orderComplete + " BOOLEAN DEFAULT 0 , "+
            fkRunsId    + " text , "+
            "FOREIGN KEY("+fkRunsId+") REFERENCES "+tableRuns+"("+id+")) ;";
    String createTableOrderItems="create table " + tableOrderItems + " ( "+
            id+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            itemOI+ " text not null, "+
            itemDesc+ " text not null, "+
            itemQty+ " text not null, "+
            itemLenght+ " text not null, "+
            fkOOI+ " text , "+
            "FOREIGN KEY("+fkOOI+") REFERENCES "+tableOrders+"("+orderNum+")) ;";

    String createTablePacks="create table " + tablePacks + " ( "+
            id+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            packId+ " text not null, "+
            itemOI+ " INTEGER not null, "+
            packNotes+ " text , "+
            orderNum+ " text , "+
            isPackScanned+ " BOOLEAN DEFAULT 0, "+
            isSync+ " BOOLEAN DEFAULT 0, "+
            routeSeq+ " text, "+
            hasMPack+ " text , "+
            "FOREIGN KEY("+orderNum+") REFERENCES "+tableOrders+"("+orderNum+")) ;";

    String createTablePics="create table " + tablePics + " ( "+
            id+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            imageB64+ " text , "+
            isSync+ " BOOLEAN DEFAULT 0, "+
            packId+ " text, "+
            orderNum+ " text not null) ;";

    String createTableDeliveries="create table " + tableDeliveries + " ( "+
            idDelivery+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            orderNum+ " text not null, "+
            dateDelivered+ " text not null, "+
            address+ " text , "+
            isSync+ " BOOLEAN DEFAULT 0, "+
            signature+ " text not null) ;";

    String createTableDetachedNotes = "create table " + tableDetachedNotes + " ( "+
            id+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            customernum+ " text not null, "+
            isSync+ " BOOLEAN DEFAULT 0, "+
            driverNote+ " text not null) ;";

    String createTableIssuesReport = "create table " + tableIssuesReport + " ( "+
            id+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            packId+ " text not null, "+
            orderNum+ " text not null, "+
            isSync+ " BOOLEAN DEFAULT 0, "+
            driverNote+ " text) ;";
//endregion


//region Constructor
    public LocalDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(createTableRuns);
            sqLiteDatabase.execSQL(createTableOrders);
            sqLiteDatabase.execSQL(createTableOrderItems);
            sqLiteDatabase.execSQL(createTablePacks);
            sqLiteDatabase.execSQL(createTablePics);
            sqLiteDatabase.execSQL(createTableDeliveries);
            sqLiteDatabase.execSQL(createTableDetachedNotes);
            sqLiteDatabase.execSQL(createTableIssuesReport);
        }catch (Exception e){
            Log.e("DbError", "onCreate: ",e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i<i1){
            try {
                sqLiteDatabase.execSQL(createTableDetachedNotes);
            }catch (Exception e){
                Log.e("Error", "onUpgrade: ",e);
            }
            try {
                sqLiteDatabase.execSQL(createTableIssuesReport);
            }catch (Exception e){
                Log.e("Error", "onUpgrade: ",e);
            }try {
                sqLiteDatabase.execSQL("ALTER TABLE "+tableOrders+" ADD COLUMN "+customernum+" INTEGER DEFAULT 0 ;");
            }catch (Exception e){
                Log.e("Error", "onUpgrade: ",e);
            }try {
                sqLiteDatabase.execSQL("ALTER TABLE "+tableRuns+" ADD COLUMN "+hiddenRun+" BOOLEAN DEFAULT 0 ;");
            }catch (Exception e){
                Log.e("Error", "onUpgrade: ",e);
            }try {
                sqLiteDatabase.execSQL("ALTER TABLE "+tablePacks+" ADD COLUMN "+customernum+" INTEGER DEFAULT 0 ;");
            }catch (Exception e){
                Log.e("Error", "onUpgrade: ",e);
            }try {
                sqLiteDatabase.execSQL("ALTER TABLE "+tablePics+" ADD COLUMN "+packId+" text ;");
            }catch (Exception e){
                Log.e("Error", "onUpgrade: ",e);
            }
        }

//        String dropTableRuns ="drop table if exists " + tableRuns;
//        String dropTableOrders ="drop table if exists " + tableOrders;
//        String dropTableOrderItems ="drop table if exists " + tableOrderItems;
//        String dropTableMasterPacks ="drop table if exists " + tableMasterPacks;
//        String dropTablePacks ="drop table if exists " + tablePacks;
//        String dropTableDeliveries ="drop table if exists " + tableDeliveries;
//        String dropTablePics ="drop table if exists " + tablePics;
//
//        sqLiteDatabase.execSQL(dropTablePacks);
//        sqLiteDatabase.execSQL(dropTableOrderItems);
//        sqLiteDatabase.execSQL(dropTableMasterPacks);
//        sqLiteDatabase.execSQL(dropTableOrders);
//        sqLiteDatabase.execSQL(dropTableRuns);
//        sqLiteDatabase.execSQL(dropTableDeliveries);
//        sqLiteDatabase.execSQL(dropTablePics);
//
//        onCreate(sqLiteDatabase);

    }
//endregion

// region Methods
    public Boolean addRun(Runs r){
        Boolean bool = false;

        try {
            Cursor cursor =  this.db.query(tableRuns,new String[]{id,runid,shipbydate},
                    runid+"='"+r.getRunid()+"' and "+shipbydate+"='"+r.getShipByDate()+"'"
                    ,null,null,null,null);
            if (cursor.getCount()==0){
                ContentValues values=new ContentValues();
                values.put(runid,r.getRunid());
                values.put(shipbydate,r.getShipByDate());
                long result = this.db.insert(tableRuns, null, values);
                if(result==-1){
                    return bool;
                }
                bool = addOrders(r.getOrders(),result);
            }else {
                bool = true;
            }
            cursor.close();
        }catch (Exception e){
            Log.i("AddingError", "addRun: "+e.getMessage());
        }
        return bool;
    }
    public boolean hideRun(Runs item) {
        boolean bool = false;
        try {
            ContentValues values = new ContentValues();
            values.put(hiddenRun,1);
            db.update(tableRuns, values,
                    runid+"=? and "+shipbydate+"=?",
                    new String[]{item.getRunid(), item.getShipByDate()});
            bool = true;

        } catch (Exception e) {
            Log.e("ScanningError", "addScannedPack: "+e.getMessage());
        }
        return bool;

    }
    public Boolean addOrders(ArrayList<Orders> os, long fkrun){
        boolean bool = true;
        Cursor cursor =  this.db.query(tableOrders,new String[]{id},
                null,null,null,null,null);
        int totalNumberOfOrders = cursor.getCount();
        for (Orders o : os) {

                ContentValues values=new ContentValues();
                values.put(id, (totalNumberOfOrders+1));
                values.put(orderNum,o.getOrdernum());
                values.put(shipToName,o.getName());
                values.put(address1,o.getAddress1());
                values.put(address2,o.getAddress2());
                values.put(address3,o.getAddress3());
                values.put(city,o.getCity());
                values.put(state,o.getState());
                values.put(zip,o.getZip());
                values.put(country,o.getCountry());
                values.put(routeSeq, o.getRouteSeq());
                values.put(customernum, o.getCustomernum());
                values.put(fkRunsId,fkrun);
            try {
                long result = this.db.insert(tableOrders, null, values);
                if(result==-1){
                    bool = false;
                }
            }catch (Exception e){
                Log.e("addOrders", e.getMessage() );
            }
        }
        cursor.close();
        return bool;
    }
    public Boolean addOrderItems(Packs o, String fkorders){
        Boolean bool = true;
            ContentValues values=new ContentValues();
            values.put(itemOI,o.getPackId());
            values.put(itemDesc,o.getPartDesc());
            values.put(itemQty,o.getOrderQty());
            values.put(itemLenght,0);
            values.put(fkOOI,fkorders);
            long result = this.db.insert(tableOrderItems, null, values);
            if(result==-1){
                return false;
            }

        return bool;
    }
    public Boolean addIssueReport(Packs p, String note){
        Boolean bool = true;
        ContentValues values = new ContentValues();
        values.put(packId,p.getPackId());
        values.put(orderNum,p.getOrdernum());
        values.put(driverNote,note);
        long result = this.db.insert(tableIssuesReport, null, values);
        if(result==-1){
            return false;
        }
        return bool;
    }
    public void addPicsForIssue(String order, String imgToWrite, String PackId){
        try {
            ContentValues values=new ContentValues();
            values.put(imageB64,imgToWrite);
            values.put(orderNum,order);
            values.put(packId,PackId);
            long result = this.db.insert(tablePics, null, values);
            if(result==-1){
                Log.e("AddingError", "addPics: not saved" );
            }
        }catch (Exception e){
            Log.e("AddingError", "addPics: "+e.getMessage() );
        }
    }
    public boolean addSoloPack(Packs piece, String orders, String masterpackNum) {
            ContentValues values=new ContentValues();
            values.put(packId,piece.getPackId());
            values.put(routeSeq,piece.getRouteSeq());
            values.put(itemOI,piece.getOrderLine());
            values.put(orderNum,orders);
            values.put(hasMPack, masterpackNum);
            long result = this.db.insert(tablePacks, null, values);
        return result != -1;
    }

    public boolean addExtraPack(Packs piece) {
        ContentValues values=new ContentValues();
        values.put(packId,piece.getPackId());
        values.put(itemOI,99);
        values.put(packNotes,piece.getNotes());
        values.put(orderNum,piece.getOrdernum());
        values.put(isPackScanned,1);
        values.put(routeSeq,"null");
        values.put(hasMPack,"");
        values.put(isSync,0);

        long result = this.db.insert(tablePacks, null, values);
        return result != -1;
    }

    public ArrayList<Packs> getExtraPacks(){
        ArrayList<Packs> packs = new ArrayList<>();
        String sQuerry = packNotes+" is 'extra'";
        try {
            Cursor cursor = db.query(tablePacks, new String[]{id,packNotes,packId,orderNum,
                            isPackScanned,hasMPack,isPackScanned},
                    sQuerry,null,null,null,routeSeq+" ASC");
            if (cursor.moveToFirst()){
                do {
                    Packs r = new Packs(cursor.getInt(0),cursor.getString(1),
                            cursor.getString(2), cursor.getString(3), cursor.getString(4),
                            cursor.getInt(5));
                    packs.add(r);
                }while(cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("SyncError", "getUnsyncPics: "+e.getMessage() );
        }
        return packs;
    }

    public Boolean addDelivery(DeliveryDone del){
        try {
            Cursor cursor =  this.db.query(tableDeliveries,new String[]{orderNum},
                    orderNum+"='"+del.getOrderNum()+"'"
                    ,null,null,null,null);
            if (cursor.getCount()==0){
                ContentValues values=new ContentValues();
                values.put(dateDelivered,del.getDateDelivered());
                values.put(signature,del.getSignature());
                values.put(address,del.getAddress());
                values.put(orderNum,del.getOrderNum());
                values.put(isSync,del.getSync());
                long result = this.db.insert(tableDeliveries, null, values);
                if(result==-1){
                    return false;
                }
            }
            cursor.close();
        }catch (Exception e){
            Log.e("AddingError", "addDelivery: "+e.getMessage() );
            return false;
        }
        return true;
    }
    public void addPics(String order, String imgToWrite){
        try {
            ContentValues values=new ContentValues();
            values.put(imageB64,imgToWrite);
            values.put(orderNum,order);
            long result = this.db.insert(tablePics, null, values);
            if(result==-1){
                Log.e("AddingError", "addPics: not saved" );
            }
        }catch (Exception e){
            Log.e("AddingError", "addPics: "+e.getMessage() );
        }
    }
    public int isPackOrMPack(String pack){
        int check = 2;
        try {
            Cursor cursor = db.query(tablePacks, new String[]{id,packId,packNotes,orderNum},
                    packId+"='"+pack+"'"
                    ,null,null,null,null);
            if (cursor.getCount()>0){
                check = 1;//is pack
            }else{
                Cursor cursor2 = db.query(tablePacks, new String[]{id,packId,packNotes,orderNum},
                        hasMPack+"='"+pack+"'"
                        ,null,null,null,null);
                if (cursor2.getCount()>0){
                    check = 0;//is master pack
                }
                cursor2.close();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("isPackOrMPack", e.getMessage());
        }
        return check;
    }
    public ArrayList<DetachedNote> getUnsyncDetachedNotes(){
        ArrayList<DetachedNote> notes = new ArrayList<>();
        try {
            Cursor cursor = db.query(tableDetachedNotes, new String[]{id,customernum,isSync,driverNote},
                    isSync+"=0",null,null,null,null);
            if (cursor.moveToFirst()){
                do {
                    DetachedNote r = new DetachedNote(cursor.getInt(0),cursor.getInt(1),
                            cursor.getInt(2),cursor.getString(3));
                    notes.add(r);
                }while(cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("SyncError", "getUnsyncDetachedNotes: "+e.getMessage() );
        }
        return notes;
    }
    public ArrayList<Packs> getPacks(String masterpackid) {
        ArrayList<Packs> packs = new ArrayList<>();
        try {
            Cursor cursor = db.query(tablePacks, new String[]{id,packNotes,packId,orderNum,
                            isPackScanned,hasMPack,isPackScanned},
                    hasMPack+"='"+masterpackid+"'",
                    null,null,null,routeSeq+" ASC");
            if (cursor.moveToFirst()){
                do {
                    Packs r = new Packs(cursor.getInt(0),cursor.getString(1),
                            cursor.getString(2), cursor.getString(3), cursor.getString(4),
                            cursor.getInt(5));
                    packs.add(r);
                }while(cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("SyncError", "getUnsyncPics: "+e.getMessage() );
        }
        return packs;
    }
    public Boolean addScannedPack(Packs pack){
        boolean bool = false;
        try {
            ContentValues values = new ContentValues();
            values.put(isPackScanned,1);
            db.update(tablePacks, values,
                    packId+"=?",
                    new String[]{String.valueOf(pack.getPackId())});
            bool = true;

        } catch (Exception e) {
            Log.e("ScanningError", "addScannedPack: "+e.getMessage());
        }
        return bool;
    }

    public Boolean addNoteToPack(String id, String note){
        boolean bool = false;

        try {
            ContentValues values = new ContentValues();
            values.put(packNotes,note);
            db.update(tablePacks, values,
                    packId+"=?",new String[]{id});
            bool = true;

        }catch (Exception e){
            Log.e("AddingError", "addNoteToPack: "+e.getMessage());
        }
        return bool;
    }
    public Boolean addDriverNote(String id, String note) {
        boolean bool = false;

        try {
            ContentValues values = new ContentValues();
            values.put(driverNote,note);
            db.update(tableDeliveries, values,
                    orderNum+"=?",new String[]{id});
            bool = true;

        }catch (Exception e){
            Log.e("AddingError", "addDriverNote: "+e.getMessage());
        }
        return bool;
    }
    public void addDetachedNote(int custNum, String note) {
        try {
            ContentValues values=new ContentValues();
            values.put(customernum,custNum);
            values.put(driverNote,note);
            long result = this.db.insert(tableDetachedNotes, null, values);
            if(result==-1){
                Log.e("AddingError", "addDetachedNote: not saved" );
            }
        }catch (Exception e){
            Log.e("AddingError", "addDetachedNote: "+e.getMessage() );
        }
    }

    public ArrayList<Runs> loadActiveRun(String currDate) {
        ArrayList<Runs> runs = new ArrayList<>();
        try {
            Cursor cursor = this.db.query(tableRuns, new String[]{id, runid, shipbydate},
                    hiddenRun + " =0",
                    null, null, null, shipbydate+" desc");
            if (cursor.moveToFirst()) {
                do {
                    Runs r = new Runs(cursor.getInt(0), cursor.getString(1),
                            cursor.getString(2), null);
                    runs.add(r);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }catch(Exception e){
            Log.i("LoadError", "loadActiveRun: "+e.getMessage());
        }
        return runs;
    }
    public Clients getClient(int custNum){
        Clients client = null;
        Cursor cursor = db.query(tableOrders, new String[]{id,shipToName,address1,address2,address3,city,state,zip,country,customernum},
                customernum+"="+custNum,null,
                shipToName+","+address1+","+address2+","+address3+","+city+","+state+","+zip+","+country+","+customernum,
                null,null);
        if (cursor.moveToFirst()){
            client = new Clients(cursor.getPosition(),cursor.getString(1),cursor.getString(2),
                    cursor.getString(3),cursor.getString(4),cursor.getString(5),
                    cursor.getString(6),cursor.getString(7),cursor.getString(8), cursor.getInt(9));

            cursor.close();
        }
        return client;
    }
    public ArrayList<Clients> getClients(int run, int flip){
        ArrayList<Clients> clients = new ArrayList<>();
        Cursor cursor = db.query(tableOrders, new String[]{id,shipToName,address1,address2,address3,city,state,zip,country,customernum},
                fkRunsId+"="+run+" and "+orderComplete+"="+flip,null,
                shipToName+","+address1+","+address2+","+address3+","+city+","+state+","+zip+","+country+","+customernum,
                null,routeSeq+" ASC");
        if (cursor.moveToFirst()){
            do {
                Clients r = new Clients(cursor.getPosition(),cursor.getString(1),cursor.getString(2),
                        cursor.getString(3),cursor.getString(4),cursor.getString(5),
                        cursor.getString(6),cursor.getString(7),cursor.getString(8), cursor.getInt(9));
                clients.add(r);
            }while(cursor.moveToNext());
            cursor.close();

        }
        for (Clients client:
             clients) {
            client.orders=getOrdersPerClt(run, client.getName(),flip);
        }

        return clients;
    }

    public Runs getRun(int fkRunId) {
        Runs run = null;
        Cursor cursor = db.query(tableRuns, new String[]{id,runid,shipbydate},
                id+"="+fkRunId,
                null,null,null,null);
        if (cursor.moveToFirst()){
            run = new Runs(cursor.getInt(0),cursor.getString(1),cursor.getString(2));
            cursor.close();
        }
        return run;
    }

    public int getOrder(String num) {
        int fkRunId = 0;
        Cursor cursor = db.query(tableOrders, new String[]{id,orderNum,fkRunsId},
                orderNum+"='"+num+"'",
                null,null,null,null);
        if (cursor.moveToFirst()){
            fkRunId= cursor.getInt(2);
            cursor.close();
        }
        return fkRunId;
    }
    public ArrayList<Orders> getOrdersPerClt(int run, String cltName, int flip){
        ArrayList<Orders> orders = new ArrayList<>();
        Cursor cursor = db.query(tableOrders, new String[]{id,orderNum},
                fkRunsId+"="+run+" and "+orderComplete+"="+flip+" and "+shipToName+"='"+cltName+"'",
                null,null,null,routeSeq+" ASC");
        if (cursor.moveToFirst()){
            do {
                Orders r = new Orders(cursor.getInt(0),cursor.getString(1));
                orders.add(r);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return orders;
    }
    public ArrayList<Orders> getOrdersPerCltDone(int run, String cltName){
        ArrayList<Orders> orders = new ArrayList<>();
        Cursor cursor = db.query(tableOrders, new String[]{id,orderNum},
                fkRunsId+"="+run+" and "+orderComplete+"=1"+" and "+shipToName+"='"+cltName+"'",
                null,null,null,routeSeq+" ASC");
        if (cursor.moveToFirst()){
            do {
                Orders r = new Orders(cursor.getInt(0),cursor.getString(1));
                orders.add(r);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return orders;
    }
    public OrderItems getOrderItem(String fkrun) {
        OrderItems oi = null;
        Cursor cursor = db.query(tableOrderItems, new String[]{id,itemOI,itemDesc,itemQty,itemLenght},
                itemOI+"='"+fkrun+"'",null,null,null,null);
        if (cursor.moveToFirst()){
            oi = new OrderItems(cursor.getInt(0),cursor.getInt(1),cursor.getString(2),
                        cursor.getInt(3),cursor.getString(4));
            cursor.close();
        }
        return oi;
    }
    public ArrayList<OrderItems> getOrderItems(String order){
        ArrayList<OrderItems> oi = new ArrayList<>();
        Cursor cursor = db.query(tableOrderItems, new String[]{id,itemOI,itemDesc,itemQty,itemLenght},
                fkOOI+"='"+order+"'",null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                OrderItems r = new OrderItems(cursor.getInt(0),cursor.getInt(1),cursor.getString(2),
                        cursor.getInt(3),cursor.getString(4));
                oi.add(r);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return oi;
    }
    public ArrayList<Packs> getUnsyncPacks() {
        ArrayList<Packs> deliveries = new ArrayList<>();
        try {
            Cursor cursor = db.query(tablePacks, new String[]{id,packId,packNotes,orderNum, hasMPack},
                    isSync+"=0 and "+isPackScanned+"=1",
                    null,null,null,null);
            if (cursor.moveToFirst()){
                do {
                    Packs r = new Packs(cursor.getInt(0),cursor.getString(1),
                            cursor.getString(2), cursor.getString(3),cursor.getString(4));
                    deliveries.add(r);
                }while(cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("SyncError", "getUnsyncPics: "+e.getMessage() );
        }
        return deliveries;
    }



    public ArrayList<ImgB64> getUnsyncPics(Context context) {
        ArrayList<ImgB64> deliveries = new ArrayList<>();
        try {
            Cursor cursor = db.query(tablePics, new String[]{id,orderNum,imageB64,packId},
                    isSync+"=0",null,null,null,null);
            if (cursor.moveToFirst()){
                do {
                    ImgB64 r = new ImgB64(cursor.getInt(0),cursor.getString(1),
                            ValidationsManager.convertBase64(context,cursor.getString(2)),Integer.parseInt(cursor.getString(3)));
                    deliveries.add(r);
                }while(cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("SyncError", "getUnsyncPics: "+e.getMessage() );
        }
        return deliveries;
    }

    public ArrayList<DeliveryDone> getUnsyncEntries(){
        ArrayList<DeliveryDone> deliveries = new ArrayList<>();
        try {
           Cursor cursor = db.query(tableDeliveries, new String[]{idDelivery,orderNum,dateDelivered,signature,address,isSync},
                   isSync+"=0",null,null,null,null);
           if (cursor.moveToFirst()){
               do {
                   DeliveryDone r = new DeliveryDone(cursor.getInt(0),cursor.getString(1),
                           cursor.getString(2),cursor.getString(3),cursor.getString(4),
                           cursor.getInt(5));
                   deliveries.add(r);
               }while(cursor.moveToNext());
               cursor.close();
           }
        } catch (Exception e) {
           Log.e("SyncError", "getUnsyncEntries: "+e.getMessage() );
        }
        return deliveries;
    }
    public ArrayList<Report> getUnsyncReports() {
        ArrayList<Report> deliveries = new ArrayList<>();
        try {
            Cursor cursor = db.query(tableIssuesReport, new String[]{id,packId,orderNum, driverNote},
                    isSync+"=0",
                    null,null,null,null);
            if (cursor.moveToFirst()){
                do {
                    Report r = new Report(cursor.getInt(0),cursor.getString(1),
                            cursor.getString(2), cursor.getString(3));
                    deliveries.add(r);
                }while(cursor.moveToNext());
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("SyncError", "getUnsyncPics: "+e.getMessage() );
        }
        return deliveries;
    }
    public Boolean checkDeliveryStatus(String order){
        boolean bool= false;

        try {
            Cursor cursor = db.query(tablePacks, new String[]{orderNum},
                    orderNum+"= '"+order+"'",null,null,
                    null,null);

            Cursor cursorScanned = db.query(tablePacks, new String[]{orderNum},
                    orderNum+"= '"+order+"' and "+isPackScanned+"=1",
                    null,null,null,null);
            if (cursor.getCount()!=0 && cursor.getCount() == cursorScanned.getCount()){
                bool = true;
            }
            cursor.close();
            cursorScanned.close();
        }catch (Exception e) {
            Log.e("checkStatus", "checkDeliveryStatus: "+e.getMessage() );
        }

        return bool;
    }
    public boolean checkForPack(Packs pie, String orders, String masterpackNum) {
        boolean bool = false;
        String sQuerry = packId+"= '"+pie.getPackId()+"' and "+orderNum+"= '"+orders+"'";
        try {
            Cursor cursor = db.query(tablePacks, new String[]{id,packId,packNotes,orderNum, hasMPack},
                    sQuerry,null,null,
                    null,null);
            if (cursor.getCount()==0){
                bool = true;
            }
            cursor.close();
        }catch (Exception e) {
            Log.e("checkStatus", "checkForPack: "+e.getMessage() );
        }
        return bool;
    }

    public boolean checkOrderItems(Packs piece, String orderNum) {
        boolean bool = false;
        String sQuerry = itemOI+"= '"+piece.getPackId()+"' and "+fkOOI+"='"+orderNum+"'";
        try {
            Cursor cursor = db.query(tableOrderItems, new String[]{id,itemOI,itemDesc,itemQty,itemLenght},
                    sQuerry,null,null,
                    null,null);
            if (cursor.getCount()==0){
                bool = true;
            }
            cursor.close();
        }catch (Exception e) {
            Log.e("checkStatus", "checkOrderItems: "+e.getMessage() );
        }
        return bool;
    }
    public Packs getPack(String pack) {

        String sQuerry = packId+"= '"+pack+"'";
        Packs r = null;
        try {
            Cursor cursor = db.query(tablePacks, new String[]{packId,isPackScanned},
                    sQuerry,null,null,null,null);
            if (cursor.moveToPosition(0)) {
                r = new Packs(cursor.getString(0), cursor.getInt(1));
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("SyncError", "getUnsyncEntries: "+e.getMessage() );
        }
        return r;
    }
    public Packs getFullPack(String pack) {

        String sQuerry = packId+"= '"+pack+"'";
        Packs r = null;
        try {
            Cursor cursor = db.query(tablePacks, new String[]{id,packId,packNotes,orderNum, hasMPack},
                    sQuerry,null,null,null,null);
            if (cursor.moveToPosition(0)) {
                r = new Packs(cursor.getInt(0),cursor.getString(1),
                        cursor.getString(2), cursor.getString(3),cursor.getString(4));
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("SyncError", "getUnsyncEntries: "+e.getMessage() );
        }
        return r;
    }
    public boolean checkMasterPackScanStatus(String mPackId, String num) {
        boolean bool = false;
        String sQuerryScanned = hasMPack+"= '"+mPackId+"' and "+orderNum+"= '"+num+"' and "+isPackScanned+"=1";
        String sQuerryAllPacks = hasMPack+"= '"+mPackId+"' and "+orderNum+"= '"+num+"'";
        try {
            Cursor cursor = db.query(tablePacks, new String[]{packId,isPackScanned},
                    sQuerryScanned,null,null,null,null);
            Cursor cursorAllPacks = db.query(tablePacks, new String[]{packId,isPackScanned},
                    sQuerryAllPacks,null,null,null,null);
            if (cursor.getCount()!=0 && cursor.getCount()==cursorAllPacks.getCount()) {
                bool = true;
            }
            cursor.close();
            cursorAllPacks.close();
        } catch (Exception e) {
            Log.e("SyncError", "checkMasterPackScanStatus: "+e.getMessage() );
        }
        return bool;
    }

    public int countAllPacksInOrderList(List<Orders>orders){
        int count = 0;
        for (Orders order:
             orders) {
            String sQuerry = orderNum+"='"+order.getOrdernum()+"' and "+isPackScanned+"=0";
            Cursor cursor = db.query(tablePacks,
                    new String[]{id,packId,orderNum,isPackScanned,hasMPack},
                    sQuerry,null,null,null,null);
            count+= cursor.getCount();
        }
        return count;
    }
    public List<Packs> getPacksPerOrder(String fkorder, int cmd) {
        List<Packs> pL = new ArrayList<>();
        String sQuerry = "";
        switch (cmd){
            case 0:
                sQuerry = orderNum+"='"+fkorder+"' and "+isPackScanned+"=0";
                break;
            case 1:
                sQuerry = orderNum+"='"+fkorder+"' and "+isPackScanned+"=1 and "+packNotes+" is not 'extra'";
                break;
            default:
                sQuerry = orderNum+"='"+fkorder+"'";
        }
        Cursor cursor = db.query(tablePacks,
                new String[]{id,packId,orderNum,isPackScanned,hasMPack},
                sQuerry,
                null,null,null,
                packId+" ASC");
        if (cursor.moveToFirst()){
            do {
                Packs r = new Packs(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2), cursor.getInt(3), cursor.getString(4));
                pL.add(r);
            }while(cursor.moveToNext());
            cursor.close();
        }
        return pL;
    }

    public boolean evaluateOrderScan(String s){
        int allPacksPerOrder=0, allScannedPacksPerOrder=1;
        try {
            Cursor cursor = db.query(tablePacks,
                    new String[]{id,packId,orderNum},
                    orderNum+"= '"+s+"'",
                    null,null,null,null);
            allPacksPerOrder = cursor.getCount();
            cursor.close();

            Cursor cursor1 = db.query(tablePacks,
                    new String[]{id,packId,orderNum},
                    orderNum+"= '"+s+"' and "+isPackScanned+"=1",
                    null,null,null,null);
            allScannedPacksPerOrder = cursor1.getCount();
            cursor1.close();
        }catch (Exception e){
            Log.e("evaluateOrderScan", e.getMessage() );
        }
        if (allPacksPerOrder == allScannedPacksPerOrder){
            return true;
        }
        return false;
    }
    public boolean checkOwnership(String s, String response) {
        try {
            Cursor cursor = db.query(tablePacks,
                    new String[]{id,packId,orderNum},
                    packId+"= '"+response+"' and "+orderNum+"= '"+s+"'",
                    null,null,null,null);
            if (cursor.getCount()!=0){
                return true;
            }
            cursor.close();
        }catch (Exception e){
            Log.e("checkOwnership", e.getMessage() );
        }
        return false;
    }

    public void updateDetachedNote(int custNum){
        ContentValues values = new ContentValues();
        values.put(isSync,1);
        try {
            db.update(tableDetachedNotes, values,
                    customernum+"=?", new String[]{custNum+""});
        }catch (Exception e){
            Log.i("updateDetachedNote", e.getMessage());
        }
    }

    public void updateDeliverySync(SyncResult piece) {

        ContentValues values = new ContentValues();
        values.put(isSync,1);
        String order = piece.getOrderNum();
        try {
            db.update(tableDeliveries, values,
                    orderNum+"=?", new String[]{order});
        }catch (Exception e){
            Log.i("updateDeliverySync", e.getMessage());
        }
    }
    public void updateSyncPacks(SyncResult piece) {
        ContentValues values = new ContentValues();
        values.put(isSync,1);
        String testOrderNum = piece.getOrderNum();
        String testId = piece.getId();
        try {
            db.update(tablePacks, values,
                    orderNum+"=? and "+packId+"=?",
                    new String[]{testOrderNum, testId});
        }catch (Exception e){
            Log.i("updateSyncPacks", e.getMessage());
        }
    }
    public void updateSyncReport(SyncResult piece) {
        ContentValues values = new ContentValues();
        values.put(isSync,1);
        String testOrderNum = piece.getOrderNum();
        String testId = piece.getId();
        try {
            db.update(tableIssuesReport, values,
                    orderNum+"=? and "+packId+"=?",
                    new String[]{testOrderNum, testId});
        }catch (Exception e){
            Log.i("updateSyncPacks", e.getMessage());
        }
    }
    public void updateOrderCompleteness(String s) {
        ContentValues values = new ContentValues();
        values.put(orderComplete,1);
        try {
            db.update(tableOrders, values,
                    orderNum+"=?",
                    new String[]{s});
        }catch (Exception e){
            Log.i("updateOrderCompleteness", e.getMessage());
        }
    }

    public void updateOrdersCompleteness(ArrayList<Orders> orders) {
        ContentValues values = new ContentValues();
        values.put(orderComplete,1);
        for (Orders order:
             orders) {
            try {
                db.update(tableOrders, values,
                        orderNum+"=?",
                        new String[]{order.getOrdernum()});
            }catch (Exception e){
                Log.i("updateOrderCompleteness", e.getMessage());
            }
        }
    }
    public void updateSyncPics(SyncResult piece) {
        ContentValues values = new ContentValues();
        values.put(isSync,1);
        String testOrderNum = piece.getOrderNum();
        try {
            db.update(tablePics, values,
                    orderNum+"=?", new String[]{testOrderNum});
        }catch (Exception e){
            Log.i("updateSyncPics", e.getMessage());
        }
    }

    public void deleteLocalData(){
        this.db.delete(tablePics,null,null);
        this.db.delete(tablePacks,null,null);
        this.db.delete(tableOrderItems,null,null);
        this.db.delete(tableOrders,null,null);
        this.db.delete(tableMasterPacks,null,null);
        this.db.delete(tableRuns,null,null);
        this.db.delete(tableDeliveries,null,null);
    }




//endregion
}