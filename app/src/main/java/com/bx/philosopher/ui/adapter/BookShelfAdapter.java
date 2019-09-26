package com.bx.philosopher.ui.adapter;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.ui.adapter.view.BookShelfHolder;

import java.util.List;


public class BookShelfAdapter extends BaseListAdapter<BookBean> implements BookShelfHolder.LongClickIml {


    private boolean isLongClick = false;//是否在处理长按事件
    private boolean isSelectAll = false;

    private SelectIml selectIml;

    public interface SelectIml {
        void beSelected(int position, boolean isSelect);
    }

    public BookShelfAdapter(SelectIml selectIml) {
        this.selectIml = selectIml;
    }


    @Override
    public List<BookBean> getItems() {
        return super.getItems();
    }

    public void setLongClick(boolean isLongClick) {
        this.isLongClick = isLongClick;
        notifyDataSetChanged();
    }

    public void setSelectAll(boolean selectAll) {
        this.isSelectAll = selectAll;
        notifyDataSetChanged();
    }

    @Override
    public boolean getIsLongClick() {
        return isLongClick;
    }

    @Override
    public boolean isSelectAll() {
        return isSelectAll;
    }

    @Override
    public void selected(int position, boolean isSelect) {
        selectIml.beSelected(position, isSelect);
    }

    @Override
    protected IViewHolder<BookBean> createViewHolder(int viewType) {
        return new BookShelfHolder(this);
    }

    @Override
    public void removeItem(BookBean value) {
        super.removeItem(value);
        notifyDataSetChanged();
    }

}
