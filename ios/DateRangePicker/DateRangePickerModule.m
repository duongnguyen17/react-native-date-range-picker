//
//  DateRangePickerModule.m
//  DateRangePicker
//
//  Created by Datacom Vietnam on 18/07/2023.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(DateRangePickerModule, NSObject)

RCT_EXTERN_METHOD(present:(NSDictionary *)config
                  doneHandler:(RCTResponseSenderBlock)doneHandler
//                    dismissHandler:(RCTResponseSenderBlock)dismissHandler
                  )

@end

