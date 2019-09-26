package com.bx.philosopher.presenter.imp;

import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BookBean;

import java.util.List;

/**
 * @Description: 我的下载接口
 * @Author: leeeef
 * @CreateDate: 2019/5/17 15:06
 */
public interface MyDownloadImp {
    interface View extends BaseContract.BaseView {
        void refresh(List<BookBean> data);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getData();

        void delete(int bid);
    }
}
