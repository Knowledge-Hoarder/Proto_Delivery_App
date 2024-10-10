package com.example.ProtoDeliveryApp;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ProtoDeliveryApp.adapters.PackidListAdapter;
import com.example.ProtoDeliveryApp.listeners.IDialogListener;
import com.example.ProtoDeliveryApp.listeners.PackIdInputListener;
import com.example.ProtoDeliveryApp.models.LocalDbHelper;
import com.example.ProtoDeliveryApp.modelsportal.Orders;
import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.services.NoteDialogFragment;
import com.example.ProtoDeliveryApp.utils.ApiManager;
import com.example.ProtoDeliveryApp.utils.DataJsonParser;
import com.example.ProtoDeliveryApp.utils.LocalStorageManager;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.ScanDataCollection.ScanData;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.Scanner.TriggerType;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;
import com.symbol.emdk.barcode.StatusData.ScannerStates;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PackScanFragment  extends Fragment implements PackIdInputListener, IDialogListener,
                                                        EMDKListener, StatusListener, DataListener{
    private Button btnStartScan, btnSubmit, btnStopScan,btnClearSubs;
    private ListView list;
    private ProgressBar spinner;
    private ArrayList<Packs> items;
    private ArrayList<Orders> orders;
    private int fkrun, custNum;
    private String sOrders;
    // Variables to hold EMDK related objects
    private EMDKManager emdkManager = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;
    private TextView scannedQtyPackScan,scannedTotalPackScan;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View binding = inflater.inflate(R.layout.fragment_packscan_list, container, false);
        View root = binding.getRootView();
        Context context = getContext();
        assert getArguments() != null;
        sOrders = getArguments().getString("orders");
        orders = DataJsonParser.ordersFromJson(sOrders);
        fkrun = getArguments().getInt("fkrun");
        custNum = getArguments().getInt("custNum");

        list = root.findViewById(R.id.listDeliveries);
        btnStartScan = root.findViewById(R.id.btnStartScan);
        btnStopScan = root.findViewById(R.id.btnStopScan);
        btnSubmit = root.findViewById(R.id.btnSubmit);
        btnClearSubs = root.findViewById(R.id.btnClearSubs);
        spinner = root.findViewById(R.id.progressBar);
        scannedQtyPackScan = root.findViewById(R.id.scannedQtyPackScan);
        scannedTotalPackScan = root.findViewById(R.id.scannedTotalPackScan);

        ApiManager.getInstance(requireContext()).setPackIdInputListener(this);
        ApiManager.getInstance(requireContext()).setDialogListener(this);

        btnStartScan.setOnClickListener(view -> startScan());

        btnStopScan.setOnClickListener(view -> stopScan());

        btnSubmit.setOnClickListener(view -> {
            btnStartScan.setEnabled(false);
            btnSubmit.setEnabled(false);
            setSpinner();
            closeDependencies();
            ApiManager.getInstance(context).saveScanedPacks(items);
            resetListState();
        });

        btnClearSubs.setOnClickListener(view -> {btnStartScan.setEnabled(true);
            list.setAdapter(null);
            btnClearSubs.setVisibility(View.INVISIBLE);
        });

        LocalDbHelper helper= new LocalDbHelper(context);
        scannedTotalPackScan.setText(helper.countAllPacksInOrderList(orders)+"");
        helper.close();

        return root;
    }

    private void resetListState() {
        if(items!=null){
            list.setAdapter(null);
            items = null;
        }
    }

    private void setSpinner() {
        if(spinner.getVisibility() == View.INVISIBLE){
            spinner.setVisibility(View.VISIBLE);
        }else {
            spinner.setVisibility(View.INVISIBLE);
        }
    }
    private void startScan(){
        btnStartScan.setEnabled(false);
        btnStopScan.setVisibility(View.VISIBLE);

        EMDKResults results = EMDKManager.getEMDKManager(requireContext(), PackScanFragment.this);
        if (results.statusCode!=   EMDKResults.STATUS_CODE.SUCCESS) {
            Log.e("EMDK", "EMDKManager object request failed!");

        } else {
            Log.w("EMDK", "EMDKManager object initialization is   in   progress.......");
        }
    }
    private void stopScan(){
        btnStartScan.setEnabled(true);
        btnStopScan.setVisibility(View.INVISIBLE);
        closeDependencies();
        if(items!=null)
            btnSubmit.setEnabled(true);
        Log.d("stop", "onClick: stop");
    }
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
    @Override
    public void onInput(String response) {
        Log.i("onInput", "onInput:"+response);

        if (!response.isEmpty()){
            if(items==null){
                items = new ArrayList<>();
            }
            Packs i = new Packs(0, response);
            boolean belongs = LocalStorageManager.getInstance(getContext()).checkOwnership(orders, response);
            if (belongs){
                Log.i("onInput", "onInput: belongs");
                //sets flag to evaluate if the pack was scanned multiple times
                if (items.stream().anyMatch(packs -> packs.getPackId().equals(i.getPackId()))){
                    i.setFlag(true);
                    Log.i("onInput", "onInput: contains");
                    playTone();
                    stopScan();
                    ApiManager.getInstance(requireContext()).onShowDialog(i.getPackId(), "repeat");
                }else {
                    Log.i("onInput", "onInput: does not contain");
                    i.setFlag(false);
                }
                addPackToArray(i);
            }else {
                stopScan();
                playTone();
                Packs p = new Packs(response,"extra",orders.get(0).getOrdernum());
                i.setNotes("extra");
                i.setOrdernum(orders.get(0).getOrdernum());
                ApiManager.getInstance(requireContext()).onShowDialog(p.toJSON().toString(), "addExtraPack");
            }
        }
    }

    private void playTone() {
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_DTMF_D,5000);
    }

    public void addPackToArray(Packs i){
        ArrayList<Packs> tempItems = new ArrayList<>();
        tempItems.add(i);
        tempItems.addAll(items);
        items = (ArrayList<Packs>) tempItems.stream().filter(distinctByKey(packs -> packs.getPackId())).collect(Collectors.toList());
        scannedQtyPackScan.setText(items.size() + "");
        list.setAdapter(new PackidListAdapter(getContext(),items));
    }
    @Override
    public void onScan(ArrayList<Packs> pie, ArrayList<Packs> controlPie) {
        if (!controlPie.isEmpty()){
            Toast.makeText(getContext(),"Some Packs where not submited", Toast.LENGTH_LONG).show();
        }
        Bundle args = new Bundle();

        String sOrders = DataJsonParser.parseOrdersList(orders);
        args.putString("orders", sOrders);
        args.putInt("fkrun", fkrun);
        args.putInt("custNum", custNum);
        Fragment frag = new SignFragment();
        frag.setArguments(args);
        requireActivity().getSupportFragmentManager().beginTransaction().addToBackStack("scan")
                .replace(R.id.fragment_container,frag).commit();
    }
    @Override
    public void afterInput(Packs p) {
        if (LocalStorageManager.getInstance(requireContext()).addExtraPack(p)){
            addPackToArray(p);
        }
    }
    @Override
    public void onShowDialog(String packid, String mode) {
        Bundle args = new Bundle();
        args.putString("packid", packid);
        args.putString("mode", mode);
        Fragment frag = new NoteDialogFragment();
        frag.setArguments(args);
        getChildFragmentManager().beginTransaction()
                .add(frag, NoteDialogFragment.TAG).commit();
    }
    private void initBarcodeManager() {
        // Get the feature object such as BarcodeManager object for accessing the feature.
        barcodeManager =  (BarcodeManager)emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
        // Add external scanner connection listener.
        if (barcodeManager == null) {
            Toast.makeText(getContext(), "Barcode scanning is not supported.", Toast.LENGTH_LONG).show();
        }
    }
    private void initScanner() {
        if (scanner == null) {
            setSpinner();
            // Get default scanner defined on the device
            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
            if(scanner != null) {
                // Implement the DataListener interface and pass the pointer of this object to get the data callbacks.
                scanner.addDataListener(this);
                // Implement the StatusListener interface and pass the pointer of this object to get the status callbacks.
                scanner.addStatusListener(this);
                scanner.addStatusListener(this);
                // Hard trigger. When this mode is set, the user has to manually
                // press the trigger on the device after issuing the read call.
                // NOTE: For devices without a hard trigger, use TriggerType.SOFT_ALWAYS.
                scanner.triggerType =  TriggerType.HARD;
                try{
                    // Enable the scanner
                    // NOTE: After calling enable(), wait for IDLE status before calling other scanner APIs
                    // such as setConfig() or read().
                    scanner.enable();
                } catch (ScannerException e) {
                    Log.e("EMDK", "updateStatus: ", e);
                    updateStatus("updateStatus: "+e);
                    deInitScanner();
                }
            } else {
                Log.w("EMDK", "Failed to   initialize the scanner device.");
                updateStatus("Failed to   initialize the scanner device.: ");
            }
        }
    }
    private void deInitScanner() {
        if (scanner != null) {
            try {
                // Release the scanner
                scanner.release();
            } catch (Exception e)   {
                Log.e("EMDK", "deInitScanner: ", e);
            }
            scanner = null;
            setSpinner();
        }
    }
    @Override
    public void onResult(String date, String runs) {

    }

    @Override
    public void onAddDriverNote(String note) {

    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager =  emdkManager;
        // Get a  reference to the BarcodeManager feature object
        initBarcodeManager();
//         Initialize the scanner
        initScanner();
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        // The ScanDataCollection object gives scanning result and the collection of ScanData. Check the data and its status.
        if ((scanDataCollection != null) &&  (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
            ArrayList<ScanData> scanData =  scanDataCollection.getScanData();
            updateStatus(scanData.get(0).getData());
        }else {
            updateStatus("onData error");
        }
    }

    private void updateStatus(final String status) {
        new Thread(() -> {
            try {
                requireActivity().runOnUiThread(() -> ApiManager.getInstance(getContext()).onNewInput(status));
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onStatus(StatusData statusData) {
        if (statusData.getState() == ScannerStates.IDLE) {
            try {
                ScannerConfig scannerConfig = scanner.getConfig();
                if (!scannerConfig.decoderParams.i2of5.enabled) {
                    scannerConfig.decoderParams.i2of5.enabled = true;
                    //Set beam timer for imager
                    scanner.setConfig(scannerConfig);
                }
            } catch (ScannerException e) {
                Log.e("EMDK", "setConfig: ", e);
            }
        }
        if (statusData.getState() == ScannerStates.IDLE) {
            try {
                scanner.read();
            } catch (ScannerException e) {
                Log.e("EMDK", "onStatus: ", e);
            }
        }

    }
    @Override
    public void onDestroy() {
        // Release all the EMDK resources
        closeDependencies();
        super.onDestroy();
    }
    @Override
    public void onClosed() {
        // The EMDK closed unexpectedly. Release all the resources.
        if (emdkManager != null) {
            try {
                scanner.release();
            } catch (ScannerException e) {
                Log.e("EMDK", "onDestroy: ", e);
            }
            scanner=null;
            emdkManager.release();
            emdkManager= null;
        }
        Log.e("EMDK", "onClosed: EMDK closed unexpectedly! Please close and restart the application.");
    }
 private void closeDependencies(){
     if (emdkManager != null) {
         deInitScanner();
         emdkManager.release();
         emdkManager= null;
     }
 }
}
