package com.flutter.vision.flutter_mobile_vision_face_api.face;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flutter.vision.flutter_mobile_vision_face_api.R;
import com.flutter.vision.flutter_mobile_vision_face_api.face.custom.CameraPreview;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import androidx.annotation.RequiresApi;

public class CameraActivity extends AbstractActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private ImageView capture, switchCamera;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    public static Bitmap bitmap;
    int cameraId;
    private boolean safeToTakePicture = false;
    String TAG  = CameraActivity.class.getName();
    private String folderName = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (this.getSupportActionBar() != null)
                this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.camera_layout);


        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {

        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            folderName = bundle.getString(FOLDER_NAME);
            if (!TextUtils.isEmpty(folderName)) {
                PreviewPicture.IMAGE_DIRECTORY = folderName;
            }
        }

        myContext = this;

        mCamera = Camera.open();

        mCamera.setDisplayOrientation(90);
        cameraPreview = (LinearLayout) findViewById(R.id.cPreview);
        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        capture = (ImageView) findViewById(R.id.btnCam);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (safeToTakePicture) {
                    mCamera.takePicture(null, null, mPicture);
                    safeToTakePicture = false;
                }
            }
        });

        switchCamera = (ImageView) findViewById(R.id.btnSwitch);
        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the number of cameras
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    //release the old camera instance
                    //switch camera, from the front and the back and vice versa

                    releaseCamera();
                    chooseCamera();
                } else {

                }
            }
        });


        //Start the Camera Preview...
        mCamera.startPreview();
        safeToTakePicture = true;
    }



    private int findFrontFacingCamera() {

        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;

    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;

            }

        }
        return cameraId;
    }


    public void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
            Log.d(TAG, "null");
        } else {
            Log.d(TAG, "no null");
        }

    }

    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview
                mCamera = Camera.open(cameraId);
                mCamera.setDisplayOrientation(90);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                bitmap = loadAndResizeBitmap(data);
                safeToTakePicture = true;
                //saveImage(bitmap);
                Intent intent = new Intent(CameraActivity.this, PreviewPicture.class);
                startActivity(intent);
            }
        };
        return picture;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    Bitmap loadAndResizeBitmap(byte[] data)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        int REQUIRED_SIZE = 100;
        int width_tmp = options.outWidth, height_tmp = options.outHeight;
        int scale = 4;
        while (true)
        {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale++;
        }

        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        Bitmap resizedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

        ExifInterface exif = null;
        try
        {
            ByteArrayInputStream bs = new ByteArrayInputStream(data);
            exif = new ExifInterface(bs);
            String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);

            Matrix matrix = new Matrix();


            if (cameraFront) {
                matrix.preRotate(-90);
            } else {
                matrix.preRotate(90);
            }
            resizedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight(), matrix, false);

            matrix = null;


            return resizedBitmap;
        }
        catch (IOException ex)
        {
            return null;
        }
    }

}
