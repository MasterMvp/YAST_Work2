package risks.yn.a606a.riskbean;

/**
 * Created by Administrator on 2017/7/19.
 * 涉案信息
 */

public class CourtLoseJudge {
    String courtName;
    String courtType;
    String caseCode;
    String caseType;
    String caseTitle;
    String judgeDate;
    String judgeProcess;
    String party;
    String plaintiff;
    String defendant;
    String appellant;
    String appellee;
    String partyIdCardCode;
    String caseCause;
    String paraclete;
    String caseSummary;
    String judgeResult;

    public CourtLoseJudge() {
    }

    public CourtLoseJudge(String courtName, String courtType, String caseCode, String caseType, String caseTitle, String judgeDate, String judgeProcess, String party, String plaintiff, String defendant, String appellant, String appellee, String partyIdCardCode, String caseCause, String paraclete, String caseSummary, String judgeResult) {
        this.courtName = courtName;
        this.courtType = courtType;
        this.caseCode = caseCode;
        this.caseType = caseType;
        this.caseTitle = caseTitle;
        this.judgeDate = judgeDate;
        this.judgeProcess = judgeProcess;
        this.party = party;
        this.plaintiff = plaintiff;
        this.defendant = defendant;
        this.appellant = appellant;
        this.appellee = appellee;
        this.partyIdCardCode = partyIdCardCode;
        this.caseCause = caseCause;
        this.paraclete = paraclete;
        this.caseSummary = caseSummary;
        this.judgeResult = judgeResult;
    }
    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getCourtType() {
        return courtType;
    }

    public void setCourtType(String courtType) {
        this.courtType = courtType;
    }

    public String getCaseCode() {
        return caseCode;
    }

    public void setCaseCode(String caseCode) {
        this.caseCode = caseCode;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getCaseTitle() {
        return caseTitle;
    }

    public void setCaseTitle(String caseTitle) {
        this.caseTitle = caseTitle;
    }

    public String getJudgeDate() {
        return judgeDate;
    }

    public void setJudgeDate(String judgeDate) {
        this.judgeDate = judgeDate;
    }

    public String getJudgeProcess() {
        return judgeProcess;
    }

    public void setJudgeProcess(String judgeProcess) {
        this.judgeProcess = judgeProcess;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getPlaintiff() {
        return plaintiff;
    }

    public void setPlaintiff(String plaintiff) {
        this.plaintiff = plaintiff;
    }

    public String getDefendant() {
        return defendant;
    }

    public void setDefendant(String defendant) {
        this.defendant = defendant;
    }

    public String getAppellant() {
        return appellant;
    }

    public void setAppellant(String appellant) {
        this.appellant = appellant;
    }

    public String getAppellee() {
        return appellee;
    }

    public void setAppellee(String appellee) {
        this.appellee = appellee;
    }

    public String getPartyIdCardCode() {
        return partyIdCardCode;
    }

    public void setPartyIdCardCode(String partyIdCardCode) {
        this.partyIdCardCode = partyIdCardCode;
    }

    public String getCaseCause() {
        return caseCause;
    }

    public void setCaseCause(String caseCause) {
        this.caseCause = caseCause;
    }

    public String getParaclete() {
        return paraclete;
    }

    public void setParaclete(String paraclete) {
        this.paraclete = paraclete;
    }

    public String getCaseSummary() {
        return caseSummary;
    }

    public void setCaseSummary(String caseSummary) {
        this.caseSummary = caseSummary;
    }

    public String getJudgeResult() {
        return judgeResult;
    }

    public void setJudgeResult(String judgeResult) {
        this.judgeResult = judgeResult;
    }
}
