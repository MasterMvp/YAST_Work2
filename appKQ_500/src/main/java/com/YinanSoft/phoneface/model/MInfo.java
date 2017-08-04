package com.YinanSoft.phoneface.model;
/**
 * 
 * 注册： userName \ userCode(idCard) \phoneNum \  regPic(b64PersonImage)
 * 比对 :verifyPic 
 * @author lenovo
 *
 */
public class MInfo {
	PInfo pInfo; // 手机信息
	String phoneNum;//手机号
	String userName;//用户名
	String personID;//用户id
	String userCode;
	String b64PersonImage; // 用户照片
	String regPic;//注册时抓取的人脸特征值
	String verifyPic;//比对时抓取的人脸特征值
	
	@Override
	public String toString() {
		return "MInfo [userName=" + userName + ", userCode=" + userCode + ", phoneNum=" + phoneNum+", regPic="
				+ regPic + ", verifyPic=" + verifyPic + "]";
	}
	
	public String getVerifyPic() {
		return verifyPic;
	}
	public void setVerifyPic(String verifyPic) {
		this.verifyPic = verifyPic;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getRegPic() {
		return regPic;
	}
	public void setRegPic(String regPic) {
		this.regPic = regPic;
	}

	String idCard; // 身份证号
	String logStartTime; // 开始时间
	String logEndTime; // 结束时间
	String logAltTime; // 总耗时/ 识别耗时
	Integer logSampleNum; // 样本数量
	String logAveTime; // 平均耗时
	String phoneInspect; // 比对结果 0.成功 1.失败
	String phoneResulCorr; // 比对结果正确性 1.正确 2错误
	String strWebServiceIp; // DBScanIP
	String webServiceIpScan; // DBScan协议
	String imagePath; // 照片保存路径
	String operType; // 操作类型 1.活体检测+人脸识别（比对联网核查）2.活体检测+人脸识别（比对近照） 3.检活测试
	String terminalType; // 终端类型 2,IPAD 3.Android设备
	String score; // 比对分值
	String detail; // 比对阈值
	String testInspect; // 检活结果0 活体 1非活体
	String testResulCorr; // 检活结果正确性 1.正确 2错误
	public MInfo(){
 	}
	public MInfo(String mac){
 		this.userCode=mac;
 	}
	public MInfo(String userCode, String b64PersonImage) {
		this.userCode = userCode;
		this.phoneNum = b64PersonImage;
	}
	public MInfo(String personName,String mac, String phoneNum){
		this.userName=personName;
		this.userCode=mac;
		this.phoneNum=phoneNum;
	}
	public MInfo(String idCard, String b64PersonImage, String operType,
			String terminalType) {
		this.idCard = idCard;
		this.b64PersonImage = b64PersonImage;
		this.operType = operType;
		this.terminalType = terminalType;
	}
	public MInfo(String personName,String idCard, String b64PersonImage, String operType,
			String terminalType) {
		this.idCard = idCard;
		this.b64PersonImage = b64PersonImage;
		this.operType = operType;
		this.terminalType = terminalType;
	}
	public MInfo(String idCard, String b64PersonImage, String operType,
			String terminalType, String testResulCorr,String phoneResulCorr) {
		this.idCard = idCard;
		this.b64PersonImage = b64PersonImage;
		this.operType = operType;
		this.terminalType = terminalType;
		this.phoneResulCorr=phoneResulCorr;
		this.testResulCorr=testResulCorr;
	}
	public MInfo(String idCard,  String operType,
			String terminalType, String testResulCorr,String phoneResulCorr,String detail,String score) {
		this.idCard = idCard;
		this.operType = operType;
		this.terminalType = terminalType;
		this.phoneResulCorr=phoneResulCorr;
		this.testResulCorr=testResulCorr;
		this.detail=detail;
		this.score=score;
	}
	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getPersonID() {
		return personID;
	}

	public void setPersonID(String personID) {
		this.personID = personID;
	}

	public String getB64PersonImage() {
		return b64PersonImage;
	}

	public void setB64PersonImage(String b64PersonImage) {
		this.b64PersonImage = b64PersonImage;
	}

	public PInfo getpInfo() {
		return pInfo;
	}

	public void setpInfo(PInfo pInfo) {
		this.pInfo = pInfo;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getLogStartTime() {
		return logStartTime;
	}

	public void setLogStartTime(String logStartTime) {
		this.logStartTime = logStartTime;
	}

	public String getLogEndTime() {
		return logEndTime;
	}

	public void setLogEndTime(String logEndTime) {
		this.logEndTime = logEndTime;
	}

	public String getLogAltTime() {
		return logAltTime;
	}

	public void setLogAltTime(String logAltTime) {
		this.logAltTime = logAltTime;
	}

	public Integer getLogSampleNum() {
		return logSampleNum;
	}

	public void setLogSampleNum(Integer logSampleNum) {
		this.logSampleNum = logSampleNum;
	}

	public String getLogAveTime() {
		return logAveTime;
	}

	public void setLogAveTime(String logAveTime) {
		this.logAveTime = logAveTime;
	}

	public String getPhoneInspect() {
		return phoneInspect;
	}

	public void setPhoneInspect(String phoneInspect) {
		this.phoneInspect = phoneInspect;
	}

	public String getPhoneResulCorr() {
		return phoneResulCorr;
	}

	public void setPhoneResulCorr(String phoneResulCorr) {
		this.phoneResulCorr = phoneResulCorr;
	}

	public String getStrWebServiceIp() {
		return strWebServiceIp;
	}

	public void setStrWebServiceIp(String strWebServiceIp) {
		this.strWebServiceIp = strWebServiceIp;
	}

	public String getWebServiceIpScan() {
		return webServiceIpScan;
	}

	public void setWebServiceIpScan(String webServiceIpScan) {
		this.webServiceIpScan = webServiceIpScan;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}



	public String getTestInspect() {
		return testInspect;
	}

	public void setTestInspect(String testInspect) {
		this.testInspect = testInspect;
	}

	public String getTestResulCorr() {
		return testResulCorr;
	}

	public void setTestResulCorr(String testResulCorr) {
		this.testResulCorr = testResulCorr;
	}
	
	
}
