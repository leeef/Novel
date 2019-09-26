package com.bx.philosopher.presenter.imp;

import com.bx.philosopher.base.activity.BaseContract;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 14:56
 */
public interface ProposalImp {
    interface View extends BaseContract.BaseView {
        void upload(List<String> cover);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void submission(String content, String phone, String cover);

        void loadImage(String base64Str);
    }
}
