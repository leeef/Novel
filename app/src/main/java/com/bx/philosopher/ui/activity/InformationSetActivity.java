package com.bx.philosopher.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.utils.login.LoginUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置昵称或更换绑定的手机号
 */
public class InformationSetActivity extends BaseActivity implements View.OnClickListener, BaseContract.BaseView {
    @BindView(R.id.info_title)
    TextView info_title;
    @BindView(R.id.set_name_container)
    LinearLayout set_name_container;
    @BindView(R.id.set_phone_container)
    LinearLayout set_phone_container;
    @BindView(R.id.set_phone_button)
    LinearLayout set_phone_button;
    @BindView(R.id.set_phone_text)
    EditText set_phone_text;
    @BindView(R.id.set_nick_name)
    EditText set_nick_name;
    @BindView(R.id.save)
    TextView save;


    public static final String SET_FLAG = "set_flag";
    public static final String SET_NICKNAME = "set_nick_name";//修改昵称
    public static final String SET_PHONE = "rebind";//修改手机号

    private String flag;//根据标识来判断具体修改什么 展示不同布局
    private String code;

    @Override
    protected int getContentId() {
        return R.layout.activity_information_set;
    }

    public static void startActivity(Context context, String flag, String code) {
        Intent intent = new Intent(context, InformationSetActivity.class);
        intent.putExtra(SET_FLAG, flag);
        intent.putExtra("code", code);
        context.startActivity(intent);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        flag = getIntent().getStringExtra(SET_FLAG);
        if (StringUtils.isTrimEmpty(flag)) flag = "";
        code = getIntent().getStringExtra("code");
        if (StringUtils.isTrimEmpty(code)) code = "";
    }

    @Override
    protected void initClick() {
        super.initClick();
        set_phone_button.setOnClickListener(this);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        switch (flag) {
            case SET_NICKNAME:
                info_title.setText(StringUtils.getString(R.string.modify_nickname));
                set_name_container.setVisibility(View.VISIBLE);
                set_phone_container.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
                break;
            case SET_PHONE:
                info_title.setText(StringUtils.getString(R.string.modify_nickname));
                set_name_container.setVisibility(View.GONE);
                set_phone_container.setVisibility(View.VISIBLE);
                break;
        }
    }

    @OnClick({R.id.save})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_phone_button://绑定新的手机号

                break;
            case R.id.save:
                if (flag.equals(SET_NICKNAME)) {
                    if (TextUtils.isEmpty(set_nick_name.getText().toString())) {
                        ToastUtils.show("Nicknames cannot be empty");
                        return;
                    }
                    LoadingUtil.show(this);
                    HttpUtil.getInstance().getRequestApi().updateName(LoginUtil.getUserId(), set_nick_name.getText().toString())
                            .compose(RxScheduler.Obs_io_main())
                            .subscribe(new BaseObserver<BaseResponse<Object>>(this) {
                                @Override
                                public void onSuccess(BaseResponse<Object> o) {
                                    showError(o.getMsg());
                                    LoginUtil.setName(set_nick_name.getText().toString());
                                    LoadingUtil.hide();
                                    InformationSetActivity.this.finish();
                                }

                                @Override
                                public void onError(String msg) {
                                    LoadingUtil.hide();
                                }
                            });
                }
                break;
        }
    }

    @Override
    public void showError(String msg) {
        ToastUtils.show(msg);
        LoadingUtil.hide();
    }

    @Override
    public void complete() {

    }
}
