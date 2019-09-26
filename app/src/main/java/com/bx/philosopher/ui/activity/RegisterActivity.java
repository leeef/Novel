package com.bx.philosopher.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

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
import butterknife.OnClick;

/**
 * @ClassName: RegisterActivity
 * @Description: 手机号注册
 * @Author: leeeef
 * @CreateDate: 2019/5/16 15:14
 * @Version: 1.0
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener, BaseContract.BaseView {
    @BindView(R.id.login_phone)
    EditText login_phone;
    @BindView(R.id.country_code)
    TextView country_code;
    @BindView(R.id.login_title)
    TextView login_title;
    @BindView(R.id.login_button)
    LinearLayout login_button;


    public static final String REGISTER = "register";//注册
    public static final String RESETPASSWORD = "reset_password";//忘记密码
    public static final String REBIND = "rebind";//重新绑定手机号
    private String flag;//第一次登录验证码或者找回密码


    public static void startActivity(Context context, String flag) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.putExtra("flag", flag);
        context.startActivity(intent);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        flag = getIntent().getStringExtra("flag");
        if (StringUtils.isTrimEmpty(flag)) flag = "";
        if (flag.equals(REBIND)) {
            login_title.setText(StringUtils.getString(R.string.rebind));
        }else if(flag.equals(RESETPASSWORD)){
            login_title.setText(StringUtils.getString(R.string.reset_password));
        }
    }

    @Override
    protected void initClick() {
        super.initClick();
        login_button.setOnClickListener(this);
    }

    @OnClick({R.id.country_code})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                if (StringUtils.isEmpty(country_code.getText().toString())) {
                    ToastUtils.show("Country code cannot be empty");
                    return;
                }
                if (StringUtils.isEmpty(login_phone.getText().toString())) {
                    ToastUtils.show("Cell phone number cannot be empty");
                    return;
                }
                LoadingUtil.show(this);
                HttpUtil.getInstance().getRequestApi().getMessageCode(login_phone.getText().toString()
                        , country_code.getText().toString().substring(0, 3))
                        .compose(RxScheduler.Obs_io_main())
                        .subscribe(new BaseObserver<BaseResponse<String>>(this) {
                            @Override
                            public void onSuccess(BaseResponse<String> o) {
                                showError(o.getMsg());
                                VerificationCodeActivity.startActivity(RegisterActivity.this, login_phone.getText().toString(), flag,
                                        o.getData());
                            }

                            @Override
                            public void onError(String msg) {
                                showError(msg);
                            }
                        });
                break;
            case R.id.country_code:
                initDialog();
                break;
        }
    }

    private void initDialog() {
        String[] items = StringUtils.getStringArray(R.array.country_code);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, (dialog, which) -> {
            switch (which) {
                case 0:
                    country_code.setText(items[0]);
                    break;
                case 1:
                    country_code.setText(items[1]);
                    break;
                case 2:
                    country_code.setText(items[2]);
                    break;
                case 3:
                    country_code.setText(items[3]);
                    break;
            }
        });
        builder.create().show();
    }

    @Override
    public void showError(String msg) {
        ToastUtils.show(msg);
        LoadingUtil.hide();
    }

    @Override
    public void complete() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            this.finish();
        }
    }
}
