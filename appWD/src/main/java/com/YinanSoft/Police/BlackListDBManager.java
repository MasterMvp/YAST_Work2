package com.YinanSoft.Police;

//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;

//import com.example.lwhc.FingerExt;
//import com.yast.yadrly001.secret;

//import android.content.ContentValues;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import risks.yn.a606a.MyApplication;


//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.Environment;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;

public class BlackListDBManager {

    private static final String tag = null;
    //private DBHelper helper;
    private SQLiteDatabase db;
    static private SQLiteDatabase dbM = null;
    //private DBHelperMobileManhunt MobileManhunthelper;
    static private BlackListDBManager _blDBMng = null;


    public BlackListDBManager() {
        //helper = new DBHelper(context);
        //MobileManhunthelper = new DBHelperMobileManhunt(context);
        // ��ΪgetWritableDatabase�ڲ�������mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // ����Ҫȷ��context�ѳ�ʼ��,���ǿ��԰�ʵ����DBmanager�Ĳ������Activity��onCreate��
        //db = helper.getWritableDatabase();


    }

    static private boolean openDBM() {
        try {
            dbM = SQLiteDatabase.openOrCreateDatabase(
                    MyApplication.DBFilePath,  //FingerExt.info.getUrl()+ "/MobileManhunt.db",
                    null);// "/sdcard/ydzt/MobileManhunt.db", null);.getWritableDatabase("#yinanKeji$wwg");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    static public BlackListDBManager getBlackListDBManagerHolder() {
        if (_blDBMng == null) {
            _blDBMng = new BlackListDBManager();
        }
        return _blDBMng;
    }

    static public String QueryBlackListByID(String id) {
        String sResult = "";
        try {
            if (openDBM()) {
                Cursor cursor = getBlackListDBManagerHolder().queryTheCursorM(id);
                String[] s = cursor.getColumnNames();
                cursor.moveToFirst();
                int counts = cursor.getCount();
                if (counts > 0) {
                    sResult = "案件编号：" + cursor.getString(cursor.getColumnIndex("AJBH")) + "\n" //cursor.getString(cursor.getColumnIndex("AJBH"))+cursor.getString(cursor.getColumnIndex("JYAQ"))
//                            + "姓名：" + secret.decodenew(cursor.getString(cursor.getColumnIndex("XM"))) + "\n"
                            + "身份证号：" + id + "\n"
                            + "简要案情：" + cursor.getString(cursor.getColumnIndex("JYAQ")) + "\n";
                    // Toast.makeText(getActivity(), cursor.getString(cursor.getColumnIndex("AJBH"))+cursor.getString(cursor.getColumnIndex("JYAQ")), 1).show();
                } else {
                    return sResult;
                }
            }
        } catch (Exception ex) {
            sResult = "可能原因1：比对库比对路径没有设置\n" + "可能原因2：比对库生成加密方式错误\n";
        } finally {
            closeDBm();
        }
        return sResult;
    }

    public Cursor queryTheCursorM(String SFZH) {
        try {
            SFZH = secret.encodenew(SFZH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dbM == null) return null;
        Cursor c = dbM.rawQuery(
                "SELECT AJBH,JYAQ FROM escapee_main_info where SFZH=?",
                new String[]{SFZH});
        return c;
    }

    static public void closeDBm() {
        if (dbM != null) dbM.close();
    }
//
//	public Cursor queryTheCursorALL() {
//		Cursor c = db
//				.rawQuery(
//						"SELECT id,name as ����,sex as �Ա�,nation as ����,birthday as ��������,cardno as ���֤��,adress as סַ,gender as ǩ������,valiDate as ��Ч����,readtime as ����ʱ�� FROM person",
//						null);
//		return c;
//	}
//
//	/**
//	 * add persons
//	 *
//	 * @param persons
//	 */
//	public boolean add(person person) {
//		db.beginTransaction(); // ��ʼ����
//		boolean result = true;
//
//		try {
//			Log.i("11111", person.Cardno.toString());
//			SimpleDateFormat sDateFormat = new SimpleDateFormat(
//					"yyyy-MM-dd HH:mm:ss");
//			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
//			String date = sDateFormat.format(curDate);
//
//			// String sql=
//			db.execSQL(
//					"INSERT INTO person (name,sex,nation,birthday,cardno,adress,gender,valiDate,photo,readtime) VALUES(?,?,?,?,?,?,?,?,?,?)", // person.IDNum,
//					new Object[] { person.Name, person.Sex, person.Nation,
//							person.BirthDay, person.Cardno, person.Address,
//							person.Gender, person.ValiDate, person.Photo, date });
//
//			Log.i("insert", person.Cardno + " " + person.Name + " "
//					+ person.Sex + " " + person.Nation + " " + person.BirthDay
//					+ " " + person.Address + " " + person.Photo + " " + " "
//					+ date);
//
//			db.setTransactionSuccessful(); // ��������ɹ����
//			result = true;
//
//		} catch (Exception e) {
//			Log.i("add police", e.toString());
//			result = false;
//			// /updateFinger(person);����
//		} finally {
//			db.endTransaction(); // ��������
//		}
//		return result;
//	}
//
//	public boolean checkLogin(String name, String code) {
//		boolean result = false;
//		Cursor cursor = null;
//		try {
//			cursor = db.rawQuery(
//					"SELECT * FROM manager where name=? and code=?",
//					new String[] { name, code });
//			if (cursor.getCount() <= 0) {
//				result = false;
//			} else {
//				result = true;
//			}
//		} catch (Exception e) {
//		} finally {
//			if (null != cursor && !cursor.isClosed()) {
//				cursor.close();
//			}
//		}
//		return result;
//
//	}
//
//	public void checkcouns() {
//		Cursor cursor = db.rawQuery("select count(*) from person", null);
//		// �α��Ƶ���һ����¼׼����ȡ����
//		cursor.moveToFirst();
//		int counts = (int) cursor.getLong(0);
//		if (counts > 400) {
//			db.execSQL("delete from person where id in(select id from person  limit 1)");
//		}
//	}
//
//	public int getCountPersonNum(String XM, String GMSFHM) {
//
//		if (!XM.equals("")) {
//			Cursor cursor = db.rawQuery(
//					"select count(*) from person where XM=?",
//					new String[] { XM });
//			// �α��Ƶ���һ����¼׼����ȡ����
//			cursor.moveToFirst();
//			// ��ȡ�����е�LONG��������
//			return (int) cursor.getLong(0);
//		} else {
//			Cursor cursor = db.rawQuery(
//					"select count(*) from person where GMSFHM=?",
//					new String[] { GMSFHM });
//			// �α��Ƶ���һ����¼׼����ȡ����
//			cursor.moveToFirst();
//			// ��ȡ�����е�LONG��������
//			return (int) cursor.getLong(0);
//		}
//
//	}
//
//	public void updatePhoto(person person) {
//		ContentValues cv = new ContentValues();
//		cv.put("SDXP", person.Photo);
//		db.update("person", cv, "IDNum = ?", new String[] { person.Cardno });
//	}
//
//	public boolean updateString(String tableName, String conditionColum,
//			String conditionVal, String changeColumnName, String Val) {
//		ContentValues values = new ContentValues();
//		values.put(changeColumnName, Val);
//		try {
//			db.update(tableName, values, conditionColum + "=? ",
//					new String[] { conditionVal });
//		} catch (Exception e) {
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 * delete old person
//	 *
//	 * @param person
//	 */
//	public void deletePerson(String IDNum) {
//		db.delete("person", "id = ?", new String[] { IDNum });
//	}
//
//	/**
//	 * query all persons, return list
//	 *
//	 * @return List<Person>
//	 */
//	public List<personShort> query() {
//		ArrayList<personShort> persons = new ArrayList<personShort>();
//		Cursor c = queryTheCursor();
//		while (c.moveToNext()) {
//			personShort person = new personShort();
//			person.IDNum = c.getString(c.getColumnIndex("cardno"));
//			person.Name = c.getString(c.getColumnIndex("name"));
//			person.Photo = c.getBlob(c.getColumnIndex("photo"));
//			person.ID = c.getString(c.getColumnIndex("id"));
//			persons.add(person);
//		}
//		c.close();
//		return persons;
//	}
//
//	public person queryIdentity(String Identity) {
//		person person = new person();
//		db.beginTransaction();
//		String str = "SELECT * FROM person where id='" + Identity + "'";
//
//		try {
//			Cursor c = db.rawQuery(str, null); // /new String[]{Identity}
//			Log.i("selectone", str);
//
//			c.moveToFirst();
//			person.Cardno = c.getString(c.getColumnIndex("cardno"));
//			person.Name = c.getString(c.getColumnIndex("name"));
//			person.Sex = c.getString(c.getColumnIndex("sex"));
//			person.Nation = c.getString(c.getColumnIndex("nation"));
//			person.BirthDay = c.getString(c.getColumnIndex("birthday"));
//			person.Address = c.getString(c.getColumnIndex("adress"));
//			person.Photo = c.getBlob(c.getColumnIndex("photo"));
//			person.Readtime = c.getString(c.getColumnIndex("readtime"));
//
//			c.close();
//		} catch (Exception e) {
//			Log.e("selectoneexp", e.toString());
//		}
//
//		db.endTransaction();
//		return person;
//	}
//
//	public List<person> queryDay(String dayTime) {
//		// Log.i("11", "11111"+dayTime);
//		ArrayList<person> persons = new ArrayList<person>();
//		Cursor c = queryTheCursorOneDay(dayTime);
//		while (c.moveToNext()) {
//			person person = new person();
//			person.Cardno = c.getString(c.getColumnIndex("IDNum"));
//			person.Name = c.getString(c.getColumnIndex("Name"));
//
//			persons.add(person);
//		}
//		c.close();
//		return persons;
//	}
//
//	/**
//	 * query all persons, return cursor
//	 *
//	 * @return Cursor
//	 */
//	private Cursor queryTheCursor() {
//		Cursor c = db.rawQuery(
//				"SELECT cardno,name,photo,id FROM person ORDER BY id DESC",
//				null);
//		return c;
//	}
//
//	private Cursor queryTheCursorIdentity(String IDNum) {
//		Cursor c = db.rawQuery("SELECT * FROM person where IDNum=?",
//				new String[] { IDNum });
//		return c;
//	}
//
//	private Cursor queryTheCursorOneDay(String CaptureTime) {
//
//		Cursor c = db
//				.rawQuery(
//						"SELECT * FROM person where datetime(CaptureTime)=datetime(?)  ",
//						new String[] { CaptureTime });
//		return c;
//	}
//
//	/**
//	 * close database
//	 */
//	public void closeDB() {
//		db.close();
//	}


}
