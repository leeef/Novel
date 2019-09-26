package com.bx.philosopher.ui.activity;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseMVPActivity;
import com.bx.philosopher.presenter.SettingPresenter;
import com.bx.philosopher.presenter.imp.SettingImp;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.FileUtils;
import com.bx.philosopher.utils.LoadingUtil;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.utils.login.LoginUtil;
import com.bx.philosopher.widget.MyAlertDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseMVPActivity<SettingPresenter> implements View.OnClickListener, SettingImp.View {
    @BindView(R.id.change_password)
    RelativeLayout change_password;
    @BindView(R.id.check_update)
    RelativeLayout check_update;
    @BindView(R.id.clear_cache)
    RelativeLayout clear_cache;
    @BindView(R.id.sign_out)
    LinearLayout sign_out;
    @BindView(R.id.cache_size)
    TextView cache_size;

    @Override
    protected int getContentId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initClick() {
        super.initClick();
        sign_out.setOnClickListener(this);
        change_password.setOnClickListener(this);
        clear_cache.setOnClickListener(this);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        try {
            cache_size.setText(FileUtils.getTotalCacheSize());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.check_update})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_out:

                MyAlertDialog.create(this, "TIPS", "Confirm logout?", "confirm", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginUtil.logout();
                        SettingActivity.this.finish();
                    }
                }, "cancel", null, true, false, false).show();
                break;
            case R.id.change_password:
                startActivity(SetPasswordActivity.class);
                break;
            case R.id.clear_cache:
                MyAlertDialog.create(this, "TIPS", "Make sure to clear the cache", "confirm", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (FileUtils.clearAllCache())
                            cache_size.setText(StringUtils.getString(R.string._0_00mb));
                    }
                }, "cancel", null, true, false, false).show();

                break;
            case R.id.check_update:
                LoadingUtil.show(this);
                mPresenter.checkUpdate();
                break;
        }
    }

    @Override
    public void showError(String errMsg) {
        LoadingUtil.hide();
        ToastUtils.show(errMsg);
        Log.i(Constant.TAG, errMsg);
    }

    @Override
    public void complete() {

    }

    @Override
    protected SettingPresenter bindPresenter() {
        return new SettingPresenter();
    }
}
