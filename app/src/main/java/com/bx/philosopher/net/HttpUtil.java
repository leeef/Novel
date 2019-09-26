package com.bx.philosopher.net;

import retrofit2.Retrofit;

/**
 * @Description: 接口工具类
 * @Author: leeeef
 * @CreateDate: 2019/5/17 17:04
 */
public class HttpUtil {
    private static HttpUtil httpUtil;
    private Retrofit retrofit;
    private RequestApi requestApi;

    private HttpUtil() {
        retrofit = RetrofitClient.getInstance().getRetrofit();
        requestApi = retrofit.create(RequestApi.class);
    }

    public static HttpUtil getInstance() {
        if (httpUtil == null) {
            synchronized (HttpUtil.class) {
                httpUtil = new HttpUtil();
            }
        }
        return httpUtil;
    }

    public RequestApi getRequestApi() {
        retrofit.baseUrl();

        return requestApi;
    }


}
