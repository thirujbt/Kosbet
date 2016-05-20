package com.kosbet;

import java.util.ArrayList;
import java.util.List;
import com.kosbet.bean.Listpackage;
import com.kosbet.dao.ListPackageDao;
import com.lock.AppInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class KosbetSplash extends Activity {
	private TextView version;
	
	private RelativeLayout rl_main;	
	Intent i;
	List<ResolveInfo> infos;
	ListPackageDao lld;
	public static List<String> packme = new ArrayList<String>();
	public static List<String> applnm = new ArrayList<String>();
	
	List<String> packagename = new ArrayList<String>();
	List<String> appname = new ArrayList<String>();
	AppInfo myApp;	
	String packName,lable,iemi;
	List<String> temp=new ArrayList<String>();
	List<Listpackage> status;
	TelephonyManager TM;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			startIntent();
			super.handleMessage(msg);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash);
		
		init();
		
		AlphaAnimation aa=new AlphaAnimation(0.5f, 1.0f);
		aa.setDuration(1000);
		rl_main.startAnimation(aa);
		handler.sendEmptyMessageDelayed(0, 3000);

	}
	private void init() {
		version = (TextView) findViewById(R.id.tv_version);		
		rl_main=(RelativeLayout) findViewById(R.id.rl_main);
		version.setText(getVersion());
	}

	private String getVersion(){
		try {
			PackageInfo info=getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
			return "KOSBET "+info.versionName;
		} catch (NameNotFoundException e) {
			return "KOSBET";
		}	
	}
	private void startIntent(){
		Intent intent=new Intent(this,Kosbet.class);
		startActivity(intent);
		finish();
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
