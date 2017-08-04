package cn.hdcloudwalk.httprequest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class HDHttpPhonePost extends AsyncTask<String, Integer, String> {

    private Context context;
    private String sfzh;
    private ProgressDialog pd;
    private long totalSize;
    private String license;
    private String xm;
    private String pic;
    private String cardNum;
    private String phoneNum;
    private String mainjsessionid;
    private String dqm;
    private String dhyys;
    private Handler handler;
    private String mmethodName;
    private SystemConfiguration config = SystemConfiguration.getInstance();

    public HDHttpPhonePost(Context context, String sfzh, String xm, String phoneNum, String dhyys, String mainjsessionid, Handler handler, String methodName) {
        this.context = context;
        this.sfzh = sfzh;
        this.xm = xm;
//        this.pic = pic;
//        this.cardNum = cardNum;
        this.phoneNum = phoneNum;
        this.dhyys = dhyys;
        this.mainjsessionid = mainjsessionid;
        this.handler = handler;
        this.dqm = "220122";
        this.mmethodName = methodName;
//        this.license="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><dwdm>1</dwdm><dwmc>测试单位</dwmc><xtzch>100000120151223065559000000992</xtzch><xtmc>测试系统</xtmc><lic>df9f4a566059aa3cfb38794d93ef7277</lic></root>";
    }

    static {
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH){
//            // 4.0以后需要加入下列两行代码，才可以访问Web Service
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads().detectDiskWrites().detectNetwork()
//                    .penaltyLog().build());
//
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
//                    .penaltyLog().penaltyDeath().build());
//        }
        //4.0以前版本不需要以上设置
    }


    @Override
    protected void onPreExecute() {
        //  pd = new ProgressDialog(context);
        // pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // pd.setMessage("照片比对中...");
        // pd.setCancelable(false);
        // pd.show();
        pd = ProgressDialog.show(context, "正在核查", "请稍后...", true, false);
    }

    @Override
    protected String doInBackground(String... params) {
        String serverResponse = null;
        String nameSpace = "http://webservice.my.com.cn/";
        // 调用的方法名称
        String methodName = this.mmethodName;// "verificationIdentity";
        // EndPoint
//		String endPoint = "http://"+config.getIp()+":"+config.getPort()+"/hdafis/services/hdWebService";
        //  202.111.188.122
//		String endPoint = "http://192.168.1.113:8089/hdafis/services/hdWebService";
//		String endPoint = "https://192.168.1.112:8443/hdafis/services/hdWebService";

        String endPoint = "https://202.111.188.117:8443/hdafis/services/hdTelWebService";
        // SOAP Action
        String soapAction = null;

        // 指定WebService的命名空间和调用的方法名
//        String lic = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><dwdm>20</dwdm><dwmc>光电子</dwmc><xtzch>100001920170413095942000000361</xtzch><xtmc>光电子</xtmc><lic>93D1ECE650983665DE651302DC21AAD2</lic></root>";
        String lic = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><dwdm>32</dwdm><dwmc>银安</dwmc><xtzch>100001720170711162537000000821</xtzch><xtmc>身份认证系统</xtmc><lic>68E3FAC3AEBFC82EECDD59092AFF97E4</lic></root>";

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><gmsfhm>" + sfzh + "</gmsfhm><xm>" + xm + "</xm><dhhm>" + phoneNum + "</dhhm><dhyys>" + dhyys + "</dhyys></root>";
        Log.e("HDHttpPhonePost","手机身份请求数据:"+xml);

        try {/*
            CustomMultiPartEntity multipartContent = new CustomMultiPartEntity(
                    new ProgressListener() {
                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });*/
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
            rpc.addProperty("license", lic);
            rpc.addProperty("params", xml);


            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = false;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);
            //	AndroidHttpTransport httpTranstation=new AndroidHttpTransport(WSDL);
            //	HttpsTransportSE transportS = new KeepAliveHttpsTransportSE(config.getIp(),Integer.parseInt(config.getPort()),"/hdafis/services/hdWebService",90000);
            SSLConnection.allowAllSSL(); /////////////////////
            HttpTransportSE transport = new HttpTransportSE(endPoint);

            transport.call(null, envelope);
            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;
            // 获取返回的结果
            serverResponse = object.getProperty(0).toString();
            Message message;
            message = handler.obtainMessage(2, 1, 1, serverResponse);
            message.what = 13;
            handler.sendMessage(message);
            //System.out.println(serverResponse);
            // Toast.makeText(context, serverResponse, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Message message;
            message = handler.obtainMessage(2, 1, 1, e.getMessage());
            message.what = 11;
            handler.sendMessage(message);
            // e.printStackTrace();
        }
        return serverResponse;
    }


    // @Override
    protected String doInBackground1(String... params) {
        String serverResponse = null;
        String nameSpace = "http://webservice.my.com.cn/";
        // 调用的方法名称
        String methodName = "verificationIdentity";
        // EndPoint
//		String endPoint = "http://"+config.getIp()+":"+config.getPort()+"/hdafis/services/hdWebService";
        String endPoint = "http://192.168.1.112:8088/hdafis/services/hdWebService?wsdl";
        // SOAP Action
        String soapAction = null;

        // 指定WebService的命名空间和调用的方法名

        String lic = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><dwdm>10</dwdm><dwmc>系统测试</dwmc><xtzch>100000720160726094447000000618</xtzch><xtmc>测试系统</xtmc><lic>94d42b9fb5e2ea333b2e5643149eca98</lic></root>";
        String parmas = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><gmsfhm>" + sfzh + "</gmsfhm><xm>" + xm + "</xm><xczp>" + pic + "</xczp><ywdm></ywdm><ywmc></ywmc></root>";

        try {/* 
            CustomMultiPartEntity multipartContent = new CustomMultiPartEntity(
                    new ProgressListener() {
                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });*/
            SoapObject rpc = new SoapObject(nameSpace, methodName);

            // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
//    		rpc.addProperty("gmsfhm", sfzh);
//    		rpc.addProperty("xm", xm);
//    		rpc.addProperty("xczp", pic);
            rpc.addProperty("license", lic);
            rpc.addProperty("parmas", parmas);
            // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            envelope.bodyOut = rpc;
            // 设置是否调用的是dotNet开发的WebService
            envelope.dotNet = false;
            // 等价于envelope.bodyOut = rpc;
            envelope.setOutputSoapObject(rpc);
            //	AndroidHttpTransport httpTranstation=new AndroidHttpTransport(WSDL);
            HttpTransportSE transport = new HttpTransportSE(endPoint);


            transport.call(null, envelope);


            // 获取返回的数据
            SoapObject object = (SoapObject) envelope.bodyIn;
            // 获取返回的结果
            serverResponse = object.getProperty(0).toString();
            Message message;
            message = handler.obtainMessage(2, 1, 1, serverResponse);
            message.what = 13;
            handler.sendMessage(message);
            //System.out.println(serverResponse);
            // Toast.makeText(context, serverResponse, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Message message;
            message = handler.obtainMessage(2, 1, 1, e.getMessage());
            message.what = 11;
            handler.sendMessage(message);
            // e.printStackTrace();
        }
        return serverResponse;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        pd.setProgress((int) (progress[0]));
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("result: " + result);
        pd.dismiss();
    }

    @Override
    protected void onCancelled() {
        Log.e("Log", "cancle");
        System.out.println("cancle");
    }

}