package com.example.ProtoDeliveryApp;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.example.ProtoDeliveryApp.adapters.DriverRunsAdapter;
import com.example.ProtoDeliveryApp.listeners.IOrderItemsListener;
import com.example.ProtoDeliveryApp.models.Clients;
import com.example.ProtoDeliveryApp.modelsportal.Orders;
import com.example.ProtoDeliveryApp.utils.ApiManager;
import com.example.ProtoDeliveryApp.utils.DataJsonParser;
import com.example.ProtoDeliveryApp.utils.LocalStorageManager;

import java.util.ArrayList;


public class DeliveryListFragment extends Fragment implements IOrderItemsListener {

    private int fkrun;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View binding = inflater.inflate(R.layout.fragment_delivery_list, container, false);
        View root = binding.getRootView();

        assert getArguments() != null;
        fkrun = getArguments().getInt("fkrun");

        ApiManager.getInstance(requireContext()).setOIListener(this);

        ExpandableListView list = root.findViewById(R.id.exlistDeliveries);


        list.setAdapter(new DriverRunsAdapter(requireContext(),fkrun));
        return root;
    }

    @Override
    public void onDetailBtnPressed(int fkrun, String cltName, String address1,int orderStatus) {
        Bundle args = new Bundle();
        args.putString("cltName", cltName);
        args.putInt("fkrunId", fkrun);
        args.putInt("orderStatus", orderStatus);
        Fragment listFrag = new OrderDetailsFragment();
        listFrag.setArguments(args);
        requireActivity().getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragment_container,listFrag , null)
                            .addToBackStack("orderdtl")
                            .commit();
    }

    @Override
    public void onDeliverBtnPressed(ArrayList<Orders> orders, int custNum) {
        Bundle args = new Bundle();
        String sOrders = DataJsonParser.parseOrdersList(orders);
        args.putString("orders", sOrders);
        args.putInt("fkrun", fkrun);
        args.putInt("custNum", custNum);
        Fragment frag = new PackScanFragment();
        frag.setArguments(args);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack("scanfrag")
                .replace(R.id.fragment_container,frag).commit();
    }

}