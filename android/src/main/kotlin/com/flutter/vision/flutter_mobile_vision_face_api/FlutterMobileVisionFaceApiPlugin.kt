package com.flutter.vision.flutter_mobile_vision_face_api

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult

import androidx.core.content.ContextCompat
import com.flutter.vision.flutter_mobile_vision_face_api.face.AbstractActivity
import com.flutter.vision.flutter_mobile_vision_face_api.face.CameraActivity
import com.google.android.gms.common.api.CommonStatusCodes
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class FlutterMobileVisionFaceApiPlugin: MethodCallHandler, PluginRegistry.ActivityResultListener {

    private var folderName: String? = ""
    private var registrar: Registrar? = null
    private var finalResult: Result? = null


    companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
        val channel = MethodChannel(registrar.messenger(), "flutter_mobile_vision_face_api")

        val flutterMobileVisionFaceApiPlugin = FlutterMobileVisionFaceApiPlugin()
        flutterMobileVisionFaceApiPlugin.setRegister(registrar)
      channel.setMethodCallHandler(flutterMobileVisionFaceApiPlugin)
        registrar.addActivityResultListener(flutterMobileVisionFaceApiPlugin)
    }
        val FOLDER_NAME: String = "FOLDER_NAME";
        const val FLUTTER_MOBILE_VISION = 1
    }

    fun setRegister(registrar: Registrar) {
        this.registrar = registrar
    }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if (call.method == "face") {
        var arguments :HashMap<String, Objects> = HashMap()

        if (call.arguments != null) {
            arguments = call.arguments as HashMap<String, Objects>
        }

        if (arguments.get(FOLDER_NAME) != null) {
            folderName = arguments.get(FOLDER_NAME) as String
        }

        if (registrar != null) {
            val activity: Activity = registrar?.activity()!!

            val intent: Intent = Intent(activity, CameraActivity::class.java)
            intent.putExtra(FOLDER_NAME, folderName)
            startActivityForResult(activity, intent, FLUTTER_MOBILE_VISION, null)
        } else {
            //result.error("Registrar is null", "", "")
            result.success("Registrar is null")
        }
    }
    else {
      result.notImplemented()
    }
      finalResult = result


  }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent): Boolean {
//        val activity: Activity = registrar?.activity()!!
//        Toast.makeText(activity, "In FLUTTER_MOBILE_VISION", Toast.LENGTH_SHORT).show()
        // Check which request we're responding to
        if (requestCode == Companion.FLUTTER_MOBILE_VISION) {

            // Make sure the request was successful
            if (resultCode == CommonStatusCodes.SUCCESS) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)

                    if (data != null && data.extras != null) {
                        val picturePath: String = data.extras.getString(AbstractActivity.OBJECT)

                        finalResult?.success(picturePath)
                    } else {
                        finalResult?.error("Picture Path is missing!!", "Missing!!", "Picture Path is missing!!")
                    }
                return true
            } else  if (resultCode == CommonStatusCodes.ERROR) {
                val e: String = data.getStringExtra(AbstractActivity.ERROR)
                finalResult?.error(e, "Cancelled", "Cancelled")
                return true
            } else {
                finalResult?.error("Cancelled", "Cancelled", "")
            }
        } else {
            finalResult?.error("Intent is (null)", "", "")
        }
        return false
    }

}
