package com.bx.philosopher.ui.adapter;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.model.bean.response.RecordBean;
import com.bx.philosopher.ui.adapter.view.MyRecordHolder;

public class MyRecordAdapter extends BaseListAdapter<RecordBean> {
    @Override
    protected IViewHolder<RecordBean> createViewHolder(int viewType) {
        return new MyRecordHolder();
    }
}
