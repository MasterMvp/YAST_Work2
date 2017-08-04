package beans;

/**
 * Created by Administrator on 2016/11/28.
 */

public class UserBean {
    /**
     * 来访单位
     */
    private String fromCompany;
    /**
     * 来访人
     */
    private String fromPeople;
    /**
     * 电话
     */
    private String telNum;
    /**
     * 拜访单位
     */
    private String toCompany;
    /**
     * 拜访部门
     */
    private String department;
    /**
     * 拜访人
     */
    private String toPeople;
    /**
     * 来访时间
     */
    private String time;
    /**
     * 签字
     */
    private String signature;

    public UserBean() {
    }

    public UserBean(String fromCompany, String fromPeople, String telNum, String toCompany, String department, String toPeople, String time, String signature) {
        this.fromCompany = fromCompany;
        this.fromPeople = fromPeople;
        this.telNum = telNum;
        this.toCompany = toCompany;
        this.department = department;
        this.toPeople = toPeople;
        this.time = time;
        this.signature = signature;
    }

    public String getFromCompany() {
        return fromCompany;
    }

    public void setFromCompany(String fromCompany) {
        this.fromCompany = fromCompany;
    }

    public String getFromPeople() {
        return fromPeople;
    }

    public void setFromPeople(String fromPeople) {
        this.fromPeople = fromPeople;
    }

    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }

    public String getToCompany() {
        return toCompany;
    }

    public void setToCompany(String toCompany) {
        this.toCompany = toCompany;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getToPeople() {
        return toPeople;
    }

    public void setToPeople(String toPeople) {
        this.toPeople = toPeople;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "fromCompany='" + fromCompany + '\'' +
                ", fromPeople='" + fromPeople + '\'' +
                ", telNum='" + telNum + '\'' +
                ", toCompany='" + toCompany + '\'' +
                ", department='" + department + '\'' +
                ", toPeople='" + toPeople + '\'' +
                ", time='" + time + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
