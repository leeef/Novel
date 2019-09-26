package com.bx.philosopher.model.bean.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: GiftCardBean
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/15 10:48
 */
public class GiftCardBean implements Serializable {
    private static final long serialVersionUID = 5363200547759128417L;

    /**
     * id : 6
     * uid : 1
     * nickname : ks
     * money : 10.00
     * p_id : 6
     * p_name : xx
     * type : 1
     * status : 1
     * addtime : 1560566024
     */

    private int id;
    private int uid;
    private String nickname;
    private String money;
    private int p_id;//赠送人的id
    private String p_name;//赠送人的名字
    private int type;
    private int status;// 1未使用 2 已使用
    private int addtime;

    public static List<GiftCardBean> arrayGiftCardBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<GiftCardBean>>() {
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

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAddtime() {
        return addtime;
    }

    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }
}
