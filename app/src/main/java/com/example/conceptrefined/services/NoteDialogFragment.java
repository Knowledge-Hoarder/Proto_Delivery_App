package com.example.ProtoDeliveryApp.services;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.ProtoDeliveryApp.R;
import com.example.ProtoDeliveryApp.models.Driver;
import com.example.ProtoDeliveryApp.modelsportal.OrderItems;
import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.utils.ApiManager;
import com.example.ProtoDeliveryApp.utils.DataJsonParser;
import com.example.ProtoDeliveryApp.utils.LocalStorageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Objects;

public class NoteDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState) {
        String arg1 = getArguments().getString("packid");
        String setMode =  getArguments().getString("mode");

        final EditText edittext = new EditText(getContext());
        Context context = getContext();
        String messageText = arg1;
        if (Objects.equals(setMode, "run")){
            messageText = "Insert Run Id";
        } else if(Objects.equals(setMode, "pop-up")){
            try{
                OrderItems oi = LocalStorageManager.getInstance(context).getOrderItem(arg1);
                return new AlertDialog.Builder(requireContext())
                        .setMessage(oi.getItemDesc())
                        .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                        } ).create();
            }catch (Exception e){
                Toast.makeText(context, "Could not display information",Toast.LENGTH_LONG).show();
            }

        }else if(Objects.equals(setMode, "addExtraPack")){
            Packs p = DataJsonParser.packParserFromJson(arg1);
            Packs finalP = p;
            return new AlertDialog.Builder(requireContext())
                    .setMessage(getResources().getString(R.string.addExtraPack)+"\n"+finalP.getPackId())
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        ApiManager.getInstance(context).afterInput(finalP);
                    } )
                    .setNegativeButton(R.string.no,(dialog, which)->{
                    })
                    .create();
        }else if(Objects.equals(setMode, "repeat")){
            try{
                return new AlertDialog.Builder(requireContext())
                        .setMessage(getResources().getString(R.string.scanned_twice)+" "+arg1)
                        .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                        } ).create();
            }catch (Exception e){
                Toast.makeText(context, "Could not display information",Toast.LENGTH_LONG).show();
            }

        }
        return new AlertDialog.Builder(requireContext())
                .setMessage(messageText)
                .setView(edittext)
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    switch (setMode){
                        case "note":
                            boolean bool = LocalStorageManager.getInstance(context)
                                    .addNoteToPack(arg1, edittext.getText().toString());
                            if (bool){
                                Log.i(TAG, "onCreateDialog: yeah the button worked");
                            }
                            break;
                        case "run":
                            LocalDate date = LocalDate.parse(arg1);
                            ApiManager.getInstance(context)
                                    .requestrunsApi(context,0, date,edittext.getText().toString());
                            break;
                        case "driverNote":
                            ApiManager.getInstance(context)
                                    .onAddDriverNote(edittext.getText().toString());
                            break;
                    }
                } )
                .setNegativeButton(R.string.cancel,(dialog, which)->{

                })
                .create();
    }
    public static String TAG = "NoteConfirmation";
}

