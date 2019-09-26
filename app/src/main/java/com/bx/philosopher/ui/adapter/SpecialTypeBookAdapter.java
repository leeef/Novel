package com.bx.philosopher.ui.adapter;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.ui.adapter.view.SpecialTypeBookHolder;

public class SpecialTypeBookAdapter extends BaseListAdapter<BaseBean> {

    private boolean show_rank_image;

    public SpecialTypeBookAdapter(boolean show_rank_image) {
        this.show_rank_image = show_rank_image;
    }

    @Override
    protected IViewHolder<BaseBean> createViewHolder(int viewType) {
        return new SpecialTypeBookHolder(show_rank_image);
    }
}
