package com.mobile.ht.bluetoothnotifier.heart;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mobile.ht.bluetoothnotifier.MyApplication;
import com.mobile.ht.bluetoothnotifier.R;
import com.mobile.ht.bluetoothnotifier.setting.Person;
import com.mobile.ht.bluetoothnotifier.setting.SettingActivity;

import java.util.ArrayList;
import java.util.List;

public class HeartActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 10000;
    private static final int MY_SETTING_REQUEST = 10001;
    private static final String GOTO_SETTING_CALL_PHONE_PERMISSION = "didGoToSettingCallPhone";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 10002;
    public TextView number, notice;
    private Button btIsActive;
    public ImageView img;
    public int i;
    public List<Person> persons = MyApplication.getInstance().persons;
    public List<String> listNumber = new ArrayList<>();
    String status = "unknown";
    private FusedLocationProviderClient mFusedLocationClient;
    LatLng latLng;
    PulseManager pulseManager = new PulseManager() {
        @Override
        protected void alert() {
            for (Person person : persons) {
                notice.setText("Step 1: Dial 115 immediately \n" +
                        "Step 2: Before ambulance arrive, put nitroglycerin under patient's tongue, keep the patient in half-sitting position\n"+
                        "Step 3: Perform artificial respiration\n"+
                        "Step 4: While emergency arrive, ... \n");
                String gMapLink = String.format("http://maps.google.com/?q=%s,%s", latLng.latitude, latLng.longitude);
                SendMessage(person.getPhoneNumber(), "SOS at: " + (null!= latLng ? gMapLink : "unknown location"));
                Call(HeartActivity.this, person.getPhoneNumber());
            }
        }

        @Override
        protected void hight() {
            notice.setText("Take pills and go to doctor");
            SoundandVibrate();
        }

        @Override
        protected void normal() {}
    };

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;


    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private EditText mOutEditText;
    private Button mSendButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the chat services
    private BluetoothChatService mChatService = null;

//    private MessageAdapter mAdapter;

    public int counter = 0;

    private List messageList = new ArrayList();

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
//                    mAdapter.notifyDataSetChanged();
//                    messageList.add(new androidRecyclerView.Message(counter++, writeMessage, "Me"));
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    String[] mess = readMessage.split(";");
                    String h = mess[0].split(":")[1];
                    String a = mess[1].split(":")[1];
                    number.setText(h);
                    btIsActive.setText(a);
                    pulseManager.changeState(Integer.parseInt(h), a);
//                    mAdapter.notifyDataSetChanged();
//                    messageList.add(new androidRecyclerView.Message(counter++, readMessage, mConnectedDeviceName));
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, "User did not enable Bluetooth or an error occured", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    public void connect(View v) {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    public void discoverable(View v) {
        ensureDiscoverable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);

        // Location get current
        getLocation();

        // Bluetooth
        initBluetooth();

        Map();
        SeekBar sbMan = (SeekBar) findViewById(R.id.sbMan);
        sbMan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                number.setText(String.valueOf(progress));
                pulseManager.changeState(progress, btIsActive.getText().toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        // Call
//        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        // Request Call Phone permission
        checkCallPhonePermission();

//        Check();
        for(int i=0;i<persons.size();i++){
            listNumber.add(persons.get(i).getPhoneNumber());
        }
    }
    public void Map(){
        btIsActive = (Button) findViewById(R.id.btIsActive);
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
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
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

    private void PlaySound(){
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sound);
        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        final int originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
            }
        });
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

    private void getLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(HeartActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            }
                        }
                    });
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
        if (!MyApplication.getInstance().pref.getBoolean(GOTO_SETTING_CALL_PHONE_PERMISSION, false)) {
            guideToSettingCallPhonePermission();
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

            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        // Got last known location. In some rare situations this can be null.
                                        if (location != null) {
                                            latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        }
                                    }
                                });
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    public void isActive(View view) {
        String text = ((Button) view).getText().toString();
        if ("0".equalsIgnoreCase(text)) {
            ((Button) view).setText("1");
        } else {
            ((Button) view).setText("0");
        }
    }

    /*PhoneStateListener phoneStateListener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
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
            super.onCallStateChanged(state, incomingNumber);
        }
    };*/

}
