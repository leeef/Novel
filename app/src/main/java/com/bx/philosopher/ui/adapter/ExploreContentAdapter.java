package com.bx.philosopher.ui.adapter;

import android.view.View;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.ui.activity.BookDetailActivity;
import com.bx.philosopher.ui.adapter.view.ExploreContentHolder;

public class ExploreContentAdapter extends BaseListAdapter<BaseBean> {
    private int height;

    public ExploreContentAdapter(int height) {
        this.height = height;
    }

    public ExploreContentAdapter() {
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    protected IViewHolder<BaseBean> createViewHolder(int viewType) {
        if (viewType == 1) return new ExploreContentHolder(viewType, height);
        return new ExploreContentHolder(viewType);
    }

}
