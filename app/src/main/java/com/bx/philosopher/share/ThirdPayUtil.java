package com.bx.philosopher.share;

import android.app.Activity;
import android.util.Log;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.MyBalanceImp;
import com.bx.philosopher.utils.ToastUtils;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @ClassName: ThirdPayUtil
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/12 15:37
 */
public class ThirdPayUtil {


    //支付宝支付
    public Disposable ZFBPay(String orderInfo, Activity activity, MyBalanceImp.View baseView) {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        return Observable.create((ObservableOnSubscribe<Map<String, String>>) emitter -> {
            // 构造AuthTask 对象
            PayTask alipay = new PayTask(activity);
            Map<String, String> result = alipay.payV2(orderInfo, true);
            emitter.onNext(result);
        }).compose(RxScheduler.Obs_io_main())
                .subscribe(new Consumer<Map<String, String>>() {
                    @Override
                    public void accept(Map<String, String> s) throws Exception {
                        if (s.get("resultStatus").equals("9000")) {
                            ToastUtils.show("payment success ");
                            baseView.paySuccess();
                        }
                        Log.i(getClass().getName(), new Gson().toJson(s));
                    }
                });
    }

    //微信支付
    public void WXPay(String orderInfo, IWXAPI api) {

        PayReq request = new PayReq();
        request.appId = "wxd930ea5d5a258f4f";//你的微信appid
        request.partnerId = "1900000109";//商户号
        request.prepayId = "1101000000140415649af9fc314aa427";//预支付交易会话ID
        request.packageValue = "Sign=WXPay";//扩展字段,这里固定填写Sign=WXPay
        request.nonceStr = "1101000000140429eb40476f8896f4c9";//随机字符串
        request.timeStamp = "1398746574";//时间戳
        request.sign = "7FFECB600D7157C5AA49810D2D8F28BC2811827B";//签名
        api.sendReq(request);
    }


}
