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
import static com.progremastudio.kido.espressohelper.TestUtils.withRecyclerView;

@RunWith(AndroidJUnit4.class) @LargeTest public class TestModuleDiaper {

    final static int NORMAL_TEST_NUMBER = 5;

    @Rule public ActivityTestRule<ActivityHome> mActivityRule = new ActivityTestRule<>(ActivityHome.class);

    @Test public void test_create_poo_normal() {

        //------------------------------------------------------------------------
        // do [5 times]:
        // Create poo

        // Check:
        // Diaper used x pieces
        // Details = Poo
        //------------------------------------------------------------------------

        int entryNumber = NORMAL_TEST_NUMBER;

        doOpenDiaperFragment();
        checkHeaderEmpty();

        for(int count=1; count<=entryNumber; count++) {
            doCreatePoo();
            checkPooCreation();
            checkDiaperUsedForPoo(count);
        }

        for(int count=1; count<=entryNumber; count++) {
            doDeleteFirstEntry();
            checkDiaperUsedForPoo(entryNumber-count);
        }

    }

    @Test public void test_create_pee_normal() {

        //------------------------------------------------------------------------
        // do [5 times]:
        // Create pee

        // Check:
        // Diaper used x pieces
        // Details = Pee
        //------------------------------------------------------------------------

        int entryNumber = NORMAL_TEST_NUMBER;

        doOpenDiaperFragment();
        checkHeaderEmpty();

        for(int count=1; count<=entryNumber; count++) {
            doCreatePee();
            checkPeeCreation();
            checkDiaperUsedForPee(count);
        }

        for(int count=1; count<=entryNumber; count++) {
            doDeleteFirstEntry();
            checkDiaperUsedForPee(entryNumber-count);
        }

    }

    @Test public void test_create_pee_and_poo_normal() {

        //------------------------------------------------------------------------
        // do [5 times]:
        // Create Pee and poo

        // Check:
        // Diaper used x pieces
        // Details = Pee and poo
        //------------------------------------------------------------------------

        int entryNumber = NORMAL_TEST_NUMBER;

        doOpenDiaperFragment();
        checkHeaderEmpty();

        for(int count=1; count<=entryNumber; count++) {
            doCreatePeeAndPoo();
            checkPeeAndPooCreation();
            checkDiaperUsedForPeeAndPoo(count);
        }

        for(int count=1; count<=entryNumber; count++) {
            doDeleteFirstEntry();
            checkDiaperUsedForPeeAndPoo(entryNumber-count);
        }
    }

    @Test public void test_edit_poo_to_pee() {

        //------------------------------------------------------------------------
        // do:
        // Create poo

        // check:
        // Poo => Diaper used 1 pieces
        // Poo => Details = Poo
        // Pee => No activity

        // do:
        // Edit Poo to Pee

        // check:
        // Poo => No activity
        // Pee => Diaper used 1 pieces
        // Pee => Details = Pee

        // delete all entries
        //------------------------------------------------------------------------

        doOpenDiaperFragment();
        checkHeaderEmpty();

        doCreatePoo();
        checkPooCreation();
        checkDiaperUsedForPoo(1);

        doEditToPee();
        checkDiaperUsedForPoo(0);
        checkPeeCreation();
        checkDiaperUsedForPee(1);

        doDeleteFirstEntry();

    }

    @Test public void test_edit_poo_to_mix() {

        //------------------------------------------------------------------------
        // do:
        // Create poo

        // check:
        // Poo => Diaper used 1 pieces
        // Poo => Details = Poo
        // Mix => No activity

        // do:
        // Edit Poo to Mix

        // check:
        // Poo => No activity
        // Mix => Diaper used 1 pieces
        // Mix => Details = Pee and poo

        // delete all entries
        //------------------------------------------------------------------------

        doOpenDiaperFragment();
        checkHeaderEmpty();

        doCreatePoo();
        checkPooCreation();
        checkDiaperUsedForPoo(1);

        doEditToMix();
        checkDiaperUsedForPoo(0);
        checkPeeAndPooCreation();
        checkDiaperUsedForPeeAndPoo(1);

        doDeleteFirstEntry();

    }

    @Test public void test_edit_pee_to_poo() {

        //------------------------------------------------------------------------
        // do:
        // Create Pee

        // check:
        // Pee => Diaper used 1 pieces
        // Pee => Details = Pee
        // Poo => No activity

        // do:
        // Edit Pee to Poo

        // check:
        // Pee => No activity
        // Poo => Diaper used 1 pieces
        // Poo => Details = Poo

        // delete all entries
        //------------------------------------------------------------------------

        doOpenDiaperFragment();
        checkHeaderEmpty();

        doCreatePee();
        checkPeeCreation();
        checkDiaperUsedForPee(1);

        doEditToPoo();
        checkDiaperUsedForPee(0);
        checkPooCreation();
        checkDiaperUsedForPoo(1);

        doDeleteFirstEntry();

    }

    @Test public void test_edit_pee_to_mix() {

        //------------------------------------------------------------------------
        // do:
        // Create Pee

        // check:
        // Pee => Diaper used 1 pieces
        // Pee => Details = Pee
        // Mix => No activity

        // do:
        // Edit Pee to Mix

        // check:
        // Pee => No activity
        // Mix => Diaper used 1 pieces
        // Mix => Details = Pee and poo

        // delete all entries
        //------------------------------------------------------------------------

        doOpenDiaperFragment();
        checkHeaderEmpty();

        doCreatePee();
        checkPeeCreation();
        checkDiaperUsedForPee(1);

        doEditToMix();
        checkDiaperUsedForPee(0);
        checkPeeAndPooCreation();
        checkDiaperUsedForPeeAndPoo(1);

        doDeleteFirstEntry();

    }

    @Test public void test_edit_mix_to_poo() {

        //------------------------------------------------------------------------
        // do:
        // Create Mix

        // check:
        // Pee => Diaper used 1 pieces
        // Pee => Details = Pee
        // Poo => No activity

        // do:
        // Edit Pee to Poo

        // check:
        // Pee => No activity
        // Poo => Diaper used 1 pieces
        // Poo => Details = Poo

        // delete all entries
        //------------------------------------------------------------------------

        doOpenDiaperFragment();
        checkHeaderEmpty();

        doCreatePeeAndPoo();
        checkPeeAndPooCreation();
        checkDiaperUsedForPeeAndPoo(1);

        doEditToPoo();
        checkDiaperUsedForPeeAndPoo(0);
        checkPooCreation();
        checkDiaperUsedForPoo(1);

        doDeleteFirstEntry();

    }

    @Test public void test_edit_mix_to_pee() {

        //------------------------------------------------------------------------
        // do:
        // Create Mix

        // check:
        // Mix => Diaper used 1 pieces
        // Mix => Details = Pee and poo
        // Pee => No activity

        // do:
        // Edit Mix to Pee

        // check:
        // Mix => No activity
        // Pee => Diaper used 1 pieces
        // Pee => Details = Pee

        // delete all entries
        //------------------------------------------------------------------------

        doOpenDiaperFragment();
        checkHeaderEmpty();

        doCreatePeeAndPoo();
        checkPeeAndPooCreation();
        checkDiaperUsedForPeeAndPoo(1);

        doEditToPee();
        checkDiaperUsedForPeeAndPoo(0);
        checkPeeCreation();
        checkDiaperUsedForPee(1);

        doDeleteFirstEntry();

    }

    private void doEditToPoo() {
        /**
         * Always delete from child number 1. Please note that RecyclerView only have 5 child max.
         * It reuse the children for optimization.
         */
        onView(withId(R.id.diaper_recycler_view))
                .perform(TestUtils.actionOnItemViewAtPosition(1, R.id.diaper_entry_edit_debug, click()));
        onView(withId(R.id.dialog_choice_dry)).perform(click());
    }

    private void doEditToPee() {
        /**
         * Always delete from child number 1. Please note that RecyclerView only have 5 child max.
         * It reuse the children for optimization.
         */
        onView(withId(R.id.diaper_recycler_view))
                .perform(TestUtils.actionOnItemViewAtPosition(1, R.id.diaper_entry_edit_debug, click()));
        onView(withId(R.id.dialog_choice_wet)).perform(click());
    }

    private void doEditToMix() {
        /**
         * Always delete from child number 1. Please note that RecyclerView only have 5 child max.
         * It reuse the children for optimization.
         */
        onView(withId(R.id.diaper_recycler_view))
                .perform(TestUtils.actionOnItemViewAtPosition(1, R.id.diaper_entry_edit_debug, click()));
        onView(withId(R.id.dialog_choice_mixed)).perform(click());
    }

    private void doOpenDiaperFragment() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText(R.string.str_Diaper)).perform(click());
    }

    private void checkHeaderEmpty() {
        onView(withId(R.id.dry_total)).check(matches(withText(R.string.str_No_activity)));
        onView(withId(R.id.wet_total)).check(matches(withText(R.string.str_No_activity)));
        onView(withId(R.id.mix_total)).check(matches(withText(R.string.str_No_activity)));
    }

    private void doCreatePoo() {
        onView(withId(R.id.fab_diaper)).perform(click());
        onView(withId(R.id.fab_diaper)).perform(click());
        onView(withId(R.id.dialog_choice_dry)).perform(click());
    }

    private void checkDiaperUsedForPoo(int diaperUsed) {
        String text = "No activity";
        if (diaperUsed!=0) text = "Diaper used " + diaperUsed + " pieces";
        onView(withId(R.id.dry_total)).check(matches(withText(text)));
    }

    private void checkPooCreation() {
        onView(withRecyclerView(R.id.diaper_recycler_view)
                .atPositionOnView(1, R.id.diaper_type_text))
                .check(matches(withText(R.string.str_Poo)));
    }

    private void doCreatePee() {
        onView(withId(R.id.fab_diaper)).perform(click());
        onView(withId(R.id.fab_diaper)).perform(click());
        onView(withId(R.id.dialog_choice_wet)).perform(click());
    }

    private void checkDiaperUsedForPee(int diaperUsed) {
        String text = "No activity";
        if (diaperUsed!=0) text = "Diaper used " + diaperUsed + " pieces";
        onView(withId(R.id.wet_total)).check(matches(withText(text)));
    }

    private void checkPeeCreation() {
        onView(withRecyclerView(R.id.diaper_recycler_view)
                .atPositionOnView(1, R.id.diaper_type_text))
                .check(matches(withText(R.string.str_Pee)));
    }

    private void doCreatePeeAndPoo() {
        onView(withId(R.id.fab_diaper)).perform(click());
        onView(withId(R.id.fab_diaper)).perform(click());
        onView(withId(R.id.dialog_choice_mixed)).perform(click());
    }

    private void checkDiaperUsedForPeeAndPoo(int diaperUsed) {
        String text = "No activity";
        if (diaperUsed!=0) text = "Diaper used " + diaperUsed + " pieces";
        onView(withId(R.id.mix_total)).check(matches(withText(text)));
    }

    private void checkPeeAndPooCreation() {
        onView(withRecyclerView(R.id.diaper_recycler_view)
                .atPositionOnView(1, R.id.diaper_type_text))
                .check(matches(withText(R.string.str_Pee_and_poo)));
    }

    private void doDeleteFirstEntry() {
        /**
         * Always delete from child number 1. Please note that RecyclerView only have 5 child max.
         * It reuse the children for optimization.
         */
        onView(withId(R.id.diaper_recycler_view))
                .perform(TestUtils.actionOnItemViewAtPosition(1, R.id.diaper_entry_delete_debug, click()));
    }

}
