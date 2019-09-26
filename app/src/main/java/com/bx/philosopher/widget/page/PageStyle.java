package com.bx.philosopher.widget.page;

import androidx.annotation.ColorRes;

import com.bx.philosopher.R;


/**
 * Created by newbiechen on 2018/2/5.
 * 作用：页面的展示风格。
 */

public enum PageStyle {
    BG_0(R.color.book_read_bg_1, R.color.book_read_1),
    BG_1(R.color.book_read_bg_2, R.color.book_read_2),
    BG_2(R.color.book_read_bg_3, R.color.book_read_3),
    BG_3(R.color.book_read_bg_4, R.color.book_read_4),
    BG_4(R.color.book_read_bg_5, R.color.book_read_5),
    NIGHT(R.color.read_night, R.color.read_bg_night),;

    private int fontColor;
    private int bgColor;

    PageStyle(@ColorRes int bgColor, @ColorRes int fontColor) {
        this.fontColor = fontColor;
        this.bgColor = bgColor;
    }

    public int getFontColor() {
        return fontColor;
    }

    public int getBgColor() {
        return bgColor;
    }
}
