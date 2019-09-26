package com.bx.philosopher.model.bean.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: LoginBean
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/8/22 16:29
 */
public class LoginBean implements Serializable {

    private static final long serialVersionUID = -6501749132562324575L;

    /**
     * id : 26
     * nickname : null
     * phone : 18348087531
     * headimg : null
     * password : 43aeabe1dd48eeddd42220c9dc2dd450
     * vip_status : 1
     * vip_addtime : null
     * vip_endtime : null
     * logtime : 1566461106
     * balance : 0.00
     * addtime : 1566461106
     * status : 1
     * del : 1
     * read : 0
     * openid : null
     */

    private int id;
    private String nickname;
    private String phone;
    private String headimg;
    private String password;
    private int vip_status;//是否是会员 1不是 2 是
    private String vip_addtime;
    private String vip_endtime;
    private String logtime;
    private String balance;
    private String addtime;
    private int status;
    private int del;
    private int read;
    private String openid;

    public static LoginBean objectFromData(String str) {

        return new Gson().fromJson(str, LoginBean.class);
    }

    public static List<LoginBean> arrayLoginBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<LoginBean>>() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getVip_status() {
        return vip_status;
    }

    public void setVip_status(int vip_status) {
        this.vip_status = vip_status;
    }

    public String getVip_addtime() {
        return vip_addtime;
    }

    public void setVip_addtime(String vip_addtime) {
        this.vip_addtime = vip_addtime;
    }

    public String getVip_endtime() {
        return vip_endtime;
    }

    public void setVip_endtime(String vip_endtime) {
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
