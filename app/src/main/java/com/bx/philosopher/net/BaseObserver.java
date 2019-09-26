package com.bx.philosopher.net;

import android.net.ParseException;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.utils.LoadingUtil;
import com.bx.philosopher.utils.StringUtils;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

/**
 * @Description: 处理网络请求返回的结果
 * @Author: leeeef
 * @CreateDate: 2019/5/20 13:57
 */
public abstract class BaseObserver<T> extends DisposableObserver<T> {
    protected BaseContract.BaseView view;
    /**
     * 解析数据失败
     */
    public static final int PARSE_ERROR = 1001;
    /**
     * 网络问题
     */
    public static final int BAD_NETWORK = 1002;
    /**
     * 连接错误
     */
    public static final int CONNECT_ERROR = 1003;
    /**
     * 连接超时
     */
    public static final int CONNECT_TIMEOUT = 1004;


    public BaseObserver(BaseContract.BaseView view) {
        this.view = view;
    }


    @Override
    protected void onStart() {
        //请求前的加载框
//        if (view != null) {
//            view.showLoading();
//        }
    }


    @Override
    public void onNext(T o) {
        //处理请求成功的数据
        try {
            BaseResponse baseResponse = (BaseResponse) o;
            if (baseResponse.getCode() == 200) {
                onSuccess(o);
            } else {
                if (view != null) {
                    view.showError(baseResponse.getMsg());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(e.toString());
        }


    }

    @Override
    public void onError(Throwable e) {
//        if (view != null) {
//            view.hideLoading();
//        }
        if (e instanceof HttpException) {
            //   HTTP错误
            onException(BAD_NETWORK);
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {
            //   连接错误
            onException(CONNECT_ERROR);
        } else if (e instanceof InterruptedIOException) {
            //  连接超时
            onException(CONNECT_TIMEOUT);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            //  解析错误
            onException(PARSE_ERROR);
        } else {
            if (e != null) {
                onError(e.toString());
            } else {
                onError(StringUtils.getString(R.string.error));
            }
        }

    }

    private void onException(int unknownError) {
        switch (unknownError) {
            case CONNECT_ERROR:
                onError(StringUtils.getString(R.string.error));
                break;

            case CONNECT_TIMEOUT:
                onError(StringUtils.getString(R.string.error));
                break;

            case BAD_NETWORK:
                onError(StringUtils.getString(R.string.error));
                break;

            case PARSE_ERROR:
                onError(StringUtils.getString(R.string.error));
                break;

            default:
                break;
        }
    }


    @Override
    public void onComplete() {
//        if (view != null) {
//            view.hideLoading();
//        }

    }

    public abstract void onSuccess(T o);

    public abstract void onError(String msg);
}