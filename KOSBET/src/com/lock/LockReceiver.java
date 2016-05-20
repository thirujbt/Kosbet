package com.lock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class LockReceiver extends BroadcastReceiver {

	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action=intent.getAction();			
		Intent server=new Intent(context,WatchDog.class);
		 if (Intent.ACTION_SCREEN_ON.equals(action)) { 
			
         } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {         	
         
         } else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) { 
        	            context.startService(server);
         }
	
	}	
}


















