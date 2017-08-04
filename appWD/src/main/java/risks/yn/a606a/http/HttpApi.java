//package risks.yn.a606a.http;
//
//import android.content.Context;
//import android.os.Build;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Message;
//import android.util.Log;
//import android.util.Pair;
//
//import com.example.yast.yastcardsysyem.bean.UploadInfo;
//
//import org.ksoap2.SoapEnvelope;
//import org.ksoap2.serialization.SoapObject;
//import org.ksoap2.serialization.SoapPrimitive;
//import org.ksoap2.serialization.SoapSerializationEnvelope;
//import org.ksoap2.transport.HttpTransportSE;
//import org.xmlpull.v1.XmlPullParserException;
//
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//
//public class HttpApi {
//    private static final int SUCCESS = 1;
//    private static final int FAIL = 2;
//    private static final int ACTION_UPLOAD = 3;
//    private final Context context;
//
//    private OnUploadListener listener;
//    private Handler master;
//    private Handler slave;
//
//    private HandlerThread workThread;
//    //内网地址 http://10.101.1.217/CFHCweb/Service1.asmx
//    //外网地址 http://42.187.120.81:9000/rchcBDwebservice/Service1.asmx
//    public static String IN_UPLOAD_URL = "http://10.101.1.217/CFHCweb/Service1.asmx";
//    public static String UPLOAD_URL = "http://42.187.120.81:9000/rchcBDwebservice/Service1.asmx";//http://192.168.1.210/WebServiceLG/Service1.asmx";
//    private String nameSpace = "http://tempuri.org/";
//    private String method = "checkpeoplecz";
//
//    private final static String ERROR_NET = "网络连接失败";
//    private final static String ERROR_XML_PARSE = "数据解析异常";
//    private final static String ERROR_SERVER = "服务器异常";
//    private final int TIMEOUT = 5000;
//
//    public HttpApi(Context context) {
//        this.context = context;
//        initHandler();
//    }
//
//    private void initHandler() {
//        initMaster();
//        initWorker();
//    }
//
//    private void initWorker() {
//        workThread = new HandlerThread("WORK");
//        workThread.start();
//
//        slave = new Handler(workThread.getLooper(), new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message msg) {
//                switch (msg.what) {
//                    case ACTION_UPLOAD:
//                        upload((UploadInfo) msg.obj);
//                        break;
//                }
//                return false;
//            }
//        });
//    }
//
//    private void initMaster() {
//        master = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//
//                    case SUCCESS:
//                        Pair<UploadInfo, String> ret = (Pair<UploadInfo, String>) msg.obj;
//                        listener.success(ret.first, ret.second);
//                        break;
//                    case FAIL:
//                        Pair<UploadInfo, String> rett = (Pair<UploadInfo, String>) msg.obj;
//                        listener.fail(rett.first, rett.second);
//                        break;
//                }
//                super.handleMessage(msg);
//            }
//        };
//    }
//
//    // checkPeople
////    <?xml version="1.0" encoding="UTF-8" ?><bdxx><scsfzh>13082819870623733X</scsfzh><sccph></sccph><scsbbh>yadr-001</scsbbh>
////    <scsbid>192.168.1.1</scsbid><scsblx>1</scsblx><scsj>2016-10-20 22:22:22</scsj><scsbzd>0002</scsbzd><scyhxm>陈红志</scyhxm>
////    <scyhsfzh>13082819870623733X</scyhsfzh></bdxx>
//    private String getCheckPeopleData(UploadInfo uploadInfo) {
//        uploadInfo.setUploadName("金陽");
//        uploadInfo.setUploadId("11010219781027232X");
//
//        uploadInfo.setUploadTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//        uploadInfo.setSbbh(Build.MODEL.toUpperCase().toString());
//        uploadInfo.setSblx("1");
//        uploadInfo.setZddm("0002");
//        uploadInfo.setMacAdress("192.168.1.1");
//        // <?xml version="1.0" encoding="UTF-8" ?><bdxx><scsfzh></scsfzh><sccph>蒙A12345</sccph><scsbbh>yadr-001</scsbbh><scsbid>192.168.1.1</scsbid><scsblx>1</scsblx><scsj>2016-10-20 22:22:22</scsj><scsbzd>0002</scsbzd><scyhxm>陈红志</scyhxm><scyhsfzh>13082819870623733X</scyhsfzh></bdxx>
//
//        return "<bdxx>" + "<scsfzh>" + uploadInfo.getCardNumber() + "</scsfzh>"
//                + "<sccph>" + uploadInfo.getCarNo() + "</sccph>"
//                + "<scsbbh>" + uploadInfo.getSbbh() + "</scsbbh>"
//                + "<scsbid>" + uploadInfo.getAdress() + "</scsbid>"
//                + "<scsblx>" + uploadInfo.getSblx() + "</scsblx>"
//                + "<scsj>" + uploadInfo.getUploadTime() + "</scsj>"
//                + "<scsbzd>" + uploadInfo.getZddm() + "</scsbzd>"
//                + "<scyhxm>" + uploadInfo.getUploadName() + "</scyhxm>"
//                + "<scyhsfzh>" + uploadInfo.getUploadId() + "</scyhsfzh>" + "</bdxx>";
//    }
//
//    public void upload(String method, final UploadInfo uploadInfo, OnUploadListener lisenner) {
//        this.method = method;
//        this.listener = lisenner;
//        slave.obtainMessage(ACTION_UPLOAD, uploadInfo).sendToTarget();
//    }
//
//
//    private Object execute(SoapObject msg) throws IOException, XmlPullParserException {
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
//        envelope.bodyOut = msg;
//        envelope.dotNet = true;
//        envelope.setOutputSoapObject(msg);
//        HttpTransportSE transport = new HttpTransportSE(UPLOAD_URL, TIMEOUT);
//        transport.call(msg.getNamespace() + msg.getName(), envelope);
//        return envelope.bodyIn;
//    }
////    CheckCar
////            CheckCarDetail
////    CheckPeople
////            CheckPeopleDetail
////    checkpeoplecz
//
//// checkPeople
////    <?xml version="1.0" encoding="UTF-8" ?><bdxx><scsfzh>13082819870623733X</scsfzh><sccph></sccph><scsbbh>yadr-001</scsbbh>
////    <scsbid>192.168.1.1</scsbid><scsblx>1</scsblx><scsj>2016-10-20 22:22:22</scsj><scsbzd>0002</scsbzd><scyhxm>陈红志</scyhxm>
////    <scyhsfzh>13082819870623733X</scyhsfzh></bdxx>
//
//
//    private void upload(UploadInfo uploadInfo) {
//        SoapObject msg = new SoapObject(nameSpace, method);
//        msg.addProperty("xmlbdxx", getCheckPeopleData(uploadInfo));
//        Log.e("HttpApi", msg.toString());
//        SoapObject resp = null;
//        Pair<UploadInfo, String> ret;
//        try {
//            resp = (SoapObject) execute(msg);
//            SoapPrimitive result = (SoapPrimitive) resp.getProperty(0);
//            Log.e("HttpApi return", result.toString());
//            // <bk_kyqk>测试布控人员2</bk_kyqk><bk_hcnr>123456789012345678</bk_hcnr>
//            //<Row><Data>000</Data><Data/></Row>
//            // <Row><Data>SFZH</Data<Data>JYAQ</Data><Data>MZ</Data><Data>XM</Data></Row>
//            // <Row><Data>230602195208233411</Data><Data>简要案情测试信息</Data><Data/><Data>陆治平</Data></Row>
//
//            // <?xml version="1.0" encoding="UTF-8"?><RBSPMessage><Version/><ServiceID>S10-00000006</ServiceID><TimeStamp/><Validity/><Security><Signature Algorithm="des"/><CheckCode Algorithm=""/> <Encrypt/></Security><Method><Name>Query</Name><Items><Item><Value Type="arrayOfArrayOf_string"><Row><Data>000</Data><Data/></Row><Row><Data>SFZH</Data<Data>JYAQ</Data><Data>MZ</Data><Data>XM</Data></Row><Row><Data>230602195208233411</Data><Data>简要案情测试信息</Data><Data/><Data>陆治平</Data></Row></Value></Item></Items></Method></RBSPMessage>
//            String res = result.toString();
//            Log.e("HttpApi", "返回信息  :" + res);
//            if("CheckPeopleDetail".equals(method)){
//                if (res.length() > 10 && res.contains(uploadInfo.getCardNumber())) {
//                    String in = res.substring(res.lastIndexOf("<Row>"), res.lastIndexOf("</Row>"));
//                    String info = in.replace("<Row>", "").replace("<Data>", "").replace("<Data/>", "").replace("</Data>", ",");
//                    String[] str = info.split(",");
//                    if (str[1] != null) {
//                        uploadInfo.setAjxq(str[1]);
//                    }
//                    if (str[2] != null) {
//                        uploadInfo.setName(str[2]);
//                    }
//                    res = "";
//                    Log.e("HttpApi", info + "   " + in);
//                }
//            }else if("CheckCarDetail".equals(method)){
//                //<bk_kyqk>测试布控车辆</bk_kyqk><bk_hcnr>蒙A12345</bk_hcnr>
//                if (res.length() > 10 && res.contains(uploadInfo.getCarNo())) {
//                    String info = res.replace("<bk_kyqk>","").replace("<bk_hcnr>","").replace("</bk_kyqk>",",").replace("</bk_hcnr>",",");
//                    String str[] = info.split(",");
//                    if (str[0] != null) {
//                        uploadInfo.setAjxq(str[0]);
//                    }
//                    if (str[1] != null) {
//                        uploadInfo.setCarNo(str[1]);
//                    }
//                    res = "";
//                    Log.e("HttpApi", info );
//                }
//            }
//
//
//            // 230602195208233411
//            ret = Pair.create(uploadInfo, res);
//            master.obtainMessage(SUCCESS, ret).sendToTarget();
//
//        } catch (XmlPullParserException e) {
//            ret = Pair.create(uploadInfo, ERROR_XML_PARSE);
//            master.obtainMessage(FAIL, ret).sendToTarget();
//            e.printStackTrace();
//        } catch (IOException e) {
//            ret = Pair.create(uploadInfo, ERROR_NET);
//            master.obtainMessage(FAIL, ret).sendToTarget();
//            e.printStackTrace();
//        } catch (ClassCastException e) {
//            ret = Pair.create(uploadInfo, ERROR_SERVER);
//            master.obtainMessage(FAIL, ret).sendToTarget();
//            e.printStackTrace();
//        }
////        <?xml version="1.0" encoding="UTF-8" ?><bdxx><scsfzh>13082819870623733X</scsfzh><sccph></sccph><scsbbh>yadr-001</scsbbh><scsbid>192.168.1.1</scsbid><scsblx>1</scsblx><scsj>2016-10-20 22:22:22</scsj><scsbzd>0002</scsbzd><scyhxm>陈红志</scyhxm><scyhsfzh>13082819870623733X</scyhsfzh></bdxx>
//
//
//    }
//
//    /**
//     * 接口返回参数对照表
//     *
//     * @param returnCode
//     * @return
//     */
//    public static String praseCode(String returnCode) {
//        String msg = "";
//        switch (returnCode) {
//            case "000":
//                msg = "核查信息正常";
//                break;
//            case "100":
//                msg = "在逃人员";
//                break;
//            case "001":
//                msg = "布控人员";
//                break;
//            case "101":
//                msg = "布控和在逃同时存在";
//                break;
//            case "200":
//                msg = "未核查，接口核查错误";
//                break;
//            case "300":
//                msg = "缺少必要身份信息";
//                break;
//            case "400":
//                msg = "数据插入数据库错误";
//                break;
//            case "500":
//                msg = "上传xml解析错误";
//                break;
//            default:
//                break;
//
//        }
//        return msg;
//    }
//
//
//    public interface OnUploadListener {
//        void success(UploadInfo UploadInfo, String errorMsg);
//
//        void fail(UploadInfo UploadInfo, String errorMsg);
//    }
//
//}
