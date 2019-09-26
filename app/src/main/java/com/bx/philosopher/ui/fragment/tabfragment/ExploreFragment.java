package com.bx.philosopher.ui.fragment.tabfragment;

import android.Manifest;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.bx.philosopher.R;
import com.bx.philosopher.base.fragment.BaseMVPFragment;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.model.bean.response.BookPackage;
import com.bx.philosopher.model.bean.response.ExploreBanner;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.ExplorePresenter;
import com.bx.philosopher.presenter.imp.ExploreImp;
import com.bx.philosopher.ui.activity.BookDetailActivity;
import com.bx.philosopher.ui.activity.SpecialTypeBookActivity;
import com.bx.philosopher.ui.adapter.ExploreExtraAdapter;
import com.bx.philosopher.ui.adapter.MyExploreViewAdapter;
import com.bx.philosopher.ui.adapter.view.ExploreExtraHolder;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.LoadingUtil;
import com.bx.philosopher.utils.PermissionUtils;
import com.bx.philosopher.utils.ScreenUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.widget.RecycleViewSpan;
import com.bx.philosopher.widget.adapter.WholeAdapter;
import com.bx.philosopher.widget.cardslideview.CardViewPager;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import pub.devrel.easypermissions.EasyPermissions;

public class ExploreFragment extends BaseMVPFragment<ExplorePresenter> implements ExploreImp.View, EasyPermissions.PermissionCallbacks {

    @BindView(R.id.explore_extra)
    RecyclerView explore_extra;
    @BindView(R.id.explore_refresh)
    SwipeRefreshLayout explore_refresh;

    private List<String> data = new ArrayList<>();
    ExploreExtraAdapter exploreExtraAdapter;


    private HeadView headView;
    private Disposable cardSlideDisposable;

    private static final int LOCATION_REQUEST = 2;

    //定位权限
    private String[] location_permission = {Manifest.permission.ACCESS_COARSE_LOCATION
            , Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.CAMERA};

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initData() {
        int index = 0;
        while (true) {
            if (index == 4) break;
            data.add(index + "");
            index++;
        }

    }


    @Override
    protected ExplorePresenter bindPresenter() {
        return new ExplorePresenter();
    }

    @Override
    protected void initViewData(View v) {


    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_explore;
    }

    @Override
    protected void onFirstUserVisible() {

        checkLocatePermission();
        headView = new HeadView();

        exploreExtraAdapter = new ExploreExtraAdapter();
        exploreExtraAdapter.addHeaderView(headView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 6);
        //设置每个item所占的列数
        gridLayoutManager.setSpanSizeLookup(new RecycleViewSpan(exploreExtraAdapter));
        explore_extra.setLayoutManager(gridLayoutManager);

        explore_extra.setAdapter(exploreExtraAdapter);


        exploreExtraAdapter.setOnItemClickListener((view, pos) -> {
            int viewType = exploreExtraAdapter.getItem(pos).getViewType();
            //书籍类
            switch (viewType) {
                case ExploreExtraHolder.BOOK_ITEM1:
                case ExploreExtraHolder.BOOK_ITEM2:
                case ExploreExtraHolder.BOOK_ITEM3:
                    if (exploreExtraAdapter.getItem(pos) instanceof BookBean) {
                        BookBean bookBean = (BookBean) exploreExtraAdapter.getItem(pos);
                        BookDetailActivity.startActivity(getContext(), BookDetailActivity.TYPE_EXPLORE, bookBean.getId());
                    }

                    break;
                case ExploreExtraHolder.IMAGE_TWO:
                    if (exploreExtraAdapter.getItem(pos) instanceof BookBean) {
                        BookBean bookBean = (BookBean) exploreExtraAdapter.getItem(pos);
                        if (bookBean.getTitle().equals(Constant.EXPLORE_RANK_NAME)) {
                            SpecialTypeBookActivity.startActivity(getContext(), true, bookBean);
                        }
                    }
                    break;
                case ExploreExtraHolder.BOOK_TAG:
                    if (exploreExtraAdapter.getItem(pos) instanceof BookPackage) {
                        BookPackage bookPackage = (BookPackage) exploreExtraAdapter.getItem(pos);
                        if (bookPackage.getName().equals(Constant.EXPLORE_RANK_NAME)) return;
                        SpecialTypeBookActivity.startActivity(getContext(), bookPackage, BookDetailActivity.TYPE_EXPLORE);
                    }
                    break;
            }

        });
        explore_refresh.setEnabled(false);
        explore_refresh.setOnRefreshListener(() -> explore_refresh.setRefreshing(false));

        LoadingUtil.show(getContext());
        mPresenter.getData();
    }


    //检查定位
    private void checkLocatePermission() {
        if (!EasyPermissions.hasPermissions(getActivity(), location_permission)) {
//            startLocation();
//        } else {
            PermissionUtils.requestPermissions(getActivity(), "need to get the relevant permissions", LOCATION_REQUEST, location_permission);
        }
    }

    @Override
    protected void onUserVisible() {
    }

    @Override
    protected void onFirstUserInvisible() {

    }

    @Override
    protected void onUserInvisible() {

    }


    @Override
    public void initImmersionBar() {

        ImmersionBar.with(this)
                .statusBarColor(R.color.white)
                .autoDarkModeEnable(true)
                .fitsSystemWindows(true)
                .init();
    }


    @Override
    public void showError(String errMsg) {
        Log.i(Constant.TAG, errMsg);
        LoadingUtil.hide();
        ToastUtils.show(errMsg);
    }

    @Override
    public void complete() {

    }

    @Override
    public void refreshBanner(ExploreBanner exploreBanner) {
        headView.init(exploreBanner);
    }

    @Override
    public void refreshList(List<BaseBean> data) {
        LoadingUtil.hide();
        exploreExtraAdapter.addItems(data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }


    class HeadView implements WholeAdapter.ItemView {
        @BindView(R.id.explore_view)
        CardViewPager explore_view;
        Unbinder unbinder;

        @Override
        public View onCreateView(ViewGroup parent) {
            View headView = LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_head_view, parent, false);
            return headView;
        }

        @Override
        public void onBindView(View view) {
            if (unbinder == null) {
                unbinder = ButterKnife.bind(this, view);
            }

            explore_view.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {
                }

                @Override
                public void onPageSelected(int i) {

                }

                @Override
                public void onPageScrollStateChanged(int i) {
                    //避免卡片左右滑动和下拉刷新冲突
                    switch (i) {
                        case ViewPager.SCROLL_STATE_DRAGGING://点击，滑屏
                            explore_refresh.setEnabled(false);
                            cancelBanner();
                            startBanner();
                            break;
                        case ViewPager.SCROLL_STATE_SETTLING://释放
                            break;
                        case ViewPager.SCROLL_STATE_IDLE://无动作、初始状态
                            break;
                    }
                }
            });


        }

        public int getIndex() {
            if (explore_view != null)
                return explore_view.getCurrentIndex();
            else return 0;
        }


        public void setIndex(int index) {
            explore_view.setCurrentIndex(index, false);
        }

        public void init(ExploreBanner exploreBanner) {
            explore_view.setCardTransformer(0, 0);
            explore_view.setCardPadding(ScreenUtils.dpToPx(5));
            explore_view.setCardMargin(0);
            explore_view.bind(getChildFragmentManager(), new MyExploreViewAdapter(exploreBanner), data);
            startBanner();
        }

        //开始轮播
        void startBanner() {
            if (cardSlideDisposable == null || cardSlideDisposable.isDisposed()) {
                explore_refresh.setEnabled(false);
//            //卡片轮播
                cardSlideDisposable = Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
                    Timer timer = new Timer();

                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            emitter.onNext(headView.getIndex() + 1);
                        }
                    }, 5000, 5000);//5s后执行 之后没5s执行一次
                }).compose(RxScheduler.Obs_io_main())
                        .subscribe(integer -> headView.setIndex(integer));
                addDisposable(cardSlideDisposable);
            }
        }


        //取消轮播
        void cancelBanner() {
            if (cardSlideDisposable != null)
                cardSlideDisposable.dispose();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (headView.unbinder != null) {
            headView.unbinder.unbind();
        }
    }
}
