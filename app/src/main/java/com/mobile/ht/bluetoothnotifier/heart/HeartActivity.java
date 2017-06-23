package com.mobile.ht.bluetoothnotifier.heart;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.ht.bluetoothnotifier.R;
import com.mobile.ht.bluetoothnotifier.setting.Person;
import com.mobile.ht.bluetoothnotifier.setting.SettingActivity;

import java.util.ArrayList;
import java.util.List;

public class HeartActivity extends AppCompatActivity {
    public TextView number, notice;
    public ImageView img;
    public int i;
    public List<Person> persons;
    public List<String> listNumber;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        Map();
        Check();
        for(int i=0;i<persons.size();i++){
            listNumber.add(persons.get(i).getPhoneNumber());
        }
    }
    public void Map(){
        img=(ImageView)findViewById(R.id.imgView);
        number=(TextView)findViewById(R.id.textViewnumber);
        number.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.images,0,0,0);
        notice=(TextView)findViewById(R.id.textViewNotice);
    }
    public void Check(){
        i=Integer.parseInt(number.getText().toString());
        if(i>90 && i<=100){
            notice.setText("Take pills and go to doctor");
        }else if(i>100){
            notice.setText("Step 1: Dial 115 immediately \n" +
                           "Step 2: Before ambulance arrive, put nitroglycerin under patient's tongue, keep the patient in half-sitting position\n"+
                           "Step 3: Perform artificial respiration\n"+
                           "Step 4: While emergency arrive, \n");

        }
    }
    public void Call(Context context,String num){
        if(!TextUtils.isEmpty(num)){
            String dial = "tel:" + num;
            Intent intent =new Intent(Intent.ACTION_CALL, Uri.parse(dial));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            context.startActivity(intent);
        }else {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }

    public void SendMessage(String number){
        Log.i("Send SMS", "");
        String msg= "MSG";
        try {
            Intent intent=new Intent(getApplicationContext(),HeartActivity.class);
            PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, msg, pi, null);
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
        v.vibrate(5000);
    }
    PhoneStateListener phoneStateListener= new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Toast.makeText(HeartActivity.this, "CALL_STATE_IDLE", Toast.LENGTH_SHORT).show();
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Toast.makeText(HeartActivity.this, "CALL_STATE_RINGING", Toast.LENGTH_SHORT).show();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Toast.makeText(HeartActivity.this, "CALL_STATE_OFFHOOK", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

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
