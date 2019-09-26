package com.bx.philosopher.presenter;

import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.ProposalImp;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.utils.login.LoginUtil;
import com.google.gson.JsonElement;

import java.util.List;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 14:56
 */
public class ProposalPresenter extends BasePresenter<ProposalImp.View> implements ProposalImp.Presenter {
    @Override
    public void submission(String content, String phone, String cover) {
        HttpUtil.getInstance().getRequestApi().proposal(LoginUtil.getUserId(), content, phone, cover)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<JsonElement>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<JsonElement> o) {
                        ToastUtils.show(o.getMsg());
                        mView.complete();
                    }

                    @Override
                    public void onError(String msg) {
                        mView.showError(msg);
                    }
                });
    }

    @Override
    public void loadImage(String base64Str) {
        HttpUtil.getInstance().getRequestApi().upLoadImage(base64Str)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<List<String>>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<List<String>> o) {
                        mView.upload(o.getData());
                    }

                    @Override
                    public void onError(String msg) {
                        mView.showError(msg);
                    }
                });
    }


}
