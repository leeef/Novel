package com.bx.philosopher.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bx.philosopher.R;
import com.bx.philosopher.base.GlideApp;
import com.bx.philosopher.base.activity.BaseActivity;
import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.SubscribeBean;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.utils.login.LoginUtil;
import com.google.gson.JsonElement;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

//我的订阅页面
public class MySubscribeActivity extends BaseActivity implements View.OnClickListener, BaseContract.BaseView {

    @BindView(R.id.subscribe_state)
    TextView subscribe_state;
    @BindView(R.id.nick_name)
    TextView nick_name;

    @BindView(R.id.head_image)
    ImageView head_image;

    public static final String SUBSCRIBE_STATE = "subscribe_state";

    private SubscribeBean subscribeBean;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected int getContentId() {
        return R.layout.activity_my_subscribe;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        getSubscribeInfo();
    }


    @OnClick({R.id.subscribe})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.subscribe:
                if (subscribeBean != null) {
                    if (subscribeBean.getVip_status() == 2) {
                        ToastUtils.show("Have subscribed to");
                    } else {
                        subscribe();
                    }
                }
                break;
        }
    }

    private void getSubscribeInfo() {
        HttpUtil.getInstance().getRequestApi().getSubscribeInfo(LoginUtil.getUserId())
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<SubscribeBean>>(this) {
                    @Override
                    public void onSuccess(BaseResponse<SubscribeBean> o) {
                        subscribeBean = o.getData();
                        if (subscribeBean.getHeadimg() != null) {
                            GlideApp.with(MySubscribeActivity.this)
                                    .load(subscribeBean.getHeadimg())
                                    .apply(new RequestOptions().transform(new CircleCrop()))
                                    .into(head_image);
                            nick_name.setText(subscribeBean.getNickname());
                        }
                        if (subscribeBean.getVip_status() == 2) {//已订阅
                            subscribe_state.setText("Effective Time : "
                                    + simpleDateFormat.format(new Date(Long.parseLong(subscribeBean.getVip_addtime()) * 1000)) + "-"
                                    + simpleDateFormat.format(new Date(Long.parseLong(subscribeBean.getVip_endtime()) * 1000)));
                        } else {
                            subscribe_state.setText("Effective Time : Not Opened");
                        }
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    private void subscribe() {
        HttpUtil.getInstance().getRequestApi().subscribe(LoginUtil.getUserId(), LoginUtil.getName(), "1")
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<JsonElement>>(this) {
                    @Override
                    public void onSuccess(BaseResponse<JsonElement> o) {
                        ToastUtils.show(o.getMsg());
                        getSubscribeInfo();
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
