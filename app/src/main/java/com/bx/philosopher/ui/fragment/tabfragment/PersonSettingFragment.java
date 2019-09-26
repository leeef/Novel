package com.bx.philosopher.ui.fragment.tabfragment;


import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bx.philosopher.R;
import com.bx.philosopher.base.GlideApp;
import com.bx.philosopher.base.fragment.BaseMVPFragment;
import com.bx.philosopher.model.bean.response.PersonInfo;
import com.bx.philosopher.presenter.PersonalPresenter;
import com.bx.philosopher.presenter.imp.PersonalImp;
import com.bx.philosopher.ui.activity.IntroduceActivity;
import com.bx.philosopher.ui.activity.LoginActivity;
import com.bx.philosopher.ui.activity.MyAccountActivity;
import com.bx.philosopher.ui.activity.MyCardActivity;
import com.bx.philosopher.ui.activity.MyDownloadActivity;
import com.bx.philosopher.ui.activity.MyMedalActivity;
import com.bx.philosopher.ui.activity.MySubscribeActivity;
import com.bx.philosopher.ui.activity.PersonalInfoActivity;
import com.bx.philosopher.ui.activity.ProposalActivity;
import com.bx.philosopher.ui.activity.RegisterActivity;
import com.bx.philosopher.ui.activity.SettingActivity;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.utils.login.LoginUtil;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import butterknife.OnClick;

import static com.bx.philosopher.ui.activity.RegisterActivity.REGISTER;

//个人详情页面
public class PersonSettingFragment extends BaseMVPFragment<PersonalPresenter> implements View.OnClickListener, PersonalImp.View {

    @BindView(R.id.person_setting)
    ImageView person_setting;

    @BindView(R.id.person_setting_head)
    ImageView person_setting_head;

    @BindView(R.id.person_setting_name)
    TextView person_setting_name;
    @BindView(R.id.login_or_register)
    LinearLayout login_or_register;
    @BindView(R.id.register_text)
    TextView register_text;
    @BindView(R.id.login_text)
    TextView login_text;
    @BindView(R.id.introduce)
    RelativeLayout introduce;
    @BindView(R.id.proposal)
    RelativeLayout proposal;
    @BindView(R.id.my_download)
    RelativeLayout my_download;
    @BindView(R.id.my_account)
    LinearLayout my_account;
    @BindView(R.id.subscribe)
    LinearLayout subscribe;
    @BindView(R.id.my_card)
    RelativeLayout my_card;
    @BindView(R.id.balance)
    TextView balance;
    @BindView(R.id.subscribe_state)
    TextView subscribe_state;

    private PersonInfo personInfo;

    @Override
    protected PersonalPresenter bindPresenter() {
        return new PersonalPresenter();
    }

    @Override
    protected void initViewData(View v) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_person_setting;
    }

    @Override
    protected void onFirstUserVisible() {
        person_setting.setOnClickListener(this);
        person_setting_head.setOnClickListener(this);
        register_text.setOnClickListener(this);
        login_text.setOnClickListener(this);
        introduce.setOnClickListener(this);
        proposal.setOnClickListener(this);
        my_download.setOnClickListener(this);
        my_account.setOnClickListener(this);

        if (LoginUtil.isLogin()) {//判断是登录状态
            mPresenter.getData();
            login_or_register.setVisibility(View.GONE);
            person_setting_name.setVisibility(View.VISIBLE);
        } else {
            person_setting_head.setImageDrawable(null);
            person_setting_name.setVisibility(View.GONE);
            login_or_register.setVisibility(View.VISIBLE);
            balance.setText("0");
            subscribe_state.setText("0");
        }
    }


    @Override
    protected void onUserVisible() {
        if (LoginUtil.isLogin()) {//判断是登录状态
            mPresenter.getData();
            login_or_register.setVisibility(View.GONE);
            person_setting_name.setVisibility(View.VISIBLE);
        } else {
            person_setting_head.setImageDrawable(null);
            person_setting_name.setVisibility(View.GONE);
            login_or_register.setVisibility(View.VISIBLE);
            balance.setText("0");
            subscribe_state.setText("0");
        }

    }

    @Override
    protected void onFirstUserInvisible() {
    }

    @Override
    protected void onUserInvisible() {

    }


    @Override
    public boolean onBackPressed() {
        return false;
    }


    @Override
    protected void initData() {

    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this)
                .statusBarColor(R.color.black)
                .fitsSystemWindows(true)
                .init();
    }

    @OnClick({R.id.my_medal, R.id.my_card})
    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() != R.id.register_text) {
            if (!LoginUtil.isLogin()) {//未登录先跳到登录页面
                startActivity(new Intent(getContext(), LoginActivity.class));
                return;
            }
        }
        switch (v.getId()) {
            case R.id.person_setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.person_setting_head:
                startActivity(new Intent(getActivity(), PersonalInfoActivity.class));
                break;
            case R.id.register_text://注册
                RegisterActivity.startActivity(getContext(), REGISTER);
                break;
            case R.id.login_text://登录
                intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.introduce:
                startActivity(new Intent(getActivity(), IntroduceActivity.class));
                break;
            case R.id.proposal:
                startActivity(new Intent(getActivity(), ProposalActivity.class));
                break;
            case R.id.my_download:
                startActivity(new Intent(getActivity(), MyDownloadActivity.class));
                break;
            case R.id.my_account:
                startActivity(new Intent(getActivity(), MyAccountActivity.class));
                break;
            case R.id.subscribe:
                startActivity(new Intent(getActivity(), MySubscribeActivity.class));
                break;
            case R.id.my_medal:
                startActivity(new Intent(getActivity(), MyMedalActivity.class));
                break;
            case R.id.my_card:
                startActivity(new Intent(getActivity(), MyCardActivity.class));
                break;
        }
    }

    @Override
    public void showError(String errMsg) {
        Log.i(Constant.TAG, errMsg);
    }

    @Override
    public void complete() {

    }

    @Override
    public void showData(PersonInfo info) {
        personInfo = info;
        setPersonInfo();
    }

    private void setPersonInfo() {
        if (StringUtils.isNotEmpty(personInfo.getNickname())) {
            person_setting_name.setText(personInfo.getNickname());
            LoginUtil.setName(personInfo.getNickname());
        } else {
            person_setting_name.setText(R.string.app_name);
        }
        balance.setText(personInfo.getBalance());
        subscribe_state.setText(personInfo.getVip_status() == 2 ? "1" : "0");
        if (StringUtils.isNotEmpty(personInfo.getHeadimg())) {
            GlideApp.with(getContext()).load(personInfo.getHeadimg())
                    .error(R.drawable.appicon)
                    .apply(new RequestOptions().transform(new CircleCrop()))
                    .into(person_setting_head);
            LoginUtil.setHead(personInfo.getHeadimg());
        } else {
            GlideApp.with(getContext()).load(R.drawable.appicon)
                    .apply(new RequestOptions().transform(new CircleCrop()))
                    .into(person_setting_head);
        }
        if (StringUtils.isNotEmpty(personInfo.getPhone())) {
            LoginUtil.setPhone(personInfo.getPhone());
        }
        LoginUtil.setUserAccount(personInfo.getBalance());

    }
}
