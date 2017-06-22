package com.mobile.ht.bluetoothnotifier.setting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.ht.bluetoothnotifier.R;

import java.util.List;

/**
 * Created by vantuegia on 6/22/2017.
 */

public class PersonAdapter extends BaseAdapter {

    private Activity activity;
    private List<Person> persons;


    public PersonAdapter(Activity activity, List<Person> persons) {
        super();
        this.activity = activity;
        this.persons = persons;
    }

    @Override
    public int getCount() {
        return persons.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (null == convertView) {
            LayoutInflater inflater = activity.getLayoutInflater();
            v = inflater.inflate(R.layout.list_row_person, null);
        }

        TextView tvPersonName = (TextView) v.findViewById(R.id.tvPersonName);
        TextView tvPhoneNumber = (TextView) v.findViewById(R.id.tvPhoneNumber);

        // set person info
        tvPersonName.setText(persons.get(position).getName());
        tvPhoneNumber.setText(persons.get(position).getPhoneNumber());

        // btn Delete
//        Button btnDelete = (Button) v.findViewById(R.id.btnDelete);
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                persons.remove(position);
//                PersonAdapter.this.notifyDataSetChanged();
//            }
//        });


        return v;
    }

}
