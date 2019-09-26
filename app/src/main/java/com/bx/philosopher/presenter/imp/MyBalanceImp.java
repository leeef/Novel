package com.bx.philosopher.presenter.imp;

import com.bx.philosopher.base.activity.BaseContract;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 14:35
 */
public interface MyBalanceImp {

    interface View extends BaseContract.BaseView {
        void getpPayInfo(String orderInfo);

        void paySuccess();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getData();

        void pay(int total, String payway);
    }
}
