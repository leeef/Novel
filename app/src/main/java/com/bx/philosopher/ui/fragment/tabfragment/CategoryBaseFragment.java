package com.bx.philosopher.ui.fragment.tabfragment;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.bx.philosopher.R;
import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.fragment.BaseLazyFragment;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.ui.activity.BookDetailActivity;
import com.bx.philosopher.ui.adapter.ExploreExtraAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CategoryBaseFragment extends BaseLazyFragment {
    @BindView(R.id.category_content)
    RecyclerView category_content;

    private List<BaseBean> books = new ArrayList<>();

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            books = bundle.getParcelableArrayList("books");
        }

    }

    @Override
    protected void initData() {

    }


    @Override
    protected void initViewData(View v) {
        category_content.setLayoutManager(new GridLayoutManager(getContext(), 2));

        ExploreExtraAdapter exploreExtraAdapter = new ExploreExtraAdapter();
        exploreExtraAdapter.addItems(books);
        category_content.setAdapter(exploreExtraAdapter);
        exploreExtraAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (exploreExtraAdapter.getItem(pos) instanceof BookBean) {
                    BookBean bookBean = (BookBean) exploreExtraAdapter.getItem(pos);
                    BookDetailActivity.startActivity(getContext(), BookDetailActivity.TYPE_LIBRARY, bookBean.getId());
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.category_content_fragment;
    }

    @Override
    protected void onFirstUserVisible() {
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
    protected void onBindPresenter() {

    }

    @Override
    public void initImmersionBar() {

    }
}
