package com.bx.philosopher.ui.adapter;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.model.bean.MedalBean;
import com.bx.philosopher.ui.adapter.view.MyMedalHolder;

public class MyMedalAdapter extends BaseListAdapter<MedalBean> {
    @Override
    protected IViewHolder<MedalBean> createViewHolder(int viewType) {
        return new MyMedalHolder();
    }
}
