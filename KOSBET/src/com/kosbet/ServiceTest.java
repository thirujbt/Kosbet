package com.kosbet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import android.annotation.SuppressLint;
import android.app.AlertDialog;



import android.app.Service;
import android.os.IBinder;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class ServiceTest extends Service implements LocationListener {
	String IMEI_Number;
	String output = null;
	List<String> finalreport;
	boolean setForeground = false;
	// flag for GPS status
	boolean isGPSEnabled = false;
	protected LocationManager locManager;
	double latitude, longitude;
	Handler mHandler;
	// flag for network status
	boolean isNetworkEnabled = false;
	WakeLock wl;
	// flag for GPS status
	boolean canGetLocation = false;
	@SuppressWarnings("unused")
	private int nCounter = 0;
	Location location = null; // location

	TimerTask mTimerTask;
	final Handler handler = new Handler();
	Timer t = new Timer();
	Timer t1 = new Timer();
	// The minimum distance to change Updates in meters

	String provider;
	// The minimum time between updates in milliseconds

	int llt;
	// Declaring a Location Manager
	protected LocationManager locationManager;


	private ThreadGroup myThreads = new ThreadGroup("ServiceWorker");
	LocationListener li;
	Bundle b;
	String min;
	Integer secm;
	int miisec;
	TimerTask task;

	public String PREFS_IP = "IPADDRESS";
	public String PREFS_PORT = "PORT";
	String new_ip, new_port;
	private String IP_DATA = "ip";
	private String PORT_DATA = "port";

	@Override
	public void onCreate() {
		super.onCreate();

		secm = 60 * 5;
		miisec = secm * 1000;
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// getting network status
		isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (isGPSEnabled) {
			if (location == null) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 10000000, 100, this);
				Log.d("GPS Enabled", "GPS Enabled");
				if (locationManager != null) {
					mTimerTask = new TimerTask() {
						public void run() {
							handler.post(new Runnable() {
								public void run() {
									location = locationManager
											.getLastKnownLocation(LocationManager.GPS_PROVIDER);

									if (location != null) {
										onLocationChanged(location);

									}

								}
							});
						}
					};

					
					t.schedule(mTimerTask, 10, miisec);
				}
			}
		}

		else if (isNetworkEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 10000000, 100, this);
			Log.d("Network", "Network");
			// Toast.makeText(getApplicationContext(), "network",
			// Toast.LENGTH_LONG).show();
			if (locationManager != null) {
				mTimerTask = new TimerTask() {
					public void run() {
						handler.post(new Runnable() {
							public void run() {

								location = locationManager
										.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
								if (location != null) {

									onLocationChanged(location);

								}

							}
						});
					}
				};

				// public void schedule (TimerTask task, long delay, long
				// period)
				t.schedule(mTimerTask, 10, miisec);
			}
		}

	}

	@SuppressLint("Wakelock")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		if (intent != null) {
			int counter = 1;
			

		}
		return START_STICKY;
	}

	
	@Override
	public void onDestroy() {
		myThreads.interrupt();

		t.cancel();
		t1.cancel();
		// mTimerTask.cancel();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onLocationChanged(final Location location) {
		this.location = location;

		nCounter++;
		final double latitude = location.getLatitude();
		final double longitude = location.getLongitude();
		TelephonyManager telephonyManager = (TelephonyManager) ServiceTest.this
				.getSystemService(Context.TELEPHONY_SERVICE);
		IMEI_Number = telephonyManager.getDeviceId();
		Log.d("your device IMEI number -->", IMEI_Number);
		java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
		java.text.DateFormat tf = new java.text.SimpleDateFormat("HH:mm:ss");
		final String todaysdate = df.format(new java.util.Date());
		final String todaystime = tf.format(new java.util.Date());

		if (isOnline(getApplicationContext())) {// offline data

			final String values = "ANN" + "&" + latitude + "&" + longitude
					+ "&" + IMEI_Number + "&" + todaysdate + "&" + todaystime;
			try {
				GetXMLTask jsonOperations = new GetXMLTask();
				getIP();
				if(new_ip!=null)
				{
				String path = "http://" + new_ip + ":" + new_port
						+ "/KOSBET/resources/location/" + values;

				// String path =
				// "http://192.168.2.13:8080/KOSBET/resources/location/"
				// + values;
				System.out.println("path :" + path);
				path = path.replaceAll(" ", "%20");
				jsonOperations.execute(new String[] { path });
				}
			} catch (Exception e) {
				System.out.println("Location is not geting");
			}

		}
	}

	private void getIP() {
		String ip = "";
		String port = "";
		SharedPreferences settings = getSharedPreferences(PREFS_IP, 0);
		SharedPreferences settings1 = getSharedPreferences(PREFS_PORT, 0);
		ip = settings.getString(IP_DATA, null);
		port = settings1.getString(PORT_DATA, null);
		if (ip == null) {
			if (port == null) {

			}

		} else {
			new_ip = ip;
			new_port = port;
		}

	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	private class GetXMLTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String output = null;
			for (String url : urls) {
				output = getOutputFromUrl(url);
			}
			return output;
		}

		private String getOutputFromUrl(String url) {
			String output = null;
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
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
}