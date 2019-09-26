package com.bx.philosopher.ui.adapter.view;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bx.philosopher.R;
import com.bx.philosopher.base.adapter.ViewHolderImpl;
import com.bx.philosopher.base.baseinterface.BaseClickListener;
import com.bx.philosopher.model.bean.response.BookBean;

public class MyDownloadHolder extends ViewHolderImpl<BookBean> {
    private LinearLayout delete_button;
    private TextView book_name;
    private TextView book_author;
    private TextView book_introduce;
    private ImageView book_cover;

    private BaseClickListener baseClickListener;


    private RelativeLayout book_info;
    private AppCompatTextView book_info_name;
    private AppCompatTextView book_info_author;


    public MyDownloadHolder(BaseClickListener baseClickListener) {
        this.baseClickListener = baseClickListener;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.my_download_item;
    }

    @Override
    public void initView() {
        delete_button = findById(R.id.delete_button);
        book_name = findById(R.id.book_name);
        book_author = findById(R.id.book_author);
        book_introduce = findById(R.id.book_introduce);
        book_cover = findById(R.id.book_cover);
        book_info = findById(R.id.book_info);
        book_info_name = findById(R.id.book_info_name);
        book_info_author = findById(R.id.book_info_author);

    }

    @Override
    public void onBind(BookBean data, int pos) {
        book_name.setText(data.getTitle());
        book_author.setText(data.getAuthor());
        book_introduce.setText(data.getBrief());
        delete_button.setOnClickListener(v -> baseClickListener.onClick(pos));

        Glide.with(getContext())
                .asDrawable()
                .load(data.getCover())
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //绘制图书封面内容
                        if (data.getBoardid() == 2) {
                            book_cover.post(() -> {
                                int width = book_cover.getWidth();
                                int height = book_cover.getHeight();
                                RelativeLayout.LayoutParams bookInfoParam = new RelativeLayout.LayoutParams(width, height);
                                book_info.setLayoutParams(bookInfoParam);
                                RelativeLayout.LayoutParams bookInfoNameParam = (RelativeLayout.LayoutParams) book_info_name.getLayoutParams();
                                bookInfoNameParam.height = width * 2 / 5;
                                book_info_name.setLayoutParams(bookInfoNameParam);
                                RelativeLayout.LayoutParams bookInfoAuthorParam = (RelativeLayout.LayoutParams) book_info_author.getLayoutParams();
                                bookInfoAuthorParam.height = width * 2 / 5;
                                book_info_author.setLayoutParams(bookInfoAuthorParam);
                                book_info_name.setText(data.getTitle());
                                book_info_author.setText(data.getAuthor());
                                book_info.setVisibility(View.VISIBLE);
                            });
                        } else {
                            book_info.setVisibility(View.GONE);
                        }
                        return false;
                    }
                })
                .into(book_cover);
    }
}
