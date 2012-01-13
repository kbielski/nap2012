package com.ctp.android.ppm.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.utils.SharedOptionsMenu;

public class CalendarViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.calendar_view);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		SharedOptionsMenu.onCreateOptionsMenu(menu, menuInflater);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return SharedOptionsMenu.onOptionsItemSelected(item, this) ? true
				: super.onOptionsItemSelected(item);
	}
}
