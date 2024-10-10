package com.example.ProtoDeliveryApp.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.ProtoDeliveryApp.HomeFragment;
import com.example.ProtoDeliveryApp.OrfanPacksFragment;
import com.example.ProtoDeliveryApp.R;
import com.example.ProtoDeliveryApp.ReportIssueFragment;
import com.example.ProtoDeliveryApp.SettingsFragment;
import com.example.ProtoDeliveryApp.listeners.IDriverRunsListener;
import com.example.ProtoDeliveryApp.listeners.IHideRun;
import com.example.ProtoDeliveryApp.listeners.ILoginListener;
import com.example.ProtoDeliveryApp.listeners.IPacksListener;
import com.example.ProtoDeliveryApp.models.Driver;
import com.example.ProtoDeliveryApp.modelsportal.Orders;
import com.example.ProtoDeliveryApp.modelsportal.Packs;
import com.example.ProtoDeliveryApp.modelsportal.Runs;
import com.example.ProtoDeliveryApp.services.TrackerService;
import com.example.ProtoDeliveryApp.utils.ApiManager;
import com.example.ProtoDeliveryApp.utils.LocalStorageManager;
import com.example.ProtoDeliveryApp.utils.ValidationsManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IHideRun, NavigationView.OnNavigationItemSelectedListener, IDriverRunsListener, IPacksListener, ILoginListener {

    private DrawerLayout drawerBase;
    private int nPacks,temp,controllDigit;
    private String appLang;
    private NavigationView navigationView;
    private static final String[] permissions = ValidationsManager.permissions;
    public final static int REQUEST_CODE = 100;
    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        ValidationsManager.askPermissions(this, permissions, REQUEST_CODE);
        ///////
        controllDigit = 0;
        ApiManager.getInstance(context).setDriverRunsListener(this);
        ApiManager.getInstance(context).setDbPacks(this);
        ApiManager.getInstance(context).setHideRunListener(this);
        ApiManager.getInstance(context).setLoginListener(this);

        SharedPreferences sharedPreference1 = getSharedPreferences(ValidationsManager.sharedName(), MODE_PRIVATE);
        appLang = sharedPreference1.getString("appLang","");
        if (appLang.isEmpty())
            appLang ="fr";

        //bellow get the phone number
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(!ValidationsManager.isServiceRunning(activityManager)&&ValidationsManager
                .isConnectionInternet(context)){
            Intent serviceIntent = new Intent(this, TrackerService.class);
            startService(serviceIntent);
        }

        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
        {
            try {
                String mPhoneNumber = tMgr.getLine1Number();
                ApiManager.getInstance(getApplicationContext())
                        .loginDriver(getApplicationContext(), mPhoneNumber);
            } catch (Exception e) {
                Log.e("number", e.getMessage());
            }
        }else{
            while (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && controllDigit ==0)
            {
                try {
                    String mPhoneNumber = tMgr.getLine1Number();
                    ApiManager.getInstance(getApplicationContext())
                            .loginDriver(getApplicationContext(), mPhoneNumber);
                } catch (Exception e) {
                    Log.e("number", e.getMessage());
                }
            }
        }
        //////
        nPacks = 0;
        temp = 0;
        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        drawerBase = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerBase, toolbar,
                R.string.open_drawer_menu,
                R.string.close_drawer_menu);
        drawerBase.addDrawerListener(toggle);
        toggle.syncState();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            Fragment listFrag = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    listFrag).addToBackStack("home").commit();
        }
        else if (itemId == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SettingsFragment()).addToBackStack("settings").commit();
        }
        else if (itemId == R.id.nav_reportIssue) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ReportIssueFragment()).addToBackStack("reportissues").commit();
        }
        else if (itemId == R.id.nav_orfanPacks) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new OrfanPacksFragment()).addToBackStack("orfanpacks").commit();
        }
        else if (itemId == R.id.nav_logout) {
            finishAndRemoveTask();
        }
        drawerBase.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerBase.isDrawerOpen(GravityCompat.START)) {
            drawerBase.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onUpdateRuns(ArrayList<Runs> listRuns) {
       boolean reload =  LocalStorageManager.getInstance(getApplicationContext()).addRuns(listRuns);
       if(reload){
           for (Runs run:
                listRuns) {
               ArrayList<Orders> orders = run.getOrders();
               for (Orders o:
                       orders) {
                   ApiManager.getInstance(getApplicationContext())
                           .requestPacksApi(getApplicationContext(), o.getOrdernum());
               }
           }
       }
    }

    @Override
    public void onLoadingRuns(Boolean bool) {
        if(bool){
            findViewById(R.id.include4).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.include4).setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetPacks(ArrayList<Packs> pie, String orders) {
        nPacks += pie.size();
        for (Packs piece : pie) {
            ApiManager.getInstance(getApplicationContext()).checkforMasterPack(piece, orders);
        }
    }

    @Override
    public void onGetMasterPack(int i) {
        temp+=i;
        if (temp == nPacks){
            findViewById(R.id.include4).setVisibility(View.GONE);
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public void onHideRun(Boolean hidden) {
        if(hidden){
            Fragment listFrag = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    listFrag).addToBackStack("home").commit();
        }
    }

    @Override
    public void onValidateLogin(Context context, Driver driver) {
        controllDigit =1;
        if (!appLang.equals("")) {
            saveToShared("empId", driver.getEmpId() + "");
            saveToShared("driverName", driver.getDriverName());
        }
        Fragment listFrag = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                listFrag).commit();
        navigationView.setCheckedItem(R.id.nav_home);
    }
}

//TODO: low priority
//return to delivery list after submit scanned
