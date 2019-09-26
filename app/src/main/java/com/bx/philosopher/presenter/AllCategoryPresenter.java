package com.bx.philosopher.presenter;

import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.BookPackage;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.AllCategoryImp;
import com.google.gson.JsonArray;

import java.util.List;

/**
 * @ClassName: AllCategoryPresenter
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/4 13:49
 */
public class AllCategoryPresenter extends BasePresenter<AllCategoryImp.View> implements AllCategoryImp.Presenter {

    @Override
    public void getData() {
        HttpUtil.getInstance().getRequestApi().getAllCategory()
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<List<BookPackage>>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<List<BookPackage>> o) {
                        mView.showData(o.getData());
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }
}
