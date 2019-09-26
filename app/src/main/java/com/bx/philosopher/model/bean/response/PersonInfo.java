package com.bx.philosopher.model.bean.response;

import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: PersonInfo
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/14 16:22
 */
public class PersonInfo implements Serializable {
    private static final long serialVersionUID = 7327718534211575607L;


    /**
     * id : 1
     * nickname : ks
     * phone : 13503835582
     * headimg : http://tvax4.sinaimg.cn/crop.0.0.1002.1002.50/cf582cbcly8g1yij4g567j20ru0ruaat.jpg
     * password : e10adc3949ba59abbe56e057f20f883e
     * vip_status : 2
     * vip_addtime : 1560497630
     * vip_endtime : 1592120030
     * logtime : 1560501369
     * balance : 85.00
     * addtime : 1559378168
     * status : 1
     * del : 1
     * read : 5
     * openid : 1559378168
     */

    private int id;
    private String nickname;
    private String phone;
    private String headimg;
    private String password;
    private int vip_status;
    private String vip_addtime;
    private String vip_endtime;
    private String logtime;
    private String balance;
    private String addtime;
    private int status;
    private int del;
    private int read;
    private String openid;

    public static List<PersonInfo> arrayPersonInfoFromData(String str) {

        Type listType = new TypeToken<ArrayList<PersonInfo>>() {
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
        if (StringUtils.isNotEmpty(headimg) && !headimg.startsWith(Constant.CLIENT_HEAD))
            return Constant.CLIENT_URL + headimg;
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
