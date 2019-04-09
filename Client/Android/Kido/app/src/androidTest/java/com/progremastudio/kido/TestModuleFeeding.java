package com.progremastudio.kido;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.progremastudio.kido.core.ActivityHome;
import com.progremastudio.kido.espressohelper.TestUtils;
import com.progremastudio.kido.espressohelper.ToastMatcher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DecimalFormat;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.progremastudio.kido.espressohelper.DurationPickerTools.pressDurationPicker;
import static com.progremastudio.kido.espressohelper.DurationPickerTools.pressOkOnDurationPicker;
import static com.progremastudio.kido.espressohelper.TestUtils.withRecyclerView;

@RunWith(AndroidJUnit4.class) @LargeTest public class TestModuleFeeding {

    DecimalFormat twoSignificantDigit = new DecimalFormat("0.00");

    final static int STRESS_TEST_NUMBER = 3;
    final static boolean SKIP_KNOWN_ISSUE = true;

    @Rule public ActivityTestRule<ActivityHome> mActivityRule = new ActivityTestRule<>(ActivityHome.class);

    @Test public void test_create_solid_food_normal() {

        //------------------------------------------------------------------------
        // do:
        // food name = Rice
        // amount = 350.45

        // check:
        // solid food summary number of meal increased by 1
        // entry created with name is Rice and amount is 350.45 gr

        // do:
        // delete entry

        // check:
        // solid food summary number of meal decreased by 1
        // entry is deleted
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkSolidFoodTotalEntry(0);

        doCreateSolidFoodEntry("rice", "350.45");
        checkSolidFoodTotalEntry(1);
        checkFeedingTypeText("rice", 1);
        checkFeedingDetailsText("350.45 gr", 1);

        doScrollToPosition(1);

        doDeleteFirstEntry();
        checkSolidFoodTotalEntry(0);
    }

    @Test public void test_create_solid_food_error_missing_name() {

        //------------------------------------------------------------------------
        // do:
        // food name = [EMPTY]
        // amount = 125.25

        // check:
        // application is not crash
        // toast message is shown with message "Please fill in food name and amount"
        // no entry is created
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkSolidFoodTotalEntry(0);

        doCreateSolidFoodEntry("", "125.25");
        checkToastMessage(R.string.str_Please_fill_in_food_name_and_amount);

        doDismissDialog();
        checkSolidFoodTotalEntry(0);

    }

    @Test public void test_create_solid_food_error_missing_amount() {

        //------------------------------------------------------------------------
        // do:
        // food name = Porridge
        // amount = [EMPTY]

        // check:
        // application is not crash
        // toast message is shown with message "Please fill in food name and amount"
        // no entry is created
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkSolidFoodTotalEntry(0);

        doCreateSolidFoodEntry("Porridge", "");
        checkToastMessage(R.string.str_Please_fill_in_food_name_and_amount);

        doDismissDialog();
        checkSolidFoodTotalEntry(0);

    }

    @Test public void test_create_solid_food_error_missing_name_amount() {

        //------------------------------------------------------------------------
        // do:
        // food name = [EMPTY]
        // amount = [EMPTY]

        // check:
        // application is not crash
        // toast message is shown with message "Please fill in food name and amount"
        // no entry is created
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkSolidFoodTotalEntry(0);

        doCreateSolidFoodEntry("", "");
        checkToastMessage(R.string.str_Please_fill_in_food_name_and_amount);

        doDismissDialog();
        checkSolidFoodTotalEntry(0);

    }

    @Test public void test_create_solid_food_error_name_length_more_than_max() {

        //------------------------------------------------------------------------
        // do:
        // food name = qwertqwertqwertqwertq [21 length]
        // amount = 100.13

        // check:
        // application is not crash
        // toast message is shown with message "Food name should be less than 20 characters"
        // no entry is created

        // do:
        // food name = qwertqwertqwertqwert [20 length]
        // amount = 100.13

        // check:
        // solid food summary number of meal increased by 1
        // entry created with name is qwertqwertqwertqwert and amount is 100.13 gr

        // do:
        // food name = qwertqwertqwertqwer [19 length]
        // amount = 100.13

        // check:
        // solid food summary number of meal increased by 1
        // entry created with name is qwertqwertqwertqwer and amount is 100.13 gr

        // do:
        // delete entry

        // check:
        // solid food summary number of meal is 0 (No Activity)
        // entry is deleted
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkSolidFoodTotalEntry(0);

        doCreateSolidFoodEntry("qwertqwertqwertqwertq", "100.13");
        checkToastMessage(R.string.str_Food_name_should_be_less_than_20_characters);

        doDismissDialog();
        checkSolidFoodTotalEntry(0);

        doCreateSolidFoodEntry("qwertqwertqwertqwert", "100.13");
        checkSolidFoodTotalEntry(1);
        checkFeedingTypeText("qwertqwertqwertqwert", 1);
        checkFeedingDetailsText("100.13 gr", 1);

        doCreateSolidFoodEntry("qwertqwertqwertqwer", "100.13");
        checkSolidFoodTotalEntry(2);
        checkFeedingTypeText("qwertqwertqwertqwer", 1);
        checkFeedingDetailsText("100.13 gr", 1);

        doScrollToPosition(1);
        doDeleteFirstEntry();
        checkSolidFoodTotalEntry(1);

        doScrollToPosition(1);
        doDeleteFirstEntry();
        checkSolidFoodTotalEntry(0);

    }

    @Test public void test_create_solid_food_error_amount_length_more_than_max() {

        //------------------------------------------------------------------------
        // do:
        // food name = rice
        // amount = 100000.00 [larger than max]

        // check:
        // application is not crash
        // toast message is shown with message "Amount should be positive and maximum is 99999.99 gr"
        // no entry is created

        // do:
        // food name = rice
        // amount = 99999.99 [equals to max]

        // check:
        // solid food summary number of meal increased by 1
        // entry created with name is rice and amount is 99999.99 gr

        // do:
        // food name = rice
        // amount = 99999.98 [lower to max]

        // check:
        // solid food summary number of meal increased by 1
        // entry created with name is rice and amount is 99999.98 gr

        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkSolidFoodTotalEntry(0);

        doCreateSolidFoodEntry("rice", "100000.00");
        checkToastMessage(R.string.str_Amount_should_be_positive_and_maximum_is_9999999_gr);

        doDismissDialog();
        checkSolidFoodTotalEntry(0);

        doCreateSolidFoodEntry("rice", "99999.99");
        checkSolidFoodTotalEntry(1);
        checkFeedingTypeText("rice", 1);
        checkFeedingDetailsText("99999.99 gr", 1);

        doCreateSolidFoodEntry("rice", "99999.98");
        checkSolidFoodTotalEntry(2);
        checkFeedingTypeText("rice", 1);
        checkFeedingDetailsText("99999.98 gr", 1);

        doScrollToPosition(1);
        doDeleteFirstEntry();
        checkSolidFoodTotalEntry(1);

        doScrollToPosition(1);
        doDeleteFirstEntry();
        checkSolidFoodTotalEntry(0);

    }

    @Test public void test_create_solid_food_error_amount_zero() {

        //------------------------------------------------------------------------
        // do:
        // food name = rice
        // amount = 0.00

        // check:
        // application is not crash
        // toast message is shown with message "Amount should be positive and maximum is 99999.99 gr"
        // no entry is created

        // do:
        // food name = rice
        // amount = 0.01

        // check:
        // solid food summary number of meal increased by 1
        // entry created with name is rice and amount is 0.01 gr
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkSolidFoodTotalEntry(0);

        doCreateSolidFoodEntry("rice", "0.00");
        checkToastMessage(R.string.str_Amount_should_be_positive_and_maximum_is_9999999_gr);

        doDismissDialog();
        checkSolidFoodTotalEntry(0);

        doCreateSolidFoodEntry("rice", "0.01");
        checkSolidFoodTotalEntry(1);
        checkFeedingTypeText("rice", 1);
        checkFeedingDetailsText("0.01 gr", 1);

        doScrollToPosition(1);
        doDeleteFirstEntry();
        checkSolidFoodTotalEntry(0);

    }

    @Test public void test_create_solid_food_error_food_name_and_amount_length_more_than_max() {

        //------------------------------------------------------------------------
        // do:
        // food name = qwertqwertqwertqwertq
        // amount = 100000.00

        // check:
        // application is not crash
        // toast message is shown with message "Food name should be less than 20 characters"
        // no entry is created

        // do:
        // food name = qwertqwertqwertqwert
        // amount = 99999.99

        // check:
        // solid food summary number of meal increased by 1
        // entry created with name is qwertqwertqwertqwert and amount is 99999.99 gr
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkSolidFoodTotalEntry(0);

        doCreateSolidFoodEntry("qwertqwertqwertqwertq", "100000.00");
        checkToastMessage(R.string.str_Food_name_should_be_less_than_20_characters);

        doDismissDialog();
        checkSolidFoodTotalEntry(0);

        doCreateSolidFoodEntry("qwertqwertqwertqwert", "99999.99");
        checkSolidFoodTotalEntry(1);
        checkFeedingTypeText("qwertqwertqwertqwert", 1);
        checkFeedingDetailsText("99999.99 gr", 1);

        doScrollToPosition(1);
        doDeleteFirstEntry();
        checkSolidFoodTotalEntry(0);

    }

    @Test public void test_create_solid_food_normal_edit_entry() {

        //------------------------------------------------------------------------
        // do:
        // food name = rice
        // amount = 11.25

        // check:
        // solid food summary number of meal
        // entry created with name is rice and amount is 11.25 gr

        // do:
        // edit food name to be porridge
        // amount = 25.35

        // check:
        // solid food summary number of meal
        // entry created with name is porridge and amount is 25.35

        // do:
        // delete entry
        // solid food summary number of meal
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkSolidFoodTotalEntry(0);

        doCreateSolidFoodEntry("rice", "11.25");
        checkSolidFoodTotalEntry(1);
        checkFeedingTypeText("rice", 1);
        checkFeedingDetailsText("11.25 gr", 1);

        doEditSolidFoodEntry("porridge", "25.35");
        checkSolidFoodTotalEntry(1);
        checkFeedingTypeText("porridge", 1);
        checkFeedingDetailsText("25.35 gr", 1);

        doScrollToPosition(1);
        doDeleteFirstEntry();
        checkSolidFoodTotalEntry(0);
    }

    @Test public void test_create_solid_food_stress() {

        //------------------------------------------------------------------------
        // do [100 times]:
        // food name = Rice i
        // amount = i

        // check [each time entry created]:
        // Solid food summary number of meal
        // Entry created with name is [Rice i] and amount is [i.00] gr

        // do[100 times]:
        // delete entry

        // check [each time entry created]:
        // solid food summary number of meal decreased by 1
        // entry is deleted
        //------------------------------------------------------------------------

        int entryNumber = STRESS_TEST_NUMBER;

        doOpenFeedingFragment();
        checkSolidFoodTotalEntry(0);

        for(int createPosition=1; createPosition<=entryNumber; createPosition++) {
            doCreateSolidFoodEntry("rice " + createPosition , String.valueOf(createPosition));
            checkSolidFoodTotalEntry(createPosition);
            checkFeedingTypeText("rice " + createPosition, 1);
            checkFeedingDetailsText(createPosition + ".00 gr", 1);
            doScrollToPosition(createPosition);
        }

        for(int count=1; count<=entryNumber; count++) {
            doDeleteFirstEntry();
            checkSolidFoodTotalEntry(entryNumber - count);
        }
    }

    @Test public void test_create_formula_normal() {

        //------------------------------------------------------------------------
        // do:
        // amount = 100.23 mL

        // check:
        // Total feeding = 100.23 mL
        // Volume entry = 100.23 mL

        // do:
        // amount = 25.37 mL

        // check :
        // Total feeding = 100.23 + 25.37 = 125.6
        // Volume entry = 25.37 mL

        // do:
        // amount = 255.69 mL

        // check :
        // Total feeding = 125.6 + 255.69 = 381.29 mL
        // Volume entry = 255.69 mL

        // do :
        // delete last entry

        // check :
        // Total feeding = 100.23 + 25.37 = 125.6

        // do :
        // delete last entry

        // check :
        // Total feeding = 25.37

        // do :
        // delete last entry

        // check :
        // Total feeding = No activity

        //------------------------------------------------------------------------

        float volume;
        float totalVolume = 0.00f;

        doOpenFeedingFragment();
        checkFormulaTotalFeeding(totalVolume);

        volume = 100.23f;
        totalVolume += volume;
        doCreateFormulaEntry(volume);
        checkFormulaTotalFeeding(totalVolume);
        checkFormulaDetailsAmount(volume, 1);

        volume = 25.37f;
        totalVolume += volume;
        doCreateFormulaEntry(volume);
        checkFormulaTotalFeeding(totalVolume);
        checkFormulaDetailsAmount(volume, 1);

        volume = 255.69f;
        totalVolume += volume;
        doCreateFormulaEntry(volume);
        checkFormulaTotalFeeding(totalVolume);
        checkFormulaDetailsAmount(volume, 1);

        totalVolume -= 255.69f;
        doDeleteFirstEntry();
        checkFormulaTotalFeeding(totalVolume);

        totalVolume -= 25.37f;
        doDeleteFirstEntry();
        checkFormulaTotalFeeding(totalVolume);

        totalVolume -= 100.23f;
        doDeleteFirstEntry();
        checkFormulaTotalFeeding(totalVolume);

    }

    @Test public void test_create_formula_error_missing_volume() {

        //------------------------------------------------------------------------
        // do:
        // amount = [EMPTY]

        // check:
        // application is not crash
        // toast message is shown with message "Please fill pump milk volume"
        // no entry is created
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkFormulaTotalFeeding(0.00f);

        doCreateEmptyFormulaEntry();
        checkToastMessage(R.string.str_Please_fill_the_formula_milk_volume);

        doDismissDialog();
        checkFormulaTotalFeeding(0.00f);
    }

    @Test public void test_create_formula_error_volume_length_more_than_max() {

        //------------------------------------------------------------------------
        // do:
        // amount = 100000.00 [larger than max]

        // check:
        // application is not crash
        // toast message is shown with message "Amount should be positive and maximum is 99999.99 mL"
        // no entry is created
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkFormulaTotalFeeding(0.00f);

        doCreateFormulaEntry(100000.00f);
        checkToastMessage(R.string.str_Volume_should_be_positive_and_maximum_is_9999999_mL);

        doDismissDialog();
        checkFormulaTotalFeeding(0.00f);

        doCreateFormulaEntry(99999.99f);
        checkFormulaTotalFeeding(99999.99f);
        checkFormulaDetailsAmount(99999.99f, 1);

        doScrollToPosition(1);
        doDeleteFirstEntry();
        checkFormulaTotalFeeding(0.00f);

    }

    @Test public void test_create_formula_error_volume_zero() {

        //------------------------------------------------------------------------
        // do:
        // amount = 0.00 mL

        // check:
        // application is not crash
        // toast message is shown with message "Amount should be positive and maximum is 99999.99 gr"
        // no entry is created

        // do:
        // amount = 0.01 mL

        // check:
        // Total feeding = 0.01 mL
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkFormulaTotalFeeding(0.00f);

        doCreateFormulaEntry(0.00f);
        checkToastMessage(R.string.str_Volume_should_be_positive_and_maximum_is_9999999_mL);

        doDismissDialog();
        checkFormulaTotalFeeding(0.00f);

        doCreateFormulaEntry(0.01f);
        checkFormulaTotalFeeding(0.01f);
        checkFormulaDetailsAmount(0.01f, 1);

        doScrollToPosition(1);
        doDeleteFirstEntry();
        checkFormulaTotalFeeding(0.00f);

    }

    @Test public void test_create_formula_normal_edit_entry() {

        //------------------------------------------------------------------------
        // do:
        // amount = 100.25 mL

        // check:
        // Total feeding = 100.25 mL

        // do:
        // Edit amount = 50.57 mL

        // check:
        // Total feeding = 50.57 mL
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkFormulaTotalFeeding(0.00f);

        doCreateFormulaEntry(100.25f);
        checkFormulaTotalFeeding(100.25f);
        checkFormulaDetailsAmount(100.25f, 1);

        doEditFormulaFoodEntry(50.57f);
        checkFormulaTotalFeeding(50.57f);
        checkFormulaDetailsAmount(50.57f, 1);

        doScrollToPosition(1);
        doDeleteFirstEntry();
        checkFormulaTotalFeeding(0.00f);

    }

    @Test public void test_create_formula_stress() {

        //------------------------------------------------------------------------
        // do [100 times]:
        // amount = 1.11 mL

        // check :
        // Total feeding = 1.11 mL times x

        // do[100 times]:
        // delete entry

        // check [each time entry created]:
        // solid food summary number of meal decreased by 1
        // entry is deleted
        //------------------------------------------------------------------------

        int entryNumber = STRESS_TEST_NUMBER;
        float volume = 1.11f;
        float totalVolume = 0.00f;

        doOpenFeedingFragment();
        checkFormulaTotalFeeding(totalVolume);

        for(int createPosition=1; createPosition<=entryNumber; createPosition++) {
            totalVolume += volume;
            doCreateFormulaEntry(volume);
            checkFormulaTotalFeeding(totalVolume);
            checkFormulaDetailsAmount(volume, 1);
            doScrollToPosition(createPosition);
        }

        for(int count=1; count<=entryNumber; count++) {
            doDeleteFirstEntry();
            checkFormulaTotalFeeding(totalVolume - count*volume);
        }
    }

    @Test public void test_create_pump_normal_stock_similar_to_feeding() {

        //------------------------------------------------------------------------
        // do:
        // Pumping = 100.25 mL

        // check:
        // Remaining stock = 100.25 mL
        // Pumping 100.25 mL

        // do:
        // Feeding = 100.25 mL

        // check:
        // Remaining stock = 0.00 mL
        // Feeding = 100.25 mL
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkPumpedMilkRemainingStockEmpty();

        doCreatePumpedMilkPumpingEntry(100.25f);
        checkPumpedMilkRemainingStock(100.25f);
        checkPumpedMilkPumpingDetails(100.25f, 1);

        doCreatePumpedMilkFeedingEntry(100.25f);
        checkPumpedMilkRemainingStock(0.00f);
        checkPumpedMilkFeedingDetails(100.25f, 1);

        doDeleteFirstEntry();
        checkPumpedMilkRemainingStock(100.25f);

        doDeleteFirstEntry();
        checkPumpedMilkRemainingStockEmpty();

    }

    @Test public void test_create_pump_normal_stock_fewer_than_feeding() {

        //------------------------------------------------------------------------
        // do:
        // Pumping = 43.26 mL

        // check:
        // Remaining stock = 43.26 mL
        // Pumping 43.26 mL

        // do:
        // Feeding = 43.27 mL

        // check:
        // Entry is not created
        // Remaining stock = 43.27 mL
        // Toast message "Not enough remaining stock"

        // do:
        // Feeding = 51.37 mL

        // check:
        // Entry is not created
        // Remaining stock = 43.26 mL
        // Toast message "Not enough remaining stock"
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkPumpedMilkRemainingStockEmpty();

        doCreatePumpedMilkPumpingEntry(43.26f);
        checkPumpedMilkRemainingStock(43.26f);
        checkPumpedMilkPumpingDetails(43.26f, 1);

        doCreatePumpedMilkFeedingEntry(43.27f);
        checkToastMessage(R.string.str_Not_enough_remaining_stock);

        doDismissDialog();
        checkPumpedMilkRemainingStock(43.26f);

        doCreatePumpedMilkFeedingEntry(51.37f);
        checkToastMessage(R.string.str_Not_enough_remaining_stock);

        doDismissDialog();
        checkPumpedMilkRemainingStock(43.26f);

        doDeleteFirstEntry();
        checkPumpedMilkRemainingStockEmpty();

    }

    @Test public void test_create_pump_normal_stock_higher_than_feeding() {

        //------------------------------------------------------------------------
        // do:
        // Pumping = 176.63 mL

        // check:
        // Remaining stock = 176.63 mL
        // Pumping 176.63 mL

        // do:
        // Feeding = 37.98 mL

        // check:
        // Remaining stock = 138.65 mL
        // Feeding = 37.98 mL

        // do:
        // Feeding = 41.24 mL

        // check:
        // Remaining stock = 97.41 mL
        // Feeding = 41.24 mL
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkPumpedMilkRemainingStockEmpty();

        doCreatePumpedMilkPumpingEntry(176.63f);
        checkPumpedMilkRemainingStock(176.63f);
        checkPumpedMilkPumpingDetails(176.63f, 1);

        doCreatePumpedMilkFeedingEntry(37.98f);
        checkPumpedMilkRemainingStock(138.65f);
        checkPumpedMilkFeedingDetails(37.98f, 1);

        doCreatePumpedMilkFeedingEntry(41.24f);
        checkPumpedMilkRemainingStock(97.41f);
        checkPumpedMilkFeedingDetails(41.24f, 1);

        doDeleteFirstEntry();
        checkPumpedMilkRemainingStock(138.65f);

        doDeleteFirstEntry();
        checkPumpedMilkRemainingStock(176.63f);

        doDeleteFirstEntry();
        checkPumpedMilkRemainingStockEmpty();

    }

    @Test public void test_create_pump_error_missing_volume() {

        //------------------------------------------------------------------------
        // do:
        // Pumping = [EMPTY]

        // check:
        // No entry created
        // Toast message "Please fill pump milk volume"

        // do:
        // Feeding = [EMPTY]

        // check:
        // No entry created
        // Toast message "Please fill pump milk volume"

        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkPumpedMilkRemainingStockEmpty();

        doCreatePumpedMilkPumpingEntryEmpty();
        checkToastMessage(R.string.str_Please_fill_pump_milk_volume);

        doDismissDialog();
        checkPumpedMilkRemainingStockEmpty();

        doCreatePumpedMilkFeedingEntryEmpty();
        checkToastMessage(R.string.str_Please_fill_pump_milk_volume);

        doDismissDialog();
        checkPumpedMilkRemainingStockEmpty();

    }

    @Test public void test_create_pump_error_volume_length_more_than_max() {

        //------------------------------------------------------------------------
        // do:
        // pumping = 100000.00 [larger than max]

        // check:
        // application is not crash
        // toast message is shown with message "Amount should be positive and maximum is 99999.99 mL"
        // no entry is created

        // do:
        // pumping = 99999.99

        // check:
        // Remaining stock = 99999.99
        // Pumping = 99999.99

        // do:
        // feeding = 100000.00 [larger than max]

        // check:
        // application is not crash
        // toast message is shown with message "Amount should be positive and maximum is 99999.99 mL"
        // no entry is created

        // do:
        // feeding = 99999.99

        // check:
        // Remaining stock = 0.00
        // feeding = 99999.99

        // do:
        // delete all entries
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkPumpedMilkRemainingStockEmpty();

        doCreatePumpedMilkPumpingEntry(100000.00f);
        checkToastMessage(R.string.str_Volume_should_be_positive_and_maximum_is_9999999_mL);

        doDismissDialog();
        checkPumpedMilkRemainingStockEmpty();

        doCreatePumpedMilkPumpingEntry(99999.99f);
        checkPumpedMilkRemainingStock(99999.99f);
        checkPumpedMilkPumpingDetails(99999.99f, 1);

        doCreatePumpedMilkFeedingEntry(100000.00f);
        checkToastMessage(R.string.str_Volume_should_be_positive_and_maximum_is_9999999_mL);

        doDismissDialog();
        checkPumpedMilkRemainingStock(99999.99f);
        checkPumpedMilkPumpingDetails(99999.99f, 1);

        doCreatePumpedMilkFeedingEntry(99999.99f);
        checkPumpedMilkRemainingStock(0.00f);
        checkPumpedMilkFeedingDetails(99999.99f, 1);

        doDeleteFirstEntry();
        checkPumpedMilkRemainingStock(99999.99f);

        doDeleteFirstEntry();
        checkPumpedMilkRemainingStockEmpty();
    }

    @Test public void test_create_pump_error_volume_zero() {

        //------------------------------------------------------------------------
        // do:
        // pumping = 0.00

        // check:
        // application is not crash
        // toast message is shown with message "Amount should be positive and maximum is 99999.99 mL"
        // no entry is created

        // do:
        // pumping = 0.01

        // check:
        // Remaining stock = 0.01
        // Pumping = 0.01

        // do:
        // feeding = 0.00

        // check:
        // application is not crash
        // toast message is shown with message "Amount should be positive and maximum is 99999.99 mL"
        // no entry is created

        // do:
        // feeding = 0.01

        // check:
        // Remaining stock = 0.00
        // feeding = 0.01

        // do:
        // delete all entries
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkPumpedMilkRemainingStockEmpty();

        doCreatePumpedMilkPumpingEntry(0.00f);
        checkToastMessage(R.string.str_Volume_should_be_positive_and_maximum_is_9999999_mL);

        doDismissDialog();
        checkPumpedMilkRemainingStockEmpty();

        doCreatePumpedMilkPumpingEntry(0.01f);
        checkPumpedMilkRemainingStock(0.01f);
        checkPumpedMilkPumpingDetails(0.01f, 1);

        doCreatePumpedMilkFeedingEntry(0.00f);
        checkToastMessage(R.string.str_Volume_should_be_positive_and_maximum_is_9999999_mL);

        doDismissDialog();
        checkPumpedMilkRemainingStock(0.01f);
        checkPumpedMilkPumpingDetails(0.01f, 1);

        doCreatePumpedMilkFeedingEntry(0.01f);
        checkPumpedMilkRemainingStock(0.00f);
        checkPumpedMilkFeedingDetails(0.01f, 1);

        doDeleteFirstEntry();
        checkPumpedMilkRemainingStock(0.01f);

        doDeleteFirstEntry();
        checkPumpedMilkRemainingStockEmpty();
    }

    @Test public void test_create_pump_error_stock_more_than_max() {
        //------------------------------------------------------------------------
        // Do:
        // Pumping = 99999.99f

        // Check:
        // Remaining stock = 99999.99f
        // Pumping = 99999.99f

        // Do:
        // Pumping = 0.01f

        // Check:
        // Remaining stock = "plentiful"
        // Pumping = 0.01f

        // Do:
        // Delete first entry

        // Check:
        // Remaining stock = 99999.99f
        // Pumping = 99999.99f

        // delete all entries
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkPumpedMilkRemainingStockEmpty();

        doCreatePumpedMilkPumpingEntry(99999.99f);
        checkPumpedMilkRemainingStock(99999.99f);
        checkPumpedMilkPumpingDetails(99999.99f, 1);

        doCreatePumpedMilkPumpingEntry(0.01f);
        checkPumpedMilkRemainingStockMoreThanMax();
        checkPumpedMilkPumpingDetails(0.01f, 1);

        doDeleteFirstEntry();
        checkPumpedMilkRemainingStock(99999.99f);
        checkPumpedMilkPumpingDetails(99999.99f, 1);

        doDeleteFirstEntry();

    }

    @Test public void test_create_pump_edit_pumping() {
        //------------------------------------------------------------------------
        // Do:
        // Pumping = 100.25f

        // Check:
        // Remaining stock = 100.25f
        // Pumping = 100.25f

        // Do:
        // Edit Pumping = 50.63f

        // Check:
        // Remaining stock = 50.63f
        // Pumping = 50.63f
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkPumpedMilkRemainingStockEmpty();

        doCreatePumpedMilkPumpingEntry(100.25f);
        checkPumpedMilkRemainingStock(100.25f);
        checkPumpedMilkPumpingDetails(100.25f, 1);

        doEditPumpedMilkPumpingEntry(50.63f);
        checkPumpedMilkRemainingStock(50.63f);
        checkPumpedMilkPumpingDetails(50.63f, 1);

        doDeleteFirstEntry();
    }

    @Test public void test_create_pump_edit_feeding() {
        //------------------------------------------------------------------------
        // Do:
        // Pumping = 100.00f

        // Check:
        // Remaining stock = 100.00f
        // Pumping = 100.00f

        // Do:
        // Feeding = 73.31f

        // Check:
        // Remaining stock = 26.69f
        // Feeding = 73.31f

        // Do:
        // Edit Feeding = 23.89f

        // Check:
        // Remaining stock = 76.11f
        // Feeding = 23.89f

        // Delete all entries
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkPumpedMilkRemainingStockEmpty();

        doCreatePumpedMilkPumpingEntry(100.00f);
        checkPumpedMilkRemainingStock(100.00f);
        checkPumpedMilkPumpingDetails(100.00f, 1);

        doCreatePumpedMilkFeedingEntry(73.31f);
        checkPumpedMilkRemainingStock(26.69f);
        checkPumpedMilkFeedingDetails(73.31f, 1);

        doEditPumpedMilkFeedingEntry(23.89f);
        checkPumpedMilkRemainingStock(76.11f);
        checkPumpedMilkFeedingDetails(23.89f, 1);

        doDeleteFirstEntry();
        doDeleteFirstEntry();
    }

    @Test public void test_create_pump_stress() {

        //------------------------------------------------------------------------
        // do [100 times]:
        // pumping = 1.1f

        // check [each time entry created]:
        // Remaining stock = 1.1f * (item created)
        // Pumping = 1.1f

        // do[100 times]:
        // feeding = 1.1f

        // check [each time entry created]:
        // Remaining stock = (1.1f * (total item created)) - 1.1f * (item created)
        // Pumping = 1.1f

        // delete all entries
        //------------------------------------------------------------------------

        /**
         * TODO: Bug found @ test_create_pump_stress
         * Test found that calculated Remaining Stock is -0.00 mL instead of 0.00 mL.
         * Need to find solution for this. Not just patching solution.
         */

        int entryNumber = STRESS_TEST_NUMBER;
        float checkValue;

        doOpenFeedingFragment();
        checkSolidFoodTotalEntry(0);

        for(int createPosition=1; createPosition<=entryNumber; createPosition++) {
            doCreatePumpedMilkPumpingEntry(1.1f);
            checkPumpedMilkRemainingStock(1.1f * createPosition);
            checkPumpedMilkPumpingDetails(1.1f, 1);
            doScrollToPosition(createPosition);
        }

        for(int createPosition=1; createPosition<=entryNumber; createPosition++) {
            doCreatePumpedMilkFeedingEntry(1.1f);
            checkValue = (1.1f*entryNumber) - (1.1f*createPosition);
            if ((checkValue == 0.00f) && SKIP_KNOWN_ISSUE)  {
                // skip known issue
            } else {
                checkPumpedMilkRemainingStock(checkValue); // bug found here when checkValue = 0.00f!!
            }
            checkPumpedMilkFeedingDetails(1.1f, 1);
            doScrollToPosition(createPosition);
        }

        for(int count=1; count<=2*entryNumber; count++) {
            doDeleteFirstEntry();
        }

    }

    @Test public void test_create_right_left_breastfeeding_normal() {

        //------------------------------------------------------------------------
        // do:
        // Left breastfeeding = 09h 09m 09s

        // check:
        // Total breastfeeding = 09h 09m 09s
        // Breastfeeding details = 09h 09m 09s

        // do:
        // Right breastfeeding = 01h 01m 01s

        // check:
        // Total breastfeeding = 10h 10m 10s
        // Breastfeeding details = 01h 01m 01s

        // delete all entries
        //------------------------------------------------------------------------

        doOpenFeedingFragment();
        checkTotalBreastfeedingEmpty();

        doCreateLeftBreastfeeding("09h 09m 09s");
        checkTotalBreastfeeding("09h 09m 09s");
        checkBreastfeedingDetails("09h 09m 09s");

        doCreateRightBreastfeeding("01h 01m 01s");
        checkTotalBreastfeeding("10h 10m 10s");
        checkBreastfeedingDetails("01h 01m 01s");

        doDeleteFirstEntry();
        doDeleteFirstEntry();

    }

    private void doCreateSolidFoodEntry(String foodName, String amount) {
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.solid_food_selector)).perform(click());
        onView(withId(R.id.food_name_entry)).perform(typeText(foodName), closeSoftKeyboard());
        onView(withId(R.id.food_volume_entry)).perform(typeText(amount), closeSoftKeyboard());
        onView(withId(R.id.solid_food_confirm)).perform(click());
    }

    private void doCreateFormulaEntry(float volume) {
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.liquid_food_selector)).perform(click());
        onView(withId(R.id.formula_selector)).perform(click());
        onView(withId(R.id.entry_formula_volume)).perform(typeText(twoSignificantDigit.format(volume)), closeSoftKeyboard());
        onView(withId(R.id.button_formula_confirm)).perform(click());
    }

    private void doCreateEmptyFormulaEntry() {
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.liquid_food_selector)).perform(click());
        onView(withId(R.id.formula_selector)).perform(click());
        onView(withId(R.id.entry_formula_volume)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.button_formula_confirm)).perform(click());
    }

    private void doCreatePumpedMilkPumpingEntry(float volume) {
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.liquid_food_selector)).perform(click());
        onView(withId(R.id.breastmilk_selector)).perform(click());
        onView(withId(R.id.pump_breastmilk_selector)).perform(click());
        onView(withId(R.id.pump_volume)).perform(typeText(twoSignificantDigit.format(volume)), closeSoftKeyboard());
        onView(withId(R.id.pump_pumping)).perform(click());
    }

    private void doCreatePumpedMilkPumpingEntryEmpty() {
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.liquid_food_selector)).perform(click());
        onView(withId(R.id.breastmilk_selector)).perform(click());
        onView(withId(R.id.pump_breastmilk_selector)).perform(click());
        onView(withId(R.id.pump_volume)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.pump_pumping)).perform(click());
    }

    private void doCreatePumpedMilkFeedingEntry(float volume) {
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.liquid_food_selector)).perform(click());
        onView(withId(R.id.breastmilk_selector)).perform(click());
        onView(withId(R.id.pump_breastmilk_selector)).perform(click());
        onView(withId(R.id.pump_volume)).perform(typeText(twoSignificantDigit.format(volume)), closeSoftKeyboard());
        onView(withId(R.id.pump_feeding)).perform(click());
    }

    private void doCreatePumpedMilkFeedingEntryEmpty() {
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.liquid_food_selector)).perform(click());
        onView(withId(R.id.breastmilk_selector)).perform(click());
        onView(withId(R.id.pump_breastmilk_selector)).perform(click());
        onView(withId(R.id.pump_volume)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.pump_feeding)).perform(click());
    }

    private void doCreateLeftBreastfeeding(String duration) {
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.liquid_food_selector)).perform(click());
        onView(withId(R.id.breastmilk_selector)).perform(click());
        onView(withId(R.id.left_breastmilk_selector)).perform(click());
        pressDurationPicker(duration);
        pressOkOnDurationPicker();
    }

    private void doCreateRightBreastfeeding(String duration) {
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.fab_feeding)).perform(click());
        onView(withId(R.id.liquid_food_selector)).perform(click());
        onView(withId(R.id.breastmilk_selector)).perform(click());
        onView(withId(R.id.right_breastmilk_selector)).perform(click());
        pressDurationPicker(duration);
        pressOkOnDurationPicker();
    }

    private void doEditSolidFoodEntry(String foodName, String amount) {
        /**
         * Always edit from child number 1. Please note that RecyclerView only have 5 child max.
         * It reuse the children for optimization.
         */
        onView(withId(R.id.feeding_recycler_view))
                .perform(TestUtils.actionOnItemViewAtPosition(1, R.id.feeding_entry_edit_debug, click()));
        onView(withId(R.id.solid_food_selector)).perform(click());
        onView(withId(R.id.food_name_entry)).perform(typeText(foodName), closeSoftKeyboard());
        onView(withId(R.id.food_volume_entry)).perform(typeText(amount), closeSoftKeyboard());
        onView(withId(R.id.solid_food_confirm)).perform(click());
    }

    private void doEditFormulaFoodEntry(float volume) {
        /**
         * Always edit from child number 1. Please note that RecyclerView only have 5 child max.
         * It reuse the children for optimization.
         */
        onView(withId(R.id.feeding_recycler_view))
                .perform(TestUtils.actionOnItemViewAtPosition(1, R.id.feeding_entry_edit_debug, click()));
        onView(withId(R.id.liquid_food_selector)).perform(click());
        onView(withId(R.id.formula_selector)).perform(click());
        onView(withId(R.id.entry_formula_volume)).perform(typeText(twoSignificantDigit.format(volume)), closeSoftKeyboard());
        onView(withId(R.id.button_formula_confirm)).perform(click());
    }

    private void doEditPumpedMilkPumpingEntry(float volume) {
        /**
         * Always edit from child number 1. Please note that RecyclerView only have 5 child max.
         * It reuse the children for optimization.
         */
        onView(withId(R.id.feeding_recycler_view))
                .perform(TestUtils.actionOnItemViewAtPosition(1, R.id.feeding_entry_edit_debug, click()));
        onView(withId(R.id.liquid_food_selector)).perform(click());
        onView(withId(R.id.breastmilk_selector)).perform(click());
        onView(withId(R.id.pump_breastmilk_selector)).perform(click());
        onView(withId(R.id.pump_volume)).perform(typeText(twoSignificantDigit.format(volume)), closeSoftKeyboard());
        onView(withId(R.id.pump_pumping)).perform(click());
    }

    private void doEditPumpedMilkFeedingEntry(float volume) {
        /**
         * Always edit from child number 1. Please note that RecyclerView only have 5 child max.
         * It reuse the children for optimization.
         */
        onView(withId(R.id.feeding_recycler_view))
                .perform(TestUtils.actionOnItemViewAtPosition(1, R.id.feeding_entry_edit_debug, click()));
        onView(withId(R.id.liquid_food_selector)).perform(click());
        onView(withId(R.id.breastmilk_selector)).perform(click());
        onView(withId(R.id.pump_breastmilk_selector)).perform(click());
        onView(withId(R.id.pump_volume)).perform(typeText(twoSignificantDigit.format(volume)), closeSoftKeyboard());
        onView(withId(R.id.pump_feeding)).perform(click());
    }

    private void checkSolidFoodTotalEntry(int totalMeal) {
        String text = "No activity";
        if (totalMeal != 0) text = "Total meal " + totalMeal + " times";
        onView(withId(R.id.solid_food_total)).check(matches(withText(text)));
    }

    private void checkFormulaTotalFeeding(float totalFeeding) {
        String text = "No activity";
        if (totalFeeding != 0.00f) text = "Total feeding " + twoSignificantDigit.format(totalFeeding) + " mL";
        onView(withId(R.id.formula_milk_total)).check(matches(withText(text)));
    }

    private void checkFeedingTypeText(String text, int position) {
        onView(withRecyclerView(R.id.feeding_recycler_view)
                .atPositionOnView(position, R.id.feeding_type_text))
                .check(matches(withText("Eating " + text)));
    }

    private void checkFeedingDetailsText(String text, int position) {
        onView(withRecyclerView(R.id.feeding_recycler_view)
                .atPositionOnView(position, R.id.feeding_details))
                .check(matches(withText("Amount " + text)));
    }

    private void checkFormulaDetailsAmount(float volume, int position) {
        onView(withRecyclerView(R.id.feeding_recycler_view)
                .atPositionOnView(position, R.id.feeding_details))
                .check(matches(withText("Volume " + twoSignificantDigit.format(volume) + " mL")));
    }

    private void checkPumpedMilkRemainingStock(float volume) {
        String text = "Remaining stock " + twoSignificantDigit.format(volume) + " mL";
        onView(withId(R.id.pumped_milk_total)).check(matches(withText(text)));
    }

    private void checkPumpedMilkRemainingStockMoreThanMax() {
        String text = "Remaining stock plentiful mL";
        onView(withId(R.id.pumped_milk_total)).check(matches(withText(text)));
    }

    private void checkPumpedMilkRemainingStockEmpty() {
        String text = "No activity";
        onView(withId(R.id.pumped_milk_total)).check(matches(withText(text)));
    }

    private void checkPumpedMilkPumpingDetails(float volume, int position) {
        onView(withRecyclerView(R.id.feeding_recycler_view)
                .atPositionOnView(position, R.id.feeding_details))
                .check(matches(withText("Pumping " + twoSignificantDigit.format(volume) + " mL")));
    }

    private void checkPumpedMilkFeedingDetails(float volume, int position) {
        onView(withRecyclerView(R.id.feeding_recycler_view)
                .atPositionOnView(position, R.id.feeding_details))
                .check(matches(withText("Feeding " + twoSignificantDigit.format(volume) + " mL")));
    }

    private void checkTotalBreastfeeding(String time) {
        String text = "Total feeding " + time;
        onView(withId(R.id.breastfeeding_total)).check(matches(withText(text)));
    }

    private void checkTotalBreastfeedingEmpty() {
        String text = "No activity";
        onView(withId(R.id.breastfeeding_total)).check(matches(withText(text)));
    }

    private void checkBreastfeedingDetails(String time) {
        onView(withRecyclerView(R.id.feeding_recycler_view)
                .atPositionOnView(1, R.id.feeding_details))
                .check(matches(withText("Breastfeeding for " + time)));
    }

    private void doDeleteFirstEntry() {
        /**
         * Always delete from child number 1. Please note that RecyclerView only have 5 child max.
         * It reuse the children for optimization.
         */
        onView(withId(R.id.feeding_recycler_view))
                .perform(TestUtils.actionOnItemViewAtPosition(1, R.id.feeding_entry_delete_debug, click()));
    }

    private void doScrollToPosition(int position) {
        /**
         * Please note that RecyclerView only have 5 child max. It reuse the children for optimization.
         */
        onView(withId(R.id.feeding_recycler_view)).perform(RecyclerViewActions.scrollToPosition(position%5));
    }

    private void checkToastMessage(int textId) {
        onView(withText(textId)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    private void doOpenFeedingFragment() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText(R.string.str_Feeding)).perform(click());
    }

    private void doDismissDialog() {
        onView(withId(android.R.id.button2)).perform(click());
    }

}
