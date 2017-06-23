package com.mobile.ht.bluetoothnotifier;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ImageView;

import com.mobile.ht.bluetoothnotifier.heart.HeartActivity;
import com.mobile.ht.bluetoothnotifier.setting.Person;
import com.mobile.ht.bluetoothnotifier.setting.SettingActivity;

import java.util.List;

public class WelcomeActivity extends AppCompatActivity {
    public ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        img=(ImageView)findViewById(R.id.imgView);
        img.setImageResource(R.drawable.wellcome);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchActivity();
            }
        });

    }
    public void SwitchActivity(){
        List<Person> persons = MyApplication.getInstance().persons;
        if (persons.size() != 0) {
            Intent intent= new Intent(this, HeartActivity.class);
            startActivity(intent);
        } else {
            Intent intent= new Intent(this, SettingActivity.class);
            Bundle args = new Bundle();
            args.putBoolean("isFirstTime", true);
            intent.putExtras(args);
            startActivity(intent);
        }
    }
}
