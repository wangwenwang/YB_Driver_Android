package com.example.administrator.ybdriver.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/25.
 * 图表中工厂送货业务信息类
 */
public class FactoryBusiness implements Serializable {
    private String ship_from_name;//工厂名称
    private int QtyTotal;//发货总数
    private int Ndeliver;//未交付货数
    private int Adeliver;//已交付货数
    private int Arrive;//已到达货数

    public String getShip_from_name() {
        return ship_from_name;
    }

    public void setShip_from_name(String ship_from_name) {
        this.ship_from_name = ship_from_name;
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
        return "FactoryBusiness{" +
                "ship_from_name='" + ship_from_name + '\'' +
                ", QtyTotal=" + QtyTotal +
                ", Ndeliver=" + Ndeliver +
                ", Adeliver=" + Adeliver +
                ", Arrive=" + Arrive +
                '}';
    }
}
