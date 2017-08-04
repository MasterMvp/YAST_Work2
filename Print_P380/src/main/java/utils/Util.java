package utils;

import beans.UserBean;
import printpp.printpp_yt.PrintPP_CPCL;

/**
 * Created by Administrator on 2016/11/28.
 */

public class Util {
    public static void access(PrintPP_CPCL iPrinter, UserBean bean){
        iPrinter.pageSetup(576, 576); //设置页面大小
        //编辑文字
        iPrinter.drawText(10, 576, 576, 30, "来访单位", 2, 1, 0, false, false);
        iPrinter.drawText(60, 576, 576, 30, "来 访 人", 2, 1, 0, false, false);
        iPrinter.drawText(110, 576, 576, 30, "电    话", 2, 1, 0, false, false);
        iPrinter.drawText(160, 576, 576, 30, "拜访单位", 2, 1, 0, false, false);
        iPrinter.drawText(210, 576, 576, 30, "拜访部门", 2, 1, 0, false, false);
        iPrinter.drawText(260, 576, 576, 30, "拜 访 人", 2, 1, 0, false, false);
        iPrinter.drawText(310, 576, 576, 30, "来访时间", 2, 1, 0, false, false);
        iPrinter.drawText(360, 576, 576, 30, "签    字", 2, 1, 0, false, false);

        iPrinter.drawText(10, 426, 426, 30, bean.getFromCompany(), 2, 1, 0, false, false);
        iPrinter.drawText(60, 426, 426, 30, bean.getFromPeople(), 2, 1, 0, false, false);
        iPrinter.drawText(110, 426, 426, 30, bean.getTelNum(), 2, 1, 0, false, false);
        iPrinter.drawText(160, 426, 426, 30, bean.getDepartment(), 2, 1, 0, false, false);
        iPrinter.drawText(210, 426, 426, 30, bean.getToCompany(), 2, 1, 0, false, false);
        iPrinter.drawText(260, 426, 426, 30, bean.getToPeople(), 2, 1, 0, false, false);
        iPrinter.drawText(310, 426, 426, 30, bean.getTime(), 2, 1, 0, false, false);
        iPrinter.drawText(360, 426, 426, 30, bean.getSignature(), 2, 1, 0, false, false);
    }
}
