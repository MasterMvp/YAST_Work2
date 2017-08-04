package cn.hdcloudwalk.httprequest;

import android.content.Context;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class SystemConfiguration implements java.io.Serializable{
	private static Properties config = new Properties();
	private static final long serialVersionUID = 100004L;
	private static SystemConfiguration s_instance = null;
	private static String tag = "Sysconfig";
	public static Context mContext;
	public static String ip= "202.111.188.117";
	public static String port= "9999";
	public static String mainjsessionid="";
	public static boolean  PropType=false ;

	public static HttpClient httpClient=null;
	public static boolean sfdl=false;


	public static void getHttpClient() {

		httpClient=null;
		HttpParams httpParams = new BasicHttpParams();


		HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);


		HttpClientParams.setRedirecting(httpParams, true);


		String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
		HttpProtocolParams.setUserAgent(httpParams, userAgent);

		httpClient = new DefaultHttpClient(httpParams);
	}
	public static String doPostS(String url, List<NameValuePair> params) {
		BasicHttpContext localContext= new BasicHttpContext();
		CookieStore CookieJar=new BasicCookieStore();
		HttpPost httpRequest = new HttpPost(url);
		httpRequest.setHeader("Cookie",  "isMobileDevice=Yes; domain="+ip);

		String strResult = "";

		try {
			String _cookies=mainjsessionid;
			String[] cookies=_cookies.split(";");
			for (int i=0;i<cookies.length;i++)
			{
				String[] nvp=cookies[i].split("=");
				BasicClientCookie c=new BasicClientCookie(nvp[0],nvp[1]);
				c.setDomain(ip);
				CookieJar.addCookie(c);
			}
			localContext.setAttribute(ClientContext.COOKIE_STORE, CookieJar);
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = httpClient.execute(httpRequest,localContext);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());

			} else {
				Log.e(tag, "Error Response: "
						+ httpResponse.getStatusLine().toString());
			}
		} catch (ClientProtocolException e) {
			Log.e(tag, e.getMessage().toString());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(tag, e.getMessage().toString());
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(tag, e.getMessage().toString());
			e.printStackTrace();
		}

		Log.v("strResult", strResult);
		return strResult;
	}



	public static String doPostN(String url, MultipartEntity  params) {

		HttpPost httpRequest = new HttpPost(url);
		httpRequest.setHeader("Cookie",  "isMobileDevice=Yes; domain="+ip);

		String strResult = "doPostError";

		try {
			httpRequest.setEntity(params);
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());

			} else {
				strResult = "Error Response: "
						+ httpResponse.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (IOException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (Exception e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		}

		Log.v("strResult", strResult);

		return strResult;
	}
	public static String doGet(String url, Map params) {
		String paramStr = "";

		Iterator iter = params.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			paramStr += paramStr = "&" + key + "=" + val;
		}

		if (!paramStr.equals("")) {
			paramStr = paramStr.replaceFirst("&", "?");
			url += paramStr;
		}
		HttpGet httpRequest = new HttpGet(url);
		httpRequest.setHeader("Cookie",  "isMobileDevice=Yes; domain="+ip);
		String strResult = "doGetError";
		try {
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());

			} else {
				strResult = "Error Response: "
						+ httpResponse.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (IOException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (Exception e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		}

		Log.v("strResult", strResult);

		return strResult;
	}

	public static String doPost(String url, List<NameValuePair> params) {

		HttpPost httpRequest = new HttpPost(url);
		httpRequest.setHeader("Cookie",  "isMobileDevice=Yes; domain="+ip);

		String strResult = "doPostError";

		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());

			} else {
				strResult = "Error Response: "
						+ httpResponse.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (IOException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (Exception e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		}

		Log.v("strResult", strResult);

		return strResult;
	}
	public static String getDictData(String fl,String id,String filterdata)
	{
		String urlStr = getServerurl()+"/dictxml";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("fl",fl));
		params.add(new BasicNameValuePair("id",id));
		params.add(new BasicNameValuePair("fdata",filterdata));
		return doPostS(urlStr,params);
	}

	public synchronized static void readConfig()
	{
		try
		{
			if (PubTools.fileIsExists(PubTools.getTempPath()+"/xmlguiconfig.properties"))
			{
				Log.e(tag, "read config...");
				FileInputStream s = new FileInputStream(PubTools.getTempPath()+"/xmlguiconfig.properties");
				config.load(s);
				Log.d(tag, "load over");
				s.close();
				PropType=true;
			}
			else
			{
				Log.e(tag, "create config...");
				java.io.InputStream is = null;
				ClassLoader cl = null;
				cl = Thread.currentThread().getContextClassLoader();
				Log.e(tag,"useing classloader" + cl);

				is = cl.getResourceAsStream("config.properties");
				Log.e(tag,"InputStream:" + is.available());

				config.load(is);
				is.close();
			}
			ip = config.getProperty("ip", "202.111.188.177");
			port =config.getProperty("port", "9999");
			mainjsessionid=config.getProperty("jsessionid", "");

		} catch (Exception e)
		{
			Log.e(tag,"" + e);
		}
	}

	public static synchronized void setIp(String v)
	{
		ip=v;
		config.put("ip", ip);
	}

	public static synchronized void setPort(String v)
	{
		port=v;
		config.put("port", port);
	}
	public static synchronized String getIp()
	{
		return ip;
	}
	public static synchronized String getPort()
	{
		return port;
	}
	public static synchronized String getServerurl()
	{
		return "http://"+ip+":"+port+"/hdsyrkweb";
	}
	public static synchronized String getProp(String propname)
	{
		return config.getProperty(propname, "");
	}

	public static synchronized void setProp(String propname,String propvalue)
	{
		config.put(propname, propvalue);
	}

	public static synchronized void SaveConfig()
	{
		try {
			FileOutputStream s = new FileOutputStream(PubTools.getTempPath()+"/xmlguiconfig.properties", false);

			config.put("ip", ip);
			config.put("port", port);

			config.store(s, "");
			s.flush();
			s.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public SystemConfiguration()
	{
		Log.e(tag, "in SystemConfiguration constructure");
		readConfig();
		PubTools.getSDPath();
		PubTools.getTempPath();
		//File f=new File(PubTools.getSDPath()+"/cxddj");  
		// if(f.exists() && f.isDirectory()){
		//	PubTools.fileml= f.listFiles();
		// }

	}

	public static synchronized SystemConfiguration getInstance()
	{
		if (s_instance == null)
			s_instance = new SystemConfiguration();

		return s_instance;
	}


}
