package com.bx.philosopher.presenter;

import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.MyDownloadImp;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.utils.login.LoginUtil;

import java.util.List;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/17 15:09
 */
public class MyDownloadPresenter extends BasePresenter<MyDownloadImp.View> implements MyDownloadImp.Presenter {


    @Override
    public void getData() {
        HttpUtil.getInstance().getRequestApi().getMyDownLoad(LoginUtil.getUserId())
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<List<BookBean>>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<List<BookBean>> o) {

                        mView.refresh(o.getData());
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });


    }

    @Override
    public void delete(int bid) {
        HttpUtil.getInstance().getRequestApi().deleteMyDownLoad(LoginUtil.getUserId(), bid)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<Integer>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<Integer> o) {
                        ToastUtils.show(o.getMsg());
                        getData();
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }


}
