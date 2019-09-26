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
 * @ClassName: SubscribeBean
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/6/13 17:40
 */
public class SubscribeBean implements Serializable {
    private static final long serialVersionUID = -8429478177263069720L;


    /**
     * nickname : ks
     * headimg : null
     * vip_status : 2 已订阅 1 未订阅
     * vip_addtime : 1560416665
     * vip_endtime : 1592039065
     * logtime : null
     */

    private String nickname;
    private String headimg;
    private int vip_status;
    private String vip_addtime;
    private String vip_endtime;
    private Object logtime;

    public static List<SubscribeBean> arraySubscribeBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<SubscribeBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimg() {
        if (StringUtils.isNotEmpty(headimg) && !headimg.startsWith(Constant.CLIENT_HEAD))
            return Constant.CLIENT_URL + headimg;
        return headimg;
    }

    public void setHeadimg(String headimg) {
        this.headimg = headimg;
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

    public Object getLogtime() {
        return logtime;
    }

    public void setLogtime(Object logtime) {
        this.logtime = logtime;
    }
}
