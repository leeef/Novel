package com.bx.philosopher.ui.fragment.tabfragment;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.fragment.BaseMVPFragment;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.presenter.BookShelfPresenter;
import com.bx.philosopher.presenter.imp.BookShelfImp;
import com.bx.philosopher.ui.activity.AllCategoryActivity;
import com.bx.philosopher.ui.activity.BookDetailActivity;
import com.bx.philosopher.ui.activity.MainActivity;
import com.bx.philosopher.ui.activity.ReadingActivity;
import com.bx.philosopher.ui.activity.SearchActivity;
import com.bx.philosopher.ui.adapter.BookShelfAdapter;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.widget.MyAlertDialog;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class BookshelfFragment extends BaseMVPFragment<BookShelfPresenter> implements View.OnClickListener, BookShelfImp.View {
    @BindView(R.id.book_shelf)
    RecyclerView bookshelf;
    @BindView(R.id.book_shelf_delete_layout)
    LinearLayout book_shelf_delete_layout;
    @BindView(R.id.book_shelf_delete)
    TextView book_shelf_delete;
    @BindView(R.id.book_shelf_top)
    RelativeLayout book_shelf_top;
    @BindView(R.id.book_shelf_top_edit)
    RelativeLayout book_shelf_top_edit;
    @BindView(R.id.book_shelf_cancel_edit)
    ImageView book_shelf_cancel_edit;
    @BindView(R.id.book_edit_all)
    TextView book_edit_all;


    private BookShelfAdapter bookShelfAdapter;


    private List<BookBean> selectData = new ArrayList<>();//删除选中的书籍


    private void initAdapter() {

        bookshelf.setLayoutManager(new GridLayoutManager(getContext(), 3));
        bookShelfAdapter = new BookShelfAdapter((position, isSelect) -> {
//                if (bookShelfAdapter.getItem(position).getTitle().equals("addImage")) return;
            //图片被点击
            if (isSelect) {
                selectData.add(bookShelfAdapter.getItem(position));
            } else {
                selectData.remove(bookShelfAdapter.getItem(position));
            }
            setDeleteButton();
        });
        bookShelfAdapter.setOnItemClickListener((view, pos) -> {
            BookBean bookBean = bookShelfAdapter.getItem(pos);
            if (bookBean.getTitle().equals("addImage")) {
                startActivity(new Intent(getContext(), AllCategoryActivity.class));
            }else {
                String from_type = bookBean.getTypeid() == 1 ? BookDetailActivity.TYPE_EXPLORE : BookDetailActivity.TYPE_LIBRARY;
                ReadingActivity.startActivity(getContext(), bookBean.getId(), from_type, bookBean.getTitle());
            }
        });
        bookshelf.setAdapter(bookShelfAdapter);
    }


    public void setTopLayout() {
        if (bookShelfAdapter.getIsLongClick()) {
            book_shelf_delete_layout.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).hideTab();
            book_shelf_top.setVisibility(View.GONE);
            book_shelf_top_edit.setVisibility(View.VISIBLE);
        } else {
            book_shelf_delete_layout.setVisibility(View.GONE);
            ((MainActivity) getActivity()).showTab();
            book_shelf_top.setVisibility(View.VISIBLE);
            book_shelf_top_edit.setVisibility(View.GONE);
        }
    }

    public void setDeleteButton() {
        if (selectData.size() > 0) {
            book_shelf_delete.setTextColor(StringUtils.getColor(R.color.color_65c933));
            Drawable drawable = StringUtils.getDrawable(R.drawable.book_delete);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            book_shelf_delete.setCompoundDrawables(drawable, null, null, null);
        } else {
            Drawable drawable = StringUtils.getDrawable(R.drawable.book_delete).mutate();
            drawable.setColorFilter(StringUtils.getColor(R.color.color_999999), PorterDuff.Mode.SRC_ATOP);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            book_shelf_delete.setTextColor(StringUtils.getColor(R.color.color_999999));
            book_shelf_delete.setCompoundDrawables(drawable, null, null, null);
        }
    }

    @OnClick(R.id.search)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.book_shelf_delete_layout:
                if (selectData.size() == 0) return;
                MyAlertDialog.create(getContext(), "TIPS", "Confirm delete?", "confirm",
                        v1 -> mPresenter.delete(selectData), "cancel", null,
                        true, false, false).show();
                break;
            case R.id.book_shelf_cancel_edit:
                recovery();
                break;
            case R.id.book_edit_all:
                bookShelfAdapter.setSelectAll(true);
                selectData.clear();
                for (int i = 0; i < bookShelfAdapter.getItems().size(); i++) {
                    if (bookShelfAdapter.getItem(i).getTitle().equals("addImage")) continue;
                    selectData.add(bookShelfAdapter.getItem(i));
                }
                setDeleteButton();
                break;
            case R.id.search:
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (bookShelfAdapter != null && bookShelfAdapter.getIsLongClick()) {
            recovery();
            return true;
        }
        return false;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected BookShelfPresenter bindPresenter() {
        return new BookShelfPresenter();
    }

    @Override
    protected void initViewData(View v) {
        initAdapter();
        initClick();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_book_shelf;
    }

    @Override
    protected void onFirstUserVisible() {

        mPresenter.getData();
    }

    @Override
    protected void onUserVisible() {
        mPresenter.getData();
    }

    @Override
    protected void onFirstUserInvisible() {
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getData();
    }

    public void initClick() {
        bookShelfAdapter.setOnItemLongClickListener((view, pos) -> {
            if (!bookShelfAdapter.getItems().get(pos).equals("addImage")) {
                bookShelfAdapter.setLongClick(true);
                setTopLayout();
            }
            return true;
        });
        book_shelf_delete_layout.setOnClickListener(this);
        book_shelf_cancel_edit.setOnClickListener(this);
        book_edit_all.setOnClickListener(this);
    }

    @Override
    protected void onUserInvisible() {

    }

    @Override
    public void initImmersionBar() {

        ImmersionBar.with(this).reset()
                .statusBarColor(R.color.white)
                .autoDarkModeEnable(true)
                .fitsSystemWindows(true)
                .init();
    }

    @Override
    public void showError(String errMsg) {
        Log.i(Constant.TAG, errMsg);
    }

    @Override
    public void complete() {

    }

    @Override
    public void refresh(List<BookBean> data) {
        BookBean bookBean = new BookBean(1);
        bookBean.setTitle("addImage");
        data.add(data.size(), bookBean);
        bookShelfAdapter.refreshItems(data);
        recovery();
    }

    //返回长按前初始状态
    void recovery() {
        bookShelfAdapter.setSelectAll(false);
        bookShelfAdapter.setLongClick(false);
        selectData.clear();
        setDeleteButton();
        setTopLayout();
    }

    @Override
    public void deleteDone(int resultCode) {
        if (resultCode != 0) {//删除成功
            bookShelfAdapter.removeItems(selectData);
            recovery();
        } else {
            ToastUtils.show("The book is no longer on the shelf");
            mPresenter.getData();
        }
    }
}
