package com.bx.philosopher.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.bx.philosopher.R;
import com.bx.philosopher.utils.ScreenUtils;

/**
 * 阅读页面长按的弹窗
 */

public class ReadDialogUtil {

    static ReadDialogClick mReadDialogClick;

    private PopupWindow readDialog;
    private Context mContext;
    View view;

    public PopupWindow getInstance(Context context, ReadDialogClick readDialogClick) {
        if (readDialog == null) {
            synchronized (ReadDialogUtil.class) {
                mContext = context;
                mReadDialogClick = readDialogClick;
                init();
            }
        }
        return readDialog;
    }


    private void init() {
        view = LayoutInflater.from(mContext).inflate(R.layout.read_long_click_menu, null);
        readDialog = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.findViewById(R.id.copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReadDialogClick != null) {
                    mReadDialogClick.copy();
                    readDialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReadDialogClick != null) {
                    mReadDialogClick.underLine();
                    readDialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReadDialogClick != null) {
                    mReadDialogClick.note();
                    readDialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReadDialogClick != null) {
                    mReadDialogClick.share();
                    readDialog.dismiss();
                }
            }
        });
        readDialog.setOutsideTouchable(false);
        readDialog.setFocusable(false);
    }

    public int getHeight() {
        return view.getMeasuredHeight();
    }

    public void setBackGround(boolean up) {
        if (up) {
            view.setPadding(ScreenUtils.pxToDp(10), 0, ScreenUtils.pxToDp(10), 0);
            view.setBackground(mContext.getResources().getDrawable(R.drawable.read_window_bg));
        } else {
            view.setPadding(ScreenUtils.pxToDp(10), ScreenUtils.pxToDp(30), ScreenUtils.pxToDp(10), 0);
            view.setBackground(mContext.getResources().getDrawable(R.drawable.read_long_menu_bg));

        }
    }


    public interface ReadDialogClick {
        void copy();

        void underLine();

        void note();

        void share();
    }

}
