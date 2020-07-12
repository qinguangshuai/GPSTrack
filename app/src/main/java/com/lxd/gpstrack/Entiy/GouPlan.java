package com.lxd.gpstrack.Entiy;

public class GouPlan {
    private int id;  //id
    private String xuhao;  //序号
    private String gudao;  //股道
    //private String zaigua;  //摘挂
    //private String cheshu;  //车数
    private String guacheshu;//挂车数
    private String shuaicheshu;//甩车数
    private String jishi;  //记事
    private String state; //完成状态 incomplete:未完成   complete:完成  running:正在执行

    public GouPlan() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXuhao() {
        return xuhao;
    }

    public void setXuhao(String xuhao) {
        this.xuhao = xuhao;
    }

    public String getGudao() {
        return gudao;
    }

    public void setGudao(String gudao) {
        this.gudao = gudao;
    }

    public String getGuacheshu() {
        return guacheshu;
    }

    public void setGuacheshu(String guacheshu) {
        this.guacheshu = guacheshu;
    }

    public String getShuaicheshu() {
        return shuaicheshu;
    }

    public void setShuaicheshu(String shuaicheshu) {
        this.shuaicheshu = shuaicheshu;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getJishi() {
        return jishi;
    }

    public void setJishi(String jishi) {
        this.jishi = jishi;
    }
}
