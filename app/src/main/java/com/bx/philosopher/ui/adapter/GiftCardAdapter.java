package com.bx.philosopher.ui.adapter;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.base.baseinterface.ItemSelectIml;
import com.bx.philosopher.ui.adapter.view.GiftCardHolder;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/22 17:26
 */
public class GiftCardAdapter extends BaseListAdapter<Integer> {

    private ItemSelectIml itemSelectIml;

    public GiftCardAdapter(ItemSelectIml itemSelectIml) {
        this.itemSelectIml = itemSelectIml;
    }

    @Override
    protected IViewHolder<Integer> createViewHolder(int viewType) {
        return new GiftCardHolder(itemSelectIml);
    }
}
