package risks.yn.a606a.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import risks.yn.a606a.Utils.CertImgDisposeUtils;
import risks.yn.a606a.bean.RecordBean;
import risks.yn.a606a.bean.RiskBean;
import risks.yn.a606a.bean.UserBean;


public class DBManager {
    private DataBase helper;
    private SQLiteDatabase database;

    private Map<Integer, String> mapFeatrues;
    private UserBean userBean;
    private RiskBean riskBean;
    private RecordBean recordBean;
    public static final String TAG = "DBManager";
    ;
    private ArrayList<ArrayList<String>> bill2List = new ArrayList<ArrayList<String>>();
    static private DBManager self = null;

    public static DBManager getInstance(Context context) {
        if (self == null) {
            self = new DBManager(context);
        }
        return self;
    }

    public DBManager(Context context) {
        helper = new DataBase(context);
        database = helper.getWritableDatabase();
    }

    public void Close() {
        Log.e(TAG, "Close()");
        if (database != null)
            database.close();
        if (helper != null)
            helper.close();
        self = null;
    }

    //添加User
    public void addUser(UserBean user) {

        database.execSQL("insert into user(name,gender,department,cardNum,address ,birthday ,nation ,photo,photo1,idphoto,finger1,finger2,password,createTime,feature,feature1) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{
                user.getName(), user.getGender(), user.getDepartment(), user.getCardNum(), user.getAddress(), user.getBirthday(), user.getNation(),
                CertImgDisposeUtils.bitmaptoString(user.getPhotoBitmap()), CertImgDisposeUtils.bitmaptoString(user.getPhotoBitmapBlack()), CertImgDisposeUtils.bitmaptoString(user.getIdphotoBitmap()), user.getFinger1(),
                user.getFinger2(), user.getPassword(), user.getCreateTime(), user.getFeature(), user.getFeature1()});


    }

    //添加User
    public void addRisk(RiskBean user) {

        database.execSQL("insert into risk(name,gender,department,cardNum,address ,birthday ,nation ,idphoto,photo,finger1,message,createTime,phonenum) values(?,?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{

                user.getName(), user.getGender(), user.getDepartment(), user.getCardNum(), user.getAddress(), user.getBirthday(), user.getNation(),
                CertImgDisposeUtils.bitmaptoString(user.getIdphotoBitmap()), CertImgDisposeUtils.bitmaptoString(user.getPhotoBitmapBlack()), user.getFinger1(),
                user.getMessage(), user.getCreateTime(), user.getPhonenum()});
    }


    //查询User
    public List<UserBean> selectUser() {
        List<UserBean> list;
        String sql = "select _id,name,gender,department,cardNum,address ,birthday ,nation ,photo,photo1,idphoto,password,createTime from user";
        Cursor cursor = database.rawQuery(sql, null);
        list = new ArrayList<UserBean>();

        while (cursor.moveToNext()) {

            userBean = new UserBean();
            userBean.setId(cursor.getInt(0));
            userBean.setName(cursor.getString(1));
            userBean.setGender(cursor.getString(2));
            userBean.setDepartment(cursor.getString(3));
            userBean.setCardNum(cursor.getString(4));
            userBean.setAddress(cursor.getString(5));
            userBean.setBirthday(cursor.getString(6));
            userBean.setNation(cursor.getString(7));
            userBean.setPhoto(cursor.getString(8));
            userBean.setPhoto1(cursor.getString(9));
            userBean.setIdphoto(cursor.getString(10));
            userBean.setPassword(cursor.getString(11));
            userBean.setCreateTime(cursor.getString(12));
            list.add(userBean);
        }
        return list;
    }

    //查询User
    public List<RiskBean> selectRisk() {
        List<RiskBean> list;
        String sql = "select _id,name,gender,department,cardNum,address ,birthday ,nation ,idphoto,photo,message,createTime,phonenum from risk";
        Cursor cursor = database.rawQuery(sql, null);
        list = new ArrayList<RiskBean>();

        while (cursor.moveToNext()) {

            riskBean = new RiskBean();
            riskBean.setId(cursor.getInt(0));
            riskBean.setName(cursor.getString(1));
            riskBean.setGender(cursor.getString(2));
            riskBean.setDepartment(cursor.getString(3));
            riskBean.setCardNum(cursor.getString(4));
            riskBean.setAddress(cursor.getString(5));
            riskBean.setBirthday(cursor.getString(6));
            riskBean.setNation(cursor.getString(7));

            riskBean.setIdphoto(cursor.getString(8));
            riskBean.setPhoto(cursor.getString(9));
            riskBean.setMessage(cursor.getString(10));
            riskBean.setCreateTime(cursor.getString(11));
            riskBean.setPhonenum(cursor.getString(12));
            list.add(riskBean);
        }
        return list;
    }

    //查询User
    public UserBean selectUserById(int uid) {
        String sql = "select name,department,cardNum,photo,photo1,password,createTime from user where _id = '" + uid + "'";
        Cursor cursor = database.rawQuery(sql, null);
        userBean = new UserBean();
        while (cursor.moveToNext()) {
            userBean.setName(cursor.getString(0));
            userBean.setDepartment(cursor.getString(1));
            userBean.setCardNum(cursor.getString(2));
            userBean.setPhoto(cursor.getString(3));
            userBean.setPhotoBitmap(CertImgDisposeUtils.convertStringToIcon(cursor.getString(3)));
            userBean.setPhotoBitmapBlack(CertImgDisposeUtils.convertStringToIcon(cursor.getString(4)));
            userBean.setPassword(cursor.getString(5));
            userBean.setCreateTime(cursor.getString(6));
        }
        return userBean;
    }

    //查询用户近红外图片人脸特征值
    public Map<Integer, String> selectUserBlackFeature() {
        String sql = "select _id,feature1 from user";
        Cursor cursor = database.rawQuery(sql, null);
        mapFeatrues = new HashMap<>();

        while (cursor.moveToNext()) {
//            Log.e("Log", "特征值：" + cursor.getString(1));
            mapFeatrues.put(cursor.getInt(0), cursor.getString(1));
        }
        return mapFeatrues;
    }

    //查询用户彩色图片人脸特征值
    public Map<Integer, String> selectUserColorFeature() {
        String sql = "select _id,feature from user";
        Cursor cursor = database.rawQuery(sql, null);
        mapFeatrues = new HashMap<>();

        while (cursor.moveToNext()) {
            mapFeatrues.put(cursor.getInt(0), cursor.getString(1));
        }
        return mapFeatrues;
    }

    //查询用户指纹特征值
    public Map<Integer, String> selectUserFingerFeature() {
        String sql = "select _id,finger1 from user where finger1 <> ''";
        Cursor cursor = database.rawQuery(sql, null);
        mapFeatrues = new HashMap<>();
        while (cursor.moveToNext()) {
            Log.e(TAG, "finger1:" + cursor.getString(1));
            mapFeatrues.put(cursor.getInt(0), cursor.getString(1));
        }
        return mapFeatrues;
    }


    /**
     * 查询考勤记录
     *
     * @return
     */
    public List<RecordBean> selectRecord() {
        List<RecordBean> list;
        String sql = "select _id,name,cardNum,time from record";
        Cursor cursor = database.rawQuery(sql, null);
        list = new ArrayList<RecordBean>();

        while (cursor.moveToNext()) {

            recordBean = new RecordBean();
            recordBean.setId(cursor.getInt(0));
            recordBean.setName(cursor.getString(1));
            recordBean.setCardNum(cursor.getString(2));
            recordBean.setTime(cursor.getString(3));
            list.add(recordBean);
        }
        return list;
    }

    public ArrayList<ArrayList<String>> getBillData() {
        bill2List = new ArrayList<>();
        String sql = "select _id,name,cardNum,time from record";
        Cursor mCrusor = database.rawQuery(sql, null);
        while (mCrusor.moveToNext()) {
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(mCrusor.getInt(0) + "");
            beanList.add(mCrusor.getString(1));
            beanList.add(mCrusor.getString(2));
            beanList.add(mCrusor.getString(3));
            bill2List.add(beanList);
        }
        mCrusor.close();
        return bill2List;
    }

    public ArrayList<ArrayList<String>> getUserData() {
        bill2List = new ArrayList<>();
        String sql = "select * from user";
        Cursor mCrusor = database.rawQuery(sql, null);
        while (mCrusor.moveToNext()) {
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(mCrusor.getInt(0) + "");
            beanList.add(mCrusor.getString(1));
            beanList.add(mCrusor.getString(2));
            beanList.add(mCrusor.getString(3));
            beanList.add(mCrusor.getString(4));
            beanList.add(mCrusor.getString(5));
            beanList.add(mCrusor.getString(6));
            beanList.add(mCrusor.getString(7));
            beanList.add(mCrusor.getString(13));
            beanList.add(mCrusor.getString(14));
            bill2List.add(beanList);
        }
        mCrusor.close();
        return bill2List;
    }


    //删除用户
    public void del(int id) {
        database.delete("user", "_id='" + id + "'", null);
    }

    //添加User
    public void addRecond(UserBean user) {

        database.execSQL("insert into record(name,cardNum,time) values(?,?,?)", new Object[]{

                user.getName(), user.getCardNum(), user.getCreateTime()});


    }

    //更新指定列名
    public void updateColumnByIdnum(String column, String value, String idnum) {
        try {
            String sql = "update person set '" + column + "'='" + value + "' where cardNum = '" + idnum + "'";
            Log.d("SQL", sql);
            database.execSQL(sql);
        } catch (Exception e) {
            Log.d(TAG, "更新列失败");
            e.printStackTrace();
        }
    }

    //更新指定列名
    public void updateColumnById(String column, String value, int id) {
        try {
            String sql = "update person set '" + column + "'='" + value + "' where _id = '" + id + "'";
            Log.d("SQL", sql);
            database.execSQL(sql);
        } catch (Exception e) {
            Log.d(TAG, "更新列失败");
            e.printStackTrace();
        }
    }

}
