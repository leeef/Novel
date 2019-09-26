package com.bx.philosopher.presenter;

import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.PersonInfo;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.MyBalanceImp;
import com.bx.philosopher.utils.login.LoginUtil;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 14:37
 */
public class MyBalancePresenter extends BasePresenter<MyBalanceImp.View> implements MyBalanceImp.Presenter {
    @Override
    public void getData() {

    }

    @Override
    public void pay(int total, String payway) {
        HttpUtil.getInstance().getRequestApi().orderForm(LoginUtil.getUserId(), total, payway, LoginUtil.getName())
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<String>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<String> o) {
                        mView.getpPayInfo(o.getData());
                    }

                    @Override
                    public void onError(String msg) {
                        mView.showError(msg);
                    }
                });
    }

    public void getBalance() {
        HttpUtil.getInstance().getRequestApi().getPersonInfo(LoginUtil.getUserId())
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<PersonInfo>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<PersonInfo> o) {
                        LoginUtil.setUserAccount(o.getData().getBalance());
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }


}
