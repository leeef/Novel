package com.bx.philosopher.net;

import android.util.Log;

import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Description: 网络请求初始化
 * @Author: leeeef
 * @CreateDate: 2019/5/17 16:48
 */
public class RetrofitClient {

    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    private static RetrofitClient retrofitClient;

    private RetrofitClient() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> {
            // 请求或者响应开始

            StringBuilder mMessage = new StringBuilder();
            // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
            if ((message.startsWith("{") && message.endsWith("}"))
                    || (message.startsWith("[") && message.endsWith("]"))) {
                message = JsonUtil.formatJson(JsonUtil.decodeUnicode(message));
            }
            mMessage.append(message.concat("\n"));
            Log.e("RetrofitClient", mMessage.toString());
        });
        Gson gson = new GsonBuilder().setLenient().create();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(Constant.CONNECT_TIME, TimeUnit.SECONDS);//连接 超时时间
        builder.writeTimeout(Constant.CONNECT_TIME, TimeUnit.SECONDS);//写操作 超时时间
        builder.readTimeout(Constant.CONNECT_TIME, TimeUnit.SECONDS);//读操作 超时时间
        builder.retryOnConnectionFailure(true);//错误重连
        builder.addInterceptor(httpLoggingInterceptor);
        mOkHttpClient = builder.build();
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constant.CLIENT_URL)
                .build();
    }

    public static RetrofitClient getInstance() {
        if (retrofitClient == null) {
            synchronized (RetrofitClient.class) {
                retrofitClient = new RetrofitClient();
            }
        }
        return retrofitClient;
    }

    Retrofit getRetrofit() {
        return mRetrofit;
    }
}
