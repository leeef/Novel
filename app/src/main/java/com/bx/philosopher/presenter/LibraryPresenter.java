package com.bx.philosopher.presenter;

import com.bx.philosopher.base.activity.BasePresenter;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BaseResponse;
import com.bx.philosopher.model.bean.response.BookBean;
import com.bx.philosopher.model.bean.response.BookPackage;
import com.bx.philosopher.model.bean.response.LibraryBannerBean;
import com.bx.philosopher.net.BaseObserver;
import com.bx.philosopher.net.HttpUtil;
import com.bx.philosopher.net.RxScheduler;
import com.bx.philosopher.presenter.imp.LibraryImp;
import com.bx.philosopher.ui.adapter.view.ExploreExtraHolder;
import com.bx.philosopher.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 15:08
 */
public class LibraryPresenter extends BasePresenter<LibraryImp.View> implements LibraryImp.Presenter {

    //最多截取的数量 不包括Recommened
    private int[] itemCount = {3, 3, 3, 4, 3, 3, 4, 6, 3, 4, 3, 4, 3, 6, 4};
    //展示的类型
    private int[] viewTypes = {ExploreExtraHolder.BOOK_ITEM2, ExploreExtraHolder.BOOK_ITEM3,
            ExploreExtraHolder.BOOK_ITEM2, ExploreExtraHolder.BOOK_ITEM1, ExploreExtraHolder.BOOK_ITEM2, ExploreExtraHolder.BOOK_ITEM3,
            ExploreExtraHolder.BOOK_ITEM1, ExploreExtraHolder.BOOK_ITEM2, ExploreExtraHolder.BOOK_ITEM3, ExploreExtraHolder.BOOK_ITEM1,
            ExploreExtraHolder.BOOK_ITEM2, ExploreExtraHolder.BOOK_ITEM1, ExploreExtraHolder.BOOK_ITEM3, ExploreExtraHolder.BOOK_ITEM2,
            ExploreExtraHolder.BOOK_ITEM3,};

    @Override
    public void getData() {
        HttpUtil.getInstance().getRequestApi().getLibraryBanner()
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<List<LibraryBannerBean>>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<List<LibraryBannerBean>> o) {
                        List<LibraryBannerBean> bannerBeans = o.getData();
                        List<String> pics = new ArrayList<>();
                        for (LibraryBannerBean libraryBannerBean : bannerBeans) {
                            pics.add(Constant.CLIENT_URL + libraryBannerBean.getImg());
                        }
                        mView.showBanner(pics);
                    }

                    @Override
                    public void onError(String msg) {
                        mView.showError(msg);
                    }
                });

        HttpUtil.getInstance().getRequestApi().getLibraryList()
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<List<BookPackage>>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<List<BookPackage>> o) {
                        List<BookPackage> bookPackages = o.getData();
                        //获取图片banner  在列表倒数第二个
                        List<BookBean> recommond = bookPackages.get(bookPackages.size() - 2).getRecommend();
                        List<BookPackage> cate = bookPackages.get(bookPackages.size() - 1).getCate();
                        for (BookPackage bookPackage : cate) {
                            bookPackage.setViewType(ExploreExtraHolder.BOOK_ITEM4);
                        }


                        List<BaseBean> libraryList = new ArrayList<>();
                        for (int i = 0; i < bookPackages.size() - 2; i++) {
                            if (i == 0) {
                                recommond.get(0).setViewType(ExploreExtraHolder.IMAGE_TWO);
                                recommond.get(1).setViewType(ExploreExtraHolder.IMAGE_TWO);
                                libraryList.add(recommond.get(0));
                                libraryList.add(recommond.get(1));
                            }

                            BookPackage bookPackage = bookPackages.get(i);
                            boolean hasList = false;
                            if (bookPackage.getList() != null && bookPackage.getList().size() > 0) {
                                hasList = true;
                                bookPackage.setViewType(ExploreExtraHolder.BOOK_TAG);
                                libraryList.add(bookPackage);

                                int viewType;
                                int spanSize;
                                if (i > itemCount.length - 1) {
                                    viewType = viewTypes[itemCount.length - 1];
                                    spanSize = itemCount[itemCount.length - 1];
                                } else {
                                    viewType = viewTypes[i];
                                    spanSize = itemCount[i];
                                }

                                if (bookPackage.getList() != null && bookPackage.getList().size() <= spanSize) {
                                    bookPackage.setListType(viewType);
                                    libraryList.addAll(bookPackage.getList());
                                } else if (bookPackage.getList() != null && bookPackage.getList().size() > spanSize) {
                                    bookPackage.setListType(viewType);
                                    libraryList.addAll(bookPackage.getList().subList(0, spanSize));
                                }
                                libraryList.add(new BookPackage(ExploreExtraHolder.BOOK_GRAY_LINE));
                            }
                            if (i == 1) {//recommended 四个模块

                                BookPackage recommend = new BookPackage(ExploreExtraHolder.BOOK_TAG);
                                recommend.setName("Recommended");
                                libraryList.add(recommend);
                                libraryList.addAll(cate);
                            } else if (i == 2) {
                                if (recommond.size() < 3) continue;
                                if (hasList) libraryList.remove(libraryList.size() - 1);//去除灰线
                                recommond.get(2).setViewType(ExploreExtraHolder.IMAGE_ONE);
                                libraryList.add(recommond.get(2));
                            } else if (i == 6) {
                                if (recommond.size() < 4) continue;
                                if (hasList) libraryList.remove(libraryList.size() - 1);
                                recommond.get(3).setViewType(ExploreExtraHolder.IMAGE_ONE);
                                libraryList.add(recommond.get(3));
                            } else if (i == 10) {
                                if (recommond.size() < 5) continue;
                                if (hasList) libraryList.remove(libraryList.size() - 1);
                                recommond.get(4).setViewType(ExploreExtraHolder.IMAGE_ONE);
                                libraryList.add(recommond.get(4));
                            }

                        }
                        mView.showList(libraryList);
                    }

                    @Override
                    public void onError(String msg) {
                        mView.showError(msg);
                    }
                });

        HttpUtil.getInstance().getRequestApi().getLibraryTop()
                .compose(RxScheduler.Obs_io_main())
                .subscribe(new BaseObserver<BaseResponse<List<BookBean>>>(mView) {
                    @Override
                    public void onSuccess(BaseResponse<List<BookBean>> o) {
                        mView.showTopList(o.getData());
                    }

                    @Override
                    public void onError(String msg) {
                        mView.showError(msg);
                    }
                });
    }


    private List<BaseBean> initRecommoned() {

        return initData(4, ExploreExtraHolder.BOOK_ITEM4, "Recommended");
    }

    private List<BaseBean> initData(int size, int show_type, String type) {
        List<BaseBean> data = new ArrayList<>();
        BookPackage bookPackage = new BookPackage(ExploreExtraHolder.BOOK_TAG);
        bookPackage.setName(type);
        data.add(bookPackage);
        for (int i = 1; i <= size; i++) {
            BookBean bookBean = new BookBean(show_type);
            bookBean.setTitle("book_name" + i);
            bookBean.setAuthor("book_author" + i);
            bookBean.setBrief("Hollywood turned them into superheroes and supervillains.");
            bookBean.setCover("/uploads/20190524/7222.jpg");
            data.add(bookBean);
        }
        return data;
    }
}
