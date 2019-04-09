/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.disease;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.progremastudio.kido.R;
import com.progremastudio.kido.module.timeline.TimeLineFragmentInterface;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.util.TextFormation;

import java.util.Calendar;

public class DiseaseDialog extends Fragment implements DatePickerDialog.OnDateSetListener {

    private TextView timestampHandler;
    private EditText nameHandler;
    private EditText symptomHandler;
    private EditText treatmentHandler;
    private EditText notesHandler;
    private Calendar timestampCalendar;
    private ImageButton add;
    private ImageButton cancel;

    public static DiseaseDialog getInstance() {
        return new DiseaseDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_disease, container, false);
        prepareHandler(root);
        return root;
    }

    private void storeDiseaseEntry() {
        Bundle bundle = getArguments();
        if (bundle.getString("CREATE_OR_EDIT").equals("EDIT")) {
            DiseaseModel disease = new DiseaseModel();
            disease.setActivityId(Long.valueOf(bundle.getString("TAG_ACTIVITY")));
            disease.setStringTimeStamp(String.valueOf(timestampCalendar.getTimeInMillis()));
            disease.setName(nameHandler.getText().toString());
            disease.setSymptom(symptomHandler.getText().toString());
            disease.setTreatment(treatmentHandler.getText().toString());
            disease.setNotes(notesHandler.getText().toString());
            disease.edit(getActivity());
        } else if (bundle.getString("CREATE_OR_EDIT").equals("CREATE")) {
            DiseaseModel disease = new DiseaseModel();
            disease.setBabyID(ActiveContext.getActiveBaby(getActivity()).getActivityId());
            disease.setFamilyId(ActiveContext.getActiveBaby(getActivity()).getFamilyId());
            disease.setTimeStamp(String.valueOf(timestampCalendar.getTimeInMillis()));
            disease.setName(nameHandler.getText().toString());
            disease.setSymptom(symptomHandler.getText().toString());
            disease.setTreatment(treatmentHandler.getText().toString());
            disease.setNotes(notesHandler.getText().toString());
            disease.insert(getActivity());
        }
        goToFragment();
    }

    private void fillHandler(Bundle bundle) {
        DiseaseModel disease = DiseaseModel.getDiseaseModel(getActivity(), bundle.getString("TAG_ACTIVITY"));
        timestampHandler.setText(TextFormation.dateComplete(getActivity(), disease.getStringTimeStamp()));
        nameHandler.setText(disease.getName());
        symptomHandler.setText(disease.getSymptom());
        treatmentHandler.setText(disease.getTreatment());
        notesHandler.setText(disease.getNotes());
    }

    private void goToTimelineFragment() {
        TimeLineFragmentInterface fragment = TimeLineFragmentInterface.getInstance();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .commit();
    }

    private void goToFragment() {
        ActiveContext.setCurrentFragment(getActivity(), getString(R.string.str_Disease));
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ActiveContext.setCurrentFragment(getActivity(), getString(R.string.str_Disease));
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, DiseaseFragment.getInstance(), getString(R.string.str_Diaper))
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .commit();
    }

    private void prepareHandler(View root) {
        Calendar now = Calendar.getInstance();
        timestampCalendar = Calendar.getInstance();
        timestampHandler = (TextView) root.findViewById(R.id.disease_timestamp);
        timestampHandler.setText(TextFormation.dateComplete(getActivity(), String.valueOf(now.getTimeInMillis())));
        timestampHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        nameHandler = (EditText) root.findViewById(R.id.disease_name);
        symptomHandler = (EditText) root.findViewById(R.id.disease_symptom);
        treatmentHandler = (EditText) root.findViewById(R.id.disease_treatment);
        notesHandler = (EditText) root.findViewById(R.id.disease_notes);
        add = (ImageButton) root.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeDiseaseEntry();
            }
        });
        cancel = (ImageButton) root.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTimelineFragment();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        if (bundle.getString("CREATE_OR_EDIT").equals("EDIT")) {
            fillHandler(bundle);
        }
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        timestampCalendar.set(year, monthOfYear, dayOfMonth);
        timestampHandler.setText(TextFormation.dateComplete(getActivity(),
                String.valueOf(timestampCalendar.getTimeInMillis())));
    }
}
