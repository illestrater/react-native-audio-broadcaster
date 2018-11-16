
#import "RNAudioBroadcaster.h"
#import "AudioProcessor.h"

@implementation RNAudioBroadcaster
@synthesize audioProcessor;

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
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
  
