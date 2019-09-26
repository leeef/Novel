package com.bx.philosopher.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseMVPActivity;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.model.bean.response.BookDetailBean;
import com.bx.philosopher.model.bean.response.BookPackage;
import com.bx.philosopher.presenter.BookDetailPresenter;
import com.bx.philosopher.presenter.imp.BookDetailImp;
import com.bx.philosopher.ui.adapter.ExploreExtraAdapter;
import com.bx.philosopher.ui.adapter.view.ExploreExtraHolder;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.widget.RecycleViewSpan;
import com.gyf.immersionbar.ImmersionBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class BookDetailActivity extends BaseMVPActivity<BookDetailPresenter> implements View.OnClickListener, Animation.AnimationListener, BookDetailImp.View {

    @BindView(R.id.book_detail_review)
    RecyclerView book_detail_review;
    @BindView(R.id.bookshelf)
    LinearLayout bookshelf;
    @BindView(R.id.trial_reading)
    LinearLayout trial_reading;
    @BindView(R.id.copyright_container)
    LinearLayout copyright_container;
    @BindView(R.id.copyright_set)
    ImageView copyright_set;
    @BindView(R.id.subscribe_or_purchase_img_2)
    ImageView subscribe_or_purchase_img_2;
    @BindView(R.id.subscribe_or_purchase_img)
    ImageView subscribe_or_purchase_img;
    @BindView(R.id.subscribe_or_purchase_txt)
    TextView subscribe_or_purchase_txt;
    @BindView(R.id.subscribe_or_purchase_txt_2)
    TextView subscribe_or_purchase_txt_2;
    @BindView(R.id.container)
    LinearLayout container;

    @BindView(R.id.title_bar_layout)
    RelativeLayout title_bar_layout;
    @BindView(R.id.back)
    LinearLayout back;
    @BindView(R.id.book_detail_top)
    LinearLayout book_detail_top;
    @BindView(R.id.top_menu_layout)
    LinearLayout top_menu_layout;
    @BindView(R.id.share)
    ImageView share;
    @BindView(R.id.book_detail_catalog)
    LinearLayout book_detail_catalog;
    @BindView(R.id.want_to_see)
    LinearLayout want_to_see;
    @BindView(R.id.subscribe_top)
    LinearLayout subscribe_top;
    @BindView(R.id.want_to_see_image)
    ImageView want_to_see_image;
    @BindView(R.id.book_cover)
    ImageView book_cover;
    @BindView(R.id.book_name)
    TextView book_name;
    @BindView(R.id.book_author)
    TextView book_author;
    @BindView(R.id.book_introduce)
    TextView book_introduce;
    @BindView(R.id.book_price)
    TextView book_price;
    @BindView(R.id.book_detail_introduce)
    TextView book_detail_introduce;
    @BindView(R.id.publish_house)
    TextView publish_house;
    @BindView(R.id.publication_date)
    TextView publication_date;
    @BindView(R.id.number_of_words)
    TextView number_of_words;
    @BindView(R.id.classification)
    TextView classification;
    @BindView(R.id.book_detail_sign)
    TextView book_detail_sign;


    //探索或者是图书馆
    public static final String BOOK_TYPE = "type";
    public static final String TYPE_LIBRARY = "type_library";
    public static final String TYPE_EXPLORE = "type_explore";

    private String book_detail_type;
    private int bookId;//书籍id
    ExploreExtraAdapter exploreExtraAdapter;
    private BookDetailBean bookDetailBean;


    public static void startActivity(Context context, String type, int bookId) {
        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra(BOOK_TYPE, type);
        intent.putExtra("bookId", bookId);
        context.startActivity(intent);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_book_detail;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);


        book_detail_type = getIntent().getStringExtra(BOOK_TYPE);
        if (StringUtils.isTrimEmpty(book_detail_type)) book_detail_type = TYPE_LIBRARY;
        bookId = getIntent().getIntExtra("bookId", -1);
    }


    @Override
    protected void initWidget() {
        super.initWidget();

        exploreExtraAdapter = new ExploreExtraAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6);
        gridLayoutManager.setSpanSizeLookup(new RecycleViewSpan(exploreExtraAdapter));
        book_detail_review.setLayoutManager(gridLayoutManager);
        book_detail_review.setAdapter(exploreExtraAdapter);
        book_detail_review.setHasFixedSize(true);
        book_detail_review.setNestedScrollingEnabled(false);
        exploreExtraAdapter.setOnItemClickListener((view, pos) -> {
            int viewType = exploreExtraAdapter.getItem(pos).getViewType();
            switch (viewType) {
                case ExploreExtraHolder.BOOK_TAG:
                    if (exploreExtraAdapter.getItem(pos) instanceof BookPackage) {
                        BookPackage bookPackage = (BookPackage) exploreExtraAdapter.getItem(pos);
                        if (book_detail_type.equals(TYPE_EXPLORE)) {
                            switch (bookPackage.getName()) {
                                case "sales":
                                    SpecialTypeBookActivity.startActivity(BookDetailActivity.this, 1, book_detail_type, "sales");
                                    break;
                                case "collect":
                                    SpecialTypeBookActivity.startActivity(BookDetailActivity.this, 5, book_detail_type, "collect");
                                    break;
                                case "hits":
                                    SpecialTypeBookActivity.startActivity(BookDetailActivity.this, 3, book_detail_type, "hits");
                                    break;
                            }
                        } else {
                            switch (bookPackage.getName()) {
                                case "sales":
                                    SpecialTypeBookActivity.startActivity(BookDetailActivity.this, 1, book_detail_type, "sales");
                                    break;
                                case "collect":
                                    SpecialTypeBookActivity.startActivity(BookDetailActivity.this, 2, book_detail_type, "collect");
                                    break;
                                case "hits":
                                    SpecialTypeBookActivity.startActivity(BookDetailActivity.this, 3, book_detail_type, "hits");
                                    break;
                            }
                        }
                    }
                    break;
                case ExploreExtraHolder.BOOK_ITEM2:
                    if (exploreExtraAdapter.getItem(pos) instanceof BookBean) {
                        BookBean bookBean = (BookBean) exploreExtraAdapter.getItem(pos);
                        BookDetailActivity.startActivity(BookDetailActivity.this, book_detail_type, bookBean.getId());
                    }
                    break;
            }
        });

        setUI();
    }

    public void setUI() {
        switch (book_detail_type) {
            case TYPE_EXPLORE:
                subscribe_or_purchase_img.setImageDrawable(getResources().getDrawable(R.drawable.purchase_2));
                subscribe_or_purchase_txt.setText(getResources().getString(R.string.purchase));
                subscribe_or_purchase_img_2.setImageDrawable(getResources().getDrawable(R.drawable.purchase));
                subscribe_or_purchase_txt_2.setText(getResources().getString(R.string.purchase));
                title_bar_layout.setBackgroundColor(getResources().getColor(R.color.color_f2f2f2));
                book_detail_top.setBackgroundColor(getResources().getColor(R.color.color_f2f2f2));
                top_menu_layout.setBackgroundColor(getResources().getColor(R.color.color_f2f2f2));

                back.setBackground(getResources().getDrawable(R.drawable.simple_item_click_bg_f2f2f2));
                share.setBackground(getResources().getDrawable(R.drawable.simple_item_click_bg_f2f2f2));
                book_detail_catalog.setBackground(getResources().getDrawable(R.drawable.simple_item_click_bg_f2f2f2));
                want_to_see.setBackground(getResources().getDrawable(R.drawable.simple_item_click_bg_f2f2f2));
                subscribe_top.setBackground(getResources().getDrawable(R.drawable.simple_item_click_bg_f2f2f2));

                ImmersionBar.with(this).statusBarColor(R.color.color_f2f2f2).init();
                mPresenter.getData(TYPE_EXPLORE, bookId);
                break;
            case TYPE_LIBRARY:
                subscribe_or_purchase_img.setImageDrawable(getResources().getDrawable(R.drawable.subscribe));
                subscribe_or_purchase_txt.setText(getResources().getString(R.string.subscribe));
                subscribe_or_purchase_img_2.setImageDrawable(getResources().getDrawable(R.drawable.subscribe_2));
                subscribe_or_purchase_txt_2.setText(getResources().getString(R.string.subscribe));
                ImmersionBar.with(this).statusBarColor(R.color.color_e4f0f3).init();
                mPresenter.getData(TYPE_LIBRARY, bookId);
                break;
        }
    }

    @OnClick({R.id.copyright_set, R.id.book_detail_catalog, R.id.book_detail_top, R.id.subscribe_bottom, R.id.subscribe_top,
            R.id.bookshelf, R.id.trial_reading, R.id.share, R.id.want_to_see})
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.copyright_set:
                setCopyright_container();
                break;
            case R.id.book_detail_catalog:
                BookCatalogActivity.startActivity(BookDetailActivity.this, bookId, book_detail_type, bookDetailBean.getTitle());

                break;
            case R.id.subscribe_top:
            case R.id.subscribe_bottom:
                switch (book_detail_type) {
                    case TYPE_EXPLORE://购买
                        if (Float.parseFloat(bookDetailBean.getDiscounts()) == 0.00) {
                            MyRechargeActivity.startActivity(this, bookDetailBean.getTitle(), bookDetailBean.getMoney(), bookId);
                        } else {
                            MyRechargeActivity.startActivity(this, bookDetailBean.getTitle(), bookDetailBean.getDiscounts(), bookId);
                        }

                        break;
                    case TYPE_LIBRARY://订阅
                        startActivity(MySubscribeActivity.class);
                        break;
                }
                break;
            case R.id.want_to_see:
                mPresenter.wantSee(bookId, bookDetailBean.getFavorite(), book_detail_type);
                break;
            case R.id.trial_reading:
                mPresenter.addBookToShelf(bookId);//添加到书架
                ReadingActivity.startActivity(this, bookId, book_detail_type, bookDetailBean.getTitle());
                break;
            case R.id.bookshelf:
                mPresenter.addBookToShelf(bookId);
                break;
            case R.id.share:
                mPresenter.loadBook(bookId, book_detail_type, this);
                break;
        }

    }

    private void setCopyright_container() {
        if (copyright_container.getVisibility() == View.VISIBLE) {
            copyright_set.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
            copyright_container.setVisibility(View.GONE);
        } else {
            copyright_set.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
            copyright_container.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void showError(String errMsg) {
        Log.i(Constant.TAG, errMsg);
    }

    @Override
    public void complete() {
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected BookDetailPresenter bindPresenter() {
        return new BookDetailPresenter();
    }

    @Override
    public void showData(BookDetailBean bookDetailBean, List<BaseBean> baseBeans) {
        this.bookDetailBean = bookDetailBean;
        exploreExtraAdapter.addItems(baseBeans);
        initBookUI();
    }

    @Override
    public void showWantSee(String result, String sign) {
        if (result.equals("true")) {
            ToastUtils.show(sign);
            want_to_see_image.setImageDrawable(StringUtils.getDrawable(R.drawable.want_to_see_fill));
        } else {
            ToastUtils.show(sign);
            want_to_see_image.setImageDrawable(StringUtils.getDrawable(R.drawable.want_to_see));
        }
    }

    void initBookUI() {
        if (bookDetailBean == null) return;
        Glide.with(this).load(bookDetailBean.getCover()).into(book_cover);
        book_name.setText(bookDetailBean.getTitle());
        book_author.setText(bookDetailBean.getAuthor());
        book_introduce.setText(bookDetailBean.getBrief());

        book_detail_introduce.setText(bookDetailBean.getBrief());

        publish_house.setText(bookDetailBean.getPress());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy", Locale.UK);
        publication_date.setText(simpleDateFormat.format(new Date(Long.parseLong(bookDetailBean.getPresstime()) * 1000)));
        number_of_words.setText(bookDetailBean.getNumber() + "");
        classification.setText(bookDetailBean.getName());

        if (bookDetailBean.getFavorite() == 1) {
            //喜欢
            want_to_see_image.setImageDrawable(StringUtils.getDrawable(R.drawable.want_to_see_fill));
        } else {
            //
            want_to_see_image.setImageDrawable(StringUtils.getDrawable(R.drawable.want_to_see));
        }

        if (book_detail_type.equals(TYPE_EXPLORE)) {
            try {
                if (Float.parseFloat(bookDetailBean.getDiscounts()) == 0.00) {
                    book_price.setText("$" + bookDetailBean.getMoney());

                    book_detail_sign.setVisibility(View.GONE);
                } else {
                    book_price.setText("$" + bookDetailBean.getDiscounts());
                    book_detail_sign.setText("$" + bookDetailBean.getMoney());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            book_detail_sign.setTextColor(StringUtils.getColor(R.color.color_cccccc));
            book_detail_sign.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);//中间加横线
        } else {
            book_price.setText("$" + bookDetailBean.getMoney());
            book_detail_sign.setText("Readable Library Books");
            book_detail_sign.setTextColor(StringUtils.getColor(R.color.color_65c933));
        }
    }
}
