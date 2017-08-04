package risks.yn.a606a.activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import risks.yn.a606a.MyApplication;
import risks.yn.a606a.R;
import risks.yn.a606a.Utils.FileToWork;
import risks.yn.a606a.riskbean.MultipointDebtList;
import risks.yn.a606a.riskbean.NetLoanBlackList;

public class RiskMessageAct extends AppCompatActivity {
    private static final String TAG = "RiskMessageAct";
    private StringBuffer sb = new StringBuffer();
    private String idCards = "";
    private String name = "";
    private String phone = "";
    TextView tv;
    Map<String, String> map = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.risk_message);
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        String badResponse = intent.getStringExtra("badResponse");
        idCards = MyApplication.idCardInfo.getCardNum();
        name = MyApplication.idCardInfo.getName();
        phone = intent.getStringExtra("phoneNum");

        map.put("$name$", name);
        map.put("$idCards$", idCards);
        map.put("$phoneNum$", phone);

        sb.append("姓名:    " + name + "\n身份证号码:    " + idCards + "\n手机号     " + phone + "\n\n");
//        String respose = "{'retcode':'10','retmsg':'调用成功','retdetail':{'message':'命中','result':'1','queryRecordMap':{'toTalCusNum':'1','lastPrdGrpName':'中国电信','toTalTransNum':'2','lastTransTime':'2017年06月05日 13时17分16秒'},'multipointDebtList':[{'contractDate':'2010年01月','arrearsAmount':'2035','borrowState':'6','borrowAmount':'5','borrowType':'1','loanPeriod':'5','repayState':'1','companyCode':'P2P393218'},{'contractDate':'2009年10月','arrearsAmount':'2250','borrowState':'6','borrowAmount':'5','borrowType':'1','loanPeriod':'4','repayState':'9','companyCode':'P2P393218'},{'contractDate':'2009年07月','arrearsAmount':'2365','borrowState':'6','borrowAmount':'6','borrowType':'1','loanPeriod':'3','repayState':'7','companyCode':'P2P393218'},{'contractDate':'2009年04月','arrearsAmount':'2476','borrowState':'2','borrowAmount':'7','borrowType':'1','loanPeriod':'2','repayState':'3','companyCode':'P2P393218'},{'contractDate':'2009年01月','arrearsAmount':'2589','borrowState':'1','borrowAmount':'8','borrowType':'1','loanPeriod':'1','repayState':'1','companyCode':'P2P393218'}],'riskLevel':'3','outOrderNo':'zx1496286575191','riskNum':3,'payBlackMap':{'blackBank':'光大银行中关村支行','blackDate':'2017年03月01日','blackArea':'北京','blackChannel':'联盟成员','blackContent':'个人钓鱼网站诈骗'},'netLoanBlackList':[{'borrowPeriod':'21','overdueDate':'2015年05月','borrowAmount':'8-10','overdueAmount':'3-6','borrowDate':'2015年02月','overdueLevel':'M3'},{'borrowPeriod':'23','overdueDate':'2015年09月','borrowAmount':'7-9','overdueAmount':'1-4','borrowDate':'2015年08月','overdueLevel':'M5'},{'borrowPeriod':'26','overdueDate':'2016年06月','borrowAmount':'5-8','overdueAmount':'10-16','borrowDate':'2016年05月','overdueLevel':'M6+'},{'borrowPeriod':'28','overdueDate':'2016年12月','borrowAmount':'2-7','overdueAmount':'3-10','borrowDate':'2016年11月','overdueLevel':'M6'}],'jnlNo':'20170601100823000683'}}";
//        Log.e(TAG, respose);
        tv = (TextView) findViewById(R.id.msg_tv);
        setBadMsg(badResponse);
        setMsg(message, tv);
    }

    private void setBadMsg(String respose) {
        Log.e(TAG, "setBadMsg" + respose);
        try {
            JSONObject jsonObj = new JSONObject(respose);
            String retcode = jsonObj.optString("retcode");

            if (!"10".equals(retcode)) {
                sb.append(jsonObj.optString("retmsg"));
                return;
            }
            if (respose.contains("\"message\":\"查询无结果\"")) {
                sb.append("\n无犯罪记录\n");
                return;
            }
            jsonObj = jsonObj.optJSONObject("retdetail");
            String badnessType = jsonObj.optString("badnessType");
            if (!TextUtils.isEmpty(badnessType)) {
                sb.append("犯罪记录:" + jsonObj.optString("badnessType") + "\n");
//                sb.append("案发时间:" + jsonObj.optString("caseTime") + "\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "setBadMsg解析出错了" + e.toString());
        }
    }

    public void onOutWordClick(View v) {
        printer();
    }

    /**
     * 为了保证模板的可用，最好在现有的模板上复制后修改
     */
    private void printer() {
        try {
            FileToWork.saveFile("yast.doc", this, R.raw.yast);//文件目录res/raw
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //现场检查记录
        String aafileurl = Environment.getExternalStorageDirectory() + "/YAST/yast.doc";
        final String bbfileurl = Environment.getExternalStorageDirectory() + "/YAST/newyast.doc";
        //获取模板文件
        File demoFile = new File(aafileurl);
        //创建生成的文件
        File newFile = new File(bbfileurl);
        if (newFile.exists()) {
            newFile.delete();
        }
        FileToWork.writeDoc(demoFile, newFile, map);

    }


    public void setMsg(String respose, TextView text) {
        MultipointDebtList multiData = null;
        NetLoanBlackList netLoanDate = null;
        ArrayList<MultipointDebtList> multList = new ArrayList<>();

        try {
            JSONObject jsonObj = new JSONObject(respose);
            jsonObj = jsonObj.optJSONObject("retdetail");
            String message = jsonObj.optString("message");
            if (message.equals("未命中")) {
                text.setText(sb.toString() + "\n个人风险信息:未命中");
                return;
            }

            JSONObject queryRecordMap = jsonObj.optJSONObject("queryRecordMap");
            if (queryRecordMap != null) {
                String toTalTransNum = queryRecordMap.getString("toTalTransNum");
                map.put("$toTalTransNum$", toTalTransNum);
                String lastTransTime = queryRecordMap.getString("lastTransTime");
                map.put("$lastTransTime$", lastTransTime);
                String toTalCusNum = queryRecordMap.getString("toTalCusNum");
                map.put("$toTalCusNum$", toTalCusNum);
                String lastPrdGrpName = queryRecordMap.getString("lastPrdGrpName");
                map.put("$lastPrdGrpName$", lastPrdGrpName);
                sb.append("三个月内查询记录\n被查询次数:" + toTalTransNum + "\n最近一次被查询时间:" + lastTransTime + "\n被查询机构数:" + toTalCusNum + "\n最近一次被查询产品:" + lastPrdGrpName + "\n");
            }
            //多头负债详情信息
            JSONArray multipointDebtList = jsonObj.optJSONArray("multipointDebtList");
            if (multipointDebtList != null && multipointDebtList.length() >= 1) {
                sb.append("\n多头负债信息:\n");
                for (int i = 0; i < multipointDebtList.length(); i++) {
                    JSONObject multObj = new JSONObject(multipointDebtList.getString(i));


                    String contractDate = multObj.getString("contractDate");
                    sb.append("借款日期:" + contractDate + "\n");
                    map.put("$contractDate" + i + "$", contractDate);

                    String arrearsAmount = multObj.getString("arrearsAmount");
                    sb.append("欠款金额:" + arrearsAmount + "\n");
                    map.put("$arrearsAmount" + i + "$", arrearsAmount);


                    String borrowState = multObj.getString("borrowState");
                    map.put("$borrowState" + i + "$", borrowState);
                    sb.append("借款状态:" + borrowState + "\n");
                    //---------------------------------
                    String borrowAmount1 = multObj.getString("borrowAmount");
                    sb.append("借款金额:" + getBorrowAmount(borrowAmount1) + "\n");
                    map.put("$borrowAmount" + i + "$", getBorrowAmount(borrowAmount1));
                    //---------------------------------
                    String borrowType = multObj.getString("borrowType");
                    sb.append("借款类型:" + getBorrowType(borrowType) + "\n");
                    map.put("$borrowType" + i + "$", borrowType);

                    String loanPeriod = multObj.getString("loanPeriod");
                    sb.append("借款期数:" + loanPeriod + "期\n");
                    map.put("$loanPeriod" + i + "$", loanPeriod);

                    String repayState = multObj.getString("repayState");

                    sb.append("当前状态:" + getRepayState(repayState) + "\n\n");
                    map.put("$repayState" + i + "$", getRepayState(repayState));
//                    sb.append(":" + multObj.getString("companyCode") + "\n\n");
                }
            }
            //个人账户支付风险信息
            JSONObject paymap = jsonObj.optJSONObject("payBlackMap");
            if (paymap != null) {
                String blackBank = paymap.getString("blackBank");
                String blackDate = paymap.getString("blackDate");
                String blackArea = paymap.getString("blackArea");
                String blackChannel = paymap.getString("blackChannel");
                String blackContent = paymap.getString("blackContent");
                map.put("$blackBank$", blackBank);
                map.put("$blackDate$", blackDate);
                map.put("$blackArea$", blackArea);
                map.put("$blackChannel$", blackChannel);
                map.put("$blackContent$", blackContent);
                sb.append("个人账户支付风险信息\n银行:" + blackBank + "\n  日期:" + blackDate + "\n  地点:" + blackArea + "\n  路径:" + blackChannel + "\n  事件:" + blackContent + "\n\n");
            }
            JSONArray netLoanArr = jsonObj.optJSONArray("netLoanBlackList");
            ArrayList<NetLoanBlackList> netLoanBlackLists = new ArrayList<>();
            //逾期及违约
            if (netLoanArr != null && netLoanArr.length() >= 1) {
                sb.append("逾期及违约信息:\n");
                for (int i = 0; i < netLoanArr.length(); i++) {
                    JSONObject netObj = new JSONObject(netLoanArr.getString(i));

                    String borrowPeriod = netObj.getString("borrowPeriod");
                    sb.append("借款期限:" + borrowPeriod + "期(每30天为1期)\n");
                    map.put("$borrowPeriod" + i + "$", borrowPeriod);

                    String overdueDate = netObj.getString("overdueDate");
                    sb.append("逾期日期:" + overdueDate + "\n");
                    map.put("$overdueDate" + i + "$", overdueDate);

                    String borrowAmount = netObj.getString("borrowAmount");
                    sb.append("借款金额:" + borrowAmount + "万元\n");
                    map.put("$netborrowAmount" + i + "$", borrowAmount + "万元");

                    //----------------------------------------------------------------------
                    String overdueAmount = netObj.getString("overdueAmount");
                    sb.append("逾期金额:" + overdueAmount + "万元\n");
                    map.put("$overdueAmount" + i + "$", overdueAmount + "万元");

                    //----------------------------------------------------------------------
                    String borrowDate = netObj.getString("borrowDate");
                    sb.append("借款日期:" + netObj.getString("borrowDate") + "\n");
                    map.put("$borrowDate" + i + "$", borrowDate);

                    String overdueLevel = netObj.getString("overdueLevel");
                    sb.append("逾期级别:" + getOverdueLevel(overdueLevel) + "\n\n");
                    map.put("$overdueLevel" + i + "$", getOverdueLevel(overdueLevel));

                }
            }
            JSONArray courtLoseArr = jsonObj.optJSONArray("courtLoseCreditList");
            if (courtLoseArr != null && courtLoseArr.length() >= 1) {
                sb.append("失信被执行人:\n");
                for (int i = 0; i < courtLoseArr.length(); i++) {
                    JSONObject netObj = new JSONObject(courtLoseArr.getString(i));
                    sb.append("案号:" + netObj.getString("caseCode") + "\n");
                    sb.append("身份证号码:" + netObj.getString("idCardCode") + "\n");
                    sb.append("身份证名称:" + netObj.getString("idCardName") + "\n");
                    sb.append("性别:" + netObj.getString("sex") + "\n");
                    sb.append("年龄:" + netObj.getString("age") + "\n");
                    sb.append("执行法院:" + netObj.getString("courtName") + "\n");
                    sb.append("省份:" + netObj.getString("areaName") + "\n");
                    sb.append("执行依据文号:" + netObj.getString("gistId") + "\n");
                    sb.append("立案时间:" + netObj.getString("regDate") + "\n");
                    sb.append("做出执行依据单位:" + netObj.getString("gistUnit") + "\n");
                    sb.append("生效法律文书确定的义务:" + netObj.getString("duty") + "\n");
                    sb.append("被执行人的履行情况:" + netObj.getString("performance") + "\n");
                    sb.append("失信被执行人行为具体情况:" + netObj.getString("disruptTypeName") + "\n");
                    sb.append("发布时间:" + netObj.getString("publishDate") + "\n");
                }
            }
            JSONArray courtLoseJudgeArr = jsonObj.optJSONArray("courtLoseJudgeList");
            if (courtLoseJudgeArr != null && courtLoseJudgeArr.length() >= 1) {
                sb.append("涉案信息:\n");
                for (int i = 0; i < courtLoseJudgeArr.length(); i++) {
                    JSONObject netObj = new JSONObject(courtLoseJudgeArr.getString(i));
                    sb.append("法院名称:" + netObj.getString("courtName") + "\n");
                    sb.append("法院等级:" + netObj.getString("courtType") + "\n");
                    sb.append("案号:" + netObj.getString("caseCode") + "\n");
                    sb.append("案件类别:" + netObj.getString("caseType") + "\n");
                    sb.append("案件标题:" + netObj.getString("caseTitle") + "\n");
                    sb.append("审结时间:" + netObj.getString("judgeDate") + "\n");
                    sb.append("审理程序:" + netObj.getString("judgeProcess") + "\n");
                    sb.append("当事人:" + netObj.getString("party") + "\n");
                    sb.append("原告:" + netObj.getString("plaintiff") + "\n");
                    sb.append("被告:" + netObj.getString("defendant") + "\n");
                    sb.append("上诉人:" + netObj.getString("appellant") + "\n");
                    sb.append("被上诉人:" + netObj.getString("appellee") + "\n");
                    sb.append("当事人身份证号码:" + netObj.getString("partyIdCardCode") + "\n");
                    sb.append("案由:" + netObj.getString("caseCause") + "\n");
                    sb.append("委托辩护人:" + netObj.getString("paraclete") + "\n");
                    sb.append("案例摘要:" + netObj.getString("caseSummary") + "\n");
                    sb.append("判决结果:" + netObj.getString("judgeResult") + "\n");
                }
            }


//            String mes = "已命中 风险:中等\n多头负债信息:" + multList.size() + "条\n" + paymapMsg + "\n逾期及违约:" + netLoanBlackLists.size() + "条";
//            mesg.setText(mes);
            text.setText(sb.toString());
            Log.e(TAG, sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "解析出错了:" + e.toString());
            e.printStackTrace();
        }
    }


    public String getBorrowAmount(String state) {
        String returnMsg = "";
        switch (state) {
            case "0":
                returnMsg = "数据方未反馈";
                break;
            case "1":
                returnMsg = "1-2万元";
                break;
            case "2":
                returnMsg = "2-4万元";
                break;
            case "4":
                returnMsg = "4-6万元";
                break;
            case "5":
                returnMsg = "6-8万元";
                break;
            case "6":
                returnMsg = "8-10万元";
                break;
            case "7":
                returnMsg = "10-12万元";
                break;
        }
        return returnMsg;
    }


    public String getBorrowState(String state) {
        String returnMsg = "";
        switch (state) {
            case "0":
                returnMsg = "数据方未反馈";
                break;
            case "1":
                returnMsg = "拒贷";
                break;
            case "2":
                returnMsg = "批贷已放款";
                break;
            case "4":
                returnMsg = "借款人放弃申请";
                break;
            case "5":
                returnMsg = "审核中";
                break;
            case "6":
                returnMsg = "待放款";
                break;
        }
        return returnMsg;
    }

    public String getBorrowType(String state) {
        String returnMsg = "";
        switch (state) {
            case "0":
                returnMsg = "数据方未反馈";
                break;
            case "1":
                returnMsg = "个人信贷";
                break;
            case "2":
                returnMsg = "个人抵押";
                break;
            case "3":
                returnMsg = "企业信贷";
                break;
            case "4":
                returnMsg = "企业抵押";
                break;
        }
        return returnMsg;
    }

    public String getRepayState(String state) {
        String returnMsg = "";
        switch (state) {
            case "0":
                returnMsg = "数据方未反馈";
                break;
            case "1":
                returnMsg = "正常还款";
                break;
            case "2":
                returnMsg = "逾期一月";
                break;
            case "3":
                returnMsg = "逾期二月";
                break;
            case "4":
                returnMsg = "逾期三月";
                break;
            case "5":
                returnMsg = "逾期四月";
                break;
            case "6":
                returnMsg = "逾期五月";
                break;
            case "7":
                returnMsg = "逾期六月";
                break;
            case "8":
                returnMsg = "逾期六月以上";
                break;
            case "9":
                returnMsg = "已还清";
                break;
        }
        return returnMsg;
    }

    public String getOverdueLevel(String state) {
        String returnMsg = "";
        switch (state) {
            case "M1":
                returnMsg = "逾期一月";
                break;
            case "M2":
                returnMsg = "逾期二月";
                break;
            case "M3":
                returnMsg = "逾期三月";
                break;
            case "M4":
                returnMsg = "逾期四月";
                break;
            case "M5":
                returnMsg = "逾期五月";
                break;
            case "M6":
                returnMsg = "逾期六月";
                break;
            case "M6+":
                returnMsg = "逾期六月以上";
                break;
        }
        return returnMsg;
    }
}
