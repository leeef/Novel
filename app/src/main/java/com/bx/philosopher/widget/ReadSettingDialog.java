package com.bx.philosopher.widget;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.ui.adapter.ReadBgAdapter;
import com.bx.philosopher.utils.BrightnessUtils;
import com.bx.philosopher.utils.ScreenUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.widget.page.PageLoader;
import com.bx.philosopher.widget.page.PageMode;
import com.bx.philosopher.widget.page.PageStyle;

import java.text.MessageFormat;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by newbiechen on 17-5-18.
 */

public class ReadSettingDialog extends Dialog {
    private static final String TAG = "ReadSettingDialog";

    @BindView(R.id.read_setting_sb_brightness)
    SeekBar mSbBrightness;
    @BindView(R.id.read_setting_cb_brightness_auto)
    CheckBox mCbBrightnessAuto;
    @BindView(R.id.read_setting_tv_font_minus)
    TextView mTvFontMinus;
    @BindView(R.id.read_setting_tv_font)
    TextView mTvFont;
    @BindView(R.id.read_setting_tv_font_plus)
    TextView mTvFontPlus;
    @BindView(R.id.read_format_1)
    LinearLayout read_format_1;
    @BindView(R.id.read_format_2)
    LinearLayout read_format_2;
    @BindView(R.id.read_format_3)
    LinearLayout read_format_3;


    LinearLayout read_setting_ll_menu;


    @BindView(R.id.read_setting_rv_bg)
    RecyclerView mRvBg;
    /************************************/
    private ReadBgAdapter mReadBgAdapter;
    private ReadSettingManager mSettingManager;
    private PageLoader mPageLoader;
    private Activity mActivity;

    private int mBrightness;
    private boolean isBrightnessAuto;
    private int mTextSize;
    private boolean isTextDefault;
    private PageMode mPageMode;
    private PageStyle mPageStyle;
    private int mReadBgTheme;
    private boolean isNightMode;


    public ReadSettingDialog(@NonNull Activity activity, PageLoader pageLoader) {
        super(activity, R.style.ReadSettingDialog);
        mActivity = activity;
        this.mPageLoader = pageLoader;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_read_setting);
        ButterKnife.bind(this);
        setUpWindow();
        initData();
        initWidget();
        initClick();
    }

    //设置Dialog显示的位置
    private void setUpWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        read_setting_ll_menu = findViewById(R.id.read_setting_ll_menu);
    }

    private void initData() {
        mSettingManager = ReadSettingManager.getInstance();

        isBrightnessAuto = mSettingManager.isBrightnessAuto();
        mBrightness = mSettingManager.getBrightness();
        mTextSize = mSettingManager.getTextSize();
        isTextDefault = mSettingManager.isDefaultTextSize();
        mReadBgTheme = mSettingManager.getPageStyle().getBgColor();


        mPageMode = mSettingManager.getPageMode();
        mPageStyle = mSettingManager.getPageStyle();
    }

    @Override
    public void show() {
        super.show();
        isNightMode = mSettingManager.isNightMode();
        if (isNightMode) {
            read_setting_ll_menu.setBackgroundColor(getContext().getResources().getColor(R.color.color_2c2c2d));
        } else {
            read_setting_ll_menu.setBackgroundColor(getContext().getResources().getColor(R.color.color_f4070707));
        }
    }


    private void initWidget() {
        mSbBrightness.setProgress(mBrightness);
        mTvFont.setText(mTextSize + "");
        mCbBrightnessAuto.setChecked(isBrightnessAuto);
        //RecyclerView
        setUpAdapter();
    }

    private void setUpAdapter() {
        Drawable[] drawables = {
                getDrawable(R.drawable.read_colour_1)
                , getDrawable(R.drawable.read_colour_2)
                , getDrawable(R.drawable.read_colour_3)
                , getDrawable(R.drawable.read_colour_4)
                , getDrawable(R.drawable.read_colour_5)};

        mReadBgAdapter = new ReadBgAdapter();
        mRvBg.setLayoutManager(new GridLayoutManager(getContext(), 5));
        mRvBg.setAdapter(mReadBgAdapter);
        mReadBgAdapter.refreshItems(Arrays.asList(drawables));

        //这里取巧了，直接将判断参数的值，传给了Recycler。如果以后要修改会造成大问题，所以不要学。
        mReadBgAdapter.setPageStyleChecked(mPageStyle);

    }


    private Drawable getDrawable(int drawRes) {
        return ContextCompat.getDrawable(getContext(), drawRes);
    }

    private void initClick() {

        mSbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (mCbBrightnessAuto.isChecked()) {
                    mCbBrightnessAuto.setChecked(false);
                }
                //设置当前 Activity 的亮度
                BrightnessUtils.setBrightness(mActivity, progress);
                //存储亮度的进度条
                ReadSettingManager.getInstance().setBrightness(progress);
            }
        });

        mCbBrightnessAuto.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {
                        //获取屏幕的亮度
                        BrightnessUtils.setBrightness(mActivity, BrightnessUtils.getScreenBrightness(mActivity));
                    } else {
                        //获取进度条的亮度
                        BrightnessUtils.setBrightness(mActivity, mSbBrightness.getProgress());
                    }
                    ReadSettingManager.getInstance().setAutoBrightness(isChecked);
                }
        );

        //字体大小调节
        mTvFontMinus.setOnClickListener(
                (v) -> {
                    int fontSize = Integer.valueOf(mTvFont.getText().toString()) - 1;
                    if (fontSize < ScreenUtils.spToPx(13)) {
                        ToastUtils.show("Is already the smallest font");
                    } else {
                        mTvFont.setText(MessageFormat.format("{0}", fontSize));
                        mPageLoader.setTextSize(fontSize);
                    }
                }
        );

        mTvFontPlus.setOnClickListener(
                (v) -> {
                    int fontSize = Integer.valueOf(mTvFont.getText().toString()) + 1;
                    if (fontSize > ScreenUtils.spToPx(20)) {
                        ToastUtils.show("Is already the largest font");
                    } else {
                        mTvFont.setText(MessageFormat.format("{0}", fontSize));
                        mPageLoader.setTextSize(fontSize);
                    }
                }
        );


        //背景的点击事件
        mReadBgAdapter.setOnItemClickListener(
                (view, pos) -> mPageLoader.setPageStyle(PageStyle.values()[pos])
        );

        read_format_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageLoader.setTextLineMargin(1);
            }
        });
        read_format_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageLoader.setTextLineMargin(2);
            }
        });
        read_format_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageLoader.setTextLineMargin(3);
            }
        });

    }


    public boolean isBrightFollowSystem() {
        if (mCbBrightnessAuto == null) {
            return false;
        }
        return mCbBrightnessAuto.isChecked();
    }
}
