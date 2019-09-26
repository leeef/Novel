package com.bx.philosopher.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseActivity;
import com.bx.philosopher.ui.fragment.tabfragment.CardFragment;
import com.bx.philosopher.utils.StringUtils;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @ClassName: MyCardActivity
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/12 9:08
 */
public class MyCardActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.card_tab)
    TabLayout card_tab;
    @BindView(R.id.card_viewpager)
    ViewPager card_viewpager;

    private List<Fragment> fragmentList = new ArrayList<>();
    private String[] tabTitles;

    @Override
    protected int getContentId() {
        return R.layout.activity_my_card;
    }

    private CardPagerAdapter cardPagerAdapter;

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        tabTitles = new String[2];
        tabTitles[0] = StringUtils.getString(R.string.my_card);
        tabTitles[1] = StringUtils.getString(R.string.gift_card);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
//        card_tab.addTab(card_tab.newTab().setText(R.string.my_card));
//        card_tab.addTab(card_tab.newTab().setText(R.string.gift_card));
        card_tab.setupWithViewPager(card_viewpager);
        for (int i = 0; i < 2; i++) {
            CardFragment cardFragment = new CardFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", (i + 1));
            cardFragment.setArguments(bundle);
            fragmentList.add(cardFragment);
        }
        cardPagerAdapter = new CardPagerAdapter(getSupportFragmentManager());
        card_viewpager.setAdapter(cardPagerAdapter);
    }


    @Override
    public void onClick(View v) {
    }

    class CardPagerAdapter extends FragmentPagerAdapter {

        public CardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
