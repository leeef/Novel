package com.bx.philosopher.base.fragment;

import android.view.View;

import com.bx.philosopher.base.activity.BaseContract;

/**
 * Created by newbiechen on 17-4-25.
 */

public abstract class BaseMVPFragment<T extends BaseContract.BasePresenter> extends BaseLazyFragment implements BaseContract.BaseView {

    protected T mPresenter;

    protected abstract T bindPresenter();


    @Override
    protected void initViewData(View v) {

    }

    @Override
    protected void onBindPresenter() {
        mPresenter = bindPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
