package com.giauphan.camerasmart.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.giauphan.camerasmart.PlaybackMonthActivity;
import com.giauphan.camerasmart.R;
import com.giauphan.camerasmart.utils.VideoClip;

import java.util.List;

public class CustomListAdapter extends ArrayAdapter<VideoClip> {

    private  String TAG = "CustomListAdapter";
    public CustomListAdapter(Context context, List<VideoClip> productList) {
        super(context, 0, productList);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        VideoClip product = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_playback_month, parent, false);
        }

        TextView start_time = convertView.findViewById(R.id.start_time);
        TextView end_time = convertView.findViewById(R.id.end_time);
        TextView type = convertView.findViewById(R.id.type);
        Log.d(TAG,"products: "+product);
        assert product != null;
        start_time.setText(product.getStartTime());
        end_time.setText(product.getEndTime());
        type.setText(String.valueOf(product.getType()));

        return convertView;
    }
}
