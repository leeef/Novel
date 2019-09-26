package com.bx.philosopher.presenter.imp;

import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.ExploreBanner;

import java.util.List;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 15:13
 */
public interface ExploreImp {
    interface View extends BaseContract.BaseView {

        void refreshBanner(ExploreBanner exploreBanner);

        void refreshList(List<BaseBean> data);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getData();
    }
}
