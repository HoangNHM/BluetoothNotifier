package com.mobile.ht.bluetoothnotifier.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mobile.ht.bluetoothnotifier.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vantuegia on 6/22/2017.
 */

public class PersonDialog extends DialogFragment {

    private String name;
    private String phoneNumber;
    private int position;
    private ArrayList<Person> persons;
    private static PersonAdapter personAdapter;

    static PersonDialog newInstance(PersonAdapter personAdapter, int position, ArrayList<Person> persons) {
        PersonDialog.personAdapter = personAdapter;
        PersonDialog dialog = new PersonDialog();
        Bundle args = new Bundle();
        args.putString("name", persons.get(position).getName());
        args.putString("phoneNumber", persons.get(position).getPhoneNumber());
        args.putInt("position", position);
        args.putParcelableArrayList("persons", persons);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        name = getArguments().getString("name");
        phoneNumber = getArguments().getString("phoneNumber");
        position = getArguments().getInt("position");
        persons = getArguments().getParcelableArrayList("persons");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_person, container, false);

        final EditText etName = (EditText) view.findViewById(R.id.etName);
        final EditText etPhoneNumber = (EditText) view.findViewById(R.id.etPhoneNumber);
        etName.setText(name);
        etPhoneNumber.setText(phoneNumber);

        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persons.set(position, new Person(etName.getText().toString(), etPhoneNumber.getText().toString()));
                PersonDialog.this.dismiss();
                personAdapter.notifyDataSetChanged();

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }
}
