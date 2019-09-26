package com.bx.philosopher.presenter;

import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.LoginBean;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.LoginImp;
import com.bx.philosopher.utils.ToastUtils;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/21 14:20
 */
public class LoginPresenter extends BasePresenter<LoginImp.View> implements LoginImp.Presenter {

    @Override
    public void login(String phone, String password) {
        HttpUtil.getInstance().getRequestApi().login(phone, password)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<LoginBean>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<LoginBean> o) {
                        mView.showError(o.getMsg());
                        mView.loginSuc(o.getData());

                    }

                    @Override
                    public void onError(String msg) {
                        mView.showError(msg);
                    }
                });
    }
}
