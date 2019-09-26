package com.bx.philosopher.model.bean.response;

import java.io.Serializable;

/**
 * @ClassName: LibraryBannerBean
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/3 16:28
 */
public class LibraryBannerBean implements Serializable {

    private static final long serialVersionUID = 7235548952165529135L;


    /**
     * id : 5
     * img : /uploads/20190525/5ce8b80b6a84c.jpeg
     * addtime : 1558755339
     */

    private int id;
    private String img;
    private String addtime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }
}
