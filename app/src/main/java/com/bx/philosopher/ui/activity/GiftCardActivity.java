package com.bx.philosopher.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseActivity;
import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.baseinterface.ItemSelectIml;
import com.bx.philosopher.ui.adapter.GiftCardAdapter;
import com.bx.philosopher.utils.StringUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/22 16:54
 */
public class GiftCardActivity extends BaseActivity implements BaseContract.BaseView {

    @BindView(R.id.card_count_list)
    RecyclerView card_count_list;
    @BindView(R.id.card_cover)
    ImageView card_cover;


    GiftCardAdapter giftCardAdapter;
    private int[] covers = {R.drawable.card5, R.drawable.card10, R.drawable.card30, R.drawable.card50, R.drawable.card100, R.drawable.card};

    private int[] card_counts;
    private int selectIndex;

    @Override
    protected int getContentId() {
        return R.layout.activity_gift_card;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        card_counts = getResources().getIntArray(R.array.card_count);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ImmersionBar.with(this).statusBarColor(R.color.color_333333).init();
        card_count_list.setLayoutManager(new LinearLayoutManager(this));
        giftCardAdapter = new GiftCardAdapter(() -> selectIndex);
        card_count_list.setAdapter(giftCardAdapter);
        List<Integer> list = new ArrayList<>();
        for (int i : card_counts) {
            list.add(i);
        }
        giftCardAdapter.addItems(list);
        card_cover.setImageDrawable(StringUtils.getDrawable(covers[selectIndex]));
        giftCardAdapter.setOnItemClickListener((view, pos) -> {
            if (selectIndex == pos) {
                //跳转到购买页面
                MyRechargeActivity.startActivity(GiftCardActivity.this, "gift_card", String.format("%.2f", Float.parseFloat(card_counts[pos] + "")));
            } else {
                selectIndex = pos;
                giftCardAdapter.notifyDataSetChanged();
                card_cover.setImageDrawable(StringUtils.getDrawable(covers[pos]));
            }
        });
    }


    @Override
    public void showError(String msg) {

    }

    @Override
    public void complete() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
