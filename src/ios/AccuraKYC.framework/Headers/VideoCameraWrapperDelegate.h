//
//  VideoCameraWrapperDelegate.h
//  AccuraSDK
//
//  Created by Chang Alex on 1/26/20.
//  Copyright © 2020 Elite Development LLC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "ResultModel.h"

typedef NS_ENUM(NSUInteger, RecType) {
    REC_INIT = 1001,
    REC_BOTH,
    REC_FACE,
    REC_MRZ
};

typedef NS_ENUM(NSUInteger, RecogType) {
    OCR,
    PDF417,
    MRZ
};

@protocol VideoCameraWrapperDelegate <NSObject>
@optional
-(void)recognizeSucceedBarcode:(NSString*)message;
-(void)processedImage:(UIImage*)image;
-(void)recognizeFailed:(NSString*)message;
-(void)recognizeSucceed:(NSMutableDictionary*)scanedInfo recType:(RecType)recType bRecDone:(BOOL)bRecDone bFaceReplace:(BOOL)bFaceReplace bMrzFirst:(BOOL)bMrzFirst photoImage:(UIImage*)photoImage docFrontImage:(UIImage*)docFrontImage docbackImage:(UIImage*)docbackImage;
-(void)recognizSuccessBankCard:(NSMutableDictionary*)cardDetail andBankCardImage:(UIImage*)bankCardImage;

-(void)dlPlateNumber:(NSString*)plateNumber andImageNumberPlate:(UIImage*)imageNumberPlate;

-(void)resultData:(ResultModel*)resultmodel;
-(void)reco_msg:(NSString*)message;
-(void)livenessData:(UIImage*)livenessImage andshowImage:(UIImage*)showImage savedVideoPath:(NSString*)savedVideoPath imagePath:(NSString*)imagePath fmScore:(float)fmScore;
-(void)screenSound;
-(void)onUpdateLayout:(CGSize)frameSize :(float)borderRatio;
-(void)HandleCloseButton:(bool)isCloseButtonHide;
-(void)recognizeSucceedBarcode:(NSString*)message barcodeImage:(UIImage*)barcodeImage;


@end
