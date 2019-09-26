package com.bx.philosopher.ui.adapter;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.ui.adapter.view.BookMarkHolder;

/**
 * @ClassName: BookMarkAdapter
 * @Description: 书签适配器
 * @Author: leeeeef
 * @CreateDate: 2019/5/24 15:56
 */
public class BookMarkAdapter extends BaseListAdapter<BaseBean> {

    @Override
    protected IViewHolder<BaseBean> createViewHolder(int viewType) {
        return new BookMarkHolder(viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return getItems().get(position).getViewType();
    }
}
