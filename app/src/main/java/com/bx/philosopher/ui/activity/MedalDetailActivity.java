package com.bx.philosopher.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseMVPActivity;
import com.bx.philosopher.presenter.MyMedalDetailPresenter;
import com.bx.philosopher.presenter.imp.MyMedalDetailImp;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.StringUtils;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;

public class MedalDetailActivity extends BaseMVPActivity<MyMedalDetailPresenter> implements MyMedalDetailImp.View {


    @BindView(R.id.medal_picture)
    ImageView medal_picture;
    @BindView(R.id.back_image)
    ImageView back_image;

    private int medal_id;

    @Override
    protected int getContentId() {
        return R.layout.activity_medal_detail;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        ImmersionBar.with(this).statusBarColor(R.color.color_65c933).init();
        back_image.setImageDrawable(StringUtils.getDrawable(R.drawable.arrow_left, R.color.white));
        medal_id = getIntent().getIntExtra("medal_id", 0);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        medal_picture.setImageDrawable(getResources().getDrawable(medal_id));
    }

    @Override
    public void showError(String errMsg) {
        Log.i(Constant.TAG,errMsg);
    }

    @Override
    public void complete() {

    }

    @Override
    protected MyMedalDetailPresenter bindPresenter() {
        return new MyMedalDetailPresenter();
    }
}
