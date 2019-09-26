package com.bx.philosopher.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseMVPActivity;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.presenter.MyDownloadPresenter;
import com.bx.philosopher.presenter.imp.MyDownloadImp;
import com.bx.philosopher.ui.adapter.MyDownloadAdapter;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.FileUtils;
import com.bx.philosopher.widget.MyAlertDialog;
import com.bx.philosopher.widget.SlideRecyclerView;

import java.util.List;

import butterknife.BindView;

public class MyDownloadActivity extends BaseMVPActivity<MyDownloadPresenter> implements MyDownloadImp.View {
    @BindView(R.id.my_download_list)
    SlideRecyclerView my_download_list;

    @BindView(R.id.my_download)
    LinearLayout my_download;
    @BindView(R.id.empty_view)
    RelativeLayout empty_view;
    @BindView(R.id.text_sign)
    TextView text_sign;
    private MyDownloadAdapter myDownloadAdapter;


    @Override
    protected int getContentId() {
        return R.layout.activity_my_download;
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

    }

    private void setEmptyView() {
        my_download.setVisibility(View.GONE);
        empty_view.setVisibility(View.VISIBLE);
        text_sign.setText(getResources().getString(R.string.download_empty_sign));
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        myDownloadAdapter = new MyDownloadAdapter(position ->
                MyAlertDialog.create(MyDownloadActivity.this, "TIPS", "Make sure to delete!", "confirm", v -> {
                    my_download_list.closeMenu();
                    mPresenter.delete(myDownloadAdapter.getItem(position).getBid());
                    FileUtils.deleteBook(myDownloadAdapter.getItem(position).getBid() + "");//删除本地id
                }, "cancel", null, true, false, false).show());
        my_download_list.setLayoutManager(new LinearLayoutManager(this));
        my_download_list.setAdapter(myDownloadAdapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.shape_divider_row));
        my_download_list.addItemDecoration(itemDecoration);
        mPresenter.getData();

        myDownloadAdapter.setOnItemClickListener((view, pos) -> {
            BookBean bookBean = myDownloadAdapter.getItem(pos);
            String from_type = bookBean.getTypeid() == 1 ? BookDetailActivity.TYPE_EXPLORE : BookDetailActivity.TYPE_LIBRARY;
            ReadingActivity.startActivity(this, bookBean.getBid(), from_type, bookBean.getTitle());
        });

    }

    @Override
    protected MyDownloadPresenter bindPresenter() {
        return new MyDownloadPresenter();
    }

    @Override
    public void refresh(List<BookBean> data) {
        myDownloadAdapter.refreshItems(data);
        if (data.size() == 0) {
            setEmptyView();
        }
    }

    @Override
    public void showError(String errMsg) {
        Log.i(Constant.TAG, errMsg);
    }

    @Override
    public void complete() {

    }
}
