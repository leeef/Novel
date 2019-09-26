package com.bx.philosopher.ui.activity;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseMVPActivity;
import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.baseinterface.ItemSelectIml;
import com.bx.philosopher.model.bean.response.BookPackage;
import com.bx.philosopher.presenter.AllCategoryPresenter;
import com.bx.philosopher.presenter.imp.AllCategoryImp;
import com.bx.philosopher.ui.adapter.CategoryTypeAdapter;
import com.bx.philosopher.ui.adapter.view.ExploreExtraHolder;
import com.bx.philosopher.ui.fragment.tabfragment.CategoryBaseFragment;
import com.bx.philosopher.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AllCategoryActivity extends BaseMVPActivity<AllCategoryPresenter> implements AllCategoryImp.View {

    @BindView(R.id.all_category_list)
    RecyclerView all_category_list;

    private int select_category_type = 0;
    private List<Fragment> fragmentList = new ArrayList<>();

    private FragmentTransaction fragmentTransaction;
    CategoryTypeAdapter categoryTypeAdapter;

    @Override
    protected int getContentId() {
        return R.layout.activity_all_category;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);


        mPresenter.getData();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        all_category_list.setLayoutManager(new LinearLayoutManager(this));
        categoryTypeAdapter = new CategoryTypeAdapter(new ItemSelectIml() {
            @Override
            public int getSelectedPosition() {
                return select_category_type;
            }
        });
        categoryTypeAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(fragmentList.get(select_category_type));
                fragmentTransaction.show(fragmentList.get(pos)).commit();
                select_category_type = pos;
                categoryTypeAdapter.notifyDataSetChanged();
            }
        });
        all_category_list.setAdapter(categoryTypeAdapter);


    }

    @Override
    public void showError(String errMsg) {
        Log.i(Constant.TAG,errMsg);
    }

    @Override
    public void complete() {

    }

    @Override
    protected AllCategoryPresenter bindPresenter() {
        return new AllCategoryPresenter();
    }

    @Override
    public void showData(List<BookPackage> data) {
        categoryTypeAdapter.addItems(data);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (BookPackage bookPackage : data) {
            bookPackage.setListType(ExploreExtraHolder.BOOK_ITEM1);
            bookPackage.setListShowType("all_catalog");
            CategoryBaseFragment categoryBaseFragment = new CategoryBaseFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("books", (ArrayList<? extends Parcelable>) bookPackage.getList());
            categoryBaseFragment.setArguments(bundle);
            fragmentList.add(categoryBaseFragment);
        }
        for (Fragment fragment : fragmentList) {
            fragmentTransaction.add(R.id.category_content_frame, fragment);
        }
        for (Fragment fragment : fragmentList) {
            fragmentTransaction.hide(fragment);
        }
        fragmentTransaction.show(fragmentList.get(0));
        fragmentTransaction.commit();
    }
}
