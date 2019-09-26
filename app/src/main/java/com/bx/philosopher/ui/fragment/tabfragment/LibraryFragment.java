package com.bx.philosopher.ui.fragment.tabfragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.fragment.BaseMVPFragment;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.model.bean.response.BookPackage;
import com.bx.philosopher.presenter.LibraryPresenter;
import com.bx.philosopher.presenter.imp.LibraryImp;
import com.bx.philosopher.ui.activity.AllCategoryActivity;
import com.bx.philosopher.ui.activity.BookDetailActivity;
import com.bx.philosopher.ui.activity.MainActivity;
import com.bx.philosopher.ui.activity.SearchActivity;
import com.bx.philosopher.ui.activity.SpecialTypeBookActivity;
import com.bx.philosopher.ui.adapter.ExploreExtraAdapter;
import com.bx.philosopher.ui.adapter.view.ExploreExtraHolder;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.LoadingUtil;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.widget.BannerImageLoader;
import com.bx.philosopher.widget.RecycleViewSpan;
import com.google.android.material.appbar.AppBarLayout;
import com.gyf.immersionbar.ImmersionBar;
import com.youth.banner.Banner;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class LibraryFragment extends BaseMVPFragment<LibraryPresenter> implements View.OnClickListener, LibraryImp.View {

    @BindView(R.id.library_list)
    RecyclerView library_list;
    @BindView(R.id.detail_toolbar)
    Toolbar detail_toolbar;
    @BindView(R.id.app_bar)
    AppBarLayout app_bar;
    @BindView(R.id.library_top_search)
    LinearLayout library_top_search;
    @BindView(R.id.library_search)
    EditText library_search;
    @BindView(R.id.library_all_category)
    ImageView library_all_category;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.whole)
    LinearLayout whole;
    @BindView(R.id.literature)
    LinearLayout literature;
    @BindView(R.id.biography)
    LinearLayout biography;
    @BindView(R.id.history)
    LinearLayout history;
    @BindView(R.id.top1)
    TextView top1;
    @BindView(R.id.top2)
    TextView top2;
    @BindView(R.id.top3)
    TextView top3;

    private ExploreExtraAdapter exploreExtraAdapter;

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initData() {

    }


    @Override
    protected LibraryPresenter bindPresenter() {
        return new LibraryPresenter();
    }

    @Override
    protected void initViewData(View v) {
        ((MainActivity) getActivity()).hideSoftInput();
        exploreExtraAdapter = new ExploreExtraAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 6);
        gridLayoutManager.setSpanSizeLookup(new RecycleViewSpan(exploreExtraAdapter));
        library_list.setLayoutManager(gridLayoutManager);
        library_list.setAdapter(exploreExtraAdapter);

        exploreExtraAdapter.setOnItemClickListener((view, pos) -> {
            int viewType = exploreExtraAdapter.getItem(pos).getViewType();
            switch (viewType) {
                case ExploreExtraHolder.BOOK_ITEM1:
                case ExploreExtraHolder.BOOK_ITEM2:
                case ExploreExtraHolder.BOOK_ITEM3:
                    if (exploreExtraAdapter.getItem(pos) instanceof BookBean) {
                        BookBean bookBean = (BookBean) exploreExtraAdapter.getItem(pos);
                        BookDetailActivity.startActivity(getContext(), BookDetailActivity.TYPE_LIBRARY, bookBean.getId());
                    }
                    break;
                case ExploreExtraHolder.BOOK_ITEM4:
                    if (exploreExtraAdapter.getItem(pos) instanceof BookPackage) {
                        BookPackage bookPackage = (BookPackage) exploreExtraAdapter.getItem(pos);
                        SpecialTypeBookActivity.startActivity(getContext(), bookPackage.getName(), bookPackage.getId());
                    }
                    break;
                case ExploreExtraHolder.BOOK_TAG:
                    if (exploreExtraAdapter.getItem(pos) instanceof BookPackage) {
                        BookPackage bookPackage = (BookPackage) exploreExtraAdapter.getItem(pos);
                        if (bookPackage.getName().equals("Recommended")) {
                            startActivity(new Intent(getContext(), AllCategoryActivity.class));
                        } else {
                            SpecialTypeBookActivity.startActivity(getContext(), bookPackage, BookDetailActivity.TYPE_LIBRARY);
                        }

                    }
                    break;
            }

        });

//        library_list.setHasFixedSize(true);
        library_list.setNestedScrollingEnabled(false);

        app_bar.addOnOffsetChangedListener((appBarLayout, i) -> {
            if (i == 0) {//展开
                library_search.setBackground(StringUtils.getDrawable(R.drawable.library_search_bg));
                library_search.setHintTextColor(StringUtils.getColor(R.color.color_cccccc));
                library_all_category.setImageDrawable(StringUtils.getDrawable(R.drawable.library_catalog));
            } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {//折叠
                library_search.setBackground(StringUtils.getDrawable(R.drawable.search_bg));
                library_search.setHintTextColor(StringUtils.getColor(R.color.color_999999));
                Drawable drawable = StringUtils.getDrawable(R.drawable.library_catalog).mutate();
                drawable.setColorFilter(StringUtils.getColor(R.color.color_999999), PorterDuff.Mode.SRC_ATOP);
                library_all_category.setImageDrawable(drawable);

            }
        });
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_library;
    }

    @Override
    protected void onFirstUserVisible() {
        LoadingUtil.show(getContext());
        mPresenter.getData();
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
                .titleBar(detail_toolbar)
                .statusBarColor(R.color.transparent)
                .statusBarDarkFont(true, 0.2f)
                .init();
    }

    @OnClick({R.id.library_search, R.id.library_all_category, R.id.whole,})
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.library_search:
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;
            case R.id.library_all_category:
            case R.id.whole:
                startActivity(new Intent(getContext(), AllCategoryActivity.class));
                break;
        }
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
    public void showBanner(List<String> pics) {

        banner.setImages(pics).setImageLoader(new BannerImageLoader()).setDelayTime(5000).start();
    }

    @Override
    public void showList(List<BaseBean> libraryList) {
        LoadingUtil.hide();
        exploreExtraAdapter.addItems(libraryList);
    }

    @Override
    public void showTopList(List<BookBean> list) {
        top1.setText(list.get(0).getName());
        top2.setText(list.get(1).getName());
        top3.setText(list.get(2).getName());
        literature.setOnClickListener(v ->
                SpecialTypeBookActivity.startActivityFromLibrary(getContext(), list.get(0).getId(), list.get(0).getName()));
        biography.setOnClickListener(v ->
                SpecialTypeBookActivity.startActivityFromLibrary(getContext(), list.get(1).getId(), list.get(1).getName()));
        history.setOnClickListener(v ->
                SpecialTypeBookActivity.startActivityFromLibrary(getContext(), list.get(2).getId(), list.get(2).getName()));
    }
}
