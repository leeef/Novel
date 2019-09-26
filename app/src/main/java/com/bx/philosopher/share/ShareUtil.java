package com.bx.philosopher.share;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.bx.philosopher.R;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

/**
 * @ClassName: ShareUtil
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/5 18:43
 */
public class ShareUtil {

    private AlertDialog alertDialog;

    public void startShareAction(Activity activity, String url, String title, String sign) {

        View view = LayoutInflater.from(activity).inflate(R.layout.share_layout, null);
        view.findViewById(R.id.facebook).setOnClickListener(v -> share(activity, url, title, sign, SHARE_MEDIA.FACEBOOK));
        view.findViewById(R.id.QQ).setOnClickListener(v -> share(activity, url, title, sign, SHARE_MEDIA.QQ));
        view.findViewById(R.id.qq_zone).setOnClickListener(v -> share(activity, url, title, sign, SHARE_MEDIA.QZONE));
        view.findViewById(R.id.sina).setOnClickListener(v -> share(activity, url, title, sign, SHARE_MEDIA.SINA));
        alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.show();

        alertDialog.setContentView(view);
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(StringUtils.getDrawable(R.drawable.dialog_bg));
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        }
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setCancelable(true);


    }

    private void share(Activity activity, String url, String title, String sign, SHARE_MEDIA share_media) {

        UMWeb web = new UMWeb(url);
        web.setTitle(title);
        web.setDescription(sign);//描述
        web.setThumb(new UMImage(activity, R.drawable.appicon)); //缩略图
        new ShareAction(activity).withMedia(web)
                .setPlatform(share_media)
                .setCallback(shareListener)
                .share();
    }

    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            ToastUtils.show("success");
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastUtils.show("failed" + t.getMessage());
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            ToastUtils.show("canceled");

        }
    };
}
