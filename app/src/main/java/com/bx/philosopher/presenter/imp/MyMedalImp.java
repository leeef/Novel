package com.bx.philosopher.presenter.imp;

import com.bx.philosopher.base.activity.BaseContract;

import java.util.List;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 14:49
 */
public interface MyMedalImp {
    interface View extends BaseContract.BaseView {
        void showData(List<String> list);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getData();
    }
}
