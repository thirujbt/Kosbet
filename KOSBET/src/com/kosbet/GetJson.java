package com.kosbet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;

import android.os.AsyncTask;


public class GetJson extends AsyncTask<String, Void, String> {
	Context context;
	InputStream is;

	String result = "";
	JSONObject jsonAppnObj;
	HttpClient httpclient;
	HttpGet httpget;
	HttpResponse response;
	HttpEntity entity;
	BufferedReader in;
	StringBuilder sb;
	String line;
	public GetJson(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {
		is = null;
		for (String url : params) {
			try {
				httpclient = new DefaultHttpClient();
				httpget= new HttpGet(url);
				System.out.println("HttpGet:" + httpget);
				response = httpclient.execute(httpget);
				System.out.println("HttpGetResponse :" + response);
				entity = response.getEntity();
				is = entity.getContent();
				publishProgress();
			} catch (Exception ex) {
				return null;
			}
		}
		try {
			in= new BufferedReader(new InputStreamReader(is));
			sb= new StringBuilder();
			
			while ((line = in.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
			System.out.println("JSON Obj:" + result);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception");
		}
		try {
			jsonAppnObj = new JSONObject(result);
			publishProgress();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;

		} catch (NullPointerException e) {
			return null;
		}
		return jsonAppnObj.toString();
	}

	public JSONObject getJsonResponseObject() {

		return jsonAppnObj;
	}
}
