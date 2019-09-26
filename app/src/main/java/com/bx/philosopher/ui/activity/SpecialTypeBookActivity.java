package com.bx.philosopher.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BaseActivity;
import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.model.bean.response.BookPackage;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.ui.adapter.SpecialTypeBookAdapter;
import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.StringUtils;
import com.bx.philosopher.widget.itemdecoration.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class SpecialTypeBookActivity extends BaseActivity implements BaseContract.BaseView {

    @BindView(R.id.book_list)
    RecyclerView book_list;
    @BindView(R.id.title)
    TextView title;

    private SpecialTypeBookAdapter specialTypeBookAdapter;

    //前三名是否展示排名图片
    private boolean show_rank;

    private BookBean bookBean;
    private BookPackage bookPackage;
    private BaseBean baseBean = new BaseBean(1);

    //1=>畅销书推荐 2=>新书推荐 3=>今日推荐 4=>礼品卡 5=>收藏量列表
    private int type;
    private String page_title;

    //图书馆上面
    private int library_type;
    private String library_name;
    private int library_recommend_type;
    private String library_recommend_name;

    private String from_type;//从哪个页面进入 图书馆或者探索


    @Override
    protected int getContentId() {
        return R.layout.activity_special_type;
    }

    //探索 ranking list
    public static void startActivity(Context context, boolean show_rank, BaseBean bookBean) {
        Intent intent = new Intent(context, SpecialTypeBookActivity.class);
        intent.putExtra("show_rank", show_rank);
        intent.putExtra("from_type", BookDetailActivity.TYPE_EXPLORE);
        Bundle bundle = new Bundle();
        if (bookBean instanceof BookPackage) {
            BookPackage bookPackage = (BookPackage) bookBean;
            bundle.putParcelable("book_package", bookPackage);
        } else if (bookBean instanceof BookBean) {

            BookBean book = (BookBean) bookBean;
            bundle.putParcelable("book", book);
        }
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    //探索和图书馆标签
    public static void startActivity(Context context, BaseBean bookBean, String from_type) {
        Intent intent = new Intent(context, SpecialTypeBookActivity.class);
        intent.putExtra("show_rank", false);
        intent.putExtra("from_type", from_type);
        Bundle bundle = new Bundle();
        if (bookBean instanceof BookPackage) {
            BookPackage bookPackage = (BookPackage) bookBean;
            bundle.putParcelable("book_package", bookPackage);
        } else if (bookBean instanceof BookBean) {

            BookBean book = (BookBean) bookBean;
            bundle.putParcelable("book", book);
        }
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    //探索卡片更多按钮
    public static void startActivity(Context context, boolean show_rank, int type) {
        Intent intent = new Intent(context, SpecialTypeBookActivity.class);
        intent.putExtra("show_rank", show_rank);
        intent.putExtra("from_type", BookDetailActivity.TYPE_EXPLORE);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }


    //图书详情推荐标签
    public static void startActivity(Context context, int type, String from_type, String title) {
        Intent intent = new Intent(context, SpecialTypeBookActivity.class);
        intent.putExtra("show_rank", false);
        intent.putExtra("from_type", from_type);
        intent.putExtra("type", type);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    //图书馆顶部三个分类
    public static void startActivityFromLibrary(Context context, int type, String library_name) {
        Intent intent = new Intent(context, SpecialTypeBookActivity.class);
        intent.putExtra("show_rank", false);
        intent.putExtra("from_type", BookDetailActivity.TYPE_LIBRARY);
        intent.putExtra("library_type", type);
        intent.putExtra("library_name", library_name);
        context.startActivity(intent);
    }

    //图书馆 recommend
    public static void startActivity(Context context, String library_recommend_name, int library_recommend_type) {
        Intent intent = new Intent(context, SpecialTypeBookActivity.class);
        intent.putExtra("show_rank", false);
        intent.putExtra("from_type", BookDetailActivity.TYPE_LIBRARY);
        intent.putExtra("library_recommend_type", library_recommend_type);
        intent.putExtra("library_recommend_name", library_recommend_name);
        context.startActivity(intent);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        show_rank = getIntent().getBooleanExtra("show_rank", false);
        type = getIntent().getIntExtra("type", -1);
        library_type = getIntent().getIntExtra("library_type", -1);
        library_recommend_type = getIntent().getIntExtra("library_recommend_type", -1);
        from_type = getIntent().getStringExtra("from_type");
        page_title = getIntent().getStringExtra("title");
        library_name = getIntent().getStringExtra("library_name");
        library_recommend_name = getIntent().getStringExtra("library_recommend_name");
        bookBean = Objects.requireNonNull(getIntent().getExtras()).getParcelable("book");
        bookPackage = getIntent().getExtras().getParcelable("book_package");
        if (baseBean instanceof BookBean) {
            bookBean = (BookBean) baseBean;
        } else if (baseBean instanceof BookPackage) {
            bookPackage = (BookPackage) baseBean;
        }

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        book_list.setLayoutManager(new LinearLayoutManager(this));
        book_list.addItemDecoration(new RecycleViewDivider(this, LinearLayout.HORIZONTAL,
                1, getResources().getColor(R.color.color_eeeeee), 20, 0));
        specialTypeBookAdapter = new SpecialTypeBookAdapter(show_rank);
        book_list.setAdapter(specialTypeBookAdapter);
        getData();

        specialTypeBookAdapter.setOnItemClickListener((view, pos) -> {
            BookBean bookBean = (BookBean) specialTypeBookAdapter.getItem(pos);
            BookDetailActivity.startActivity(SpecialTypeBookActivity.this, from_type, bookBean.getId());
        });
    }

    @Override
    public void showError(String errMsg) {
        Log.i(Constant.TAG, errMsg);
    }

    @Override
    public void complete() {

    }

    private void getData() {
        if (bookBean != null) {
            title.setText(bookBean.getAuthor());
            HttpUtil.getInstance().getRequestApi().getExploreRankList(bookBean.getId())
                    .compose(RxScheduler.Obs_io_main())
                    .subscribe(new BaseObserver<BaseResponse<List<BookBean>>>(SpecialTypeBookActivity.this) {
                        @Override
                        public void onSuccess(BaseResponse<List<BookBean>> o) {
                            List<BaseBean> data = new ArrayList<>();
                            data.addAll(o.getData());
                            specialTypeBookAdapter.addItems(data);
                        }

                        @Override
                        public void onError(String msg) {

                        }
                    });
        } else if (bookPackage != null) {

            title.setText(bookPackage.getName());
            switch (from_type) {
                case BookDetailActivity.TYPE_EXPLORE:
                    HttpUtil.getInstance().getRequestApi().getExploreTypeList(bookPackage.getId())
                            .compose(RxScheduler.Obs_io_main())
                            .subscribe(new BaseObserver<BaseResponse<List<BookBean>>>(this) {
                                @Override
                                public void onSuccess(BaseResponse<List<BookBean>> o) {
                                    List<BaseBean> data = new ArrayList<>();
                                    data.addAll(o.getData());
                                    specialTypeBookAdapter.addItems(data);
                                }

                                @Override
                                public void onError(String msg) {

                                }
                            });
                    break;
                case BookDetailActivity.TYPE_LIBRARY:
                    HttpUtil.getInstance().getRequestApi().getLibraryTypeList(bookPackage.getId())
                            .compose(RxScheduler.Obs_io_main())
                            .subscribe(new BaseObserver<BaseResponse<List<BookBean>>>(this) {
                                @Override
                                public void onSuccess(BaseResponse<List<BookBean>> o) {
                                    List<BaseBean> data = new ArrayList<>();
                                    data.addAll(o.getData());
                                    specialTypeBookAdapter.addItems(data);
                                }

                                @Override
                                public void onError(String msg) {

                                }
                            });
                    break;
            }

        } else if (type != -1) {
            switch (type) {
                case 1:
                    title.setText("Recommended");
                    break;
                case 2:
                    title.setText("New books");
                    break;
                case 3:
                    title.setText("Today");
                    break;
            }
            if (StringUtils.isNotEmpty(page_title)) title.setText(page_title);
            switch (from_type) {
                case BookDetailActivity.TYPE_EXPLORE:
                    HttpUtil.getInstance().getRequestApi().getExploreBannerMore(type)
                            .compose(RxScheduler.Obs_io_main())
                            .subscribe(new BaseObserver<BaseResponse<List<BookBean>>>(this) {
                                @Override
                                public void onSuccess(BaseResponse<List<BookBean>> o) {
                                    List<BaseBean> data = new ArrayList<>();
                                    data.addAll(o.getData());
                                    specialTypeBookAdapter.addItems(data);
                                }

                                @Override
                                public void onError(String msg) {

                                }
                            });
                    break;
                case BookDetailActivity.TYPE_LIBRARY:
                    HttpUtil.getInstance().getRequestApi().getLibraryBookDetailTypeList(type)
                            .compose(RxScheduler.Obs_io_main())
                            .subscribe(new BaseObserver<BaseResponse<List<BookBean>>>(this) {
                                @Override
                                public void onSuccess(BaseResponse<List<BookBean>> o) {
                                    List<BaseBean> data = new ArrayList<>();
                                    data.addAll(o.getData());
                                    specialTypeBookAdapter.addItems(data);
                                }

                                @Override
                                public void onError(String msg) {

                                }
                            });
                    break;
            }

        } else if (library_type != -1) {
            title.setText(library_name);
            HttpUtil.getInstance().getRequestApi().getLibraryTopList(library_type)
                    .compose(RxScheduler.Obs_io_main())
                    .subscribe(new BaseObserver<BaseResponse<List<BookBean>>>(this) {
                        @Override
                        public void onSuccess(BaseResponse<List<BookBean>> o) {
                            List<BaseBean> data = new ArrayList<>();
                            data.addAll(o.getData());
                            specialTypeBookAdapter.addItems(data);
                        }

                        @Override
                        public void onError(String msg) {

                        }
                    });
        } else if (library_recommend_type != -1) {
            title.setText(library_recommend_name);
            HttpUtil.getInstance().getRequestApi().getLibraryRecommendList(library_recommend_type)
                    .compose(RxScheduler.Obs_io_main())
                    .subscribe(new BaseObserver<BaseResponse<List<BookBean>>>(this) {
                        @Override
                        public void onSuccess(BaseResponse<List<BookBean>> o) {
                            List<BaseBean> data = new ArrayList<>();
                            data.addAll(o.getData());
                            specialTypeBookAdapter.addItems(data);
                        }

                        @Override
                        public void onError(String msg) {

                        }
                    });
        }
    }
}
