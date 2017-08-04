package risks.yn.a606a.bean;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/6/2.
 */

public class RiskBean {

    private int id;
    private String address = "";
    private String birthday = "";
    private String nation = "";
    private String name = "";
    private String cardNum = "";
    private String gender = "";//性别
    private String department = "";//部门
    private String photo = "";
    private String idphoto = "";
    private String finger1 = "";
    private String message = "";
    private String createTime = "";
    private Bitmap photoBitmap = null;
    private Bitmap photoBitmapBlack = null;
    private Bitmap idphotoBitmap = null;
    private String phonenum = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getIdphoto() {
        return idphoto;
    }

    public void setIdphoto(String idphoto) {
        this.idphoto = idphoto;
    }

    public String getFinger1() {
        return finger1;
    }

    public void setFinger1(String finger1) {
        this.finger1 = finger1;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        this.photoBitmap = photoBitmap;
    }

    public Bitmap getPhotoBitmapBlack() {
        return photoBitmapBlack;
    }

    public void setPhotoBitmapBlack(Bitmap photoBitmapBlack) {
        this.photoBitmapBlack = photoBitmapBlack;
    }

    public Bitmap getIdphotoBitmap() {
        return idphotoBitmap;
    }

    public void setIdphotoBitmap(Bitmap idphotoBitmap) {
        this.idphotoBitmap = idphotoBitmap;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    @Override
    public String toString() {
        return "RiskBean{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", birthday='" + birthday + '\'' +
                ", nation='" + nation + '\'' +
                ", name='" + name + '\'' +
                ", cardNum='" + cardNum + '\'' +
                ", gender='" + gender + '\'' +
                ", department='" + department + '\'' +
                ", photo='" + photo + '\'' +
                ", idphoto='" + idphoto + '\'' +
                ", finger1='" + finger1 + '\'' +
                ", message='" + message + '\'' +
                ", createTime='" + createTime + '\'' +
                ", photoBitmap=" + photoBitmap +
                ", photoBitmapBlack=" + photoBitmapBlack +
                ", idphotoBitmap=" + idphotoBitmap +
                ", phonenum='" + phonenum + '\'' +
                '}';
    }
}
