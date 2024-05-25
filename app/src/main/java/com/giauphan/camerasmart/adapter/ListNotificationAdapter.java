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

import androidx.recyclerview.widget.RecyclerView;

import com.giauphan.camerasmart.PlaybackP2pActivity;
import com.giauphan.camerasmart.R;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.ipc.messagecenter.bean.CameraMessageBean;
import com.thingclips.smart.sdk.bean.DeviceBean;

import java.util.List;

public class ListNotificationAdapter extends RecyclerView.Adapter<ListDeviceAdapter.ViewHolder> {

    private  String TAG = "ListDeviceAdapter";

    private Context context;
    private final List<CameraMessageBean> notification;

    private HomeBean homeBean;
    public ListNotificationAdapter(Context context, List<CameraMessageBean> notification,HomeBean homeBean) {
        this.context = context;
        this.notification = notification;
        this.homeBean = homeBean;
    }

    @Override
    public ListDeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_home, parent, false);
        return new ListDeviceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListDeviceAdapter.ViewHolder holder, int position) {
        CameraMessageBean deviceList = notification.get(position);
        Log.d(TAG,"DeviceBean"+deviceList.toString());

        holder.devName.setText(deviceList.getMsgTitle());
        holder.devStatus.setText(deviceList.getAttachPics());

    }

    @Override
    public int getItemCount() {
        return notification.size();
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
