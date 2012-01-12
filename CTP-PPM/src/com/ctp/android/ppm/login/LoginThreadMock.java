package com.ctp.android.ppm.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Looper;

import com.ctp.android.ppm.MainScreenActivity;

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
	private LoginActivity loginActivity;
	private boolean loginSuccessfully;

	public LoginThreadMock(String user, String password, LoginActivity loginActivity, ProgressDialog loginDialog) {
		this.user = user;
		this.password = password;
		this.loginActivity = loginActivity;
		this.loginDialog = loginDialog;
	}

	/**
	 * Calls the authentication service with the given user and password. 
	 */
	public void run() {

		// TODO: implement authentication logic
		try {
			Thread.sleep(1000);
			if (user.equals("ppm") && password.equals("")) {
				loginSuccessfully = true;
			} else {
				loginSuccessfully = false;
			}
		} catch (InterruptedException e) {

		}
		//END TODO

		// close the login progress dialog
		loginDialog.dismiss();

		if (loginSuccessfully == true) {

			//TODO: save the user and password if checkbox remember is checked
			//..
			
			loginActivity.startActivity(new Intent(loginActivity, MainScreenActivity.class));
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
