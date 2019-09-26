package com.bx.philosopher.ui.adapter.view;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.adapter.ViewHolderImpl;
import com.bx.philosopher.base.baseinterface.ItemSelectIml;
import com.bx.philosopher.model.bean.response.BookPackage;

public class CategoryTypeHolder extends ViewHolderImpl<BookPackage> {

    TextView category_type_name;
    LinearLayout category_type_container;

    private ItemSelectIml itemSelectIml;

    public CategoryTypeHolder(ItemSelectIml itemSelectIml) {
        this.itemSelectIml = itemSelectIml;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.category_type_item;
    }

    @Override
    public void initView() {
        category_type_name = findById(R.id.category_type_name);
        category_type_container = findById(R.id.category_type_container);
    }

    @Override
    public void onBind(BookPackage data, int pos) {
        if (pos == itemSelectIml.getSelectedPosition()) {
            category_type_container.setBackgroundColor(getContext().getResources().getColor(R.color.color_f5f5f5));
        } else {
            category_type_container.setBackgroundColor(getContext().getResources().getColor(R.color.white));
        }
        category_type_name.setText(data.getName());
    }


}
