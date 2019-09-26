package com.bx.philosopher.ui.adapter;

import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.BaseViewHolder;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.ui.adapter.view.ReadBgHolder;
import com.bx.philosopher.widget.page.PageStyle;


/**
 * Created by newbiechen on 17-5-19.
 */

public class ReadBgAdapter extends BaseListAdapter<Drawable> {
    private int currentChecked;

    @Override
    protected IViewHolder<Drawable> createViewHolder(int viewType) {
        return new ReadBgHolder();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        IViewHolder iHolder = ((BaseViewHolder) holder).holder;
        ReadBgHolder readBgHolder = (ReadBgHolder) iHolder;
        if (currentChecked == position) {
            readBgHolder.setChecked();
        }
    }

    public void setPageStyleChecked(PageStyle pageStyle){
        currentChecked = pageStyle.ordinal();
    }

    @Override
    protected void onItemClick(View v, int pos) {
        super.onItemClick(v, pos);
        currentChecked = pos;
        notifyDataSetChanged();
    }
}
