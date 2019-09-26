package com.bx.philosopher.presenter.imp;

import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.LoginBean;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/21 14:19
 */
public interface LoginImp {
    interface View extends BaseContract.BaseView {
        void loginSuc(LoginBean loginBean);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void login(String phone, String password);
    }
}
