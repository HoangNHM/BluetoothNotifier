package com.mobile.ht.bluetoothnotifier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;

import com.mobile.ht.bluetoothnotifier.heart.HeartActivity;
import com.mobile.ht.bluetoothnotifier.setting.SettingActivity;

public class WelcomeActivity extends AppCompatActivity {
    public ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        img=(ImageView)findViewById(R.id.imgView);
        img.setImageResource(R.drawable.ic_action_name);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchActivity();
            }
        });

    }
    public void SwitchActivity(){
        Intent intent= new Intent(this, SettingActivity.class);
        startActivity(intent);

    }
}
