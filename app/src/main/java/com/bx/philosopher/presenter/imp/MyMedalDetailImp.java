package com.bx.philosopher.presenter.imp;

import com.bx.philosopher.base.activity.BaseContract;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 14:53
 */
public interface MyMedalDetailImp {
    interface View extends BaseContract.BaseView {

    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getData();
    }
}
