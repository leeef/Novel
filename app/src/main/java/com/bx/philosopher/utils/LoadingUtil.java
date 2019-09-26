package com.bx.philosopher.utils;

import android.content.Context;

import com.bx.philosopher.widget.LoadingDialog;


/**
 * @ClassName: LoadingUtil
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/7/26 10:09
 */
public class LoadingUtil {

    private static LoadingDialog loadingDialog;

    public static void show(Context context) {
        loadingDialog = new LoadingDialog(context, "玩命加载中...");
        loadingDialog.show();
    }

    public static void hide() {
        if (loadingDialog != null && loadingDialog.isShowing())
            loadingDialog.dismiss();
    }
}
