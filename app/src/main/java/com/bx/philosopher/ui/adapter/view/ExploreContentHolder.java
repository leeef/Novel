package com.bx.philosopher.ui.adapter.view;

import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.ScreenUtils;
import com.bx.philosopher.utils.StringUtils;

//explore页面每个卡片的holder
public class ExploreContentHolder extends ViewHolderImpl<BaseBean> {


    private int viewType;
    private int viewHeight;
    private TextView book_name;
    private TextView book_introduce;
    private ImageView book_cover;
    private LinearLayout container;


    private RelativeLayout book_info;
    private AppCompatTextView book_info_name;
    private AppCompatTextView book_info_author;

    public ExploreContentHolder(int viewType) {
        this.viewType = viewType;
    }

    public ExploreContentHolder(int viewType, int height) {
        this.viewType = viewType;
        this.viewHeight = height;
    }


    @Override
    protected int getItemLayoutId() {
        switch (viewType) {
            case 1:
                return R.layout.book_list_item;
            case 2:
                return R.layout.book_item2;
            case 3:
                return R.layout.book_item2;
        }
        return R.layout.book_list_item;
    }

    @Override
    public void initView() {
        switch (viewType) {
            case 1:
                book_name = findById(R.id.book_name);
                book_introduce = findById(R.id.book_introduce);
                book_cover = findById(R.id.book_cover);
                book_info_name = findById(R.id.book_info_name);
                book_info_author = findById(R.id.book_info_author);
                book_info = findById(R.id.book_info);
                container = findById(R.id.container);
                break;
            case 2:
                book_name = findById(R.id.book_name);
                book_cover = findById(R.id.book_cover);
                container = findById(R.id.container);
                book_info_name = findById(R.id.book_info_name);
                book_info_author = findById(R.id.book_info_author);
                book_info = findById(R.id.book_info);
                break;
            case 3:
                book_name = findById(R.id.book_name);
                book_cover = findById(R.id.book_cover);
                container = findById(R.id.container);
                book_info_name = findById(R.id.book_info_name);
                book_info_author = findById(R.id.book_info_author);
                book_info = findById(R.id.book_info);
                break;
        }

    }

    @Override
    public void onBind(BaseBean baseBean, int pos) {
        BookBean bookBean = new BookBean(0);
        if (baseBean instanceof BookBean) {
            bookBean = (BookBean) baseBean;
        }
        switch (viewType) {
            case 1:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) book_cover.getLayoutParams();

                int itemHeight = viewHeight / 3 - ScreenUtils.dpToPx(40);//40包括item的上下间距和图片的上下间距
                if (itemHeight < ScreenUtils.dpToPx(96)) {
                    layoutParams.height = itemHeight;
                    layoutParams.width = itemHeight * 70 / 96;
                } else {
                    layoutParams.height = ScreenUtils.dpToPx(96);
                    layoutParams.width = ScreenUtils.dpToPx(70);
                }

                book_name.setText(bookBean.getTitle());
                book_introduce.setText(bookBean.getBrief());
                BookBean finalBookBean1 = bookBean;
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
                                if (finalBookBean1.getBoardid() == 2) {
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
                                            book_info_name.setText(finalBookBean1.getTitle());
                                            book_info_author.setText(finalBookBean1.getAuthor());
                                            book_info.setVisibility(View.VISIBLE);
                                        }
                                    });
                                } else {
                                    book_info.setVisibility(View.GONE);
                                }
                                return false;
                            }
                        })
                        .into(book_cover);
                break;
            case 2:
                book_name.setText(bookBean.getTitle());
                BookBean finalBookBean2 = bookBean;
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
                                if (finalBookBean2.getBoardid() == 2) {
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
                                            book_info_name.setText(finalBookBean2.getTitle());
                                            book_info_author.setText(finalBookBean2.getAuthor());
                                            book_info.setVisibility(View.VISIBLE);
                                        }
                                    });
                                } else {
                                    book_info.setVisibility(View.GONE);
                                }
                                return false;
                            }
                        })
                        .into(book_cover);
                break;
            case 3:
                container.setGravity(Gravity.CENTER);
                book_name.setVisibility(View.GONE);
                container.setBackground(StringUtils.getDrawable(R.drawable.simple_item_click_bg_e4f0f3));
                BookBean finalBookBean3 = bookBean;
                Glide.with(getContext())
                        .asDrawable()
                        .load( bookBean.getCover())
                        .centerCrop()
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                //绘制图书封面内容
                                if (finalBookBean3.getBoardid() == 2) {
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
                                            book_info_name.setText(finalBookBean3.getTitle());
                                            book_info_author.setText(finalBookBean3.getAuthor());
                                            book_info.setVisibility(View.VISIBLE);
                                        }
                                    });
                                } else {
                                    book_info.setVisibility(View.GONE);
                                }
                                return false;
                            }
                        })
                        .into(book_cover);
                break;
        }
    }
}
