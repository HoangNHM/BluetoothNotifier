package com.mobile.ht.bluetoothnotifier.heart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mobile.ht.bluetoothnotifier.R;

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
        if(i>90&& i<100){
            notice.setText("Message!");
        }else if(i>100){

        }
    }
}
