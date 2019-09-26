package com.bx.philosopher.model.bean.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: BookMarkPackage
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/10 17:28
 */
public class BookMarkPackage extends BaseBean implements Serializable {
    private static final long serialVersionUID = 6761964392202494414L;
    private String chapter;
    private List<BookMark> list;

    private int index;

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public List<BookMark> getList() {
        return list;
    }

    public void setList(List<BookMark> list) {
        this.list = list;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
