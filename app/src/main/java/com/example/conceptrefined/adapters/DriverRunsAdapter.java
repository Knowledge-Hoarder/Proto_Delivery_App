package com.example.ProtoDeliveryApp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ProtoDeliveryApp.R;
import com.example.ProtoDeliveryApp.models.Clients;
import com.example.ProtoDeliveryApp.models.LocalDbHelper;
import com.example.ProtoDeliveryApp.modelsportal.Orders;
import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.utils.ApiManager;
import com.example.ProtoDeliveryApp.utils.LocalStorageManager;
import com.example.ProtoDeliveryApp.utils.ValidationsManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DriverRunsAdapter extends BaseExpandableListAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Clients> items;
    private int runId;
    private List<String> titlesList;
    private Map<String, List<Clients>> listCollection;
    public DriverRunsAdapter(Context context, int runId){
        this.context = context;
//        this.items = items;
        this.runId = runId;
        Map<String, List<Clients>> tempData = new HashMap<String, List<Clients>>();

        ArrayList<Clients> clients = LocalStorageManager.getInstance(context).showOrders(runId,0);
        tempData.put(0+"", clients);
        ArrayList<Clients> clientsFinished = LocalStorageManager.getInstance(context).showOrders(runId,1);
        tempData.put(1+"",clientsFinished);

        this.listCollection = tempData;
        List<String> titles = new ArrayList<>(listCollection.keySet());
        this.titlesList = titles;
    }

    @Override
    public int getGroupCount() {
        return listCollection.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listCollection.get(titlesList.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.titlesList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listCollection.get(titlesList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String listTitle = context.getString(R.string.finished);
        if (getGroup(i)=="0")
            listTitle = context.getString(R.string.todo);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_list_collectionchild, null);
        }
        view.findViewById(R.id.ilcnewnote).setVisibility(View.GONE);
        view.findViewById(R.id.ilcDeets).setVisibility(View.GONE);
        TextView item = view.findViewById(R.id.ilcPack);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(listTitle);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
         TextView tvIdAddress1,tvIdAddress2,tvIdZip,tvIdState,
                tvIdCustName, tvIdCountry, tvOdTotalPacksValue;
        Button btnMap, btnDetails, btnDeliverOrder;

        Clients item = (Clients) getChild(i,i1);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.field_ordertodeliver, null);
        }
        tvIdAddress1 = view.findViewById(R.id.tvIdAddress1);
        tvIdAddress2 = view.findViewById(R.id.tvIdAddress2);
        tvIdZip = view.findViewById(R.id.tvIdZip);
        tvIdState = view.findViewById(R.id.tvIdState);
        tvIdCustName = view.findViewById(R.id.tvIdCustName);
        tvIdCountry = view.findViewById(R.id.tvIdCountry);
        tvOdTotalPacksValue = view.findViewById(R.id.tvOdTotalPacksValue);

        tvIdAddress1.setText(item.getAddress1());
        tvIdAddress2.setText(item.getAddress2());
        tvIdCustName.setText(item.getName());
        tvIdZip.setText(item.getZip());
        tvIdState.setText(item.getState());
        tvIdCountry.setText(item.getCountry());

        btnDetails = view.findViewById(R.id.btnDetails);
        btnMap = view.findViewById(R.id.btnMap);
        btnDeliverOrder = view.findViewById(R.id.btnEnd);
        ArrayList<Orders>orders = LocalStorageManager.getInstance(context).getOrders(runId,item.getName(),0);
        List<Packs> mpL = LocalStorageManager.getInstance(context).getPacksPerOrder(orders,0);

        tvOdTotalPacksValue.setText(""+ mpL.size());
        int orderStatus = i;
        btnDetails.setOnClickListener(view1 -> ApiManager.getInstance(context).trstOrderLines(runId,item.getName(), item.getAddress1(), orderStatus));

        if(!ApiManager.getInstance(context).checkDeliveryStatus(item.getOrders())) {

            btnMap.setVisibility(View.VISIBLE);
            btnMap.setOnClickListener(view1 -> {
                String address = item.getAddress1() + " " + item.getZip() + " " + item.getCountry();
                if (ValidationsManager.isConnectionInternet(context)) {
                    //Call google maps search query
                    //https://developers.google.com/maps/documentation/urls/android-intents#java_12
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address);
                    try {
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        context.startActivity(mapIntent);
                    }catch (Exception e){
                        Log.e("DriverRunsAdapter", "mapIntent: ",e );
                        Toast.makeText(context,R.string.errorMaps,Toast.LENGTH_LONG).show();
                    }

                }
            });
//
            btnDeliverOrder.setVisibility(View.VISIBLE);
            btnDeliverOrder.setOnClickListener(view1 -> ApiManager.getInstance(context)
                    .trstSign(item.getOrders(), item.getCustomernum()));
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
