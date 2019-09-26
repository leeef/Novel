package com.bx.philosopher.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bx.philosopher.R;
import com.bx.philosopher.utils.ActivityManager;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by PC on 2016/9/8.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final int INVALID_VAL = -1;
    /**
     * 如果这个CompositeDisposable容器已经是处于dispose的状态，那么所有加进来的disposable都会被自动切断。防止内存泄漏
     */
    protected CompositeDisposable mDisposable;

    private Unbinder unbinder;

    private LinearLayout back;

    /****************************abstract area*************************************/

    @LayoutRes
    protected abstract int getContentId();

    /************************init area************************************/
    protected void addDisposable(Disposable d) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(d);
    }


    protected void initData(Bundle savedInstanceState) {
    }

    /**
     * 初始化零件
     */
    protected void initWidget() {

    }

    /**
     * 初始化点击事件
     */
    protected void initClick() {
    }

    /**
     * 逻辑使用区
     */
    protected void processLogic() {
    }

    protected void initPresenter() {

    }

    /*************************lifecycle area*****************************************************/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加Activity到堆栈
        ActivityManager.getInstance().addActivity(this);
        setContentView(getContentId());
        ImmersionBar.with(this)
                .statusBarColor(R.color.white)//状态栏背景
                .autoDarkModeEnable(true)//自动根据背景设置状态栏字体和图标颜色
                .fitsSystemWindows(true)//解决状态栏和布局重叠问题
                .init();
        unbinder = ButterKnife.bind(this);
        initPresenter();
        initData(savedInstanceState);
        initBack();
        initWidget();
        initClick();
        processLogic();

    }


    private void initBack() {
        back = findViewById(R.id.back);
        if (back != null) {
            back.setOnClickListener(v -> finish());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    /**************************used method area*******************************************/

    protected void startActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }


}
