package com.example.ProtoDeliveryApp.camera;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ProtoDeliveryApp.R;
import com.example.ProtoDeliveryApp.utils.PicIndex;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class camera_fragment extends Fragment {

    private View root;
    private PreviewView previewCamera;
    private ExecutorService cameraAnalyserExecutor;
    private ImageAnalyser analyser;
    private ImageCapture imageCapture;
    private static final String CAMERA_PERMISSION = android.Manifest.permission.CAMERA;
    private String picStorageFolger;

    public camera_fragment(){
        super (R.layout.fragment_camera);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private boolean hasCameraPermissions(){
        return ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_camera, container, false);
        previewCamera = root.findViewById(R.id.previewView);
        ImageView clicableImg = root.findViewById(R.id.captureImg);

        assert getArguments() != null;
        String orderNum = getArguments().getString("orderNum");
        picStorageFolger = PicIndex.StorageFolder(orderNum)+"/";
        clicableImg.setOnClickListener(view -> capturePhoto());

        return root.getRootView();
    }
    private void requestPermissionLauncher(){
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), (isGranted) -> {
            if (isGranted){
                Log.d("permission", "Camera permission is allowed to be accessed: " + isGranted);
                startCamera();
            }else {
                Log.d("permission", "Camera permission is allowed to be accessed: " + isGranted);
                Toast.makeText(requireContext(), "Change permissions to access camera", Toast.LENGTH_SHORT).show();
            }
        }).launch(android.Manifest.permission.CAMERA);
    }
    private Executor getExecutor(){
        return ContextCompat.getMainExecutor(requireContext());
    }
    public void startCamera(){
        Log.d("permission", "starting camera");

        cameraAnalyserExecutor = Executors.newSingleThreadExecutor();
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        analyser = new ImageAnalyser();

        cameraProviderFuture.addListener(() -> {
            try{
                ProcessCameraProvider processCameraProvider = cameraProviderFuture.get();
                processCameraProvider.unbindAll();
                bindingPreview(processCameraProvider);
            } catch (ExecutionException | InterruptedException e){
                e.printStackTrace();
            }
        }, getExecutor());
    }
    @SuppressLint("RestrictedApi")
    private void bindingPreview(ProcessCameraProvider processCameraProvider) {
       CameraSelector cameraSelector = new CameraSelector.Builder()
               .requireLensFacing(CameraSelector.LENS_FACING_BACK)
               .build();

       Preview preview = new Preview.Builder().build();
       preview.setSurfaceProvider(previewCamera.getSurfaceProvider());

         imageCapture = new ImageCapture.Builder()
                 .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                 .setMaxResolution(new Size(1920,1080))
                 .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(860, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraAnalyserExecutor, analyser);

        processCameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);
    }
    public static class ImageAnalyser implements ImageAnalysis.Analyzer {
        InputImage imageToScan;
        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
            @SuppressLint("UnsafeOptInUsageError")
            Image mediaImage = imageProxy.getImage();

            if(mediaImage != null){

                imageToScan = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            }
             ScannerUtil.scanThis(imageToScan);
            // after done, release the ImageProxy object
            imageProxy.close();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(hasCameraPermissions()){
            startCamera();
        } else if (shouldShowRequestPermissionRationale(CAMERA_PERMISSION)) {
            Log.d("permission", "Showing rationale on UI");
        } else {
            requestPermissionLauncher();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        root = null;
    }
    private void capturePhoto() {
        long timeStamp = System.currentTimeMillis();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timeStamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        requireActivity().getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        File from = new File(Environment.getExternalStorageDirectory(),
                                "Pictures/"+timeStamp+".jpg");
                        File to = new File(picStorageFolger+timeStamp+".jpg");
                        from.renameTo(to);
                        closefragment(picStorageFolger+timeStamp+".jpg");
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(requireActivity(),"Error: "+exception.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void closefragment(String lastFileTaken) {
        Bundle result = new Bundle();
        result.putString("imgPath", lastFileTaken);
        getParentFragmentManager().setFragmentResult("requestKey", result);
        requireActivity().onBackPressed();
    }
}