package risks.yn.a606a.bean;

/**
 * Created by Administrator on 2017/5/2.
 */

public class RecordBean {
    private int id;
    private String name;
    private String cardNum;
    private String time;

    public RecordBean() {
    }

    public RecordBean(int id, String name, String cardNum, String time) {
        this.id = id;
        this.name = name;
        this.cardNum = cardNum;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "RecordBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", cardNum='" + cardNum + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
