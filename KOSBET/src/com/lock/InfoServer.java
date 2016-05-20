package com.lock;

import java.util.ArrayList;
import java.util.List;


import com.kosbet.GetJson;


import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.telephony.TelephonyManager;


public class InfoServer {
	private Context context;
	private BlackDao dao;
	private PackageManager pm;
	private List<AppInfo> appInfos;
	private AppInfo myApp;
	TelephonyManager TM;
	GetJson jsonOperations;
	
	Context c;
	String iemi;
	public InfoServer(Context context) {
		this.context = context;
		pm=context.getPackageManager();
		 TM = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	 		iemi = TM.getDeviceId();
	}

	public List<AppInfo> getAllApps(){
		
		appInfos=new ArrayList<AppInfo>();
		Intent intent = new Intent(Intent.ACTION_MAIN);      
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        dao=new BlackDao(context);      
        dao.deleteall();
        for(ResolveInfo info : infos){
        	myApp = new AppInfo();
			String packName=info.activityInfo.packageName;
			//System.out.println(packName);
			if(packName!="com.kosbet")
			{
				Drawable icon=info.loadIcon(pm);
				String lable=(String) info.loadLabel(pm);
				dao.add(packName);
				myApp.setIcon(icon);
				myApp.setLable(lable);
				myApp.setPackName(packName);
				appInfos.add(myApp);
				myApp=null;
			}				           
        }
        dao.add("com.sec.android.app.controlpanel");
		dao.add("com.sec.android.app.launcher");
		dao.add("com.android.systemui");
        dao.delete("com.kosbet");
		dao.delete("com.example.latlong");
		dao.delete("com.example.message");	
		dao.delete("com.android.mms");
		dao.delete("com.teamviewer.quicksupport.market");
		dao.delete("com.example.expensive");
		dao.delete("com.pointofsale");
		dao.delete("com.android.gallery3d");
		dao.delete("com.android.phone");
		dao.delete("com.android.contacts");    
		dao.delete("com.sec.android.app.popupcalculator"); 
		dao.delete("android.provider.Settings.ACTION_DATE_SETTINGS"); 
		dao.delete("com.android.calculator2");
		dao.delete("com.skale");
		dao.delete("com.android.dialer");
		return appInfos;
	}
	
}
