package com.bx.philosopher.base.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gyf.immersionbar.components.SimpleImmersionFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * @ClassName: BaseLazyFragment
 * @Description: fragment基类 SimpleImmersionFragment用来设置状态栏
 * @Author: leeeef
 * @CreateDate: 2019/5/17 15:31
 * @Version: 1.0
 */

public abstract class BaseLazyFragment extends SimpleImmersionFragment {
    View v;

    private Unbinder unbinder;
    /**
     * 第一次可见的标识
     */
    private boolean isFirstVisible = true;
    /**
     * 第一次不可见标识
     */
    private boolean isFirstInvisible = true;
    /**
     * 数据是否加载完成，由具体的子类来维护
     */
    protected boolean isLoaded = false;
    /**
     * 视图创建完成标识
     */
    private boolean isPrepared;

    /**
     * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
     * FragmentActivity捕捉到物理返回键点击事件后会首先询问Fragment是否消费该事件
     * 如果没有Fragment消息时FragmentActivity自己才会消费该事件
     */
    public abstract boolean onBackPressed();

    protected CompositeDisposable mDisposable;

    protected void addDisposable(Disposable d) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(d);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 我们只在视图创建完成后，调用视图是否可见的方法
        if (isPrepared) {
            if (isVisibleToUser) {
                isFirstVisible();
            } else {
                isFirstInvisible();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getLayoutId() != 0) {
            v = inflater.inflate(getLayoutId(), container, false);
        } else {
            v = super.onCreateView(inflater, container, savedInstanceState);
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, v);
        onBindPresenter();
        initViewData(v);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 视图准备完成
        isPrepared = true;
        // 视图是否可见
        //isVisibleToUser();
        // 初始化视图数据
        if (isLoaded()) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //if (getUserVisibleHint()) {
        //onUserVisible();
        //}
        isVisibleToUser();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserInvisible();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unbinder.unbind();
        if (mDisposable != null) {
            mDisposable.clear();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 视图销毁后将isPrepared重置
        isPrepared = false;
    }

    private void isVisibleToUser() {
        if (getUserVisibleHint()) {
            isFirstVisible();
        }
    }

    private void isFirstVisible() {
        if (isFirstVisible) {
            isFirstVisible = false;
            onFirstUserVisible();
        } else {
            onUserVisible();
        }
    }

    private void isFirstInvisible() {
        if (isFirstInvisible) {
            isFirstInvisible = false;
            onFirstUserInvisible();
        } else {
            onUserInvisible();
        }
    }

    private boolean isLoaded() {
        return isLoaded;
    }

    public void loaded() {
        isLoaded = true;
    }

    /**
     * initialized data but view is not initialized
     */
    protected abstract void initData();

    /**
     * when view created for the first time,we can set data on the View
     */
    protected abstract void initViewData(View v);

    /**
     * get LayoutId
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * when fragment is visible for the first time, here we can do something Time-consuming operation like network access
     */
    protected abstract void onFirstUserVisible();

    /**
     * this method like the fragment's lifecycle method onResume()
     */
    protected abstract void onUserVisible();

    /**
     * when fragment is invisible for the first time
     */
    protected abstract void onFirstUserInvisible();

    /**
     * this method like the fragment's lifecycle method onPause()
     */
    protected abstract void onUserInvisible();


    protected abstract void onBindPresenter();

}