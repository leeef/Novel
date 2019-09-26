package com.bx.philosopher.widget;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

/**
 * @ClassName: BannerImageLoader
 * @Description: banner 图片加载器
 * @Author: leeeeef
 * @CreateDate: 2019/5/27 15:23
 */
public class BannerImageLoader extends ImageLoader {
    private static final long serialVersionUID = -1610264227548669937L;

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context).load(path).into(imageView);
    }
}
