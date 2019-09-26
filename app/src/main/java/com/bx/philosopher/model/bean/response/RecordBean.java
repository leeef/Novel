package com.bx.philosopher.model.bean.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: RecordBean
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/14 15:52
 */
public class RecordBean implements Serializable {

    private static final long serialVersionUID = -2280965043049078833L;


    /**
     * id : 3
     * uid : 1
     * nickname : ks
     * content : Buy the book 《First-Strike.》
     * total : 2.00
     * addtime : 1560498204
     */

    private int id;
    private int uid;
    private String nickname;
    private String content;
    private String total;
    private String addtime;

    public static List<RecordBean> arrayRecordBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<RecordBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }
}
