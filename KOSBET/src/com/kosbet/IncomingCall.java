package com.kosbet;


import java.io.File;



import android.os.Handler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import android.util.Log;


public class IncomingCall extends BroadcastReceiver {

	boolean isPhoneRinging = false;
	boolean isPhoneCalling = false;	
	File audiofile = null;
	Context mContext;
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		
		mContext = context;
		try {
			Log.i("BradCast Call", "Calling BroadCats");
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			isPhoneRinging = true;
			isPhoneCalling = true;
//			try
//			{
//			Intent myIntent = new Intent(mContext,
//					Callservice.class);
//			myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			mContext.stopService(myIntent);
//			}
//			catch(Exception e)
//			{
//				
//			}
			PhoneStateListener callStateListener = new PhoneStateListener() {
				public void onCallStateChanged(int state, String incomingNumber) {
					if (state == TelephonyManager.CALL_STATE_RINGING) {
						Log.i("Call Result", "Phone Is Riging");
						isPhoneRinging = false;
					}
					if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
					//	startRecording();
						isPhoneCalling = false;
					}
					if (state == TelephonyManager.CALL_STATE_IDLE) 					
					{
						
						Log.i("Call Result","phone is neither ringing nor in a call");
						if (isPhoneRinging) {
							if (isPhoneCalling) {
							//	stopRecording();
								Log.i("Call My Service", "Call My Service");
								Intent myIntent = new Intent(mContext,
										Callservice.class);
								myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								mContext.stopService(myIntent);
								Handler handler = new Handler();
								handler.postDelayed(new Runnable() {
									@Override
									public void run() 
									{
										Intent myIntent = new Intent(mContext,Callservice.class);
										myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										mContext.startService(myIntent);
									}

								}, 0);			
								
								//mContext.stopService(myIntent);
								isPhoneCalling = false;
								isPhoneRinging = false;
							}
						}
					}

				}				
			};
			telephonyManager.listen(callStateListener,
					PhoneStateListener.LISTEN_CALL_STATE);

		} catch (Exception e) {
			Log.e("Phone Receive Error", " " + e);
		}
	}	
	
	
	
		   
		    
		   

		 
	
		
		
		 


}