package accura.kyc.plugin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.accurascan.facedetection.utils.AccuraLivenessLog;
import com.accurascan.ocr.mrz.model.ContryModel;
import com.accurascan.ocr.mrz.util.AccuraLog;
import com.docrecog.scan.MRZDocumentType;
import com.docrecog.scan.RecogEngine;
import com.docrecog.scan.RecogType;

import java.util.List;


public class NavigateActivity extends AppCompatActivity {
    public int R(String name, String type){
        return getResources().getIdentifier(name, type, getPackageName());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R("activity_navigate", "layout"));
        Bundle bundle = getIntent().getExtras();
        List<ContryModel> modelList = null;
        try {
            RecogEngine recogEngine = new RecogEngine();
            AccuraLog.enableLogs(false); // make sure to disable logs in release mode
            AccuraLivenessLog.setDEBUG(false);
            recogEngine.setDialog(false); // setDialog(false) To set your custom dialog for license validation
            RecogEngine.SDKModel sdkModel = recogEngine.initEngine(this);
            if (sdkModel.i >= 0) {
                // if OCR enable then get card list
                Resources res = getResources();
                recogEngine.setBlurPercentage(this, bundle.getInt("rg_setBlurPercentage", res.getInteger(R("rg_setBlurPercentage", "integer"))));
                recogEngine.setFaceBlurPercentage(this, bundle.getInt("rg_setFaceBlurPercentage", res.getInteger(R("rg_setFaceBlurPercentage", "integer"))));
                recogEngine.setGlarePercentage(this, bundle.getInt("rg_setGlarePercentage_0", res.getInteger(R("rg_setGlarePercentage_0", "integer"))), bundle.getInt("rg_setGlarePercentage_1", res.getInteger(R("rg_setGlarePercentage_1", "integer"))));
                recogEngine.isCheckPhotoCopy(this, bundle.getBoolean("rg_isCheckPhotoCopy", res.getBoolean(R("rg_isCheckPhotoCopy", "bool"))));
                recogEngine.SetHologramDetection(this, bundle.getBoolean("rg_SetHologramDetection", res.getBoolean(R("rg_SetHologramDetection", "bool"))));
                recogEngine.setLowLightTolerance(this, bundle.getInt("rg_setLowLightTolerance", res.getInteger(R("rg_setLowLightTolerance", "integer"))));
                recogEngine.setMotionThreshold(this, bundle.getInt("rg_setMotionThreshold", res.getInteger(R("rg_setMotionThreshold", "integer"))));
                Intent ocr = new Intent(this, OcrActivity.class);
                ocr.putExtras(bundle);
                if (bundle.getString("type").equalsIgnoreCase("ocr")) {
                    int cardType = bundle.getInt("card_type", 0);
                    if (cardType == 1) {
                        RecogType.PDF417.attachTo(ocr);
                    } else if (cardType == 2) {
                        RecogType.DL_PLATE.attachTo(ocr);
                    } else {
                        RecogType.OCR.attachTo(ocr);
                    }
                } else if (bundle.getString("type").equalsIgnoreCase("mrz")) {
                    String mrzType = bundle.getString("sub-type");
                    switch (mrzType) {
                        case "passport_mrz":
                            RecogType.MRZ.attachTo(ocr);
                            MRZDocumentType.PASSPORT_MRZ.attachTo(ocr);
                            ocr.putExtra("card_name", getResources().getString(R("passport_mrz", "string")));
                            break;
                        case "id_mrz":
                            RecogType.MRZ.attachTo(ocr);
                            MRZDocumentType.ID_CARD_MRZ.attachTo(ocr);
                            ocr.putExtra("card_name", getResources().getString(R("id_mrz", "string")));
                            break;
                        case "visa_mrz":
                            RecogType.MRZ.attachTo(ocr);
                            MRZDocumentType.VISA_MRZ.attachTo(ocr);
                            ocr.putExtra("card_name", getResources().getString(R("visa_mrz", "string")));
                            break;
                        default:
                            RecogType.MRZ.attachTo(ocr);
                            MRZDocumentType.NONE.attachTo(ocr);
                            ocr.putExtra("card_name", getResources().getString(R("other_mrz", "string")));
                    }
                } else if (bundle.getString("type").equalsIgnoreCase("bankcard")) {
                    RecogType.BANKCARD.attachTo(ocr);
                    ocr.putExtras(getIntent().getExtras());
                    ocr.putExtra("card_name", getResources().getString(R("bank_card", "string")));
                } else if (bundle.getString("type").equalsIgnoreCase("barcode")) {
                    RecogType.BARCODE.attachTo(ocr);
                    ocr.putExtras(getIntent().getExtras());
                    ocr.putExtra("card_name", "Barcode");
                } else {

                }
                startActivity(ocr);
            } else {
                ACCURAService.ocrCL.error("No Licence Found");
                this.finish();
            }

        } catch (Exception e) {
            Log.e("ee", e.getLocalizedMessage());
            ACCURAService.ocrCL.error("No Action Found");
            this.finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (ACCURAService.ocrCLProcess) {
            ACCURAService.ocrCLProcess = false;
            this.finish();
        }
    }
}