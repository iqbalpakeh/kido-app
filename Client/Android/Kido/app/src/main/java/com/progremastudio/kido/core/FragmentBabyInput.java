/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.core;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.progremastudio.kido.R;
import com.progremastudio.kido.models.Baby;
import com.progremastudio.kido.models.BaseActor;
import com.progremastudio.kido.provider.Contract;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.util.TextFormation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

public class FragmentBabyInput extends Fragment implements DatePickerDialog.OnDateSetListener {

    private final int INTENT_CAMERA = 0;
    private final int INTENT_GALLERY = INTENT_CAMERA + 1;
    private final int INTENT_PICTURE = INTENT_GALLERY + 1;
    private final int PERMISSION_REQUEST_CAMERA = 0;
    private final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = PERMISSION_REQUEST_CAMERA + 1;
    private EditText nameHandler;
    private Button birthdayHandler;
    private Spinner sexHandler;
    private ImageButton acceptHandler;
    private ImageButton cancelHandler;
    private ImageButton imageHandler;
    private View root;
    private ArrayAdapter<String> adapter;
    private String babyName, babyBirthday, babySexType;
    private Uri tmpImageUri;
    private Bitmap imageBitmap;
    private String timestamp;
    private Calendar birthday;
    private Calendar now;
    private int year, month, date;

    public static FragmentBabyInput getInstance() {
        return new FragmentBabyInput();
    }

    /**********************************************************************************************
     * Framework Callback Sections
     **********************************************************************************************/

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_baby_input, container, false);
        prepareExternalStorage();
        prepareCalendar();
        prepareNameHandler();
        prepareBirthdayHandler();
        prepareSexHandler();
        prepareAcceptHandler();
        prepareCancelHandler();
        prepareImageHandler();
        return root;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.date = dayOfMonth;
        birthday.set(year, monthOfYear, dayOfMonth);
        birthdayHandler.setText(TextFormation.date(getActivity(), String.valueOf(birthday.getTimeInMillis())));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case INTENT_GALLERY:
                    processImageFromGallery(data);
                    break;
                case INTENT_CAMERA:
                    processImageFromCamera(data);
                    break;
                case INTENT_PICTURE:
                    showFinalImage(data);
                    break;
            }
        }
    }

    /**********************************************************************************************
     * UI Callback Sections
     **********************************************************************************************/
    private void onCancelClick() {
        deleteTemporaryBitmap();
        if (ActiveContext.isBabyCreated(getActivity())) {
            goToHomeActivity();
        } else {
            warnUser(getString(R.string.str_No_baby_yet_Please_fill_in_your_baby_data));
        }
    }

    private void onBirthdayClick() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, date);
        datePickerDialog.show();
    }

    private void onAcceptClick() {
        if (checkUserInput()) {
            deleteTemporaryBitmap();
            saveBitmapOnDirectory();
            getBabyProperty();
            storeBabyToDataBase();
            setActiveBabyContext();
            skipLoginNextStartup();
            goToHomeActivity();
        }
    }

    private void onImageClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.str_Add_baby_image);
        builder.setItems(R.array.add_image_method, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    onCameraClick();
                } else {
                    onGalleryClick();
                }
            }
        });
        builder.setNegativeButton(R.string.str_Cancel, null);
        builder.show();
    }

    private void onCameraClick() {
        if ((android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            launchCamera();
        }
    }

    private void prepareExternalStorage() {
        if ((android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            tmpImageUri = Uri.fromFile(createImageOnDirectory());
        }
    }

    private void launchCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tmpImageUri);
            startActivityForResult(intent, INTENT_CAMERA);
        } catch (ActivityNotFoundException error) {
            String errorMessage = getString(R.string.str_Device_is_not_supporting_image_capture);
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void onGalleryClick() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tmpImageUri);
            startActivityForResult(intent, INTENT_GALLERY);
        } catch (ActivityNotFoundException error) {
            String errorMessage = getString(R.string.str_Gallery_is_not_exist_on_device);
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**********************************************************************************************
     * UI Callback Binding Section
     **********************************************************************************************/

    private void prepareCalendar() {
        birthday = Calendar.getInstance();
        now = Calendar.getInstance();
        year = now.get(Calendar.YEAR);
        month = now.get(Calendar.MONTH);
        date = now.get(Calendar.DATE);
    }

    private void prepareNameHandler() {
        nameHandler = (EditText) root.findViewById(R.id.baby_input_name);
    }

    private void prepareBirthdayHandler() {
        birthdayHandler = (Button) root.findViewById(R.id.baby_input_birthday);
        birthdayHandler.setText(TextFormation.date(getActivity(),
                String.valueOf(Calendar.getInstance().getTimeInMillis())));
        birthdayHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBirthdayClick();
            }
        });
    }

    private void prepareSexHandler() {
        sexHandler = (Spinner) root.findViewById(R.id.baby_input_sex);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.gender_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexHandler.setAdapter(adapter);
    }

    private void prepareAcceptHandler() {
        acceptHandler = (ImageButton) root.findViewById(R.id.baby_input_accept);
        acceptHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAcceptClick();
            }
        });
    }

    private void prepareCancelHandler() {
        cancelHandler = (ImageButton) root.findViewById(R.id.baby_input_cancel);
        cancelHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClick();
            }
        });
    }

    private void prepareImageHandler() {
        imageHandler = (ImageButton) root.findViewById(R.id.baby_input_image);
        imageHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    // todo: implement app if camera is not granted
                }
                return;
            }
            case PERMISSION_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tmpImageUri = Uri.fromFile(createImageOnDirectory());
                } else {
                    // todo: implement app if camera is not granted
                }
                return;
            }
        }
    }

    /**********************************************************************************************
     * Private Helper Sections
     **********************************************************************************************/

    private void processImageFromCamera(Intent data) {
        performImageCrop(tmpImageUri, tmpImageUri);
    }

    private void processImageFromGallery(Intent data) {
        performImageCrop(data.getData(), tmpImageUri);
    }

    private void showFinalImage(Intent data) {
        Bundle bundle = data.getExtras();
        try {
            imageBitmap = bundle.getParcelable("data");
            imageBitmap = getRoundedCornerBitmap(imageBitmap);
            imageHandler.setImageBitmap(imageBitmap);
        } catch (Exception e) {
            Log.e("_DBG_IMAGE", Log.getStackTraceString(e));
        }
    }

    /**
     * launch image crop activity
     *
     * @param srcUri source image URI
     * @param dstUri destination image URI (crop result)
     */
    private void performImageCrop(Uri srcUri, Uri dstUri) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(srcUri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 300);
            intent.putExtra("return-data", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, dstUri);
            startActivityForResult(intent, INTENT_PICTURE);
        } catch (ActivityNotFoundException error) {
            String errorMessage = getString(R.string.str_Device_is_not_supporting_image_crop);
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
            performDefaultImageCrop();
        }
    }

    private void performDefaultImageCrop() {
        try {
            imageBitmap = decodeUri(tmpImageUri, getActivity());
            imageHandler.setImageBitmap(imageBitmap);
        } catch (Exception e) {
            Log.e("_DBG_IMAGE", Log.getStackTraceString(e));
        }
    }

    private Bitmap decodeUri(Uri selectedImage, Context inputContext) throws FileNotFoundException {
        int REQUIRED_SIZE = 140;
        BitmapFactory.Options optionsOne = new BitmapFactory.Options();
        BitmapFactory.Options optionsTwo = new BitmapFactory.Options();
        int width = optionsOne.outWidth, height = optionsOne.outHeight;
        int scale = 1;
        optionsOne.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputContext.getContentResolver().openInputStream(selectedImage), null, optionsOne);
        while (true) {
            if (width / 2 < REQUIRED_SIZE || height / 2 < REQUIRED_SIZE) break;
            width /= 2;
            height /= 2;
            scale *= 2;
        }
        optionsTwo.inSampleSize = scale;
        return BitmapFactory.decodeStream(inputContext.getContentResolver().openInputStream(selectedImage), null, optionsTwo);
    }

    private File createImageOnDirectory() {
        File imageDirectory = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
        if (!imageDirectory.exists()) imageDirectory.mkdir();
        return (new File(imageDirectory.getPath() + File.separator + "Kido_Tmp" + ".jpg"));
    }

    private Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        int color = 0xff424242;
        float roundPx = 10;
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private void saveBitmapOnDirectory() {
        timestamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
        File imageDirectory = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
        try {
            FileOutputStream out = new FileOutputStream(imageDirectory.getPath() + File.separator + "Kido_" + timestamp + ".jpg");
            tmpImageUri = Uri.parse("file://" + imageDirectory.getPath() + File.separator + "Kido_" + timestamp + ".jpg");
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBabyProperty() {
        Calendar birthday = Calendar.getInstance();
        birthday.set(year, month, date);
        babyBirthday = String.valueOf(birthday.getTimeInMillis());
        babySexType = (String) sexHandler.getAdapter().getItem(sexHandler.getSelectedItemPosition());
        babyName = nameHandler.getText().toString();
    }

    private void storeBabyToDataBase() {
        Baby baby = new Baby();
        baby.setName(babyName);
        baby.setFamilyId(""); // todo: to add family id
        baby.setBirthday(babyBirthday);
        baby.setPicture(tmpImageUri);
        if (babySexType.equals(BaseActor.Sex.MALE.getTitle())) {
            baby.setSex(BaseActor.Sex.MALE);
        } else if (babySexType.equals(BaseActor.Sex.FEMALE.getTitle())) {
            baby.setSex(BaseActor.Sex.FEMALE);
        }
        if (getArguments().getString("CREATE_OR_EDIT").equals("EDIT")) baby.edit(getActivity());
        else baby.insert(getActivity());
    }

    private void setActiveBabyContext() {
        ActiveContext.setActiveBaby(getActivity(), babyName);
    }

    private void skipLoginNextStartup() {
        SharedPreferences setting = getActivity().getSharedPreferences(ActivityLogin.FLAG_LOGIN, 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putBoolean(ActivityLogin.FLAG_SKIP_LOGIN, true);
        editor.commit();
    }

    private void goToHomeActivity() {
        startActivity(new Intent(getActivity(), ActivityHome.class));
        getActivity().finish();
    }

    private void deleteTemporaryBitmap() {
        File imageDirectory = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
        File tempFile = new File(imageDirectory.getPath() + File.separator + "Kido_Tmp.jpg");
        if (tempFile.exists()) tempFile.delete();
    }

    private boolean checkUserInput() {
        if (nameHandler.getText().toString().equals("")) {
            warnUser(getString(R.string.str_Please_give_baby_name));
            return false;
        }
        if (imageBitmap == null) {
            warnUser(getString(R.string.str_Please_give_baby_picture));
            return false;
        }
        if (checkBabyName() != true) {
            warnUser(getString(R.string.str_This_name_is_exist_Please_use_different_name));
            return false;
        }
        if (checkDateEarlierThanNow() != true) {
            warnUser(getString(R.string.str_Please_select_date_earlier_than_today));
            return false;
        }
        return true;
    }

    private boolean checkBabyName() {
        String[] selectionArg = {nameHandler.getText().toString()};
        Cursor cursor = getActivity().getContentResolver().query(Contract.Baby.CONTENT_URI,
                Contract.Baby.Query.PROJECTION,
                "name == ?",
                selectionArg,
                Contract.Baby.NAME);
        if(cursor.getCount() > 0) {
            return false;
        }
        return true;
    }

    private boolean checkDateEarlierThanNow() {
        if (birthday.getTimeInMillis() > now.getTimeInMillis()) return false;
        return true;
    }

    private void warnUser(String message) {
        (Toast.makeText(getActivity(), message, Toast.LENGTH_LONG)).show();
    }
}
