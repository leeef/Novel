package com.bx.philosopher.share;

import android.app.Activity;
import android.util.Log;

import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.UserInfo;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.LoginImp;
import com.bx.philosopher.utils.login.LoginUtil;
import com.google.gson.Gson;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ThirdPartyLogin
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/6 10:33
 */
public class ThirdPartyLogin {

    //第三方登录
    public void ThirdLogin(Activity activity, LoginImp.View view, SHARE_MEDIA share_media) {
        //每次登陆都拉起授权页面
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(activity).setShareConfig(config);
        UMShareAPI.get(activity).getPlatformInfo(activity, share_media, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Log.i(getClass().getName(), "onStart");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {

                //名字 头像   id 微博无没有手机号返回
                String nickname = map.get("name");
                String openid = map.get("uid");
                String headimg = map.get("iconurl");
                Map<String, String> param = new HashMap<>();
                param.put("nickname", nickname);
                param.put("openid", openid);
                param.put("headimg", headimg);
                param.put("phone", "");
                HttpUtil.getInstance().getRequestApi().weiboLogin(param)
                        .compose(RxScheduler.Obs_io_main())
                        .subscribe(new BaseObserver<BaseResponse<UserInfo>>(view) {
                            @Override
                            public void onSuccess(BaseResponse<UserInfo> o) {
                                UserInfo userInfo = o.getData();

                                LoginUtil.setUserId(userInfo.getId());
                                LoginUtil.setLogin(true);
                                LoginUtil.setHead(userInfo.getHeadimg());
                                LoginUtil.setName(userInfo.getNickname());
                                LoginUtil.setPhone(userInfo.getPhone());
                                LoginUtil.setUserAccount(userInfo.getBalance());
                                view.complete();
                            }

                            @Override
                            public void onError(String msg) {
                                view.showError(msg);
                            }
                        });
                Log.i(getClass().getName(), "onComplete:" + new Gson().toJson(map));

            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Log.i(getClass().getName(), "onError");

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Log.i(getClass().getName(), "onCancel");
            }
        });

    }

}
