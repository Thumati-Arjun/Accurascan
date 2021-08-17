struct LivenessConfigs {
    static let feedbackTextSize: Float = 18.0


    static let feedBackframeMessage: String = "Frame Your Face"


    static let feedBackAwayMessage: String = "Move Phone Away"


    static let feedBackOpenEyesMessage: String = "Keep Your Eyes Open"


    static let feedBackCloserMessage: String = "Move Phone Closer"


    static let feedBackCenterMessage: String = "Move Phone Center"


    static let feedBackMultipleFaceMessage: String = "Multiple Face Detected"


    static let feedBackHeadStraightMessage: String = "Keep Your Head Straight"


    static let feedBackBlurFaceMessage: String = "Blur Detected Over Face"


    static let feedBackGlareFaceMessage: String = "Glare Detected"


    static let feedBackVideoRecordingMessage: String = "Processing..."
    
    static let feedBackLowLightMessage: String = "Low light detected"


    static let setBlurPercentage: Int32 = 80


    static let setGlarePercentage_0: Int32 = -1


    static let setGlarePercentage_1: Int32 = -1


    static let isSaveImage: Bool = true

    /* // Video recording settings */
    static let isRecordVideo: Bool = true

    /* // video length in seconds */
    static let videoLengthInSecond: Int32 = 5


    static let recordingTimerTextSize: CGFloat = 45.0


    static let recordingMessage: String = "Scanning your face be steady"


    static let recordingMessageTextSize: CGFloat = 18


    static let enableFaceDetect: Bool = true

    /* // If video recording and face detect are on then it'll check face match is enable or disable otherwise it will be disable.
    // Enable face match so it will compare the selfie camera face with document face. */
    static let enableFaceMatch = false

    /* // it should be match at least livenessCustomization.fmScoreThreshold. */
    static let fmScoreThreshold: Int32 = 50


    static let feedbackFMFailed: String = "Face not matched"


    static let liveness_url: String = "https://api1.accurascan.com:9922/"
    
    static let livenessBackground: String = "#C4C4C5"
    static let livenessCloseIconColor: String = "#000000"
    static let livenessfeedbackBackground: String = "#C4C4C5"
    static let livenessfeedbackTextColor: String = "#000000"
    static let livenessRecordingTextColor: String = "#FFFFFF"
    static let livenessRecordingTimerColor: String = "#FF5555"
    static var isLivenessGetVideo = false
    static var livenessVideo = ""
}
