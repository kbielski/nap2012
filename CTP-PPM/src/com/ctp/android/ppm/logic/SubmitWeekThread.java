package com.ctp.android.ppm.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

public class SubmitWeekThread extends Thread {

	private int mWeek;
	private int mYear;
	private Activity mActivity;
	private ProgressDialog mDialog;

	public SubmitWeekThread(int week, int year, Activity activity,
			ProgressDialog dialog) {
		this.mWeek = week;
		this.mYear = year;
		this.mActivity = activity;
		this.mDialog = dialog;
	}

	@Override
	public void run() {

		// TODO:
		
		String result = callRESTWebservicervice("http://10.0.2.2:8580/jboss-as-login-kb/rest/customers/1");
		Log.d("WEBSERVICE:", result);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {

		}

		// close the login progress dialog
		mDialog.dismiss();

		// TODO: call the WS to submit this week
		// ..
	}

	String callRESTWebservicervice(String serviceURL) {
		// http get client
		HttpClient client = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet();

		try {
			// construct a URI object
			getRequest.setURI(new URI(serviceURL));
		} catch (URISyntaxException e) {
			Log.e("URISyntaxException", e.toString());
		}

		// buffer reader to read the response
		BufferedReader in = null;
		// the service response
		HttpResponse response = null;
		try {
			// execute the request
			response = client.execute(getRequest);
		} catch (ClientProtocolException e) {
			Log.e("ClientProtocolException", e.toString());
		} catch (IOException e) {
			Log.e("IO exception", e.toString());
		}
		try {
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
		} catch (IllegalStateException e) {
			Log.e("IllegalStateException", e.toString());
		} catch (IOException e) {
			Log.e("IO exception", e.toString());
		}
		StringBuffer buff = new StringBuffer("");
		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				buff.append(line);
			}
		} catch (IOException e) {
			Log.e("IO exception", e.toString());
			return e.getMessage();
		}

		try {
			in.close();
		} catch (IOException e) {
			Log.e("IO exception", e.toString());
		}
		// response, need to be parsed
		return buff.toString();
	}

}
