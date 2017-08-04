package risks.yn.a606a.riskbean;

/**
 * Created by Administrator on 2017/7/19.
 * 失信被执行人
 */

public class CourtLoseCredit {
    String caseCode;
    String idCardCode;
    String idCardName;
    String sex;
    String age;
    String courtName;
    String areaName;
    String gistId;
    String regDate;
    String gistUnit;
    String duty;
    String performance;
    String disruptTypeName;
    String publishDate;

    public CourtLoseCredit() {
    }

    public CourtLoseCredit(String caseCode, String idCardCode, String idCardName, String sex, String age, String courtName, String areaName, String gistId, String regDate, String gistUnit, String duty, String performance, String disruptTypeName, String publishDate) {
        this.caseCode = caseCode;
        this.idCardCode = idCardCode;
        this.idCardName = idCardName;
        this.sex = sex;
        this.age = age;
        this.courtName = courtName;
        this.areaName = areaName;
        this.gistId = gistId;
        this.regDate = regDate;
        this.gistUnit = gistUnit;
        this.duty = duty;
        this.performance = performance;
        this.disruptTypeName = disruptTypeName;
        this.publishDate = publishDate;
    }

    public String getCaseCode() {
        return caseCode;
    }

    public void setCaseCode(String caseCode) {
        this.caseCode = caseCode;
    }

    public String getIdCardCode() {
        return idCardCode;
    }

    public void setIdCardCode(String idCardCode) {
        this.idCardCode = idCardCode;
    }

    public String getIdCardName() {
        return idCardName;
    }

    public void setIdCardName(String idCardName) {
        this.idCardName = idCardName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getGistId() {
        return gistId;
    }

    public void setGistId(String gistId) {
        this.gistId = gistId;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getGistUnit() {
        return gistUnit;
    }

    public void setGistUnit(String gistUnit) {
        this.gistUnit = gistUnit;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public String getDisruptTypeName() {
        return disruptTypeName;
    }

    public void setDisruptTypeName(String disruptTypeName) {
        this.disruptTypeName = disruptTypeName;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
}
