package com.bx.philosopher.widget.page;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * 阅读页面 每页文章的信息
 */

public class TxtPage {
    int position;//页码
    String title;  //标题
    int titleLines; //当前 lines 中为 title 的行数。
    List<String> lines;//每页的文字信息 以行位单位

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : lines) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
}
