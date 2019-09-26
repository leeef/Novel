package com.bx.philosopher.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;

import static com.bx.philosopher.ui.activity.RegisterActivity.REBIND;
import static com.bx.philosopher.ui.activity.RegisterActivity.REGISTER;
import static com.bx.philosopher.ui.activity.RegisterActivity.RESETPASSWORD;
import static com.bx.philosopher.utils.Constant.VERIFY_CODE_COUNT;

/**
 * @ClassName: VerificationCodeActivity
 * @Description: 验证码
 * @Author: leeeef
 * @CreateDate: 2019/5/16 15:13
 * @Version: 1.0
 */
public class VerificationCodeActivity extends BaseActivity implements View.OnClickListener, BaseContract.BaseView {
    @BindView(R.id.verified_input)
    EditText verified_input;

    @BindView(R.id.verified_one)
    TextView verified_one;
    @BindView(R.id.verified_two)
    TextView verified_two;
    @BindView(R.id.verified_three)
    TextView verified_three;
    @BindView(R.id.verified_four)
    TextView verified_four;
    @BindView(R.id.verified_phone)
    TextView verified_phone;
    @BindView(R.id.verified_time)
    TextView verified_time;
    private List<TextView> input_text = new ArrayList<>();

    private String phone_number;
    private String flag;//第一次登录验证码或者找回密码

    private String message_code;

    private Timer timer;

    private int wait_time = VERIFY_CODE_COUNT;//验证码重新发送时间


    @Override
    protected int getContentId() {
        return R.layout.activity_verification_code;
    }

    public static void startActivity(Activity context, String phone_number, String flag, String message_code) {
        Intent intent = new Intent(context, VerificationCodeActivity.class);
        intent.putExtra("flag", flag);
        intent.putExtra("phone_number", phone_number);
        intent.putExtra("message_code", message_code);
        if (flag.equals(REBIND))
            context.startActivityForResult(intent, 100);
        else
            context.startActivity(intent);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        phone_number = getIntent().getStringExtra("phone_number");
        message_code = getIntent().getStringExtra("message_code");
        if (StringUtils.isEmpty(phone_number)) phone_number = "0";
        flag = getIntent().getStringExtra("flag");
        if (StringUtils.isTrimEmpty(flag)) flag = "";
    }

    //倒数计时器
    public void startCounting() {
        Disposable subscribe = Observable.create((ObservableOnSubscribe<String>) e -> {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    e.onNext("-1s");
                }
            }, 1000, 1000);//1s后执行 之后每隔一秒执行一次
        }).compose(RxScheduler.Obs_io_main())
                .subscribe(s -> {
                    if (wait_time == 0) {
                        timer.cancel();
                        verified_time.setText(StringUtils.getString(R.string.remain));
                    } else {
                        wait_time--;
                        verified_time.setText(MessageFormat.format("{0}({1}s)", StringUtils.getString(R.string.remain), wait_time));
                    }
                });
        addDisposable(subscribe);
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        input_text.add(verified_one);
        input_text.add(verified_two);
        input_text.add(verified_three);
        input_text.add(verified_four);
        verified_phone.setText(MessageFormat.format("{0} {1}", StringUtils.getString(R.string.has_been_sent_to), phone_number));
        startCounting();
    }

    @Override
    protected void initClick() {
        super.initClick();
        verified_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtils.isTrimEmpty(s.toString())) {
                    setText(s.toString());
                    verified_input.setText("");
                }
            }
        });
        verified_input.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                deleteText();
                return true;
            }
            return false;
        });
        verified_time.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verified_time:
                if (wait_time == 0) {//倒数完毕，可以重新发送验证码
                    wait_time = VERIFY_CODE_COUNT;
                    startCounting();
                }
                break;
        }
    }

    //给每个textView赋值
    public void setText(String verify_code) {
        for (int i = 0; i < input_text.size(); i++) {
            if (StringUtils.isEmpty(input_text.get(i).getText())) {
                input_text.get(i).setText(verify_code);
                if (i == input_text.size() - 1) {
                    inputFinish();
                }
                break;
            }
        }
    }

    public String getCode() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TextView textView : input_text) {
            stringBuilder.append(textView.getText());
        }
        return stringBuilder.toString();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    //输入完成 根据标示处理逻辑
    private void inputFinish() {
        if (!message_code.equals(getCode())) {
            ToastUtils.show("Incorrect verification code input");
            return;
        }
        switch (flag) {
            case RESETPASSWORD://忘记密码
            case REGISTER://注册
                ResetPasswordActivity.startActivity(this, phone_number, flag, getCode());
                break;
            case REBIND://重新绑定手机号
                LoadingUtil.show(this);
                Map<String, String> param = new HashMap<>();
                param.put("phone", phone_number);
                param.put("uid", LoginUtil.getUserIdStr());
                param.put("code", getCode());
                HttpUtil.getInstance().getRequestApi().updatePhone(param)
                        .compose(RxScheduler.Obs_io_main())
                        .subscribe(new BaseObserver<BaseResponse<Object>>(this) {
                            @Override
                            public void onSuccess(BaseResponse<Object> o) {
                                LoginUtil.setPhone(phone_number);
                                LoadingUtil.hide();
                                showError(o.getMsg());
                                setResult(RESULT_OK);
                                VerificationCodeActivity.this.finish();
                            }

                            @Override
                            public void onError(String msg) {

                                LoadingUtil.hide();
                                showError(msg);
                            }
                        });
                break;
        }
    }

    //监听editText删除事件
    public void deleteText() {
        for (int i = input_text.size() - 1; i >= 0; i--) {
            if (!StringUtils.isEmpty(input_text.get(i).getText())) {
                input_text.get(i).setText("");
                break;
            }
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
