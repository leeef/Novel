package com.bx.philosopher.model.bean.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: IntroduceBean
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/5 11:59
 */
public class IntroduceBean implements Serializable {
    private static final long serialVersionUID = -3365963315103262203L;

    /**
     * id : 13
     * cover : /uploads/20190525/5ce8a2b576d90.jpeg
     * title : app小说
     * detail : 小说介绍epub
     * email : 522911865@qq.com
     */

    private int id;
    private String cover;
    private String title;
    private String detail;
    private String email;

    public static List<IntroduceBean> arrayIntroduceBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<IntroduceBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
