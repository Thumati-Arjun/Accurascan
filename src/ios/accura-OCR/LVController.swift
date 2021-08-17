//
//  LVController.swift
//  Accura ORC and Facematch
//
//  Created by apple on 26/07/2021.
//

import UIKit
import AccuraKYC

class LVController: UIViewController, LivenessData
{
    
    var audioPath: URL? = nil
    func livenessData(_ stLivenessValue: String!, livenessImage: UIImage!, status: Bool, videoPath: String!, imagePath: String!) {
        var results:[String: Any] = [:]
        var pluginResult = CDVPluginResult(
            status: CDVCommandStatus_OK,
            messageAs: results
        )
        if status == true {
            print(stLivenessValue)
            results["status"] = true
            results["score"] = stLivenessValue.replacingOccurrences(of: " %", with: "")
            results["with_face"] = gl.withFace
            results["fm_score"] = 0.0
            if gl.face1 != nil {
                gl.face1Detect = EngineWrapper.detectSourceFaces(gl.face1)
                gl.face2Detect = EngineWrapper.detectTargetFaces(livenessImage, feature1: gl.face1Detect!.feature)
                results["fm_score"] = EngineWrapper.identify(gl.face1Detect!.feature, featurebuff2: gl.face2Detect!.feature) * 100
            }
            if gl.face2Detect != nil {
                results["detect"] = ACCURAService.getImageUri(img: ACCURAService.resizeImage(image: livenessImage, targetSize: gl.face2Detect!.bound), name: nil)
            } else {
                results["detect"] = ACCURAService.getImageUri(img: livenessImage, name: nil)
            }
            if imagePath != "" {
                results["image_uri"] = "file://\(imagePath!)"
            }
            if videoPath != "" {
                LivenessConfigs.isLivenessGetVideo = true
                LivenessConfigs.livenessVideo = videoPath!.replacingOccurrences(of: "file://", with: "")
                results["video_uri"] = videoPath!
            }
            
            pluginResult = CDVPluginResult(
                status: CDVCommandStatus_OK,
                messageAs: results
            )
        } else {
            pluginResult = CDVPluginResult(
                status: CDVCommandStatus_ERROR,
                messageAs: "Failed to get liveness. Please try again"
            )
        }
        self.commandDelegate!.send(
            pluginResult,
            callbackId: gl.ocrClId
        )
        
        closeMe()
    }
    
    func livenessViewDisappear() {
        
    }
    
    var livenessConfigs:[String: Any] = [:]
    var commandDelegate:CDVCommandDelegate? = nil
    var cordovaViewController:UIViewController? = nil
    var win: UIWindow? = nil
    
    func closeMe() {
        self.win!.rootViewController = cordovaViewController!
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        startLV()
    }
    func startLV() {
        let liveness = Liveness()
        
        if ScanConfigs.accuraConfigs.index(forKey: "with_face") != nil {
            gl.withFace = ScanConfigs.accuraConfigs["with_face"] as! Bool
            if ScanConfigs.accuraConfigs.index(forKey: "face_uri") != nil {
                if let face = ACCURAService.getImageFromUri(path: ScanConfigs.accuraConfigs["face_uri"] as! String) {
                    gl.face1 = face
                    
                }
            }
        }
        // To customize your screen theme and feed back messages
        liveness.setLivenessURL(LivenessConfigs.liveness_url)
        if livenessConfigs["liveness_url"] != nil {
            liveness.setLivenessURL(livenessConfigs["liveness_url"] as! String)
        }
        // To customize your screen theme and feed back messages
        liveness.setBackGroundColor(LivenessConfigs.livenessBackground)
        if livenessConfigs["livenessBackground"] != nil {
            liveness.setBackGroundColor(livenessConfigs["livenessBackground"] as! String)
        }
        liveness.setCloseIconColor(LivenessConfigs.livenessCloseIconColor)
        if livenessConfigs["livenessCloseIconColor"] != nil {
            liveness.setCloseIconColor(livenessConfigs["livenessCloseIconColor"] as! String)
        }
        liveness.setFeedbackBackGroundColor(LivenessConfigs.livenessfeedbackBackground)
        if livenessConfigs["livenessfeedbackBackground"] != nil {
            liveness.setFeedbackBackGroundColor(livenessConfigs["livenessfeedbackBackground"] as! String)
        }
        liveness.setFeedbackTextColor(LivenessConfigs.livenessfeedbackTextColor)
        if livenessConfigs["livenessfeedbackTextColor"] != nil {
            liveness.setFeedbackTextColor(livenessConfigs["livenessfeedbackTextColor"] as! String)
        }
        liveness.setFeedBackframeMessage(LivenessConfigs.feedBackframeMessage)
        if livenessConfigs["feedBackframeMessage"] != nil {
            liveness.setFeedBackframeMessage(livenessConfigs["feedBackframeMessage"] as! String)
        }
        liveness.setFeedBackAwayMessage(LivenessConfigs.feedBackAwayMessage)
        if livenessConfigs["feedBackAwayMessage"] != nil {
            liveness.setFeedBackAwayMessage(livenessConfigs["feedBackAwayMessage"] as! String)
        }
        liveness.setFeedBackOpenEyesMessage(LivenessConfigs.feedBackOpenEyesMessage)
        if livenessConfigs["feedBackOpenEyesMessage"] != nil {
            liveness.setFeedBackOpenEyesMessage(livenessConfigs["feedBackOpenEyesMessage"] as! String)
        }
        liveness.setFeedBackCloserMessage(LivenessConfigs.feedBackCloserMessage)
        if livenessConfigs["feedBackCloserMessage"] != nil {
            liveness.setFeedBackCloserMessage(livenessConfigs["feedBackCloserMessage"] as! String)
        }
        liveness.setFeedBackCenterMessage(LivenessConfigs.feedBackCenterMessage)
        if livenessConfigs["feedBackCenterMessage"] != nil {
            liveness.setFeedBackCenterMessage(livenessConfigs["feedBackCenterMessage"] as! String)
        }
        liveness.setFeedbackMultipleFaceMessage(LivenessConfigs.feedBackMultipleFaceMessage)
        if livenessConfigs["feedBackMultipleFaceMessage"] != nil {
            liveness.setFeedbackMultipleFaceMessage(livenessConfigs["feedBackMultipleFaceMessage"] as! String)
        }
        liveness.setFeedBackFaceSteadymessage(LivenessConfigs.feedBackHeadStraightMessage)
        if livenessConfigs["feedBackHeadStraightMessage"] != nil {
            liveness.setFeedBackFaceSteadymessage(livenessConfigs["feedBackHeadStraightMessage"] as! String)
        }
        liveness.setFeedBackLowLightMessage(LivenessConfigs.feedBackLowLightMessage)
        if livenessConfigs["feedBackLowLightMessage"] != nil {
            liveness.setFeedBackLowLightMessage(livenessConfigs["feedBackLowLightMessage"] as! String)
        }
        liveness.setFeedBackBlurFaceMessage(LivenessConfigs.feedBackBlurFaceMessage)
        if livenessConfigs["feedBackBlurFaceMessage"] != nil {
            liveness.setFeedBackBlurFaceMessage(livenessConfigs["feedBackBlurFaceMessage"] as! String)
        }
        liveness.setFeedBackGlareFaceMessage(LivenessConfigs.feedBackGlareFaceMessage)
        if livenessConfigs["feedBackGlareFaceMessage"] != nil {
            liveness.setFeedBackGlareFaceMessage(livenessConfigs["feedBackGlareFaceMessage"] as! String)
        }
        liveness.setfeedBackVideoRecordingMessage(LivenessConfigs.feedBackVideoRecordingMessage)
        if livenessConfigs["feedBackVideoRecordingMessage"] != nil {
            liveness.setfeedBackVideoRecordingMessage(livenessConfigs["feedBackVideoRecordingMessage"] as! String)
        }
        liveness.setRecordingMessage(LivenessConfigs.recordingMessage)
        if livenessConfigs["recordingMessage"] != nil {
            liveness.setRecordingMessage(livenessConfigs["recordingMessage"] as! String)
        }
        liveness.setFeedbackTextSize(LivenessConfigs.feedbackTextSize)
        if livenessConfigs["feedbackTextSize"] != nil {
            liveness.setFeedbackTextSize(livenessConfigs["feedbackTextSize"] as! Float)
        }
        
        liveness.setBlurPercentage(LivenessConfigs.setBlurPercentage) // set blure percentage -1 to remove this filter
        
        if livenessConfigs["setBlurPercentage"] != nil {
            liveness.setBlurPercentage(livenessConfigs["setBlurPercentage"] as! Int32)
        }
        
        var glarePerc0 = LivenessConfigs.setGlarePercentage_0
        if livenessConfigs["setGlarePercentage_0"] != nil {
            glarePerc0 = livenessConfigs["setGlarePercentage_0"] as! Int32
        }
        var glarePerc1 = Int32(LivenessConfigs.setGlarePercentage_1)
        if livenessConfigs["setGlarePercentage_1"] != nil {
            glarePerc1 = livenessConfigs["setGlarePercentage_1"] as! Int32
        }
        liveness.setGlarePercentage(glarePerc0, glarePerc1) //set glaremin -1 and glaremax -1 to remove this filter
        
        liveness.setVideoLengthInSecond(LivenessConfigs.videoLengthInSecond)
        if livenessConfigs["videoLengthInSecond"] != nil {
            liveness.setVideoLengthInSecond(livenessConfigs["videoLengthInSecond"] as! Int32)
        }
        
        liveness.setfeedBackFMFailMessage(LivenessConfigs.feedbackFMFailed)
        if livenessConfigs["feedbackFMFailed"] != nil {
            liveness.setfeedBackFMFailMessage(livenessConfigs["feedbackFMFailed"] as! String)
        }
        liveness.saveImageinDocumentDirectory(LivenessConfigs.isSaveImage)
        if livenessConfigs["isSaveImage"] != nil {
            liveness.saveImageinDocumentDirectory(livenessConfigs["isSaveImage"] as! Bool)
        }
        
        var isRecVid = LivenessConfigs.isRecordVideo
        if livenessConfigs["isRecordVideo"] != nil {
            isRecVid = livenessConfigs["isRecordVideo"] as! Bool
        }
        if isRecVid && LivenessConfigs.isLivenessGetVideo {
            if(FileManager.default.fileExists(atPath: LivenessConfigs.livenessVideo)) {
                isRecVid = false
            }
        }
        liveness.saveVideoinDocumentDirectory(isRecVid)
        
        liveness.enableFaceDetect(LivenessConfigs.enableFaceDetect)
        if livenessConfigs["enableFaceDetect"] != nil {
            liveness.enableFaceDetect(livenessConfigs["enableFaceDetect"] as! Bool)
        }
        liveness.enableFaceMatch(LivenessConfigs.enableFaceMatch)
        if livenessConfigs["enableFaceMatch"] != nil {
            liveness.enableFaceMatch(livenessConfigs["enableFaceMatch"] as! Bool)
        }
        liveness.fmScoreThreshold(Int32(LivenessConfigs.fmScoreThreshold))
        if livenessConfigs["fmScoreThreshold"] != nil {
            liveness.fmScoreThreshold(livenessConfigs["fmScoreThreshold"] as! Int32)
        }
        
        
        liveness.setRecordingTimerTextSize(LivenessConfigs.recordingTimerTextSize)
        if livenessConfigs["recordingTimerTextSize"] != nil {
            liveness.setRecordingTimerTextSize(CGFloat(livenessConfigs["recordingTimerTextSize"] as! Float))
        }
        liveness.setRecordingTimerTextColor(LivenessConfigs.livenessRecordingTimerColor)
        if livenessConfigs["livenessRecordingTimerColor"] != nil {
            liveness.setRecordingTimerTextColor(livenessConfigs["livenessRecordingTimerColor"] as! String)
        }
        liveness.setRecordingMessageTextSize(LivenessConfigs.recordingMessageTextSize)
        if livenessConfigs["recordingMessageTextSize"] != nil {
            liveness.setRecordingMessageTextSize(livenessConfigs["recordingMessageTextSize"] as! CGFloat)
        }
        liveness.setRecordingMessageTextColor(LivenessConfigs.livenessRecordingTextColor)
        if livenessConfigs["livenessRecordingTextColor"] != nil {
            liveness.setRecordingMessageTextColor(livenessConfigs["livenessRecordingTextColor"] as! String)
        }
        liveness.evaluateServerTrustWIthSSLPinning(false)
        
        liveness.setLiveness(self)
        
    }
}
