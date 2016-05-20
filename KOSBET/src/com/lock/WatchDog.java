package com.lock;

import java.util.ArrayList;
import java.util.List;

import com.kosbet.GetJson;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;

public class WatchDog extends Service {
	private BlackDao dao;
	private List<String> apps;	

	private MyBinder binder;
	private List<String> temps=new ArrayList<String>();
	private boolean flag;
	private BroadcastReceiver mLockReceiver;
	private IntentFilter filter;
	TelephonyManager TM;
	GetJson jsonOperations;
	Context c=this;
	String iemi;
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	public class MyBinder extends Binder{
		public void stop(String str){
				temps.add(str);
		}
		public void start(String str){
			
	}
		
	}
	@Override
	public void onCreate() {
		flag=true;
		binder=new MyBinder();
		mLockReceiver=new LockReceiver();
		filter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
		dao=new BlackDao(c);	
		
		new Thread(){
			@Override
			public void run() {
				while(flag){
					try
					{
						apps=dao.getAll();
						//new MyActivityManager(WatchDog.this).clearRecentTasks();
						
					}
					catch(Exception e)
					{
						
					}
					registerReceiver(mLockReceiver, filter);

					
					if(apps.contains("com.kosbet"))
					{
						apps.remove("com.kosbet");
					}
					ActivityManager am=(ActivityManager) getSystemService(ACTIVITY_SERVICE);
					List<RunningTaskInfo> tasks=am.getRunningTasks(1);
					
					
					String packName =tasks.get(0).topActivity.getPackageName();			
					String packName1 =tasks.get(0).topActivity.getClassName();
					if(packName1.contains("ActivityManager.RecentTaskInfo"))
					{
						System.out.println(packName1);
						am.killBackgroundProcesses (packName);
						
						Intent intent = new Intent(getApplicationContext(),
								PwdActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("packName", packName);
						startActivity(intent);
					}
					if(apps.contains(packName))
					{	
						if(packName.contains("com.android.settings"))
						{
							if(!packName1.contains("com.android.settings.Settings$DateTimeSettingsActivity"))
							{								
								System.out.println(packName);
								System.out.println(packName1);
								Intent intent = new Intent(getApplicationContext(),
										PwdActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.putExtra("packName", packName);
								startActivity(intent);
							}
							
						}
						else {
							System.out.println(packName);
							System.out.println(packName1);
							am.killBackgroundProcesses (packName);
							
							Intent intent = new Intent(getApplicationContext(),
									PwdActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packName", packName);
							startActivity(intent);
						}
					}		
					try {
						sleep(300);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
					}

				}				
			}		
		}.start();		
		super.onCreate();
			
	}

	

	@Override
	public void onDestroy() {
		super.onDestroy();
		flag=false;
		unregisterReceiver(mLockReceiver);
	}
	

}
