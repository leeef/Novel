package com.bx.philosopher.presenter;

import com.bx.philosopher.PhilosopherApplication;
import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.SettingImp;
import com.bx.philosopher.utils.StringUtils;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/21 14:49
 */
public class SettingPresenter extends BasePresenter<SettingImp.View> implements SettingImp.Presenter {


    @Override
    public void checkUpdate() {
        HttpUtil.getInstance().getRequestApi().checkVersion(StringUtils.getVersionName(PhilosopherApplication.getContext()))
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<Object>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<Object> o) {
                        mView.showError(o.getMsg());
                    }

                    @Override
                    public void onError(String msg) {
                        mView.showError(msg);
                    }
                });
    }

    @Override
    public void signOut() {

    }
}
