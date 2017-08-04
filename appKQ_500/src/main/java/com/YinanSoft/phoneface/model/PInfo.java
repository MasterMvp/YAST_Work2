package com.YinanSoft.phoneface.model;

public class PInfo {
	private String brand; //品牌
	private String imei; //IMEI
	private String manufacturer;//制造商
	private String netSupplier; //网络运营商
	private String packageId; //包ID
	private String platformName; //平台名称
	private String platformVer; //平台版本
	private String softwareVer; //软件版本
	private String type; //类型
	private String version; //版本


	public String getBrand() {
		return this.brand;
	}

	public String getImei() {
		return this.imei;
	}

	public String getManufacturer() {
		return this.manufacturer;
	}

	public String getNetSupplier() {
		return this.netSupplier;
	}

	public String getPackageId() {
		return this.packageId;
	}

	public String getPlatformName() {
		return this.platformName;
	}

	public String getPlatformVer() {
		return this.platformVer;
	}

	public String getSoftwareVer() {
		return this.softwareVer;
	}

	public String getType() {
		return this.type;
	}

	public String getVersion() {
		return this.version;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public void setNetSupplier(String netSupplier) {
		this.netSupplier = netSupplier;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public void setPlatformVer(String platformVer) {
		this.platformVer = platformVer;
	}

	public void setSoftwareVer(String softwareVer) {
		this.softwareVer = softwareVer;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
