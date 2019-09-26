package com.bx.philosopher.presenter.imp;

import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BookBean;

import java.util.List;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/17 15:39
 */
public interface BookShelfImp {
    interface View extends BaseContract.BaseView {
        void refresh(List<BookBean> data);

        void deleteDone(int resultCode);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void delete(List<BookBean> book);

        void getData();
    }
}
