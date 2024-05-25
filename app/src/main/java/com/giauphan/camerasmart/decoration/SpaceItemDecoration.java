package com.giauphan.camerasmart.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        if (position % 2 == 0) { // Check if it's an even position
            outRect.left = space; // Add space to the left of even items
        } else {
            outRect.right = space; // Add space to the right of odd items
        }
    }
}
