package com.bx.philosopher.presenter;

import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.MyMedalImp;
import com.bx.philosopher.utils.login.LoginUtil;

import java.util.List;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 14:50
 */
public class MyMedalPresenter extends BasePresenter<MyMedalImp.View> implements MyMedalImp.Presenter {
    @Override
    public void getData() {
        HttpUtil.getInstance().getRequestApi().getMedalList(LoginUtil.getUserId())
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<List<String>>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<List<String>> o) {
                        mView.showData(o.getData());
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }
}
