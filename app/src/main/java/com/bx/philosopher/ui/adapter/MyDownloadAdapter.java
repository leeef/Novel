package com.bx.philosopher.ui.adapter;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.base.baseinterface.BaseClickListener;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.ui.adapter.view.MyDownloadHolder;

public class MyDownloadAdapter extends BaseListAdapter<BookBean> {

    private BaseClickListener baseClickListener;

    public MyDownloadAdapter(BaseClickListener baseClickListener) {
        this.baseClickListener = baseClickListener;
    }

    @Override
    protected IViewHolder<BookBean> createViewHolder(int viewType) {
        return new MyDownloadHolder(baseClickListener);
    }

}
