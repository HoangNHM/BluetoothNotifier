package com.mobile.ht.bluetoothnotifier.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mobile.ht.bluetoothnotifier.R;
import com.mobile.ht.bluetoothnotifier.heart.HeartActivity;

import java.util.ArrayList;

/**
 * Created by vantuegia on 6/22/2017.
 */

public class PersonAdapter extends BaseAdapter {

    private static LayoutInflater inflater;
    private Activity activity;
    private ArrayList<Person> persons;


    public PersonAdapter(Activity activity, ArrayList<Person> persons) {
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.persons = persons;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (null == convertView) {
            v = inflater.inflate(R.layout.list_row_person, null);
        }

        TextView tvPersonName = (TextView) v.findViewById(R.id.tvPersonName);
        TextView tvPhoneNumber = (TextView) v.findViewById(R.id.tvPhoneNumber);

        // set person info
        tvPersonName.setText(persons.get(position).getName());
        tvPhoneNumber.setText(persons.get(position).getPhoneNumber());



        return v;
    }

}
