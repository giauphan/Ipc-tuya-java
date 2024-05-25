package com.giauphan.camerasmart;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.giauphan.camerasmart.adapter.PlaybackDayAdapter;
import com.giauphan.camerasmart.utils.VideoClip;
import com.thingclips.smart.android.camera.sdk.ThingIPCSdk;
import com.thingclips.smart.android.camera.sdk.api.IThingIPCCore;
import com.thingclips.smart.camera.camerasdk.thingplayer.callback.OperationDelegateCallBack;
import com.thingclips.smart.camera.ipccamerasdk.p2p.ICameraP2P;
import com.thingclips.smart.camera.middleware.p2p.IThingSmartCameraP2P;
import com.thingclips.smart.camera.middleware.widget.AbsVideoViewCallback;
import com.thingclips.smart.camera.middleware.widget.ThingCameraView;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.sdk.bean.DeviceBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlaybackP2pActivity extends AppCompatActivity {

    private static final String TAG = "PlaybackP2pLog";
    private IThingSmartCameraP2P  mCameraP2P;
    private ThingCameraView mVideoView;
    private RecyclerView listView;
    private TextView recordTxt;
    private boolean isPlayback = false;

    private boolean isPlay = false;

    private boolean isRecording = false;

    private  String devID;

    private  Long homeID;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playback_p2p);

        Button steam_video = findViewById(R.id.steam_video);
        recordTxt = findViewById(R.id.record_txt);
        steam_video.setVisibility(View.GONE);

        devID = getIntent().getStringExtra("devID");
        homeID = getIntent().getLongExtra("homeID",0);

        recordTxt.setOnClickListener(view -> startRecordLocalMp4(devID));
        try{

        HomeBean homeBean = SessionManager.getSession(PlaybackP2pActivity.this,"home", HomeBean.class);
        assert homeBean != null;

        ThingHomeSdk.newHomeInstance(homeID).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                List<DeviceBean> deviceList = homeBean.getDeviceList();
                if (!deviceList.isEmpty()){
                    initCameraP2P(devID);
                }
            }
            @Override
            public void onError(String errorCode, String errorMsg) {
                Log.d(TAG," "+errorCode +" "+errorMsg);
            }
        });
        } catch (Exception e) {
            Log.e(TAG, "Error parsing video clip data", e);
            Toast.makeText(PlaybackP2pActivity.this, "Error parsing video clip data", Toast.LENGTH_SHORT).show();
        }
    }

    private void initCameraP2P(String devID) {

        IThingIPCCore cameraInstance = ThingIPCSdk.getCameraInstance();
        if (cameraInstance != null) {
            mCameraP2P = cameraInstance.createCameraP2P(devID);
        }
        mVideoView = findViewById(R.id.camera_video_view);

        mVideoView.setViewCallback(new AbsVideoViewCallback() {
            @Override
            public void onCreated(Object o) {
                super.onCreated(o);
                if (mCameraP2P != null) {
                    mCameraP2P.generateCameraView(mVideoView.createdView());
                }
            }
        });
        mVideoView.createVideoView(devID);


        if (mCameraP2P!= null) {
            mCameraP2P.connect(devID, new OperationDelegateCallBack() {
                @Override
                public void onSuccess(int sessionId, int requestId, String data) {
                        Toast.makeText(PlaybackP2pActivity.this, "A P2P connection is created.", Toast.LENGTH_SHORT).show();
                        fetchVideoClips();
                        preview();
                }

                @Override
                public void onFailure(int sessionId, int requestId, int errCode) {
                    Log.e(TAG, "Failed to create a P2P connection."+errCode);
                    Toast.makeText(PlaybackP2pActivity.this, "Failed to create a P2P connection.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "Failed to establish Camera P2P connection:"+cameraInstance);
            Toast.makeText(PlaybackP2pActivity.this, "Failed to connect to the camera device", Toast.LENGTH_SHORT).show();
        }
    }


    private void fetchVideoClips() {
        if (mCameraP2P!= null) {
            Date currentDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
//            int month = calendar.get(Calendar);

            mCameraP2P.queryRecordTimeSliceByDay(2024, 5,13, new OperationDelegateCallBack() {
                @Override
                public void onSuccess(int sessionId, int requestId, String data) {
                    Log.d(TAG,"video: "+data);
                    runOnUiThread(() -> {
                        listView = findViewById(R.id.recyclerView);
                        List<VideoClip> products = parseProducts(data);
                        listView.setLayoutManager(new LinearLayoutManager(PlaybackP2pActivity.this));
                        PlaybackDayAdapter adapter = new PlaybackDayAdapter(PlaybackP2pActivity.this,products,isPlayback,mCameraP2P);
                        Log.d(TAG,"video"+products.size()+adapter.getItemCount());
                        listView.setAdapter(adapter);
                    });
                }
                @Override
                public void onFailure(int sessionId, int requestId, int errCode) {
                    Log.e(TAG, "Failed to fetch video clips");
                    Toast.makeText(PlaybackP2pActivity.this, "Failed to fetch video clips", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public List<VideoClip> parseProducts(String jsonData) {
        List<VideoClip> productList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemJson  = itemsArray.getJSONObject(i);
                int startTime = itemJson.getInt("startTime");
                int endTime = itemJson.getInt("endTime");
                int type = itemJson.getInt("type");
                productList.add(new VideoClip(startTime, endTime, type));
            }
        } catch (JSONException e) {
            Log.d(TAG,"JSONException: "+e);
        }
        return productList;
    }

private  void stopPlayback(){
    mCameraP2P.stopPlayBack(new OperationDelegateCallBack() {
        @Override
        public void onSuccess(int sessionId, int requestId, String data) {
        }

        @Override
        public void onFailure(int sessionId, int requestId, int errCode) {
        }
    });
    isPlayback = false;

}
    private void preview() {
        int videoClarity = ICameraP2P.HD;
        mCameraP2P.startPreview(videoClarity, new OperationDelegateCallBack() {
            @Override
            public void onSuccess(int sessionId, int requestId, String data) {
                Log.d(TAG, "start preview onSuccess");
                isPlay = true;
            }

            @Override
            public void onFailure(int sessionId, int requestId, int errCode) {
                Log.d(TAG, "start preview onFailure, errCode: " + errCode);
                isPlay = false;
            }
        });
    }

    public void startRecordLocalMp4(String devId) {
        if (ContextCompat.checkSelfPermission(PlaybackP2pActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&  ContextCompat.checkSelfPermission(PlaybackP2pActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (mCameraP2P == null) {
                Log.e(TAG, "Camera P2P instance is null");
                return;
            }
            if (!isRecording) {
                String picPath = getExternalFilesDir(null).getPath() + "/Camera/";
                File file = new File(picPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String fileName = System.currentTimeMillis() + ".mp4";
                Log.i(TAG, "start :" + fileName);
                mCameraP2P.startRecordLocalMp4(picPath, fileName, PlaybackP2pActivity.this, new OperationDelegateCallBack() {
                    @Override
                    public void onSuccess(int sessionId, int requestId, String data) {
                        isRecording = true;
                        Log.i(TAG, "record :" + data);
                        Toast.makeText(PlaybackP2pActivity.this, "Record video",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int sessionId, int requestId, int errCode) {
                        Log.e(TAG," startRecordLocalMp4" +" "+errCode);
                    }
                });
                recordStatue(true);
            } else {
                mCameraP2P.stopRecordLocalMp4(new OperationDelegateCallBack() {
                    @Override
                    public void onSuccess(int sessionId, int requestId, String data) {
                        isRecording = false;
                        Log.i(TAG, "record RecordLocalMp4:" + data);
                        Toast.makeText(PlaybackP2pActivity.this, "Success record"+data,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int sessionId, int requestId, int errCode) {
                        isRecording = false;
                        Log.e(TAG," startRecordLocalMp4" +" "+errCode);
                    }
                });
                recordStatue(false);
            }
        } else {
            ActivityCompat.requestPermissions(PlaybackP2pActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    private void recordStatue(boolean isRecording) {
        recordTxt.setEnabled(true);
        recordTxt.setSelected(isRecording);
    }


}
