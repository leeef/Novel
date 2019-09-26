package com.bx.philosopher.ui.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseActivity;
import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.SubscribeBean;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.utils.login.LoginUtil;

import butterknife.BindView;


public class MyAccountActivity extends BaseActivity implements View.OnClickListener, BaseContract.BaseView {
    @BindView(R.id.recharge)
    LinearLayout recharge;
    @BindView(R.id.record)
    LinearLayout record;
    @BindView(R.id.subscribe_state)
    TextView subscribe_state;
    @BindView(R.id.open_subscribe)
    LinearLayout open_subscribe;
    @BindView(R.id.account)
    TextView account;

    private SubscribeBean subscribeBean;

    @Override
    protected int getContentId() {
        return R.layout.activity_my_account;
    }

    @Override
    protected void initClick() {
        super.initClick();
        recharge.setOnClickListener(this);
        record.setOnClickListener(this);
        open_subscribe.setOnClickListener(this);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        account.setText("$ " + LoginUtil.getUserAccount());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recharge:
                startActivity(MyBalanceActivity.class);
                break;
            case R.id.record:
                startActivity(MyRecordActivity.class);
                break;
            case R.id.open_subscribe:
                startActivity(MySubscribeActivity.class);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        account.setText("$ " + LoginUtil.getUserAccount());
        getSubscribeInfo();
    }


    private void getSubscribeInfo() {
        HttpUtil.getInstance().getRequestApi().getSubscribeInfo(LoginUtil.getUserId())
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<SubscribeBean>>(this) {
                    @Override
                    public void onSuccess(BaseResponse<SubscribeBean> o) {
                        subscribeBean = o.getData();

                        if (subscribeBean.getVip_status() == 2) {//已订阅
                            subscribe_state.setText("Opened");
                        } else {
                            subscribe_state.setText(getResources().getString(R.string.not_opened));
                        }
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void complete() {

    }


}
