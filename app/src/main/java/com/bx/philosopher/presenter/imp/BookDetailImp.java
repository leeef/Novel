package com.bx.philosopher.presenter.imp;

import android.app.Activity;

import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BookDetailBean;

import java.util.List;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 15:16
 */
public interface BookDetailImp {
    interface View extends BaseContract.BaseView {

        void showData(BookDetailBean bookDetailBean, List<BaseBean> baseBeans);

        void showWantSee(String result, String sign);

    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getData(String type, int bookId);

        void wantSee(int bookId, int favorite, String from_type);

        void addBookToShelf(int id);

        void download(List<String> chapter, int bookId, String from_type, Activity activity);

        void loadBook(int bookId, String from_type, Activity activity);
    }
}
