# flutter_mobile_vision_face_api

Flutter plugin for google mobile vision face api. 

# Current Release is for Android only. 

This plugin helps to detect face in a captured picture and returns the path of the picture.

# Dependencies

Add the below line in <b>pubspec.yaml</b>. For the latest version, please visit https://pub.dev/packages/flutter_mobile_vision_api_face


```
flutter_mobile_vision_api_face: ^0.0.3
```


<b> Permission </b>

Need to add the below permission in <b> AndroidManifest.xml</b>

```
<uses-feature android:name="android.hardware.camera" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```


Since <b> google mobile vision api </b> is used in this library to detect face, need to add the below lines in <b> AndroidManifest.xml</b> inside the <b> Application </b> tag


```
<meta-data
    android:name="com.google.android.gms.version"
    android:value="@integer/google_play_services_version" />

<meta-data
    android:name="com.google.android.gms.vision.DEPENDENCIES"
    android:value="face" />
 ```
    
After adding the permissions and meta tags, need to add the activity developed in this plugin.

```
 <activity android:name="com.flutter.vision.flutter_mobile_vision_face_api.face.CameraActivity" />
 <activity android:name="com.flutter.vision.flutter_mobile_vision_face_api.face.PreviewPicture" />
````

Make Sure <b> Minimum SDK version is 21 </b>.


Finally add the below code in your dart file, 

```
FlutterMobileVisionFaceApi.face();
```



