package com.mobile.ht.bluetoothnotifier;

import android.app.Application;

import com.mobile.ht.bluetoothnotifier.setting.Person;

import java.util.ArrayList;

/**
 * Created by vantuegia on 6/22/2017.
 */

public class MyApplication extends Application {

    private static MyApplication singleton;
    ArrayList<Person> persons;

    public static MyApplication getInstance(){
        return singleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        persons = new ArrayList<Person>(2);
        for (int i = 0; i < 2; i++) {
            persons.add(new Person("Person " + i, "031351321"));
        }
    }


}
