package com.bx.philosopher.model.bean.response;

import java.io.Serializable;

/**
 * @ClassName: CardBean
 * @Description:
 * @Author: leeeeef
 * @CreateDate: 2019/5/30 13:02
 */
public class CardBean extends BaseBean implements Serializable {
    private static final long serialVersionUID = 8210432030976822179L;
    /**
     * id : 2
     * name : logo
     * money : 50.00
     * logo : /uploads/20190524/5ce7b79faaec4.jpeg
     * addtime : 1558684273
     * updatetime : 1558689695
     * num : null
     */

    private int id;
    private String name;
    private String money;
    private String logo;
    private int addtime;
    private int updatetime;
    private Object num;


    public CardBean(int viewType) {
        super(viewType);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getAddtime() {
        return addtime;
    }

    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }

    public int getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(int updatetime) {
        this.updatetime = updatetime;
    }

    public Object getNum() {
        return num;
    }

    public void setNum(Object num) {
        this.num = num;
    }
}
