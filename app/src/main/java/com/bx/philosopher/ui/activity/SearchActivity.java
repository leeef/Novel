package com.bx.philosopher.ui.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseMVPActivity;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.SearchPresenter;
import com.bx.philosopher.presenter.imp.SearchIml;
import com.bx.philosopher.ui.adapter.ExploreExtraAdapter;
import com.bx.philosopher.ui.adapter.view.ExploreExtraHolder;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.SharedPreUtils;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.widget.FlowLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;

public class SearchActivity extends BaseMVPActivity<SearchPresenter> implements SearchIml.View {


    @BindView(R.id.search_list)
    FlowLayout search_list;
    @BindView(R.id.search_content)
    LinearLayout search_content;
    @BindView(R.id.empty_view)
    RelativeLayout empty_view;
    @BindView(R.id.text_sign)
    TextView text_sign;
    @BindView(R.id.library_search)
    EditText library_search;
    @BindView(R.id.search_result)
    RecyclerView search_result;
    @BindView(R.id.cancel)
    TextView cancel;


    private ExploreExtraAdapter exploreExtraAdapter;
    private Disposable subscribe;

    @Override
    protected int getContentId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        initChildViews();

        exploreExtraAdapter = new ExploreExtraAdapter();
        search_result.setLayoutManager(new LinearLayoutManager(this));
        search_result.setAdapter(exploreExtraAdapter);
        exploreExtraAdapter.setOnItemClickListener((view, pos) -> {
            if (exploreExtraAdapter.getItem(pos) instanceof BookBean) {
                BookBean bookBean = (BookBean) exploreExtraAdapter.getItem(pos);
                BookDetailActivity.startActivity(SearchActivity.this, BookDetailActivity.TYPE_LIBRARY, bookBean.getId());
            }
        });
    }

    private void setEmptyView() {
        search_content.setVisibility(View.GONE);
        empty_view.setVisibility(View.VISIBLE);
        text_sign.setText(getResources().getString(R.string.search_empty_sign));
    }

    @Override
    protected void initClick() {
        super.initClick();
        library_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("SearchPresenter", s.toString() + "=====");
                cancelSearch();
                startSearch(s.toString());

            }
        });
        cancel.setOnClickListener(v -> SearchActivity.this.finish());
    }

    //搜索监听 延迟500毫秒搜索
    private void startSearch(String content) {
        if (subscribe == null || subscribe.isDisposed()) {
            subscribe = Observable.create((ObservableOnSubscribe<String>) e -> {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        e.onNext(content);
                    }
                }, 500);
            }).compose(RxScheduler.Obs_io_main())
                    .subscribe(s -> {
                        if (StringUtils.isTrimEmpty(s)) {
                            //显示搜索记录
                            search_content.setVisibility(View.VISIBLE);
                            empty_view.setVisibility(View.GONE);
                            search_result.setVisibility(View.GONE);
                            initChildViews();
                        } else {
                            mPresenter.startSearch(s);
                        }
                    });
            addDisposable(subscribe);
        }
    }

    private void cancelSearch() {
        if (subscribe != null)
            subscribe.dispose();
    }

    private void initChildViews() {
        if (search_list.getChildCount() != 0) search_list.removeAllViews();
        String history = SharedPreUtils.getInstance().getString(Constant.SEARCHKEY);
        if (StringUtils.isTrimEmpty(history)) history = new JsonArray().toString();//空数据

        JsonArray jsonArray = new JsonParser().parse(history).getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            String content = jsonArray.get(i).getAsString();
            View view = getLayoutInflater().inflate(R.layout.search_history_item, null);
            TextView search_record_count = view.findViewById(R.id.search_record_count);
            TextView search_record_content = view.findViewById(R.id.search_record_content);
//
            search_record_count.setText((i + 1) + "");
            search_record_content.setText(content);
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
            view.setOnClickListener(v -> {
                library_search.setText(content);
                library_search.setSelection(content.length());
            });
            search_list.addView(view);
        }


    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void complete() {

    }

    @Override
    protected SearchPresenter bindPresenter() {
        return new SearchPresenter();
    }

    @Override
    public void showResult(List<BaseBean> result) {
        if (result.size() == 0) {
            setEmptyView();
        } else {
            search_content.setVisibility(View.GONE);
            empty_view.setVisibility(View.GONE);
            search_result.setVisibility(View.VISIBLE);
            for (BaseBean baseBean : result) {
                baseBean.setViewType(ExploreExtraHolder.BOOK_ITEM3);
            }
            exploreExtraAdapter.refreshItems(result);
        }
    }
}
