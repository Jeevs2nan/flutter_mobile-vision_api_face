package com.flutter.vision.flutter_mobile_vision_face_api.face;

import android.content.Intent;

import com.google.android.gms.common.api.CommonStatusCodes;

import androidx.appcompat.app.AppCompatActivity;

public class AbstractActivity extends AppCompatActivity {

    public static String FOLDER_NAME = "FOLDER_NAME";
    public static String OBJECT = "OBJECT";
    public static String ERROR = "OBJECT";

    private void onError(Exception e) {
        Intent data = new Intent();
        data.putExtra(ERROR, e.getMessage());
        setResult(CommonStatusCodes.ERROR);
        finish();
    }
}
