package com.bx.philosopher.presenter.imp;

import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BaseBean;

import java.util.List;

/**
 * @ClassName: SearchIml
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/4 19:32
 */
public interface SearchIml {
    interface View extends BaseContract.BaseView {
        void showResult(List<BaseBean> result);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void startSearch(String content);
    }
}
