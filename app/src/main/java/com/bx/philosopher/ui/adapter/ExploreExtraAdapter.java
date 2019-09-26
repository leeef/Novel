package com.bx.philosopher.ui.adapter;

import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.ui.adapter.view.ExploreExtraHolder;
import com.bx.philosopher.widget.adapter.WholeAdapter;

public class ExploreExtraAdapter extends WholeAdapter<BaseBean> {


    @Override
    public int getItemViewType(int position) {
        int index = super.getItemViewType(position);
        if (index == TYPE_HEAD || index == TYPE_FOOT) return index;
        return getItem(index - mHeaderList.size()).getViewType();
    }


    @Override
    protected IViewHolder<BaseBean> createViewHolder(int viewType) {
        return new ExploreExtraHolder(viewType);
    }


}
