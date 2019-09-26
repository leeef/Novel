package com.bx.philosopher.model.bean.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

//bean基类
public class BaseBean implements Serializable, Parcelable {

    private static final long serialVersionUID = 3789549078363934485L;
    private int viewType;//设置显示样式

    protected BaseBean(Parcel in) {
        viewType = in.readInt();
    }

    public static final Creator<BaseBean> CREATOR = new Creator<BaseBean>() {
        @Override
        public BaseBean createFromParcel(Parcel in) {
            return new BaseBean(in);
        }

        @Override
        public BaseBean[] newArray(int size) {
            return new BaseBean[size];
        }
    };

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public BaseBean() {
    }

    public BaseBean(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(viewType);
    }
}
