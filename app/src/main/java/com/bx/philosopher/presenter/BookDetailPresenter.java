package com.bx.philosopher.presenter;

import android.app.Activity;
import android.util.Log;

import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.BookDetailBean;
import com.bx.philosopher.model.bean.response.BookPackage;
import com.bx.philosopher.model.dao.BookDaoManager;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.BookDetailImp;
import com.bx.philosopher.ui.activity.BookDetailActivity;
import com.bx.philosopher.ui.adapter.view.ExploreExtraHolder;
import com.bx.philosopher.utils.FileUtils;
import com.bx.philosopher.utils.ToastUtils;
import com.bx.philosopher.utils.login.LoginUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 15:17
 */
public class BookDetailPresenter extends BasePresenter<BookDetailImp.View> implements BookDetailImp.Presenter {
    String types[] = {"sales", "hits", "collect"};
    private Subscription mChapterSub;

    @Override
    public void getData(String type, int bookId) {
        switch (type) {
            case "type_explore":
                HttpUtil.getInstance().getRequestApi().getExploreBookDetail(bookId, LoginUtil.getUserId())
                        .compose(RxScheduler.Obs_io_main())
                        .subscribe(new BaseObserver<BaseResponse<BookDetailBean>>(mView) {
                            @Override
                            public void onSuccess(BaseResponse<BookDetailBean> o) {
                                BookDetailBean bookDetailBean = o.getData();
                                List<BaseBean> baseBeans = new ArrayList<>();
                                for (String type : types) {
                                    baseBeans.add(new BaseBean(ExploreExtraHolder.BOOK_GRAY_LINE));
                                    BookPackage bookPackage = new BookPackage(ExploreExtraHolder.BOOK_TAG);
                                    bookPackage.setName(type);
                                    baseBeans.add(bookPackage);
                                    switch (type) {
                                        case "sales":
                                            bookDetailBean.setSalesType(ExploreExtraHolder.BOOK_ITEM2);
                                            baseBeans.addAll(bookDetailBean.getSales());
                                            break;
                                        case "hits":
                                            bookDetailBean.setHitsType(ExploreExtraHolder.BOOK_ITEM2);
                                            baseBeans.addAll(bookDetailBean.getHits());
                                            break;
                                        case "collect":
                                            bookDetailBean.setCollectType(ExploreExtraHolder.BOOK_ITEM2);
                                            baseBeans.addAll(bookDetailBean.getCollect());
                                            break;
                                    }
                                }
                                mView.showData(bookDetailBean, baseBeans);
                            }

                            @Override
                            public void onError(String msg) {

                            }
                        });
                break;
            case "type_library":
                HttpUtil.getInstance().getRequestApi().getLibraryBookDetail(bookId, LoginUtil.getUserId())
                        .compose(RxScheduler.Obs_io_main())
                        .subscribe(new BaseObserver<BaseResponse<BookDetailBean>>(mView) {
                            @Override
                            public void onSuccess(BaseResponse<BookDetailBean> o) {
                                BookDetailBean bookDetailBean = o.getData();
                                List<BaseBean> baseBeans = new ArrayList<>();
                                for (String type : types) {
                                    baseBeans.add(new BaseBean(ExploreExtraHolder.BOOK_GRAY_LINE));
                                    BookPackage bookPackage = new BookPackage(ExploreExtraHolder.BOOK_TAG);
                                    bookPackage.setName(type);
                                    baseBeans.add(bookPackage);
                                    switch (type) {
                                        case "sales":
                                            bookDetailBean.setSalesType(ExploreExtraHolder.BOOK_ITEM2);
                                            baseBeans.addAll(bookDetailBean.getSales());
                                            break;
                                        case "hits":
                                            bookDetailBean.setHitsType(ExploreExtraHolder.BOOK_ITEM2);
                                            baseBeans.addAll(bookDetailBean.getHits());
                                            break;
                                        case "collect":
                                            bookDetailBean.setCollectType(ExploreExtraHolder.BOOK_ITEM2);
                                            baseBeans.addAll(bookDetailBean.getCollect());
                                            break;
                                    }
                                }
                                mView.showData(bookDetailBean, baseBeans);
                            }

                            @Override
                            public void onError(String msg) {

                            }
                        });
                break;
        }
    }

    @Override
    public void wantSee(int bookId, int favorite, String from_type) {
//        switch (from_type) {
//            case "type_explore":
//                break;
//            case "type_library":
        HttpUtil.getInstance().getRequestApi().getLibraryBookDetailWantSee(bookId, LoginUtil.getUserId())
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<String>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<String> o) {
                        mView.showWantSee(o.getData(), o.getMsg());
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
//                break;

//        }

    }

    @Override
    public void addBookToShelf(int id) {
        HttpUtil.getInstance().getRequestApi().addBookToShelf(LoginUtil.getUserId(), id)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<Object>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<Object> o) {
                        ToastUtils.show(o.getMsg());
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    //下载整本书籍
    @Override
    public void download(List<String> bookChapters, int bookId, String from_type, Activity activity) {
        int size = bookChapters.size();

        //取消上次的任务，防止多次加载
        if (mChapterSub != null) {
            mChapterSub.cancel();
        }

        List<Single<BaseResponse<JsonObject>>> chapterInfos = new ArrayList<>(bookChapters.size());
        ArrayDeque<String> titles = new ArrayDeque<>(bookChapters.size());

        // 将要下载章节，转换成网络请求。
        for (int i = 0; i < size; ++i) {
            String bookChapter = bookChapters.get(i);
            //被缓存过 跳过
            if (FileUtils.isChapterCached(bookId + "", bookChapter)) continue;
            Single<BaseResponse<JsonObject>> chapterInfoSingle;
            if (from_type.equals(BookDetailActivity.TYPE_EXPLORE)) {
                // 网络中获取数据
                chapterInfoSingle = HttpUtil.getInstance()
                        .getRequestApi().getExploreBookChapter(bookId, bookChapter);
            } else {
                // 网络中获取数据
                chapterInfoSingle = HttpUtil.getInstance()
                        .getRequestApi().getLibraryBookChapter(bookId, bookChapter);
            }


            chapterInfos.add(chapterInfoSingle);

            titles.add(bookChapter);
        }
        if (chapterInfos.size() == 0) {
            activity.runOnUiThread(() -> ToastUtils.show("The book has been downloaded"));
        } else {

            ToastUtils.show("Has been downloaded in the background");
            Single.concat(chapterInfos)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            new Subscriber<BaseResponse<JsonObject>>() {
                                String title = titles.poll();

                                @Override
                                public void onSubscribe(Subscription s) {
                                    s.request(Integer.MAX_VALUE);
                                    mChapterSub = s;
                                }

                                @Override
                                public void onNext(BaseResponse<JsonObject> result) {
                                    if (result.getCode() == 200) {

                                        //存储数据
                                        BookDaoManager.saveChapterInfo(
                                                bookId + "", title, result.getData().get(title).getAsString());
                                        //将获取到的数据进行存储
                                        title = titles.poll();
                                    }
                                    if (titles.size() == 0) {
                                        activity.runOnUiThread(() -> {
                                            //下载完成
                                            ToastUtils.show("Cache complete!");
                                            addMyDownload(bookId);
                                        });
                                    }

                                }

                                @Override
                                public void onError(Throwable t) {
                                    //只有第一个加载失败才会调用errorChapter
                                    if (bookChapters.get(0).equals(title)) {
                                        ToastUtils.show("The book cache failed. Please check the network");
                                    }
//                                LogUtils.e(t);
                                }

                                @Override
                                public void onComplete() {

                                }
                            }
                    );
        }
    }

    @Override
    public void loadBook(int bookId, String from_type, Activity activity) {
        switch (from_type) {
            case BookDetailActivity.TYPE_EXPLORE:
                HttpUtil.getInstance().getRequestApi().getExploreBookDetailCatalog(LoginUtil.getUserId(),bookId)
                        .compose(RxScheduler.Obs_io_main())
                        .subscribe(new BaseObserver<BaseResponse<List<String>>>(mView) {
                            @Override
                            public void onSuccess(BaseResponse<List<String>> o) {
                                List<String> catalogs = o.getData().subList(2, o.getData().size() - 1);
                                download(catalogs, bookId, from_type, activity);
                            }

                            @Override
                            public void onError(String msg) {

                            }
                        });
                break;
            case BookDetailActivity.TYPE_LIBRARY:
                HttpUtil.getInstance().getRequestApi().getLibraryBookCatelog(bookId)
                        .compose(RxScheduler.Obs_io_main())
                        .subscribe(new BaseObserver<BaseResponse<List<String>>>(mView) {
                            @Override
                            public void onSuccess(BaseResponse<List<String>> o) {
                                List<String> catalogs = o.getData().subList(2, o.getData().size());
                                download(catalogs, bookId, from_type, activity);
                            }

                            @Override
                            public void onError(String msg) {

                            }
                        });
                break;
        }
    }

    public void addMyDownload(int bookId) {
        HttpUtil.getInstance().getRequestApi().addMyDownLoad(LoginUtil.getUserId(), bookId)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<JsonElement>>(null) {
                    @Override
                    public void onSuccess(BaseResponse<JsonElement> o) {
                        Log.i("download", "添加成功");
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }


}
