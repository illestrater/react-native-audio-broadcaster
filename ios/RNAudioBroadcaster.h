
#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif

@class AudioProcessor;

@interface RNAudioBroadcaster : NSObject <RCTBridgeModule>

@property (retain, nonatomic) AudioProcessor *audioProcessor;

@end
  
