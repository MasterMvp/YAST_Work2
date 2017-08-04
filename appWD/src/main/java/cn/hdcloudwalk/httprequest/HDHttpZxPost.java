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

public class HDHttpZxPost extends AsyncTask<String, Integer, String> {

    private Context context;
    private String sfzh;
    private ProgressDialog pd;
    private long totalSize;
    private String license;
    private String xm;
    private String dhhm;
    private String mainjsessionid;
    private String dqm;
    private Handler handler;
    private String mmethodName;
    private SystemConfiguration config = SystemConfiguration.getInstance();

    /**
     * @param context        上下文
     * @param sfzh           身份证号码
     * @param xm             姓名
     * @param dhhm           电话号码
     * @param mainjsessionid ""
     * @param handler        接收返回的Handler
     * @param methodName     方法名
     */
    public HDHttpZxPost(Context context, String sfzh, String xm, String dhhm, String mainjsessionid, Handler handler, String methodName) {
        this.context = context;
        this.sfzh = sfzh;
        this.xm = xm;
        this.dhhm = dhhm;
        this.mainjsessionid = mainjsessionid;
        this.handler = handler;
        this.dqm = "220122";
        this.mmethodName = methodName;
//        this.license="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><dwdm>1</dwdm><dwmc>测试单位</dwmc><xtzch>100000120151223065559000000992</xtzch><xtmc>测试系统</xtmc><lic>df9f4a566059aa3cfb38794d93ef7277</lic></root>";
    }


    @Override
    protected void onPreExecute() {
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

        String endPoint = "https://202.111.188.117:8443/hdafis/services/hdZxWebService";
//        String endPoint = "https://202.111.188.117:8443/hdafis/services";
        // SOAP Action
        String soapAction = null;

        // 指定WebService的命名空间和调用的方法名
//        参数：<dwdm>20</dwdm><dwmc>银安</dwmc><xtzch>100001920170413095942000000361</xtzch><xtmc>身份认证系统</xtmc><lic>93D1ECE650983665DE651302DC21AAD2</lic>
//        String lic = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><dwdm>20</dwdm><dwmc>光电子</dwmc><xtzch>100001920170413095942000000361</xtzch><xtmc>光电子</xtmc><lic>93D1ECE650983665DE651302DC21AAD2</lic></root>";
//        String lic = "{'dwdm':'20','dwmc':'银安','xtzch':'100001920170413095942000000361','xtmc':'身份认证系统','lic':'93D1ECE650983665DE651302DC21AAD2'}";
        String lic = "{'dwdm':'32','dwmc':'银安','xtzch':'100001720170711162537000000821','xtmc':'身份认证系统','lic':'68E3FAC3AEBFC82EECDD59092AFF97E4'}";
//
//        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><gmsfhm>" + sfzh + "</gmsfhm><xm>" + xm + "</xm><dhhm>" + dhhm + "</dhhm><ywdm></ywdm><ywmc></ywmc></root>";
        String xml = "{'gmsfhm':'" + sfzh + "','xm':'" + xm + "','dhhm':'" + dhhm + "'}";
        Log.e("HDHttpZxPost", "请求数据:" + xml);

        try {
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
            message.what = 10;
            handler.sendMessage(message);
            //System.out.println(serverResponse);
            // Toast.makeText(context, serverResponse, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Message message;
            message = handler.obtainMessage(2, 1, 1, e.getMessage() + e.toString());
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
        System.out.println("cancle");
    }

}