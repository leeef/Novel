package com.bx.philosopher.model.bean.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: ExploreBanner
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/5/30 9:04
 */
public class ExploreBanner implements Serializable {
    private static final long serialVersionUID = 8672912069179127857L;
    private List<BookBean> recommend;
    private List<BookBean> newbook;
    private List<BookBean> today;
    private List<CardBean> card;

    public List<BookBean> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<BookBean> recommend) {
        this.recommend = recommend;
    }

    public void setRecommendType(int viewType) {
        for (BookBean bookBean : recommend) {
            bookBean.setViewType(viewType);
        }
    }

    public List<BookBean> getNewbook() {
        return newbook;
    }

    public void setNewbook(List<BookBean> newbook) {
        this.newbook = newbook;
    }

    public void setNewBookType(int viewType) {
        for (BookBean bookBean : newbook) {
            bookBean.setViewType(viewType);
        }
    }


    public List<BookBean> getToday() {
        return today;
    }

    public void setToday(List<BookBean> today) {
        this.today = today;
    }

    public void setTodayType(int viewType) {
        for (BookBean bookBean : today) {
            bookBean.setViewType(viewType);
        }
    }

    public List<CardBean> getCard() {
        return card;
    }

    public void setCard(List<CardBean> card) {
        this.card = card;
    }
}
