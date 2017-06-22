package com.mobile.ht.bluetoothnotifier.setting;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mobile.ht.bluetoothnotifier.R;

import java.util.ArrayList;

/**
 * Created by vantuegia on 6/22/2017.
 */

public class PersonDialog extends DialogFragment {

    private String name;
    private String phoneNumber;
    private int position;
    private static ArrayList<Person> persons;



    static PersonDialog newInstance(int position, String name, String phoneNumber, ArrayList<Person> persons) {
        PersonDialog.persons = persons;
        PersonDialog dialog = new PersonDialog();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("phoneNumber", phoneNumber);
        args.putInt("position", position);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        name = getArguments().getString("name");
        phoneNumber = getArguments().getString("phoneNumber");
        position = getArguments().getInt("position");
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
                persons.add(position, new Person(etName.getText().toString(), etPhoneNumber.getText().toString()));
                PersonDialog.this.dismiss();
            }
        });
        return view;

    }
}
