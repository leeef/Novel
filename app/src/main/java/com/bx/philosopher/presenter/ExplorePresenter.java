package com.bx.philosopher.presenter;

import com.bx.philosopher.R;
import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.model.bean.response.BookPackage;
import com.bx.philosopher.model.bean.response.ExploreBanner;
import com.bx.philosopher.presenter.imp.ExploreImp;
import com.bx.philosopher.ui.adapter.view.ExploreExtraHolder;
import com.bx.philosopher.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 15:14
 */
public class ExplorePresenter extends BasePresenter<ExploreImp.View> implements ExploreImp.Presenter {



    //最多截取的数量
    private int[] itemCount = {3, 3, 4, 3, 6, 3, 4, 3, 3, 4, 4};
    //除去ranklist 和图片banner
    private int[] viewType = {ExploreExtraHolder.BOOK_ITEM2, ExploreExtraHolder.BOOK_ITEM3, ExploreExtraHolder.BOOK_ITEM1,
            ExploreExtraHolder.BOOK_ITEM3, ExploreExtraHolder.BOOK_ITEM2, ExploreExtraHolder.BOOK_ITEM3, ExploreExtraHolder.BOOK_ITEM1,
            ExploreExtraHolder.BOOK_ITEM2, ExploreExtraHolder.BOOK_ITEM3, ExploreExtraHolder.BOOK_ITEM1, ExploreExtraHolder.BOOK_ITEM3,};

    @Override
    public void getData() {
        HttpUtil.getInstance().getRequestApi().getExploreBanner()
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<ExploreBanner>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<ExploreBanner> o) {
                        mView.refreshBanner(o.getData());
                    }

                    @Override
                    public void onError(String msg) {
                        mView.showError(msg);
                    }
                });
        HttpUtil.getInstance().getRequestApi().getExploreList()
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<List<BookPackage>>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<List<BookPackage>> o) {
                        List<BookPackage> bookPackages = o.getData();
                        //获取图片banner  在列表最后
                        List<BookBean> recommond = bookPackages.get(bookPackages.size() - 1).getRecommend();

                        List<BaseBean> exploreList = new ArrayList<>();
                        for (int i = 0; i < bookPackages.size() - 1; i++) {
                            BookPackage bookPackage = bookPackages.get(i);
                            bookPackage.setViewType(ExploreExtraHolder.BOOK_TAG);

                            exploreList.add(bookPackage);
                            if (bookPackage.getList() != null && bookPackage.getList().size() <= itemCount[i]) {
                                bookPackage.setListType(viewType[i]);
                                exploreList.addAll(bookPackage.getList());
                            } else if (bookPackage.getList() != null && bookPackage.getList().size() > itemCount[i]) {
                                bookPackage.setListType(viewType[i]);
                                exploreList.addAll(bookPackage.getList().subList(0, itemCount[i]));
                            }
                            exploreList.add(new BookPackage(ExploreExtraHolder.BOOK_GRAY_LINE));
                            if (i == 0) {
                                exploreList.addAll(initRankList());
                                exploreList.add(new BookPackage(ExploreExtraHolder.BOOK_GRAY_LINE));
                            } else if (i == 1) {
                                exploreList.remove(exploreList.size() - 1);
                                exploreList.add(recommond.get(0));
                            } else if (i == 4) {
                                exploreList.remove(exploreList.size() - 1);
                                exploreList.add(recommond.get(1));
                            } else if (i == 7) {
                                exploreList.remove(exploreList.size() - 1);
                                exploreList.add(recommond.get(2));
                            }

                        }
                        mView.refreshList(exploreList);

                    }

                    @Override
                    public void onError(String msg) {
                        mView.showError(msg);

                    }
                });

    }

    private List<BaseBean> initRankList() {
        List<BaseBean> rankList = new ArrayList<>();
        BookPackage bookPackage = new BookPackage(ExploreExtraHolder.BOOK_TAG);
        bookPackage.setName(Constant.EXPLORE_RANK_NAME);
        BookBean scienceBook = getRankBook("Science Fiction", bookPackage.getName(), R.drawable.explore_image_1 + "", 1);
        BookBean classicBook = getRankBook("Romance", bookPackage.getName(), R.drawable.explore_image_2 + "", 2);
        BookBean artBook = getRankBook("Fantasy", bookPackage.getName(), R.drawable.explore_image_3 + "", 3);
        BookBean fictionBook = getRankBook("Story", bookPackage.getName(), R.drawable.explore_image_4 + "", 4);
        rankList.add(bookPackage);
        rankList.add(scienceBook);
        rankList.add(classicBook);
        rankList.add(artBook);
        rankList.add(fictionBook);
        return rankList;
    }

    private BookBean getRankBook(String type, String tag, String cover, int rank_type) {
        BookBean bookBean = new BookBean(ExploreExtraHolder.IMAGE_TWO);
        bookBean.setTitle(tag);
        bookBean.setAuthor(type);
        bookBean.setCover(cover);
        bookBean.setId(rank_type);
        return bookBean;
    }
}
