package com.bx.philosopher.model.bean.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: BookMark
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/10 17:26
 */
public class BookMark extends BaseBean implements Serializable {
    private static final long serialVersionUID = 6864822723205684377L;

    /**
     * id : 4
     * uid : 1
     * bid : 117
     * chapter : Chapter 5
     * site : 26
     * content : disabling her engines. When the frigate Orion stopped the convoy to allow the crippled vessel to make repairs, they were hit by a Kurgan raiding party. Once the Orion was taken out of action, the remainder of the ships in the convoy were picked off one by one as they tried to escape. She made a note to inform her superiors to abandon any ship that couldn&#8217;t keep up with the others from now on. It was a cold move,
     */

    private int id;
    private int uid;
    private int bid;
    private String chapter;
    private String site;
    private String content;

    //笔记
    private String startsite;
    private String endsite;
    private String note;

    public static List<BookMark> arrayBookMarkFromData(String str) {

        Type listType = new TypeToken<ArrayList<BookMark>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartsite() {
        return startsite;
    }

    public void setStartsite(String startsite) {
        this.startsite = startsite;
    }

    public String getEndsite() {
        return endsite;
    }

    public void setEndsite(String endsite) {
        this.endsite = endsite;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
