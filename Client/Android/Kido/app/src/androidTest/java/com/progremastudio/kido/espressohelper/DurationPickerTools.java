package com.progremastudio.kido.espressohelper;

import com.progremastudio.kido.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class DurationPickerTools {

    final static int POS_HOURS_TENS = 0;
    final static int POS_HOURS_UNIT = POS_HOURS_TENS + 1;
    final static int POS_MINUTES_TENS = POS_HOURS_UNIT + 3;
    final static int POS_MINUTES_UNIT = POS_MINUTES_TENS + 1;
    final static int POS_SECOND_TENS = POS_MINUTES_UNIT + 3;
    final static int POS_SECOND_UNIT = POS_SECOND_TENS + 1;

    public static void pressDurationPicker(String time) {
        /**
         * Time Format HHh MMm SSs
         */
        pressNumberOnDurationPicker(time.charAt(POS_HOURS_TENS));
        pressNumberOnDurationPicker(time.charAt(POS_HOURS_UNIT));
        pressNumberOnDurationPicker(time.charAt(POS_MINUTES_TENS));
        pressNumberOnDurationPicker(time.charAt(POS_MINUTES_UNIT));
        pressNumberOnDurationPicker(time.charAt(POS_SECOND_TENS));
        pressNumberOnDurationPicker(time.charAt(POS_SECOND_UNIT));
    }

    public static void pressOkOnDurationPicker() {
        onView(withId(R.id.digit_ok)).perform(click());
    }

    private static void pressNumberOnDurationPicker(char number) {
        if(number == '0') {
            onView(withId(R.id.digit_zero)).perform(click());
        } else if(number == '1') {
            onView(withId(R.id.digit_one)).perform(click());
        } else if(number == '2') {
            onView(withId(R.id.digit_two)).perform(click());
        } else if(number == '3') {
            onView(withId(R.id.digit_three)).perform(click());
        } else if(number == '4') {
            onView(withId(R.id.digit_four)).perform(click());
        } else if(number == '5') {
            onView(withId(R.id.digit_five)).perform(click());
        } else if(number == '6') {
            onView(withId(R.id.digit_six)).perform(click());
        } else if(number == '7') {
            onView(withId(R.id.digit_seven)).perform(click());
        } else if(number == '8') {
            onView(withId(R.id.digit_eight)).perform(click());
        } else if(number == '9') {
            onView(withId(R.id.digit_nine)).perform(click());
        }
    }

}
