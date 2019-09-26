package com.bx.philosopher.presenter;

import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.BookShelfImp;
import com.bx.philosopher.utils.login.LoginUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 书架
 * @Author: leeeef
 * @CreateDate: 2019/5/17 15:40
 */
public class BookShelfPresenter extends BasePresenter<BookShelfImp.View> implements BookShelfImp.Presenter {


    /**
     * @Description: 删除书籍
     */
    @Override
    public void delete(List<BookBean> book) {
        List<Integer> bids = new ArrayList<>();
        for (BookBean bookBean : book) {
            bids.add(bookBean.getId());
        }
        HttpUtil.getInstance().getRequestApi().deleteBookFromBookShelf(LoginUtil.getUserId(), bids)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<Integer>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<Integer> o) {
                        mView.deleteDone(o.getData());
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    /**
     * @Description: 获取数据
     */
    @Override
    public void getData() {

        HttpUtil.getInstance().getRequestApi().getBookShelf(LoginUtil.getUserId())
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

    /**
     * @Description: 添加
     */
    public void addData() {
    }
}
