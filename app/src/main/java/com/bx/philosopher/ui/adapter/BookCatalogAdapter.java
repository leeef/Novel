package com.bx.philosopher.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.base.adapter.BaseViewHolder;
import com.bx.philosopher.base.adapter.IViewHolder;
import com.bx.philosopher.ui.adapter.view.BookCatalogHolder;

public class BookCatalogAdapter extends BaseListAdapter<String> {

    private int CurrentChapter = -1;

    @Override
    protected IViewHolder<String> createViewHolder(int viewType) {
        return new BookCatalogHolder();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        IViewHolder iHolder = ((BaseViewHolder) holder).holder;
        BookCatalogHolder bookCatalogHolder = (BookCatalogHolder) iHolder;
        if (CurrentChapter == position) {
            bookCatalogHolder.setCurrentChapter();
        }
    }

    public int getCurrentChapter() {
        return CurrentChapter;
    }

    public void setCurrentChapter(int currentChapter) {
        CurrentChapter = currentChapter;

        notifyDataSetChanged();
    }

}
