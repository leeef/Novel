package com.bx.philosopher.ui.adapter.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.adapter.ViewHolderImpl;
import com.bx.philosopher.base.baseinterface.BaseClickListener;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BookMark;
import com.bx.philosopher.model.bean.response.BookMarkPackage;
import com.bx.philosopher.utils.StringUtils;

public class BookNoteHolder extends ViewHolderImpl<BaseBean> {

    private LinearLayout delete_button;
    private LinearLayout book_note_container;
    private TextView note_id;
    private TextView note_chapter;
    private TextView book_note_content;
    private TextView book_underline_content;
    private ImageView book_type_image;


    private BaseClickListener baseClickListener;
    private int note_type;

    public BookNoteHolder(BaseClickListener baseClickListener, int note_type) {
        this.baseClickListener = baseClickListener;
        this.note_type = note_type;
    }

    @Override
    protected int getItemLayoutId() {
        switch (note_type) {
            case 1:
                return R.layout.note_head_view;
        }
        return R.layout.book_note_item;
    }

    @Override
    public void initView() {
        switch (note_type) {
            case 1:
                note_id = findById(R.id.note_id);
                note_chapter = findById(R.id.note_chapter);
                break;
            case 0:
                delete_button = findById(R.id.delete_button);
                book_underline_content = findById(R.id.book_underline_content);
                book_type_image = findById(R.id.book_type_image);


                book_note_container = findById(R.id.book_note_container);
                book_note_content = findById(R.id.book_note_content);
                break;
        }
    }

    @Override
    public void onBind(BaseBean data, int pos) {
        if (data instanceof BookMark) {
            BookMark bookMark = (BookMark) data;
            if (bookMark.getSite() == null) {
                if (bookMark.getNote() == null) {//下划线
                    book_type_image.setImageDrawable(StringUtils.getDrawable(R.drawable.type_underline));
                } else {//笔记
                    book_type_image.setVisibility(View.INVISIBLE);
                    book_note_container.setVisibility(View.VISIBLE);
                    book_note_content.setText(bookMark.getNote());
                }
            }
            book_underline_content.setText(bookMark.getContent());
            delete_button.setOnClickListener(v -> baseClickListener.onClick(pos));
        } else if (data instanceof BookMarkPackage) {
            BookMarkPackage bookMarkPackage = (BookMarkPackage) data;
            note_id.setText("Note:" + bookMarkPackage.getIndex());
            note_chapter.setText(bookMarkPackage.getChapter());
        }


    }
}
