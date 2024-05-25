package com.giauphan.camerasmart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.builder.ThingCameraActivatorBuilder;
import com.thingclips.smart.sdk.api.IThingActivatorGetToken;
import com.thingclips.smart.sdk.api.IThingCameraDevActivator;
import com.thingclips.smart.sdk.api.IThingSmartCameraActivatorListener;
import com.thingclips.smart.sdk.bean.DeviceBean;

import java.util.Hashtable;

public class ThingCameraActivatorActivity extends AppCompatActivity {

    private  String tokenUser;String ssid = "TIN";String password = "0965643046";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thing_camera_activator);
        HomeBean home = SessionManager.getSession(ThingCameraActivatorActivity.this,"home",HomeBean.class);
        if (home != null) {
            GetToken(home.getHomeId());
        }
    }

    private  void GetToken (Long homeId){
        ThingHomeSdk.getActivatorInstance().getActivatorToken(homeId,
                new IThingActivatorGetToken() {
                    @Override
                    public void onSuccess(String token) {
                        tokenUser = token;
                        Toast.makeText(ThingCameraActivatorActivity.this," SuccessFully  gettoken "+ token,Toast.LENGTH_LONG).show();
                        try {
                            ThingCameraActivatorBuilder builder = new ThingCameraActivatorBuilder()
                                    .setContext(ThingCameraActivatorActivity.this)
                                    .setSsid(ssid)
                                    .setPassword(password)
                                    .setToken(tokenUser)
                                    .setTimeOut(100)
                                    .setListener(new IThingSmartCameraActivatorListener() {
                                        @Override
                                        public void onQRCodeSuccess(String qrcodeUrl) {
                                            try {
                                                Bitmap qrCodeBitmap = createQRCode(qrcodeUrl, 200);
                                                ImageView qrCodeImageView = findViewById(R.id.qrcodeImageView);
                                                qrCodeImageView.setImageBitmap(qrCodeBitmap);
                                            } catch (WriterException e) {
                                                Log.e("QRCodeGenerationError", "Error generating QR code: " + e.getMessage());
                                                Toast.makeText(ThingCameraActivatorActivity.this, "Error generating QR code", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onError(String errorCode, String errorMsg) {
                                            Log.e("Register error", "Error: " + errorMsg + " code:" + errorCode);
                                            Toast.makeText(ThingCameraActivatorActivity.this, "Failed pairing device: " + errorMsg+". Please check your Wi-Fi password.", Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onActiveSuccess(DeviceBean devResp) {
                                            SessionManager.saveSession(ThingCameraActivatorActivity.this,"device",devResp);
                                            Toast.makeText(ThingCameraActivatorActivity.this, "Successfully paired device", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(ThingCameraActivatorActivity.this, HomeActivity.class);
                                            startActivity(intent);

                                        }
                                    });

                            // Now that the builder is initialized, you can proceed with creating and starting the activator
                            IThingCameraDevActivator mThingActivator = ThingHomeSdk.getActivatorInstance().newCameraDevActivator(builder);
                            mThingActivator.createQRCode();
                            mThingActivator.start();
                        }catch (Exception e){
                            Log.e("Qrcode", "Error: " + e);
                            Toast.makeText(ThingCameraActivatorActivity.this,"erorr:"+e,Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(String error, String errorCode) {
                        Log.e("Register error", "Error: " + error + " code:" + errorCode);
                        Toast.makeText(ThingCameraActivatorActivity.this, "Failed to create home: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public static Bitmap createQRCode(String url, int widthAndHeight)
            throws WriterException {
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN,0);
        BitMatrix matrix = new MultiFormatWriter().encode(url,
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xFF000000;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


}