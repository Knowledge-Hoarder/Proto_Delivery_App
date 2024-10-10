package com.example.ProtoDeliveryApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.ProtoDeliveryApp.camera.camera_fragment;
import com.example.ProtoDeliveryApp.listeners.IDialogListener;
import com.example.ProtoDeliveryApp.listeners.ILocationListener;
import com.example.ProtoDeliveryApp.models.Clients;
import com.example.ProtoDeliveryApp.modelsportal.Orders;
import com.example.ProtoDeliveryApp.services.NoteDialogFragment;
import com.example.ProtoDeliveryApp.utils.ApiManager;
import com.example.ProtoDeliveryApp.utils.DataJsonParser;
import com.example.ProtoDeliveryApp.utils.LocalStorageManager;
import com.example.ProtoDeliveryApp.utils.MyLocationManager;
import com.example.ProtoDeliveryApp.utils.PicIndex;
import com.example.ProtoDeliveryApp.utils.ValidationsManager;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class SignFragment extends Fragment implements ILocationListener, IDialogListener {
    private Context context;
    private View binding;
    private SignaturePad mSignaturePad;
    private Button submitBtn;
    private Button clearBtn;
    private Button fsAddNote;
    private Fragment ScanFragment;
    private String imgPath, packId;
    private String sigStr;
    private String driverNote="";
    private List<String> imageUris;
    private ArrayList<Orders> orders;
    private Carousel carousel;
    private int fkrun, custNum;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        String sOrders = getArguments().getString("orders");
        orders = DataJsonParser.ordersFromJson(sOrders);
        fkrun = getArguments().getInt("fkrun");
        custNum = getArguments().getInt("custNum");
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            imgPath = bundle.getString("imgPath");

            assert imgPath != null;
            if (imgPath.length()>0){
                showPreview(imgPath);
            }
        });
    }
    private  void showPreview(String s){
        if (imageUris==null){imageUris = new ArrayList<>();}
        if(!imageUris.contains(s)){imageUris.add(0,s);}
        carousel.refresh();
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = inflater.inflate(R.layout.fragment_sign, container, false);
        context = requireContext();
        View root = binding.getRootView();
        MyLocationManager.getInstance(context).setLocationListener(this);
        ApiManager.getInstance(requireContext()).setDialogListener(this);

        packId = "";
        mSignaturePad = root.findViewById(R.id.signature_pad);
        submitBtn = root.findViewById(R.id.btnSubmit);
        fsAddNote = root.findViewById(R.id.fsAddNote);
        clearBtn = root.findViewById(R.id.btnClear);
        Button addPhotoBtn = root.findViewById(R.id.btnAddPhoto);
        imageUris = PicIndex.getAllPics(orders.get(0).getOrdernum());

        carousel = root.findViewById(R.id.carousel);
        carousel.setAdapter(new Carousel.Adapter() {
            @Override
            public int count() {
                if (imageUris==null){
                    return 0;
                }else {
                    return imageUris.size();
                }
            }

            @Override
            public void populate(View view, int index) {
                loadImage(index, (ImageView) view);
            }

            @Override
            public void onNewItem(int index) {
            }
        });

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }
            @Override
            public void onSigned() {
                submitBtn.setEnabled(true);
                clearBtn.setEnabled(true);
            }
            @Override
            public void onClear() {
                submitBtn.setEnabled(false);
                clearBtn.setEnabled(false);
            }
        });

        clearBtn.setOnClickListener(view -> mSignaturePad.clear());

        submitBtn.setOnClickListener(view -> {
            sigStr = mSignaturePad.getSignatureSvg();
            String dayTime = Calendar.getInstance().getTime().toString();
            String signXml = sigStr;

            MyLocationManager.getInstance(context).getLastLocation(context, dayTime, signXml);
        });

        fsAddNote.setOnClickListener(view ->{

            Clients client = LocalStorageManager.getInstance(context).getClientWNum(custNum);
            ApiManager.getInstance(context).onShowDialog(client.getName(), "driverNote");
        });

        addPhotoBtn.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("orderNum", orders.get(0).getOrdernum());
            ScanFragment = new camera_fragment();
            ScanFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container,ScanFragment , null)
                    .addToBackStack("takePic")
                    .commit();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onLocationGetter(List<Address> addresses, String dayTime, String signXml) {
        String addressToWrite = "";
        ArrayList<String> imgsToWrite = new ArrayList<>();
        if(addresses!=null){
            addressToWrite = addresses.get(0).toString();
        }
        if(imageUris!=null){
            imgsToWrite.addAll(imageUris);
        }
        if (LocalStorageManager.getInstance(getContext()).addDelivery(orders, dayTime, signXml,addressToWrite,0)) {
            LocalStorageManager.getInstance(getContext()).addImages(orders.get(0).getOrdernum(), imgsToWrite);
            if(!driverNote.isEmpty() && driverNote != null){
                LocalStorageManager.getInstance(getContext()).addDetachedNote(custNum, driverNote);
            }
            Toast.makeText(requireContext(), R.string.delivery_complete,Toast.LENGTH_SHORT).show();
        }

        navigate();
    }
    private void loadImage(int index, ImageView image) {
        String uri = imageUris.get(index);
        image.setImageURI(Uri.parse(uri));
    }

    private void navigate(){
        Bundle args = new Bundle();
        args.putInt("fkrun", fkrun);
        args.putInt("popflag", 1);
        Fragment listFrag = new DeliveryListFragment();
        listFrag.setArguments(args);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,listFrag).commit();
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

    @Override
    public void onResult(String date, String runs) {

    }

    @Override
    public void onAddDriverNote(String note) {
        driverNote = note;
    }
}