package risks.yn.a606a.bean;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/4/18.
 */

public class UserBean {
    private int id;
    private String address = "";
    private String birthday = "";
    private String nation = "";
    private String name = "";
    private String cardNum = "";
    private String gender = "";//性别
    private String department = "";//部门
    private String photo = "";
    private String photo1 = "";
    private String idphoto = "";
    private String finger1 = "";
    private String finger2 = "";
    private String password = "";
    private String createTime = "";
    private Bitmap photoBitmap = null;
    private Bitmap photoBitmapBlack = null;
    private Bitmap idphotoBitmap = null;
    private String feature = "";
    private String feature1 = "";

    public UserBean() {
    }

    public UserBean(int id, String address, String birthday, String nation, String name, String cardNum, String gender, String department, String photo, String photo1, String idphoto, String finger1, String finger2, String password, String createTime, Bitmap photoBitmap, Bitmap photoBitmapBlack, Bitmap idphotoBitmap, String feature, String feature1) {
        this.id = id;
        this.address = address;
        this.birthday = birthday;
        this.nation = nation;
        this.name = name;
        this.cardNum = cardNum;
        this.gender = gender;
        this.department = department;
        this.photo = photo;
        this.photo1 = photo1;
        this.idphoto = idphoto;
        this.finger1 = finger1;
        this.finger2 = finger2;
        this.password = password;
        this.createTime = createTime;
        this.photoBitmap = photoBitmap;
        this.photoBitmapBlack = photoBitmapBlack;
        this.idphotoBitmap = idphotoBitmap;
        this.feature = feature;
        this.feature1 = feature1;
    }

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

    public String getPhoto1() {
        return photo1;
    }

    public void setPhoto1(String photo1) {
        this.photo1 = photo1;
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

    public String getFinger2() {
        return finger2;
    }

    public void setFinger2(String finger2) {
        this.finger2 = finger2;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getFeature1() {
        return feature1;
    }

    public void setFeature1(String feature1) {
        this.feature1 = feature1;
    }
}
