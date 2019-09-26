package com.bx.philosopher.ui.adapter.view;

import android.widget.ImageView;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.adapter.ViewHolderImpl;
import com.bx.philosopher.model.bean.MedalBean;

public class MyMedalHolder extends ViewHolderImpl<MedalBean> {

    ImageView my_medal_picture;
    TextView my_medal_name;

    @Override

    protected int getItemLayoutId() {
        return R.layout.my_medal_item;
    }

    @Override
    public void initView() {
        my_medal_picture = findById(R.id.my_medal_picture);
        my_medal_name = findById(R.id.my_medal_name);
    }

    @Override
    public void onBind(MedalBean data, int pos) {
        my_medal_name.setText(data.getName());
        my_medal_picture.setImageDrawable(getContext().getResources().getDrawable(data.getImage_id()));
    }
}
