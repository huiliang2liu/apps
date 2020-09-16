package com.electricity.widget;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class StudyLayoutManage extends RecyclerView.LayoutManager {
    private static final String TAG = "StudyLayoutManage";
    private int first = 0;
    private int end = 0;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(500, 500);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        if (getChildCount() == 0 && state.isPreLayout())
            return;
        detachAndScrapAttachedViews(recycler);
        fill(recycler, state);
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int startX = getPaddingLeft();
        int startY = getPaddingTop();
        int endX = getWidth() - getPaddingRight();
        int endY = getHeight() - getPaddingBottom();
        for (int i = first; i < getItemCount(); i++) {
            View child = recycler.getViewForPosition(i);
            if (child == null)
                continue;
            addView(child);
            measureChildWithMargins(child, 0, 0);
            int width = getDecoratedMeasuredWidth(child);
            int height = getDecoratedMeasuredHeight(child);
            if (startX + width > endX) {
                startX = getPaddingLeft();
                startY += height;
//                if (startY > endX) {
//                    removeAndRecycleView(child, recycler);
//                    return;
//                }
            }
            Log.e(TAG, String.format("width:%d,height:%d,w:%d,h:%d", width, height, getWidth(), getHeight()));
            layoutDecoratedWithMargins(child, startX, startY, startX + Math.min(width, getWidth()), startY + Math.min(height, getHeight()));
            end++;
            startX += width;
            if (startY > endY) {
                removeAndRecycleView(child, recycler);
                startX -= width;
                end--;
                break;
            }

        }
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (dy == 0 || getChildCount() <= 0)
            return 0;
        if(dy>0){
            View child=getChildAt(getChildCount()-1);
            int bottom=child.getBottom()+getBottomDecorationHeight(child)+dy;
            int endY=getHeight()-getPaddingBottom();
            Log.e(TAG,String.format("bottom:%d,endY:%d",bottom,endY));
            if(bottom>endY){
                offsetChildrenVertical(-dy);
                return -dy;
            }else{
             return 0;
            }
        }else{
            return 0;
        }
    }
}
