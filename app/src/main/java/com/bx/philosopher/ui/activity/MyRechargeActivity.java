package com.bx.philosopher.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseActivity;
import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.utils.LoadingUtil;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.utils.login.LoginUtil;
import com.google.gson.JsonElement;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;

public class MyRechargeActivity extends BaseActivity implements BaseContract.BaseView {

    @BindView(R.id.book_name)
    TextView book_name;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.book_price)
    TextView book_price;
    @BindView(R.id.recharge_amount)
    TextView recharge_amount;
    @BindView(R.id.payment)
    LinearLayout payment;


    private String bookName;
    private String price;
    private int bookId;
    private boolean card;

    @Override
    protected int getContentId() {
        return R.layout.activity_my_recharge;
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        bookName = getIntent().getStringExtra("book_name");
        price = getIntent().getStringExtra("price");
        bookId = getIntent().getIntExtra("bookId", -1);
        card = getIntent().getBooleanExtra("card", false);
    }

    //图书购买
    public static void startActivity(Context context, String bookName, String price, int bookId) {
        Intent intent = new Intent(context, MyRechargeActivity.class);
        intent.putExtra("book_name", bookName);
        intent.putExtra("price", price);
        intent.putExtra("bookId", bookId);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String card_name, String price) {
        Intent intent = new Intent(context, MyRechargeActivity.class);
        intent.putExtra("book_name", card_name);
        intent.putExtra("price", price);
        intent.putExtra("card", true);
        context.startActivity(intent);
    }

    @Override
    protected void initWidget() {
        book_name.setText(bookName);
        book_price.setText(MessageFormat.format("${0}", price));
        recharge_amount.setText(MessageFormat.format("${0}", price));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        time.setText(simpleDateFormat.format(new Date()));
        payment.setOnClickListener(v -> getData());
    }

    private void getData() {
        LoadingUtil.show(this);
        if (bookId != -1) {
            HttpUtil.getInstance().getRequestApi().buyBook(LoginUtil.getUserId(), bookId, price, LoginUtil.getName())
                    .compose(RxScheduler.Obs_io_main())
                    .subscribe(new BaseObserver<BaseResponse<JsonElement>>(this) {
                        @Override
                        public void onSuccess(BaseResponse<JsonElement> o) {
                            showError(o.getMsg());
                            MyRechargeActivity.this.finish();
                        }

                        @Override
                        public void onError(String msg) {
                            showError(msg);
                        }
                    });
        } else if (card) {
            HttpUtil.getInstance().getRequestApi().buyCard(LoginUtil.getUserId(), price)
                    .compose(RxScheduler.Obs_io_main())
                    .subscribe(new BaseObserver<BaseResponse<JsonElement>>(this) {
                        @Override
                        public void onSuccess(BaseResponse<JsonElement> o) {
                            showError(o.getMsg());
                            MyRechargeActivity.this.finish();
                        }

                        @Override
                        public void onError(String msg) {
                            showError(msg);

                        }
                    });
        }
    }

    @Override
    public void showError(String msg) {

        LoadingUtil.hide();
        ToastUtils.show(msg);
    }

    @Override
    public void complete() {

    }
}
