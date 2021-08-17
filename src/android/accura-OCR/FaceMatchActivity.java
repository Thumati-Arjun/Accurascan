package accura.kyc.plugin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.accurascan.facedetection.LivenessCustomization;
import com.accurascan.facedetection.SelfieCameraActivity;
import com.accurascan.facedetection.model.AccuraVerificationResult;
import com.accurascan.facematch.util.BitmapHelper;
import com.inet.facelock.callback.FaceCallback;
import com.inet.facelock.callback.FaceDetectionResult;
import com.inet.facelock.callback.FaceHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;



public class FaceMatchActivity extends AppCompatActivity implements FaceHelper.FaceMatchCallBack, FaceCallback {
    FaceHelper faceHelper;
    Bitmap face1, detectFace1, detectFace2, face2;
    Bundle bundle;
    boolean witFace = false;
    JSONObject livenessResult;
    Boolean isLiveness = false;

    public int R(String name, String type) {
        return getResources().getIdentifier(name, type, getPackageName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bundle = getIntent().getExtras();
        if (bundle.getString("app_orientation", "portrait").contains("portrait")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R("activity_face_match", "layout"));
        faceHelper = new FaceHelper(this);
        isLiveness = !bundle.getString("type").equalsIgnoreCase("fm");
        if (bundle.containsKey("with_face")) {
            witFace = bundle.getBoolean("with_face", false);
            if (witFace) {
                String uri = bundle.getString("face_uri", "");
                if (uri.length() > 0) {
                    Bitmap face = BitmapFactory.decodeFile(uri.replace("file://", ""));
                    face1 = face;
                    faceHelper.setInputImage(face);
                }

            } else {
                if (!isLiveness) {
                    if (!bundle.containsKey("face1")) {
                        ACCURAService.faceCL.error("Missing face1 configuration");
                        this.finish();
                        return;
                    }
                    if (bundle.containsKey("face2")) {
                        boolean isFace2 = bundle.getBoolean("face2", false);
                        if (isFace2) {
                            if (ACCURAService.face1 == null) {
                                ACCURAService.faceCL.error("Please first take Face1 photo");
                                this.finish();
                                return;
                            } else {
                                face1 = ACCURAService.face1;
                                faceHelper.setInputImage(face1);
                            }
                        }
                    } else {
                        ACCURAService.faceCL.error("Missing face2 configuration");
                        this.finish();
                        return;
                    }
                }
            }
        } else {
            ACCURAService.faceCL.error("Missing with_face configuration");
            this.finish();
            return;
        }
        try {
            if (!isLiveness) {
                openFaceMatch();
            } else if (bundle.getString("type").equalsIgnoreCase("lv")) {
                opneLiveness();
            } else {
                this.finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void opneLiveness() throws JSONException {
        Resources res = getResources();

        LivenessCustomization livenessCustomization = new LivenessCustomization();
        livenessCustomization.backGroundColor = res.getColor(R("livenessBackground", "color"));
        if (bundle.containsKey("livenessBackground")) {
            livenessCustomization.backGroundColor = Color.parseColor(bundle.getString("livenessBackground"));
        }
        livenessCustomization.closeIconColor = res.getColor(R("livenessCloseIcon", "color"));
        if (bundle.containsKey("livenessCloseIcon")) {
            livenessCustomization.closeIconColor = Color.parseColor(bundle.getString("livenessCloseIcon"));
        }
        livenessCustomization.feedbackBackGroundColor = res.getColor(R("livenessfeedbackBg", "color"));
        if (bundle.containsKey("livenessfeedbackBg")) {
            livenessCustomization.feedbackBackGroundColor = Color.parseColor(bundle.getString("livenessfeedbackBg"));
        }
        livenessCustomization.feedbackTextColor = res.getColor(R("livenessfeedbackText", "color"));
        if (bundle.containsKey("livenessfeedbackText")) {
            livenessCustomization.feedbackTextColor = Color.parseColor(bundle.getString("livenessfeedbackText"));
        }
        livenessCustomization.feedbackTextSize = res.getInteger(R("feedbackTextSize", "integer"));
        if (bundle.containsKey("livenessfeedbackText")) {
            livenessCustomization.feedbackTextSize = bundle.getInt("feedbackTextSize");
        }
        livenessCustomization.feedBackframeMessage = res.getString(R("feedBackframeMessage", "string"));
        if (bundle.containsKey("feedBackframeMessage")) {
            livenessCustomization.feedBackframeMessage = bundle.getString("feedBackframeMessage");
        }
        livenessCustomization.feedBackAwayMessage = res.getString(R("feedBackAwayMessage", "string"));
        if (bundle.containsKey("feedBackAwayMessage")) {
            livenessCustomization.feedBackAwayMessage = bundle.getString("feedBackAwayMessage");
        }
        livenessCustomization.feedBackOpenEyesMessage = res.getString(R("feedBackOpenEyesMessage", "string"));
        if (bundle.containsKey("feedBackOpenEyesMessage")) {
            livenessCustomization.feedBackOpenEyesMessage = bundle.getString("feedBackOpenEyesMessage");
        }
        livenessCustomization.feedBackCloserMessage = res.getString(R("feedBackCloserMessage", "string"));
        if (bundle.containsKey("feedBackCloserMessage")) {
            livenessCustomization.feedBackCloserMessage = bundle.getString("feedBackCloserMessage");
        }
        livenessCustomization.feedBackCenterMessage = res.getString(R("feedBackCenterMessage", "string"));
        if (bundle.containsKey("feedBackCenterMessage")) {
            livenessCustomization.feedBackCenterMessage = bundle.getString("feedBackCenterMessage");
        }
        livenessCustomization.feedBackMultipleFaceMessage = res.getString(R("feedBackMultipleFaceMessage", "string"));
        if (bundle.containsKey("feedBackMultipleFaceMessage")) {
            livenessCustomization.feedBackMultipleFaceMessage = bundle.getString("feedBackMultipleFaceMessage");
        }
        livenessCustomization.feedBackHeadStraightMessage = res.getString(R("feedBackHeadStraightMessage", "string"));
        if (bundle.containsKey("feedBackHeadStraightMessage")) {
            livenessCustomization.feedBackHeadStraightMessage = bundle.getString("feedBackHeadStraightMessage");
        }
        livenessCustomization.feedBackBlurFaceMessage = res.getString(R("feedBackBlurFaceMessage", "string"));
        if (bundle.containsKey("feedBackBlurFaceMessage")) {
            livenessCustomization.feedBackBlurFaceMessage = bundle.getString("feedBackBlurFaceMessage");
        }
        livenessCustomization.feedBackGlareFaceMessage = res.getString(R("feedBackGlareFaceMessage", "string"));
        if (bundle.containsKey("feedBackGlareFaceMessage")) {
            livenessCustomization.feedBackGlareFaceMessage = bundle.getString("feedBackGlareFaceMessage");
        }
        livenessCustomization.feedBackVideoRecordingMessage = res.getString(R("feedBackVideoRecordingMessage", "string"));
        if (bundle.containsKey("feedBackVideoRecordingMessage")) {
            livenessCustomization.feedBackVideoRecordingMessage = bundle.getString("feedBackVideoRecordingMessage");
        }
        livenessCustomization.setBlurPercentage(res.getInteger(R("setBlurPercentage", "integer")));
        if (bundle.containsKey("setBlurPercentage")) {
            livenessCustomization.setBlurPercentage(bundle.getInt("setBlurPercentage"));
        }
        int minGlare = res.getInteger(R("setGlarePercentage_0", "integer"));
        int maxGlare = res.getInteger(R("setGlarePercentage_1", "integer"));
        if (bundle.containsKey("setGlarePercentage_0")) {
            minGlare = bundle.getInt("setGlarePercentage_0");
        }
        if (bundle.containsKey("setGlarePercentage_1")) {
            minGlare = bundle.getInt("setGlarePercentage_1");
        }
        livenessCustomization.setGlarePercentage(minGlare, maxGlare);
        livenessCustomization.isSaveImage = res.getBoolean(R("isSaveImage", "bool"));
        if (bundle.containsKey("isSaveImage")) {
            livenessCustomization.isSaveImage = bundle.getBoolean("isSaveImage");
        }
        livenessCustomization.isRecordVideo = res.getBoolean(R("isRecordVideo", "bool"));
        if (bundle.containsKey("isRecordVideo")) {
            livenessCustomization.isRecordVideo = bundle.getBoolean("isRecordVideo");
        }
        if (livenessCustomization.isRecordVideo && ACCURAService.isLivenessGetVideo) {
            File vid = new File(ACCURAService.livenessVideo);
            if (vid.exists()) {
                livenessCustomization.isRecordVideo = false;
            } else {
                livenessCustomization.isRecordVideo = res.getBoolean(R("isRecordVideo", "bool"));
                if (bundle.containsKey("isRecordVideo")) {
                    livenessCustomization.isRecordVideo = bundle.getBoolean("isRecordVideo");
                }
            }
        }
        // video length in seconds
        livenessCustomization.videoLengthInSecond = res.getInteger(R("videoLengthInSecond", "integer"));
        if (bundle.containsKey("videoLengthInSecond")) {
            livenessCustomization.videoLengthInSecond = bundle.getInt("videoLengthInSecond");
        }
        livenessCustomization.recordingTimerTextColor = res.getColor(R("livenessRecordingText", "color"));
        if (bundle.containsKey("livenessRecordingTextColor")) {
            livenessCustomization.recordingTimerTextColor = Color.parseColor(bundle.getString("livenessRecordingTextColor"));
        }
        livenessCustomization.recordingTimerTextSize = res.getInteger(R("recordingTimerTextSize", "integer"));
        if (bundle.containsKey("recordingTimerTextSize")) {
            livenessCustomization.recordingTimerTextSize = bundle.getInt("recordingTimerTextSize");
        }
        livenessCustomization.recordingMessage = res.getString(R("recordingMessage", "string"));
        if (bundle.containsKey("recordingMessage")) {
            livenessCustomization.recordingMessage = bundle.getString("recordingMessage");
        }
        livenessCustomization.recordingMessageTextColor = res.getColor(R("livenessRecordingText", "color"));
        if (bundle.containsKey("livenessRecordingTextColor")) {
            livenessCustomization.recordingMessageTextColor = Color.parseColor(bundle.getString("livenessRecordingTextColor"));
        }
        livenessCustomization.recordingMessageTextSize = res.getInteger(R("recordingMessageTextSize", "integer"));
        if (bundle.containsKey("recordingMessageTextSize")) {
            livenessCustomization.recordingMessageTextSize = bundle.getInt("recordingMessageTextSize");
        }
        livenessCustomization.enableFaceDetect = res.getBoolean(R("enableFaceDetect", "bool"));
        if (bundle.containsKey("enableFaceDetect")) {
            livenessCustomization.enableFaceDetect = bundle.getBoolean("enableFaceDetect");
        }
        livenessCustomization.enableFaceMatch = res.getBoolean(R("enableFaceMatch", "bool"));
        if (bundle.containsKey("enableFaceMatch")) {
            livenessCustomization.enableFaceMatch = bundle.getBoolean("enableFaceMatch");
        }
        livenessCustomization.fmScoreThreshold = res.getInteger(R("fmScoreThreshold", "integer"));
        if (bundle.containsKey("fmScoreThreshold")) {
            livenessCustomization.fmScoreThreshold = bundle.getInt("fmScoreThreshold");
        }
        livenessCustomization.feedbackFMFailed = res.getString(R("feedbackFMFailed", "string"));
        if (bundle.containsKey("feedbackFMFailed")) {
            livenessCustomization.feedbackFMFailed = bundle.getString("feedbackFMFailed");
        }


        String liveUrl = res.getString(R("liveness_url", "string"));
        if (bundle.containsKey("liveness_url")) {
            liveUrl = bundle.getString("liveness_url");
        }
        Intent intent = SelfieCameraActivity.getLivenessCameraIntent(this, livenessCustomization, liveUrl);
        startActivityForResult(intent, 201);
    }

    public void openFaceMatch() throws JSONException {
        LivenessCustomization cameraScreenCustomization = new LivenessCustomization();
        cameraScreenCustomization.backGroundColor = getResources().getColor(R("camera_Background", "color"));
        if (bundle.containsKey("backGroundColor")) {
            cameraScreenCustomization.backGroundColor = Color.parseColor(bundle.getString("backGroundColor"));
        }
        cameraScreenCustomization.closeIconColor = getResources().getColor(R("camera_CloseIcon", "color"));
        if (bundle.containsKey("closeIconColor")) {
            cameraScreenCustomization.closeIconColor = Color.parseColor(bundle.getString("closeIconColor"));
        }
        cameraScreenCustomization.feedbackBackGroundColor = getResources().getColor(R("camera_feedbackBg", "color"));
        if (bundle.containsKey("feedbackBackGroundColor")) {
            cameraScreenCustomization.feedbackBackGroundColor = Color.parseColor(bundle.getString("feedbackBackGroundColor"));
        }
        cameraScreenCustomization.feedbackTextColor = getResources().getColor(R("camera_feedbackText", "color"));
        if (bundle.containsKey("feedbackTextColor")) {
            cameraScreenCustomization.feedbackTextColor = Color.parseColor(bundle.getString("feedbackTextColor"));
        }
        cameraScreenCustomization.feedbackTextSize = bundle.getInt("feedbackTextSize", getResources().getInteger(R("feedbackTextSize", "integer")));
        cameraScreenCustomization.feedBackframeMessage = bundle.getString("feedBackframeMessage", getResources().getString(R("feedBackframeMessage", "string")));
        cameraScreenCustomization.feedBackAwayMessage = bundle.getString("feedBackAwayMessage", getResources().getString(R("feedBackAwayMessage", "string")));
        cameraScreenCustomization.feedBackOpenEyesMessage = bundle.getString("feedBackOpenEyesMessage", getResources().getString(R("feedBackOpenEyesMessage", "string")));
        cameraScreenCustomization.feedBackCloserMessage = bundle.getString("feedBackCloserMessage", getResources().getString(R("feedBackCloserMessage", "string")));
        cameraScreenCustomization.feedBackCenterMessage = bundle.getString("feedBackCenterMessage", getResources().getString(R("feedBackCenterMessage", "string")));
        cameraScreenCustomization.feedBackMultipleFaceMessage = bundle.getString("feedBackMultipleFaceMessage", getResources().getString(R("feedBackMultipleFaceMessage", "string")));
        cameraScreenCustomization.feedBackHeadStraightMessage = bundle.getString("feedBackHeadStraightMessage", getResources().getString(R("feedBackHeadStraightMessage", "string")));
        cameraScreenCustomization.feedBackBlurFaceMessage = bundle.getString("feedBackBlurFaceMessage", getResources().getString(R("feedBackBlurFaceMessage", "string")));
        cameraScreenCustomization.feedBackGlareFaceMessage = bundle.getString("feedBackGlareFaceMessage", getResources().getString(R("feedBackGlareFaceMessage", "string")));
        cameraScreenCustomization.setBlurPercentage(bundle.getInt("setBlurPercentage", getResources().getInteger(R("setBlurPercentage", "integer"))));
        cameraScreenCustomization.setGlarePercentage(bundle.getInt("setGlarePercentage_0", getResources().getInteger(R("setGlarePercentage_0", "integer"))), bundle.getInt("setGlarePercentage_1", getResources().getInteger(R("setGlarePercentage_1", "integer"))));
        Intent intent = SelfieCameraActivity.getFaceMatchCameraIntent(this, cameraScreenCustomization);
        startActivityForResult(intent, 202);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 202) {
            if (data == null) {
                ACCURAService.faceCL.error("No data");
                this.finish();
                return;
            }
            AccuraVerificationResult result = data.getParcelableExtra("Accura.fm");
            if (result == null) {
                return;
            }
            if (result.getStatus().equals("1")) {
                handleVerificationSuccessResultFM(result);
            } else {
                Toast.makeText(getApplicationContext(), "Retry...", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 201) {
            if (data == null) {
                ACCURAService.faceCL.error("No Data");
                this.finish();
                return;
            }
            JSONObject results = new JSONObject();
            try {
                results.put("status", false);
                results.put("type", "liveness");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            AccuraVerificationResult result = data.getParcelableExtra("Accura.liveness");
            if (result != null) {
                if (result.getStatus().equals("1")) {
                    livenessResult = handleVerificationSuccessResult(result, results);
                    if (witFace) {
                        if (result.getFaceBiometrics() != null) {
                            Bitmap nBmp = result.getFaceBiometrics();
                            face2 = nBmp;
                            faceHelper.setMatchImage(nBmp);
                        }
                    } else {
                        ACCURAService.faceCL.success(livenessResult);
                        this.finish();
                    }

                } else {
                    ACCURAService.faceCL.error(result.getErrorMessage());
                    if (result.getVideoPath() != null) {
                        ACCURAService.isLivenessGetVideo = true;
                        ACCURAService.livenessVideo = result.getVideoPath().getPath();
                        Toast.makeText(getApplicationContext(), "Video Path : " + result.getVideoPath() + "\n" + result.getErrorMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                    this.finish();
                }
            }
        }
    }

    public JSONObject handleVerificationSuccessResult(final AccuraVerificationResult result, JSONObject caResults) {
        if (result != null) {
            if (result.getLivenessResult() != null) {
                if (result.getLivenessResult().getLivenessStatus()) {
                    try {
                        caResults.put("status", true);
                        caResults.put("with_face", witFace);
                        caResults.put("score", result.getLivenessResult().getLivenessScore() * 100);
                        if (result.getFaceBiometrics() != null) {
                            caResults.put("detect", ACCURAService.getImageUri(result.getFaceBiometrics(), "live_detect", getFilesDir().getAbsolutePath()));
                        }
                        if (result.getImagePath() != null) {
                            caResults.put("image_uri", result.getImagePath());
                        }
                        if (result.getVideoPath() != null) {
                            caResults.put("video_uri", result.getVideoPath());
                        }
                    } catch (JSONException ignored) {
                    }
                }
            }
        }

        return caResults;

    }

    public void handleVerificationSuccessResultFM(final AccuraVerificationResult result) {
        if (result != null) {
            if (face1 == null) {
                if (result.getFaceBiometrics() != null) {
                    ACCURAService.face1 = face1 = result.getFaceBiometrics();
                    JSONObject results = new JSONObject();
                    try {
                        results.put("status", false);
                        results.put("with_face", witFace);
                        String fileDir = getFilesDir().getAbsolutePath();
                        if (detectFace1 == null) {
                            results.put("img_1", ACCURAService.getImageUri(ACCURAService.face1, "img_1", fileDir));
                        } else {
                            results.put("img_1", ACCURAService.getImageUri(detectFace1, "img_1", fileDir));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ACCURAService.faceCL.success(results);
                    this.finish();
                    return;
                }
                return;
            }
            if (result.getFaceBiometrics() != null) {
                Bitmap nBmp = result.getFaceBiometrics();
                face2 = nBmp;
                faceHelper.setMatchImage(nBmp);
            }
        }
    }

    @Override
    public void onInitEngine(int i) {
    }

    @Override
    public void onLeftDetect(FaceDetectionResult faceDetectionResult) {
        Bitmap det = BitmapHelper.createFromARGB(faceDetectionResult.getNewImg(), faceDetectionResult.getNewWidth(), faceDetectionResult.getNewHeight());
        detectFace1 = faceDetectionResult.getFaceImage(det);
    }

    @Override
    public void onRightDetect(FaceDetectionResult faceDetectionResult) {
        Bitmap det = BitmapHelper.createFromARGB(faceDetectionResult.getNewImg(), faceDetectionResult.getNewWidth(), faceDetectionResult.getNewHeight());
        detectFace2 = faceDetectionResult.getFaceImage(det);
    }

    @Override
    public void onExtractInit(int i) {

    }

    @Override
    public void onFaceMatch(float v) {

        if (face2 != null) {
            JSONObject results = new JSONObject();
            String fileDir = getFilesDir().getAbsolutePath();
            try {
                if (!isLiveness) {

                    results.put("status", true);
                    results.put("score", v);
                    results.put("with_face", witFace);
                    if (!witFace) {
                        results.put("img_1", ACCURAService.getImageUri(detectFace1, "img_1", fileDir));
                        results.put("img_2", ACCURAService.getImageUri(detectFace2, "img_2", fileDir));
                    } else {
                        results.put("detect", ACCURAService.getImageUri(detectFace2, "img_1", fileDir));
                    }
                    ACCURAService.faceCL.success(results);
                } else {
                    livenessResult.put("fm_score", v);
                    ACCURAService.faceCL.success(livenessResult);
                }
                ACCURAService.face1 = null;
                ACCURAService.face2 = null;
            } catch (JSONException e) {
                ACCURAService.faceCL.error("Error found in data. Please try again");
                e.printStackTrace();
                this.finish();
            }
            this.finish();
        }
    }

    @Override
    public void onSetInputImage(Bitmap bitmap) {

    }

    @Override
    public void onSetMatchImage(Bitmap bitmap) {

    }
}