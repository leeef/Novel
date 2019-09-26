package com.bx.philosopher.widget.page;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.PopupWindow;

import com.bx.philosopher.R;
import com.bx.philosopher.share.ShareUtil;
import com.bx.philosopher.ui.activity.BookDetailActivity;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.ScreenUtils;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.widget.PullToRefreshLayout;
import com.bx.philosopher.widget.ReadDialogUtil;
import com.bx.philosopher.widget.animation.CoverPageAnim;
import com.bx.philosopher.widget.animation.HorizonPageAnim;
import com.bx.philosopher.widget.animation.NonePageAnim;
import com.bx.philosopher.widget.animation.PageAnimation;
import com.bx.philosopher.widget.animation.ScrollPageAnim;
import com.bx.philosopher.widget.animation.SimulationPageAnim;
import com.bx.philosopher.widget.animation.SlidePageAnim;


/**
 * 原作者的GitHub Project Path:(https://github.com/newbiechen1024/NovelReader.git)
 * 阅读页面显示
 */
public class PageView extends View implements GestureDetector.OnGestureListener {

    private final static String TAG = "BookPageWidget";

    private int mViewWidth = 0; // 当前View的宽
    private int mViewHeight = 0; // 当前View的高

    private int mStartX = 0;
    private int mStartY = 0;
    //x轴方向的移动
    private boolean isMoveX = false;
    //y轴方向的移动
    private boolean isMoveY = false;
    // 初始化参数
    private int mBgColor = 0xFFCEC29C;
    private PageMode mPageMode = PageMode.COVER;
    // 是否允许点击
    private boolean canTouch = true;
    // 唤醒菜单的区域
    private RectF mCenterRect = null;
    private boolean isPrepare;

    private boolean isLongPress = false;

    //选中的文字
    private TxtChar firstSelectTxt;
    private boolean first = true;//默认选中的时候first不变
    private boolean end = false;
    private TxtChar lastSelectTxt;
    //取消长按后是否结束本次触摸事件
    private boolean finishTouch = false;


    protected Slider mLeftSlider = null;//左侧滑动条
    protected Slider mRightSlider = null;//右侧滑动条
    protected static int SliderWidth = 40;//滑动条宽度
    public Paint sliderPaint;

    //长按弹窗
    private ReadDialogUtil readDialogUtil;
    private PopupWindow readDialog;
    // 动画类
    private PageAnimation mPageAnim;
    // 动画监听类
    private PageAnimation.OnPageChangeListener mPageAnimListener = new PageAnimation.OnPageChangeListener() {
        @Override
        public boolean hasPrev() {
            return PageView.this.hasPrevPage();
        }

        @Override
        public boolean hasNext() {
            return PageView.this.hasNextPage();
        }

        @Override
        public void pageCancel() {
            PageView.this.pageCancel();
        }
    };

    //点击监听
    private TouchListener mTouchListener;
    //内容加载器
    private PageLoader mPageLoader;

    public PageView(Context context) {
        this(context, null);
    }

    public PageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readDialogUtil = new ReadDialogUtil();
        readDialog = readDialogUtil.getInstance(getContext(), new ReadDialogUtil.ReadDialogClick() {
            Intent intent;

            @Override
            public void copy() {
                if (mPageLoader != null) {
                    String s = mPageLoader.getSelectText();
                    if (copyStr(s))
                        ToastUtils.show("Has been copied");

                    setLongPressMode(false, true);
                }
            }

            @Override
            public void underLine() {
                mPageLoader.hasUnderline = true;
                setLongPressMode(false, true);
                mPageLoader.setUnderLine();
            }

            @Override
            public void note() {

                mPageLoader.hasUnderline = true;
                setLongPressMode(false, true);
                mPageLoader.setNote();
            }

            @Override
            public void share() {
                String url;
                if (mPageLoader.getFrom_type().equals(BookDetailActivity.TYPE_EXPLORE)) {
                    url = Constant.EXPLORE_SHARE + mPageLoader.getBookID();
                } else {
                    url = Constant.LIBRARY_SHARE + mPageLoader.getBookID();
                }

                new ShareUtil().startShareAction((Activity) getContext(), url, StringUtils.getString(R.string.book_share_title)
                        , StringUtils.getString(R.string.book_share_info));
            }
        });
        initSlide();
    }

    private void initSlide() {
        if (mLeftSlider == null) {
            mLeftSlider = new DefaultLeftSlider();
        }
        if (mRightSlider == null) {
            mRightSlider = new DefaultRightSlider();
        }
        SliderWidth = ScreenUtils.dpToPx(13);
        mLeftSlider.SliderWidth = SliderWidth;
        mRightSlider.SliderWidth = SliderWidth;

        sliderPaint = new Paint();
        sliderPaint.setColor(StringUtils.getColor(R.color.color_0065c933));
        sliderPaint.setAntiAlias(true);
    }

    public void drawSlider(Canvas canvas) {
        if (firstSelectTxt != null && lastSelectTxt != null) {
            canvas.drawPath(getLeftSliderPath(), sliderPaint);
            canvas.drawPath(getRightSliderPath(), sliderPaint);
        }
    }

    /**
     * @param FirstSelectedChar 设置左滑动条数据
     */
    public void setLeftSlider(TxtChar FirstSelectedChar) {
        mLeftSlider.Left = FirstSelectedChar.Left - SliderWidth * 2;
        mLeftSlider.Right = FirstSelectedChar.Left;
        mLeftSlider.Top = FirstSelectedChar.Bottom;
        mLeftSlider.Bottom = FirstSelectedChar.Bottom + SliderWidth * 2;
    }

    /**
     * @param LastSelectedChar 设置右滑动条数据
     */
    public void setRightSlider(TxtChar LastSelectedChar) {
        mRightSlider.Left = LastSelectedChar.Right;
        mRightSlider.Right = LastSelectedChar.Right + SliderWidth * 2;
        mRightSlider.Top = LastSelectedChar.Bottom;
        mRightSlider.Bottom = LastSelectedChar.Bottom + SliderWidth * 2;
    }

    private Path mSliderPath = new Path();

    /**
     * @return 可能返回null
     */
    protected Path getLeftSliderPath() {
        if (lastSelectTxt.Bottom < firstSelectTxt.Bottom
                || (lastSelectTxt.Bottom == firstSelectTxt.Bottom && lastSelectTxt.Left < firstSelectTxt.Left)) {

            return mLeftSlider.getPath(lastSelectTxt, mSliderPath);
        } else {
            return mLeftSlider.getPath(firstSelectTxt, mSliderPath);
        }

    }

    /**
     * @return 可能返回null
     */
    protected Path getRightSliderPath() {
        if (lastSelectTxt.Bottom < firstSelectTxt.Bottom
                || (lastSelectTxt.Bottom == firstSelectTxt.Bottom && lastSelectTxt.Left < firstSelectTxt.Left)) {

            return mRightSlider.getPath(firstSelectTxt, mSliderPath);
        } else {
            return mRightSlider.getPath(lastSelectTxt, mSliderPath);
        }

    }

    /**
     * 复制内容到剪切板
     *
     * @param copyStr
     * @return
     */
    private boolean copyStr(String copyStr) {
        try {
            //获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    //显示或隐藏长按菜单
    void showDialog(boolean show) {
        ((Activity) getContext()).runOnUiThread(() -> {
            if (readDialogUtil != null) {
                if (show) {
                    setDialogLocation();
                } else {
                    readDialog.dismiss();
                }
            }
        });

    }


    //设置弹窗的位置
    void setDialogLocation() {
        int top;
        int bottom;
        if (lastSelectTxt.Bottom > firstSelectTxt.Bottom) {
            top = firstSelectTxt.Top;
            bottom = lastSelectTxt.Bottom + SliderWidth * 2;
        } else {
            top = lastSelectTxt.Top;
            bottom = firstSelectTxt.Bottom + SliderWidth * 2;
        }
        int height = readDialogUtil.getHeight() + 10;
        if (mPageLoader.getBottomPosition() - bottom > height) {
            readDialogUtil.setBackGround(false);
            readDialog.showAtLocation(this, Gravity.TOP
                    , 0, bottom);
        } else if (top - mPageLoader.getTopPosition() > height) {
            readDialogUtil.setBackGround(true);
            readDialog.showAtLocation(this, Gravity.TOP
                    , 0, top - height);
        } else {
            readDialogUtil.setBackGround(true);
            readDialog.showAtLocation(this, Gravity.CENTER
                    , 0, 0);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (pullToRefreshLayout.state == pullToRefreshLayout.NONE) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (mViewWidth != w && mViewHeight != h) {
                mViewWidth = w;
                mViewHeight = h;

                isPrepare = true;

                if (mPageLoader != null) {
                    mPageLoader.prepareDisplay(w, h);
                }
            }
        }
    }

    //设置翻页的模式
    void setPageMode(PageMode pageMode) {
        mPageMode = pageMode;
        //视图未初始化的时候，禁止调用
        if (mViewWidth == 0 || mViewHeight == 0) return;

        switch (mPageMode) {
            case SIMULATION:
                mPageAnim = new SimulationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
            case COVER:
                mPageAnim = new CoverPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
            case SLIDE:
                mPageAnim = new SlidePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
            case NONE:
                mPageAnim = new NonePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                break;
            case SCROLL:
                mPageAnim = new ScrollPageAnim(mViewWidth, mViewHeight, 0,
                        mPageLoader.getMarginHeight(), this, mPageAnimListener);
                break;
            default:
                mPageAnim = new SimulationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
        }
    }

    public Bitmap getNextBitmap() {
        if (mPageAnim == null) return null;
        return mPageAnim.getNextBitmap();
    }

    public Bitmap getBgBitmap() {
        if (mPageAnim == null) return null;
        return mPageAnim.getBgBitmap();
    }

    public boolean autoPrevPage() {
        //滚动暂时不支持自动翻页
        if (mPageAnim instanceof ScrollPageAnim) {
            return false;
        } else {
            startPageAnim(PageAnimation.Direction.PRE);
            return true;
        }
    }

    public boolean autoNextPage() {
        if (mPageAnim instanceof ScrollPageAnim) {
            return false;
        } else {
            startPageAnim(PageAnimation.Direction.NEXT);
            return true;
        }
    }

    private void startPageAnim(PageAnimation.Direction direction) {
        if (mTouchListener == null) return;
        //是否正在执行动画
        abortAnimation();
        if (direction == PageAnimation.Direction.NEXT) {
            int x = mViewWidth;
            int y = mViewHeight;
            //初始化动画
            mPageAnim.setStartPoint(x, y);
            //设置点击点
            mPageAnim.setTouchPoint(x, y);
            //设置方向
            Boolean hasNext = hasNextPage();

            mPageAnim.setDirection(direction);
            if (!hasNext) {
                return;
            }
        } else {
            int x = 0;
            int y = mViewHeight;
            //初始化动画
            mPageAnim.setStartPoint(x, y);
            //设置点击点
            mPageAnim.setTouchPoint(x, y);
            mPageAnim.setDirection(direction);
            //设置方向方向
            Boolean hashPrev = hasPrevPage();
            if (!hashPrev) {
                return;
            }
        }
        mPageAnim.startAnim();
        this.postInvalidate();
    }

    public void setBgColor(int color) {
        mBgColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘制背景
        canvas.drawColor(mBgColor);

        //绘制动画
        mPageAnim.draw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (!canTouch && event.getAction() != MotionEvent.ACTION_DOWN) return true;

        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                showDialog(false);
                mStartX = x;
                mStartY = y;
                isMoveX = false;
                isMoveY = false;
                if (isLongPress) {//已在长按模式 改变选取内容
//                    TxtChar tempChar = mPageLoader.findCharByPositionOfVerticalMode(event.getX(), event.getY());
                    if (firstSelectTxt != null && lastSelectTxt != null) {
                        if (isInLeft(mStartX, mStartY)) {
                            //固定last
                            end = true;
                            first = false;
                        } else if (isInRight(mStartX, mStartY)) {
                            //固定first
                            first = true;
                            end = false;
                        } else {//不在选中区域 取消长按操作
                            finishTouch = true;
                            mPageLoader.finishTouch = true;
                            drawCurPage(false);
                            return true;
                        }

                    } else {
                        finishTouch = true;
                        mPageLoader.finishTouch = true;
                        drawCurPage(false);
                        return true;
                    }
                } else {
                    canTouch = mTouchListener.onTouch();
                    mPageAnim.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (finishTouch) return true;
                //长按后的移动
                if (isLongPress) {
//                    //根据手势来确定选取的文字信息
                    TxtChar tempChar = mPageLoader.findCharByPositionOfVerticalMode(event.getX(), event.getY());
                    if (tempChar == null) {
                        return true;
                    } else {
                        if (firstSelectTxt == null) {//初始化
                            firstSelectTxt = tempChar;
                            lastSelectTxt = tempChar;
                        }
                        if (first) {
                            lastSelectTxt = tempChar;

                        } else if (end) {
                            firstSelectTxt = tempChar;
                        }
                        if (lastSelectTxt.Bottom < firstSelectTxt.Bottom
                                || (lastSelectTxt.Bottom == firstSelectTxt.Bottom && lastSelectTxt.Left < firstSelectTxt.Left)) {
                            mPageLoader.setSelectTexts(lastSelectTxt, firstSelectTxt);
                            setLongPressMode(true, true);//移动后绘制选取的文字背景
                        } else {
                            mPageLoader.setSelectTexts(firstSelectTxt, lastSelectTxt);
                            setLongPressMode(true, true);//移动后绘制选取的文字背景
                        }
                        Log.i("yzp", firstSelectTxt.Char + "===" + lastSelectTxt.Char);

                    }
                    return true;
                }
                int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (!isMoveX) {
                    isMoveX = Math.abs(mStartX - event.getX()) > slop && Math.abs(mStartX - event.getX()) > Math.abs(mStartY - event.getY());
                }
                if (!isMoveY) {
                    isMoveY = event.getY() - mStartY > slop;
                }
                // 如果横向滑动了，并且当前不是下拉书签状态，则进行翻页
                if (isMoveX && pullToRefreshLayout.state == pullToRefreshLayout.NONE) {
                    mPageAnim.onTouchEvent(event);
                } else if (isMoveY) {
                    mTouchListener.onTouchEvent(event);
                    return true;
                }


                break;
            case MotionEvent.ACTION_UP:
                if (isLongPress) {
                    if (finishTouch) {
                        setLongPressMode(false, false);
                        finishTouch = false;
                        mPageLoader.finishTouch = false;
                        return true;
                    }
                    if (firstSelectTxt != null && lastSelectTxt != null) {
                        //弹框
                        mTouchListener.longClick();
                        showDialog(true);
                    }
                    return true;
                }
                if (!isMoveX) {
                    //设置中间区域范围
                    if (mCenterRect == null) {
                        mCenterRect = new RectF(mViewWidth / 5, mViewHeight / 3,
                                mViewWidth * 4 / 5, mViewHeight * 2 / 3);
                    }

                    //是否点击了中间
                    if (mCenterRect.contains(x, y) && !isMoveY) {
                        if (mTouchListener != null) {
                            mTouchListener.center();
                        }
                        return true;
                    }

                    if (isMoveY) {//下拉 加标签
                        mTouchListener.onTouchEvent(event);
                        return true;
                    }
                }
                //下拉还未完成
                if (pullToRefreshLayout.state == pullToRefreshLayout.NONE) {
                    mPageAnim.onTouchEvent(event);
                } else {
                    mTouchListener.onTouchEvent(event);
                    return true;
                }


                break;
        }
        return true;
    }

    //长按不动的情况 绘制最开始位置选中的字符
    public void initSelect(float positionX, float positionY) {
        TxtChar tempChar = mPageLoader.findCharByPositionOfVerticalMode(positionX, positionY);
        if (tempChar != null) {
            firstSelectTxt = tempChar;
            lastSelectTxt = tempChar;
            setLeftSlider(firstSelectTxt);
            setRightSlider(lastSelectTxt);
            mPageLoader.setSelectTexts(firstSelectTxt, lastSelectTxt);
            setLongPressMode(true, true);
            showDialog(true);
        }
    }

    /**
     * @return 是否点击在选中的区域
     */
    private boolean isInTxt(TxtChar txtChar, float TouchX, float TouchY) {

        Path p = new Path();
        p.moveTo(txtChar.Left, txtChar.Top);
        p.lineTo(txtChar.Left, txtChar.Top - mPageLoader.getTextWidth() / 2);
        p.lineTo(txtChar.Right + txtChar.CharWidth / 2, txtChar.Top - mPageLoader.getTextWidth() / 2);
        p.lineTo(txtChar.Right + txtChar.CharWidth / 2, txtChar.Bottom + mPageLoader.getTextWidth() / 2);
        p.lineTo(txtChar.Left - txtChar.CharWidth / 2, txtChar.Bottom + mPageLoader.getTextWidth() / 2);
        p.lineTo(txtChar.Left - txtChar.CharWidth / 2, txtChar.Top - mPageLoader.getTextWidth() / 2);
        return computeRegion(p).contains((int) TouchX, (int) TouchY);

    }

    private boolean isInLeft(float TouchX, float TouchY) {
        Path p = new Path();
        if (lastSelectTxt.Bottom < firstSelectTxt.Bottom
                || (lastSelectTxt.Bottom == firstSelectTxt.Bottom && lastSelectTxt.Left < firstSelectTxt.Left)) {

            p.moveTo(firstSelectTxt.Right, firstSelectTxt.Bottom);
            p.lineTo(firstSelectTxt.Right + SliderWidth * 2, firstSelectTxt.Bottom);
            p.lineTo(firstSelectTxt.Right + SliderWidth * 2, firstSelectTxt.Bottom + SliderWidth * 2);
            p.lineTo(firstSelectTxt.Right, firstSelectTxt.Bottom + SliderWidth * 2);
            p.lineTo(firstSelectTxt.Right, firstSelectTxt.Bottom);
        } else {
            p.moveTo(firstSelectTxt.Left, firstSelectTxt.Bottom);
            p.lineTo(firstSelectTxt.Left - SliderWidth * 2, firstSelectTxt.Bottom);
            p.lineTo(firstSelectTxt.Left - SliderWidth * 2, firstSelectTxt.Bottom + SliderWidth * 2);
            p.lineTo(firstSelectTxt.Left, firstSelectTxt.Bottom + SliderWidth * 2);
            p.lineTo(firstSelectTxt.Left, firstSelectTxt.Bottom);
        }


        return computeRegion(p).contains((int) TouchX, (int) TouchY);
    }

    private boolean isInRight(float TouchX, float TouchY) {

        Path p = new Path();
        if (lastSelectTxt.Bottom < firstSelectTxt.Bottom
                || (lastSelectTxt.Bottom == firstSelectTxt.Bottom && lastSelectTxt.Left < firstSelectTxt.Left)) {

            p.moveTo(lastSelectTxt.Left, lastSelectTxt.Bottom);
            p.lineTo(lastSelectTxt.Left - SliderWidth * 2, lastSelectTxt.Bottom);
            p.lineTo(lastSelectTxt.Left - SliderWidth * 2, lastSelectTxt.Bottom + SliderWidth * 2);
            p.lineTo(lastSelectTxt.Left, lastSelectTxt.Bottom + SliderWidth * 2);
            p.lineTo(lastSelectTxt.Left, lastSelectTxt.Bottom);

        } else {
            p.moveTo(lastSelectTxt.Right, lastSelectTxt.Bottom);
            p.lineTo(lastSelectTxt.Right + SliderWidth * 2, lastSelectTxt.Bottom);
            p.lineTo(lastSelectTxt.Right + SliderWidth * 2, lastSelectTxt.Bottom + SliderWidth * 2);
            p.lineTo(lastSelectTxt.Right, lastSelectTxt.Bottom + SliderWidth * 2);
            p.lineTo(lastSelectTxt.Right, lastSelectTxt.Bottom);
        }
        return computeRegion(p).contains((int) TouchX, (int) TouchY);
    }

    /**
     * @param path
     * @return 计算区域
     */

    private Region computeRegion(Path path) {
        Region region = new Region();
        RectF f = new RectF();
        path.computeBounds(f, true);
        region.setPath(path, new Region((int) f.left, (int) f.top, (int) f.right, (int) f.bottom));
        return region;
    }

    /* 判断是否有长按动作发生
     * @param lastX 按下时X坐标
     * @param lastY 按下时Y坐标
     * @param thisX 移动时X坐标
     * @param thisY 移动时Y坐标
     * @param lastDownTime 按下时间
     * @param thisEventTime 移动时间
     * @param longPressTime 判断长按时间的阀值
     */
    private boolean isLongPressed(float lastX, float lastY,
                                  float thisX, float thisY,
                                  long lastDownTime, long thisEventTime,
                                  long longPressTime) {
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }


    //设置长按的状态参数
    private void setLongPressMode(boolean state, boolean fresh) {
        isLongPress = state;
        mPageLoader.isLongPress = state;
        mPageAnim.isLongClick = state;
        pullToRefreshLayout.isLongPress = state;
        if (!state) {
            firstSelectTxt = null;
            lastSelectTxt = null;
        }
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (fresh) {
                    drawCurPage(false);
                }
            }
        });
    }

    /**
     * 判断是否存在上一页
     *
     * @return
     */
    private boolean hasPrevPage() {
        mTouchListener.prePage();
        return mPageLoader.prev();
    }

    /**
     * 判断是否下一页存在
     *
     * @return
     */
    private boolean hasNextPage() {
        mTouchListener.nextPage();
        return mPageLoader.next();
    }

    private void pageCancel() {
        mTouchListener.cancel();
        mPageLoader.pageCancel();
    }

    @Override
    public void computeScroll() {
        //进行滑动
        mPageAnim.scrollAnim();
        super.computeScroll();
    }

    //如果滑动状态没有停止就取消状态，重新设置Anim的触碰点
    public void abortAnimation() {
        mPageAnim.abortAnim();
    }

    public boolean isRunning() {
        if (mPageAnim == null) {
            return false;
        }
        return mPageAnim.isRunning();
    }

    public boolean isPrepare() {
        return isPrepare;
    }

    public void setTouchListener(TouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
    }

    public void drawNextPage() {
        if (!isPrepare) return;

        if (mPageAnim instanceof HorizonPageAnim) {
            ((HorizonPageAnim) mPageAnim).changePage();
        }
        mPageLoader.hasUnderline = false;
        mPageLoader.drawPage(getNextBitmap(), false);
    }

    /**
     * 绘制当前页。
     *
     * @param isUpdate
     */
    public void drawCurPage(boolean isUpdate) {
        if (!isPrepare) return;

        if (!isUpdate) {
            if (mPageAnim instanceof ScrollPageAnim) {
                ((ScrollPageAnim) mPageAnim).resetBitmap();
            }
        }

        mPageLoader.drawPage(getNextBitmap(), isUpdate);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPageAnim.abortAnim();
        mPageAnim.clear();

        mPageLoader = null;
        mPageAnim = null;
    }

    /**
     * 获取 PageLoader
     *
     * @return
     */
    public PageLoader getPageLoader(int bookId) {
        // 判是否已经存在
        if (mPageLoader != null) {
            return mPageLoader;
        }
        // 根据书籍类型，获取具体的加载器
//        if (collBook.isLocal()) {
//            mPageLoader = new LocalPageLoader(this, collBook);
//        } else {
        mPageLoader = new NetPageLoader(this, bookId);
//        }
        // 判断是否 PageView 已经初始化完成
        if (mViewWidth != 0 || mViewHeight != 0) {
            // 初始化 PageLoader 的屏幕大小
            mPageLoader.prepareDisplay(mViewWidth, mViewHeight);
        }

        return mPageLoader;
    }

    PullToRefreshLayout pullToRefreshLayout;

    public void setPullToRefreshView(PullToRefreshLayout pullToRefreshView) {
        pullToRefreshLayout = pullToRefreshView;
    }


    public interface TouchListener {
        boolean onTouch();

        void center();

        void prePage();

        void nextPage();

        void cancel();

        void longClick();

        void onTouchEvent(MotionEvent motionEvent);
    }
}
