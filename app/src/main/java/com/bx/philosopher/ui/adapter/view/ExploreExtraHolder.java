package com.bx.philosopher.ui.adapter.view;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bx.philosopher.R;
import com.bx.philosopher.base.GlideApp;
import com.bx.philosopher.base.adapter.ViewHolderImpl;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.model.bean.response.BookPackage;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.ScreenUtils;
import com.bx.philosopher.utils.StringUtils;

//explore下面列表布局
public class ExploreExtraHolder extends ViewHolderImpl<BaseBean> {

    public static final int BOOK_TAG = 1;//标签

    public static final int BOOK_GRAY_LINE = 2;//灰线
    //三种图书样式
    public static final int BOOK_ITEM1 = 3;//青色椭圆背景 上面封面 下面名字和介绍
    public static final int BOOK_ITEM2 = 4;//上面封面 下面名字
    public static final int BOOK_ITEM3 = 5; //左边图片 右边图书的三种信息
    public static final int BOOK_ITEM4 = 6;//左边种类 青色背景 右边图片
    //用来控制列数
    public static final int IMAGE_ONE = 7;//一行显示一个图片
    public static final int IMAGE_TWO = 8;//一行显示两个图片


    private int viewType;

    private LinearLayout book_gray_line;

    private TextView book_type;
    private ImageView tag_right;
    private TextView change_pic;

    private ImageView book_cover;
    private TextView book_name;
    private TextView book_introduce;
    private TextView book_author;
    private LinearLayout book_bg;

    private TextView image_sign;
    private TextView image_sign_name;
    private LinearLayout library_sign;
    private LinearLayout explore_sign;

    private RelativeLayout book_info;
    private AppCompatTextView book_info_name;
    private AppCompatTextView book_info_author;


    public ExploreExtraHolder(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public void initView() {
        switch (viewType) {
            case BOOK_GRAY_LINE:
                book_gray_line = findById(R.id.book_gray_line);
                break;
            case BOOK_TAG:
                book_type = findById(R.id.book_type);
                tag_right = findById(R.id.tag_right);
                change_pic = findById(R.id.change_pic);
                break;
            case BOOK_ITEM1:

                book_cover = findById(R.id.book_cover);
                book_name = findById(R.id.book_name);
                book_introduce = findById(R.id.book_introduce);
                book_bg = findById(R.id.book_bg);
                book_info_name = findById(R.id.book_info_name);
                book_info_author = findById(R.id.book_info_author);
                book_info = findById(R.id.book_info);
                break;
            case BOOK_ITEM2:
                book_cover = findById(R.id.book_cover);
                book_name = findById(R.id.book_name);
                book_info_name = findById(R.id.book_info_name);
                book_info_author = findById(R.id.book_info_author);
                book_info = findById(R.id.book_info);
                break;
            case BOOK_ITEM3:
                book_cover = findById(R.id.book_cover);
                book_name = findById(R.id.book_name);
                book_author = findById(R.id.book_author);
                book_introduce = findById(R.id.book_introduce);
                book_info_name = findById(R.id.book_info_name);
                book_info_author = findById(R.id.book_info_author);
                book_info = findById(R.id.book_info);
                break;
            case BOOK_ITEM4:
                book_cover = findById(R.id.book_cover);
                book_type = findById(R.id.book_type);
                book_info_name = findById(R.id.book_info_name);
                book_info_author = findById(R.id.book_info_author);
                book_info = findById(R.id.book_info);
                break;
            case IMAGE_ONE:
            case IMAGE_TWO:
                book_cover = findById(R.id.book_cover);
                image_sign = findById(R.id.image_sign);
                image_sign_name = findById(R.id.image_sign_name);
                library_sign = findById(R.id.library_sign);
                explore_sign = findById(R.id.explore_sign);
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBind(BaseBean data, int pos) {
        BookBean bookBean = new BookBean(0);
        if (data instanceof BookBean) {
            bookBean = (BookBean) data;
        }
        switch (viewType) {
            case BOOK_GRAY_LINE:
                break;
            case BOOK_TAG:
                if (data instanceof BookPackage) {
                    BookPackage bookPackage = (BookPackage) data;
                    if (bookPackage.getName().equals(Constant.EXPLORE_RANK_NAME)) {
                        tag_right.setVisibility(View.GONE);
                        change_pic.setVisibility(View.GONE);
                    } else if (bookPackage.getName().equals("Books you like")) {
                        change_pic.setVisibility(View.VISIBLE);
                        tag_right.setVisibility(View.GONE);
                    } else {
                        tag_right.setVisibility(View.VISIBLE);
                        change_pic.setVisibility(View.GONE);
                    }
                    book_type.setText(bookPackage.getName());
                }
                break;
            case BOOK_ITEM2:
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
                book_name.setText(bookBean.getTitle());
                break;
            case BOOK_ITEM1:
                if (bookBean.getShow_type().equals("all_catalog")) {
                    book_introduce.setVisibility(View.GONE);
                    book_bg.setBackground(StringUtils.getDrawable(R.drawable.shape_corner_gray));
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) book_cover.getLayoutParams();
                    layoutParams.width = ScreenUtils.dpToPx(60);
                    layoutParams.height = ScreenUtils.dpToPx(84);
                }
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
                book_name.setText(bookBean.getTitle());
                book_introduce.setText(bookBean.getBrief());

                break;
            case BOOK_ITEM3:
                BookBean finalBookBean3 = bookBean;
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
                book_name.setText(bookBean.getTitle());
                book_author.setText(bookBean.getAuthor());
                book_introduce.setText(bookBean.getBrief());
                break;
            case BOOK_ITEM4:
                if (data instanceof BookPackage) {
                    BookPackage bookPackage = (BookPackage) data;
                    book_type.setText(bookPackage.getName());
                    Glide.with(getContext())
                            .asDrawable()
                            .load(Constant.book_covers[pos % 4])
                            .centerCrop()
                            .into(book_cover);
                }

                break;
            case IMAGE_ONE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) book_cover.getLayoutParams();
                layoutParams.height = ScreenUtils.dpToPx(117);
                Glide.with(getContext())
                        .asDrawable()
                        .load(bookBean.getCover())
                        .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(10)))
                        .into(book_cover);
                break;
            case IMAGE_TWO:
                RelativeLayout.LayoutParams image_two_params = (RelativeLayout.LayoutParams) book_cover.getLayoutParams();
                image_two_params.setMarginStart(ScreenUtils.dpToPx(8));
                image_two_params.setMarginEnd(ScreenUtils.dpToPx(8));
                if (bookBean.getTitle().equals(Constant.EXPLORE_RANK_NAME)) {
                    explore_sign.setVisibility(View.VISIBLE);
                    library_sign.setVisibility(View.GONE);
                    image_sign_name.setText(bookBean.getAuthor());
                    image_sign.getPaint().setFakeBoldText(true);
                    image_sign_name.getPaint().setFakeBoldText(true);
                    LinearLayout.LayoutParams image_sign_name_param = (LinearLayout.LayoutParams) image_sign_name.getLayoutParams();
                    image_sign_name_param.topMargin = ScreenUtils.dpToPx(10);

                    GlideApp.with(getContext())
                            .asDrawable()
                            .load(StringUtils.getDrawable(Integer.parseInt(bookBean.getCover().replace(Constant.CLIENT_URL, ""))))
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                            .into(book_cover);
                } else {
                    library_sign.setVisibility(View.VISIBLE);
                    explore_sign.setVisibility(View.GONE);

                    image_two_params.height = ScreenUtils.dpToPx(86);
                    Glide.with(getContext())
                            .asDrawable()
                            .load(bookBean.getCover())
                            .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(10)))
                            .into(book_cover);
                }
                break;
        }

    }


    @Override
    protected int getItemLayoutId() {
        switch (viewType) {
            case BOOK_TAG:
                return R.layout.tag_layout;//标签
            case BOOK_GRAY_LINE:
                return R.layout.book_item_grayline;//灰线
            case BOOK_ITEM1:
                return R.layout.book_item_card;
            case BOOK_ITEM2:
                return R.layout.book_item2;
            case BOOK_ITEM3:
                return R.layout.book_item3;
            case BOOK_ITEM4:
                return R.layout.book_item4;
            case IMAGE_ONE:
            case IMAGE_TWO:
                return R.layout.image_item_layout;
        }
        return R.layout.tag_layout;
    }


}
