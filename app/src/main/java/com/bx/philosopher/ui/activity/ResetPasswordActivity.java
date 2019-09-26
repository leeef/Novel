package com.bx.philosopher.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import butterknife.BindView;

/**
 * @ClassName: ResetPasswordActivity
 * @Description: 设置新密码页面
 * @Author: leeeef
 * @CreateDate: 2019/5/16 15:39
 * @Version: 1.0
 */
public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener, BaseContract.BaseView {

    @BindView(R.id.login_password_new)
    EditText login_password_new;
    @BindView(R.id.login_password)
    EditText login_password;
    @BindView(R.id.login_button)
    LinearLayout login_button;
    @BindView(R.id.login_button_text)
    TextView login_button_text;


    private String phone_number;
    private String flag;
    private String code;//验证码

    @Override
    protected int getContentId() {
        return R.layout.activity_reset_password;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        phone_number = getIntent().getStringExtra("phone_number");
        if (StringUtils.isEmpty(phone_number)) phone_number = "0";
        flag = getIntent().getStringExtra("flag");
        if (StringUtils.isTrimEmpty(flag)) flag = "";
        code = getIntent().getStringExtra("code");
        if (StringUtils.isTrimEmpty(flag)) code = "0";
    }

    @Override
    protected void initClick() {
        super.initClick();
        login_button.setOnClickListener(this);
    }

    public static void startActivity(Context context, String phone_number, String flag, String code) {
        Intent intent = new Intent(context, ResetPasswordActivity.class);
        intent.putExtra("flag", flag);
        intent.putExtra("code", code);
        intent.putExtra("phone_number", phone_number);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                if (StringUtils.isEmpty(login_password.getText())) {
                    ToastUtils.show("The new password cannot be empty");
                    return;
                }
                if (StringUtils.isEmpty(login_password_new.getText())) {
                    ToastUtils.show("Verify that the password cannot be empty");
                    return;
                }
                if (login_password_new.getText().toString().equals(login_password.getText().toString())) {
                    dealLogic();
                } else {
                    ToastUtils.show("Different passwords!");
                }
                break;
        }
    }

    //处理逻辑
    private void dealLogic() {
        switch (flag) {
            case RegisterActivity.REGISTER://注册
                LoadingUtil.show(this);
                HttpUtil.getInstance().getRequestApi().loginByCode(phone_number, code, login_password.getText().toString())
                        .compose(RxScheduler.Obs_io_main())
                        .subscribe(new BaseObserver<BaseResponse<Object>>(this) {
                            @Override
                            public void onSuccess(BaseResponse<Object> o) {
                                //注册成功 调到登录页面
                                showError(o.getMsg());
                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }

                            @Override
                            public void onError(String msg) {
                                showError(msg);
                            }
                        });
                break;
            case RegisterActivity.RESETPASSWORD://忘记密码
//                ToastUtils.show("Reset the success");
//                MainActivity.startActivity(this, 3);
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
