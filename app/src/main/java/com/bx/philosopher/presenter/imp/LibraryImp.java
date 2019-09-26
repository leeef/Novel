package com.bx.philosopher.presenter.imp;

import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BookBean;

import java.util.List;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 15:07
 */
public interface LibraryImp {
    interface View extends BaseContract.BaseView {

        void showBanner(List<String> pics);

        void showList(List<BaseBean> libraryList);

        void showTopList(List<BookBean> list);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getData();
    }
}
