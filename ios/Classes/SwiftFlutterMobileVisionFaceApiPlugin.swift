import Flutter
import UIKit

public class SwiftFlutterMobileVisionFaceApiPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "flutter_mobile_vision_face_api", binaryMessenger: registrar.messenger())
    let instance = SwiftFlutterMobileVisionFaceApiPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result("iOS " + UIDevice.current.systemVersion)
  }
}
