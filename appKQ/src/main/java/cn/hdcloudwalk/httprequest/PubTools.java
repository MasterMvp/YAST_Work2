package cn.hdcloudwalk.httprequest;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import android.os.Environment; 
public class PubTools {
	public static String dicttype="p";
	public static String dictname="";
	public static String dictfilename="";
	public static String basefilter="";
	public static String infilter="";
	public static String aotofilter="0";
	public static int outsize=1;
	public static int pagecount=10;
	
	public static int screen_w=200, screen_h=200; 
	public static String SDPath="";
	public static String KBBM="";
	public static boolean ISSD=false;
	public static boolean TESTSD=false;
	public static File[]  fileml =null;
	public static String path="cchongda";
	public static String getSystemDateTime()
	{
		SimpleDateFormat datetimef = new SimpleDateFormat("yyyyMMddHHmmss");
		return datetimef.format(new Date());
	}
	public static String getSystemDate()
	{
		SimpleDateFormat datetimef = new SimpleDateFormat("yyyyMMdd");
		return datetimef.format(new Date());
	}
	public static String getSystemDate(String f)
	{
		SimpleDateFormat datetimef = new SimpleDateFormat(f);
		return datetimef.format(new Date());
	}
	public static boolean fileIsExists(String filename){			 
        File f=new File(filename);       
        if(!f.exists()){
                return false;
        }
       return true;

	}
	public static int getScreenXY( android.view.Display display)   
    {           
        int ver = Build.VERSION.SDK_INT;   

        DisplayMetrics dm = new DisplayMetrics();   
        display.getMetrics(dm);       
       
       if (ver < 13)   
        {   
    	    screen_w  = dm.widthPixels;  
            screen_h = dm.heightPixels;   
        }   
        else if (ver == 13)   
        {   
            try {   
                Method mt = display.getClass().getMethod("getRealHeight");   
                screen_h = (Integer) mt.invoke(display);  
                mt = display.getClass().getMethod("getRealWidth");
                screen_w = (Integer) mt.invoke(display);  
              //  WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);  

               // android.view.Display display1= wm.getDefaultDisplay();  

                //int width     = display1.getRawWidth();  

               // int height    = display.getRawHeight();  
            } catch (Exception e) {   
                //e.printStackTrace(); 
            	 screen_w  = dm.widthPixels;  
                 screen_h = dm.heightPixels;  
            }    
        }   
        else if (ver > 13)   
        {   
            try {   
                Method mt = display.getClass().getMethod("getRawHeight");   
                screen_h = (Integer) mt.invoke(display);  
                mt = display.getClass().getMethod("getRawWidth");
                screen_w = (Integer) mt.invoke(display);  
             
            } catch (Exception e) {   
               // e.printStackTrace(); 
            	 try {   
                     Method mt = display.getClass().getMethod("getRealHeight");   
                     screen_h = (Integer) mt.invoke(display);  
                     mt = display.getClass().getMethod("getRealWidth");
                     screen_w = (Integer) mt.invoke(display);  
                   //  WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);  

                    // android.view.Display display1= wm.getDefaultDisplay();  

                     //int width     = display1.getRawWidth();  

                    // int height    = display.getRawHeight();  
                 } catch (Exception e1) {   
                     //e.printStackTrace(); 
                	 screen_w  = dm.widthPixels;  
                     screen_h = dm.heightPixels;  
                 }  
            }   
        }               
    
        return 0;   
    } 
	public static void copyfile(File fromFile, File toFile,Boolean rewrite )
	{	
	  if (!fromFile.exists()) 
	  {
		  return;
	  }
	  if (!fromFile.isFile())
	  {
		  return ;
	   }
	  if (!fromFile.canRead()) {return ;}
	  if (!toFile.getParentFile().exists()) {toFile.getParentFile().mkdirs();}
	  if (toFile.exists() && rewrite)
	  { 
		toFile.delete();
	  }
	  try 
	  {
		  java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
		  java.io.FileOutputStream fosto = new FileOutputStream(toFile);
		  byte bt[] = new byte[1024];
		  int c;
		  while ((c = fosfrom.read(bt)) > 0) {
			  fosto.write(bt, 0, c); //������д�����ļ�����
		  }
		 fosfrom.close();
		 fosto.close();
		 } 
	  catch(Exception ex){
		  
	  }
	}
	
	public static String getSDPath(){ 
		   if (TESTSD==false) 
		   {
		       File sdDir = null; 
		       boolean sdCardExist = Environment.getExternalStorageState()   
		                           .equals(android.os.Environment.MEDIA_MOUNTED);   //�ж�sd���Ƿ����?
		       if (sdCardExist)   
		       {  
		    	 ISSD=true;
		         sdDir = Environment.getExternalStorageDirectory();//��ȡ��Ŀ¼ 
		         SDPath=sdDir.toString();
		       }  
		       TESTSD=true;
		   }			   
	       return SDPath; 	       
	} 
	
	public static String getTempPath(){ 
		File file = new File(PubTools.getSDPath()+"/" + path);               
	    file.mkdirs();  
	    return PubTools.getSDPath()+"/" + path;
	}
	
	public static String getTempPicPath(){ 
		File file = new File(getTempPath()+"/pic");               
	    file.mkdirs();  
	    return getTempPath()+"/pic";
	}
	
	public static String getTempvideoPath(){ 
		File file = new File(getTempPath()+"/video");               
	    file.mkdirs();  
	    return getTempPath()+"/video";
	}

	
}
