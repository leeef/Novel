package com.bx.philosopher.ui.adapter.view;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bx.philosopher.R;
import com.bx.philosopher.base.adapter.ViewHolderImpl;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.utils.ScreenUtils;
import com.bx.philosopher.utils.StringUtils;

public class BookShelfHolder extends ViewHolderImpl<BookBean> {

    private RelativeLayout book_cover_container;
    private ImageView book_cover;
    private ImageView book_add;
    private CheckBox delete;
    private int[] picture = {R.drawable.book_1, R.drawable.book_2, R.drawable.book_3, R.drawable.book_4,};


    private RelativeLayout book_info;
    private AppCompatTextView book_info_name;
    private AppCompatTextView book_info_author;

    public interface LongClickIml {
        //是否触发长按事件
        boolean getIsLongClick();

        //是够全选
        boolean isSelectAll();

        void selected(int position, boolean isSelect);

    }

    private LongClickIml longClickIml;

    public BookShelfHolder(LongClickIml longClick) {
        this.longClickIml = longClick;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.bookshelf_item;
    }


    @Override
    public void initView() {
        book_cover_container = findById(R.id.book_cover_container);
        book_cover = findById(R.id.book_cover);
        book_add = findById(R.id.book_add);
        delete = findById(R.id.bookshelf_delete);
        book_info = findById(R.id.book_info);
        book_info_name = findById(R.id.book_info_name);
        book_info_author = findById(R.id.book_info_author);
    }

    @Override
    public void onBind(BookBean data, int pos) {

        if (data.getTitle().equals("addImage")) {
            book_cover.setVisibility(View.GONE);
            book_add.setVisibility(View.VISIBLE);
            delete.setVisibility(View.GONE);
            book_cover_container.setOnClickListener(null);
            book_cover_container.setClickable(false);
        } else {

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) book_cover.getLayoutParams();
            layoutParams.width = ScreenUtils.dpToPx(100);
            layoutParams.height = ScreenUtils.dpToPx(150);
            book_cover.setVisibility(View.VISIBLE);
            book_add.setVisibility(View.GONE);
//            Glide.with(getContext()).load(data.getCover())
//                    .into(book_cover);
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
            Drawable drawable = StringUtils.getDrawable(R.drawable.delete_checkbox).mutate();
            drawable.setColorFilter(StringUtils.getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
            delete.setBackground(drawable);
            //长按
            if (longClickIml.getIsLongClick()) {
                delete.setVisibility(View.VISIBLE);
                book_cover_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setDeleteButtonState();
                        longClickIml.selected(pos, getDeleteButtonChecked());
                    }
                });
            } else {
                delete.setChecked(false);
                delete.setVisibility(View.GONE);
                book_cover_container.setOnClickListener(null);
                book_cover_container.setClickable(false);
            }

            if (longClickIml.isSelectAll()) {
                delete.setChecked(true);
            }

            delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        Drawable drawable = StringUtils.getDrawable(R.drawable.delete_checkbox).mutate();
                        drawable.setColorFilter(StringUtils.getColor(R.color.gray), PorterDuff.Mode.SRC_ATOP);
                        delete.setBackground(drawable);
                    } else {
                        delete.setBackground(StringUtils.getDrawable(R.drawable.delete_checkbox));
                    }

                }
            });

        }


    }


    //获取按钮是否被选中
    private boolean getDeleteButtonChecked() {
        return delete.isChecked();
    }

    private void setDeleteButtonState() {
        delete.setChecked(!delete.isChecked());
    }


}
