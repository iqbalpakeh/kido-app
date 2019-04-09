package com.progremastudio.kido;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.progremastudio.kido.core.ActivityHome;
import com.progremastudio.kido.espressohelper.TestUtils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.progremastudio.kido.espressohelper.DurationPickerTools.pressDurationPicker;
import static com.progremastudio.kido.espressohelper.DurationPickerTools.pressOkOnDurationPicker;
import static com.progremastudio.kido.espressohelper.TestUtils.withRecyclerView;

@RunWith(AndroidJUnit4.class) @LargeTest public class TestModuleSleeping {

    @Rule public ActivityTestRule<ActivityHome> mActivityRule = new ActivityTestRule<>(ActivityHome.class);

    @Test public void test_create_night_sleep_normal() {

        //------------------------------------------------------------------------
        // do:
        // Create Night sleep = 09h 09m 09s

        // check:
        // Total Night Duration = 09h 09m 09s
        // Night Duration = 09h 09m 09s
        // Sleep type = Night sleep

        // do:
        // Create Night sleep = 01h 01m 01s

        // check:
        // Total Night Duration = 10h 10m 10s
        // Night Duration = 01h 01m 01s
        // Sleep type = Night sleep

        // Delete all entries
        //------------------------------------------------------------------------

        doOpenSleepFragment();
        checkTotalNightDurationEmpty();
        checkTotalDayDurationEmpty();

        doCreateNightSleepEntry("09h 09m 09s");
        checkTotalNightDuration("09h 09m 09s");
        checkNightSleepDuration("09h 09m 09s");
        checkNightSleepType();
        checkTotalDayDurationEmpty();

        doCreateNightSleepEntry("01h 01m 01s");
        checkTotalNightDuration("10h 10m 10s");
        checkNightSleepDuration("01h 01m 01s");
        checkNightSleepType();
        checkTotalDayDurationEmpty();

        doDeleteFirstEntry();
        doDeleteFirstEntry();
    }

    @Test public void test_create_day_sleep_normal() {

        //------------------------------------------------------------------------
        // do:
        // Create Day sleep = 09h 09m 09s

        // check:
        // Total Day Duration = 09h 09m 09s
        // Day Duration = 09h 09m 09s
        // Sleep type = Day sleep

        // do:
        // Create Day sleep = 01h 01m 01s

        // check:
        // Total Day Duration = 10h 10m 10s
        // Day Duration = 01h 01m 01s
        // Sleep type = Day sleep

        // Delete all entries
        //------------------------------------------------------------------------

        doOpenSleepFragment();
        checkTotalDayDurationEmpty();
        checkTotalNightDurationEmpty();

        doCreateDaySleepEntry("09h 09m 09s");
        checkTotalDayDuration("09h 09m 09s");
        checkDaySleepDuration("09h 09m 09s");
        checkDaySleepType();
        checkTotalNightDurationEmpty();

        doCreateDaySleepEntry("01h 01m 01s");
        checkTotalDayDuration("10h 10m 10s");
        checkDaySleepDuration("01h 01m 01s");
        checkDaySleepType();
        checkTotalNightDurationEmpty();

        doDeleteFirstEntry();
        doDeleteFirstEntry();
    }

    private void doOpenSleepFragment() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText(R.string.str_Sleep)).perform(click());
    }

    private void doCreateNightSleepEntry(String duration) {
        onView(withId(R.id.fab_sleep)).perform(click());
        onView(withId(R.id.fab_sleep)).perform(click());
        onView(withId(R.id.night_sleep)).perform(click());
        pressDurationPicker(duration);
        pressOkOnDurationPicker();
    }

    private void doCreateDaySleepEntry(String duration) {
        onView(withId(R.id.fab_sleep)).perform(click());
        onView(withId(R.id.fab_sleep)).perform(click());
        onView(withId(R.id.day_sleep)).perform(click());
        pressDurationPicker(duration);
        pressOkOnDurationPicker();
    }

    private void checkTotalNightDuration(String duration) {
        String text = "Total duration " + duration;
        onView(withId(R.id.night_sleep_total)).check(matches(withText(text)));
    }

    private void checkNightSleepDuration(String duration) {
        String text = "Duration " + duration;
        onView(withRecyclerView(R.id.sleep_recycler_view)
                .atPositionOnView(1, R.id.sleep_duration)).check(matches(withText(text)));
    }

    private void checkTotalNightDurationEmpty() {
        String text = "No activity";
        onView(withId(R.id.night_sleep_total)).check(matches(withText(text)));
    }

    private void checkNightSleepType() {
        String text = "Night sleep";
        onView(withRecyclerView(R.id.sleep_recycler_view)
                .atPositionOnView(1, R.id.sleep_type)).check(matches(withText(text)));
    }

    private void checkTotalDayDuration(String duration) {
        String text = "Total duration " + duration;
        onView(withId(R.id.day_sleep_total)).check(matches(withText(text)));
    }

    private void checkTotalDayDurationEmpty() {
        String text = "No activity";
        onView(withId(R.id.day_sleep_total)).check(matches(withText(text)));
    }

    private void checkDaySleepDuration(String duration) {
        String text = "Duration " + duration;
        onView(withRecyclerView(R.id.sleep_recycler_view)
                .atPositionOnView(1, R.id.sleep_duration)).check(matches(withText(text)));
    }

    private void checkDaySleepType() {
        String text = "Day sleep";
        onView(withRecyclerView(R.id.sleep_recycler_view)
                .atPositionOnView(1, R.id.sleep_type)).check(matches(withText(text)));
    }

    private void doDeleteFirstEntry() {
        /**
         * Always delete from child number 1. Please note that RecyclerView only have 5 child max.
         * It reuse the children for optimization.
         */
        onView(withId(R.id.sleep_recycler_view))
                .perform(TestUtils.actionOnItemViewAtPosition(1, R.id.sleep_entry_delete_debug, click()));
    }

}
