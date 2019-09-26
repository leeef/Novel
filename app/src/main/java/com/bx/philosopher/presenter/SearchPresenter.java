package com.bx.philosopher.presenter;

import android.util.Log;

import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.SearchIml;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.SharedPreUtils;
import com.bx.philosopher.utils.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: SearchPresenter
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/4 19:33
 */
public class SearchPresenter extends BasePresenter<SearchIml.View> implements SearchIml.Presenter {


    @Override
    public void startSearch(String content) {
        Log.i("SearchPresenter", content);
        if (!StringUtils.isTrimEmpty(content)) {
            addSearchHistory(content);
        }
        HttpUtil.getInstance().getRequestApi().getLibrarySearch(content)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<List<BookBean>>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<List<BookBean>> o) {
                        Log.i("SearchPresenter", o.getData().size() + "===");
                        List<BaseBean> data = new ArrayList<>();
                        data.addAll(o.getData());
                        mView.showResult(data);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    private void addSearchHistory(String search) {
        String history = SharedPreUtils.getInstance().getString(Constant.SEARCHKEY);
        if (StringUtils.isTrimEmpty(history)) history = "[]";//空数据
        JsonArray jsonArray = new JsonParser().parse(history).getAsJsonArray();
        boolean hasSearch = false;
        for (JsonElement jsonElement : jsonArray) {
            if (jsonElement.getAsString().equals(search)) {
                hasSearch = true;
                break;
            }
        }
        if (!hasSearch) jsonArray.add(search);
        SharedPreUtils.getInstance().putString(Constant.SEARCHKEY, jsonArray.toString());
    }
}
