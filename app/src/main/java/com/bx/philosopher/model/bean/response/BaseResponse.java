package com.bx.philosopher.model.bean.response;

import java.io.Serializable;

/**
 * @Description: 请求实体的基类
 * @Author: leeeef
 * @CreateDate: 2019/5/20 9:24
 */
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -1320627772679582499L;
    public T data;
    private int code;//200 返回成功
    private String msg;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
