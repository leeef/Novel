package com.bx.philosopher.ui.adapter.view;

import android.widget.CheckBox;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.adapter.ViewHolderImpl;

import java.io.File;
import java.util.HashMap;

public class LocalFileHolder extends ViewHolderImpl<File> {


    private TextView book_name;
    private CheckBox book_select;

    private HashMap<File, Boolean> mSelectedMap;

    public LocalFileHolder(HashMap<File, Boolean> selectedMap) {
        mSelectedMap = selectedMap;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.local_file_item;
    }

    @Override
    public void initView() {
        book_name = findById(R.id.book_name);
        book_select = findById(R.id.book_select);
    }

    @Override
    public void onBind(File data, int pos) {
        book_name.setText(data.getName());
        boolean isSelected = mSelectedMap.get(data);
        book_select.setChecked(isSelected);
    }
}
