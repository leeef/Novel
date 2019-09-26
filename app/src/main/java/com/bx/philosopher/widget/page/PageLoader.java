package com.bx.philosopher.widget.page;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.bx.philosopher.R;
import com.bx.philosopher.model.bean.response.BookMark;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.ui.activity.BookDetailActivity;
import com.bx.philosopher.ui.activity.NoteEditActivity;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.IOUtils;
import com.bx.philosopher.utils.ScreenUtils;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.widget.ReadSettingManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;

/**
 * 绘制图书显示页面
 */

public abstract class PageLoader {
    private static final String TAG = "PageLoader";

    // 当前页面的状态
    public static final int STATUS_LOADING = 1;         // 正在加载
    public static final int STATUS_FINISH = 2;          // 加载完成
    public static final int STATUS_ERROR = 3;           // 加载错误 (一般是网络加载情况)
    public static final int STATUS_EMPTY = 4;           // 空数据
    public static final int STATUS_PARING = 5;          // 正在解析 (装载本地数据)
    public static final int STATUS_PARSE_ERROR = 6;     // 本地文件解析错误(暂未被使用)
    public static final int STATUS_CATEGORY_EMPTY = 7;  // 获取到的目录为空
    // 默认的显示参数配置
    private static final int DEFAULT_MARGIN_HEIGHT = 28;
    private static final int DEFAULT_MARGIN_WIDTH = 15;
    private static final int DEFAULT_TIP_SIZE = 13;
    private static final int EXTRA_TITLE_SIZE = 2;

    private int readPageCount = 4;//免费试读的页数
    private boolean hadPay;//购买
    private boolean subscribe;//订阅
    private String from_type;//图书馆或者探索页面书籍

    // 当前章节列表
    protected List<String> mChapterList;
    // 书本对象
//    protected CollBookBean mCollBook;
    protected int bookID;
    protected String bookName;
    // 监听器
    protected OnPageChangeListener mPageChangeListener;

    private Context mContext;
    // 页面显示类
    private PageView mPageView;
    // 当前显示的页
    private TxtPage mCurPage;
    // 上一章的页面列表缓存
    private List<TxtPage> mPrePageList;
    // 当前章节的页面列表
    private List<TxtPage> mCurPageList;
    // 下一章的页面列表缓存
    private List<TxtPage> mNextPageList;

    // 绘制电池的画笔
    private Paint mBatteryPaint;
    // 绘制提示的画笔
    private Paint mTipPaint;
    // 绘制标题的画笔
    private Paint mTitlePaint;
    // 绘制背景颜色的画笔(用来擦除需要重绘的部分)
    private Paint mBgPaint;
    // 绘制小说内容的画笔
    private TextPaint mTextPaint;
    private TextPaint selectTextPaint;
    // 阅读器的配置选项
    private ReadSettingManager mSettingManager;
    // 被遮盖的页，或者认为被取消显示的页
    private TxtPage mCancelPage;
    // 存储阅读记录类
    private BookMark mBookRecord;

    private Disposable mPreLoadDisp;

    /*****************params**************************/
    // 当前的状态
    protected int mStatus = STATUS_LOADING;
    // 判断章节列表是否加载完成
    protected boolean isChapterListPrepare;

    // 是否打开过章节
    private boolean isChapterOpen;
    private boolean isFirstOpen = true;
    private boolean isClose;
    // 页面的翻页效果模式
    private PageMode mPageMode;
    // 加载器的颜色主题
    private PageStyle mPageStyle;
    //当前是否是夜间模式
    private boolean isNightMode;
    //书籍绘制区域的宽高
    private int mVisibleWidth;
    private int mVisibleHeight;
    //应用的宽高
    private int mDisplayWidth;
    private int mDisplayHeight;
    //间距
    private int mMarginWidth;
    private int mMarginHeight;
    //字体的颜色
    private int mTextColor;
    //标题的大小
    private int mTitleSize;
    //字体的大小
    private int mTextSize;
    //行间距
    public int mTextInterval;
    //标题的行间距 = 行间距
    private int mTitleInterval;
    //段落距离 = 行间距
    private int mTextPara;
    private int mTextParaType;//用来动态控制段落间距和行间距 值为1 2 4
    private int mTitlePara;//标题和 章节内容的距离 行间距+2
    //电池的百分比
    private int mBatteryLevel;
    //当前页面的背景
    private int mBgColor;


    // 当前章
    protected int mCurChapterPos = 0;
    //上一章的记录
    private int mLastChapterPos = 0;

    boolean isLongPress = false;
    boolean finishTouch = false;
    boolean hasUnderline = false;

    public int markSite = -1;

    /*****************************init params*******************************/
    public PageLoader(PageView pageView, int bookId) {
        mPageView = pageView;
        mContext = pageView.getContext();
        bookID = bookId;
        mChapterList = new ArrayList<>(1);

        // 初始化数据
        initData();
        // 初始化画笔
        initPaint();
        // 初始化PageView
        initPageView();
        // 初始化书籍
        prepareBook();
    }

    private void initData() {
        // 获取配置管理器
        mSettingManager = ReadSettingManager.getInstance();
        // 获取配置参数
        mPageMode = mSettingManager.getPageMode();
        mPageStyle = mSettingManager.getPageStyle();
        // 初始化参数
        mMarginWidth = ScreenUtils.dpToPx(DEFAULT_MARGIN_WIDTH);
        mMarginHeight = ScreenUtils.dpToPx(DEFAULT_MARGIN_HEIGHT);

        mTextParaType = mSettingManager.getFormat();
        // 配置文字有关的参数
        setUpTextParams(mSettingManager.getTextSize());
    }

    /**
     * 作用：设置与文字相关的参数
     *
     * @param textSize
     */
    private void setUpTextParams(int textSize) {
        // 文字大小
        mTextSize = textSize;
        mTitleSize = mTextSize + ScreenUtils.spToPx(EXTRA_TITLE_SIZE);//标题比字体大2

        // 行间距 默认字体的二倍
        mTextInterval = mTextSize * 2 / mTextParaType;

        mTitleInterval = mTextInterval;
        // 段落间距 = 行间距
        mTextPara = mTextInterval;

        mTitlePara = mTextInterval + +ScreenUtils.spToPx(EXTRA_TITLE_SIZE);//行间距+2
    }


    int getTextWidth() {
        return mTextSize;
    }


    @SuppressLint("RestrictedApi")
    private void initPaint() {
        // 绘制提示的画笔
        mTipPaint = new Paint();
        mTipPaint.setColor(mTextColor);
        mTipPaint.setTextAlign(Paint.Align.LEFT); // 绘制的起始点
        mTipPaint.setTextSize(ScreenUtils.spToPx(DEFAULT_TIP_SIZE)); // Tip默认的字体大小
        mTipPaint.setAntiAlias(true);
        mTipPaint.setSubpixelText(true);

        // 绘制页面内容的画笔
        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        // 绘制选中的内容的画笔
        selectTextPaint = new TextPaint();
        selectTextPaint.setColor(mContext.getResources().getColor(R.color.color_0065c933));
//        selectTextPaint.setTextSize(mTextSize + 5);
        selectTextPaint.setAntiAlias(true);

        // 绘制标题的画笔
        mTitlePaint = new TextPaint();
        mTitlePaint.setColor(mTextColor);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTitlePaint.setAntiAlias(true);

        // 绘制背景的画笔
        mBgPaint = new Paint();
        mBgPaint.setColor(mBgColor);

        // 绘制电池的画笔 去掉
        mBatteryPaint = new Paint();
        mBatteryPaint.setAntiAlias(true);
        mBatteryPaint.setDither(true);

        // 初始化页面样式
        setNightMode(mSettingManager.isNightMode());

    }

    public boolean isHadPay() {
        return hadPay;
    }

    public void setHadPay(boolean hadPay) {
        this.hadPay = hadPay;
    }

    public boolean isTrialRead() {
        if (from_type.equals(BookDetailActivity.TYPE_EXPLORE)) {
            return hadPay;
        } else {
            return subscribe;
        }
    }

    public int getBookID() {
        return bookID;
    }

    public String getFrom_type() {
        return from_type;
    }

    public void setFrom_type(String from_type) {
        this.from_type = from_type;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public void setSubscribe(boolean subscribe) {
        this.subscribe = subscribe;
    }

    private void initPageView() {
        //配置参数
        mPageView.setPageMode(mPageMode);
        mPageView.setBgColor(mBgColor);
    }

    /****************************** public method***************************/


    /**
     * 跳转到指定章节
     *
     * @param pos:从 0 开始。
     */
    public void skipToChapter(int pos) {
        // 设置参数
        mCurChapterPos = pos;

        // 将上一章的缓存设置为null
        mPrePageList = null;
        // 如果当前下一章缓存正在执行，则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }
        // 将下一章缓存设置为null
        mNextPageList = null;

        // 打开指定章节
        openChapter();
    }

    /**
     * 跳转到指定的页
     *
     * @param pos
     */
    public boolean skipToPage(int pos) {
        if (!isChapterListPrepare) {
            return false;
        }
        mCurPage = getCurPage(pos);
        mPageView.drawCurPage(false);
        return true;
    }

    /**
     * 跳转到指定的页
     *
     * @param site
     */
    public boolean skipToPageBySite(int site) {
        if (!isChapterListPrepare) {
            return false;
        }
        mCurPage = getPageBySite(site);
        mPageView.drawCurPage(false);
        return true;
    }

    //在当前章节根据书签位置定位页面
    private TxtPage getPageBySite(int site) {
        int length = 0;
        for (int i = 0; i < mCurPageList.size(); i++) {
            int start = length;
            int end = length + mCurPageList.get(i).toString().length();
            if (start <= site && site < end) {
                return mCurPageList.get(i);
            } else {
                length += mCurPageList.get(i).toString().length();
            }
        }
        return new TxtPage();
    }

    //在当前章节根据书签位置定位页面
    private int getPagePostionBySite(int site) {
        int length = 0;
        for (int i = 0; i < mCurPageList.size(); i++) {
            int start = length;
            int end = length + mCurPageList.get(i).toString().length();
            if (start <= site && site < end) {
                return mCurPageList.get(i).position;
            } else {
                length += mCurPageList.get(i).toString().length();
            }
        }
        return 0;
    }


    /**
     * 跳转到指定的章节的指定页面
     *
     * @param site
     */
    public void skipToPage(int chapter, int site) {
        if (chapter == mCurChapterPos) {
            skipToPageBySite(site);
        } else {
            // 设置参数
            mCurChapterPos = chapter;

            // 将上一章的缓存设置为null
            mPrePageList = null;
            // 如果当前下一章缓存正在执行，则取消
            if (mPreLoadDisp != null) {
                mPreLoadDisp.dispose();
            }
            // 将下一章缓存设置为null
            mNextPageList = null;

            // 打开指定章节
            openChapter(site);
        }
    }

    /**
     * 翻到上一页
     *
     * @return
     */
    public boolean skipToPrePage() {
        return mPageView.autoPrevPage();
    }

    /**
     * 翻到下一页
     *
     * @return
     */
    public boolean skipToNextPage() {
        return mPageView.autoNextPage();
    }

    /**
     * 更新时间
     */
    public void updateTime() {
        if (!mPageView.isRunning()) {
            mPageView.drawCurPage(true);
        }
    }

    /**
     * 更新电量
     *
     * @param level
     */
    public void updateBattery(int level) {
        mBatteryLevel = level;

        if (!mPageView.isRunning()) {
            mPageView.drawCurPage(true);
        }
    }

    /**
     * 设置提示的文字大小
     *
     * @param textSize:单位为 px。
     */
    public void setTipTextSize(int textSize) {
        mTipPaint.setTextSize(textSize);

        // 如果屏幕大小加载完成
        mPageView.drawCurPage(false);
    }

    /**
     * 设置文字相关参数
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        // 设置文字相关参数
        setUpTextParams(textSize);

        // 设置画笔的字体大小
        mTextPaint.setTextSize(mTextSize);
        // 设置标题的字体大小
        mTitlePaint.setTextSize(mTitleSize);
        // 存储文字大小
        mSettingManager.setTextSize(mTextSize);
        // 取消缓存
        mPrePageList = null;
        mNextPageList = null;

        // 如果当前已经显示数据
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            // 重新计算当前页面
            dealLoadPageList(mCurChapterPos, true);

            if (mCurPageList != null) {
                // 防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
                if (mCurPage.position >= mCurPageList.size()) {
                    mCurPage.position = mCurPageList.size() - 1;
                }

                // 重新获取指定页面
                mCurPage = mCurPageList.get(mCurPage.position);
            }
        }

        mPageView.drawCurPage(false);
    }

    /**
     * 设置文字相关参数
     *
     * @param mTextParaType 行间距 1/1 1/2 1/3
     */
    public void setTextLineMargin(int mTextParaType) {
        this.mTextParaType = mTextParaType;
        // 设置文字相关参数
        setUpTextParams(mTextSize);

        // 存储文字行间距大小
        mSettingManager.setFormat(mTextParaType);
        // 取消缓存
        mPrePageList = null;
        mNextPageList = null;

        // 如果当前已经显示数据
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            // 重新计算当前页面
            dealLoadPageList(mCurChapterPos, true);

            if (mCurPageList == null) return;
            // 防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
            if (mCurPage.position >= mCurPageList.size()) {
                mCurPage.position = mCurPageList.size() - 1;
            }

            // 重新获取指定页面
            mCurPage = mCurPageList.get(mCurPage.position);
        }

        mPageView.drawCurPage(false);
    }

    /**
     * 设置夜间模式
     *
     * @param nightMode
     */
    public void setNightMode(boolean nightMode) {
        mSettingManager.setNightMode(nightMode);
        isNightMode = nightMode;

        if (isNightMode) {
            mBatteryPaint.setColor(Color.WHITE);
            setPageStyle(PageStyle.NIGHT);
        } else {
            mBatteryPaint.setColor(Color.BLACK);
            setPageStyle(mPageStyle);
        }
    }

    /**
     * 设置页面样式
     *
     * @param pageStyle:页面样式
     */
    public void setPageStyle(PageStyle pageStyle) {
        if (pageStyle != PageStyle.NIGHT) {
            mPageStyle = pageStyle;
            mSettingManager.setPageStyle(pageStyle);
        }

        if (isNightMode && pageStyle != PageStyle.NIGHT) {
            return;
        }

        // 设置当前颜色样式
        mTextColor = ContextCompat.getColor(mContext, pageStyle.getFontColor());
        mBgColor = ContextCompat.getColor(mContext, pageStyle.getBgColor());

        mTipPaint.setColor(mTextColor);
        mTitlePaint.setColor(mTextColor);
        mTextPaint.setColor(mTextColor);

        mBgPaint.setColor(mBgColor);

        mPageView.drawCurPage(false);
    }

    /**
     * 翻页动画
     *
     * @param pageMode:翻页模式
     * @see PageMode
     */
    public void setPageMode(PageMode pageMode) {
        mPageMode = pageMode;

        mPageView.setPageMode(mPageMode);
        mSettingManager.setPageMode(mPageMode);

        // 重新绘制当前页
        mPageView.drawCurPage(false);
    }

    /**
     * 设置内容与屏幕的间距
     *
     * @param marginWidth  :单位为 px
     * @param marginHeight :单位为 px
     */
    public void setMargin(int marginWidth, int marginHeight) {
        mMarginWidth = marginWidth;
        mMarginHeight = marginHeight;

        // 如果是滑动动画，则需要重新创建了
        if (mPageMode == PageMode.SCROLL) {
            mPageView.setPageMode(PageMode.SCROLL);
        }

        mPageView.drawCurPage(false);
    }

    /**
     * 设置页面切换监听
     *
     * @param listener
     */
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mPageChangeListener = listener;

        // 如果目录加载完之后才设置监听器，那么会默认回调
        if (isChapterListPrepare) {
            mPageChangeListener.onCategoryFinish(mChapterList);
        }
    }

    /**
     * 获取当前页的状态
     *
     * @return
     */
    public int getPageStatus() {
        return mStatus;
    }

    /**
     * 获取章节目录。
     *
     * @return
     */
    public List<String> getChapterCategory() {
        return mChapterList;
    }

    /**
     * 获取当前页的页码
     *
     * @return
     */
    public int getPagePos() {
        return mCurPage == null ? -1 : mCurPage.position;
    }

    /**
     * 获取当前章节的章节位置
     *
     * @return
     */
    public int getChapterPos() {
        return mCurChapterPos;
    }


    public String getChapter() {
        return mCurPage.title;
    }

    public String getCurrentPage() {
        return mCurPage.toString();
    }

    public int getPageStartPosition() {//当前绘制页在一章中的位置下标
        if (mCurPageList == null) return 0;
        int length = 0;
        for (int i = 0; i < mCurPage.position; i++) {
            length += mCurPageList.get(i).toString().length();
        }
        return length;
    }

    public int getMarkRecord() {
        return getPageStartPosition();
    }

    public int getPageStartPosition(int pos) {
        if (mCurPageList == null) return 0;
        int length = 0;
        for (int i = 0; i < pos; i++) {
            length += mCurPageList.get(i).toString().length();
        }
        return length;
    }

    public int getPageEndPosition() {
        if (mCurPageList == null) return 0;
        int length = 0;
        for (int i = 0; i <= mCurPage.position; i++) {
            length += mCurPageList.get(i).toString().length();
        }
        return length;
    }

    public int getPageEndPosition(int pos) {
        if (mCurPageList == null) return 0;
        int length = 0;
        for (int i = 0; i <= pos; i++) {
            length += mCurPageList.get(i).toString().length();
        }
        return length;
    }

    /**
     * 获取距离屏幕的高度
     *
     * @return
     */
    public int getMarginHeight() {
        return mMarginHeight;
    }


    /**
     * 初始化书籍
     */
    private void prepareBook() {
        mLastChapterPos = mCurChapterPos;
    }

    public void setChapterPos(int chapter) {
        mCurChapterPos = chapter;
    }

    public void setBookAutoMark(BookMark bookAutoMark) {
        this.mBookRecord = bookAutoMark;
    }

    public BookMark getBookAutoMark() {
        return mBookRecord;
    }

    /**
     * 打开指定章节
     */
    public void openChapter() {
        isFirstOpen = false;

        if (!mPageView.isPrepare()) {
            return;
        }

        // 如果章节目录没有准备好
        if (!isChapterListPrepare) {
            mStatus = STATUS_LOADING;
            mPageView.drawCurPage(false);
            return;
        }

        // 如果获取到的章节目录为空
        if (mChapterList.isEmpty()) {
            mStatus = STATUS_CATEGORY_EMPTY;
            mPageView.drawCurPage(false);
            return;
        }

        if (parseCurChapter(true)) {
            // 如果章节从未打开
            if (!isChapterOpen) {

                int position = 0;
                if (mBookRecord != null) {
                    position = getPagePostionBySite(Integer.parseInt(mBookRecord.getSite()));
                }

                // 防止记录页的页号，大于当前最大页号
                if (position >= mCurPageList.size()) {
                    position = mCurPageList.size() - 1;
                }
                mCurPage = getCurPage(position);
                mCancelPage = mCurPage;
                // 切换状态
                isChapterOpen = true;
            } else {
                mCurPage = getCurPage(0);
            }
        } else {
            mCurPage = new TxtPage();
        }

        mPageView.drawCurPage(false);
    }

    /**
     * 打开指定页面
     */
    public void openChapter(int site) {
        isFirstOpen = false;
        markSite = site;
        if (!mPageView.isPrepare()) {
            return;
        }

        // 如果章节目录没有准备好
        if (!isChapterListPrepare) {
            mStatus = STATUS_LOADING;
            mPageView.drawCurPage(false);
            return;
        }

        // 如果获取到的章节目录为空
        if (mChapterList.isEmpty()) {
            mStatus = STATUS_CATEGORY_EMPTY;
            mPageView.drawCurPage(false);
            return;
        }

        if (parseCurChapter()) {
            // 如果章节从未打开
            if (!isChapterOpen) {

                // 切换状态
                isChapterOpen = true;
            }

            mCurPage = getPageBySite(site);
            mCancelPage = mCurPage;

        } else {
            mCurPage = new TxtPage();
        }

        mPageView.drawCurPage(false);
    }


    public void chapterError() {
        //加载错误
        mStatus = STATUS_ERROR;
        mPageView.drawCurPage(false);
    }

    /**
     * 关闭书本
     */
    public void closeBook() {
        isChapterListPrepare = false;
        isClose = true;

        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }

        clearList(mChapterList);
        clearList(mCurPageList);
        clearList(mNextPageList);

        mChapterList = null;
        mCurPageList = null;
        mNextPageList = null;
        mPageView = null;
        mCurPage = null;
    }

    private void clearList(List list) {
        if (list != null) {
            list.clear();
        }
    }

    public boolean isClose() {
        return isClose;
    }

    public boolean isChapterOpen() {
        return isChapterOpen;
    }

    /**
     * 加载页面列表
     *
     * @param chapterPos:章节序号
     * @return
     */
    private List<TxtPage> loadPageList(int chapterPos) throws Exception {
        // 获取章节
        String chapter = mChapterList.get(chapterPos);
        // 判断章节是否存在
        if (!hasChapterData(chapter)) {
            return null;
        }
        // 获取章节的文本流
        BufferedReader reader = getChapterReader(chapter);


        return loadPages(chapter, reader);
    }

    /**
     * 加载页面列表
     *
     * @param chapterPos:章节序号
     * @return
     */
    private List<TxtPage> loadPageList(int chapterPos, boolean currentPage) throws Exception {
        List<TxtPage> textPages = null;
        // 获取章节
        String chapter = mChapterList.get(chapterPos);
        // 判断章节是否存在
        if (!hasChapterData(chapter)) {
            return null;
        }
        // 获取章节的文本流
        BufferedReader reader = getChapterReader(chapter);

        //调用异步进行预加载加载
        Single.create((SingleOnSubscribe<List<TxtPage>>)
                e -> e.onSuccess(loadPages(chapter, reader))).compose(RxScheduler.Single_io_main())
                .subscribe(new SingleObserver<List<TxtPage>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<TxtPage> pages) {
                        try {
                            mCurPageList = pages;
                            if (mCurPageList != null) {
                                if (mCurPageList.isEmpty()) {
                                    mStatus = STATUS_EMPTY;

                                    // 添加一个空数据
                                    TxtPage page = new TxtPage();
                                    page.lines = new ArrayList<>(1);
                                    mCurPageList.add(page);
                                } else {
                                    mStatus = STATUS_FINISH;
                                }
                            } else {
                                mStatus = STATUS_EMPTY;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                            mCurPageList = null;
                            mStatus = STATUS_ERROR;
                        }

                        // 回调
                        chapterChangeCallback();
                        if (currentPage) {
                            if (markSite != -1) {//书签跳转
                                mCurPage = getPageBySite(markSite);
                                markSite = -1;
                            } else {
                                // 防止在最后一页，通过修改字体，以至于页面数减少导致崩溃的问题
                                if (mCurPage.position >= mCurPageList.size()) {
                                    mCurPage.position = mCurPageList.size() - 1;
                                }

                                // 重新获取指定页面
                                mCurPage = mCurPageList.get(mCurPage.position);
                            }
                            // 如果章节从未打开
                            if (!isChapterOpen) {

                                // 切换状态
                                isChapterOpen = true;
                            }
                            mPageView.drawCurPage(false);
                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        //无视错误
                    }
                });
        return textPages;
    }

    /**
     * 加载页面列表
     *
     * @param chapterPos:章节序号
     * @return
     */
    private List<TxtPage> loadPageList(int chapterPos, boolean currentPage, boolean first) throws Exception {
        List<TxtPage> textPages = null;
        // 获取章节
        String chapter = mChapterList.get(chapterPos);
        // 判断章节是否存在
        if (!hasChapterData(chapter)) {
            return null;
        }
        // 获取章节的文本流
        BufferedReader reader = getChapterReader(chapter);

        //调用异步进行预加载加载
        Single.create((SingleOnSubscribe<List<TxtPage>>)
                e -> e.onSuccess(loadPages(chapter, reader))).compose(RxScheduler.Single_io_main())
                .subscribe(new SingleObserver<List<TxtPage>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<TxtPage> pages) {
                        try {
                            mCurPageList = pages;
                            if (mCurPageList != null) {
                                if (mCurPageList.isEmpty()) {
                                    mStatus = STATUS_EMPTY;

                                    // 添加一个空数据
                                    TxtPage page = new TxtPage();
                                    page.lines = new ArrayList<>(1);
                                    mCurPageList.add(page);
                                } else {
                                    mStatus = STATUS_FINISH;
                                }
                            } else {
                                mStatus = STATUS_EMPTY;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                            mCurPageList = null;
                            mStatus = STATUS_ERROR;
                        }

                        // 回调
                        chapterChangeCallback();

                        // 如果章节从未打开
                        if (!isChapterOpen) {

                            int position = 0;
                            if (mBookRecord != null) {
                                position = getPagePostionBySite(Integer.parseInt(mBookRecord.getSite()));
                            }

                            // 防止记录页的页号，大于当前最大页号
                            if (position >= mCurPageList.size()) {
                                position = mCurPageList.size() - 1;
                            }
                            mCurPage = getCurPage(position);
                            mCancelPage = mCurPage;
                            // 切换状态
                            isChapterOpen = true;
                        } else {
                            mCurPage = getCurPage(0);
                        }
                        mPageView.drawCurPage(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //无视错误
                    }
                });
        return textPages;
    }

    /**
     * 加载页面列表
     *
     * @param chapterPos:章节序号
     * @return
     */
    private List<TxtPage> loadPageList(int chapterPos, boolean prePage, boolean draw, boolean isNetPage) throws Exception {
        List<TxtPage> textPages = null;
        // 获取章节
        String chapter = mChapterList.get(chapterPos);
        // 判断章节是否存在
        if (!hasChapterData(chapter)) {
            return null;
        }
        // 获取章节的文本流
        BufferedReader reader = getChapterReader(chapter);

        //调用异步进行预加载加载
        Single.create((SingleOnSubscribe<List<TxtPage>>)
                e -> e.onSuccess(loadPages(chapter, reader))).compose(RxScheduler.Single_io_main())
                .subscribe(new SingleObserver<List<TxtPage>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<TxtPage> pages) {
                        try {
                            mCurPageList = pages;
                            if (mCurPageList != null) {
                                if (mCurPageList.isEmpty()) {
                                    mStatus = STATUS_EMPTY;

                                    // 添加一个空数据
                                    TxtPage page = new TxtPage();
                                    page.lines = new ArrayList<>(1);
                                    mCurPageList.add(page);
                                } else {
                                    mStatus = STATUS_FINISH;
                                }
                            } else {
                                mStatus = STATUS_EMPTY;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                            mCurPageList = null;
                            mStatus = STATUS_ERROR;
                        }

                        // 回调
                        chapterChangeCallback();

                        if (prePage) {
                            mCurPage = getPrevLastPage();
                        } else {
                            mCurPage = mCurPageList.get(0);
                        }
                        if (draw) {
                            //未付费
                            if (!isTrialRead()) {
                                if (!prePage) {
                                    if (readPageCount <= 0) {

                                        ToastUtils.show("The end of the trial");
                                    } else {
                                        readPageCount--;
                                        mPageView.drawNextPage();
                                    }
                                } else {
                                    if (readPageCount < 4) readPageCount++;
                                    mPageView.drawNextPage();
                                }

                            } else {
                                mPageView.drawNextPage();
                            }

                        }
                        if (isNetPage) {
                            if (prePage) {
                                if (mStatus == STATUS_FINISH) {
                                    loadPrevChapter();
                                } else if (mStatus == STATUS_LOADING) {
                                    loadCurrentChapter();
                                }
                            } else {
                                if (mStatus == STATUS_FINISH) {
                                    loadNextChapter();
                                } else if (mStatus == STATUS_LOADING) {
                                    loadCurrentChapter();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        //无视错误
                    }
                });
        return textPages;
    }

    /**
     * 加载当前页的前面两个章节
     */
    private void loadPrevChapter() {
        if (mPageChangeListener != null) {
            int end = mCurChapterPos;
            int begin = end - 2;
            if (begin < 0) {
                begin = 0;
            }

            requestChapters(begin, end);
        }
    }

    /**
     * 加载当前页的后两个章节
     */
    private void loadNextChapter() {
        if (mPageChangeListener != null) {

            // 提示加载后两章
            int begin = mCurChapterPos + 1;
            int end = begin + 1;

            // 判断是否大于最后一章
            if (begin >= mChapterList.size()) {
                // 如果下一章超出目录了，就没有必要加载了
                return;
            }

            if (end > mChapterList.size()) {
                end = mChapterList.size() - 1;
            }

            requestChapters(begin, end);
        }
    }

    /**
     * 加载前一页，当前页，后一页。
     */
    private void loadCurrentChapter() {
        if (mPageChangeListener != null) {
            int begin = mCurChapterPos;
            int end = mCurChapterPos;

            // 是否当前不是最后一章
            if (end < mChapterList.size()) {
                end = end + 1;
                if (end >= mChapterList.size()) {
                    end = mChapterList.size() - 1;
                }
            }

            // 如果当前不是第一章
            if (begin != 0) {
                begin = begin - 1;
                if (begin < 0) {
                    begin = 0;
                }
            }

            requestChapters(begin, end);
        }
    }


    private void requestChapters(int start, int end) {
        // 检验输入值
        if (start < 0) {
            start = 0;
        }

        if (end >= mChapterList.size()) {
            end = mChapterList.size() - 1;
        }


        List<String> chapters = new ArrayList<>();

        // 过滤，哪些数据已经加载了
        for (int i = start; i <= end; ++i) {
            String txtChapter = mChapterList.get(i);
            if (!hasChapterData(txtChapter)) {
                chapters.add(txtChapter);
            }
        }

        if (!chapters.isEmpty()) {
            mPageChangeListener.requestChapters(chapters);
        }
    }

    /*******************************abstract method***************************************/

    /**
     * 刷新章节列表
     */
    public abstract void refreshChapterList(List<String> bookChapterList);

    /**
     * 获取章节的文本流
     *
     * @param chapter
     * @return
     */
    protected abstract BufferedReader getChapterReader(String chapter) throws Exception;

    /**
     * 章节数据是否存在
     *
     * @return
     */
    protected abstract boolean hasChapterData(String chapter);

    /***********************************default method***********************************************/

    void drawPage(Bitmap bitmap, boolean isUpdate) {
        drawBackground(mPageView.getBgBitmap(), isUpdate);
        if (!isUpdate) {
            drawContent(bitmap);
        }
        //更新绘制
        mPageView.invalidate();
        if (mPageChangeListener != null)
            mPageChangeListener.onPageChange(getChapterPos(), getPagePos());
    }

    private void drawBackground(Bitmap bitmap, boolean isUpdate) {
        Canvas canvas = new Canvas(bitmap);
        int tipMarginHeight = ScreenUtils.dpToPx(3);
        if (!isUpdate) {
            /****绘制背景****/
            canvas.drawColor(mBgColor);

            if (!mChapterList.isEmpty()) {
                /*****初始化标题的参数********/
                //需要注意的是:绘制text的y的起始点是text的基准线的位置，而不是从text的头部的位置
                float tipTop = tipMarginHeight - mTipPaint.getFontMetrics().top;
                //根据状态不一样，数据不一样
                if (mStatus != STATUS_FINISH) {
                    if (isChapterListPrepare) {
                        canvas.drawText(mChapterList.get(mCurChapterPos)
                                , mMarginWidth, tipTop, mTipPaint);
                    }
                } else {
                    canvas.drawText(mCurPage.title, mMarginWidth, tipTop, mTipPaint);
                }

                /******绘制页码********/
//                // 底部的字显示的位置Y
//                float y = mDisplayHeight - mTipPaint.getFontMetrics().bottom - tipMarginHeight;
//                // 只有finish的时候采用页码
//                if (mStatus == STATUS_FINISH) {
//                    String percent = (mCurPage.position + 1) + "/" + mCurPageList.size();
//                    canvas.drawText(percent, mMarginWidth, y, mTipPaint);
//                }
            }
        } else {
            //擦除区域
            mBgPaint.setColor(mBgColor);
            canvas.drawRect(mDisplayWidth / 2, mDisplayHeight - mMarginHeight + ScreenUtils.dpToPx(2), mDisplayWidth, mDisplayHeight, mBgPaint);
        }

        /******绘制电池********/

        int visibleRight = mDisplayWidth - mMarginWidth;
        int visibleBottom = mDisplayHeight - tipMarginHeight;

        int outFrameWidth = (int) mTipPaint.measureText("xxx");
        int outFrameHeight = (int) mTipPaint.getTextSize();

        int polarHeight = ScreenUtils.dpToPx(6);
        int polarWidth = ScreenUtils.dpToPx(2);
        int border = 1;
        int innerMargin = 1;

        //电极的制作
        int polarLeft = visibleRight - polarWidth;
        int polarTop = visibleBottom - (outFrameHeight + polarHeight) / 2;
        Rect polar = new Rect(polarLeft, polarTop, visibleRight,
                polarTop + polarHeight - ScreenUtils.dpToPx(2));

        mBatteryPaint.setStyle(Paint.Style.FILL);
//        canvas.drawRect(polar, mBatteryPaint);

        //外框的制作
        int outFrameLeft = polarLeft - outFrameWidth;
        int outFrameTop = visibleBottom - outFrameHeight;
        int outFrameBottom = visibleBottom - ScreenUtils.dpToPx(2);
        Rect outFrame = new Rect(outFrameLeft, outFrameTop, polarLeft, outFrameBottom);

        mBatteryPaint.setStyle(Paint.Style.STROKE);
        mBatteryPaint.setStrokeWidth(border);
//        canvas.drawRect(outFrame, mBatteryPaint);

        //内框的制作
        float innerWidth = (outFrame.width() - innerMargin * 2 - border) * (mBatteryLevel / 100.0f);
        RectF innerFrame = new RectF(outFrameLeft + border + innerMargin, outFrameTop + border + innerMargin,
                outFrameLeft + border + innerMargin + innerWidth, outFrameBottom - border - innerMargin);

        mBatteryPaint.setStyle(Paint.Style.FILL);
//        canvas.drawRect(innerFrame, mBatteryPaint);

        /******绘制当前时间********/
        //底部的字显示的位置Y
        float y = mDisplayHeight - mTipPaint.getFontMetrics().bottom - tipMarginHeight;
        String time = StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_TIME);
        float x = outFrameLeft - mTipPaint.measureText(time) - ScreenUtils.dpToPx(4);
//        canvas.drawText(time, x, y, mTipPaint);
    }


    private void drawContent(Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);

        if (mPageMode == PageMode.SCROLL) {
            canvas.drawColor(mBgColor);
        }
        /******绘制内容****/

        if (mStatus != STATUS_FINISH) {
            //绘制字体
            String tip = "";
            switch (mStatus) {
                case STATUS_LOADING:
                    tip = "正在拼命加载中...";
                    break;
                case STATUS_ERROR:
                    tip = "加载失败(点击边缘重试)";
                    break;
                case STATUS_EMPTY:
                    tip = "文章内容为空";
                    break;
                case STATUS_PARING:
                    tip = "正在排版请等待...";
                    break;
                case STATUS_PARSE_ERROR:
                    tip = "文件解析错误";
                    break;
                case STATUS_CATEGORY_EMPTY:
                    tip = "目录列表为空";
                    break;
            }

            //将提示语句放到正中间
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            float textHeight = fontMetrics.top - fontMetrics.bottom;
            float textWidth = mTextPaint.measureText(tip);
            float pivotX = (mDisplayWidth - textWidth) / 2;
            float pivotY = (mDisplayHeight - textHeight) / 2;
            canvas.drawText(tip, pivotX, pivotY, mTextPaint);
        } else {
            float top;

            if (mPageMode == PageMode.SCROLL) {
                top = -mTextPaint.getFontMetrics().top;
            } else {
                top = mMarginHeight - mTextPaint.getFontMetrics().top;
            }

            //设置总距离
            int interval = mTextInterval + (int) mTextPaint.getTextSize();
            int para = mTextPara + (int) mTextPaint.getTextSize();
            int titleInterval = mTitleInterval + (int) mTitlePaint.getTextSize();
            int titlePara = mTitlePara + (int) mTextPaint.getTextSize();
            String str = null;

            currentTxtLines.clear();
            //对内容进行绘制
            int charIndex = 0;
            //对标题进行绘制
            for (int i = 0; i < mCurPage.titleLines; ++i) {
                str = mCurPage.lines.get(i);


                //设置顶部间距
                if (i == 0) {
                    top += mTitlePara;
                }

                //计算文字显示的起始点
                int start = (int) (mDisplayWidth - mTitlePaint.measureText(str)) / 2;
                //进行绘制
                canvas.drawText(str, start, top, mTitlePaint);
                //确定字体坐标
                TxtLine txtLine = getList(str, start, mTitlePaint, top, charIndex);
                charIndex += str.length();//一行的长度 用来确定每个字在一章中的下标
                currentTxtLines.add(txtLine);
                //设置尾部间距
                if (i == mCurPage.titleLines - 1) {
                    top += titlePara;
                } else {
                    //行间距
                    top += titleInterval;
                }
            }
            for (int i = mCurPage.titleLines; i < mCurPage.lines.size(); ++i) {
                str = mCurPage.lines.get(i);
                canvas.drawText(str, mMarginWidth, top, mTextPaint);
                //确定字体坐标
                TxtLine txtLine = getList(str, mMarginWidth, mTextPaint, top, charIndex);
                charIndex += str.length();//一行的长度 用来确定每个字在一章中的下标
                currentTxtLines.add(txtLine);
                if (str.endsWith("\n")) {
                    top += para;
                } else {
                    top += interval;
                }
            }
            if (isLongPress && !finishTouch) {
                drawSelectedLines(canvas);
            }

            if (hasUnderline) {
                drawSelectedUnderLine(selectTxtLine, canvas, selectTextPaint);
            }

            mPageChangeListener.setUnderLine(canvas, selectTextPaint);

        }
    }

    void prepareDisplay(int w, int h) {
        // 获取PageView的宽高
        mDisplayWidth = w;
        mDisplayHeight = h;

        // 获取内容显示位置的大小
        mVisibleWidth = mDisplayWidth - mMarginWidth * 2;
        mVisibleHeight = mDisplayHeight - mMarginHeight * 2;

        // 重置 PageMode
        mPageView.setPageMode(mPageMode);

        if (!isChapterOpen) {
            // 展示加载界面
            mPageView.drawCurPage(false);
            // 如果在 display 之前调用过 openChapter 肯定是无法打开的。
            // 所以需要通过 display 再重新调用一次。
            if (!isFirstOpen) {
                // 打开书籍
                openChapter();
            }
        } else {
            // 如果章节已显示，那么就重新计算页面
            if (mStatus == STATUS_FINISH) {
                dealLoadPageList(mCurChapterPos, true);
                // 重新设置文章指针的位置
                mCurPage = getCurPage(mCurPage.position);
            }
            mPageView.drawCurPage(false);
        }
    }

    /**
     * 翻阅上一页
     *
     * @return
     */
    boolean prev() {
        // 以下情况禁止翻页
        if (!canTurnPage()) {
            return false;
        }

        if (mStatus == STATUS_FINISH) {
            // 先查看是否存在上一页
            TxtPage prevPage = getPrevPage();
            if (prevPage != null) {
                if (!isTrialRead()) {
                    if (readPageCount < 4) readPageCount++;
                }
                mCancelPage = mCurPage;
                mCurPage = prevPage;
                mPageView.drawNextPage();
                return true;
            }
        }

        if (!hasPrevChapter()) {
            return false;
        }

        mCancelPage = mCurPage;
        if (parsePrevChapter(true, false)) {
            mCurPage = getPrevLastPage();
        } else {
            mCurPage = new TxtPage();
        }
        if (!isTrialRead()) {
            if (readPageCount < 4) readPageCount++;
        }
        mPageView.drawNextPage();
        return true;
    }

    /**
     * 解析上一章数据
     *
     * @return:数据是否解析成功
     */
    boolean parsePrevChapter(boolean draw, boolean isNetPage) {
        // 加载上一章数据
        int prevChapter = mCurChapterPos - 1;

        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = prevChapter;

        // 当前章缓存为下一章
        mNextPageList = mCurPageList;

        // 判断是否具有上一章缓存
        if (mPrePageList != null) {
            mCurPageList = mPrePageList;
            mPrePageList = null;

            // 回调
            chapterChangeCallback();
        } else {
            dealLoadPageList(prevChapter, true, draw, isNetPage);
        }
        return mCurPageList != null;
    }

    private boolean hasPrevChapter() {
        //判断是否上一章节为空
        if (mCurChapterPos - 1 < 0) {
            return false;
        }
        return true;
    }

    /**
     * 翻到下一页
     *
     * @return:是否允许翻页
     */
    boolean next() {
        // 以下情况禁止翻页
        if (!canTurnPage()) {
            return false;
        }

        if (mStatus == STATUS_FINISH) {
            // 先查看是否存在下一页
            TxtPage nextPage = getNextPage();
            if (nextPage != null) {
                //未订阅或者未付费
                if (!isTrialRead()) {
                    if (readPageCount <= 0) {
                        ToastUtils.show("The end of the trial");
                        return false;
                    } else {
                        readPageCount--;
                    }
                }
                mCancelPage = mCurPage;
                mCurPage = nextPage;
                mPageView.drawNextPage();

                return true;
            }
        }

        if (!hasNextChapter()) {
            return false;
        }

        //未订阅或者未付费
        if (!isTrialRead()) {
            if (readPageCount <= 0) {
                ToastUtils.show("The end of the trial");
                return false;
            } else {
                readPageCount--;
            }
        }
        mCancelPage = mCurPage;
        // 解析下一章数据
        if (parseNextChapter(true, false)) {
            mCurPage = mCurPageList.get(0);
        } else {
            mCurPage = new TxtPage();
        }
        mPageView.drawNextPage();
        return true;
    }


    private boolean hasNextChapter() {
        // 判断是否到达目录最后一章
        if (mCurChapterPos + 1 >= mChapterList.size()) {
            return false;
        }
        return true;
    }

    boolean parseCurChapter() {
        // 解析数据
        dealLoadPageList(mCurChapterPos, true);
        // 预加载下一页面
        preLoadNextChapter();
        return mCurPageList != null;
    }

    boolean parseCurChapter(boolean first) {
        // 解析数据
        dealLoadPageList(mCurChapterPos, true, first);
        // 预加载下一页面
        preLoadNextChapter();
        return mCurPageList != null;
    }

    /**
     * 解析下一章数据
     *
     * @return:返回解析成功还是失败
     */
    boolean parseNextChapter(boolean draw, boolean isNetPage) {
        int nextChapter = mCurChapterPos + 1;

        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = nextChapter;

        // 将当前章的页面列表，作为上一章缓存
        mPrePageList = mCurPageList;

        // 是否下一章数据已经预加载了
        if (mNextPageList != null) {
            mCurPageList = mNextPageList;
            mNextPageList = null;
            // 回调
            chapterChangeCallback();
        } else {
            // 处理页面解析
            dealLoadPageList(nextChapter, false, draw, isNetPage);
        }
        // 预加载下一页面
        preLoadNextChapter();
        return mCurPageList != null;
    }

    private void dealLoadPageList(int chapterPos, boolean currentPage) {
        try {
            mCurPageList = loadPageList(chapterPos, currentPage);
            if (mCurPageList != null) {
                if (mCurPageList.isEmpty()) {
                    mStatus = STATUS_EMPTY;

                    // 添加一个空数据
                    TxtPage page = new TxtPage();
                    page.lines = new ArrayList<>(1);
                    mCurPageList.add(page);
                } else {
                    mStatus = STATUS_FINISH;
                }
            } else {
                mStatus = STATUS_LOADING;
            }
        } catch (Exception e) {
            e.printStackTrace();

            mCurPageList = null;
            mStatus = STATUS_ERROR;
        }

        // 回调
        chapterChangeCallback();
    }

    private void dealLoadPageList(int chapterPos, boolean currentPage, boolean first) {
        try {
            mCurPageList = loadPageList(chapterPos, currentPage, first);
            if (mCurPageList != null) {
                if (mCurPageList.isEmpty()) {
                    mStatus = STATUS_EMPTY;

                    // 添加一个空数据
                    TxtPage page = new TxtPage();
                    page.lines = new ArrayList<>(1);
                    mCurPageList.add(page);
                } else {
                    mStatus = STATUS_FINISH;
                }
            } else {
                mStatus = STATUS_LOADING;
            }
        } catch (Exception e) {
            e.printStackTrace();

            mCurPageList = null;
            mStatus = STATUS_ERROR;
        }

        // 回调
        chapterChangeCallback();
    }

    private void dealLoadPageList(int chapterPos, boolean prePage, boolean draw, boolean isNetPage) {
        try {
            mCurPageList = loadPageList(chapterPos, prePage, draw, isNetPage);
            if (mCurPageList != null) {
                if (mCurPageList.isEmpty()) {
                    mStatus = STATUS_EMPTY;

                    // 添加一个空数据
                    TxtPage page = new TxtPage();
                    page.lines = new ArrayList<>(1);
                    mCurPageList.add(page);
                } else {
                    mStatus = STATUS_FINISH;
                }
            } else {
                mStatus = STATUS_LOADING;
            }
        } catch (Exception e) {
            e.printStackTrace();

            mCurPageList = null;
            mStatus = STATUS_ERROR;
        }

        // 回调
        chapterChangeCallback();
    }


    private void chapterChangeCallback() {
        if (mPageChangeListener != null) {
            mPageChangeListener.onChapterChange(mCurChapterPos);
            mPageChangeListener.onPageCountChange(mCurPageList != null ? mCurPageList.size() : 0);
        }
    }

    // 预加载下一章
    private void preLoadNextChapter() {
        int nextChapter = mCurChapterPos + 1;

        // 如果不存在下一章，或者下一章数据还没下载好，则不进行加载。
        if (!hasNextChapter()
                || !hasChapterData(mChapterList.get(nextChapter))) {
            return;
        }

        //如果之前正在加载则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }

        //调用异步进行预加载加载
        Single.create((SingleOnSubscribe<List<TxtPage>>) e ->
                e.onSuccess(loadPageList(nextChapter))).compose(RxScheduler.Single_io_main())
                .subscribe(new SingleObserver<List<TxtPage>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mPreLoadDisp = d;
                    }

                    @Override
                    public void onSuccess(List<TxtPage> pages) {
                        mNextPageList = pages;
                    }

                    @Override
                    public void onError(Throwable e) {
                        //无视错误
                    }
                });
    }

    // 取消翻页
    void pageCancel() {
        if (mCurPage.position == 0 && mCurChapterPos > mLastChapterPos) { // 加载到下一章取消了
            if (mPrePageList != null) {
                cancelNextChapter();
            } else {
                if (parsePrevChapter(false, false)) {
                    mCurPage = getPrevLastPage();
                } else {
                    mCurPage = new TxtPage();
                }
            }
        } else if (mCurPageList == null
                || (mCurPage.position == mCurPageList.size() - 1
                && mCurChapterPos < mLastChapterPos)) {  // 加载上一章取消了

            if (mNextPageList != null) {
                cancelPreChapter();
            } else {
                if (parseNextChapter(false, false)) {
                    mCurPage = mCurPageList.get(0);
                } else {
                    mCurPage = new TxtPage();
                }
            }
        } else {
            // 假设加载到下一页，又取消了。那么需要重新装载。
            mCurPage = mCancelPage;
        }
    }

    private void cancelNextChapter() {
        int temp = mLastChapterPos;
        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = temp;

        mNextPageList = mCurPageList;
        mCurPageList = mPrePageList;
        mPrePageList = null;

        chapterChangeCallback();

        mCurPage = getPrevLastPage();
        mCancelPage = null;
    }

    private void cancelPreChapter() {
        // 重置位置点
        int temp = mLastChapterPos;
        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = temp;
        // 重置页面列表
        mPrePageList = mCurPageList;
        mCurPageList = mNextPageList;
        mNextPageList = null;

        chapterChangeCallback();

        mCurPage = getCurPage(0);
        mCancelPage = null;
    }


    /**************************************private method********************************************/
    /**
     * 将章节数据，解析成页面列表
     *
     * @param chapter：章节信息
     * @param br：章节的文本流
     * @return
     */
    private List<TxtPage> loadPages(String chapter, BufferedReader br) {
        //生成的页面
        List<TxtPage> pages = new ArrayList<>();
        //使用流的方式加载
        List<String> lines = new ArrayList<>();
        int rHeight = mVisibleHeight;
        int titleLinesCount = 0;
        boolean showTitle = true; // 是否展示标题
        String paragraph = chapter;//默认展示标题
        try {
            while (showTitle || (paragraph = br.readLine()) != null) {
                paragraph = StringUtils.convertCC(paragraph, mContext);
                // 重置段落
                if (!showTitle) {
                    paragraph = StringUtils.replaceEpub(paragraph, chapter, getBookName());
                    // 如果只有换行符，那么就不执行
                    if (paragraph.equals("")) continue;
                    paragraph = StringUtils.halfToFull("  ") + paragraph + "\n";
                } else {
                    //设置 title 的顶部间距
                    rHeight -= mTitlePara;
                }
                int wordCount = 0;
                String subStr = null;
                while (paragraph.length() > 0) {
                    //当前空间，是否容得下一行文字
                    if (showTitle) {
                        rHeight -= mTitlePaint.getTextSize();
                    } else {
                        rHeight -= mTextPaint.getTextSize();
                    }
                    // 一页已经填充满了，创建 TextPage
                    if (rHeight <= 0) {
                        // 创建Page
                        TxtPage page = new TxtPage();
                        page.position = pages.size();
                        page.title = StringUtils.convertCC(chapter, mContext);
                        page.lines = new ArrayList<>(lines);
                        page.titleLines = titleLinesCount;
                        pages.add(page);
                        // 重置Lines
                        lines.clear();
                        rHeight = mVisibleHeight;
                        titleLinesCount = 0;

                        continue;
                    }

                    //测量一行占用的字节数
                    if (showTitle) {
                        wordCount = mTitlePaint.breakText(paragraph,
                                true, mVisibleWidth, null);
                    } else {
                        wordCount = mTextPaint.breakText(paragraph,
                                true, mVisibleWidth, null);
                    }

                    subStr = paragraph.substring(0, wordCount);
                    if (!subStr.equals("\n")) {
                        //将一行字节，存储到lines中
                        lines.add(subStr);

                        //设置段落间距
                        if (showTitle) {
                            titleLinesCount += 1;
                            rHeight -= mTitleInterval;
                        } else {
                            rHeight -= mTextInterval;
                        }
                    }
                    //裁剪
                    paragraph = paragraph.substring(wordCount);
                }

                //增加段落的间距
                if (!showTitle && lines.size() != 0) {
                    rHeight = rHeight - mTextPara + mTextInterval;
                }

                if (showTitle) {
                    rHeight = rHeight - mTitlePara + mTitleInterval;
                    showTitle = false;
                }
            }

            if (lines.size() != 0) {
                //创建Page
                TxtPage page = new TxtPage();
                page.position = pages.size();
                page.title = StringUtils.convertCC(chapter, mContext);
                page.lines = new ArrayList<>(lines);
                page.titleLines = titleLinesCount;
                pages.add(page);
                //重置Lines
                lines.clear();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(br);
        }
        return pages;
    }


    /**
     * @return:获取初始显示的页面
     */
    private TxtPage getCurPage(int pos) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos);
        }
        return mCurPageList.get(pos);
    }

    /**
     * @return:获取上一个页面
     */
    private TxtPage getPrevPage() {
        int pos = mCurPage.position - 1;
        if (pos < 0) {
            return null;
        }
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos);
        }
        return mCurPageList.get(pos);
    }

    /**
     * @return:获取下一的页面
     */
    private TxtPage getNextPage() {
        if(mCurPageList == null||mCurPageList.size() == 0)return null;
        int pos = mCurPage.position + 1;
        if (pos >= mCurPageList.size()) {
            return null;
        }
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos);
        }
        return mCurPageList.get(pos);
    }

    /**
     * @return:获取上一个章节的最后一页
     */
    private TxtPage getPrevLastPage() {
        int pos = mCurPageList.size() - 1;

        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos);
        }

        return mCurPageList.get(pos);
    }

    /**
     * 根据当前状态，决定是否能够翻页
     *
     * @return
     */
    private boolean canTurnPage() {

        if (!isChapterListPrepare) {
            return false;
        }

        if (mStatus == STATUS_PARSE_ERROR
                || mStatus == STATUS_PARING) {
            return false;
        } else if (mStatus == STATUS_ERROR) {
            mStatus = STATUS_FINISH;
        }
        return true;
    }

    //当前页面所有字数的坐标
    private List<TxtLine> currentTxtLines = new ArrayList<>();

    //绘制内容时  确定每行字的坐标
    public TxtLine getList(String content, float x, Paint paint, float y, int site) {

        TxtLine txtLine = new TxtLine();
        if (content.length() <= 0) return txtLine;

        char[] cs = content.toCharArray();
        if (cs == null || cs.length == 0) {
            return null;
        }

        for (int i = 0, size = cs.length; i < size; i++) {
            String mesasrustr = String.valueOf(cs[i]);
            float charwidth = paint.measureText(mesasrustr);

            TxtChar txtChar = new TxtChar(cs[i]);
            txtChar.CharWidth = charwidth;

            txtChar.Left = (int) x;
            txtChar.Right = (int) (x + txtChar.CharWidth);
            txtChar.Bottom = (int) (paint.getFontMetrics().descent + y);
            txtChar.Top = (int) (paint.getFontMetrics().ascent + y);
            x = txtChar.Right;
            txtChar.index = getPageStartPosition() + site + i;
            txtLine.addChar(txtChar);
        }
        return txtLine;
    }


    //找到长按选中的字符
    TxtChar findCharByPositionOfVerticalMode(float positionX, float positionY) {
        for (TxtLine line : currentTxtLines) {
            List<TxtChar> chars = line.getTxtChars();
            if (chars != null && chars.size() > 0) {
                for (TxtChar c : chars) {
                    if (positionY > (c.Top) && positionY < (c.Bottom)) {
                        if (positionX > c.Left && positionX < c.Right) {
                            return c;
                        } else {//说明在行的左边或者右边啊
                            TxtChar first = chars.get(0);
                            TxtChar last = chars.get(chars.size() - 1);
                            if (positionX < first.Left) {
                                return first;
                            } else if (positionX > last.Right) {
                                return last;
                            }
                        }
                    } else {
                        break;//说明在下一行
                    }
                }
            }
        }
        return null;
    }

    //当前每行选中的文字
    private List<TxtLine> selectTxtLine = new ArrayList<>();


    //获取选中的文字
    String getSelectText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TxtLine txtLine : selectTxtLine) {
            stringBuilder.append(txtLine.toString());
        }
        return stringBuilder.toString();
    }

    //设置移动后之前的文字信息
    List<TxtLine> getSelectText(TxtChar start, TxtChar end) {
        boolean sameLine = start.Bottom == end.Bottom;//是否同行
        List<TxtLine> data = new ArrayList<>();
        for (TxtLine txtLine : currentTxtLines) {
            List<TxtChar> chars = txtLine.getTxtChars();
            TxtLine txtLine1 = new TxtLine();
            for (TxtChar txtChar : chars) {
                if (sameLine) {
                    if (txtChar.Bottom == start.Bottom) {
                        if (txtChar.Left >= start.Left && txtChar.Left <= end.Left) {
                            txtLine1.addChar(txtChar);
                        }
                    }
                } else {
                    if (txtChar.Bottom == start.Bottom) {
                        if (txtChar.Left >= start.Left) {
                            txtLine1.addChar(txtChar);
                        }
                    } else if (txtChar.Bottom > start.Bottom && txtChar.Bottom < end.Bottom) {
                        txtLine1.addChar(txtChar);
                    } else if (txtChar.Bottom == end.Bottom) {
                        if (txtChar.Left <= end.Left) {
                            txtLine1.addChar(txtChar);
                        }
                    }
                }
            }
            if (txtLine1.getTxtChars() != null && txtLine1.getTxtChars().size() > 0)
                data.add(txtLine1);
        }
        return data;
    }

    //设置移动后之前的文字信息
    void setSelectTexts(TxtChar start, TxtChar end) {
        mPageView.showDialog(false);
        Log.i("yzp", "123");
        boolean sameLine = start.Bottom == end.Bottom;//是否同行
        List<TxtLine> data = new ArrayList<>();
        for (TxtLine txtLine : currentTxtLines) {
            List<TxtChar> chars = txtLine.getTxtChars();
            TxtLine txtLine1 = new TxtLine();
            for (TxtChar txtChar : chars) {
                if (sameLine) {
                    if (txtChar.Bottom == start.Bottom) {
                        if (txtChar.Left >= start.Left && txtChar.Left <= end.Left) {
                            txtLine1.addChar(txtChar);
                        }
                    }
                } else {
                    if (txtChar.Bottom == start.Bottom) {
                        if (txtChar.Left >= start.Left) {
                            txtLine1.addChar(txtChar);
                        }
                    } else if (txtChar.Bottom > start.Bottom && txtChar.Bottom < end.Bottom) {
                        txtLine1.addChar(txtChar);
                    } else if (txtChar.Bottom == end.Bottom) {
                        if (txtChar.Left <= end.Left) {
                            txtLine1.addChar(txtChar);
                        }
                    }
                }
            }
            if (txtLine1.getTxtChars() != null && txtLine1.getTxtChars().size() > 0)
                data.add(txtLine1);
        }
        selectTxtLine.clear();
        selectTxtLine.addAll(data);
//        //更新绘制
        ((Activity) mContext).runOnUiThread(() -> mPageView.drawCurPage(false));
    }


    //绘制选中的背景
    public void drawSelectedLines(Canvas canvas) {

        for (TxtLine line : selectTxtLine) {
            if (line.getTxtChars() != null && line.getTxtChars().size() > 0) {

                TxtChar fistChar = line.getTxtChars().get(0);
                TxtChar lastChar = line.getTxtChars().get(line.getTxtChars().size() - 1);

                RectF rect = new RectF(fistChar.Left, fistChar.Top,
                        lastChar.Right, lastChar.Bottom);

                canvas.drawRect(rect, selectTextPaint);

            }
        }

        mPageView.drawSlider(canvas);
    }


    //绘制选中的下划线
    public void drawSelectedUnderLine(List<TxtLine> selectedLines, Canvas canvas, Paint paint) {

        for (TxtLine line : selectedLines) {
            if (line.getTxtChars() != null && line.getTxtChars().size() > 0) {

                TxtChar fistChar = line.getTxtChars().get(0);
                TxtChar lastChar = line.getTxtChars().get(line.getTxtChars().size() - 1);


                RectF rect = new RectF(fistChar.Left, fistChar.Bottom + mTextInterval / 2,
                        lastChar.Right, lastChar.Bottom + mTextInterval / 2 + 3);

                canvas.drawRect(rect, paint);

            }
        }
    }

    void setUnderLine() {
        String underLine = getSelectText();
        int start = getPageStartPosition() + mCurPage.toString().indexOf(underLine);
        int end = start + underLine.length() - 1;//下标
        mPageChangeListener.onSetUnderLine(start, end, underLine, "");
    }

    void setNote() {
        String underLine = getSelectText();
        int start = getPageStartPosition() + mCurPage.toString().indexOf(underLine);
        int end = start + underLine.length() - 1;//下标

        NoteEditActivity.startActivity(mContext, underLine, start, end, bookID, getChapter());
    }


    //获取最底部文字的位置
    int getBottomPosition() {
        List<TxtChar> txtChars = currentTxtLines.get(currentTxtLines.size() - 1).getTxtChars();
        return txtChars.get(txtChars.size() - 1).Bottom;
    }

    //获取最顶部文字的位置
    public int getTopPosition() {
        List<TxtChar> txtChars = currentTxtLines.get(0).getTxtChars();
        return txtChars.get(0).Top;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }


    //根据下划线确定坐标
    public void setSelectTxt(List<BookMark> bookMarks, Canvas canvas, Paint paint) {
        if (currentTxtLines == null || currentTxtLines.size() == 0) return;
        for (BookMark bookMark : bookMarks) {
            TxtChar start = null;
            TxtChar end = null;
            for (TxtLine txtLine : currentTxtLines) {
                for (TxtChar txtChar : txtLine.getTxtChars()) {
                    if (txtChar.index == Integer.parseInt(bookMark.getStartsite())) {
                        start = txtChar;
                    }
                    if (txtChar.index == Integer.parseInt(bookMark.getEndsite())) {
                        end = txtChar;
                    }
                }
            }
            if (start == null && end != null)
                start = currentTxtLines.get(0).getTxtChars().get(0);
            List<TxtChar> temp = currentTxtLines.get(currentTxtLines.size() - 1).getTxtChars();
            if (end == null && start != null) end = temp.get(temp.size() - 1);
            if (start != null && end != null)
                drawSelectedUnderLine(getSelectText(start, end), canvas, paint);

        }

    }

    /*****************************************interface*****************************************/

    public interface OnPageChangeListener {
        /**
         * 作用：章节切换的时候进行回调
         *
         * @param pos:切换章节的序号
         */
        void onChapterChange(int pos);

        /**
         * 作用：请求加载章节内容
         *
         * @param requestChapters:需要下载的章节列表
         */
        void requestChapters(List<String> requestChapters);

        /**
         * 作用：章节目录加载完成时候回调
         *
         * @param chapters：返回章节目录
         */
        void onCategoryFinish(List<String> chapters);

        /**
         * 作用：章节页码数量改变之后的回调。==> 字体大小的调整，或者是否关闭虚拟按钮功能都会改变页面的数量。
         *
         * @param count:页面的数量
         */
        void onPageCountChange(int count);

        /**
         * 作用：当页面改变的时候回调
         *
         * @param pos:当前的页面的序号
         */
        void onPageChange(int pos);

        /**
         * 作用：无论是跳转还是手动翻页 只要重新绘制页面就会回调
         *
         * @param chapter:     章节
         * @param pos:当前的页面的序号
         */
        void onPageChange(int chapter, int pos);

        void onSetUnderLine(int startSite, int endSite, String content, String note);


        void setUnderLine(Canvas canvas, Paint paint);
    }
}
