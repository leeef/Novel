package com.bx.philosopher.ui.adapter.view;

import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bx.philosopher.R;
import com.bx.philosopher.base.adapter.ViewHolderImpl;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BookBean;

public class SpecialTypeBookHolder extends ViewHolderImpl<BaseBean> {

    private ImageView book_cover;
    private ImageView book_rank;
    private TextView book_name;
    private TextView book_author;
    private TextView book_introduce;

    private boolean show_rank_image;


    private RelativeLayout book_info;
    private AppCompatTextView book_info_name;
    private AppCompatTextView book_info_author;

    public SpecialTypeBookHolder(boolean show_rank_image) {
        this.show_rank_image = show_rank_image;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.layout_special_type_item;
    }

    @Override
    public void initView() {
        book_cover = findById(R.id.book_cover);
        book_rank = findById(R.id.book_rank);
        book_name = findById(R.id.book_name);
        book_author = findById(R.id.book_author);
        book_introduce = findById(R.id.book_introduce);


        book_info_name = findById(R.id.book_info_name);
        book_info_author = findById(R.id.book_info_author);
        book_info = findById(R.id.book_info);
    }

    @Override
    public void onBind(BaseBean baseBean, int pos) {
        if (baseBean instanceof BookBean) {
            BookBean bookBean = (BookBean) baseBean;

            book_name.setText(bookBean.getTitle());
            book_author.setText(bookBean.getAuthor());
            book_introduce.setText(bookBean.getBrief());
            Glide.with(getContext())
                    .asDrawable()
                    .load(bookBean.getCover())
                    .centerCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //绘制图书封面内容
                            if (bookBean.getBoardid() == 2) {
                                book_cover.post(new Runnable() {
                                    @Override
                                    public void run() {
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
                                        book_info_name.setText(bookBean.getTitle());
                                        book_info_author.setText(bookBean.getAuthor());
                                        book_info.setVisibility(View.VISIBLE);
                                    }
                                });
                            }else {
                                book_info.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    })
                    .into(book_cover);
        }
        if (show_rank_image) {
            switch (pos) {
                case 0:
                    book_rank.setVisibility(View.VISIBLE);
                    book_rank.setImageDrawable(getContext().getResources().getDrawable(R.drawable.book_rank_first));
                    break;
                case 1:
                    book_rank.setVisibility(View.VISIBLE);
                    book_rank.setImageDrawable(getContext().getResources().getDrawable(R.drawable.book_rank_second));
                    break;
                case 2:
                    book_rank.setVisibility(View.VISIBLE);
                    book_rank.setImageDrawable(getContext().getResources().getDrawable(R.drawable.book_rank_third));
                    break;
                default:
                    book_rank.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
