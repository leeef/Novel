package com.bx.philosopher.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseActivity;
import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.SubscribeBean;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.ui.adapter.BookCatalogAdapter;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.utils.login.LoginUtil;

import java.util.List;

import butterknife.BindView;

public class BookCatalogActivity extends BaseActivity implements BaseContract.BaseView {


    @BindView(R.id.book_catalog_list)
    RecyclerView book_catalog_lis;
    @BindView(R.id.container)
    LinearLayout container;

    private BookCatalogAdapter bookCatalogAdapter;


    private int bookId;
    private String from_type;
    private String bookName;
    private boolean hadPay;

    private SubscribeBean subscribeBean;

    @Override
    protected int getContentId() {
        return R.layout.activity_book_catalog;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        bookId = getIntent().getIntExtra("bookId", -1);
        from_type = getIntent().getStringExtra("from_type");
        bookName = getIntent().getStringExtra("bookName");
    }

    public static void startActivity(Context context, int bookId, String from_type, String bookName) {
        Intent intent = new Intent(context, BookCatalogActivity.class);
        intent.putExtra("bookId", bookId);
        intent.putExtra("from_type", from_type);
        intent.putExtra("bookName", bookName);
        context.startActivity(intent);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        book_catalog_lis.setLayoutManager(new LinearLayoutManager(this));

        bookCatalogAdapter = new BookCatalogAdapter();
        book_catalog_lis.setAdapter(bookCatalogAdapter);
        getData();
        bookCatalogAdapter.setOnItemClickListener((view, pos) -> {
            if (from_type.equals(BookDetailActivity.TYPE_EXPLORE)) {
                if (hadPay) {
                    ReadingActivity.startActivity(BookCatalogActivity.this, bookId, from_type, bookName, bookCatalogAdapter.getItem(pos));
                } else {
                    ToastUtils.show("Please buy this book first");
                }
            } else {
                if (subscribeBean != null) {
                    if (subscribeBean.getVip_status() == 1) {
                        ToastUtils.show("Please buy this book first");
                    } else {
                        ReadingActivity.startActivity(BookCatalogActivity.this, bookId, from_type, bookName, bookCatalogAdapter.getItem(pos));

                    }
                }
            }
        });
    }

    private void getData() {
        if (bookId != -1) {
            switch (from_type) {
                case BookDetailActivity.TYPE_EXPLORE:
                    HttpUtil.getInstance().getRequestApi().getExploreBookDetailCatalog(LoginUtil.getUserId(),bookId)
                            .compose(RxScheduler.Obs_io_main())
                            .subscribe(new BaseObserver<BaseResponse<List<String>>>(this) {
                                @Override
                                public void onSuccess(BaseResponse<List<String>> o) {
                                    List<String> catalogs = o.getData().subList(2, o.getData().size() - 1);
                                    hadPay = o.getData().get(o.getData().size() - 1).equals("1");
                                    bookCatalogAdapter.addItems(catalogs);
                                }

                                @Override
                                public void onError(String msg) {

                                }
                            });
                    break;
                case BookDetailActivity.TYPE_LIBRARY:
                    getSubscribeInfo();
                    HttpUtil.getInstance().getRequestApi().getLibraryBookCatelog(bookId)
                            .compose(RxScheduler.Obs_io_main())
                            .subscribe(new BaseObserver<BaseResponse<List<String>>>(this) {
                                @Override
                                public void onSuccess(BaseResponse<List<String>> o) {
                                    List<String> catalogs = o.getData().subList(2, o.getData().size());
                                    bookCatalogAdapter.addItems(catalogs);
                                }

                                @Override
                                public void onError(String msg) {

                                }
                            });
                    break;
            }

        }
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void complete() {

    }


    private void getSubscribeInfo() {
        HttpUtil.getInstance().getRequestApi().getSubscribeInfo(LoginUtil.getUserId())
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<SubscribeBean>>(this) {
                    @Override
                    public void onSuccess(BaseResponse<SubscribeBean> o) {
                        subscribeBean = o.getData();
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }
}
