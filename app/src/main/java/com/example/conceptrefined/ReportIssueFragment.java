package com.example.ProtoDeliveryApp;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.fragment.app.Fragment;

import com.example.ProtoDeliveryApp.camera.camera_fragment;
import com.example.ProtoDeliveryApp.models.LocalDbHelper;
import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.utils.DataJsonParser;
import com.example.ProtoDeliveryApp.utils.LocalStorageManager;

import java.util.ArrayList;
import java.util.List;

public class ReportIssueFragment  extends Fragment {
    private Carousel carousel;
    private String imgPath,nPackId, note;
    private ArrayList<String> imageUris;
    Packs p=null;
private Button btnSubRep;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            imgPath = bundle.getString("imgPath");

            assert imgPath != null;
            if (imgPath.length()>0){
                showPreview(imgPath);
            }
        });
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View binding = inflater.inflate(R.layout.fragment_reportissue, container, false);
        View root = binding.getRootView();

        btnSubRep = root.findViewById(R.id.btnReportML);
        Button addPhotoBtn = root.findViewById(R.id.btnAddPhotoML);
        Button addReportBtn = root.findViewById(R.id.btnAddReportML);
        LocalDbHelper helper = new LocalDbHelper(requireContext());

        addReportBtn.setOnClickListener(view -> {
            EditText mEdit = (EditText)root.findViewById(R.id.editTextNumber);
            nPackId = mEdit.getText().toString();
            EditText mEditNote = (EditText)root.findViewById(R.id.editTextReport);
            note = mEditNote.getText().toString();

            p = helper.getFullPack(nPackId);
            if(p==null){
                Toast.makeText(requireContext(),"Pack Not found",Toast.LENGTH_LONG).show();
            }else {
                mEdit.clearFocus();
                addPhotoBtn.setEnabled(true);
            }
        });

        addPhotoBtn.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("orderNum", nPackId);
            Fragment pic = new camera_fragment();
            pic.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container,pic , null)
                    .addToBackStack("takePic")
                    .commit();
        });

        btnSubRep.setOnClickListener(view -> {
            Boolean bool = LocalStorageManager.getInstance(requireContext()).addReport(p,imageUris,note);
            if (bool){
                Toast.makeText(requireContext(), "Report added successfully",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(requireContext(), "Report not added",Toast.LENGTH_LONG).show();
            }
        });

        carousel = root.findViewById(R.id.riCarousel);
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

        return root;
    }
    private  void showPreview(String s){
        if (imageUris==null){imageUris = new ArrayList<>();}
        if(!imageUris.contains(s)){imageUris.add(0,s);}
        carousel.refresh();
    }
    private void loadImage(int index, ImageView image) {
        String uri = imageUris.get(index);
        image.setImageURI(Uri.parse(uri));
    }

}
