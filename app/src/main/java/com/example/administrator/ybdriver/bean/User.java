package com.example.administrator.ybdriver.bean;

public class User implements java.io.Serializable {
	private String USER_NAME;
	private String USER_TYPE;//ADMIN：怡宝管理员 ,司机：司机 ， WLS：物流商
	private String USER_CODE;//用户手机号
	private String IDX;

	public String getUSER_NAME() {
		return USER_NAME;
	}

	public void setUSER_NAME(String USER_NAME) {
		this.USER_NAME = USER_NAME;
	}

	public String getUSER_TYPE() {
		return USER_TYPE;
	}

	public void setUSER_TYPE(String USER_TYPE) {
		this.USER_TYPE = USER_TYPE;
	}

	public String getUSER_CODE() {
		return USER_CODE;
	}

	public void setUSER_CODE(String USER_CODE) {
		this.USER_CODE = USER_CODE;
	}

	public String getIDX() {
		return IDX;
	}

	public void setIDX(String IDX) {
		this.IDX = IDX;
	}
}
