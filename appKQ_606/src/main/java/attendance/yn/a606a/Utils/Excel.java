package attendance.yn.a606a.Utils;

import android.content.Context;
import android.os.Environment;

import com.YinanSoft.CardReaders.IDCardInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import attendance.yn.a606a.sqlite.DBManager;

/**
 * Created by Administrator on 2016/8/1.
 */
public class Excel {
    private File file;
    private String[] title = {"序号", "姓名", "身份证号码", "时间"};
    private String[] userTitle = {"序号", "姓名", "性别", "部门", "身份证号码", "地址", "出生日期", "民族","比对结果","录入时间"};
    DBManager dbManager;
    Context context;
    SimpleDateFormat formatter;
    Date curDate;
    private ArrayList<ArrayList<IDCardInfo>> bill2List;

    public Excel(Context context) {
        this.context = context;
        formatter = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        dbManager = DBManager.getInstance(context);
        file = new File(getSDPath() + "/YAST");
        makeDir(file);
    }

    public void initData() {
        String time = formatter.format(new Date());
        ExcelUtils.initExcel(file.toString() + "/考勤记录表" + time + ".xls", title);
        ExcelUtils.writeObjListToExcel(dbManager.getBillData(), getSDPath()
                + "/YAST/" + "考勤记录表" + time + ".xls", context);
    }

    public void initUserData() {
        String time = formatter.format(new Date());
        ExcelUtils.initExcel(file.toString() + "/人员记录表" + time + ".xls", userTitle);
        ExcelUtils.writeObjListToExcel(dbManager.getUserData(), getSDPath()
                + "/YAST/" + "人员记录表" + time + ".xls", context);
    }

    public static void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        String dir = sdDir.toString();
        return dir;

    }

}
