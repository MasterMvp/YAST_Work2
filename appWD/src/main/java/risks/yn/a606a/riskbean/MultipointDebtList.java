package risks.yn.a606a.riskbean;

/**
 * Created by Administrator on 2017/6/2.
 */

public class MultipointDebtList {
    private String contractDate;
    private String arrearsAmount;
    private String borrowState;
    private String borrowAmount;
    private String borrowType;
    private String loanPeriod;
    private String repayState;
    private String companyCode;

    public String getContractDate() {
        return contractDate;
    }

    public void setContractDate(String contractDate) {
        this.contractDate = contractDate;
    }

    public String getArrearsAmount() {
        return arrearsAmount;
    }

    public void setArrearsAmount(String arrearsAmount) {
        this.arrearsAmount = arrearsAmount;
    }

    public String getBorrowState() {
        return borrowState;
    }

    public void setBorrowState(String borrowState) {
        this.borrowState = borrowState;
    }

    public String getBorrowAmount() {
        return borrowAmount;
    }

    public void setBorrowAmount(String borrowAmount) {
        this.borrowAmount = borrowAmount;
    }

    public String getBorrowType() {
        return borrowType;
    }

    public void setBorrowType(String borrowType) {
        this.borrowType = borrowType;
    }

    public String getLoanPeriod() {
        return loanPeriod;
    }

    public void setLoanPeriod(String loanPeriod) {
        this.loanPeriod = loanPeriod;
    }

    public String getRepayState() {
        return repayState;
    }

    public void setRepayState(String repayState) {
        this.repayState = repayState;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
