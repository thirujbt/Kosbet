package com.kosbet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kosbet.bean.Listpackage;
import com.kosbet.dao.ListPackageDao;
import com.kosbet.java.CheckInternetConnection;
import com.lock.AppInfo;
import com.lock.BlackDao;
import com.lock.ListInfo;
import com.lock.MD5Utils;
import com.lock.WatchDog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SdCardPath")
public class Kosbet extends Activity implements OnItemSelectedListener {
	private PackageManager packageManager, pm;
	private SharedPreferences sp;
	ComponentName cn;
	Intent nullActivity;
	ArrayList<HashMap<String, Object>> items;
	List<PackageInfo> packs;
	HashMap<String, Object> map;
	String packageName1;
	Uri SMS_STATUS_URI;
	String usernam;
	String pass;
	ProgressDialog myPd_ring;
	String ip = "";
	String port = "";
	
	
	
	SMSObserver smsSentObserver;
	public static final String CATEGORY_APP_CALCULATOR = "android.intent.category.APP_CALCULATOR";
	ServiceHandler sh;
	Dialog custom;
	EditText userName, pwd;
	ImageButton login, cancel;
	private Button call, sms, team, locationfinder, expencive, skale, time,
			settings, calulator;
	private ImageButton apploader, refresh, upload;
	private TextView name;
	private StringBuffer packagenamelist = new StringBuffer();
	private StringBuffer Appnamelist = new StringBuffer();
	List<ApplicationInfo> packages;
	GetXMLTask jsonOperations;
	EditText IP;
	EditText Port;
	public static List<String> packme = new ArrayList<String>();
	public static List<String> applnm = new ArrayList<String>();
	TelephonyManager TM;
	String iemi, pack, value, User, name1 = "", packageName = "", packName,
			lable, provider, packname, appName, path;
	String new_ip, new_port;
	int size, i1;
	String jsonStr, output;
	DefaultHttpClient httpClient;
	private BlackDao dao;
	List<String> packagename = new ArrayList<String>();
	List<String> appname = new ArrayList<String>();
	Boolean isConnectionExist = false;
	CheckInternetConnection connection;
	ListPackageDao lld;
	AppInfo myApp;
	int read;
	List<AppInfo> appInfos;
	Spinner spin;
	public static final String PREFS_NAME = "USER";
	public static String PREFS_IP = "IPADDRESS";
	public static String PREFS_PORT = "PORT";
	View layout;
	private static String IP_DATA = "ip";
	private static String PORT_DATA = "port";
	private static final String USERNAME = "username";
	List<ResolveInfo> infos;
	AssetManager assetManager;
	InputStream in = null;
	OutputStream out = null;
	byte[] buffer = new byte[1024];
	Editor edit;
	Intent intent, i, launchIntent;
	Context context = this;
	JSONObject jsonObj, jsn;
	JSONArray pack1, appnme;

	static ConnectivityManager cm;
	static NetworkInfo netInfo;
	HttpGet httpGet;
	HttpResponse httpResponse;
	HttpEntity httpEntity;
	Method dataConnSwitchmethod;
	Class<?> telephonyManagerClass;
	Object ITelephonyStub;
	Class<?> ITelephonyClass;
	Method getITelephonyMethod;
	List<Listpackage> status;
	List<String> temp = new ArrayList<String>();
	LayoutInflater inflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		pm = getPackageManager();
		assetManager = getAssets();
		dao = new BlackDao(getBaseContext());
		TM = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		iemi = TM.getDeviceId();
		name = (TextView) findViewById(R.id.name);
		spin = (Spinner) findViewById(R.id.spinner1);
		packme.add("");
		applnm.add("Select app");
		getIP();
		getname();
		turnGPSOn();
		turnOnDataConnection(true, context);
		//start alert send sms
		SmsManager sms1 = SmsManager.getDefault();
		String name1 = "";
		SharedPreferences settings1 = getSharedPreferences(PREFS_NAME, 0);
		name1 = settings1.getString(USERNAME, null);
		if(name1==null)
		{
			name1="Unknown";
			sms1.sendTextMessage("9176903430", null, name1+" is Start KOSBET. The IMEI number is "+iemi, null, null);
		}
		else
		{
			sms1.sendTextMessage("9176903430", null, name1+" is Start KOSBET. The IMEI number is "+iemi, null, null);
		}
		
		
		
		apploader = (ImageButton) findViewById(R.id.go);
		upload = (ImageButton) findViewById(R.id.upload);

		time = (Button) findViewById(R.id.time);
		settings = (Button) findViewById(R.id.settings);
		calulator = (Button) findViewById(R.id.calulator);

		call = (Button) findViewById(R.id.call);
		sms = (Button) findViewById(R.id.sms);
		team = (Button) findViewById(R.id.team);
		refresh = (ImageButton) findViewById(R.id.refresh);
		locationfinder = (Button) findViewById(R.id.location);
		expencive = (Button) findViewById(R.id.expence);
		skale = (Button) findViewById(R.id.skale);
		packageManager = getPackageManager();
		appInfos = new ArrayList<AppInfo>();
		lld = new ListPackageDao(getApplicationContext());
		lld.createDatabase();
		lld.open();
		dao.deleteall();

		try {
			i = new Intent(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_LAUNCHER);
			infos = packageManager.queryIntentActivities(i, 0);
			for (ResolveInfo info : infos) {
				myApp = new AppInfo();
				packName = info.activityInfo.packageName;
				lable = (String) info.loadLabel(packageManager);
				temp.add(packName);
				packagename.add(packName);
				appname.add(lable);
				dao.add(packName);

			}

			dao.add("com.sec.android.app.controlpanel");
			dao.add("com.sec.android.app.launcher");
			dao.add("com.android.systemui");
			dao.delete("com.kosbet");
			dao.delete("com.example.latlong");
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
			status = lld.select("ANN", iemi, "1");
			lld.open();
			if (status.size() != 0) {
				for (int i = 0; i < status.size(); i++) {
					packme.add(status.get(i).getPackagename());
					applnm.add(status.get(i).getApplicationname());
					dao.delete(status.get(i).getPackagename());
				}
			}

		} catch (Exception e) {

		}

		spin.setAdapter(new MyAdapter(context, R.layout.custom_spinner, applnm));
		spin.setOnItemSelectedListener(this);
		apploader.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				try {
					launchIntent = pm.getLaunchIntentForPackage(packageName);
					startActivity(launchIntent);
				} catch (Exception e1) {
					Toast.makeText(context, "Please Select Application",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		try {
			pm = getPackageManager();
			
			for (int i = 0; i < 19; i++) {
				cn = new ComponentName("com.kosbet", "com.kosbet.kosbet" + i);
				pm.setComponentEnabledSetting(cn,
						PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
						PackageManager.DONT_KILL_APP);
				nullActivity = new Intent();
				nullActivity.setComponent(cn);
				nullActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(nullActivity);
				Thread.sleep(20);
			}
			Thread.sleep(100);
			for (int i = 0; i < 18; i++) {
				cn = new ComponentName("com.kosbet", "com.kosbet.kosbet" + i);
				pm.setComponentEnabledSetting(cn,
						PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
						PackageManager.DONT_KILL_APP);
			}
		} catch (Exception e) {
			
		}

		calulator.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				try {					
					
					items = new ArrayList<HashMap<String, Object>>();

					 pm= getPackageManager();
					 packs= pm.getInstalledPackages(0);
					for (PackageInfo pi : packs) {
						if (pi.packageName.toString().toLowerCase()
								.contains("calcul")) {
							 map = new HashMap<String, Object>();
							map.put("appName",
									pi.applicationInfo.loadLabel(pm));
							map.put("packageName", pi.packageName);
							items.add(map);
						}
					}
					if (items.size() >= 1) {
						packageName1= (String) items.get(0).get(
								"packageName");
						nullActivity = pm.getLaunchIntentForPackage(packageName1);
						if (nullActivity != null)
							startActivity(nullActivity);
					} else {
						Toast.makeText(context, "Application not found",
								Toast.LENGTH_LONG).show();
					}
				} catch (Exception e1) {
					Toast.makeText(context, "Application not found",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		upload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				init();
				upload();
			}
		});
		time.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(
						android.provider.Settings.ACTION_DATE_SETTINGS));
			}
		});
		settings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getdata();
			}
		});

		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (isOnline(getApplicationContext())) {
					Editname();
					new Refresh(getBaseContext()).execute();
				} else {
					Toast.makeText(context, "Internet Connection Failure",
							Toast.LENGTH_LONG).show();
					status = lld.select("ANN", iemi, "1");
					lld.open();
					for (int i = 0; i < status.size(); i++) {
						packme.add(status.get(i).getPackagename());
						applnm.add(status.get(i).getApplicationname());
					}
				}
				if (packme.isEmpty()) {
					Toast.makeText(context,
							"Internet Connection Failure  Refresh again",
							Toast.LENGTH_LONG).show();
					applnm.add("Select App");
					packme.add("");
					lld.open();
					status = lld.select("ANN", iemi, "1");
					for (int i = 0; i < status.size(); i++) {
						packme.add(status.get(i).getPackagename());
						applnm.add(status.get(i).getApplicationname());
					}
					lld.close();
					spin.setAdapter(new MyAdapter(Kosbet.this,
							R.layout.custom_spinner, applnm));
				}
			}
		});
		call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				i = new Intent();
				i.setAction("android.intent.action.DIAL");
				i.setData(Uri.parse("tel:"));
				startActivity(i);
			}
		});
		locationfinder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				try {
					launchIntent = pm
							.getLaunchIntentForPackage("com.example.latlong");
					startActivity(launchIntent);
				} catch (Exception e1) {
					Toast.makeText(context, "Please install Location finder",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		team.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				try {
					launchIntent = pm
							.getLaunchIntentForPackage("com.teamviewer.quicksupport.market");
					startActivity(launchIntent);
				} catch (Exception e1) {
					Toast.makeText(context, "Please install Team Viewer",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		expencive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					launchIntent = pm
							.getLaunchIntentForPackage("com.example.expensive");
					startActivity(launchIntent);
				} catch (Exception e1) {
					Toast.makeText(context, "Please install Expense Tool",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		skale.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					launchIntent = pm
							.getLaunchIntentForPackage("com.skale");
					startActivity(launchIntent);
				} catch (Exception e1) {
					Toast.makeText(context, "Please install SKALE",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		if (checkapp2() == true) {

		} else {

			try {
				in = assetManager.open("ExpenseTool.apk");
				out = new FileOutputStream("/sdcard/ExpenseTool.apk");

				while ((read = in.read(buffer)) != -1) {
					out.write(buffer, 0, read);
				}
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
						Uri.fromFile(new File("/sdcard/ExpenseTool.apk")),
						"application/vnd.android.package-archive");
				startActivity(intent);
			} catch (Exception e) {
			}
		}
		

		if (checkapp1() == true) {

		} else {

			try {
				in = assetManager.open("QuickRemote.apk");
				out = new FileOutputStream("/sdcard/QuickRemote.apk");

				while ((read = in.read(buffer)) != -1) {
					out.write(buffer, 0, read);
				}
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
						Uri.fromFile(new File("/sdcard/QuickRemote.apk")),
						"application/vnd.android.package-archive");
				startActivity(intent);
			} catch (Exception e) {
			}
		}

		if (checkapp3() == true) {

		} else {

			try {
				in = assetManager.open("LocationFinder.apk");
				out = new FileOutputStream("/sdcard/LocationFinder.apk");

				while ((read = in.read(buffer)) != -1) {
					out.write(buffer, 0, read);
				}
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
						Uri.fromFile(new File("/sdcard/LocationFinder.apk")),
						"application/vnd.android.package-archive");
				startActivity(intent);
			} catch (Exception e) {
			}
		}
		sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				try {
					packageName = "com.android.mms";
					launchIntent = pm.getLaunchIntentForPackage(packageName);
					startActivity(launchIntent);
				} catch (Exception e1) {
					Toast.makeText(context, "Please install SMS application",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		i = new Intent(context, WatchDog.class);
		startService(i);
	}

	
	void getdata() {
		custom = new Dialog(Kosbet.this);
		custom.setContentView(R.layout.login_dialog);
		userName = (EditText) custom.findViewById(R.id.editUser);
		pwd = (EditText) custom.findViewById(R.id.editPwd);
		login = (ImageButton) custom.findViewById(R.id.butLogin);
		cancel = (ImageButton) custom.findViewById(R.id.butCancel);
		custom.setTitle("Login");
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				usernam = userName.getText().toString();
				pass= pwd.getText().toString();
				if (usernam.length() != 0) {
					if (pass.length() != 0) {
						value = usernam + "&" + pass;
						if (isOnline(Kosbet.this)) {
							myPd_ring = ProgressDialog
									.show(Kosbet.this, "Please wait",
											"Loding...", true);
							myPd_ring.setCancelable(true);
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										synchronized (this) {
											wait(2000);
											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													try {

														// http://182.74.73.29/KOSBET/resources/userlogin/test&123
														GetJson jsonOperations = new GetJson(								Kosbet.this);
														 path = "http://182.74.73.29/KOSBET/resources/userlogin/"
																+ value;
														path = path.replaceAll(
																" ", "%20");
														jsonOperations
																.execute(new String[] { path });
														Thread.sleep(3000);
														 jsonObj = jsonOperations
																.getJsonResponseObject();
														String stas = jsonObj
																.getString(
																		"username")
																.toString();
														if (stas.equalsIgnoreCase("0")) {
															Toast.makeText(
																	getBaseContext(),
																	"Incorrect credential",
																	Toast.LENGTH_LONG)
																	.show();
														} else if (stas
																.equalsIgnoreCase("1")) {
															custom.dismiss();
															Toast.makeText(
																	getBaseContext(),
																	"Login successfully",
																	Toast.LENGTH_LONG)
																	.show();

															
															custom = new Dialog(
																	Kosbet.this);
														
															custom.setContentView(R.layout.ip_change_dialog);
															SharedPreferences settings1 = getSharedPreferences(	PREFS_IP, 0);
															SharedPreferences settings2 = getSharedPreferences(	PREFS_PORT,		0);
															IP = (EditText) custom.findViewById(R.id.editIP);
															Port = (EditText) custom
																	.findViewById(R.id.editPort);
															ImageButton submit = (ImageButton) custom
																	.findViewById(R.id.butSubmit);
															ImageButton cancel = (ImageButton) custom
																	.findViewById(R.id.butCancell);

															
															ip = settings1
																	.getString(
																			IP_DATA,
																			null);
															port = settings2
																	.getString(
																			PORT_DATA,
																			null);
															if (ip == null
																	|| port == null) {

																IP.setText("182.74.73.29");
																Port.setText("80");

															} else {
																IP.setText(ip);
																Port.setText(port);
															}

															custom.setTitle("Configuration");
															submit.setOnClickListener(new View.OnClickListener() {

																@Override
																public void onClick(
																		View v) {
																	String IPAdd = IP
																			.getText()
																			.toString();

																	String portNo = Port
																			.getText()
																			.toString();
																	if (IPAdd
																			.length() == 0) {
																		Toast.makeText(
																				getApplicationContext(),
																				"Enter IP address",
																				Toast.LENGTH_SHORT)
																				.show();
																	} else if (portNo
																			.length() == 0) {
																		Toast.makeText(
																				getApplicationContext(),
																				"Enter port number",
																				Toast.LENGTH_SHORT)
																				.show();
																	} else {

																		SharedPreferences settings1 = getSharedPreferences(
																				PREFS_IP,
																				0);
																		SharedPreferences settings2 = getSharedPreferences(
																				PREFS_PORT,
																				0);
																		SharedPreferences.Editor editor1 = settings1
																				.edit();
																		SharedPreferences.Editor editor2 = settings2
																				.edit();

																		editor1.putString(
																				IP_DATA,
																				IPAdd);
																		editor1.commit();

																		editor2.putString(
																				PORT_DATA,
																				portNo);
																		editor2.commit();
																		Toast.makeText(
																				getApplicationContext(),
																				"IP and Port number changed successfully",
																				Toast.LENGTH_LONG)
																				.show();

																		stopService(new Intent(
																				context,
																				ServiceTest.class));
																		stopService(new Intent(
																				context,
																				Dashboard.class));

																		startService(new Intent(
																				context,
																				ServiceTest.class));
																		startService(new Intent(
																				context,
																				Dashboard.class));
																		custom.dismiss();
																	}
																}
															});

															cancel.setOnClickListener(new View.OnClickListener() {

																@Override
																public void onClick(
																		View v) {
																	custom.dismiss();
																}
															});
														
															custom.show();
															
														}
													} catch (Exception e) {
														Toast.makeText(
																getBaseContext(),
																"Connection Failure",
																Toast.LENGTH_LONG)
																.show();
													}
												}
											});
										}
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									myPd_ring.dismiss();
								}
							}).start();
						} else {
							Toast.makeText(
									getBaseContext(),
									"No Internet Connections please try again later !!!!!!",
									Toast.LENGTH_SHORT).show();
						}
					} else {

						Toast.makeText(getApplicationContext(),
								"Enter password", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getApplicationContext(), "Enter user name",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				custom.dismiss();

			}
		});
		custom.show();
	}

	private void getIP() {
		SharedPreferences settings1 = getSharedPreferences(	PREFS_IP, 0);
		SharedPreferences settings2 = getSharedPreferences(	PREFS_PORT,		0);
		ip = settings1.getString(IP_DATA, null);
		port = settings2.getString(PORT_DATA, null);
		if (ip == null || port == null) {

			getdata();

		} else {
			new_ip = ip;
			new_port = port;
			i = new Intent(context, WatchDog.class);
			startService(i);
			i = new Intent(context, ServiceTest.class);      	  
			
			
			SMS_STATUS_URI= Uri.parse("content://sms");
			smsSentObserver = new SMSObserver(new Handler(), this);
			this.getContentResolver().registerContentObserver(SMS_STATUS_URI, true,
					smsSentObserver);
            startService(i);			
			startService(new Intent(context, Dashboard.class));
		}

	}

	private void init() {
		sp = getSharedPreferences("pwd", Activity.MODE_PRIVATE);
		String pwd = sp.getString("pwd", null);
		if (pwd == null) {
			String pwd1 = "password";
			String pwd2 = "password";

			if (pwd1.equals(pwd2) && pwd1 != null) {
				try {
					edit = sp.edit();
					edit.putString("pwd", MD5Utils.encode(pwd1));
					edit.commit();
					overridePendingTransition(R.anim.activity_in,
							R.anim.activity_out);
					intent = new Intent(context, ListInfo.class);
					startActivity(intent);
				} catch (Exception e) {

				}
			} else {

			}
		} else {
			initLog();
		}

	}

	private void initLog() {
		intent = new Intent(context, ListInfo.class);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		turnGPSOn();
		turnOnDataConnection(true, context);
		applnm.clear();
		packme.clear();
		applnm.add("Select App");
		packme.add("");
		lld.open();
		status = lld.select("ANN", iemi, "1");

		if (status.size() != 0) {
			for (int i = 0; i < status.size(); i++) {
				packme.add(status.get(i).getPackagename());
				applnm.add(status.get(i).getApplicationname());
			}
		}
		lld.close();
		spin.setAdapter(new MyAdapter(context, R.layout.custom_spinner, applnm));
		spin.setOnItemSelectedListener(this);
	}

	private void upload() {
		size = packagename.size();
		jsonOperations = new GetXMLTask();
		for (int i = 0; i < size; i++) {
			packname = packagename.get(i);
			appName = appname.get(i);
			packagenamelist.append(packname + ",");
			Appnamelist.append(appName + ",");
			int size2 = packagenamelist.length();
			int size3 = Appnamelist.length();
			if (size2 >= 300) {
				value = "ANN" + "&" + iemi + "&" + Appnamelist + "&"
						+ packagenamelist + "&" + "0";
			
				try {

					
					getIP();
					if(new_ip!=null)
					{
					path = "http://" + new_ip + ":" + new_port
							+ "/KOSBET/resources/listpackage/" + value;
				
					path = path.replaceAll(" ", "%20");
					jsonOperations.execute(new String[] { path });
					Thread.sleep(3000);
					}
					else
					{
						Toast.makeText(getApplicationContext(), "IP and PORT number not set \n Please configure IP and PORT..", Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					
				}
				packagenamelist.delete(0, size2);
				Appnamelist.delete(0, size3);
			}
		}
	}

	private void getname() {
		String name1 = "";
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		name1 = settings.getString(USERNAME, null);
		if (name1 == null) {
			new Getname(this).execute();

		} else
			name.setText(name1);
	}

	private void Editname() {

		new Getname(this).execute();

	}

	public boolean checkapp2() {

		pm = getPackageManager();
		packages = pm.getInstalledApplications(0);
		boolean enable = false;
		for (ApplicationInfo packageInfo : packages) {
			if (packageInfo.packageName.equals("com.example.expensive")) {
				enable = true;
			}
		}
		return enable;
	}

	public boolean checkapp1() {

		pm = getPackageManager();
		packages = pm.getInstalledApplications(0);
		boolean enable = false;
		for (ApplicationInfo packageInfo : packages) {
			if (packageInfo.packageName
					.equals("com.teamviewer.quicksupport.market")) {
				enable = true;
			}
		}
		return enable;
	}

	public boolean checkapp3() {
		
		pm = getPackageManager();
		packages = pm.getInstalledApplications(0);
		boolean enable = false;
		for (ApplicationInfo packageInfo : packages) {

			if (packageInfo.packageName.equals("com.example.latlong")) {
				enable = true;
			}
		}
		return enable;
	}

	public void onBackPressed() {
	}

	@SuppressWarnings("deprecation")
	public void turnGPSOn() {
		try {

			provider = Settings.Secure.getString(getContentResolver(),
					Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			if (!provider.contains("gps")) { // if gps is disabled
				final Intent poke = new Intent();
				poke.setClassName("com.android.settings",
						"com.android.settings.widget.SettingsAppWidgetProvider");
				poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
				poke.setData(Uri.parse("3"));
				sendBroadcast(poke);
			}
		} catch (Exception e) {
			Toast.makeText(context, "Autostart GPS Failure", Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		packageName = "";
		String appname = "";
		try {
			appname = ((TextView) arg1.findViewById(R.id.tvLanguage)).getText()
					.toString();
			if (appname != null) {
			
				for (int i = 0; i < applnm.size(); i++) {
					if (appname == applnm.get(i)) {
						packageName = packme.get(i);
		
					}
				}
			}
		} catch (Exception e) {

		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	int bv = Build.VERSION.SDK_INT;

	boolean turnOnDataConnection(boolean ON, Context context) {
		try {
			if (bv == Build.VERSION_CODES.FROYO)

			{

				TM = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				telephonyManagerClass = Class.forName(TM.getClass().getName());
				getITelephonyMethod = telephonyManagerClass
						.getDeclaredMethod("getITelephony");
				getITelephonyMethod.setAccessible(true);
				ITelephonyStub = getITelephonyMethod.invoke(TM);
				ITelephonyClass = Class.forName(ITelephonyStub.getClass()
						.getName());
				if (ON) {
					dataConnSwitchmethod = ITelephonyClass
							.getDeclaredMethod("enableDataConnectivity");
				} else {
					dataConnSwitchmethod = ITelephonyClass
							.getDeclaredMethod("disableDataConnectivity");
				}
				dataConnSwitchmethod.setAccessible(true);
				dataConnSwitchmethod.invoke(ITelephonyStub);
			
			} else {
				final ConnectivityManager conman = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				final Class<?> conmanClass = Class.forName(conman.getClass()
						.getName());
				final Field iConnectivityManagerField = conmanClass
						.getDeclaredField("mService");
				iConnectivityManagerField.setAccessible(true);
				final Object iConnectivityManager = iConnectivityManagerField
						.get(conman);
				final Class<?> iConnectivityManagerClass = Class
						.forName(iConnectivityManager.getClass().getName());
				final Method setMobileDataEnabledMethod = iConnectivityManagerClass
						.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
				setMobileDataEnabledMethod.setAccessible(true);
				setMobileDataEnabledMethod.invoke(iConnectivityManager, ON);
				
			}
			return true;
		} catch (Exception e) {
			Log.e("Test", "error turning on/off data");
			return false;
		}
	}

	private class GetXMLTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			output = null;
			for (String url : urls) {
				output = getOutputFromUrl(url);
			}
			return output;
		}

		private String getOutputFromUrl(String url) {
			output = null;
			try {
				httpClient = new DefaultHttpClient();
				httpGet = new HttpGet(url);
				httpResponse = httpClient.execute(httpGet);

				httpEntity = httpResponse.getEntity();

				output = EntityUtils.toString(httpEntity);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return output;
		}

	}

	public static boolean isOnline(Context context) {
		cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		

	}

	@Override
	protected void onPause() {
		super.onPause();
		
	}

	public class MyAdapter extends ArrayAdapter<String> {

		public MyAdapter(Context context, int textViewResourceId,
				List<String> tname) {
			super(context, textViewResourceId, tname);

		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {

			
			if (applnm.size() == 0) {
				applnm.add("Select app");
			}
			inflater= getLayoutInflater();
			layout= inflater.inflate(R.layout.custom_spinner, parent,
					false);			
			TextView tvLanguage = (TextView) layout
					.findViewById(R.id.tvLanguage);			
			tvLanguage.setText(applnm.get(position));			
			tvLanguage.setTextColor(Color.WHITE);
			int count = 0;			
			if (position == 0) {
				tvLanguage.setTextSize(15f);
				tvLanguage.setTextColor(Color.WHITE);
				count++;
				if (count == 0)
					tvLanguage.setVisibility(View.INVISIBLE);
			}

			return layout;
		}

		
		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {

			return getCustomView(position, convertView, parent);
		}

		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			return getCustomView(position, convertView, parent);
		}

	}

	public class CustomSpinnerSelection extends Spinner {

		private boolean mToggleFlag = true;

		public CustomSpinnerSelection(Context context, AttributeSet attrs,
				int defStyle, int mode) {
			super(context, attrs);
		}

		public CustomSpinnerSelection(Context context, AttributeSet attrs,
				int defStyle) {
			super(context, attrs, defStyle);
		}

		public CustomSpinnerSelection(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public CustomSpinnerSelection(Context context, int mode) {
			super(context);
		}

		public CustomSpinnerSelection(Context context) {
			super(context);
		}

		@Override
		public int getSelectedItemPosition() {
			if (!mToggleFlag) {
				return 0; 
			}
			return super.getSelectedItemPosition();
		}

		@Override
		public boolean performClick() {
			mToggleFlag = false;
			boolean result = super.performClick();
			mToggleFlag = true;
			return result;
		}
	}

	private class Getname extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;

		public Getname(Context context) {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialog = new ProgressDialog(context);
			dialog.setMessage("Please wait...");
			dialog.setCancelable(true);
			dialog.show();

		}

		protected String doInBackground(String... arg0) {
			try {
				getIP();
				if(new_ip!=null)
				{
				String getname = "http://" + new_ip + ":" + new_port
						+ "/KOSBET/resources/username/";
				value = "ANN" + "&" + iemi;
				sh = new ServiceHandler();
				jsonStr = sh.makeServiceCall(getname + value,
						ServiceHandler.GET);
				Log.d("Response: ", "> " + jsonStr);
				if (jsonStr != null) {
					try {
						jsonObj = new JSONObject(jsonStr);
						String jsn = jsonObj.getString("username").toString();
						if (jsn.equals(null)) {
							name1 = "Unknown name";
						} else {
							name1 = jsn;
							try {
								SharedPreferences settings = getSharedPreferences(
										PREFS_NAME, 0);
								SharedPreferences.Editor editor = settings
										.edit();
								editor.putString(USERNAME, name1);
								editor.commit();
							} catch (Exception e) {

							}
						}
					} catch (JSONException e) {

					}

				} else {
					Log.e("ServiceHandler",
							"Couldn't get any data from the server");
				}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "IP and PORT number not set \n Please configure IP and PORT..", Toast.LENGTH_LONG).show();

				}
				
			} catch (Exception e) {

			}

			return null;
		}

		protected void onPostExecute(String result) {

			try {
				if ((dialog != null) && dialog.isShowing()) {

					dialog.dismiss();
					name.setText(name1);

				}
			} catch (final IllegalArgumentException e) {
				
			} catch (final Exception e) {
				
			} finally {
				dialog = null;
			}

		}

	}

	private class Refresh extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog;

		public Refresh(Context context) {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			applnm.clear();
			packme.clear();
			applnm.add("Select App");
			packme.add("");
			dialog = new ProgressDialog(context);
			dialog.setMessage("Please wait...");
			dialog.setCancelable(true);
			dialog.show();

		}

		protected String doInBackground(String... arg0) {
			try {
				lld = new ListPackageDao(getApplicationContext());
				lld.createDatabase();
				lld.open();
				lld.delete();
				dao.deleteall();
				i = new Intent(Intent.ACTION_MAIN);
				i.addCategory(Intent.CATEGORY_LAUNCHER);
				infos = packageManager.queryIntentActivities(i, 0);
				for (ResolveInfo info : infos) {
					myApp = new AppInfo();
					packName = info.activityInfo.packageName;
					lable = (String) info.loadLabel(packageManager);
					packagename.add(packName);
					appname.add(lable);
					dao.add(packName);
				}
				dao.add("com.sec.android.app.controlpanel");
				dao.add("com.sec.android.app.launcher");
				dao.add("com.android.systemui");
				dao.delete("com.kosbet");
				dao.delete("com.example.latlong");
				dao.delete("com.android.mms");
				dao.delete("com.teamviewer.quicksupport.market");
				dao.delete("com.example.expensive");
				dao.delete("com.pointofsale");
				dao.delete("com.android.gallery3d");
				dao.delete("com.android.phone");
				dao.delete("com.android.contacts");
				dao.delete("com.sec.android.app.popupcalculator");
				dao.delete("com.android.calculator2");
				dao.delete("android.provider.Settings.ACTION_DATE_SETTINGS");
				dao.delete("com.skale");
				dao.delete("com.android.dialer");
				value = "ANN" + "&" + iemi + "&" + "1";
				getIP();
				if(new_ip!=null)
				{
				path = "http://" + new_ip + ":" + new_port
						+ "/KOSBET/resources/packageresponse/";

				sh = new ServiceHandler();
				jsonStr = sh
						.makeServiceCall(path + value, ServiceHandler.GET);
				Log.d("Response: ", "> " + jsonStr);
				if (jsonStr != null) {
					try {
						jsonObj = new JSONObject(jsonStr);

						try {
							jsn = jsonObj.getJSONObject("Response");
							pack1 = jsn.getJSONArray("PackageName");
							appnme = jsn.getJSONArray("applicationname");
							for (i1 = 0; i1 < pack1.length(); i1++) {
								packme.add(pack1.get(i1).toString());
								applnm.add(appnme.get(i1).toString());
								lld.insertlist("ANN", iemi, appnme.get(i1)
										.toString(), pack1.get(i1).toString(),
										"1");
								dao.delete(pack1.get(i1).toString());
							}

						} catch (Exception e) {

						}

					} catch (JSONException e) {
						
					}

				} else {
					Log.e("ServiceHandler",
							"Couldn't get any data from the server");
				}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "IP and PORT number not set \n Please configure IP and PORT..", Toast.LENGTH_LONG).show();

				}

			} catch (Exception e) {

			}
			lld.close();
			return null;
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			spin.setAdapter(new MyAdapter(Kosbet.this, R.layout.custom_spinner,
					applnm));
		}

	}

	protected void onDestroy() {
		super.onDestroy();
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		name1 = settings.getString(USERNAME, null);
		SmsManager sms1 = SmsManager.getDefault();
		sms1.sendTextMessage("9176903430", null, name1+"is closed KOSBET\n The IMEI number is "+iemi, null, null);
		try {
			trimCache(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void trimCache(Context context) {
		try {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
	}

	
}
