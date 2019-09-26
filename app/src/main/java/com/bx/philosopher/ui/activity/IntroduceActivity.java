package com.bx.philosopher.ui.activity;

import android.widget.ImageView;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.GlideApp;
import com.bx.philosopher.base.activity.BaseActivity;
import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.IntroduceBean;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.widget.BreakTextView;

import butterknife.BindView;

//平台介绍
public class IntroduceActivity extends BaseActivity implements BaseContract.BaseView {

    @BindView(R.id.introduce_text)
    BreakTextView introduce_text;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.app_name)
    TextView app_name;

    @Override
    protected int getContentId() {
        return R.layout.activity_introduce;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        getData();
    }

    void getData() {
        HttpUtil.getInstance().getRequestApi().getIntroduce()
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<IntroduceBean>>(this) {
                    @Override
                    public void onSuccess(BaseResponse<IntroduceBean> o) {
                        IntroduceBean introduceBean = o.getData();
                        GlideApp.with(IntroduceActivity.this)
                                .load(Constant.CLIENT_URL + introduceBean.getCover())
                                .into(image);
                        app_name.setText(o.getData().getTitle());
                        introduce_text.setText(o.getData().getDetail());
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
