package com.bx.philosopher.ui.adapter;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.base.baseinterface.BaseClickListener;
import com.bx.philosopher.model.bean.BookNoteBean;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.ui.adapter.view.BookNoteHolder;

public class BookNoteAdapter extends BaseListAdapter<BaseBean> {

    private BaseClickListener baseClickListener;

    public BookNoteAdapter(BaseClickListener baseClickListener) {
        this.baseClickListener = baseClickListener;
    }

    @Override
    protected IViewHolder<BaseBean> createViewHolder(int viewType) {
        return new BookNoteHolder(baseClickListener, viewType);
    }


    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

}
