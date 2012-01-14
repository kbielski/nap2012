package com.ctp.android.ppm.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.login.LoginDbAdapter;
import com.ctp.android.ppm.login.LoginThreadMock;
import com.ctp.android.ppm.services.AcceptedHoursService;

/**
 * Login screen allowing the authentication in the PPM application.
 * 
 * @author kbiels
 * 
 */
public class LoginActivity extends Activity {

	private EditText user;
	private EditText password;
	private CheckBox autoLogin;
	private LoginDbAdapter mDbHelper;
	public static final String LOGOUT = "logout";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		user = (EditText) findViewById(R.id.Username);
		password = (EditText) findViewById(R.id.Password);
		autoLogin = (CheckBox) findViewById(R.id.AutoLoginBox);

		Button loginBtn = (Button) findViewById(R.id.LoginButton);
		loginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performLogin();
			}
		});

		mDbHelper = new LoginDbAdapter(this);
		mDbHelper.open();

		//call the service to check hours acceptance
		startAcceptedHoursService();
		
		//check if called from options menu to logout the current user
		Bundle extras = getIntent().getExtras();
		boolean logout = false;
		if (extras != null) {
			logout = extras.getBoolean(LOGOUT);
		}
		if(logout == false) {
			checkIfAutologinTheLastUser();	
		}
	}

	private void checkIfAutologinTheLastUser() {
		Cursor cursor = mDbHelper.fetchLastUserLogged();
		startManagingCursor(cursor);
		if (cursor == null || cursor.getCount() == 0) {
			return;
		}
		boolean autologinVal = cursor.getInt(cursor
				.getColumnIndexOrThrow(LoginDbAdapter.KEY_AUTOLOGIN)) > 0;
		String usernameTxt = cursor.getString(cursor
				.getColumnIndexOrThrow(LoginDbAdapter.KEY_USER));
		String passwordTxt = cursor.getString(cursor
				.getColumnIndexOrThrow(LoginDbAdapter.KEY_PASSWORD));
		user.setText(usernameTxt);
		password.setText(passwordTxt);
		autoLogin.setChecked(autologinVal);
		if (autologinVal) {
			performLogin();
		}
		cursor.close();
	}

	private void performLogin() {
		ProgressDialog loginProgressDialog = ProgressDialog.show(
				LoginActivity.this, getString(R.string.please_wait),
				getString(R.string.logging_into_ppm), true, true,
				new CancelLoginListener());

		saveUserInDb();

		LoginThreadMock loginThread = new LoginThreadMock(user.getText()
				.toString(), password.getText().toString(), this,
				loginProgressDialog);
		loginThread.start();
	}

	private void saveUserInDb() {
		String usernameTxt = user.getText().toString();
		String passwordTxt = password.getText().toString();
		int autologinValue = autoLogin.isChecked() ? 1 : 0;

		mDbHelper.saveNewUser(usernameTxt, passwordTxt, autologinValue);
	}
	
	
	private void startAcceptedHoursService() {
		Intent intent = new Intent(this, AcceptedHoursService.class);
		startService(intent);
	}

	/**************** LIFE CYCLE METHODS ****************/
	@Override
	protected void onPause() {
		super.onPause();
		if(mDbHelper != null) {
			mDbHelper.close();	
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mDbHelper != null) {
			mDbHelper.open();	
		}
	}
	
	@Override 
	protected void onDestroy() {
		super.onDestroy();
		if(mDbHelper != null) {
			mDbHelper.close();	
		}
	}
	
	/**************** END LIFE CYCLE METHODS ****************/

	private class CancelLoginListener implements OnCancelListener {

		@Override
		public void onCancel(DialogInterface dialog) {
			// liot.stopLoggingIn();
		}

	}
}