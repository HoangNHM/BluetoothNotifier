package com.mobile.ht.bluetoothnotifier.heart;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.ht.bluetoothnotifier.R;
import com.mobile.ht.bluetoothnotifier.setting.Person;
import com.mobile.ht.bluetoothnotifier.setting.SettingActivity;

import java.util.ArrayList;
import java.util.List;

public class HeartActivity extends AppCompatActivity {
    public TextView number, notice;
    public int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        Map();
        Check();
    }
    public void Map(){
        number=(TextView)findViewById(R.id.textViewnumber);
        notice=(TextView)findViewById(R.id.textViewNotice);
    }
    public void Check(){
        i=Integer.parseInt(number.getText().toString());
        if(i>90 && i<=100){
            notice.setText("Take pills and go to doctor");
        }else if(i>100){
            notice.setText("Step 1: Call 115 \n" +
                           "Step 2: \n"+
                           "Step 3: \n"+
                           "Step 4: \n");
        }
    }
    public void Call(String num){
        if(!TextUtils.isEmpty(num)){
            String dial = "tel:" + num;
            Intent intent =new Intent(Intent.ACTION_CALL, Uri.parse(dial));
            try{
                startActivity(intent);
            }

            catch (android.content.ActivityNotFoundException ex){
                Toast.makeText(getApplicationContext(),"yourActivity is not founded",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }

    public void SendMessage(String number){
        Log.i("Send SMS", "");
        String msg= "MSG";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent!", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void SoundandVibrate(){
        MediaPlayer media= MediaPlayer.create(HeartActivity.this, R.raw.sound);
        media.start();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                Intent intent= new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
