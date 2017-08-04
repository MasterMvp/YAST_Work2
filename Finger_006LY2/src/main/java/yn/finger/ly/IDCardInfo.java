package yn.finger.ly;


import android.graphics.Bitmap;

import java.io.Serializable;


public class IDCardInfo implements Serializable {
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String CARDNUM = "cardNum";

    private int id;
    private String address;
    private String birthday;
    private String cardNum;
    private String gender;
    private String name;
    private String nation;
    private Bitmap photo;
    private String registInstitution;
    private String validEndDate;
    private String validStartDate;
    private String newAddress;
    private byte[] fingerInfo;
    private String photos;
    private String time;
    private String result;
    private boolean canRemove = true;

    public Bitmap getPhotos() {
        return CertImgDisposeUtils.convertStringToIcon(photos);
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    private boolean checked;

    public IDCardInfo() {

    }

    public IDCardInfo(int id, String name, String gender, String nation, String cardNum, String b) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.nation = nation;
        this.cardNum = cardNum;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getFingerInfo() {
        return this.fingerInfo;
    }

    public String getNewAddress() {
        return this.newAddress;
    }

    public String getAddress() {
        return this.address;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public String getCardNum() {
        return this.cardNum;
    }

    public String getGender() {
        return this.gender;
    }

    public String getName() {
        return this.name;
    }

    public String getNation() {
        return this.nation;
    }

    public Bitmap getPhoto() {
        return this.photo;
    }

    public String getRegistInstitution() {
        return this.registInstitution;
    }

    public String getValidEndDate() {
        return this.validEndDate;
    }

    public String getValidStartDate() {
        return this.validStartDate;
    }

    //---------------------------------------------------------------------------------------
    //
    public void setFingerInfo(byte[] paramByte) {
        this.fingerInfo = paramByte;
    }

    public void setNewAddress(String paramString) {
        this.newAddress = paramString;
    }

    public void setAddress(String paramString) {
        this.address = paramString;
    }

    public void setBirthday(String paramString) {
        this.birthday = paramString;
    }

    public void setCardNum(String paramString) {
        this.cardNum = paramString;
    }

    public void setGender(String paramString) {
        this.gender = paramString;
    }

    public void setName(String paramString) {
        this.name = paramString;
    }

    public void setNation(String paramString) {
        this.nation = paramString;
    }

    public void setPhoto(Bitmap paramBitmap) {
        this.photo = paramBitmap;
    }

    public void setRegistInstitution(String paramString) {
        this.registInstitution = paramString;
    }

    public void setValidEndDate(String paramString) {
        this.validEndDate = paramString;
    }

    public void setValidStartDate(String paramString) {
        this.validStartDate = paramString;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isCanRemove() {
        return canRemove;
    }

    public void setCanRemove(boolean canRemove) {
        this.canRemove = canRemove;
    }


}

