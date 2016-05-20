package com.lock;

import java.util.ArrayList;
import java.util.List;




import com.kosbet.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

@SuppressLint("HandlerLeak")
public class ListInfo extends Activity {
	
	
	@SuppressWarnings("unused")
	private List<AppInfo> infos;
	private BlackDao dao;
	private List<String> temps;	

	List<PackageInfo> packageList1 = new ArrayList<PackageInfo>();
	List<PackageInfo> packageList2 = new ArrayList<PackageInfo>();
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {		
			super.handleMessage(msg);					
		}		
	};
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.list_info);
		dao=new BlackDao(getBaseContext());		
		new Thread(){
			@Override
			public void run() {
				InfoServer infoServer=new InfoServer(getApplicationContext());
				infos=infoServer.getAllApps();
				handler.sendEmptyMessage(0);
			}
			
		}.start();
		init();	
		Intent server=new Intent(getApplicationContext(), WatchDog.class);
		startService(server);		
		finish();		
		
		
	}

	private void init() {
		//lv_info=(ListView) findViewById(R.id.lv_info);
		dao=new BlackDao(getApplicationContext());		
		temps=dao.getAll();
		if(temps.contains("com.kosbet"))
		{
			temps.remove("com.kosbet");
		}
		
	}
	
		
		

	
	
	
	
	
	
	
	
	
	
	
	
}
