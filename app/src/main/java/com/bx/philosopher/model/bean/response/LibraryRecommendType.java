package com.bx.philosopher.model.bean.response;

import java.io.Serializable;

/**
 * @ClassName: LibraryRecommendType
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/10 15:10
 */
public class LibraryRecommendType extends BaseBean implements Serializable {
    private static final long serialVersionUID = -473736777102636187L;

    /**
     * id : 37
     * name : 文学
     */

    private int id;
    private String name;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
