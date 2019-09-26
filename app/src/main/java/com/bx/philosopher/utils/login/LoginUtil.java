package com.bx.philosopher.utils.login;

import com.bx.philosopher.utils.Constant;
import com.bx.philosopher.utils.SharedPreUtils;
import com.bx.philosopher.utils.StringUtils;

/**
 * @Description:
 * @Author: leeeef
 * @CreateDate: 2019/5/17 9:20
 */
public class LoginUtil {


    private static final String LOGIN_FLAG = "hasLogin";
    private static final String USERID = "userID";
    private static final String NICKNAME = "nick_name";
    private static final String HEADIMAGE = "head_image";
    private static final String PHONE = "user_phone";

    /**
     * @Description: 返回是否登陆的状态
     */
    public static boolean isLogin() {
        return SharedPreUtils.getInstance().getBoolean(LOGIN_FLAG, false);
    }

    public static void setLogin(boolean login) {
        SharedPreUtils.getInstance().putBoolean(LOGIN_FLAG, login);
    }

    public static void setUserId(int id) {
        SharedPreUtils.getInstance().putInt(USERID, id);
    }

    public static int getUserId() {
        return SharedPreUtils.getInstance().getInt(USERID, -1);
    }

    public static String getUserIdStr() {
        return SharedPreUtils.getInstance().getInt(USERID, -1) + "";
    }

    public static void setHead(String url) {
        SharedPreUtils.getInstance().putString(HEADIMAGE, url);
    }

    public static String getHead() {
        String head = SharedPreUtils.getInstance().getString(HEADIMAGE);
        if (StringUtils.isNotEmpty(head)) {
            if (!head.startsWith(Constant.CLIENT_HEAD)) {
                return Constant.CLIENT_URL + head;
            } else {
                return head;
            }
        } else return "";
    }

    public static void setName(String name) {
        SharedPreUtils.getInstance().putString(NICKNAME, name);
    }

    public static String getName() {
        return SharedPreUtils.getInstance().getString(NICKNAME);
    }

    public static void setPhone(String phone) {
        SharedPreUtils.getInstance().putString(PHONE, phone);
    }

    public static String getPhone() {
        return SharedPreUtils.getInstance().getString(PHONE);
    }

    public static void setUserAccount(String balance) {
        SharedPreUtils.getInstance().putString("balance", balance);
    }

    public static String getUserAccount() {
        return SharedPreUtils.getInstance().getString("balance");
    }


    //退出
    public static void logout() {
        setLogin(false);
        setUserId(-1);
        setName("");
        setPhone("");
        setHead("");
        setUserAccount("0.00");
    }

}
