package com.bx.philosopher.model.bean.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: UserInfo
 * @Description: 登录返回信息
 * @Author: leeeeef
 * @CreateDate: 2019/6/11 17:13
 */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = -6782209015718905386L;

    /**
     * id : 7
     * nickname : leeeefe
     * phone : null
     * headimg : http://tvax4.sinaimg.cn/crop.0.0.1002.1002.50/cf582cbcly8g1yij4g567j20ru0ruaat.jpg
     * password : null
     * vip_status : 1
     * vip_addtime : null
     * vip_endtime : null
     * logtime : 1560244379
     * money : 0.00
     * addtime : 1560244379
     * status : 1
     * del : 1
     * read : 0
     * openid : 3478662332
     */

    private int id;
    private String nickname;
    private String phone;
    private String headimg;
    private Object password;
    private int vip_status;
    private Object vip_addtime;
    private Object vip_endtime;
    private String logtime;
    private String balance;
    private String addtime;
    private int status;
    private int del;
    private int read;
    private String openid;

    public static List<UserInfo> arrayUserInfoFromData(String str) {

        Type listType = new TypeToken<ArrayList<UserInfo>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHeadimg() {
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
    }

    public Object getPassword() {
        return password;
    }

    public void setPassword(Object password) {
        this.password = password;
    }

    public int getVip_status() {
        return vip_status;
    }

    public void setVip_status(int vip_status) {
        this.vip_status = vip_status;
    }

    public Object getVip_addtime() {
        return vip_addtime;
    }

    public void setVip_addtime(Object vip_addtime) {
        this.vip_addtime = vip_addtime;
    }

    public Object getVip_endtime() {
        return vip_endtime;
    }

    public void setVip_endtime(Object vip_endtime) {
        this.vip_endtime = vip_endtime;
    }

    public String getLogtime() {
        return logtime;
    }

    public void setLogtime(String logtime) {
        this.logtime = logtime;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDel() {
        return del;
    }

    public void setDel(int del) {
        this.del = del;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
