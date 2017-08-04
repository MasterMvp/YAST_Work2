package risks.yn.a606a.riskbean;

/**
 * Created by Administrator on 2017/7/19.
 */

public class QueryRecord {
    String toTalTransNum;
    String lastTransTime;
    String toTalCusNum;
    String lastPrdGrpName;

    public QueryRecord() {

    }

    public QueryRecord(String toTalTransNum, String lastTransTime, String toTalCusNum, String lastPrdGrpName) {
        this.toTalTransNum = toTalTransNum;
        this.lastTransTime = lastTransTime;
        this.toTalCusNum = toTalCusNum;
        this.lastPrdGrpName = lastPrdGrpName;
    }

    public String getToTalTransNum() {
        return toTalTransNum;
    }

    public void setToTalTransNum(String toTalTransNum) {
        this.toTalTransNum = toTalTransNum;
    }

    public String getLastTransTime() {
        return lastTransTime;
    }

    public void setLastTransTime(String lastTransTime) {
        this.lastTransTime = lastTransTime;
    }

    public String getToTalCusNum() {
        return toTalCusNum;
    }

    public void setToTalCusNum(String toTalCusNum) {
        this.toTalCusNum = toTalCusNum;
    }

    public String getLastPrdGrpName() {
        return lastPrdGrpName;
    }

    public void setLastPrdGrpName(String lastPrdGrpName) {
        this.lastPrdGrpName = lastPrdGrpName;
    }
}
