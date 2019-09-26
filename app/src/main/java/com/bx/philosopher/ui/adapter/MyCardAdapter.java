package com.bx.philosopher.ui.adapter;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.model.bean.response.GiftCardBean;
import com.bx.philosopher.ui.adapter.view.MyCardHolder;

/**
 * @ClassName: MyCardAdapter
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/12 10:34
 */
public class MyCardAdapter extends BaseListAdapter<GiftCardBean> {
    @Override
    protected IViewHolder<GiftCardBean> createViewHolder(int viewType) {
        return new MyCardHolder();
    }
}
