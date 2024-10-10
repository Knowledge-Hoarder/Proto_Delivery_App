package com.example.ProtoDeliveryApp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.ProtoDeliveryApp.adapters.OrfanPacksAdapter;
import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.utils.LocalStorageManager;

import java.util.ArrayList;

public class OrfanPacksFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View binding = inflater.inflate(R.layout.fragment_orfan_packs, container, false);
        View root = binding.getRootView();
        Context context = getContext();
        ListView list = root.findViewById(R.id.listOrfans);
        ArrayList<Packs> peas = LocalStorageManager.getInstance(context).getExtraPacks();
        list.setAdapter(new OrfanPacksAdapter(context,peas));
        return root;
    }
}
