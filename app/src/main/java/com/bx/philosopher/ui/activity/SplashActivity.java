package com.bx.philosopher.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bx.philosopher.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by newbiechen on 17-4-14.
 */

public class SplashActivity extends AppCompatActivity {
    private static final int WAIT_TIME = 3000;
    private static final int PERMISSIONS_REQUEST_STORAGE = 0;

    static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @BindView(R.id.splash_tv_skip)
    TextView mTvSkip;

    private Unbinder unbinder;
    private Runnable skipRunnable;

    private boolean isSkip = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        unbinder = ButterKnife.bind(this);

        skipRunnable = () -> skipToMain();

        //自动跳过
        mTvSkip.postDelayed(skipRunnable, WAIT_TIME);
        //点击跳过
        mTvSkip.setOnClickListener((view) -> skipToMain());
    }


    private synchronized void skipToMain() {
        if (!isSkip) {
            isSkip = true;
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE: {
/*                // 如果取消权限，则返回的值为0
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //申请权限成功

                } else {
                    //申请权限失败
                }*/

                //自动跳过
                mTvSkip.postDelayed(skipRunnable, WAIT_TIME);
                //点击跳过
                mTvSkip.setOnClickListener((view) -> skipToMain());
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSkip = true;
        mTvSkip.removeCallbacks(skipRunnable);
        unbinder.unbind();
    }
}
