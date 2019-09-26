package com.bx.philosopher.ui.adapter.view;

import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.adapter.ViewHolderImpl;

public class BookCatalogHolder extends ViewHolderImpl<String> {

    private TextView book_chapter;
    private TextView book_chapter_name;


    @Override
    protected int getItemLayoutId() {
        return R.layout.book_catalog_item;
    }

    @Override
    public void initView() {
        book_chapter = findById(R.id.book_chapter);
        book_chapter_name = findById(R.id.book_chapter_name);
    }

    @Override
    public void onBind(String data, int pos) {

        book_chapter.setTextColor(getContext().getResources().getColor(R.color.color_999999));
//        book_chapter_name.setTextColor(getContext().getResources().getColor(R.color.color_999999));

        book_chapter.setText(data);
//        book_chapter_name.setText(data.getTitle());
    }

    public void setCurrentChapter() {
        book_chapter.setTextColor(getContext().getResources().getColor(R.color.color_65c933));
        book_chapter_name.setTextColor(getContext().getResources().getColor(R.color.color_65c933));
    }

}
