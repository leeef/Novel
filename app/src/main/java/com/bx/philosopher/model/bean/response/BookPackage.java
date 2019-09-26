package com.bx.philosopher.model.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.bx.philosopher.ui.adapter.view.ExploreExtraHolder;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: BookPackage
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/5/29 18:37
 */
public class BookPackage extends BaseBean implements Serializable, Parcelable {

    private static final long serialVersionUID = -6906338828492678202L;
    /**
     * name : Correspondence
     * id : 32
     */

    private String name;
    private int id;

    private List<BookBean> list;

    private List<BookBean> recommend;

    private List<BookPackage> cate;

    public BookPackage(int viewType) {
        super(viewType);
    }


    protected BookPackage(Parcel in) {
        name = in.readString();
        id = in.readInt();
        list = in.createTypedArrayList(BookBean.CREATOR);
        recommend = in.createTypedArrayList(BookBean.CREATOR);
    }

    public static final Creator<BookPackage> CREATOR = new Creator<BookPackage>() {
        @Override
        public BookPackage createFromParcel(Parcel in) {
            return new BookPackage(in);
        }

        @Override
        public BookPackage[] newArray(int size) {
            return new BookPackage[size];
        }
    };

    public List<BookBean> getList() {
        return list;
    }

    public void setList(List<BookBean> list) {
        this.list = list;
    }

    public void setListType(int viewType) {
        for (BookBean bookBean : list) {
            bookBean.setViewType(viewType);
        }
    }

    public List<BookPackage> getCate() {
        return cate;
    }

    public void setCate(List<BookPackage> cate) {
        this.cate = cate;
    }

    public void setListShowType(String showType) {
        for (BookBean bookBean : list) {
            bookBean.setShow_type(showType);
        }
    }

    public List<BookBean> getRecommend() {
        if (recommend != null) {
            for (BookBean bookBean : recommend) {
                bookBean.setViewType(ExploreExtraHolder.IMAGE_ONE);
            }
        }
        return recommend;
    }

    public void setRecommend(List<BookBean> recommend) {
        this.recommend = recommend;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(id);
        dest.writeTypedList(list);
        dest.writeTypedList(recommend);
    }
}
