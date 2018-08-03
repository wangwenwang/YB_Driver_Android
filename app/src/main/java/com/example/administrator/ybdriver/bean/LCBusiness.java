package com.example.administrator.ybdriver.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/25.
 * 图表中物流商送货业务信息类
 */
public class LCBusiness implements Serializable {
    private String tms_fllet_name;//物流商名称
    private int QtyTotal;//发货总数
    private int Ndeliver;//未交付货数
    private int Adeliver;//已交付货数
    private int Arrive;//已到达货数

    public String getTms_fllet_name() {
        return tms_fllet_name;
    }

    public void setTms_fllet_name(String tms_fllet_name) {
        this.tms_fllet_name = tms_fllet_name;
    }

    public int getQtyTotal() {
        return QtyTotal;
    }

    public void setQtyTotal(int qtyTotal) {
        QtyTotal = qtyTotal;
    }

    public int getNdeliver() {
        return Ndeliver;
    }

    public void setNdeliver(int ndeliver) {
        Ndeliver = ndeliver;
    }

    public int getAdeliver() {
        return Adeliver;
    }

    public void setAdeliver(int adeliver) {
        Adeliver = adeliver;
    }

    public int getArrive() {
        return Arrive;
    }

    public void setArrive(int arrive) {
        Arrive = arrive;
    }

    @Override
    public String toString() {
        return "LCBusiness{" +
                "tms_fllet_name='" + tms_fllet_name + '\'' +
                ", QtyTotal=" + QtyTotal +
                ", Ndeliver=" + Ndeliver +
                ", Adeliver=" + Adeliver +
                ", Arrive=" + Arrive +
                '}';
    }
}
