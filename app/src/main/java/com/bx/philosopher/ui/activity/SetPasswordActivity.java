package com.bx.philosopher.ui.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

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

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

//修改账号密码
public class SetPasswordActivity extends BaseActivity implements View.OnClickListener, BaseContract.BaseView {
    @BindView(R.id.old_password)
    EditText old_password;
    @BindView(R.id.new_password)
    EditText new_password;
    @BindView(R.id.new_password_again)
    EditText new_password_again;
    @BindView(R.id.replace)
    LinearLayout replace;

    @Override
    protected int getContentId() {
        return R.layout.activity_set_passwrod;
    }

    @Override
    protected void initClick() {
        super.initClick();
        replace.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.replace:
                if (StringUtils.isEmpty(old_password.getText())) {
                    ToastUtils.show("The original password must not be empty");
                    return;
                }
                if (StringUtils.isEmpty(new_password.getText())) {
                    ToastUtils.show("The new password must not be empty");
                    return;
                }
                if (StringUtils.isEmpty(new_password_again.getText())) {
                    ToastUtils.show("Verify that the new password must not be empty");
                    return;
                }
                if (!new_password.getText().toString().equals(new_password_again.getText().toString())) {
                    ToastUtils.show("The new password does not match the new password");
                    return;
                }
                LoadingUtil.show(this);
                Map<String, String> param = new HashMap<>();
                param.put("oldpwd", old_password.getText().toString());
                param.put("password", new_password.getText().toString());
                param.put("repwd", new_password_again.getText().toString());
                param.put("uid", LoginUtil.getUserIdStr());
                HttpUtil.getInstance().getRequestApi().updatePassword(param)
                        .compose(RxScheduler.Obs_io_main())
                        .subscribe(new BaseObserver<BaseResponse<Object>>(this) {
                            @Override
                            public void onSuccess(BaseResponse<Object> o) {
                                showError(o.getMsg());
                                LoadingUtil.hide();
                                SetPasswordActivity.this.finish();
                            }

                            @Override
                            public void onError(String msg) {
                                showError(msg);
                                LoadingUtil.hide();
                            }
                        });
                break;
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
