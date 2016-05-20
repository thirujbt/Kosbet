package com.kosbet;


import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class IPChangeActivity extends Activity 
{
	EditText ip,port;
	Button submit;
	String ip_data,port_data;
	public static String PREFS_IP = "IPADDRESS";
	public static String PREFS_PORT = "PORT";
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ipchange);
		ip=(EditText)findViewById(R.id.editIP);
		port=(EditText)findViewById(R.id.editPort);
		submit=(Button)findViewById(R.id.butSubmit);
		submit.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) {
				try {
					ip_data = ip.getEditableText().toString().trim();
					port_data = port.getEditableText().toString().trim();
					if(ip_data.length()!=0)
					{
						if(port_data.length()!=0)
						{
							finish();
							Intent i=new Intent(getApplicationContext(),Kosbet.class);
							startActivity(i);
						}
					}

					
				} catch (Exception e) {

				}
			}
			
		});


	}

	
}
