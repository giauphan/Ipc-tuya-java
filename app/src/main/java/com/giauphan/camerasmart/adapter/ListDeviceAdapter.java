package com.giauphan.camerasmart.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.giauphan.camerasmart.HomeActivity;
import com.giauphan.camerasmart.MainActivity;
import com.giauphan.camerasmart.PlaybackP2pActivity;
import com.giauphan.camerasmart.R;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.sdk.bean.DeviceBean;
import java.util.List;

public class ListDeviceAdapter extends RecyclerView.Adapter<ListDeviceAdapter.ViewHolder> {

    private  String TAG = "ListDeviceAdapter";

    private Context context;
    private final List<DeviceBean> deviceBeanList;

    private HomeBean homeBean;
    public ListDeviceAdapter(Context context, List<DeviceBean> deviceList,HomeBean homeBean) {
        this.context = context;
        this.deviceBeanList = deviceList;
        this.homeBean = homeBean;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DeviceBean deviceList = deviceBeanList.get(position);
        Log.d(TAG,"DeviceBean"+deviceList.toString());

        holder.devName.setText(deviceList.getName());
        holder.devStatus.setText(deviceList.getIsOnline() ? "online" : "offline");
        if (deviceList.getIsOnline()) {
            holder.streamVideoButton.setOnClickListener(v -> {
                    Intent intent = new Intent(context, PlaybackP2pActivity.class);
                    intent.putExtra("devID", deviceList.devId);
                    intent.putExtra("homeID", homeBean.getHomeId());
                    context.startActivity(intent);
            });
        }
        else {
            holder.streamVideoButton.setVisibility(View.GONE);
            Toast.makeText(context," Camera offline",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return deviceBeanList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView devName, devStatus;
        Button streamVideoButton;

        ViewHolder(View itemView) {
            super(itemView);
            devName = itemView.findViewById(R.id.dev_name);
            devStatus = itemView.findViewById(R.id.dev_status);
            streamVideoButton = itemView.findViewById(R.id.steam_video);
        }
    }
}
