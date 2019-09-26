package com.bx.philosopher.presenter.imp;

import com.bx.philosopher.base.activity.BaseContract;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/21 14:47
 */
public interface SettingImp {

    interface View extends BaseContract.BaseView {

    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        //检查更新
        void checkUpdate();

        //退出
        void signOut();
    }
}
