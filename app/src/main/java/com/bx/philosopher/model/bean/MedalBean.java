package com.bx.philosopher.model.bean;

import com.bx.philosopher.model.bean.response.BaseBean;

import java.io.Serializable;

public class MedalBean extends BaseBean implements Serializable {

    private static final long serialVersionUID = -3673157324288826129L;
    private String name;
    private int image_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public MedalBean(int viewType) {
        super(viewType);
    }
}
