package attendance.yn.a606a.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/7/19.
 */
public class DataBase extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "attendance.db";
    private static final int DATABASE_VERSION = 1;
    public static String SQL = null;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //用户表    id,名字,性别,部门,身份证号,彩色照片,近红外照片,指纹特征值1,指纹特征值2,密码,创建时间，彩色照片特征值，近红外照片特征值
        String sql1 = "create table user(_id integer primary key autoincrement,name varchar,gender varchar,department varchar,cardNum varchar,address varchar,birthday varchar,nation varchar,photo varchar,photo1 varchar,idphoto varchar,finger1 varchar,finger2 varchar,password varchar,createTime varchar,feature varchar,feature1 varchar);";
        //配置表   id,公司名称,公司代码,管理员密码,上班时间,下班时间,设备地址
        String sql2 = "create table config(_id integer primary key autoincrement,companyName varchar, companyNum varchar ,adminPass varchar,time1 varchar,time2 varchar,mac varchar);";
        //考勤记录表   id,姓名,身份证号码,10个时间
        String sql3 = "create table record(_id integer primary key autoincrement,name varchar,cardNum varchar,time varchar);";
        //字典表    性别,部门
        String sql4 = "create table dictionary(_id integer primary key autoincrement,gender varchar , department varchar);";

        sqLiteDatabase.execSQL(sql1);
        sqLiteDatabase.execSQL(sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}



