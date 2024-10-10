package com.example.ProtoDeliveryApp.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ProtoDeliveryApp.R;
import com.example.ProtoDeliveryApp.listeners.ILoginListener;
import com.example.ProtoDeliveryApp.models.Driver;
import com.example.ProtoDeliveryApp.utils.ApiManager;
import com.example.ProtoDeliveryApp.utils.ValidationsManager;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements ILoginListener {
    private String appLang, mPhoneNumber;
    private static final String[] permissions = ValidationsManager.permissions;
    public final static int REQUEST_CODE = 100;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Context  context = getApplicationContext();
        ApiManager.getInstance(context).setLoginListener(this);
        ValidationsManager.askPermissions(this, permissions, REQUEST_CODE);

        SharedPreferences sharedPreference1 = getSharedPreferences(ValidationsManager.sharedName(), MODE_PRIVATE);
        appLang = sharedPreference1.getString("appLang","");

        //bellow get the phone number
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
        {
            try {
                mPhoneNumber = tMgr.getLine1Number();
                if (mPhoneNumber!=null)
                    saveToShared("phoneNumber",mPhoneNumber);
            } catch (Exception e) {
                Log.e("number", e.getMessage());
            }
        }

        if(!appLang.isEmpty()){
            languageSelector(appLang);
            launchMain();
        }
        ImageView langSelectEN= findViewById(R.id.langSelectEN);
        ImageView langSelectFR= findViewById(R.id.langSelectFR);

        langSelectEN.setOnClickListener(view -> {
            if (languageSelector("en")){
                appLang = "en";
                saveToShared("appLang","en");
                launchMain();
            }
        });

        langSelectFR.setOnClickListener(view -> {
            if (languageSelector("fr")){
                appLang = "fr";
                saveToShared("appLang","fr");
                launchMain();
            }
        });
    }


    private void launchMain(){
        SharedPreferences sharedPreference1 = getSharedPreferences(ValidationsManager.sharedName(), MODE_PRIVATE);
        String driverNum = sharedPreference1.getString("phoneNumber","");
        ApiManager.getInstance(getApplicationContext())
                .loginDriver(getApplicationContext(), driverNum);
    }
    private boolean languageSelector(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        return true;
    }

    public void saveToShared(String sharedName,String lang){
        String name = ValidationsManager.sharedName();
        SharedPreferences sharedPreferences = getSharedPreferences(name,MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor addShare = sharedPreferences.edit();
        addShare.putString(sharedName,lang);
        addShare.apply();
    }

    @Override
    public void onValidateLogin(Context context, Driver driver) {
        if (!appLang.equals("")){
            Bundle bundle = new Bundle();
            bundle.putInt("empId", driver.getEmpId());
            saveToShared("empId", driver.getEmpId()+"");
            bundle.putString("driverName", driver.getDriverName());
            saveToShared("driverName", driver.getDriverName());
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults.length>0 && grantResults[i]== PackageManager.PERMISSION_GRANTED){
                Log.d("Permissions", permissions[i]+"::"+grantResults[i]+": granted!");
            }else {
                Log.d("Permissions", permissions[i]+"::"+grantResults[i]+": needed!");
                Toast.makeText(getApplicationContext(),"Permission needed: "+permissions[i],
                        Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
