package com.bx.philosopher.ui.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseMVPActivity;
import com.bx.philosopher.model.bean.daobean.CollBookBean;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.BookMark;
import com.bx.philosopher.model.bean.response.SubscribeBean;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.ReadingPresenter;
import com.bx.philosopher.presenter.imp.ReadingImp;
import com.bx.philosopher.ui.adapter.BookCatalogAdapter;
import com.bx.philosopher.ui.adapter.BookMarkAdapter;
import com.bx.philosopher.ui.adapter.BookNoteAdapter;
import com.bx.philosopher.utils.BrightnessUtils;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.utils.login.LoginUtil;
import com.bx.philosopher.widget.PullToRefreshLayout;
import com.bx.philosopher.widget.ReadSettingDialog;
import com.bx.philosopher.widget.ReadSettingManager;
import com.bx.philosopher.widget.SlideRecyclerView;
import com.bx.philosopher.widget.page.PageLoader;
import com.bx.philosopher.widget.page.PageMode;
import com.bx.philosopher.widget.page.PageView;
import com.google.android.material.tabs.TabLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

import static android.view.View.VISIBLE;

public class ReadingActivity extends BaseMVPActivity<ReadingPresenter> implements View.OnClickListener, ReadingImp.View, EasyPermissions.PermissionCallbacks {
    private static final String TAG = "ReadActivity";

    @BindView(R.id.reading_top)
    RelativeLayout reading_top;
    @BindView(R.id.reading_top_container)
    RelativeLayout reading_top_container;
    @BindView(R.id.read_ll_bottom_menu)
    LinearLayout read_ll_bottom_menu;
    @BindView(R.id.book_read_drawer)
    DrawerLayout book_read_drawer;
    @BindView(R.id.read_pv_page)
    PageView mPvPage;
    @BindView(R.id.read_left_tab)
    TabLayout read_left_tab;
    @BindView(R.id.read_left_recycler)
    SlideRecyclerView read_left_recycler;
    @BindView(R.id.read_text_set)
    ImageView read_text_set;
    @BindView(R.id.read_tv_pre_chapter)
    TextView mTvPreChapter;
    @BindView(R.id.read_tv_next_chapter)
    TextView mTvNextChapter;
    @BindView(R.id.read_sb_chapter_progress)
    SeekBar read_sb_chapter_progress;
    @BindView(R.id.read_tv_night_mode)
    ImageView read_tv_night_mode;
    @BindView(R.id.page_refresh)
    PullToRefreshLayout page_refresh;
    @BindView(R.id.book_mark)
    ImageView book_mark;
    @BindView(R.id.back_image)
    ImageView back_image;
    @BindView(R.id.book_note)
    ImageView book_note;
    @BindView(R.id.back)
    LinearLayout back;
    @BindView(R.id.download)
    ImageView download;


    public static final String EXTRA_COLL_BOOK = "extra_coll_book";

    //注册 Brightness 的 uri
    private final Uri BRIGHTNESS_MODE_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE);
    private final Uri BRIGHTNESS_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
    private final Uri BRIGHTNESS_ADJ_URI =
            Settings.System.getUriFor("screen_auto_brightness_adj");
    //顶部菜单显示和隐藏的动画
    private Animation mTopInAnim;
    private Animation mTopOutAnim;

    private Animation mBottomInAnim;
    private Animation mBottomOutAnim;

    private ReadSettingDialog mSettingDialog;

    //控制屏幕常亮
    private PowerManager.WakeLock mWakeLock;
    private boolean isRegistered = false;

    private PageLoader mPageLoader;

    private BookCatalogAdapter bookCatalogAdapter;

    private BookNoteAdapter bookNoteAdapter;


    private BookMarkAdapter bookMarkAdapter;


    //是否是夜间模式
    private boolean isNightMode = false;

    private View headerView;//下拉书签布局
    private TextView sign_text;

    private int bookId;
    private String from_type;
    private String bookName;
    private String chapter;


    private Canvas mCanvas;
    private Paint mPaint;

    private boolean operateMark;

    private SubscribeBean subscribeBean;

    @Override
    protected int getContentId() {
        return R.layout.activity_reading;
    }

    //亮度调节监听
    //由于亮度调节没有 Broadcast 而是直接修改 ContentProvider 的。所以需要创建一个 Observer 来监听 ContentProvider 的变化情况。
    private ContentObserver mBrightObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange);

            //判断当前是否跟随屏幕亮度，如果不是则返回
            if (selfChange || !mSettingDialog.isBrightFollowSystem()) return;

            //如果系统亮度改变，则修改当前 Activity 亮度
            if (BRIGHTNESS_MODE_URI.equals(uri)) {
                Log.d(TAG, "亮度模式改变");
            } else if (BRIGHTNESS_URI.equals(uri) && !BrightnessUtils.isAutoBrightness(ReadingActivity.this)) {
                Log.d(TAG, "亮度模式为手动模式 值改变");
                BrightnessUtils.setBrightness(ReadingActivity.this, BrightnessUtils.getScreenBrightness(ReadingActivity.this));
            } else if (BRIGHTNESS_ADJ_URI.equals(uri) && BrightnessUtils.isAutoBrightness(ReadingActivity.this)) {
                Log.d(TAG, "亮度模式为自动模式 值改变");
                BrightnessUtils.setBrightness(ReadingActivity.this, BrightnessUtils.getScreenBrightness(ReadingActivity.this));
            } else {
                Log.d(TAG, "亮度调整 其他");
            }
        }
    };


    //注册亮度观察者
    private void registerBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (!isRegistered) {
                    final ContentResolver cr = getContentResolver();
                    cr.unregisterContentObserver(mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_MODE_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_URI, false, mBrightObserver);
                    cr.registerContentObserver(BRIGHTNESS_ADJ_URI, false, mBrightObserver);
                    isRegistered = true;
                }
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "[ouyangyj] register mBrightObserver error! " + throwable);
        }
    }

    //解注册
    private void unregisterBrightObserver() {
        try {
            if (mBrightObserver != null) {
                if (isRegistered) {
                    getContentResolver().unregisterContentObserver(mBrightObserver);
                    isRegistered = false;
                }
            }
        } catch (Throwable throwable) {
            Log.e(TAG, "unregister BrightnessObserver error! " + throwable);
        }
    }


    //暂无
    public static void startActivity(Context context, CollBookBean collBookBean) {
        Intent intent = new Intent(context, ReadingActivity.class);
        intent.putExtra(EXTRA_COLL_BOOK, collBookBean);
        context.startActivity(intent);
    }

    //
    public static void startActivity(Context context, int bookId, String from_type, String bookName) {
        Intent intent = new Intent(context, ReadingActivity.class);
        intent.putExtra("bookId", bookId);
        intent.putExtra("from_type", from_type);
        intent.putExtra("book_name", bookName);
        context.startActivity(intent);
    }


    //图书目录跳转
    public static void startActivity(Context context, int bookId, String from_type, String bookName, String chapter) {
        Intent intent = new Intent(context, ReadingActivity.class);
        intent.putExtra("bookId", bookId);
        intent.putExtra("from_type", from_type);
        intent.putExtra("book_name", bookName);
        intent.putExtra("chapter", chapter);
        context.startActivity(intent);
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        isNightMode = ReadSettingManager.getInstance().isNightMode();
        bookId = getIntent().getIntExtra("bookId", -1);
        from_type = getIntent().getStringExtra("from_type");
        bookName = getIntent().getStringExtra("book_name");
        chapter = getIntent().getStringExtra("chapter");

        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in);
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out);

        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
        //退出的速度要快
        mTopOutAnim.setDuration(100);

        setScreenLight();


        bookCatalogAdapter = new BookCatalogAdapter();

        bookNoteAdapter = new BookNoteAdapter(position -> {
            if (bookNoteAdapter.getItem(position) instanceof BookMark) {
                BookMark bookMark = (BookMark) bookNoteAdapter.getItem(position);
                mPresenter.deleteBookNote(bookMark.getId());
            }

        });
    }

    private void setScreenLight() {
        //设置当前Activity的Brightness
        if (ReadSettingManager.getInstance().isBrightnessAuto()) {
            BrightnessUtils.setBrightness(this, BrightnessUtils.getScreenBrightness(this));
        } else {
            BrightnessUtils.setBrightness(this, ReadSettingManager.getInstance().getBrightness());
        }
        //初始化屏幕常亮类
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "myapp:mywakelocktag");
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //重置基类的设置
        ImmersionBar.with(this).reset().init();
        //隐藏状态栏
        ImmersionBar.hideStatusBar(getWindow());

        //状态栏和标题栏会有重叠 设置标题栏marginTop
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = ImmersionBar.getStatusBarHeight(this);
        reading_top.setLayoutParams(layoutParams);
        page_refresh.setPageView(mPvPage);
        headerView = page_refresh.setHeader(R.layout.pull_add_sign);
        //获取页面加载器
        mPageLoader = mPvPage.getPageLoader(bookId);
        mPvPage.setPullToRefreshView(page_refresh);

        mSettingDialog = new ReadSettingDialog(this, mPageLoader);
        initLeftTab();
        toggleNightMode();
        mPageLoader.setFrom_type(from_type);
        mPageLoader.setPageMode(PageMode.SIMULATION);

        sign_text = headerView.findViewById(R.id.sign_text);
        back_image.setColorFilter(StringUtils.getColor(R.color.white));//设置返回键颜色为白色
        mPageLoader.setBookName(bookName);

    }

    private void toggleNightMode() {
        if (isNightMode) {
            read_tv_night_mode.setImageDrawable(getResources().getDrawable(R.drawable.read_day));
            reading_top_container.setBackgroundColor(getResources().getColor(R.color.color_2c2c2d));
            back.setBackground(StringUtils.getDrawable(R.drawable.simple_item_click_bg_2c2c2d));
            read_ll_bottom_menu.setBackgroundColor(getResources().getColor(R.color.color_2c2c2d));
            reading_top.setBackgroundColor(getResources().getColor(R.color.color_2c2c2d));
            download.setBackgroundColor(getResources().getColor(R.color.color_2c2c2d));
        } else {
            read_tv_night_mode.setImageDrawable(getResources().getDrawable(R.drawable.read_night));
            reading_top_container.setBackgroundColor(getResources().getColor(R.color.color_f4070707));
            read_ll_bottom_menu.setBackgroundColor(getResources().getColor(R.color.color_f4070707));
            reading_top.setBackgroundColor(getResources().getColor(R.color.color_f4070707));
            back.setBackground(StringUtils.getDrawable(R.drawable.simple_item_click_bg_black));
            download.setBackground(StringUtils.getDrawable(R.drawable.simple_item_click_bg_black));
        }
    }

    //初始化侧边栏
    private void initLeftTab() {
        read_left_recycler.setLayoutManager(new LinearLayoutManager(this));
        read_left_tab.addTab(read_left_tab.newTab().setIcon(getResources().getDrawable(R.drawable.tab_catalog_bg)));
        read_left_tab.addTab(read_left_tab.newTab().setIcon(getResources().getDrawable(R.drawable.tab_note_bg)));
        read_left_tab.addTab(read_left_tab.newTab().setIcon(getResources().getDrawable(R.drawable.tab_sign_bg)));

        bookMarkAdapter = new BookMarkAdapter();

        read_left_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        read_left_recycler.setAdapter(bookCatalogAdapter);
                        read_left_recycler.setCanSlideLeft(false);
                        break;
                    case 1:
                        read_left_recycler.setAdapter(bookNoteAdapter);
                        read_left_recycler.setCanSlideLeft(true);
                        break;
                    case 2:
                        read_left_recycler.setCanSlideLeft(false);
                        read_left_recycler.setAdapter(bookMarkAdapter);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        read_left_recycler.setAdapter(bookCatalogAdapter);
        read_left_recycler.setCanSlideLeft(false);
        bookNoteAdapter.setOnItemClickListener((view, pos) -> {
            if (from_type.equals(BookDetailActivity.TYPE_EXPLORE)) {
                if (mPageLoader.isHadPay()) {
                    if (bookNoteAdapter.getItem(pos) instanceof BookMark) {
                        BookMark bookMark = (BookMark) bookNoteAdapter.getItem(pos);

                        mPageLoader.skipToPage(bookCatalogAdapter.getItems().indexOf(bookMark.getChapter()), Integer.parseInt(bookMark.getStartsite()));
                        book_read_drawer.closeDrawer(Gravity.LEFT);
                    }
                } else {
                    ToastUtils.show("Please buy this book first");
                }
            } else {
                if (mPageLoader.isSubscribe()) {
                    if (bookNoteAdapter.getItem(pos) instanceof BookMark) {
                        BookMark bookMark = (BookMark) bookNoteAdapter.getItem(pos);

                        mPageLoader.skipToPage(bookCatalogAdapter.getItems().indexOf(bookMark.getChapter()), Integer.parseInt(bookMark.getStartsite()));
                        book_read_drawer.closeDrawer(Gravity.LEFT);
                    }
                } else {
                    ToastUtils.show("Please subscribe to");
                }
            }

        });

        bookMarkAdapter.setOnItemClickListener((view, pos) -> {
            if (from_type.equals(BookDetailActivity.TYPE_EXPLORE)) {
                if (mPageLoader.isHadPay()) {
                    if (bookMarkAdapter.getItem(pos) instanceof BookMark) {
                        BookMark bookMark = (BookMark) bookMarkAdapter.getItem(pos);

                        mPageLoader.skipToPage(bookCatalogAdapter.getItems().indexOf(bookMark.getChapter()), Integer.parseInt(bookMark.getSite()));
                        book_read_drawer.closeDrawer(Gravity.LEFT);
                    }
                } else {
                    ToastUtils.show("Please buy this book first");
                }
            } else {
                if (mPageLoader.isSubscribe()) {
                    if (bookMarkAdapter.getItem(pos) instanceof BookMark) {
                        BookMark bookMark = (BookMark) bookMarkAdapter.getItem(pos);

                        mPageLoader.skipToPage(bookCatalogAdapter.getItems().indexOf(bookMark.getChapter()), Integer.parseInt(bookMark.getSite()));
                        book_read_drawer.closeDrawer(Gravity.LEFT);
                    }
                } else {
                    ToastUtils.show("Please subscribe to");
                }
            }


        });


    }


    @Override
    protected void initClick() {
        super.initClick();
        mPvPage.setTouchListener(new PageView.TouchListener() {
            @Override
            public void center() {
                setClickMenu();
            }

            @Override
            public boolean onTouch() {
                return !hideReadMenu();
            }

            @Override
            public void prePage() {
            }

            @Override
            public void nextPage() {
            }

            @Override
            public void cancel() {
            }

            @Override
            public void longClick() {
            }

            @Override
            public void onTouchEvent(MotionEvent motionEvent) {
                page_refresh.onTouchEvent(motionEvent);
            }

        });


        mPageLoader.setOnPageChangeListener(
                new PageLoader.OnPageChangeListener() {

                    @Override
                    public void onChapterChange(int pos) {
                        bookCatalogAdapter.setCurrentChapter(pos);
                    }

                    @Override
                    public void requestChapters(List<String> requestChapters) {
                        mPresenter.loadChapter(bookId, requestChapters, from_type);
                    }

                    @Override
                    public void onCategoryFinish(List<String> chapters) {
                        bookCatalogAdapter.refreshItems(chapters);
                    }

                    @Override
                    public void onPageCountChange(int count) {
                        read_sb_chapter_progress.setMax(Math.max(0, count - 1));
                        read_sb_chapter_progress.setProgress(0);
                        // 如果处于错误状态，那么就冻结使用
                        if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING
                                || mPageLoader.getPageStatus() == PageLoader.STATUS_ERROR) {
                            read_sb_chapter_progress.setEnabled(false);
                        } else {
                            read_sb_chapter_progress.setEnabled(true);
                        }
                    }

                    @Override
                    public void onPageChange(int pos) {
                        read_sb_chapter_progress.post(
                                () -> read_sb_chapter_progress.setProgress(pos)
                        );
                    }

                    @Override
                    public void onPageChange(int chapter, int pos) {
                        if (hasMark(chapter, pos)) {
                            book_mark.setVisibility(VISIBLE);
                        } else {
                            book_mark.setVisibility(View.GONE);
                        }
                        hasNote(chapter, pos);
                    }

                    @Override
                    public void onSetUnderLine(int startSite, int endSite, String content, String note) {
                        mPresenter.addBookNote(bookId, bookCatalogAdapter.getItem(bookCatalogAdapter.getCurrentChapter()),
                                startSite, endSite, content, note);
                    }

                    @Override
                    public void setUnderLine(Canvas canvas, Paint paint) {
                        mCanvas = canvas;
                        mPaint = paint;
                    }
                }
        );

        read_sb_chapter_progress.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (read_ll_bottom_menu.getVisibility() == VISIBLE) {
                            //显示标题
//                            mTvPageTip.setText((progress + 1) + "/" + (read_sb_chapter_progress.getMax() + 1));
//                            mTvPageTip.setVisibility(VISIBLE);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //进行切换
                        int pagePos = read_sb_chapter_progress.getProgress();
                        if (pagePos != mPageLoader.getPagePos()) {
                            mPageLoader.skipToPage(pagePos);
                        }
                        //隐藏提示
//                        mTvPageTip.setVisibility(GONE);
                    }
                }
        );

        bookCatalogAdapter.setOnItemClickListener((view, pos) -> {
            if (from_type.equals(BookDetailActivity.TYPE_EXPLORE)) {
                if (mPageLoader.isHadPay()) {
                    book_read_drawer.closeDrawer(Gravity.LEFT);
                    mPageLoader.skipToChapter(pos);
                } else {
                    ToastUtils.show("Please buy this book first");
                }
            } else {
                if (mPageLoader.isSubscribe()) {
                    book_read_drawer.closeDrawer(Gravity.LEFT);
                    mPageLoader.skipToChapter(pos);
                } else {
                    ToastUtils.show("Please subscribe to");
                }

            }
        });


        page_refresh.setListener(new PullToRefreshLayout.Listener() {

            @Override
            public void onPre() {
                if (hasMark()) {
                    sign_text.setText("Drop down to remove bookmarks");
                } else {
                    sign_text.setText("Drop down to add bookmarks");
                }
            }

            @Override
            public void onChange() {
                if (hasMark()) {
                    sign_text.setText("Let go and delete the bookmark");
                } else {
                    sign_text.setText("Let go and bookmark");
                }
                operateMark = true;//到此处松手就会删除或添加书签
            }

            @Override
            public void onRecover() {
                if (hasMark()) {
                    sign_text.setText("Drop down to remove bookmarks");
                } else {
                    sign_text.setText("Drop down to add bookmarks");
                }
                operateMark = false;//到此处说明下拉幅度小或者手动又上滑上去
            }

            @Override
            public void onRefresh() {
                if (operateMark) {
                    if (hasMark()) {
                        mPresenter.deleteBookMark(getMark());
                    } else {
                        int site = mPageLoader.getPageStartPosition();
                        mPresenter.addBookMark(bookId, mPageLoader.getChapter(), site, mPageLoader.getCurrentPage());
                    }
                    operateMark = false;
                }
            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onPull(float rate) {

            }
        });


        //关闭手势滑动
        book_read_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }


    @OnClick({R.id.book_menu_set, R.id.book_read_drawer, R.id.read_text_set, R.id.read_tv_pre_chapter, R.id.read_tv_next_chapter,
            R.id.read_tv_night_mode, R.id.book_note, R.id.download})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.book_menu_set:
                setClickMenu();
                book_read_drawer.openDrawer(Gravity.LEFT);
                read_left_recycler.scrollToPosition(mPageLoader.getChapterPos());
                break;
            case R.id.read_text_set:
                setClickMenu();
                mSettingDialog.show();
                break;
            case R.id.read_tv_pre_chapter:
                mPageLoader.skipToPrePage();
                break;
            case R.id.read_tv_next_chapter:
                mPageLoader.skipToNextPage();
                break;
            case R.id.read_tv_night_mode:
                if (isNightMode) {
                    isNightMode = false;
                } else {
                    isNightMode = true;
                }
                mPageLoader.setNightMode(isNightMode);
                toggleNightMode();
                break;
            case R.id.book_note:
                NoteEditActivity.startActivity(this, mPageLoader.getCurrentPage(), mPageLoader.getPageStartPosition(), mPageLoader.getPageEndPosition(),
                        bookId, mPageLoader.getChapter());
                break;
            case R.id.download:
                if (from_type.equals(BookDetailActivity.TYPE_EXPLORE)) {
                    if (mPageLoader.isHadPay()) {
                        ToastUtils.show("Has been downloaded in the background");
                        mPresenter.downLoadBook(bookId, from_type, this);
                    } else {
                        ToastUtils.show("Please buy this book first");
                    }
                } else {
                    if (mPageLoader.isSubscribe()) {
                        ToastUtils.show("Has been downloaded in the background");
                        mPresenter.downLoadBook(bookId, from_type, this);
                    } else {
                        ToastUtils.show("Please subscribe to");
                    }
                }


                break;
        }
    }

    private boolean hasMark() {
        for (BaseBean baseBean : bookMarkAdapter.getItems()) {
            if (baseBean instanceof BookMark) {
                BookMark bookMark = (BookMark) baseBean;
                int chapterPosition = bookCatalogAdapter.getItems().indexOf(bookMark.getChapter());
                if (chapterPosition == mPageLoader.getChapterPos()) {
                    int site = Integer.parseInt(bookMark.getSite());
                    if (mPageLoader.getPageStartPosition() <= site && site < mPageLoader.getPageEndPosition()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasMark(int chapter, int pos) {
        for (BaseBean baseBean : bookMarkAdapter.getItems()) {
            if (baseBean instanceof BookMark) {
                BookMark bookMark = (BookMark) baseBean;
                int chapterPosition = bookCatalogAdapter.getItems().indexOf(bookMark.getChapter());
                if (chapterPosition == chapter) {
                    int site = Integer.parseInt(bookMark.getSite());
                    if (mPageLoader.getPageStartPosition() <= site && site < mPageLoader.getPageEndPosition()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void hasNote(int chapter, int pos) {
        List<BookMark> bookMarks = new ArrayList<>();
        for (BaseBean baseBean : bookNoteAdapter.getItems()) {
            if (baseBean instanceof BookMark) {
                BookMark bookMark = (BookMark) baseBean;
                int chapterPosition = bookCatalogAdapter.getItems().indexOf(bookMark.getChapter());
                if (chapterPosition == chapter) {
                    int startSite = Integer.parseInt(bookMark.getStartsite());
                    int endSite = Integer.parseInt(bookMark.getEndsite());
                    if (mPageLoader.getPageStartPosition(pos) <= startSite && endSite <= mPageLoader.getPageEndPosition(pos)) {
                        bookMarks.add(bookMark);
                    }
                }
            }
        }
        mPageLoader.setSelectTxt(bookMarks, mCanvas, mPaint);

    }

    private List<Integer> getMark() {
        List<Integer> marks = new ArrayList<>();
        for (BaseBean baseBean : bookMarkAdapter.getItems()) {
            if (baseBean instanceof BookMark) {
                BookMark bookMark = (BookMark) baseBean;
                int chapterPosition = bookCatalogAdapter.getItems().indexOf(bookMark.getChapter());
                if (chapterPosition == mPageLoader.getChapterPos()) {
                    int site = Integer.parseInt(bookMark.getSite());
                    if (mPageLoader.getPageStartPosition() <= site && site < mPageLoader.getPageEndPosition()) {
                        marks.add(bookMark.getId());
                    }
                }
            }
        }
        return marks;
    }


    @Override
    protected ReadingPresenter bindPresenter() {
        return new ReadingPresenter();
    }

    @Override
    protected void processLogic() {
        super.processLogic();

        mPresenter.loadAutoMark(bookId);
        if (from_type.equals(BookDetailActivity.TYPE_LIBRARY)) {
            getSubscribeInfo();
        }
        mPresenter.loadBook(bookId, from_type);
        mPresenter.loadBookMark(bookId);
        mPresenter.loadBookNote(bookId);
    }

    //设置菜单的显示和隐藏
    private void setClickMenu() {
        if (reading_top_container.getVisibility() == View.VISIBLE) {
            reading_top_container.startAnimation(mTopOutAnim);
            reading_top_container.setVisibility(View.GONE);
            read_ll_bottom_menu.startAnimation(mBottomOutAnim);
            read_ll_bottom_menu.setVisibility(View.GONE);
            ImmersionBar.hideStatusBar(getWindow());
        } else {
            reading_top_container.startAnimation(mTopInAnim);
            reading_top_container.setVisibility(View.VISIBLE);

            read_ll_bottom_menu.startAnimation(mBottomInAnim);
            read_ll_bottom_menu.setVisibility(View.VISIBLE);
            ImmersionBar.showStatusBar(getWindow());
        }
    }


    /**
     * 隐藏阅读界面的菜单显示
     *
     * @return 是否隐藏成功
     */
    private boolean hideReadMenu() {
        if (reading_top_container.getVisibility() == VISIBLE) {
            setClickMenu();
            return true;
        } else if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
            return true;
        }
        return false;
    }


    @Override
    protected void onStart() {
        super.onStart();
        registerBrightObserver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPresenter.loadBookNote(bookId);
        mWakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            //自动保存书签 有可能书籍id不存在
            mPresenter.addAutoBookMark(bookId, bookCatalogAdapter.getItem(bookCatalogAdapter.getCurrentChapter())
                    , mPageLoader.getMarkRecord(), mPageLoader.getCurrentPage());
        } catch (Exception e) {
            Log.i(getClass().getName(), e.getLocalizedMessage());
        }

        mWakeLock.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBrightObserver();
    }

    @Override
    public void showError(String errMsg) {
        Log.i(Constant.TAG, errMsg);
    }

    @Override
    public void complete() {

    }

    @Override
    public void showCategory(List<String> bookChapterList) {
        if (chapter != null) {
            mPageLoader.setChapterPos(bookChapterList.indexOf(chapter));
            if (mPageLoader.getBookAutoMark() != null) {

                mPageLoader.getBookAutoMark().setChapter(chapter);
                mPageLoader.getBookAutoMark().setSite("0");
            }
        } else if (mPageLoader.getBookAutoMark() != null) {
            mPageLoader.setChapterPos(bookChapterList.indexOf(mPageLoader.getBookAutoMark().getChapter()));
        }
        mPageLoader.refreshChapterList(bookChapterList);

    }

    @Override
    public void finishChapter() {
        runOnUiThread(() -> {
            if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
                mPageLoader.openChapter();
            }
        });

    }


    @Override
    public void errorChapter() {
        runOnUiThread(() -> {
            if (mPageLoader.getPageStatus() == PageLoader.STATUS_LOADING) {
                mPageLoader.chapterError();
            }
        });

    }

    @Override
    public void showMark(List<BaseBean> bookMark) {
        bookMarkAdapter.refreshItems(bookMark);
    }

    @Override
    public void addMarkFinish() {

        book_mark.setVisibility(VISIBLE);
        mPresenter.loadBookMark(bookId);
    }

    @Override
    public void addNoteFinish() {
        mPresenter.loadBookNote(bookId);
    }

    @Override
    public void deleteMarkFinish() {
        book_mark.setVisibility(View.GONE);
        mPresenter.loadBookMark(bookId);
    }

    @Override
    public void deleteNoteFinish() {

        mPresenter.loadBookNote(bookId);
    }

    @Override
    public void showAutoMark(BookMark bookMark) {
        mPageLoader.setBookAutoMark(bookMark);
    }

    @Override
    public void showNote(List<BaseBean> bookNote) {
        bookNoteAdapter.refreshItems(bookNote);
    }

    @Override
    public void hadPay(boolean pay) {
        //是否购买
        mPageLoader.setHadPay(pay);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    private void getSubscribeInfo() {
        HttpUtil.getInstance().getRequestApi().getSubscribeInfo(LoginUtil.getUserId())
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<SubscribeBean>>(this) {
                    @Override
                    public void onSuccess(BaseResponse<SubscribeBean> o) {
                        subscribeBean = o.getData();
                        mPageLoader.setSubscribe(subscribeBean.getVip_status() == 2);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
