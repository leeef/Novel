package com.bx.philosopher.ui.adapter.view;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bx.philosopher.R;
import com.bx.philosopher.base.adapter.ViewHolderImpl;


/**
 * Created by newbiechen on 17-5-19.
 */

public class ReadBgHolder extends ViewHolderImpl<Drawable> {

    private ImageView mReadBg;
    private LinearLayout read_bg_view_container;

    @Override
    public void initView() {
        mReadBg = findById(R.id.read_bg_view);
        read_bg_view_container = findById(R.id.read_bg_view_container);
    }

    @Override
    public void onBind(Drawable data, int pos) {
        mReadBg.setBackground(data);
        read_bg_view_container.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_read_bg;
    }

    public void setChecked() {
        read_bg_view_container.setBackground(getContext().getResources().getDrawable(R.drawable.read_colour_select_bg));
    }
}
