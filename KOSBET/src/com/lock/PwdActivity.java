package com.lock;


import com.kosbet.Kosbet;
import com.kosbet.R;
import com.lock.WatchDog.MyBinder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class PwdActivity extends Activity implements OnClickListener{
	private MyBinder builder;
	private String packName;
	private MyConn conn;
	
	private Button button;
	@SuppressWarnings("unused")
	private SharedPreferences sp;
	@SuppressWarnings("unused")
	private int i=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		packName=getIntent().getExtras().getString("packName");	
		sp=getSharedPreferences("pwd", Context.MODE_PRIVATE);
		
		setContentView(R.layout.confirm);
		conn=new MyConn();		
		button=(Button) findViewById(R.id.bt_confirm);
		button.setOnClickListener(this);
		Intent server=new Intent(this,WatchDog.class);
		bindService(server, conn , Context.BIND_AUTO_CREATE);
	}
	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		try {		
			builder.stop(packName);
			Intent i= new Intent(this,Kosbet.class); 			 
			 startActivity(i);
			 
				
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Check password", 0).show();
		}
	
	}
	private class MyConn implements ServiceConnection{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			builder=(MyBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
			
		}
		
	}
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		unbindService(conn);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//start=System.currentTimeMillis();
		if(keyCode==KeyEvent.KEYCODE_BACK){
			i++;
			 Intent i= new Intent(this,Kosbet.class); 			 
			 startActivity(i);  
			return true;

		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	
	
	
	
}
