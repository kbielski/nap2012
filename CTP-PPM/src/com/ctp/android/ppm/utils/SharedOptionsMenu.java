package com.ctp.android.ppm.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.activities.LoginActivity;

public class SharedOptionsMenu {

	public static void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		menuInflater.inflate(R.menu.options_menu, menu);
	}

	public static boolean onOptionsItemSelected(MenuItem item, Activity activity) {
		switch (item.getItemId()) {
		case R.id.logoutOption:
			Intent intent = new Intent(activity, LoginActivity.class);
			intent.putExtra(LoginActivity.LOGOUT, true);
			activity.startActivity(intent);
			return true;
		default:
			return false;
		}
	}
}
