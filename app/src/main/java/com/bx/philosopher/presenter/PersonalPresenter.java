package com.bx.philosopher.presenter;

import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.PersonInfo;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.PersonalImp;
import com.bx.philosopher.utils.login.LoginUtil;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 15:12
 */
public class PersonalPresenter extends BasePresenter<PersonalImp.View> implements PersonalImp.Presenter {
    @Override
    public void getData() {
        HttpUtil.getInstance().getRequestApi().getPersonInfo(LoginUtil.getUserId())
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<PersonInfo>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<PersonInfo> o) {
                        mView.showData(o.getData());
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }
}
