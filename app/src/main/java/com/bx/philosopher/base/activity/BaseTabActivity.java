package com.bx.philosopher.base.activity;

import android.content.Context;

import com.bx.philosopher.ui.activity.GuideActivity;
import com.bx.philosopher.utils.SharedPreUtils;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.widget.NoScrollViewPager;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;

/**
 * Created by newbiechen on 17-4-24.
 */

public abstract class BaseTabActivity extends BaseActivity {
    /**************View***************/
    @BindView(R.id.tab_tl_indicator)
    protected TabLayout mTlIndicator;
    @BindView(R.id.tab_vp)
    protected NoScrollViewPager mVp;
    /************Params*******************/
    private List<Fragment> mFragmentList;
    private List<String> mTitleList;

    /**************abstract***********/
    protected abstract List<Fragment> createTabFragments();

    protected abstract List<String> createTabTitles();

    //底部导航栏图标
    private int[] tab_icons = {R.drawable.explore_tab_bg, R.drawable.library_tab_bg, R.drawable.bookshelf_tab_bg, R.drawable.personal_tab_bg};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreUtils.getInstance().getBoolean("first", true)) {
            startActivity(GuideActivity.class);
            this.finish();
        }
    }

    /*****************rewrite method***************************/



    @Override
    protected void initWidget() {
        super.initWidget();

        setUpTabLayout();
    }

    private void setUpTabLayout() {
        mFragmentList = createTabFragments();
        mTitleList = createTabTitles();

        checkParamsIsRight();

        TabFragmentPageAdapter adapter = new TabFragmentPageAdapter(getSupportFragmentManager());
        mVp.setAdapter(adapter);
        mVp.setOffscreenPageLimit(4);
        mTlIndicator.setupWithViewPager(mVp);


        for (int i = 0; i < mTlIndicator.getTabCount(); i++) {
            Objects.requireNonNull(mTlIndicator.getTabAt(i)).setCustomView(adapter.getTabView(i));
        }
        mTlIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tab_title);
                textView.setTextColor(getResources().getColor(R.color.color_65c933));
                tab.getCustomView().findViewById(R.id.tab_image).setFocusable(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tab_title);
                textView.setTextColor(getResources().getColor(R.color.color_666666));

                tab.getCustomView().findViewById(R.id.tab_image).setFocusable(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mVp.setCurrentItem(0);
    }

    //隐藏底部导航栏
    public void hideTab() {
        mTlIndicator.setVisibility(View.GONE);
    }

    public void hideSoftInput() {
        View v = getCurrentFocus();
        if (v != null && v.getWindowToken() != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean isOpen = manager.isActive();
            if (isOpen) {
                manager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    //设置显示导航栏位置
    public void setTab(int position) {
        mVp.setCurrentItem(position);
    }


    //显示底部导航栏
    public void showTab() {
        mTlIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * 检查输入的参数是否正确。即Fragment和title是成对的。
     */
    private void checkParamsIsRight() {
        if (mFragmentList == null || mTitleList == null) {
            throw new IllegalArgumentException("fragmentList or titleList doesn't have null");
        }

        if (mFragmentList.size() != mTitleList.size())
            throw new IllegalArgumentException("fragment and title size must equal");
    }

    /******************inner class*****************/
    class TabFragmentPageAdapter extends FragmentPagerAdapter {

        public TabFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }

        public View getTabView(int position) {
            View v = LayoutInflater.from(BaseTabActivity.this).inflate(R.layout.tab_item_layout, null);
            TextView tv = (TextView) v.findViewById(R.id.tab_title);
            ImageView tab_image = (ImageView) v.findViewById(R.id.tab_image);
            tv.setText(mTitleList.get(position));
            tab_image.setImageDrawable(getResources().getDrawable(tab_icons[position]));
            return v;
        }

    }
}
