package com.mobile.ht.bluetoothnotifier.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.mobile.ht.bluetoothnotifier.R;
import com.mobile.ht.bluetoothnotifier.heart.HeartActivity;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    private ArrayList<Person> persons;
    private ListView lvPerson;
    private PersonAdapter personAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Test
        persons = new ArrayList<Person>(2);
        for (int i = 0; i < 2; i++) {
            persons.add(new Person("Person " + i, "031351321"));
        }

        lvPerson = (ListView) findViewById(R.id.lvPerson);
        personAdapter = new PersonAdapter(this, persons);
        lvPerson.setAdapter(personAdapter);

        //btn Add
        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, HeartActivity.class));
                Log.d("test", "test");
            }
        });

    }
}
