package com.kosbet.java;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternetConnection {

	private Context _context;

	public CheckInternetConnection(Context context) {
		this._context = context;
	}

	public boolean checkInternetConn() {
		ConnectivityManager connectivity = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo info1 = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			
			if (info1 != null || info != null) {
				if (info1.isConnected() || info.isConnected()) {
					return true;
				}
			}
			/*else if(info != null){
				if (info.isConnected()) {
					return true;
				}else{
					return false;
				}
			}*/
		}
		return false;
	}
}
