package com.bx.philosopher.utils;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.bx.philosopher.PhilosopherApplication;


public class ToastUtils {

    private static Toast toast;

    @SuppressLint("ShowToast")
    public static void show(String content) {
        if (toast == null) {
            toast = Toast.makeText(PhilosopherApplication.getContext(), content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
