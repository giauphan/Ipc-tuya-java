package com.giauphan.camerasmart.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.giauphan.camerasmart.R;
import com.giauphan.camerasmart.utils.VideoClip;
import com.thingclips.smart.camera.camerasdk.thingplayer.callback.OperationDelegateCallBack;
import com.thingclips.smart.camera.middleware.p2p.IThingSmartCameraP2P;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PlaybackDayAdapter extends RecyclerView.Adapter<PlaybackDayAdapter.ViewHolder> {
    private final List<VideoClip> playbackList;
    private Context context;

    private IThingSmartCameraP2P mCameraP2P;

    private boolean isPlayback = false;

    public PlaybackDayAdapter(Context context,List<VideoClip> productList,boolean isPlayback,IThingSmartCameraP2P mCameraP2P ) {
        this.context = context;
        this.mCameraP2P = mCameraP2P;
        this.isPlayback = isPlayback;
        this.playbackList = productList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_playback_p2p, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoClip playback = playbackList.get(position);
        Log.d("PlaybackDayAdapter", "getView called for position: " + position + ", product: " + playback);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        int startTimeSeconds = playback.getStartTime();
        Date startTimeDate = new Date((long) startTimeSeconds * 1000);
        String startTimeFormat = sdf.format(startTimeDate);

        int endTimeSeconds = playback.getEndTime();
        Date endTimeDate = new Date((long) endTimeSeconds * 1000);
        String endTimeFormat = sdf.format(endTimeDate);

        holder.startTime.setText(startTimeFormat);
        holder.endTime.setText(endTimeFormat);
        holder.streamVideoButton.setOnClickListener(v -> {
            playback(playback.getStartTime(),playback.getEndTime(),playback.getStartTime());
        });
    }

    private void playback(int startTime, int endTime, int playTime) {
        mCameraP2P.startPlayBack(startTime,
                endTime,
                playTime, new OperationDelegateCallBack() {
                    @Override
                    public void onSuccess(int sessionId, int requestId, String data) {
                        isPlayback = true;
                    }

                    @Override
                    public void onFailure(int sessionId, int requestId, int errCode) {
                        isPlayback = false;
                    }
                }, new OperationDelegateCallBack() {
                    @Override
                    public void onSuccess(int sessionId, int requestId, String data) {
                        isPlayback = false;
                    }

                    @Override
                    public void onFailure(int sessionId, int requestId, int errCode) {
                        isPlayback = false;
                    }
                });
    }
    @Override
    public int getItemCount() {
        return playbackList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView startTime, endTime;
        Button streamVideoButton;

        ViewHolder(View itemView) {
            super(itemView);
            startTime = itemView.findViewById(R.id.start_time);
            endTime = itemView.findViewById(R.id.end_time);
            streamVideoButton = itemView.findViewById(R.id.steam_video);
        }
    }
}
