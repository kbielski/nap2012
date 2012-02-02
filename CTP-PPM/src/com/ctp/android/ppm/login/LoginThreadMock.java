package com.ctp.android.ppm.login;

import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.activities.LoginActivity;
import com.ctp.android.ppm.activities.WeekViewActivity;
import com.ctp.android.ppm.model.JsonAuthenticationModel;
import com.ctp.android.ppm.model.JsonTokenModel;
import com.ctp.android.ppm.model.SharedModel;

/**
 * Thread that executes the authentication logic.
 * 
 * @author kbiels
 * 
 */
public class LoginThreadMock extends Thread {

	private String user;
	private String password;
	private ProgressDialog loginDialog;
	private Activity loginActivity;
	private boolean loginSuccessfully;

	public LoginThreadMock(String user, String password, Activity loginActivity, ProgressDialog loginDialog) {
		this.user = user;
		this.password = password;
		this.loginActivity = loginActivity;
		this.loginDialog = loginDialog;
	}

	/**
	 * Calls the authentication service with the given user and password. 
	 */
	public void run() {

		JsonAuthenticationModel authenticationModel = new JsonAuthenticationModel();
		authenticationModel.setUsername(user);
		authenticationModel.setPassword(password);
		
		String url = loginActivity.getString(R.string.URL_BASE_PPM) + loginActivity.getString(R.string.URL_LOGIN);
		RestTemplate restTemplate = new RestTemplate();
		JsonTokenModel token = restTemplate.postForObject(url, authenticationModel, JsonTokenModel.class);
		if(token != null) {
			Log.i(getName(), token.getTokenValue());
			SharedModel.getInstance().setToken(token);
			loginSuccessfully = true;
		}

		// close the login progress dialog
		loginDialog.dismiss();

		if (loginSuccessfully == true) {
			if(loginActivity != null) {
				((LoginActivity) loginActivity).onSuccessfullyLogged();
				loginActivity.startActivity(new Intent(loginActivity, WeekViewActivity.class));
			}
		} else {
			Looper.prepare();
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(loginActivity);
			alertBuilder.setMessage("Login Failed. Please check your username and password and try again.");
			alertBuilder.setTitle("Error");
			alertBuilder.setNeutralButton("Ok", null);
			alertBuilder.create().show();
			Looper.loop();
		}
	}
}
