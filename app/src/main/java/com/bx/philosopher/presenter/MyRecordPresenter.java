package com.bx.philosopher.presenter;

import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.RecordBean;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.MyRecordImp;
import com.bx.philosopher.utils.login.LoginUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 14:45
 */
public class MyRecordPresenter extends BasePresenter<MyRecordImp.View> implements MyRecordImp.Presenter {
    @Override
    public void getData() {
        HttpUtil.getInstance().getRequestApi().getRecord(LoginUtil.getUserId())
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<List<RecordBean>>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<List<RecordBean>> o) {
                        mView.showData(o.getData());
                    }

                    @Override
                    public void onError(String msg) {
                        mView.showData(new ArrayList<>());
                    }
                });
    }
}
