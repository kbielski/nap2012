package com.ctp.android.ppm.utils;

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

import android.util.Log;

/**
 * Standard way to call the REST web services.
 * 
 * @deprecated Using Spring-Android integration with the Rest Template.
 * 
 * @author kbiels
 *
 */
public class WebserviceHelper {

	public static String callRESTWebservicervice(String serviceURL) {
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
