package com.bx.philosopher.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseMVPActivity;
import com.bx.philosopher.model.bean.response.RecordBean;
import com.bx.philosopher.presenter.MyRecordPresenter;
import com.bx.philosopher.presenter.imp.MyRecordImp;
import com.bx.philosopher.ui.adapter.MyRecordAdapter;
import com.bx.philosopher.utils.Constant;

import java.util.List;

import butterknife.BindView;

//我的花费记录
public class MyRecordActivity extends BaseMVPActivity<MyRecordPresenter> implements MyRecordImp.View {
    @BindView(R.id.my_account_list)
    RecyclerView my_account_list;
    @BindView(R.id.empty_view)
    RelativeLayout empty_view;
    @BindView(R.id.text_sign)
    TextView text_sign;

    private MyRecordAdapter accountAdapter;

    @Override
    protected int getContentId() {
        return R.layout.activity_my_record;
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        my_account_list.setLayoutManager(linearLayoutManager);
        accountAdapter = new MyRecordAdapter();
        my_account_list.setAdapter(accountAdapter);
        mPresenter.getData();

    }

    private void setEmpty_view() {
        my_account_list.setVisibility(View.GONE);
        empty_view.setVisibility(View.VISIBLE);
        text_sign.setText(getResources().getString(R.string.record_empty_sign));
    }

    @Override
    public void showError(String errMsg) {
        Log.i(Constant.TAG, errMsg);
    }

    @Override
    public void complete() {

    }

    @Override
    protected MyRecordPresenter bindPresenter() {
        return new MyRecordPresenter();
    }

    @Override
    public void showData(List<RecordBean> recordBeans) {
        accountAdapter.refreshItems(recordBeans);
        if (recordBeans == null || recordBeans.size() == 0) {
            setEmpty_view();
        }
    }
}
