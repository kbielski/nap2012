package com.ctp.android.ppm.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.utils.SharedOptionsMenu;

/**
 * The home screen of the application. Containing the week and daily selection view.
 * 
 * @author kbiels
 * 
 */
public class MainScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button btnWeek = (Button) findViewById(R.id.btnSelectWeek);
		btnWeek.setOnClickListener(clickWeekListener());

		Button btnDay = (Button) findViewById(R.id.btnSelectDay);
		btnDay.setOnClickListener(clickDayListener());
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

	private OnClickListener clickDayListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goToDayView();
			}
		};
	}

	private OnClickListener clickWeekListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				goToWeekView();
			}
		};
	}

	private void goToWeekView() {
		startActivity(new Intent(this, WeekViewActivity.class));
	}

	private void goToDayView() {
		startActivity(new Intent(this, CalendarViewActivity.class));
	}
}
