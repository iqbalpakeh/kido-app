/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.vaccine;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.progremastudio.kido.R;
import com.progremastudio.kido.module.timeline.TimeLineFragmentInterface;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.util.TextFormation;
import com.progremastudio.kido.widget.AlarmManagerHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class VaccineDialog extends Fragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Button timestamp;
    private EditText name;
    private EditText location;
    private EditText notes;
    private Button reminderSwitch;
    private Button dateReminder;
    private Button timeReminder;
    private ImageButton reminderReset;
    private Calendar timestampCalendar;
    private CalendarReminder calendarReminder;
    private ImageButton add;
    private ImageButton cancel;

    public static VaccineDialog getInstance() {
        return new VaccineDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_vaccine, container, false);
        prepareHandler(root);
        return root;
    }

    public void prepareHandler(View root) {
        Calendar now = Calendar.getInstance();
        calendarReminder = new CalendarReminder();
        timestampCalendar = Calendar.getInstance();
        timestamp = (Button) root.findViewById(R.id.vaccine_timestamp_button);
        name = (EditText) root.findViewById(R.id.vaccine_name);
        location = (EditText) root.findViewById(R.id.vaccine_location);
        notes = (EditText) root.findViewById(R.id.vaccine_notes);
        dateReminder = (Button) root.findViewById(R.id.vaccine_reminder_date_button);
        timeReminder = (Button) root.findViewById(R.id.vaccine_reminder_time_button);
        reminderReset = (ImageButton) root.findViewById(R.id.vaccine_reminder_clear_button);
        reminderSwitch = (Button) root.findViewById(R.id.vaccine_reminder_trigger_button);
        add = (ImageButton) root.findViewById(R.id.add);
        cancel = (ImageButton) root.findViewById(R.id.cancel);
        timestamp.setText(TextFormation.dateComplete(getActivity(), String.valueOf(now.getTimeInMillis())));
        timestamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(true);
            }
        });
        dateReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(false);
            }
        });
        timeReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
        reminderReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetReminder();
            }
        });
        reminderSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateReminder();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeVaccineEntry();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toTimelineFragment();
            }
        });
    }

    private void activateReminder() {
        if (reminderSwitch.getVisibility() == View.VISIBLE) {
            calendarReminder.clearCalendar();
            dateReminder.setText(getResources().getString(R.string.str_Date));
            timeReminder.setText(getResources().getString(R.string.str_Time));
            reminderSwitch.setVisibility(View.GONE);
            dateReminder.setVisibility(View.VISIBLE);
            timeReminder.setVisibility(View.VISIBLE);
            reminderReset.setVisibility(View.VISIBLE);
        }
    }

    private void resetReminder() {
        if (reminderReset.getVisibility() == View.VISIBLE) {
            calendarReminder.clearCalendar();
            dateReminder.setText(getResources().getString(R.string.str_Date));
            timeReminder.setText(getResources().getString(R.string.str_Time));
            reminderReset.setVisibility(View.GONE);
            dateReminder.setVisibility(View.GONE);
            timeReminder.setVisibility(View.GONE);
            reminderSwitch.setVisibility(View.VISIBLE);
        }
    }

    private void showDatePicker(boolean isTimestampHandler) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
        if (isTimestampHandler) {
            dialog.getDatePicker().setTag("TAG_TIMESTAMP");
        } else {
            dialog.getDatePicker().setTag("TAG_REMINDER");
        }
        dialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, 12, 00, true);
        dialog.show();
    }

    private void toTimelineFragment() {
        TimeLineFragmentInterface fragment = TimeLineFragmentInterface.getInstance();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .commit();
    }

    public void storeVaccineEntry() {
        Bundle bundle = getArguments();
        if (bundle.getString("CREATE_OR_EDIT").equals("EDIT")) {
            VaccineModel model = new VaccineModel();
            model.setActivityId(Long.valueOf(bundle.getString("TAG_ACTIVITY")));
            model.setStringTimeStamp(String.valueOf(timestampCalendar.getTimeInMillis()));
            model.setName(name.getText().toString());
            model.setLocation(location.getText().toString());
            model.setNotes(notes.getText().toString());
            model.setReminder(calendarReminder.getStringTimeInMillis());
            model.edit(getActivity());
        } else if (bundle.getString("CREATE_OR_EDIT").equals("CREATE")) {
            VaccineModel model = new VaccineModel();
            model.setBabyID(ActiveContext.getActiveBaby(getActivity()).getActivityId());
            model.setFamilyId(ActiveContext.getActiveBaby(getActivity()).getFamilyId());
            model.setTimeStamp(String.valueOf(timestampCalendar.getTimeInMillis()));
            model.setName(name.getText().toString());
            model.setLocation(location.getText().toString());
            model.setReminder(calendarReminder.getStringTimeInMillis());
            model.setNotes(notes.getText().toString());
            model.insert(getActivity());
        }
        if (!calendarReminder.isCalendarNotSet()) AlarmManagerHelper.setAlarms(getActivity());
        else Toast.makeText(getActivity(), getString(R.string.str_Reminder_is_not_set), Toast.LENGTH_LONG).show();
        toVaccineFragment();
    }

    private void toVaccineFragment() {
        ActiveContext.setCurrentFragment(getActivity(), getString(R.string.str_Vaccine));
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ActiveContext.setCurrentFragment(getActivity(), getString(R.string.str_Vaccine));
        fragmentManager
                .beginTransaction()
                .replace(R.id.home_activity_container, VaccineFragment.getInstance(), getString(R.string.str_Vaccine))
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .commit();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (view.getTag().equals("TAG_TIMESTAMP")) {
            setTimestamp(year, monthOfYear, dayOfMonth);
        } else {
            setReminder(year, monthOfYear, dayOfMonth);
            reminderReset.setVisibility(View.VISIBLE);
        }
    }

    private void setTimestamp(int year, int monthOfYear, int dayOfMonth) {
        timestampCalendar.set(year, monthOfYear, dayOfMonth);
        timestamp.setText(TextFormation.dateComplete(getActivity(),
                String.valueOf(timestampCalendar.getTimeInMillis())));
    }

    private void setReminder(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        calendarReminder.setYear(year);
        calendarReminder.setMonth(monthOfYear);
        calendarReminder.setDay(dayOfMonth);
        dateReminder.setText(TextFormation.date(getActivity(),
                String.valueOf(calendar.getTimeInMillis())));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeReminder.setText(betterTimeFormat(hourOfDay) + ":" + betterTimeFormat(minute));
        calendarReminder.setHour(hourOfDay);
        calendarReminder.setMinute(minute);
        reminderReset.setVisibility(View.VISIBLE);
    }

    private String betterTimeFormat(int input) {
        if (input < 10) {
            return "0" + input;
        }
        return String.valueOf(input);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        if (bundle.getString("CREATE_OR_EDIT").equals("EDIT")) {
            activateReminder();
            fillHandler(bundle);
        }
    }

    private void fillHandler(Bundle bundle) {
        VaccineModel model = VaccineModel.getVaccineModel(getActivity(), bundle.getString("TAG_ACTIVITY"));
        timestamp.setText(TextFormation.dateComplete(getActivity(), model.getStringTimeStamp()));
        name.setText(model.getName());
        location.setText(model.getLocation());
        notes.setText(model.getNotes());
        if (!model.getReminder().equals("ALARM_NOT_SET")) {
            calendarReminder.setReminder(model.getReminder());
            dateReminder.setText(TextFormation.date(getActivity(), model.getReminder()));
            timeReminder.setText(getTimeFormat(model.getReminder()));
        }
    }

    private String getTimeFormat(String timeInMs) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(timeInMs));
        return (new SimpleDateFormat("HH:mm").format(calendar.getTime()));
    }

    private class CalendarReminder {

        private int year;
        private int month;
        private int day;
        private int hour;
        private int minute;

        public CalendarReminder() {
            clearCalendar();
        }

        public void setYear(int year) {
            this.year = year;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public void clearCalendar() {
            year = 0;
            month = 0;
            day = 0;
            hour = 0;
            minute = 0;
        }

        public boolean isCalendarNotSet() {
            // if either date picker or time picker is not set, than the alarm is not activate
            return (((year == 0) & (month == 0) & (day == 0)) || ((hour == 0) & (minute == 0)));
        }

        public Long getTimeInMillis() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute);
            return calendar.getTimeInMillis();
        }

        public String getStringTimeInMillis() {
            if (isCalendarNotSet()) {
                return "ALARM_NOT_SET";
            } else {
                return String.valueOf(getTimeInMillis());
            }
        }

        public void setReminder(String reminder) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(reminder));
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DATE);
        }
    }
}
