package risks.yn.a606a.riskbean;

/**
 * Created by Administrator on 2017/7/19.
 */

public class PayBlack {
    String blackBank;
    String blackDate;
    String blackChannel;
    String blackContent;
    String blackArea;

    public PayBlack() {
    }

    public PayBlack(String blackBank, String blackDate, String blackChannel, String blackContent, String blackArea) {
        this.blackBank = blackBank;
        this.blackDate = blackDate;
        this.blackChannel = blackChannel;
        this.blackContent = blackContent;
        this.blackArea = blackArea;
    }

    public String getBlackBank() {
        return blackBank;
    }

    public void setBlackBank(String blackBank) {
        this.blackBank = blackBank;
    }
}
