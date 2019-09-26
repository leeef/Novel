package com.bx.philosopher.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseMVPActivity;
import com.bx.philosopher.model.bean.MedalBean;
import com.bx.philosopher.presenter.MyMedalPresenter;
import com.bx.philosopher.presenter.imp.MyMedalImp;
import com.bx.philosopher.ui.adapter.MyMedalAdapter;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.StringUtils;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MyMedalActivity extends BaseMVPActivity<MyMedalPresenter> implements MyMedalImp.View {

    @BindView(R.id.my_medal_list)
    RecyclerView my_medal_list;
    @BindView(R.id.my_medal_text)
    TextView my_medal_text;
    @BindView(R.id.medal_list_text)
    TextView medal_list_text;
    @BindView(R.id.back_image)
    ImageView back_image;

    private MyMedalAdapter myMedalAdapter;
    private List<MedalBean> data = new ArrayList<>();

    @Override
    protected int getContentId() {
        return R.layout.activity_my_medal;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        ImmersionBar.with(this).statusBarColor(R.color.color_65c933).init();
        back_image.setImageDrawable(StringUtils.getDrawable(R.drawable.arrow_left, R.color.white));
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        my_medal_list.setLayoutManager(new GridLayoutManager(this, 3));
        myMedalAdapter = new MyMedalAdapter();
        my_medal_list.setAdapter(myMedalAdapter);
        mPresenter.getData();
    }

    @Override
    protected void initClick() {
        super.initClick();
        myMedalAdapter.setOnItemClickListener((view, pos) -> {
            Intent intent = new Intent(MyMedalActivity.this, MedalDetailActivity.class);
            intent.putExtra("medal_id", myMedalAdapter.getItem(pos).getImage_id());
            startActivity(intent);
        });
    }

    @Override
    public void showError(String errMsg) {
        Log.i(Constant.TAG, errMsg);
    }

    @Override
    public void complete() {

    }

    @Override
    protected MyMedalPresenter bindPresenter() {
        return new MyMedalPresenter();
    }

    @Override
    public void showData(List<String> list) {
        my_medal_text.setText("My Medal: " + list.size());
        medal_list_text.setText("Special Medal (" + list.size() + "/11)");
        String[] medal_names = getResources().getStringArray(R.array.my_medal_name);
        int[] medal_id = {R.drawable.medal_1, R.drawable.medal_2, R.drawable.medal_3, R.drawable.medal_4,
                R.drawable.medal_5, R.drawable.medal_6, R.drawable.medal_7, R.drawable.medal_8,
                R.drawable.medal_9, R.drawable.medal_10, R.drawable.medal_11};
        int[] medal_gary_id = {R.drawable.medal_gay_1, R.drawable.medal_gay_2, R.drawable.medal_gay_3, R.drawable.medal_gay_4,
                R.drawable.medal_gay_5, R.drawable.medal_gay_6, R.drawable.medal_gay_7, R.drawable.medal_gay_8,
                R.drawable.medal_gay_9, R.drawable.medal_gay_10, R.drawable.medal_gay_11};
        for (int i = 0; i < medal_names.length; i++) {
            MedalBean medalBean = new MedalBean(0);
            if (list.indexOf(medal_names[i]) != -1) {
                medalBean.setImage_id(medal_id[i]);
            } else {
                medalBean.setImage_id(medal_gary_id[i]);
            }
            medalBean.setName(medal_names[i]);
            data.add(medalBean);
        }
        myMedalAdapter.refreshItems(data);
    }
}
