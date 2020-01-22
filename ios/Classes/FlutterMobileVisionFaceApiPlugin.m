#import "FlutterMobileVisionFaceApiPlugin.h"
#import <flutter_mobile_vision_face_api/flutter_mobile_vision_face_api-Swift.h>

@implementation FlutterMobileVisionFaceApiPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterMobileVisionFaceApiPlugin registerWithRegistrar:registrar];
}
@end
