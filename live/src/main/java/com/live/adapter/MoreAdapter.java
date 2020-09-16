package com.live.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.adapter.RecyclerViewAdapter;
import com.base.adapter.tag.ViewHolder;
import com.live.R;
import com.live.entities.MoreEntity;

public class MoreAdapter extends RecyclerViewAdapter<MoreEntity> {
    public MoreAdapter(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    public ViewHolder<MoreEntity> getViewHolder(int itemType) {
        return new VH();
    }

    @Override
    public int getView(int itemType) {
        return R.layout.holder_more;
    }

    private class VH extends ViewHolder<MoreEntity> {
        private ImageView imageView;
        private TextView textView;

        @Override
        public void setContext(MoreEntity moreEntity) {
            imageView.setImageResource(moreEntity.ico);
            textView.setText(moreEntity.name);
        }

        @Override
        public void bindView() {
            imageView = (ImageView) findViewById(R.id.holder_more_iv);
            textView = (TextView) findViewById(R.id.holder_more_tv);
        }
    }
}
