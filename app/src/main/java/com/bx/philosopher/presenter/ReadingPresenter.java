package com.bx.philosopher.presenter;

import android.app.Activity;
import android.util.Log;

import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.BookMark;
import com.bx.philosopher.model.bean.response.BookMarkPackage;
import com.bx.philosopher.model.dao.BookDaoManager;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.ReadingImp;
import com.bx.philosopher.ui.activity.BookDetailActivity;
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
 * @CreateDate: 2019/5/20 17:36
 */
public class ReadingPresenter extends BasePresenter<ReadingImp.View> implements ReadingImp.Presenter {

    private Subscription mChapterSub;
    private Subscription downloadSub;

    @Override
    public void getData(int bookid) {
        HttpUtil.getInstance().getRequestApi().getExploreBookDetailContent(117)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<JsonElement>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<JsonElement> o) {
                        o.toString();
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    /**
     * @param bookId       图书id
     * @param bookChapters 图书目录
     */
    @Override
    public void loadChapter(int bookId, List<String> bookChapters, String from_type) {
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
                                    mView.finishChapter();
                                    //将获取到的数据进行存储
                                    title = titles.poll();
                                }

                            }

                            @Override
                            public void onError(Throwable t) {
                                //只有第一个加载失败才会调用errorChapter
                                if (bookChapters.get(0).equals(title)) {
                                    mView.errorChapter();
                                }
//                                LogUtils.e(t);
                            }

                            @Override
                            public void onComplete() {

                            }
                        }
                );
    }

    @Override
    public void loadBook(int bookId, String from_type) {
        switch (from_type) {
            case BookDetailActivity.TYPE_EXPLORE:
                HttpUtil.getInstance().getRequestApi().getExploreBookDetailCatalog(LoginUtil.getUserId(), bookId)
                        .compose(RxScheduler.Obs_io_main())
                        .subscribe(new BaseObserver<BaseResponse<List<String>>>(mView) {
                            @Override
                            public void onSuccess(BaseResponse<List<String>> o) {
                                List<String> catalogs = o.getData().subList(2, o.getData().size() - 1);
                                mView.hadPay(o.getData().get(o.getData().size() - 1).equals("1"));
                                mView.showCategory(catalogs);
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
                                mView.showCategory(catalogs);
                            }

                            @Override
                            public void onError(String msg) {

                            }
                        });
                break;
        }
    }

    @Override
    public void loadBookMark(int bookID) {
        HttpUtil.getInstance().getRequestApi().getBookMark(LoginUtil.getUserId(), bookID)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<List<BookMarkPackage>>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<List<BookMarkPackage>> o) {
                        List<BaseBean> bookMark = new ArrayList<>();
                        for (int i = 0; i < o.getData().size(); i++) {
                            BookMarkPackage bookMarkPackage = o.getData().get(i);
                            bookMarkPackage.setViewType(1);
                            bookMarkPackage.setIndex(i + 1);
                            bookMark.add(bookMarkPackage);
                            bookMark.addAll(bookMarkPackage.getList());
                        }
                        mView.showMark(bookMark);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void loadAutoMark(int bookId) {
        HttpUtil.getInstance().getRequestApi().getAutoBookmark(LoginUtil.getUserId(), bookId)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<BookMark>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<BookMark> o) {
                        mView.showAutoMark(o.getData());
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void addBookMark(int bid, String chapter, int site, String content) {
        HttpUtil.getInstance().getRequestApi().setBookMark(LoginUtil.getUserId(), bid, chapter, site, content)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<Boolean>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> o) {
                        if (o.getData()) {
                            mView.addMarkFinish();
                            ToastUtils.show(o.getMsg());
                        }
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void addAutoBookMark(int bid, String chapter, int site, String content) {
        HttpUtil.getInstance().getRequestApi().setAutoBookMark(LoginUtil.getUserId(), bid, chapter, site, content)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<Boolean>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> o) {
                        o.toString();
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void deleteBookMark(List<Integer> mid) {
        HttpUtil.getInstance().getRequestApi().deleteBookMark(LoginUtil.getUserId(), mid)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<Integer>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<Integer> o) {
                        if (o.getData() > 0) {
                            mView.deleteMarkFinish();
                            ToastUtils.show(o.getMsg());
                        }
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void addBookNote(int bid, String chapter, int startSite, int endSite, String content, String note) {
        HttpUtil.getInstance().getRequestApi().setBookNote(LoginUtil.getUserId(), bid, chapter, startSite, endSite, content, note)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<Boolean>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> o) {
                        if (o.getData()) {
                            mView.addNoteFinish();
                            ToastUtils.show(o.getMsg());
                        }
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void loadBookNote(int bookID) {
        HttpUtil.getInstance().getRequestApi().getBookNote(LoginUtil.getUserId(), bookID)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<List<BookMarkPackage>>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<List<BookMarkPackage>> o) {
                        List<BaseBean> bookNote = new ArrayList<>();
                        for (int i = 0; i < o.getData().size(); i++) {
                            BookMarkPackage bookMarkPackage = o.getData().get(i);
                            bookMarkPackage.setViewType(1);
                            bookMarkPackage.setIndex(i + 1);
                            bookNote.add(bookMarkPackage);
                            bookNote.addAll(bookMarkPackage.getList());
                        }
                        mView.showNote(bookNote);
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }

    @Override
    public void deleteBookNote(int nid) {
        HttpUtil.getInstance().getRequestApi().deleteBookNote(LoginUtil.getUserId(), nid)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<Integer>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<Integer> o) {
                        if (o.getData() > 0) {
                            mView.deleteNoteFinish();
                            ToastUtils.show(o.getMsg());
                        }
                    }

                    @Override
                    public void onError(String msg) {

                    }
                });
    }


    public void downLoadBook(int bookId, String from_type, Activity activity) {
        switch (from_type) {
            case BookDetailActivity.TYPE_EXPLORE:
                HttpUtil.getInstance().getRequestApi().getExploreBookDetailCatalog(LoginUtil.getUserId(), bookId)
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


    public void download(List<String> bookChapters, int bookId, String from_type, Activity activity) {
        int size = bookChapters.size();

        //取消上次的任务，防止多次加载
        if (downloadSub != null) {
            downloadSub.cancel();
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
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.show("The book has been downloaded");
                }
            });

        } else {

            Single.concat(chapterInfos)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            new Subscriber<BaseResponse<JsonObject>>() {
                                String title = titles.poll();

                                @Override
                                public void onSubscribe(Subscription s) {
                                    s.request(Integer.MAX_VALUE);
                                    downloadSub = s;
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
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //下载完成
                                                ToastUtils.show("Cache complete!");
                                                addMyDownload(bookId);
                                            }
                                        });
                                    }

                                }

                                @Override
                                public void onError(Throwable t) {
                                    //只有第一个加载失败才会调用errorChapter
                                    if (bookChapters.get(0).equals(title)) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtils.show("The book cache failed. Please check the network");
                                            }
                                        });

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

    public void addMyDownload(int bookId) {
        HttpUtil.getInstance().getRequestApi().addMyDownLoad(LoginUtil.getUserId(), bookId)
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<JsonElement>>(mView) {
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
