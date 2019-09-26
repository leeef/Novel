package com.bx.philosopher.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseMVPActivity;
import com.bx.philosopher.model.bean.response.LoginBean;
import com.bx.philosopher.presenter.LoginPresenter;
import com.bx.philosopher.presenter.imp.LoginImp;
import com.bx.philosopher.share.ThirdPartyLogin;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.LoadingUtil;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.utils.login.LoginUtil;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import butterknife.BindView;

import static com.bx.philosopher.ui.activity.RegisterActivity.RESETPASSWORD;

//登录或者注册页面
public class LoginActivity extends BaseMVPActivity<LoginPresenter> implements View.OnClickListener, LoginImp.View {
    @BindView(R.id.gray_line)
    LinearLayout gray_line;
    @BindView(R.id.login_button)
    LinearLayout login_button;
    @BindView(R.id.login_phone)
    EditText login_phone;
    @BindView(R.id.login_password)
    EditText login_password;
    @BindView(R.id.forget_password)
    TextView forget_password;
    @BindView(R.id.forget_password_container)
    LinearLayout forget_password_container;
    @BindView(R.id.other_login)
    FrameLayout other_login;
    @BindView(R.id.login_button_text)
    TextView login_button_text;
    @BindView(R.id.login_title)
    TextView login_title;
    @BindView(R.id.weibo)
    LinearLayout weibo;
    @BindView(R.id.facebook)
    LinearLayout facebook;
    @BindView(R.id.twitter)
    LinearLayout twitter;
    @BindView(R.id.linkedIn)
    LinearLayout linkedIn;
    @BindView(R.id.qq)
    LinearLayout qq;


    @Override
    protected int getContentId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @Override
    protected void initClick() {
        super.initClick();
        login_button.setOnClickListener(this);
        forget_password_container.setOnClickListener(this);
        weibo.setOnClickListener(this);
        facebook.setOnClickListener(this);
        twitter.setOnClickListener(this);
        linkedIn.setOnClickListener(this);
        qq.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.login_button:
                if (StringUtils.isEmpty(login_phone.getText())) {
                    ToastUtils.show("Cell phone number cannot be empty");
                    return;
                }
                if (StringUtils.isEmpty(login_password.getText())) {
                    ToastUtils.show("The password cannot be empty");
                    return;
                }
                LoadingUtil.show(this);
                mPresenter.login(login_phone.getText().toString(), login_password.getText().toString());
                break;
            case R.id.forget_password_container:
                RegisterActivity.startActivity(this, RESETPASSWORD);
                break;
            case R.id.weibo:
                new ThirdPartyLogin().ThirdLogin(this, this, SHARE_MEDIA.SINA);
                break;
            case R.id.facebook:
                new ThirdPartyLogin().ThirdLogin(this, this, SHARE_MEDIA.FACEBOOK);
                break;
            case R.id.twitter:
                new ThirdPartyLogin().ThirdLogin(this, this, SHARE_MEDIA.TWITTER);
                break;
            case R.id.linkedIn:
                new ThirdPartyLogin().ThirdLogin(this, this, SHARE_MEDIA.LINKEDIN);
                break;
            case R.id.qq:
                new ThirdPartyLogin().ThirdLogin(this, this, SHARE_MEDIA.QQ);
                break;
        }
    }

    @Override
    public void showError(String errMsg) {
        Log.i(Constant.TAG, errMsg);
        ToastUtils.show(errMsg);
        LoadingUtil.hide();
    }

    @Override
    public void complete() {
        //登陆成功回调
        MainActivity.startActivity(this, 3);
    }

    @Override
    protected LoginPresenter bindPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void loginSuc(LoginBean loginBean) {
        LoginUtil.setUserId(loginBean.getId());
        LoginUtil.setLogin(true);
        LoginUtil.setHead(loginBean.getHeadimg());
        LoginUtil.setPhone(loginBean.getPhone());
        LoginUtil.setName(loginBean.getNickname());
        LoginUtil.setUserAccount(loginBean.getBalance());
        //登陆成功回调
        MainActivity.startActivity(this, 3);
        this.finish();
    }
}
