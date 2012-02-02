package com.ctp.android.ppm.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.login.LoginDbAdapter;
import com.ctp.android.ppm.login.LoginThreadMock;
import com.ctp.android.ppm.model.SharedModel;

/**
 * Login screen allowing the authentication in the PPM application.
 * 
 * @author kbiels
 * 
 */
public class LoginActivity extends Activity {

	/** UI component to input the user name **/
	private EditText mUser;
	
	/** UI component to input the password **/
	private EditText mPassword;
	
	/** UI component to check if auto login next time **/
	private CheckBox mAutoLogin;
	
	/** DB Adapter used to store the user credentials **/
	private LoginDbAdapter mDbHelper;
	
	public static final String LOGOUT = "logout";
	public static int ENDPOINT_CTP = 901;
	public static int ENDPOINT_OPENSHIFT = 902;

	/** 
	 * onCreate - called when the activity is first created.
	 * 
	 * Initialize the member fields and opens the DB connection.
	 * 
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		mUser = (EditText) findViewById(R.id.Username);
		mPassword = (EditText) findViewById(R.id.Password);
		mAutoLogin = (CheckBox) findViewById(R.id.AutoLoginBox);

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
		checkIfAutologinTheLastUser(logout);	
	}

	/**
	 * Getting the previous user credentials from the local DB.
	 * If the auto login option was checked then performing the login
	 * to the server.
	 */
	private void checkIfAutologinTheLastUser(boolean logoutPerformed) {
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
		cursor.close();
		mUser.setText(usernameTxt);
		mPassword.setText(passwordTxt);
		mAutoLogin.setChecked(autologinVal);
		if (autologinVal && logoutPerformed == false) {
			performLogin();
		}
	}

	/**
	 * Calling the web servive to authenticate the user.
	 */
	private void performLogin() {
		ProgressDialog loginProgressDialog = ProgressDialog.show(
				LoginActivity.this, getString(R.string.please_wait),
				getString(R.string.logging_into_ppm), true, true,
				null);

		LoginThreadMock loginThread = new LoginThreadMock(mUser.getText()
				.toString(), mPassword.getText().toString(), this,
				loginProgressDialog);
		loginThread.start();
	}
	
	/**
	 * Saves the successfully logged user and closes the DB connection.
	 */
	public void onSuccessfullyLogged() {
		saveUserInDb();
		closeDb();
	}

	/**
	 * Save the user credentials to the local DB.
	 */
	private void saveUserInDb() {
		String usernameTxt = mUser.getText().toString();
		String passwordTxt = mPassword.getText().toString();
		int autologinValue = mAutoLogin.isChecked() ? 1 : 0;

		mDbHelper.saveNewUser(usernameTxt, passwordTxt, autologinValue);
	}
	
	/**
	 * Starting the service to inform about the accepted hours to the user.
	 */
	private void startAcceptedHoursService() {
		//Intent intent = new Intent(this, AcceptedHoursService.class);
		//startService(intent);
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
	
	public void closeDb() {
		if(mDbHelper != null) {
			mDbHelper.close();	
		}
	}
	/**************** END LIFE CYCLE METHODS ****************/
	
	/**************** SWITCH BETWEEN ENDPOINTS *************/

	/**
	 * This is called right before the menu is shown. Adding or removing the 
	 * endpoint options depending which one is currently selected.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// add or remove the CTP endpoint to the menu
		if (menu.findItem(ENDPOINT_CTP) != null
				&& SharedModel.getInstance().isConnectedToCTP() == true) {
			menu.removeItem(ENDPOINT_CTP);
		} else if (menu.findItem(ENDPOINT_CTP) == null
				&& SharedModel.getInstance().isConnectedToCTP() == false) {
			MenuItem pasteItem = menu.add(0, ENDPOINT_CTP, 0,
					getString(R.string.endpoint_ctp));
			pasteItem.setIcon(R.drawable.home);
		}
		//add or remove the OpenShift endpoint to the menu
		if (menu.findItem(ENDPOINT_OPENSHIFT) != null
				&& SharedModel.getInstance().isConnectedToCTP() == false) {
			menu.removeItem(ENDPOINT_OPENSHIFT);
		} else if (menu.findItem(ENDPOINT_OPENSHIFT) == null
				&& SharedModel.getInstance().isConnectedToCTP() == true) {
			MenuItem pasteItem = menu.add(0, ENDPOINT_OPENSHIFT, 0,
					getString(R.string.endpoint_openshift));
			pasteItem.setIcon(R.drawable.world);
		}
		
		return true;
	}
	
	/**
	 * React to the select Options menu items. 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == ENDPOINT_CTP) {
			SharedModel.getInstance().setConnectedToCTP(true);
			SharedModel.getInstance().clearModel();
			return true;
		} else if (item.getItemId() == ENDPOINT_OPENSHIFT) {
			SharedModel.getInstance().setConnectedToCTP(false);
			SharedModel.getInstance().clearModel();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**************** END SWITCH BETWEEN ENDPOINTS *************/
}