package com.mobile.ht.bluetoothnotifier.heart;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mobile.ht.bluetoothnotifier.MyApplication;
import com.mobile.ht.bluetoothnotifier.R;
import com.mobile.ht.bluetoothnotifier.setting.Person;
import com.mobile.ht.bluetoothnotifier.setting.SettingActivity;

import java.util.ArrayList;
import java.util.List;

public class HeartActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 10001;
    private static final int MY_SETTING_REQUEST = 10002;
    private static final String GOTO_SETTING_CALL_PHONE_PERMISSION = "didGoToSettingCallPhone";
    public TextView number, notice;
    public ImageView img;
    public int i;
    public List<Person> persons = MyApplication.getInstance().persons;
    public List<String> listNumber = new ArrayList<>();
    String status = "unknown";
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);
        // Request Call Phone permission
        checkCallPhonePermission();
        if (!MyApplication.getInstance().pref.getBoolean(GOTO_SETTING_CALL_PHONE_PERMISSION, false)) {
            guideToSettingCallPhonePermission();
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Toast.makeText(HeartActivity.this, "location: " + location.getLatitude() + location.getLongitude(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
        Map();
//        Check();
        for(int i=0;i<persons.size();i++){
            listNumber.add(persons.get(i).getPhoneNumber());
        }
    }
    public void Map(){
        img=(ImageView)findViewById(R.id.imgView);
        number=(TextView)findViewById(R.id.textViewnumber);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            number.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.images,0,0,0);
        }
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
                doCallAndSendMess();
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

    public void SendMessage(String number, String msg){
        Log.i("Send SMS", "");
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
        v.vibrate(1000);
    }
    PhoneStateListener phoneStateListener= new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Toast.makeText(HeartActivity.this, "CALL_STATE_IDLE", Toast.LENGTH_SHORT).show();
                    status="idle";
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Toast.makeText(HeartActivity.this, "CALL_STATE_RINGING", Toast.LENGTH_SHORT).show();
                    status="ringing";
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Toast.makeText(HeartActivity.this, "CALL_STATE_OFFHOOK", Toast.LENGTH_SHORT).show();
                    status="off-hook";
                    break;
            }
        }
    };
    private void PlaySound(){
        MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.sound);
        mediaPlayer.start();
    }

    public void doCallAndSendMess(){
      for(int i=0;i<listNumber.size();i++){
          Call(this,listNumber.get(i));
          if(status.equals("off-hook")){
              PlaySound();
              SendMessage(listNumber.get(i),"Msg");
              break;
          }else if(status.equals("idle")||status.equals("unknown")){
              Call(this,listNumber.get(i+1));
              SendMessage(listNumber.get(i),"Msg");
          }
      }
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

    private void guideToSettingCallPhonePermission() {
        new AlertDialog.Builder(this)
                .setTitle("Go to Setting to enable Call phone Permission")
                .setMessage("This app needs the Call phone permission, please accept to use Call functionality")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        goToSettings();
                        MyApplication.getInstance().pref.edit().putBoolean(GOTO_SETTING_CALL_PHONE_PERMISSION, true).commit();
                    }
                })
                .create()
                .show();
    }

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, MY_SETTING_REQUEST);
    }

    private void checkCallPhonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE )
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Call phone Permission Needed")
                        .setMessage("This app needs the Call phone permission, please accept to use Call functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(HeartActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED) {
                        // do the work
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
