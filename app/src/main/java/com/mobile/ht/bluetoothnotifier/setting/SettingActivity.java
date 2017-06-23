package com.mobile.ht.bluetoothnotifier.setting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.mobile.ht.bluetoothnotifier.MyApplication;
import com.mobile.ht.bluetoothnotifier.R;
import com.mobile.ht.bluetoothnotifier.heart.HeartActivity;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity implements PersonFragment.OnFragmentInteractionListener {

    private FrameLayout mFmContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //get isFirstTime
        final boolean isFirstTime = getIntent().getBooleanExtra("isFirstTime", false);
        mFmContainer = (FrameLayout) findViewById(R.id.mFmContainer);
        PersonFragment personFragment = PersonFragment.newInstance("", "");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mFmContainer, personFragment).commit();
        //btn Done
        Button btnDone = (Button) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstTime) {
                    startActivity(new Intent(SettingActivity.this, HeartActivity.class));
                } else {
                    onBackPressed();
                }
            }
        });
        int permission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            goToSettings();
        }

    }
    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, 12345);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}
