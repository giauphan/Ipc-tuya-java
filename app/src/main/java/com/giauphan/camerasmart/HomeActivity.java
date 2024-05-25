package com.giauphan.camerasmart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.giauphan.camerasmart.adapter.ListDeviceAdapter;
import com.giauphan.camerasmart.adapter.PlaybackDayAdapter;
import com.giauphan.camerasmart.utils.DeviceBeanList;
import com.giauphan.camerasmart.utils.VideoClip;
import com.thingclips.smart.android.user.api.ILogoutCallback;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingGetHomeListCallback;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.sdk.bean.DeviceBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView tv_userName,tv_Uid;

    private Button btnLogout ,btnDevice,btnNotification ;
    private  static final String TAG = "HomeActivityLog";

    String HomeName = "MyHome";

    String[] rooms = {"Bet"};

    ArrayList<String> roomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        initViews();
        fetchUserInfo();
        queryHomeList();

        btnLogout.setOnClickListener(view -> {
            ThingHomeSdk.getUserInstance().touristLogOut(LogoutCallback);
            SessionManager.removeSession(HomeActivity.this,"user");
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ThingCameraActivatorActivity.class);
                startActivity(intent);
            }
        });
        Button steam_video = findViewById(R.id.steam_video);
        steam_video.setVisibility(View.GONE);

        HomeBean homeBean = SessionManager.getSession(HomeActivity.this,"home",HomeBean.class);
        if (homeBean != null) {

            btnNotification.setOnClickListener(view ->{
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                intent.putExtra("homeID", homeBean.getHomeId());
                startActivity(intent);
            });
            ThingHomeSdk.newHomeInstance(homeBean.getHomeId()).getHomeDetail(new IThingHomeResultCallback() {
                @Override
                public void onSuccess(HomeBean homeBean) {
                    List<DeviceBean> deviceList = homeBean.getDeviceList();
                    if (!deviceList.isEmpty()) {

                        runOnUiThread(() -> {
                            RecyclerView    listView = findViewById(R.id.recyclerView);
                            listView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                            ListDeviceAdapter adapter = new ListDeviceAdapter(HomeActivity.this,deviceList,homeBean);

                            listView.setAdapter(adapter);
                        });

                    }
                }

                @Override
                public void onError(String errorCode, String errorMsg) {
                Log.d(TAG," "+errorCode +" "+errorMsg);
                }
            });
        }

    }


    ILogoutCallback LogoutCallback = new ILogoutCallback() {
        @Override
        public void onSuccess() {
            Toast.makeText(HomeActivity.this,"Logout SuccessFully",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        }

        @Override
        public void onError(String code, String error) {
            Toast.makeText(HomeActivity.this,"Failed Logout: "+error,Toast.LENGTH_LONG).show();
        }
    };

    private  void queryHomeList(){
    ThingHomeSdk.getHomeManagerInstance().queryHomeList(new IThingGetHomeListCallback() {
        @Override
        public void onSuccess(List<HomeBean> homeBeans) {
            if (!homeBeans.isEmpty()) {
                HomeBean bean = homeBeans.get(0);
                Log.d("home_beans",bean.toString());
                SessionManager.saveSession(HomeActivity.this,"home", bean);
            } else {
                roomList = new ArrayList<>();

                roomList.addAll(Arrays.asList(rooms));

                createHome(HomeName,roomList);
            }
        }
        @Override
        public void onError(String errorCode, String error) {
            Toast.makeText(HomeActivity.this,"Failed Logout: "+error,Toast.LENGTH_LONG).show();
        }
    });

}
    private  void createHome(String homeName, List<String> roomList){
        ThingHomeSdk.getHomeManagerInstance().createHome(homeName, 0, 0, "", roomList, new IThingHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean bean) {
                Toast.makeText(HomeActivity.this," SuccessFully  Home created",Toast.LENGTH_LONG).show();
                SessionManager.saveSession(HomeActivity.this,"home", bean);
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Log.e("Register error","Error: "+errorMsg +"code:"+ errorCode);
                Toast.makeText(HomeActivity.this,"Failed created home"+errorMsg,Toast.LENGTH_LONG).show();
            }
        });
    }
    private void fetchUserInfo() {

        User user = SessionManager.getSession(HomeActivity.this,"user",User.class);
        if (user != null) {
            String userName = user.getUsername();

            tv_Uid.setText(user.getUid());
            Log.d("uid_user",user.getUid());
            tv_userName.setText(userName);
            Toast.makeText(this, "Retrieved User: " + user.getUid(), Toast.LENGTH_SHORT).show();
        }
    }


    private  void initViews(){
        btnLogout = findViewById(R.id.btn_Logout);
        tv_userName = findViewById(R.id.tv_userName);
        btnDevice = findViewById(R.id.btn_device);
        tv_Uid = findViewById(R.id.tv_uid);
        btnNotification= findViewById(R.id.btn_notification);


    }

}