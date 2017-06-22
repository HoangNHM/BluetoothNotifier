package com.mobile.ht.bluetoothnotifier;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.ht.bluetoothnotifier.setting.Person;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vantuegia on 6/22/2017.
 */

public class MyApplication extends Application {

    public static final String PREF_FILE_NAME = "bluetooth_notifier_pref_file";

    private static MyApplication singleton;

    public List<Person> persons;
    public SharedPreferences pref;


    public static MyApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        pref = this.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);

        persons = new ArrayList<Person>();
        Gson gson = new Gson();
        String json = pref.getString("persons", "");
        if (!"".equals(json)) {
            Type type = new TypeToken<List<Person>>(){}.getType();
            persons = gson.fromJson(json, type);
        }

    }

}
