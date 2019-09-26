package com.bx.philosopher.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.widget.page.PageView;

import java.util.Timer;
import java.util.TimerTask;

public class PullToRefreshLayout extends LinearLayout {

    private Context context;

    public PullToRefreshLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(VERTICAL);
    }

    //header

    private FrameLayout headerContainer;
    private View header;

    public View setHeader(int headerId) {

        //header container

        headerContainer = new FrameLayout(context);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
        headerContainer.setLayoutParams(llp);
        addView(headerContainer, 0);

        //header

        header = ((Activity) context).getLayoutInflater().inflate(headerId, headerContainer, false);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, header.getLayoutParams().height);
        lp.gravity = Gravity.BOTTOM;
        header.setLayoutParams(lp);
        headerContainer.addView(header);

        //header container set the same bg like header

        headerContainer.setBackground(header.getBackground());
        hideTop();
        return header;
    }

    //state

    private static final int BASE = 1;

    public final int NONE = BASE;

    public static final int PULL_LESS_THAN_CHANGE_HEIGHT = 2;
    private static final int PULL_MORE_THAN_CHANGE_HEIGHT = 3;
    private static final int RECOVER_LESS_THAN_CHANGE_HEIGHT = 4;
    private static final int RECOVER_MORE_THAN_CHANGE_HEIGHT = 5;
    private static final int RECOVER_REFRESHING = 6;

    public int state = NONE;
    public boolean isLongPress = false;
    //listener

    private Listener listener;
    private PageView pageView;

    public interface Listener {
        void onPre();//下拉前的操作

        void onChange();//下拉刷新->释放刷新

        void onRecover();//释放刷新->下拉刷新

        void onRefresh();//释放刷新->刷新中

        void onFinish();//刷新中->下拉刷新

        void onPull(float rate);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setPageView(PageView pageView) {
        this.pageView = pageView;
    }

    public void refreshFinished() {//网络请求完后 进行调用

        //从刷新中状态恢复到初始状态

        if (state == RECOVER_REFRESHING) {
            int start = -containerHeight + headerHeight;
            int end = -containerHeight;

            ValueAnimator animator = ValueAnimator.ofInt(start, end);
            animator.setDuration(500).start();
            animator.addUpdateListener(animation -> {
                int i = (int) animation.getAnimatedValue();

                LayoutParams params = (LayoutParams) headerContainer.getLayoutParams();
                params.topMargin = i;
                headerContainer.setLayoutParams(params);

                //回调finish

                if (i == end) {
                    state = NONE;
                    listener.onFinish();
                }
            });
        }
    }

    public void hideTop() {
        LinearLayout.LayoutParams params = (LayoutParams) headerContainer.getLayoutParams();
        params.topMargin = -200;
        headerContainer.setLayoutParams(params);
    }

    //params

    //初始化
    private boolean isInitialized = false;


    private int containerHeight;
    private int headerHeight;


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);

        if (!isInitialized) {
            isInitialized = true;

            //header

            containerHeight = headerContainer.getHeight();
            headerHeight = header.getHeight();

//            LinearLayout.LayoutParams params = (LayoutParams) headerContainer.getLayoutParams();
//            params.topMargin = -containerHeight;
//            headerContainer.setLayoutParams(params);

        }
    }

    //intercept

    private int downY;
    private int downX;
    boolean moveX = false;
    boolean moveY = false;
    public boolean intercept;
    //下拉距离
    int pullOffset;
    int downOffset;

    private Timer timer;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {//只需要做入口状态判断即可

        int y = (int) ev.getY();
        int x = (int) ev.getX();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(Constant.TAG, "onInterceptTouchEvent-ACTION_DOWN");
                //手指落点

                downY = y;
                downX = x;
                intercept = false;
                moveY = false;
                moveX = false;

                if (!isLongPress) {//按住不动的情况
                    timer = new Timer();
                    timer.schedule(new TimerTask() {//1秒后位长按事件 只要移动就取消长按事件
                        @Override
                        public void run() {
                            pageView.initSelect(ev.getX(), ev.getY());
                        }
                    }, 1000);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(Constant.TAG, "onInterceptTouchEvent-ACTION_MOVE");
                int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (Math.abs(downX - ev.getX()) > slop || Math.abs(downY - ev.getY()) > slop) {//移动 取消长按延时操作
                    cancelTimer();
                }
//                if (!isLongPress) {
//                    isLongPress = isLongPressed(downX, downY, ev.getX(), ev.getY(), ev.getDownTime(), ev.getEventTime(), 1000);
//                    Log.i("yzp", "isLongPress" + isLongPress);
//                }

                if (!moveX) {//横向滑动 且横向滑动大于纵向滑动
                    moveX = Math.abs(downX - ev.getX()) > slop && Math.abs(downX - ev.getX()) > Math.abs(downY - ev.getY());
                }
                if (!moveY) {//只有向下滑动满足条件
                    moveY = ev.getY() - downY > slop;
                }

                //拦截事件 下拉拦截
                if (!moveX && moveY && !isLongPress) {
                    intercept = true;
                    listener.onPre();
                    state = PULL_LESS_THAN_CHANGE_HEIGHT;
                } else {
                    intercept = false;
                    state = NONE;
                }

                Log.i(Constant.TAG, "onInterceptTouchEvent-ACTION_MOVE" + "=========" + intercept);
                break;
            case MotionEvent.ACTION_UP:
                cancelTimer();
                Log.i(Constant.TAG, "onInterceptTouchEvent-ACTION_UP");
                break;
        }

        return intercept;
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
    //touch

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        int x = (int) event.getX();
        pullOffset = 0;
        downOffset = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(Constant.TAG, "PullToRefreshLayout-onTouchEvent-ACTION_DOWN");
                downY = y;
                downX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(Constant.TAG, "PullToRefreshLayout-onTouchEvent-ACTION_MOVE");
                //根据下拉距离是否大于头布局的高度来设置当前的状态
                pullOffset = y - downY;
                if (pullOffset <= 0) return false;
                downOffset = (int) (pullOffset * 0.4);
                if (state == PULL_LESS_THAN_CHANGE_HEIGHT) {
                    if (downOffset > headerHeight) {
                        state = PULL_MORE_THAN_CHANGE_HEIGHT;
                        listener.onChange();
                    }
                }


                if (state == PULL_MORE_THAN_CHANGE_HEIGHT) {
                    if (downOffset <= headerHeight) {
                        state = PULL_LESS_THAN_CHANGE_HEIGHT;
                        listener.onRecover();
                    }
                }

                //执行下拉

                if (state == PULL_LESS_THAN_CHANGE_HEIGHT || state == PULL_MORE_THAN_CHANGE_HEIGHT) {
                    LinearLayout.LayoutParams params = (LayoutParams) headerContainer.getLayoutParams();
                    params.topMargin = -containerHeight + downOffset;
                    headerContainer.setLayoutParams(params);

                    listener.onPull(((float) downOffset) / headerHeight);
                    return true;
                }
//                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.i(Constant.TAG, "PullToRefreshLayout-onTouchEvent-ACTION_UP");
                //下拉距离

                pullOffset = y - downY;
                if (pullOffset < 0) pullOffset = 0;
                downOffset = (int) (pullOffset * 0.4);

                // 正常恢复

                int start = -containerHeight + downOffset;
                int end = -containerHeight;

                ValueAnimator animator = ValueAnimator.ofInt(start, end);
                animator.setDuration(500).start();
                animator.addUpdateListener(animation -> {
                    int i = (int) animation.getAnimatedValue();

                    LayoutParams params = (LayoutParams) headerContainer.getLayoutParams();
                    params.topMargin = i;
                    headerContainer.setLayoutParams(params);

                    //state back to NONE
                    if (i == end) {
                        listener.onRefresh();
                        state = NONE;
                    }
                });

        }

        return true;
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
}
