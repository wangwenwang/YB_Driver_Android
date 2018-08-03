package com.example.administrator.ybdriver.bean;

public class OrderDetails implements java.io.Serializable {
	private String PRODUCT_NO;//yb 物料编码
	private String PRODUCT_NAME;//yb 物料名称
	private double ORDER_QTY;
	private String ORDER_UOM;
	private String ORDER_WEIGHT;
	private String ORDER_VOLUME;
	private String ISSUE_QTY;//yb 发货数量
	private String ISSUE_WEIGHT;
	private String ISSUE_VOLUME;
	private String PRODUCT_PRICE;
	private String ACT_PRICE;
	private String MJ_PRICE;
	private String MJ_REMARK;
	private double ORG_PRICE;
	private String PRODUCT_URL;
	private String PRODUCT_TYPE;
	private String ORD_J_NO;// yb 订单编号
	private String ORD_FROM_NAME;// yb 发货仓库编号
	private String SHIP_FROM_NAME;// yb 发货仓库名称
	private String ORD_REQUEST_ISSUE;//yb 计划发运日期
	private String ORD_REMARK_CLIENT;//yb 收货客户
	private String ORD_TO_REGION;//yb 销售区域
	private String ORD_QTY;//yb 订单数量
	public String getPRODUCT_TYPE() {
		return PRODUCT_TYPE;
	}

	public void setPRODUCT_TYPE(String PRODUCT_TYPE) {
		this.PRODUCT_TYPE = PRODUCT_TYPE;
	}

	public String getACT_PRICE() {
		return ACT_PRICE;
	}

	public void setACT_PRICE(String ACT_PRICE) {
		this.ACT_PRICE = ACT_PRICE;
	}

	public String getMJ_PRICE() {
		return MJ_PRICE;
	}

	public void setMJ_PRICE(String MJ_PRICE) {
		this.MJ_PRICE = MJ_PRICE;
	}

	public String getMJ_REMARK() {
		return MJ_REMARK;
	}

	public void setMJ_REMARK(String MJ_REMARK) {
		this.MJ_REMARK = MJ_REMARK;
	}

	public String getPRODUCT_URL() {
		return PRODUCT_URL;
	}

	public void setPRODUCT_URL(String PRODUCT_URL) {
		this.PRODUCT_URL = PRODUCT_URL;
	}

	public String getPRODUCT_NO() {
		return PRODUCT_NO;
	}

	public void setPRODUCT_NO(String PRODUCT_NO) {
		this.PRODUCT_NO = PRODUCT_NO;
	}

	public String getPRODUCT_NAME() {
		return PRODUCT_NAME;
	}

	public void setPRODUCT_NAME(String PRODUCT_NAME) {
		this.PRODUCT_NAME = PRODUCT_NAME;
	}

	public double getORDER_QTY() {
		return ORDER_QTY;
	}

	public void setORDER_QTY(double ORDER_QTY) {
		this.ORDER_QTY = ORDER_QTY;
	}

	public double getORG_PRICE() {
		return ORG_PRICE;
	}

	public void setORG_PRICE(double ORG_PRICE) {
		this.ORG_PRICE = ORG_PRICE;
	}

	public String getORDER_UOM() {
		return ORDER_UOM;
	}

	public void setORDER_UOM(String ORDER_UOM) {
		this.ORDER_UOM = ORDER_UOM;
	}

	public String getORDER_WEIGHT() {
		return ORDER_WEIGHT;
	}

	public void setORDER_WEIGHT(String ORDER_WEIGHT) {
		this.ORDER_WEIGHT = ORDER_WEIGHT;
	}

	public String getORDER_VOLUME() {
		return ORDER_VOLUME;
	}

	public void setORDER_VOLUME(String ORDER_VOLUME) {
		this.ORDER_VOLUME = ORDER_VOLUME;
	}

	public String getISSUE_QTY() {
		return ISSUE_QTY;
	}

	public void setISSUE_QTY(String ISSUE_QTY) {
		this.ISSUE_QTY = ISSUE_QTY;
	}

	public String getISSUE_WEIGHT() {
		return ISSUE_WEIGHT;
	}

	public void setISSUE_WEIGHT(String ISSUE_WEIGHT) {
		this.ISSUE_WEIGHT = ISSUE_WEIGHT;
	}

	public String getISSUE_VOLUME() {
		return ISSUE_VOLUME;
	}

	public void setISSUE_VOLUME(String ISSUE_VOLUME) {
		this.ISSUE_VOLUME = ISSUE_VOLUME;
	}

	public String getPRODUCT_PRICE() {
		return PRODUCT_PRICE;
	}

	public void setPRODUCT_PRICE(String PRODUCT_PRICE) {
		this.PRODUCT_PRICE = PRODUCT_PRICE;
	}

	public String getORD_J_NO() {
		return ORD_J_NO;
	}

	public void setORD_J_NO(String ORD_J_NO) {
		this.ORD_J_NO = ORD_J_NO;
	}

	public String getORD_FROM_NAME() {
		return ORD_FROM_NAME;
	}

	public void setORD_FROM_NAME(String ORD_FROM_NAME) {
		this.ORD_FROM_NAME = ORD_FROM_NAME;
	}

	public String getORD_REQUEST_ISSUE() {
		return ORD_REQUEST_ISSUE;
	}

	public void setORD_REQUEST_ISSUE(String ORD_REQUEST_ISSUE) {
		this.ORD_REQUEST_ISSUE = ORD_REQUEST_ISSUE;
	}

	public String getORD_REMARK_CLIENT() {
		return ORD_REMARK_CLIENT;
	}

	public void setORD_REMARK_CLIENT(String ORD_REMARK_CLIENT) {
		this.ORD_REMARK_CLIENT = ORD_REMARK_CLIENT;
	}

	public String getORD_TO_REGION() {
		return ORD_TO_REGION;
	}

	public void setORD_TO_REGION(String ORD_TO_REGION) {
		this.ORD_TO_REGION = ORD_TO_REGION;
	}

	public String getORD_QTY() {
		return ORD_QTY;
	}

	public void setORD_QTY(String ORD_QTY) {
		this.ORD_QTY = ORD_QTY;
	}

	public String getSHIP_FROM_NAME() {
		return SHIP_FROM_NAME;
	}

	public void setSHIP_FROM_NAME(String SHIP_FROM_NAME) {
		this.SHIP_FROM_NAME = SHIP_FROM_NAME;
	}
}
