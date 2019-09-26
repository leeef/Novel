package com.bx.philosopher.model.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.StringUtils;

import java.io.Serializable;

/**
 * @ClassName: BookBean
 * @Description: 图书
 * @Author: leeeeef
 * @CreateDate: 2019/5/29 18:08
 */
public class BookBean extends BaseBean implements Serializable, Parcelable {

    private static final long serialVersionUID = 7119863206253457963L;
    /**
     * id : 106
     * title : After-the-Cure
     * author : Mark
     * cover : /uploads/20190524/7222.jpg
     * hits : 22
     * brief : 关于世界各地的景点介绍
     * money : 2.00
     * discounts : 0.00
     * copyright : China
     * boardid :1  是否需要绘制封面
     * <p>
     * <p>
     * press 图书馆分类列表图书字段
     * presstime 图书馆分类列表图书字段
     * name 图书馆分类列表图书字段
     * number 0 图书馆书籍详情字段
     * <p>
     * sales 99 图书馆书籍详情字段
     * <p>
     * show_type 展示类型字段 本地字段
     * typeid 1 探索 2 图书馆
     * 我的下载
     * uid  用户id
     * bid 书籍id
     */

    private int id;
    private int bid;
    private int uid;
    private String title;
    private String author;
    private String cover;
    private int hits;
    private String brief;
    private String money;
    private String discounts;
    private String copyright;
    private String press;
    private String presstime;
    private String name;
    private int number;
    private int boardid;
    private int sales;
    private int typeid;
    private String show_type;

    public BookBean(int viewType) {
        super(viewType);
    }

    protected BookBean(Parcel in) {
        id = in.readInt();
        title = in.readString();
        author = in.readString();
        cover = in.readString();
        hits = in.readInt();
        brief = in.readString();
        money = in.readString();
        discounts = in.readString();
        copyright = in.readString();
        press = in.readString();
        presstime = in.readString();
        name = in.readString();
        number = in.readInt();
        boardid = in.readInt();
        sales = in.readInt();
        show_type = in.readString();
    }

    public static final Creator<BookBean> CREATOR = new Creator<BookBean>() {
        @Override
        public BookBean createFromParcel(Parcel in) {
            return new BookBean(in);
        }

        @Override
        public BookBean[] newArray(int size) {
            return new BookBean[size];
        }
    };

    public int getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
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

    public String getCover() {
        if (!StringUtils.isTrimEmpty(cover)) {
            if (cover.startsWith(Constant.CLIENT_HEAD)) {
                return cover;
            } else {
                return Constant.CLIENT_URL + cover;
            }
        }
        return cover;
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getDiscounts() {
        return discounts;
    }

    public void setDiscounts(String discounts) {
        this.discounts = discounts;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getShow_type() {
        if (show_type == null) return "";
        return show_type;
    }

    public void setShow_type(String show_type) {
        this.show_type = show_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(cover);
        dest.writeInt(hits);
        dest.writeString(brief);
        dest.writeString(money);
        dest.writeString(discounts);
        dest.writeString(copyright);
        dest.writeString(press);
        dest.writeString(presstime);
        dest.writeString(name);
        dest.writeInt(number);
        dest.writeInt(boardid);
        dest.writeInt(sales);
        dest.writeString(show_type);
    }
}
