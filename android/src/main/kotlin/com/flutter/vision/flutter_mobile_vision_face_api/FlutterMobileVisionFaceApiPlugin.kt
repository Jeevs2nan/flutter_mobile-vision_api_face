package com.flutter.vision.flutter_mobile_vision_face_api

import android.app.Activity
import android.content.Intent
import android.support.v4.app.ActivityCompat.startActivityForResult
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

class FlutterMobileVisionFaceApiPlugin: MethodCallHandler {

    private var folderName: String? = ""
    private var registrar: Registrar? = null
    private var finalResult: Result? = null


    companion object {
    @JvmStatic
    val FOLDER_NAME: String = "FOLDER_NAME";
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "flutter_mobile_vision_face_api")
      channel.setMethodCallHandler(FlutterMobileVisionFaceApiPlugin())
    }
        const val FLUTTER_MOBILE_VISION = 1
    }

    fun FlutterMobileVisionFaceApiPlugin(registrar: Registrar) {
        this.registrar = registrar
    }

  override fun onMethodCall(call: MethodCall, result: Result) {
   /* if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else {
      result.notImplemented()
    }*/
      finalResult = result

  var arguments :HashMap<String, Objects> = HashMap()

    if (call.arguments != null) {
        arguments = call.arguments as HashMap<String, Objects>
    }

    if (arguments.get(FOLDER_NAME) != null) {
        folderName = arguments.get(FOLDER_NAME) as String
    }

      var activity :Activity  = registrar?.activity()!!

      val intent: Intent  = Intent (registrar?.activeContext(), CameraActivity::class.java)
      intent.putExtra(FOLDER_NAME, folderName)
      startActivityForResult(activity, intent, FLUTTER_MOBILE_VISION, null)
  }


        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // Check which request we're responding to
        if (requestCode == Companion.FLUTTER_MOBILE_VISION) {
            // Make sure the request was successful
            if (resultCode == CommonStatusCodes.SUCCESS) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)


                    var picturePath : String = data.getStringExtra(AbstractActivity.OBJECT)

                    finalResult?.success(picturePath)
            } else  if (resultCode == CommonStatusCodes.ERROR) {
                val e: String = data.getStringExtra(AbstractActivity.ERROR)
                finalResult?.error(e, null, null)
            }
        } else {
            finalResult?.error("Intent is (null)", null, null)
        }
    }

}
