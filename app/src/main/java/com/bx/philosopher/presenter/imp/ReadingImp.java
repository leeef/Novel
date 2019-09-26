package com.bx.philosopher.presenter.imp;

import com.bx.philosopher.base.activity.BaseContract;
import com.bx.philosopher.model.bean.response.BaseBean;
import com.bx.philosopher.model.bean.response.BookMark;

import java.util.List;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/20 17:36
 */
public interface ReadingImp {
    interface View extends BaseContract.BaseView {
        void showCategory(List<String> bookChapterList);

        void finishChapter();

        void errorChapter();

        void showMark(List<BaseBean> bookMark);

        void addMarkFinish();

        void addNoteFinish();

        void deleteMarkFinish();

        void deleteNoteFinish();

        void showAutoMark(BookMark bookMark);

        void showNote(List<BaseBean> bookNote);

        void hadPay(boolean pay);


    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void getData(int bookId);

        void loadChapter(int bookId, List<String> bookChapterList, String from_type);

        void loadBook(int bookID, String from_type);

        //加载书签
        void loadBookMark(int bookID);

        void loadAutoMark(int bookId);

        void addBookMark(int bid, String chapter, int site, String content);

        void addAutoBookMark(int bid, String chapter, int site, String content);

        void deleteBookMark(List<Integer> mid);

        void addBookNote(int bid, String chapter, int startSite, int endSite, String content, String note);

        void loadBookNote(int bookID);

        void deleteBookNote(int nid);
    }
}
