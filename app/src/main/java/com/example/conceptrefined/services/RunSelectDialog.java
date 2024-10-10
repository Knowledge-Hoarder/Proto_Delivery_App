package com.example.ProtoDeliveryApp.services;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.ProtoDeliveryApp.R;
import com.example.ProtoDeliveryApp.main.MainActivity;
import com.example.ProtoDeliveryApp.modelsportal.Runs;
import com.example.ProtoDeliveryApp.utils.ApiManager;
import com.example.ProtoDeliveryApp.utils.DataJsonParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class RunSelectDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = requireContext();
        String sDate = getArguments().getString("date");
        LocalDate date = LocalDate.parse(sDate);
        String sRuns = getArguments().getString("sRuns");
        JSONArray newArray = null;
        try {
            newArray = new JSONArray(sRuns);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Runs> pie = DataJsonParser.availableRunsParser(newArray);
//        ArrayList<Runs> pie = DataJsonParser.availableRunsParser(newArray).stream().filter(element -> "PKOT".equals(element.getRunid()))
//                .collect(Collectors.toCollection(ArrayList::new));
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(R.drawable.ic_launcher_background);
        builderSingle.setTitle("Select One Name:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);
        for (Runs piece:
             pie) {
            arrayAdapter.addAll(piece.getRunid());
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(context);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        ApiManager.getInstance(context).requestrunsApi(context,0, date,strName);
                    }
                });
                builderInner.setNegativeButton("Cancel",(dialog1, which1) -> dialog1.dismiss());
                builderInner.show();
            }
        });
        return builderSingle.show();
    }
}
