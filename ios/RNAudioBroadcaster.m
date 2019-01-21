
#import "RNAudioBroadcaster.h"
#import "AudioProcessor.h"

@implementation RNAudioBroadcaster
@synthesize audioProcessor;

- (dispatch_queue_t)methodQueue
{
    dispatch_queue_attr_t queueAttrs = dispatch_queue_attr_make_with_qos_class(
        DISPATCH_QUEUE_SERIAL,
        QOS_CLASS_USER_INITIATED, 
        0
    );

    return dispatch_queue_create("com.cuenative.BroadcastQueue", queueAttrs);
}
RCT_EXPORT_MODULE()

// - (RNAudioBroadcaster *)init
// {
//    self = [super init];
//    if (self) {
//         // if (audioProcessor == nil) {
//         //     audioProcessor = [[AudioProcessor alloc] init];
//         //     [audioProcessor start];
//         // }
//         NSLog(@"initializing icecast straeam");
//    }

//    return self;
// }

RCT_EXPORT_METHOD(init: (NSDictionary *)settings withCallback:(RCTResponseSenderBlock) callback)
{
    NSLog(@"initializing");
    if (audioProcessor == nil) {
        audioProcessor = [[AudioProcessor alloc] init];
        [audioProcessor start:[settings objectForKey:@"url"] withPort:[settings objectForKey:@"port"] withPassword:[settings objectForKey:@"password"] withMount:[settings objectForKey:@"mount"]];
    }

    callback(@[@{@"STATE": @"BROADCASTING"}]);
}

RCT_EXPORT_METHOD(stop)
{
    NSLog(@"STOPPING");
    if (audioProcessor != nil) {
        [audioProcessor stop];
        audioProcessor = nil;
    }
    // callback(@[@{@"STATE": @"STOPPED"}]);
}

RCT_EXPORT_METHOD(levels: (RCTResponseSenderBlock) callback)
{
    NSLog(@"GETTING LEVELS");
    if (audioProcessor != nil) {
        float level = [audioProcessor getLevels];
        callback(@[@{@"level": [NSString stringWithFormat:@"%f", level]}]);
    } else {
        callback(@[@{@"level": @"WTF"}]);
    }
}

@end
  
