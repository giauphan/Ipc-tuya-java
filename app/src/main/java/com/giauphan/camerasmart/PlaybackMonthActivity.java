package com.giauphan.camerasmart;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.giauphan.camerasmart.adapter.CustomListAdapter;
import com.giauphan.camerasmart.utils.VideoClip;
import com.thingclips.smart.android.camera.sdk.ThingIPCSdk;
import com.thingclips.smart.android.camera.sdk.api.IThingIPCCore;
import com.thingclips.smart.camera.camerasdk.thingplayer.callback.OperationDelegateCallBack;
import com.thingclips.smart.camera.middleware.p2p.IThingSmartCameraP2P;
import com.thingclips.smart.camera.middleware.widget.AbsVideoViewCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaybackMonthActivity extends AppCompatActivity {

   private ListView VideoPlayback;

    private IThingSmartCameraP2P mCameraP2P;

   private  String TAG = "PlaybackMonthActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playback_month);
        initView();
        initCameraP2P("eb22c749f6df047db3riel");

        String jsonData = "[{\"view_count\":260, \"startTime\":\"245345456\",\"endTime\":\"234234234\",\"type\":0},{\"startTime\":\"235345\",\"endTime\":\"324364456\",\"type\":0}]";

        try {
            List<VideoClip> products = parseProducts(jsonData);
            CustomListAdapter adapter = new CustomListAdapter(this, products);
            VideoPlayback.setAdapter(adapter);

        }
        catch ( Exception e){
            Log.e(TAG,"Exception: "+e);
        }
    }

    private void initCameraP2P(String devID) {

        IThingIPCCore cameraInstance = ThingIPCSdk.getCameraInstance();
        if (cameraInstance != null) {
            mCameraP2P = cameraInstance.createCameraP2P(devID);
        }
        if (mCameraP2P!= null) {
            mCameraP2P.connect(devID, new OperationDelegateCallBack() {
                @Override
                public void onSuccess(int sessionId, int requestId, String data) {
                    Toast.makeText(PlaybackMonthActivity.this, "A P2P connection is created.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "list video playback."+data);
                }

                @Override
                public void onFailure(int sessionId, int requestId, int errCode) {
                    Log.e(TAG, "Failed to create a P2P connection."+errCode);
                    Toast.makeText(PlaybackMonthActivity.this, "Failed to create a P2P connection.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "Failed to establish Camera P2P connection:"+cameraInstance);
            Toast.makeText(PlaybackMonthActivity.this, "Failed to connect to the camera device", Toast.LENGTH_SHORT).show();
        }
    }

    public List<VideoClip> parseProducts(String jsonData) {
        List<VideoClip> productList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject productJson = jsonArray.getJSONObject(i);
                int startTime = productJson.getInt("startTime");
                int endTime = productJson.getInt("endTime");
                int Type = Integer.parseInt(productJson.getString("type"));
                productList.add(new VideoClip( startTime, endTime, Type));
            }
        } catch (JSONException e) {
            Log.e(TAG,"JSONException: "+e);
        }
        return productList;
    }



    private  void initView( ){
        VideoPlayback = findViewById(R.id.list_playback_month);

    }
}