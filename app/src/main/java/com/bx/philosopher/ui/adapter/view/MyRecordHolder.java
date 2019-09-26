package com.bx.philosopher.ui.adapter.view;

import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.adapter.ViewHolderImpl;
import com.bx.philosopher.model.bean.response.RecordBean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyRecordHolder extends ViewHolderImpl<RecordBean> {

    private TextView record_time;
    private TextView record_name;
    private TextView record_money;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected int getItemLayoutId() {
        return R.layout.my_record_item;
    }

    @Override
    public void initView() {
        record_time = findById(R.id.record_time);
        record_name = findById(R.id.record_name);
        record_money = findById(R.id.record_money);
    }

    @Override
    public void onBind(RecordBean data, int pos) {
        record_name.setText(data.getContent());
        record_time.setText(simpleDateFormat.format(new Date(Long.parseLong(data.getAddtime()) * 1000)));
        record_money.setText("$ " + data.getTotal());
    }
}
