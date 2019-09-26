package com.bx.philosopher.presenter.imp;


import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BookPackage;

import java.util.List;

/**
 * @ClassName: AllCategoryImp
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/4 13:48
 */
public interface AllCategoryImp {
    interface View extends BaseContract.BaseView {
        void showData(List<BookPackage> data);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getData();
    }
}
