/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.widget;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.progremastudio.kido.R;

import java.util.concurrent.TimeUnit;

public class DurationPicker {

    private final int stateHoursTens = 0;
    private final int stateHoursUnit = stateHoursTens + 1;
    private final int stateMinuteTens = stateHoursUnit + 1;
    private final int stateMinuteUnit = stateMinuteTens + 1;
    private final int stateSecondTens = stateMinuteUnit + 1;
    private final int stateSecondUnit = stateSecondTens + 1;
    private final int stateFinish = stateSecondUnit + 1;
    private Callback callback;
    private Context context;
    private Button zeroHandler;
    private Button oneHandler;
    private Button twoHandler;
    private Button threeHandler;
    private Button fourHandler;
    private Button fiveHandler;
    private Button sixHandler;
    private Button sevenHandler;
    private Button eightHandler;
    private Button nineHandler;
    private Button okHandler;
    private Button deleteHandler;
    private TextView durationValueHandler;
    private TextView titleHandler;
    private String hoursTens;
    private String hoursUnit;
    private String minutesTens;
    private String minutesUnit;
    private String secondTens;
    private String secondUnit;
    private int state;

    public DurationPicker(Context context, View root) {
        this.context = context;
        state = stateHoursTens;
        hoursTens = "-";
        hoursUnit = "-";
        minutesTens = "-";
        minutesUnit = "-";
        secondTens = "-";
        secondUnit = "-";
        prepareHandler(root);
    }

    public void setTitle(String title) {
        titleHandler.setText(title);
    }

    private void prepareHandler(View root) {
        durationValueHandler = (TextView) root.findViewById(R.id.duration_value);
        titleHandler = (TextView) root.findViewById(R.id.duration_title);
        zeroHandler = (Button) root.findViewById(R.id.digit_zero);
        zeroHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDuration("0", false);
            }
        });
        oneHandler = (Button) root.findViewById(R.id.digit_one);
        oneHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDuration("1", false);
            }
        });
        twoHandler = (Button) root.findViewById(R.id.digit_two);
        twoHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDuration("2", false);
            }
        });
        threeHandler = (Button) root.findViewById(R.id.digit_three);
        threeHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDuration("3", false);
            }
        });
        fourHandler = (Button) root.findViewById(R.id.digit_four);
        fourHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDuration("4", false);
            }
        });
        fiveHandler = (Button) root.findViewById(R.id.digit_five);
        fiveHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDuration("5", false);
            }
        });
        sixHandler = (Button) root.findViewById(R.id.digit_six);
        sixHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDuration("6", false);
            }
        });
        sevenHandler = (Button) root.findViewById(R.id.digit_seven);
        sevenHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDuration("7", false);
            }
        });
        eightHandler = (Button) root.findViewById(R.id.digit_eight);
        eightHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDuration("8", false);
            }
        });
        nineHandler = (Button) root.findViewById(R.id.digit_nine);
        nineHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDuration("9", false);
            }
        });
        deleteHandler = (Button) root.findViewById(R.id.digit_delete);
        deleteHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDuration("-", true);
            }
        });
        okHandler = (Button) root.findViewById(R.id.digit_ok);
        okHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDigitComplete()) {
                    Intent intent = new Intent();
                    intent.putExtra("duration", getDuration());
                    callback.onDurationPickerOkClicked(intent);
                }
            }
        });
    }

    private void buildDuration(String digitValue, boolean isDelete) {
        if (isDelete && (state != 0)) state--;
        switch (state) {
            case stateHoursTens:
                hoursTens = digitValue;
                if (!isDelete) state = stateHoursUnit;
                break;
            case stateHoursUnit:
                hoursUnit = digitValue;
                if (!isDelete) state = stateMinuteTens;
                break;
            case stateMinuteTens:
                if (isDigitAllow(digitValue)) {
                    minutesTens = digitValue;
                    if (!isDelete) state = stateMinuteUnit;
                }
                break;
            case stateMinuteUnit:
                minutesUnit = digitValue;
                if (!isDelete) state = stateSecondTens;
                break;
            case stateSecondTens:
                if (isDigitAllow(digitValue)) {
                    secondTens = digitValue;
                    if (!isDelete) state = stateSecondUnit;
                }
                break;
            case stateSecondUnit:
                secondUnit = digitValue;
                if (!isDelete) state = stateFinish;
                break;
        }
        durationValueHandler.setText((hoursTens + hoursUnit + "h ") + (minutesTens + minutesUnit + "m ") + (secondTens + secondUnit + "s"));
    }

    private long getDuration() {
        String value = durationValueHandler.getText().toString();
        return TimeUnit.SECONDS.toMillis(getSecond(value) + (getMinute(value) * 60) + (getHour(value) * 3600));
    }

    private boolean isDigitComplete() {
        if (hoursTens.equals("-") || hoursUnit.equals("-") || minutesTens.equals("-") || minutesUnit.equals("-") || secondTens.equals("-") || secondUnit.equals("-")) {
            (Toast.makeText(this.context, this.context.getString(R.string.str_Digit_is_not_completed), Toast.LENGTH_LONG)).show();
            return false;
        }
        return true;
    }

    private int getSecond(String value) {
        return Integer.parseInt(value.substring(8, 10));
    }

    private int getMinute(String value) {
        return Integer.parseInt(value.substring(4, 6));
    }

    private int getHour(String value) {
        return Integer.parseInt(value.substring(0, 2));
    }

    private boolean isDigitAllow(String digitValue) {
        if (!digitValue.equals("-")) {
            if (Integer.parseInt(digitValue) > 5) {
                (Toast.makeText(this.context, this.context.getString(R.string.str_Invalid_digit_entered), Toast.LENGTH_LONG)).show();
                return false;
            }
        }
        return true;
    }

    public void setCallback(Callback listener) {
        callback = listener;
    }

    public interface Callback {
        void onDurationPickerOkClicked(Intent intent);
    }

}

