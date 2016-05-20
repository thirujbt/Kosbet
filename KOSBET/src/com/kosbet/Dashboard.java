package com.kosbet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.TelephonyManager;


@SuppressLint("SimpleDateFormat")
public class Dashboard extends Service 
{
	
	private String gprs;
	private String batteryStatus;
	private String networkStatus;
	String GpsStatus,IMEI_Number;
	GetXMLTask jsonOperations;
	TelephonyManager telephonyManager;
	IntentFilter batteryFilter;
	Context context=this;
	public static String PREFS_IP = "IPADDRESS";
	public static String PREFS_PORT = "PORT";
	String new_ip,new_port;
	private static String IP_DATA = "ip";
	private static String PORT_DATA = "port";
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	public Dashboard() {
	}
	public void onCreate() 
	{
		super.onCreate();
		getIP();
		telephonyManager = (TelephonyManager) Dashboard.this
				.getSystemService(Context.TELEPHONY_SERVICE);
		batteryFilter= new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryMonitor, batteryFilter);
		jsonOperations = new GetXMLTask();
		IMEI_Number = telephonyManager.getDeviceId();
		mTimer = new Timer();
		mTimer.schedule(timerTask, 0, 6000 * 10*3);
	}
	private void getIP() 
	{
		String ip = "";
		String port = "";
		SharedPreferences settings = getSharedPreferences(PREFS_IP, 0);
		SharedPreferences settings1 = getSharedPreferences(PREFS_PORT, 0);
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
	private Timer mTimer;
	TimerTask timerTask = new TimerTask() {
		
			
				public void run() 
				{
					
					telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					int simState = telephonyManager.getSimState();
					switch (simState) {
					case TelephonyManager.SIM_STATE_ABSENT:					
						networkStatus="Not Available";						
					break;
					case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
					
						networkStatus="Not Available";
					break;
					case TelephonyManager.SIM_STATE_PIN_REQUIRED:
					
					break;
					case TelephonyManager.SIM_STATE_PUK_REQUIRED:
					
					break;
					case TelephonyManager.SIM_STATE_READY:
					
						networkStatus="Available";
					break; 
					case TelephonyManager.SIM_STATE_UNKNOWN:
					
						networkStatus="Not Available";
					break;
					}
					
					 displayGpsStatus();
					 
					gprs=isOnline(context);
					
					java.text.DateFormat df = new java.text.SimpleDateFormat(
							"yyyy-MM-dd");
					java.text.DateFormat tf = new java.text.SimpleDateFormat("HH:mm:ss");
					final String todaysdate = df.format(new java.util.Date());
					final String todaystime = tf.format(new java.util.Date());
					final String values = "ANN" + "&" +IMEI_Number+"&"+ GpsStatus + "&" +networkStatus  + "&"
							+ gprs + "&" + batteryStatus + "&" + todaysdate + "&"
							+todaystime ;
					
					try {
						
						//182.74.73.29:80
						getIP();
						if(new_ip!=null)
						{
						jsonOperations = new GetXMLTask();
						String path = "http://"+new_ip+":"+new_port+"/KOSBET/resources/dashboard/"
								+ values;
						System.out.println("path :" + path);
						path = path.replaceAll(" ", "%20");
						jsonOperations.execute(new String[] { path });
						}
						
					} catch (Exception e) {
						System.out.println("DashBoard 8");
						
					}
					
				}

		
		};
	
	private String isOnline(Context context) 
	{
		 ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo netInfo = cm.getActiveNetworkInfo();
	        if (netInfo != null && netInfo.isConnected()) {
	           return "True";
	        }
	        return "False";
	}
	private BroadcastReceiver batteryMonitor = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			int batteryLevel = arg1.getIntExtra("level", 0);
			batteryStatus=String.valueOf(batteryLevel);
		}
	};
	@SuppressWarnings("deprecation")
	private void displayGpsStatus() {
		ContentResolver contentResolver = getBaseContext().getContentResolver();
		boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
		if (gpsStatus) {			
			GpsStatus="ON";
		} else {
			
			GpsStatus="OFF";
		}
	}	
	private class GetXMLTask extends AsyncTask<String, Void, String>
	{
		
		@Override
		protected String doInBackground(String... urls) {
			String output = null;
			for (String url : urls) {
				output = getOutputFromUrl(url);
			}
			return output;
		}    
		private String getOutputFromUrl(String url) {
			String output = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse = httpClient.execute(httpGet);

				HttpEntity httpEntity = httpResponse.getEntity();

				output = EntityUtils.toString(httpEntity);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return output;
		}

	}
	public void onDestroy() {
		super.onDestroy();
		try {
			mTimer.cancel();
			timerTask.cancel();			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
		
}
