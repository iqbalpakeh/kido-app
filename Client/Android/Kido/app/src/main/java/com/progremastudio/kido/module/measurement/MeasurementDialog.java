/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.module.measurement;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.progremastudio.kido.R;
import com.progremastudio.kido.util.ActiveContext;
import com.progremastudio.kido.util.TextFormation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

public class MeasurementDialog extends DialogFragment {

    private static final int INTENT_CAMERA = 0;
    private static final int INTENT_CROP = INTENT_CAMERA + 1;

    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = PERMISSION_REQUEST_CAMERA + 1;

    private String babyHeight;
    private String babyWeight;
    private String babyHeadCircumference;
    private String babyImage;
    private String timestamp;
    private EditText weightInput;
    private EditText heightInput;
    private EditText headCircumferenceInput;
    private ImageButton pictureInput;
    private Uri cameraImageUri;
    private Bitmap imageBitmap;

    public static MeasurementDialog getInstance() {
        return new MeasurementDialog();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View root = inflater.inflate(R.layout.dialog_growth, null);
        createCameraUri();
        createHandler(root);
        builder.setView(root);
        builder.setNegativeButton(R.string.str_Cancel, null)
                .setPositiveButton(R.string.str_OK,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                onPositiveButtonClick();
                            }
                        });
        return builder.create();
    }

    private void createHandler(View root) {
        weightInput = (EditText) root.findViewById(R.id.entry_text_weight);
        heightInput = (EditText) root.findViewById(R.id.entry_text_height);
        headCircumferenceInput = (EditText) root.findViewById(R.id.entry_text_head);
        pictureInput = (ImageButton) root.findViewById(R.id.baby_input_image);
        pictureInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraClick();
            }
        });
    }

    private void createCameraUri() {
        if ((android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            cameraImageUri = Uri.fromFile(createImageOnDirectory());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deleteTemporaryBitmap();
    }

    private void onCameraClick() {
        if ((android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
            startActivityForResult(intent, INTENT_CAMERA);
        } catch (ActivityNotFoundException error) {
            String errorMessage = getString(R.string.str_Device_is_not_supporting_image_capture);
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
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
                    cameraImageUri = Uri.fromFile(createImageOnDirectory());
                } else {
                    // todo: implement app if camera is not granted
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case INTENT_CAMERA:
                processImage(resultCode);
                break;
            case INTENT_CROP:
                showFinalImage(resultCode, data);
                break;
        }
    }

    private void showFinalImage(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle;
            bundle = data.getExtras();
            imageBitmap = bundle.getParcelable("data");
            imageBitmap = getRoundedCornerBitmap(imageBitmap);
            pictureInput.setImageBitmap(imageBitmap);
        }
    }

    private void processImage(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            performImageCrop(Uri.parse(cameraImageUri.toString()));
        }
    }

    private void performImageCrop(Uri selectedImage) {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(selectedImage, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 225);
            intent.putExtra("outputY", 225);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, INTENT_CROP);
        } catch (ActivityNotFoundException error) {
            warnUser(getString(R.string.str_Device_is_not_supporting_image_crop));
            performDefaultImageCrop();
        }
    }

    private void performDefaultImageCrop() {
        try {
            imageBitmap = decodeUri(Uri.parse(cameraImageUri.toString()), getActivity());
            pictureInput.setImageBitmap(imageBitmap);
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
        return (new File(imageDirectory.getPath() + File.separator + "Kido_Tmp.jpg"));
    }

    private void onPositiveButtonClick() {
        if (checkUserInput()) {
            deleteTemporaryBitmap();
            saveBitmapOnDirectory();
            setHeight();
            setWeight();
            setHead();
            setImage();
            if (!checkEntryFormat()) return;
            submitAndDismissDialog();
        }
    }

    private boolean checkUserInput() {
        if(weightInput.getText().toString().equals("")) {
            warnUser(getString(R.string.str_Please_give_baby_weight));
            return false;
        }
        if(heightInput.getText().toString().equals("")) {
            warnUser(getString(R.string.str_Please_give_baby_height));
            return false;
        }
        if(headCircumferenceInput.getText().toString().equals("")) {
            warnUser(getString(R.string.str_Please_give_baby_head_circumference_size));
            return false;
        }
        if(imageBitmap == null) {
            warnUser(getString(R.string.str_Please_give_baby_picture));
            return false;
        }
        return true;
    }

    private void warnUser(String message) {
        (Toast.makeText(getActivity(), message, Toast.LENGTH_LONG)).show();
    }

    private void deleteTemporaryBitmap() {
        File imageDirectory = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
        File tempFile = new File(imageDirectory.getPath() + File.separator + "Kido_Tmp.jpg");
        if (tempFile.exists()) tempFile.delete();
    }

    private void saveBitmapOnDirectory() {
        timestamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
        File imageDirectory = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
        try {
            FileOutputStream out = new FileOutputStream(imageDirectory.getPath() + File.separator + "Kido_" + timestamp + ".jpg");
            cameraImageUri = Uri.parse("file://" + imageDirectory.getPath() + File.separator + "Kido_"  + timestamp + ".jpg");
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHeight() {
        babyHeight = heightInput.getText().toString();
    }

    private void setWeight() {
        babyWeight = weightInput.getText().toString();
    }

    private void setHead() {
        babyHeadCircumference = headCircumferenceInput.getText().toString();
    }

    private void setImage() {
        babyImage = cameraImageUri.toString();
    }

    private boolean checkEntryFormat() {
        if (!TextFormation.checkValidNumber(babyHeight) ||
                !TextFormation.checkValidNumber(babyWeight) ||
                !TextFormation.checkValidNumber(babyHeadCircumference)) {
            (Toast.makeText(getActivity(), getString(R.string.str_Invalid_number), Toast.LENGTH_LONG)).show();
            return false;
        }
        return true;
    }

    private void submitAndDismissDialog() {
        Bundle bundle = getArguments();
        if (bundle.getString("CREATE_OR_EDIT").equals("EDIT")) {
            MeasurementModel model = new MeasurementModel();
            model.setActivityId(Long.valueOf(bundle.getString("TAG_ACTIVITY")));
            model.setHeight(Float.valueOf(babyHeight));
            model.setWeight(Float.valueOf(babyWeight));
            model.setHead(Float.valueOf(babyHeadCircumference));
            model.setPicture(babyImage);
            model.edit(getActivity());
        } else if (bundle.getString("CREATE_OR_EDIT").equals("CREATE")) {
            MeasurementModel model = new MeasurementModel();
            model.setBabyID(ActiveContext.getActiveBaby(getActivity()).getActivityId());
            model.setFamilyId(ActiveContext.getActiveBaby(getActivity()).getFamilyId());
            model.setTimeStamp(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            model.setHeight(Float.valueOf(babyHeight));
            model.setWeight(Float.valueOf(babyWeight));
            model.setHead(Float.valueOf(babyHeadCircumference));
            model.setPicture(babyImage);
            model.insert(getActivity());
        }
        getDialog().dismiss();
        openGrowFragment();
    }

    private void openGrowFragment() {
        if (!ActiveContext.getCurrentFragment(getActivity()).equals(getString(R.string.str_Growth))) {
            ActiveContext.setCurrentFragment(getActivity(), getString(R.string.str_Growth));
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ActiveContext.setCurrentFragment(getActivity(), getString(R.string.str_Growth));
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.home_activity_container, MeasurementFragment.getInstance(), getString(R.string.str_Growth))
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .commit();
        }
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
}
