package com.mobile.ht.bluetoothnotifier.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.mobile.ht.bluetoothnotifier.R;
import com.mobile.ht.bluetoothnotifier.heart.HeartActivity;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity implements PersonFragment.OnFragmentInteractionListener {



    private FrameLayout mFmContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);




        mFmContainer = (FrameLayout) findViewById(R.id.mFmContainer);
        PersonFragment personFragment = PersonFragment.newInstance("", "");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mFmContainer, personFragment).commit();







    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
