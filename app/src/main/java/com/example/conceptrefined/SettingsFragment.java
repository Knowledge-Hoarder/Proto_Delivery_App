package com.example.ProtoDeliveryApp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ProtoDeliveryApp.utils.ValidationsManager;

import java.util.Locale;

public class SettingsFragment extends Fragment {
    String appLang;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View binding = inflater.inflate(R.layout.fragment_settings, container, false);
        View root = binding.getRootView();

        ImageView langSelectEN= root.findViewById(R.id.stLangSelectEN);
        ImageView langSelectFR= root.findViewById(R.id.stLangSelectFR);
        String versionName;
        try {
            versionName = requireContext().getPackageManager()
                    .getPackageInfo(getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        TextView versionTag = root.findViewById(R.id.versionTag);
        versionTag.setText(String.format("v:%s", versionName));
        SharedPreferences sharedPreference1 = requireActivity().getSharedPreferences(ValidationsManager.sharedName(), MODE_PRIVATE);
        appLang = sharedPreference1.getString("appLang","");

        langSelectEN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (languageSelector("en")){
                    appLang = "en";
                    saveToShared("appLang","en");
                    requireActivity().onBackPressed();
                }
            }
        });

        langSelectFR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (languageSelector("fr")){
                    appLang = "fr";
                    saveToShared("appLang","fr");
                    requireActivity().onBackPressed();
                }
            }
        });

        return root;
    }
    public void saveToShared(String sharedName,String lang){
        String name = ValidationsManager.sharedName();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(name,MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor addShare = sharedPreferences.edit();
        addShare.putString(sharedName,lang);
        addShare.apply();
    }

    private boolean languageSelector(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        requireContext().getResources().updateConfiguration(config, requireContext().getResources().getDisplayMetrics());
        return true;
    }
}
