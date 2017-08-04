package risks.yn.a606a.riskbean;

/**
 * Created by Administrator on 2017/6/2.
 */

public class NetLoanBlackList {
    private String borrowPeriod;
    private String overdueDate;
    private String borrowAmount;
    private String overdueAmount;
    private String borrowDate;
    private String overdueLevel;

    public String getBorrowPeriod() {
        return borrowPeriod;
    }

    public void setBorrowPeriod(String borrowPeriod) {
        this.borrowPeriod = borrowPeriod;
    }

    public String getOverdueDate() {
        return overdueDate;
    }

    public void setOverdueDate(String overdueDate) {
        this.overdueDate = overdueDate;
    }

    public String getBorrowAmount() {
        return borrowAmount;
    }

    public void setBorrowAmount(String borrowAmount) {
        this.borrowAmount = borrowAmount;
    }

    public String getOverdueAmount() {
        return overdueAmount;
    }

    public void setOverdueAmount(String overdueAmount) {
        this.overdueAmount = overdueAmount;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getOverdueLevel() {
        return overdueLevel;
    }

    public void setOverdueLevel(String overdueLevel) {
        this.overdueLevel = overdueLevel;
    }
}
