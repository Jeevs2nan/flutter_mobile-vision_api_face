import 'dart:async';

import 'package:flutter/services.dart';

class FlutterMobileVisionFaceApi {
  static const MethodChannel _channel =
      const MethodChannel('flutter_mobile_vision_face_api');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> face(
      {String folderName: 'FlutterMobileVision'}) async {
    Map<String, dynamic> arguments = {
      'FOLDER_NAME': folderName,
    };

    String result = await _channel.invokeMethod('face', arguments);
    return result;
  }
}
