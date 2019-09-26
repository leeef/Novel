package com.bx.philosopher.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.GlideApp;
import com.bx.philosopher.base.adapter.BaseListAdapter;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.ExploreBanner;
import com.bx.philosopher.ui.activity.BookDetailActivity;
import com.bx.philosopher.ui.activity.GiftCardActivity;
import com.bx.philosopher.ui.activity.SpecialTypeBookActivity;
import com.bx.philosopher.utils.ScreenUtils;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.widget.cardslideview.CardHandler;
import com.bx.philosopher.widget.cardslideview.CardViewPager;
import com.bx.philosopher.widget.cardslideview.ElasticCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;


//CardViewPager的适配器
public class MyExploreViewAdapter implements CardHandler<String> {

    private static final long serialVersionUID = -3409574689506097437L;
    @BindView(R.id.explore_content)
    transient RecyclerView explore_content;
    @BindView(R.id.book_second_introduce)
    transient TextView book_second_introduce;
    @BindView(R.id.book_type_name)
    transient TextView book_type_name;
    @BindView(R.id.explore_more)
    transient TextView explore_more;
    @BindView(R.id.book_item)
    transient RelativeLayout book_item;
    @BindView(R.id.book_content)
    transient RelativeLayout book_content;
    @BindView(R.id.explore_more_container)
    transient LinearLayout explore_more_container;
    @BindView(R.id.card_view)
    transient LinearLayout card_view;
    @BindView(R.id.shadow1)
    transient View shadow1;
    @BindView(R.id.shadow2)
    transient View shadow2;
    @BindView(R.id.shadow3)
    transient View shadow3;
    @BindView(R.id.shadow4)
    transient View shadow4;
    @BindView(R.id.shadow)
    transient RelativeLayout shadow;
    @BindView(R.id.book_cover)
    transient ImageView book_cover;
    @BindView(R.id.ElasticCardView)
    transient ElasticCardView ElasticCardView;

    private transient ExploreContentAdapter exploreContentAdapter;

    private transient List<View> views = new ArrayList<>();

    private ExploreBanner exploreBanner;

    private int[] covers = {R.drawable.card5, R.drawable.card10, R.drawable.card30, R.drawable.card50, R.drawable.card100,};

    public MyExploreViewAdapter(ExploreBanner exploreBanner) {
        this.exploreBanner = exploreBanner;
    }

    @Override
    public View onBind(Context context, String data, int position, @CardViewPager.TransformerMode int mode) {

        View view = View.inflate(context, R.layout.explore_view_item, null);
        ButterKnife.bind(this, view);
        exploreContentAdapter = new ExploreContentAdapter();

        //卡片高度 app高度-底部导航栏 -标题栏-状态栏
        int height = ScreenUtils.getAppSize()[1] - ScreenUtils.dpToPx(105) - ScreenUtils.getStatusBarHeight();
//        int shadowWidth = ScreenUtils.getAppSize()[0] - ScreenUtils.dpToPx(60);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        ElasticCardView.setLayoutParams(layoutParams1);
        switch ((position + 1) % 4) {
            case 1:
                book_type_name.setText("Recommended");
                book_second_introduce.setText("All kinds of popular books");
                book_type_name.measure(0, 0);
                book_second_introduce.measure(0, 0);
                explore_more_container.measure(0, 0);
                int recycleHeight = height - book_type_name.getMeasuredHeight() - book_second_introduce.getMeasuredHeight() - explore_more_container.getMeasuredHeight()
                        - ScreenUtils.dpToPx(55);//55上面字体的上下间距
                exploreContentAdapter = new ExploreContentAdapter(recycleHeight);
                addData(1, context);
                explore_content.setLayoutManager(new GridLayoutManager(context, 1));
                explore_content.setAdapter(exploreContentAdapter);
                explore_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SpecialTypeBookActivity.startActivity(context, false, 1);
                    }
                });


                break;
            case 2:

                book_type_name.setText("New books");
                book_second_introduce.setText("Hot Search Books This Week");
                explore_content.setLayoutManager(new GridLayoutManager(context, 3));
                addData(2, context);
                explore_content.setBottom(ScreenUtils.pxToDp(20));
                explore_content.setAdapter(exploreContentAdapter);
                explore_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SpecialTypeBookActivity.startActivity(context, false, 2);
                    }
                });

                break;
            case 0:
                book_type_name.setText("Today");
                book_second_introduce.setText("Today's Recommended Books");
                explore_content.setLayoutManager(new GridLayoutManager(context, 2));
                layoutParams.setMargins(10, 0, 10, 0);
                explore_content.setLayoutParams(layoutParams);
                addData(0, context);
                explore_content.setBackground(StringUtils.getDrawable(R.drawable.shape_corner_card_item));
                explore_content.setAdapter(exploreContentAdapter);
                explore_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SpecialTypeBookActivity.startActivity(context, false, 3);
                    }
                });

                break;
            case 3:
                book_type_name.setText("Hot-selling gift cards");
                book_type_name.setTextColor(StringUtils.getColor(R.color.color_fefefe));
                book_second_introduce.setText("Want to read with your friends");
                book_second_introduce.setTextColor(StringUtils.getColor(R.color.color_a5a5a5));
                explore_content.setVisibility(View.GONE);
                book_item.setVisibility(View.VISIBLE);

                int randomIndex = new Random().nextInt(covers.length);
                BitmapFactory.Options options = new BitmapFactory.Options();
                BitmapFactory.decodeResource(context.getResources(), covers[randomIndex], options);
                int shadowWidth = options.outWidth;
                int cardHeight = options.outHeight;
                GlideApp.with(context)
                        .load(covers[randomIndex])
                        .override(shadowWidth, cardHeight)
                        .into(book_cover);
                views.clear();
                views.add(shadow1);
                views.add(shadow2);
                views.add(shadow3);
                views.add(shadow4);
                setShaw(shadowWidth);

                card_view.setBackgroundColor(context.getResources().getColor(R.color.color_2b3C33));
                explore_more.setBackground(StringUtils.getDrawable(R.drawable.more_bg_gradient));
                explore_more.setTextColor(StringUtils.getColor(R.color.color_8d6a29));
                explore_more.setOnClickListener(v ->
                        context.startActivity(new Intent(context, GiftCardActivity.class)));
                break;
        }
        return view;
    }


    void setShaw(int width) {
        if (width == 0) {
            shadow.setVisibility(View.GONE);
            return;
        }
        width = width - ScreenUtils.dpToPx(20);
        int height = ScreenUtils.dpToPx(30);
        for (int i = 1; i <= views.size(); i++) {
            int shawWidth = width - ScreenUtils.dpToPx(20) * i;
            int shawHeight = height + ScreenUtils.dpToPx(10) * i;
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) views.get(i - 1).getLayoutParams();
            layoutParams.width = shawWidth;
            layoutParams.height = shawHeight;

            views.get(i - 1).setLayoutParams(layoutParams);
        }
        shadow.setVisibility(View.VISIBLE);
    }

    void setUI() {
        book_type_name.setTextColor(StringUtils.getColor(R.color.color_fefefe));
        book_second_introduce.setTextColor(StringUtils.getColor(R.color.color_a5a5a5));
    }

    private void addData(int index, Context context) {
        List<BaseBean> baseBeans = new ArrayList<>();
        switch (index) {
            case 0:
                exploreBanner.setTodayType(3);
                if (exploreBanner.getToday().size() > 4) {
                    baseBeans.addAll(exploreBanner.getToday().subList(0, 4));
                } else {
                    baseBeans.addAll(exploreBanner.getToday());
                }
                exploreContentAdapter.refreshItems(baseBeans);
                exploreContentAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int pos) {
                        BookDetailActivity.startActivity(context, BookDetailActivity.TYPE_EXPLORE, exploreBanner.getToday().get(pos).getId());
                    }
                });
                break;
            case 1:
                exploreBanner.setRecommendType(1);
                if (exploreBanner.getRecommend().size() > 3) {
                    baseBeans.addAll(exploreBanner.getRecommend().subList(0, 3));
                } else {
                    baseBeans.addAll(exploreBanner.getRecommend());
                }
                exploreContentAdapter.refreshItems(baseBeans);
                exploreContentAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int pos) {
                        BookDetailActivity.startActivity(context, BookDetailActivity.TYPE_EXPLORE, exploreBanner.getRecommend().get(pos).getId());
                    }
                });
                break;
            case 2:
                exploreBanner.setNewBookType(2);
                if (exploreBanner.getNewbook().size() > 6) {
                    baseBeans.addAll(exploreBanner.getNewbook().subList(0, 6));
                } else {
                    baseBeans.addAll(exploreBanner.getNewbook());
                }
                exploreContentAdapter.refreshItems(baseBeans);
                exploreContentAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int pos) {
                        BookDetailActivity.startActivity(context, BookDetailActivity.TYPE_EXPLORE, exploreBanner.getNewbook().get(pos).getId());
                    }
                });
                break;
            case 3://卡片
                break;
        }
    }


}