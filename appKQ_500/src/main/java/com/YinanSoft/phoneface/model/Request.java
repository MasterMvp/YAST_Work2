package com.YinanSoft.phoneface.model;

/**
 * 请求数据类
 * 
 * @author zhme
 * 
 */
public class Request {
	private String imageBase; // 用户照片
	private String idCard; // 身份证号
	private String terminalType; // 终端类型 2.苹果设备 ,3.Android设备

	public Request(String idCard, String terminalType) {
		this.idCard = idCard;
		this.terminalType = terminalType;
	}

	public String getImageBase() {
		return imageBase;
	}

	public void setImageBase(String imageBase) {
		this.imageBase = imageBase;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}
}
