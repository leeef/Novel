package com.bx.philosopher.presenter.imp;

import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.RecordBean;

import java.util.List;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 14:44
 */
public interface MyRecordImp {
    interface View extends BaseContract.BaseView {
        void showData(List<RecordBean> recordBeans);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getData();
    }
}
