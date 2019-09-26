package com.bx.philosopher.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseActivity;
import com.bx.philosopher.utils.SharedPreUtils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;

/**
 * @ClassName: GuideActivity
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/9/5 11:46
 */
public class GuideActivity extends BaseActivity {
    @BindView(R.id.guide_pager)
    ViewPager guide_pager;


    private GuideAdapter guideAdapter;

    private int downY;
    private int downX;
    private int[] pics = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};

    @Override
    protected int getContentId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        SharedPreUtils.getInstance().putBoolean("first", false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initWidget() {
        super.initWidget();
        ImmersionBar.with(this).reset().hideBar(BarHide.FLAG_HIDE_BAR).init();
        guideAdapter = new GuideAdapter();
        guide_pager.setAdapter(guideAdapter);

        guide_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                guide_pager.setOnTouchListener((v, event) -> {
                    int y = (int) event.getY();
                    int x = (int) event.getX();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            downY = y;
                            downX = x;
                            break;

                        case MotionEvent.ACTION_MOVE:
                            int slop = ViewConfiguration.get(GuideActivity.this).getScaledTouchSlop();
                            Log.i("yzp", downX + "+++" + event.getX());

                            if (position == 2) {
                                if (downX - event.getX() > slop && Math.abs(downX - event.getX()) > Math.abs(downY - event.getY())) {
                                    Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    GuideActivity.this.finish();
                                    return true;
                                }
                            }
                            break;
                    }
                    return false;
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pics.length;
        }


        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = View.inflate(GuideActivity.this, R.layout.guide_pager_item, null);
            ImageView guide_image = view.findViewById(R.id.guide_image);
            Glide.with(GuideActivity.this)
                    .load(pics[position])
                    .into(guide_image);

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
