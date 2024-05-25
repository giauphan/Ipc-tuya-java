package com.giauphan.camerasmart.device;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.Manifest;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.thingclips.smart.camera.camerasdk.thingplayer.callback.OperationDelegateCallBack;
import com.thingclips.smart.camera.middleware.p2p.IThingSmartCameraP2P;
import java.io.File;

public class AudioVideoActivity {
    private IThingSmartCameraP2P cameraP2P;
    private Context context;
    private boolean isRecording = false;

    private TextView  recordTxt;

    private  String TAG = "AudioVideoActivity";

    private static final int PERMISSION_REQUEST_CODE = 1001;


    public AudioVideoActivity(@NonNull Context context, @NonNull IThingSmartCameraP2P cameraP2P,boolean isRecording,TextView recordTxt) {
        this.context = context;
        this.isRecording = isRecording;
        this.cameraP2P = cameraP2P;
        this.recordTxt = recordTxt;
    }

    public void startRecordLocalMp4() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            String picPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Camera/";
            File directory = new File(picPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            Log.d(TAG," startRecordLocalMp4 start .." );
            cameraP2P.startRecordLocalMp4(picPath, context, new OperationDelegateCallBack() {
                @Override
                public void onSuccess(int sessionId, int requestId, String data) {
                    isRecording = true;
                }

                @Override
                public void onFailure(int sessionId, int requestId, int errCode) {
                    Log.e(TAG," startRecordLocalMp4" +" "+errCode);
                }
            });
            recordStatue(true);
        } else {
            ActivityCompat.requestPermissions((Activity) this.context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void recordStatue(boolean isRecording) {
        recordTxt.setEnabled(true);
        recordTxt.setSelected(isRecording);
    }
}
