package accura.kyc.plugin;

import android.content.Intent;
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

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class FaceMatchActivity extends AppCompatActivity implements FaceHelper.FaceMatchCallBack, FaceCallback {
    FaceHelper faceHelper;
    Bitmap face1, detectFace, face2;
    Bundle bundle;
    boolean witFace = false;
    public int R(String name, String type){
        return getResources().getIdentifier(name, type, getPackageName());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R("activity_face_match", "layout"));
        bundle = getIntent().getExtras();
        faceHelper = new FaceHelper(this);
        if (bundle.containsKey("with_face")) {
            witFace = bundle.getBoolean("with_face", false);
            Log.e("withFace", witFace + "");
            if (witFace) {
                String uri = bundle.getString("face_uri");
                Bitmap face = BitmapFactory.decodeFile(uri.replace("file://",""));
                face1 = face;
                faceHelper.setInputImage(face);
            }
        }
        try {
            openFaceMatch();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        cameraScreenCustomization.feedBackAwayMessage =bundle.getString("feedBackAwayMessage", getResources().getString(R("feedBackAwayMessage", "string")));
        cameraScreenCustomization.feedBackOpenEyesMessage = bundle.getString("feedBackOpenEyesMessage", getResources().getString(R("feedBackOpenEyesMessage", "string")));
        cameraScreenCustomization.feedBackCloserMessage = bundle.getString("feedBackCloserMessage", getResources().getString(R("feedBackCloserMessage", "string")));
        cameraScreenCustomization.feedBackCenterMessage = bundle.getString("feedBackCenterMessage", getResources().getString(R("feedBackCenterMessage", "string")));
        cameraScreenCustomization.feedBackMultipleFaceMessage = bundle.getString("feedBackMultipleFaceMessage", getResources().getString(R("feedBackMultipleFaceMessage", "string")));
        cameraScreenCustomization.feedBackHeadStraightMessage =bundle.getString("feedBackHeadStraightMessage", getResources().getString(R("feedBackHeadStraightMessage", "string")));
        cameraScreenCustomization.feedBackBlurFaceMessage = bundle.getString("feedBackBlurFaceMessage", getResources().getString(R("feedBackBlurFaceMessage", "string")));
        cameraScreenCustomization.feedBackGlareFaceMessage = bundle.getString("feedBackGlareFaceMessage", getResources().getString(R("feedBackGlareFaceMessage", "string")));
        cameraScreenCustomization.setBlurPercentage(bundle.getInt("setBlurPercentage", getResources().getInteger(R("setBlurPercentage", "integer"))));
        cameraScreenCustomization.setGlarePercentage(bundle.getInt("setGlarePercentage_0", getResources().getInteger(R("setGlarePercentage_0", "integer"))), bundle.getInt("setGlarePercentage_1",getResources().getInteger(R("setGlarePercentage_1", "integer"))));
        Intent intent = SelfieCameraActivity.getFaceMatchCameraIntent(this, cameraScreenCustomization);
        startActivityForResult(intent, 202);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", requestCode + "");
        if (requestCode == 202) {
            AccuraVerificationResult result = data.getParcelableExtra("Accura.fm");
            if (result == null) {
                return;
            }
            Log.e("result", result.getStatus());
            if (result.getStatus().equals("1")) {
                handleVerificationSuccessResultFM(result);
            } else {
                Toast.makeText(getApplicationContext(), "Retry...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void handleVerificationSuccessResultFM(final AccuraVerificationResult result) {
        if (result != null) {
            if (face1 == null) {
                if (result.getFaceBiometrics() != null) {
                    face1 = result.getFaceBiometrics();
                    faceHelper.setInputImage(face1);
                    try {
                        openFaceMatch();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    }

    @Override
    public void onRightDetect(FaceDetectionResult faceDetectionResult) {
        Bitmap det = BitmapHelper.createFromARGB(faceDetectionResult.getNewImg(), faceDetectionResult.getNewWidth(), faceDetectionResult.getNewHeight());
        detectFace = faceDetectionResult.getFaceImage(det);
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
                results.put("status", true);
                results.put("score", v);
                results.put("with_face", witFace);
                if (!witFace) {
                    results.put("img_1", ACCURAService.getImageUri(face1, "img_1", fileDir));
                    results.put("img_2", ACCURAService.getImageUri(face2, "img_2", fileDir));
                } else {
                    results.put("img_1", ACCURAService.getImageUri(detectFace, "img_1", fileDir));
                }

            } catch (JSONException e) {
                Log.e("err",e.getLocalizedMessage());
                e.printStackTrace();
            }
//            Log.e("face", results.toString());
            ACCURAService.faceCL.success(results);
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