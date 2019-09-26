package com.bx.philosopher.model.bean.response;

import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: BookDetailBean
 * @Description: 图书馆图书详情
 * @Author: leeeeef
 * @CreateDate: 2019/6/4 11:17
 */
public class BookDetailBean implements Serializable {
    private static final long serialVersionUID = 4224023370898015731L;

    /**
     * title : ZHENGZHOU8
     * author : ZHENGZHOU
     * cover : http://www.jiawenjie.com/uploads/20190525/5ce8a469b2845.jpeg
     * boardid : 2
     * brief : ZHENGZHOUZHENGZHOU
     * number : 85881
     * money : 6.00
     * press : ZHENGZHOU
     * presstime : 1559491200
     * name : 艺术
     *
     * discounts 0.00
     * favorite 1
     */

    private String title;
    private String author;
    private String cover;
    private int boardid;
    private String brief;
    private int number;
    private int favorite;
    private String money;
    private String discounts;
    private String press;
    private String presstime;
    private String name;
    private List<BookBean> sales;
    private List<BookBean> hits;
    private List<BookBean> collect;

    public static List<BookDetailBean> arrayBookDetailBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<BookDetailBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public String getCover() {
        if (!StringUtils.isTrimEmpty(cover)) {
            if (cover.startsWith(Constant.CLIENT_URL)) {
                return cover;
            } else {
                return Constant.CLIENT_URL + cover;
            }
        }
        return cover;
    }

    public String getDiscounts() {
        return discounts;
    }

    public void setDiscounts(String discounts) {
        this.discounts = discounts;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getBoardid() {
        return boardid;
    }

    public void setBoardid(int boardid) {
        this.boardid = boardid;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getPresstime() {
        return presstime;
    }

    public void setPresstime(String presstime) {
        this.presstime = presstime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BookBean> getSales() {
        return sales;
    }

    public void setSalesType(int viewType) {
        for (BookBean bookBean : sales) {
            bookBean.setViewType(viewType);
        }
    }

    public void setSales(List<BookBean> sales) {
        this.sales = sales;
    }

    public List<BookBean> getHits() {
        return hits;
    }

    public void setHits(List<BookBean> hits) {
        this.hits = hits;
    }

    public void setHitsType(int viewType) {
        for (BookBean bookBean : hits) {
            bookBean.setViewType(viewType);
        }
    }

    public List<BookBean> getCollect() {
        return collect;
    }

    public void setCollect(List<BookBean> collect) {
        this.collect = collect;
    }

    public void setCollectType(int viewType) {
        for (BookBean bookBean : collect) {
            bookBean.setViewType(viewType);
        }
    }
}
