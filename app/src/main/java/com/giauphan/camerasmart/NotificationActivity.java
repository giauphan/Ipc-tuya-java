package com.giauphan.camerasmart;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.giauphan.camerasmart.adapter.ListDeviceAdapter;
import com.thingclips.smart.android.camera.sdk.ThingIPCSdk;
import com.thingclips.smart.android.camera.sdk.api.IThingCameraMessage;
import com.thingclips.smart.android.camera.sdk.api.IThingIPCMsg;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.home.sdk.callback.IThingResultCallback;
import com.thingclips.smart.ipc.messagecenter.bean.CameraMessageBean;
import com.thingclips.smart.ipc.messagecenter.bean.CameraMessageClassifyBean;
import com.thingclips.smart.sdk.bean.DeviceBean;

import java.util.List;
import java.util.TimeZone;

public class NotificationActivity extends AppCompatActivity {

    private  String TAG = "NotificationActivityLog";

    private int offset = 0;
    private IThingCameraMessage mTyCameraMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

       long homeID = getIntent().getLongExtra("homeID",0);
        try {
        ThingHomeSdk.newHomeInstance(homeID).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                List<DeviceBean> deviceList = homeBean.getDeviceList();
                if (!deviceList.isEmpty()){
                    IThingIPCMsg message = ThingIPCSdk.getMessage();
                    if (message != null) {
                        mTyCameraMessage = message.createCameraMessage();
                    }


                    mTyCameraMessage.queryAlarmDetectionClassify(deviceList.get(0).devId, new IThingResultCallback<List<CameraMessageClassifyBean>>() {
                        @Override
                        public void onSuccess(List<CameraMessageClassifyBean> result) {

                            mTyCameraMessage.getAlarmDetectionMessageList(deviceList.get(0).devId, 1715565482, 1715565527, result.get(0).getMsgCode(), offset, 30, new IThingResultCallback<List<CameraMessageBean>>() {
                                @Override
                                public void onSuccess(List<CameraMessageBean> result) {
                                    offset += result.size();
                                    runOnUiThread(() -> {
                                        RecyclerView listView = findViewById(R.id.recyclerView);
                                        listView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
                                        ListDeviceAdapter adapter = new ListDeviceAdapter(NotificationActivity.this,deviceList,homeBean);

                                        listView.setAdapter(adapter);
                                    });
                                    Log.d(TAG,"result"+result.size()+"  "+result.get(0).getAttachPics());
                                }

                                @Override
                                public void onError(String errorCode, String errorMessage) {

                                }
                            });

                            }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            Log.d(TAG,"result"+errorMessage);
                        }
                    });


                }
            }
            @Override
            public void onError(String errorCode, String errorMsg) {
                Log.d(TAG," "+errorCode +" "+errorMsg);
            }
            });
        }  catch (Exception e){
            Log.e(TAG,"result"+e);
        }
    }
}