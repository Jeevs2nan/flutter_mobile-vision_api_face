package com.flutter.vision.flutter_mobile_vision_face_api.face;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.flutter.vision.flutter_mobile_vision_face_api.R;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PreviewPicture extends AppCompatActivity {

    private ImageView imageView;
    public static String IMAGE_DIRECTORY = "FlutterMobileVision";


    Button okBtn = null;
    Button cancelBtn = null;
    private String picturePath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (this.getSupportActionBar() != null)
                this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.preview_layout);

        imageView = findViewById(R.id.img);
        okBtn = (Button) findViewById(R.id.okBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkReadWritePermission()) {
                setImage();
            } else {
                checkAndRequestPermission(001);
            }
        } else {
            setImage();
        }

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkReadWritePermission()) {
                        faceDetect();
                    } else {
                        checkAndRequestPermission(002);
                    }
                } else {
                    faceDetect();
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CameraActivity.bitmap != null) {
                    CameraActivity.bitmap = null;
                }
                setResult(CommonStatusCodes.CANCELED);
                finish();
            }
        });
    }


    private void setImage() {
        if (CameraActivity.bitmap != null) {
            imageView.setImageBitmap(CameraActivity.bitmap);
        } else {
            Toast.makeText(this, "Image is missing. Try Again!", Toast.LENGTH_SHORT).show();
            setResult(CommonStatusCodes.CANCELED);
            finish();
        }
    }

    private void checkAndRequestPermission(int requestCode) {
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int rc1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> permissionList = new ArrayList<>();
        if (rc != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (rc1 != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (permissionList != null && permissionList.size() > 0) {
            String[] permissionArray = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissionArray, requestCode);
        }
    }


    private boolean checkReadWritePermission() {
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int rc1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return ((rc == PackageManager.PERMISSION_GRANTED) && (rc1 == PackageManager.PERMISSION_GRANTED));
    }

    private void faceDetect() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        Frame frame = new Frame.Builder().setBitmap(CameraActivity.bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);

        if (faces != null && faces.size() > 0) {
            Bitmap detectedFace = frame.getBitmap();
            Toast.makeText(this, "Face detected sucessfully!", Toast.LENGTH_SHORT).show();
            picturePath = saveImage(detectedFace);

            detector.release();

            if (CameraActivity.bitmap != null) {
                CameraActivity.bitmap = null;
            }

            Intent data = new Intent();
            data.putExtra(CameraActivity.OBJECT, picturePath);
            setResult(CommonStatusCodes.SUCCESS, data);
            finish();
        } else {
            detector.release();
            Toast.makeText(this, "Face not detected", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File file = new File(
                Environment.getExternalStorageDirectory() + "/" + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.

        if (!file.exists()) {
            Log.d("dirrrrrr", "" + file.mkdirs());
            file.mkdirs();
        }

        try {
            File f = new File(file, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();   //give read write permission
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case 001:
                for (int i = 0; i < permissions.length; i++) {
                    int grantResult = grantResults[i];

                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        checkAndRequestPermission(001);
                        break;
                    } else {
                        if (i == (permissions.length - 1)) {
                            setImage();
                        }
                    }

                }
                break;
            case 002:
                for (int i = 0; i < permissions.length; i++) {
                    int grantResult = grantResults[i];

                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        checkAndRequestPermission(002);
                        break;
                    } else {
                        if (i == (permissions.length - 1)) {
                            faceDetect();
                        }
                    }

                }
                break;
        }

    }

}
