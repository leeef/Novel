package com.bx.philosopher.ui.adapter;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.base.baseinterface.ItemSelectIml;
import com.bx.philosopher.model.bean.response.BookPackage;
import com.bx.philosopher.ui.adapter.view.CategoryTypeHolder;

public class CategoryTypeAdapter extends BaseListAdapter<BookPackage> {

    private ItemSelectIml itemSelectIml;

    public CategoryTypeAdapter(ItemSelectIml itemSelectIml) {
        this.itemSelectIml = itemSelectIml;
    }

    @Override
    protected IViewHolder<BookPackage> createViewHolder(int viewType) {
        return new CategoryTypeHolder(itemSelectIml);
    }
}
