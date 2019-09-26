package com.bx.philosopher.ui.adapter;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.base.baseinterface.ItemSelectIml;
import com.bx.philosopher.ui.adapter.view.RechargeHolder;

public class RechargeAdapter extends BaseListAdapter<String> {

    private ItemSelectIml itemSelect;

    public RechargeAdapter(ItemSelectIml itemSelect) {
        this.itemSelect = itemSelect;
    }

    @Override
    protected IViewHolder<String> createViewHolder(int viewType) {
        return new RechargeHolder(itemSelect);
    }


}
