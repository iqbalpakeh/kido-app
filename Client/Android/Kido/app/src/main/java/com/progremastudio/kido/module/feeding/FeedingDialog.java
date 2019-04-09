/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.feeding;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.progremastudio.kido.R;
import com.progremastudio.kido.core.ActivityChild;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.widget.HistoryFragment;
import com.progremastudio.kido.widget.DurationPicker;

import java.util.Calendar;

public class FeedingDialog extends DialogFragment implements DurationPicker.Callback {

    private static final int STATE_FOOD_TYPE = 0;
    private static final int STATE_SOLID_FOOD = STATE_FOOD_TYPE + 1;
    private static final int STATE_LIQUID_FOOD = STATE_SOLID_FOOD + 1;
    private static final int STATE_BREAST_FEED = STATE_LIQUID_FOOD + 1;
    private static final int STATE_BREAST_PUMP = STATE_BREAST_FEED + 1;
    private static final int STATE_FORMULA_MILK = STATE_BREAST_PUMP + 1;
    private static final int STATE_DURATION_ENTRY = STATE_FORMULA_MILK + 1;

    private int state;
    private EditText foodNameEntryHandler;
    private EditText foodVolumeEntryHandler;
    private EditText entryFormulaVolumeHandler;
    private EditText pumpVolumeHandler;
    private FeedingModel entry;

    public static FeedingDialog getInstance() {
        return new FeedingDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View root = inflater.inflate(R.layout.dialog_feeding, null);
        createFeedingModel();
        createDurationPicker(root);
        createHandler(root);
        builder.setView(root);
        builder.setNegativeButton(R.string.str_Cancel, null);
        return builder.create();
    }

    private void createFeedingModel() {
        entry = new FeedingModel();
    }

    private void createDurationPicker(View root) {
        DurationPicker durationPicker = new DurationPicker(getActivity(), root);
        durationPicker.setCallback(this);
        durationPicker.setTitle(getResources().getString(R.string.str_Feeding_duration));
    }

    private void createHandler(View root) {
        Button solidFoodSelectorHandler = (Button) root.findViewById(R.id.solid_food_selector);
        solidFoodSelectorHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSolidFoodLayout();
            }
        });
        Button liquidFoodSelectorHandler = (Button) root.findViewById(R.id.liquid_food_selector);
        liquidFoodSelectorHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLiquidFoodLayout();
            }
        });
        foodNameEntryHandler = (EditText) root.findViewById(R.id.food_name_entry);
        foodVolumeEntryHandler = (EditText) root.findViewById(R.id.food_volume_entry);
        Button solidFoodConfirmHandler = (Button) root.findViewById(R.id.solid_food_confirm);
        solidFoodConfirmHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSolidFoodEntry();
            }
        });
        Button breastMilkSelectorHandler = (Button) root.findViewById(R.id.breastmilk_selector);
        breastMilkSelectorHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBreastMilkLayout();
            }
        });
        Button formulaMilkSelectorHandler = (Button) root.findViewById(R.id.formula_selector);
        formulaMilkSelectorHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFormulaLayout();
            }
        });
        entryFormulaVolumeHandler = (EditText) root.findViewById(R.id.entry_formula_volume);
        Button formulaConfirmHandler = (Button) root.findViewById(R.id.button_formula_confirm);
        formulaConfirmHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFormulaEntry();
            }
        });
        Button leftBreastMilkSelectorHandler = (Button) root.findViewById(R.id.left_breastmilk_selector);
        leftBreastMilkSelectorHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLeftBreastMilkLayout();
            }
        });
        Button rightBreastMilkSelectorHandler = (Button) root.findViewById(R.id.right_breastmilk_selector);
        rightBreastMilkSelectorHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRightBreastMilkLayout();
            }
        });
        Button pumpBreastMilkSelectorHandler = (Button) root.findViewById(R.id.pump_breastmilk_selector);
        pumpBreastMilkSelectorHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPumpBreastMilkLayout();
            }
        });
        pumpVolumeHandler = (EditText) root.findViewById(R.id.pump_volume);
        Button pumpAddHandler = (Button) root.findViewById(R.id.pump_pumping);
        pumpAddHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPumpStockEntry();
            }
        });
        Button pumpUseHandler = (Button) root.findViewById(R.id.pump_feeding);
        pumpUseHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPumpFeedingEntry();
            }
        });
    }

    private void createPumpFeedingEntry() {
        if (!checkPumpInput()) return;
        float pumpVolume = Float.valueOf(pumpVolumeHandler.getText().toString());
        if (!checkPumpingStockEnough(pumpVolume)) {
            Toast toast = Toast.makeText(getActivity(),
                    getResources().getString(R.string.str_Not_enough_remaining_stock), Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        entry.setType(FeedingModel.FeedingType.PUMP);
        entry.setVolume(0);
        entry.setPump(0 - pumpVolume);
        entry.setDuration(0);
        storeFeedingEntry();
        getDialog().dismiss();
        openFeedingFragment();
    }

    private boolean checkPumpingStockEnough(float pumpVolume) {
        return (calculatePumpingStockVolume() >= pumpVolume);
    }

    private void createPumpStockEntry() {
        if (!checkPumpInput()) return;
        entry.setType(FeedingModel.FeedingType.PUMP);
        entry.setVolume(0);
        entry.setPump(Float.valueOf(pumpVolumeHandler.getText().toString()));
        entry.setDuration(0);
        storeFeedingEntry();
        getDialog().dismiss();
        openFeedingFragment();
    }

    private boolean checkPumpInput() {
        // TODO: this type of checking can be refactored with other user input checking!!!
        if (pumpVolumeHandler.getText().toString().equals("")) {
            (Toast.makeText(getActivity(),
                    getString(R.string.str_Please_fill_pump_milk_volume), Toast.LENGTH_LONG)).show();
            return false;
        }
        if (Float.valueOf(pumpVolumeHandler.getText().toString()) > 99999.99f ) {
            (Toast.makeText(getActivity(),
                    getString(R.string.str_Volume_should_be_positive_and_maximum_is_9999999_mL), Toast.LENGTH_LONG)).show();
            return false;
        }
        if (Float.valueOf(pumpVolumeHandler.getText().toString()) <= 0.00f ) {
            (Toast.makeText(getActivity(),
                    getString(R.string.str_Volume_should_be_positive_and_maximum_is_9999999_mL), Toast.LENGTH_LONG)).show();
            return false;
        }
        return true;
    }

    private float calculatePumpingStockVolume() {
        String type;
        float pumpingStockVolume = 0;
        String[] timeFilterArg =
                HistoryFragment.createTimeFilter(getActivity(), HistoryFragment.TIME_FILTER_POSITION_THIS_WEEK);
        Cursor cursor = getActivity().getContentResolver().query(
                Contract.Feeding.CONTENT_URI,
                Contract.Feeding.Query.PROJECTION,
                "baby_id = ? AND timestamp >= ? AND timestamp <= ?",
                timeFilterArg,
                null
        );
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            type = cursor.getString(Contract.Feeding.Query.OFFSET_TYPE);
            if(type.equals(FeedingModel.FeedingType.PUMP.getTitle())) {
                pumpingStockVolume += Float.valueOf(cursor.getString(Contract.Feeding.Query.OFFSET_PUMP));
            }
        }
        return pumpingStockVolume;
    }

    private void selectRightBreastMilkLayout() {
        entry.setType(FeedingModel.FeedingType.RIGHT);
        entry.setPump(0);
        moveToState(STATE_DURATION_ENTRY);
    }

    private void selectLeftBreastMilkLayout() {
        entry.setType(FeedingModel.FeedingType.LEFT);
        entry.setPump(0);
        moveToState(STATE_DURATION_ENTRY);
    }

    private void selectSolidFoodLayout() {
        moveToState(STATE_SOLID_FOOD);
    }

    private void selectLiquidFoodLayout() {
        moveToState(STATE_LIQUID_FOOD);
    }

    private void selectBreastMilkLayout() {
        moveToState(STATE_BREAST_FEED);
    }

    private void selectPumpBreastMilkLayout() {
        moveToState(STATE_BREAST_PUMP);
    }

    private void selectFormulaLayout() {
        moveToState(STATE_FORMULA_MILK);
    }

    private void createSolidFoodEntry() {
        if (!checkSolidFoodInput()) {
            return;
        }
        entry.setType(FeedingModel.FeedingType.SOLID);
        entry.setName(foodNameEntryHandler.getText().toString());
        entry.setVolume(Float.valueOf(foodVolumeEntryHandler.getText().toString()));
        entry.setPump(0);
        entry.setDuration(0);
        storeFeedingEntry();
        getDialog().dismiss();
        openFeedingFragment();
    }

    private boolean checkSolidFoodInput() {
        // TODO: this type of checking can be refactored with other user input checking!!!
        if (foodNameEntryHandler.getText().toString().equals("")
                || foodVolumeEntryHandler.getText().toString().equals("")) {
            (Toast.makeText(getActivity(),
                    getString(R.string.str_Please_fill_in_food_name_and_amount), Toast.LENGTH_LONG)).show();
            return false;
        }
        if (foodNameEntryHandler.getText().toString().length() > 20) {
            (Toast.makeText(getActivity(),
                    getString(R.string.str_Food_name_should_be_less_than_20_characters), Toast.LENGTH_LONG)).show();
            return false;
        }
        if (Float.valueOf(foodVolumeEntryHandler.getText().toString()) > 99999.99f ) {
            (Toast.makeText(getActivity(),
                    getString(R.string.str_Amount_should_be_positive_and_maximum_is_9999999_gr), Toast.LENGTH_LONG)).show();
            return false;
        }
        if (Float.valueOf(foodVolumeEntryHandler.getText().toString()) <= 0.00f ) {
            (Toast.makeText(getActivity(),
                    getString(R.string.str_Amount_should_be_positive_and_maximum_is_9999999_gr), Toast.LENGTH_LONG)).show();
            return false;
        }
        return true;
    }

    private void createFormulaEntry() {
        if (!checkFormulaInput()) {
            return;
        }
        entry.setType(FeedingModel.FeedingType.FORMULA);
        entry.setVolume(Float.valueOf(entryFormulaVolumeHandler.getText().toString()));
        entry.setPump(0);
        entry.setDuration(0);
        storeFeedingEntry();
        getDialog().dismiss();
        openFeedingFragment();
    }

    private boolean checkFormulaInput() {
        // TODO: this type of checking can be refactored with other user input checking!!!
        if (entryFormulaVolumeHandler.getText().toString().equals("")) {
            (Toast.makeText(getActivity(),
                    getString(R.string.str_Please_fill_the_formula_milk_volume), Toast.LENGTH_LONG)).show();
            return false;
        }
        if (Float.valueOf(entryFormulaVolumeHandler.getText().toString()) > 99999.99f ) {
            (Toast.makeText(getActivity(),
                    getString(R.string.str_Volume_should_be_positive_and_maximum_is_9999999_mL), Toast.LENGTH_LONG)).show();
            return false;
        }
        if (Float.valueOf(entryFormulaVolumeHandler.getText().toString()) <= 0.00f ) {
            (Toast.makeText(getActivity(),
                    getString(R.string.str_Volume_should_be_positive_and_maximum_is_9999999_mL), Toast.LENGTH_LONG)).show();
            return false;
        }
        return true;
    }

    private void moveToState(int destinationState) {
        LinearLayout foodTypeLayout = (LinearLayout) getDialog().findViewById(R.id.layout_food_type);
        LinearLayout solidFoodLayout = (LinearLayout) getDialog().findViewById(R.id.layout_solid_food);
        LinearLayout liquidFoodLayout = (LinearLayout) getDialog().findViewById(R.id.layout_liquid_food);
        LinearLayout breastMilkLayout = (LinearLayout) getDialog().findViewById(R.id.layout_breastmilk);
        LinearLayout breastPumpLayout = (LinearLayout) getDialog().findViewById(R.id.layout_breastpump);
        LinearLayout durationLayout = (LinearLayout) getDialog().findViewById(R.id.layout_duration);
        LinearLayout formulaLayout = (LinearLayout) getDialog().findViewById(R.id.layout_formula);
        state = destinationState;
        switch (state) {
            case STATE_FOOD_TYPE:
                foodTypeLayout.setVisibility(View.VISIBLE);
                breastPumpLayout.setVisibility(View.GONE);
                solidFoodLayout.setVisibility(View.GONE);
                liquidFoodLayout.setVisibility(View.GONE);
                breastMilkLayout.setVisibility(View.GONE);
                durationLayout.setVisibility(View.GONE);
                formulaLayout.setVisibility(View.GONE);
                break;
            case STATE_SOLID_FOOD:
                solidFoodLayout.setVisibility(View.VISIBLE);
                breastPumpLayout.setVisibility(View.GONE);
                foodTypeLayout.setVisibility(View.GONE);
                liquidFoodLayout.setVisibility(View.GONE);
                breastMilkLayout.setVisibility(View.GONE);
                durationLayout.setVisibility(View.GONE);
                formulaLayout.setVisibility(View.GONE);
                break;
            case STATE_LIQUID_FOOD:
                liquidFoodLayout.setVisibility(View.VISIBLE);
                breastPumpLayout.setVisibility(View.GONE);
                foodTypeLayout.setVisibility(View.GONE);
                solidFoodLayout.setVisibility(View.GONE);
                breastMilkLayout.setVisibility(View.GONE);
                durationLayout.setVisibility(View.GONE);
                formulaLayout.setVisibility(View.GONE);
                break;
            case STATE_FORMULA_MILK:
                liquidFoodLayout.setVisibility(View.GONE);
                breastPumpLayout.setVisibility(View.GONE);
                foodTypeLayout.setVisibility(View.GONE);
                solidFoodLayout.setVisibility(View.GONE);
                breastMilkLayout.setVisibility(View.GONE);
                durationLayout.setVisibility(View.GONE);
                formulaLayout.setVisibility(View.VISIBLE);
                break;
            case STATE_BREAST_FEED:
                breastMilkLayout.setVisibility(View.VISIBLE);
                breastPumpLayout.setVisibility(View.GONE);
                foodTypeLayout.setVisibility(View.GONE);
                solidFoodLayout.setVisibility(View.GONE);
                liquidFoodLayout.setVisibility(View.GONE);
                durationLayout.setVisibility(View.GONE);
                formulaLayout.setVisibility(View.GONE);
                break;
            case STATE_BREAST_PUMP:
                breastMilkLayout.setVisibility(View.GONE);
                breastPumpLayout.setVisibility(View.VISIBLE);
                foodTypeLayout.setVisibility(View.GONE);
                solidFoodLayout.setVisibility(View.GONE);
                liquidFoodLayout.setVisibility(View.GONE);
                durationLayout.setVisibility(View.GONE);
                formulaLayout.setVisibility(View.GONE);
                break;
            case STATE_DURATION_ENTRY:
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String keyValue = sharedPref.getString(getString(R.string.var_KEY_COUNTER_METHOD_BREASTFEEDING), getString(R.string.str_Quick));
                if (keyValue.equals(getString(R.string.str_Quick))) {
                    durationLayout.setVisibility(View.VISIBLE);
                    breastMilkLayout.setVisibility(View.GONE);
                    breastPumpLayout.setVisibility(View.GONE);
                    foodTypeLayout.setVisibility(View.GONE);
                    solidFoodLayout.setVisibility(View.GONE);
                    liquidFoodLayout.setVisibility(View.GONE);
                } else  {
                    getDialog().dismiss();
                    startStopwatchCounting();
                }
                break;
        }
    }

    private void startStopwatchCounting() {
        Intent intent = new Intent(getActivity(), ActivityChild.class);
        intent.putExtra("FRAGMENT", "FEEDING_STOPWATCH");
        intent.putExtra("CREATE_OR_EDIT", getArguments().getString("CREATE_OR_EDIT"));
        intent.putExtra("ENTRY", entry);
        startActivity(intent);
    }

    @Override
    public void onDurationPickerOkClicked(Intent intent) {
        entry.setDuration(intent.getLongExtra("duration", 0));
        storeFeedingEntry();
        getDialog().dismiss();
        openFeedingFragment();
    }

    private void openFeedingFragment() {
        if (!ActiveContext.getCurrentFragment(getActivity()).equals(getString(R.string.str_Feeding))) {
            ActiveContext.setCurrentFragment(getActivity(), getString(R.string.str_Feeding));
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ActiveContext.setCurrentFragment(getActivity(), getString(R.string.str_Feeding));
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.home_activity_container, FeedingFragment.getInstance(), getString(R.string.str_Feeding))
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .commit();
        }
    }

    private void storeFeedingEntry() {
        Bundle bundle = getArguments();
        if (bundle.getString("CREATE_OR_EDIT").equals("EDIT")) {
            entry.setActivityId(Long.valueOf(bundle.getString("TAG_ACTIVITY")));
            entry.edit(getActivity());
        } else if (bundle.getString("CREATE_OR_EDIT").equals("CREATE")) {
            entry.setTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            entry.setBabyID(ActiveContext.getActiveBaby(getActivity()).getActivityId());
            entry.setFamilyId(ActiveContext.getActiveBaby(getActivity()).getFamilyId());
            entry.insert(getActivity());
        }
    }
}
