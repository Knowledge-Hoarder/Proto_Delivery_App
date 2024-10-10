package com.example.ProtoDeliveryApp;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import androidx.fragment.app.Fragment;

import com.example.ProtoDeliveryApp.adapters.ExpandableListAdapter;
import com.example.ProtoDeliveryApp.listeners.IDialogListener;
import com.example.ProtoDeliveryApp.services.NoteDialogFragment;
import com.example.ProtoDeliveryApp.utils.ApiManager;

public class OrderDetailsFragment extends Fragment implements IDialogListener {
 private String cltName;
 private int fkrun,orderStatus;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View binding = inflater.inflate(R.layout.fragment_order_details, container, false);
        View root = binding.getRootView();

        assert getArguments() != null;
        orderStatus = getArguments().getInt("orderStatus");
        fkrun = getArguments().getInt("fkrunId");
        cltName = getArguments().getString("cltName");
        Button btnItemInfo = root.findViewById(R.id.btnDetailsItems);

        Button btnPackInfo = root.findViewById(R.id.btnDetailsPacks);

        ExpandableListView exList = root.findViewById(R.id.exListSelectedEfect);

        ApiManager.getInstance(requireContext()).setDialogListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int maxWidth = displayMetrics.widthPixels;
        exList.setAdapter(new ExpandableListAdapter(getContext(), maxWidth,fkrun,cltName,orderStatus,0));

        btnItemInfo.setOnClickListener(view -> {
            exList.setAdapter(new ExpandableListAdapter(getContext(), maxWidth,fkrun,cltName,orderStatus,1));
        });

        btnPackInfo.setOnClickListener(view -> {
            exList.setAdapter(new ExpandableListAdapter(getContext(), maxWidth,fkrun,cltName,orderStatus,0));
//            if (exList.getVisibility() == View.INVISIBLE) {
//                list.setVisibility(View.INVISIBLE);
//                exList.setVisibility(View.VISIBLE);
//                exList.setAdapter(new ExpandableListAdapter(getContext(), maxWidth,fkrun,cltName));
//            }
        });
        return root;
    }

    @Override
    public void onShowDialog(String packid, String mode) {
        Bundle args = new Bundle();
        args.putString("packid", packid);
        args.putString("mode", mode);
        Fragment frag = new NoteDialogFragment();
        frag.setArguments(args);
        getChildFragmentManager().beginTransaction().add(frag, NoteDialogFragment.TAG)
                .commit();
    }

    @Override
    public void onResult(String date, String runs) {

    }

    @Override
    public void onAddDriverNote(String note) {

    }
}