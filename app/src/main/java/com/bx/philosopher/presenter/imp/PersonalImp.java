package com.bx.philosopher.presenter.imp;

import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.PersonInfo;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 15:12
 */
public interface PersonalImp {
    interface View extends BaseContract.BaseView {
        void showData(PersonInfo personInfo);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getData();
    }
}
