package com.ctp.android.ppm.activities;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.R.id;
import com.ctp.android.ppm.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * The home screen of the application. 
 * @author kbiels
 *
 */
public class MainScreenActivity extends Activity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		Button btnWeek = (Button) findViewById(R.id.btnSelectWeek);
		btnWeek.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goToWeekView();
			}
		});
		
		Button btnDay = (Button) findViewById(R.id.btnSelectDay);
		btnDay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToDayView();
			}
		});
	}

	private void goToWeekView() {
		startActivity(new Intent(this, WeekViewActivity.class));
	}
	
	private void goToDayView() {
		startActivity(new Intent(this, CalendarViewActivity.class));
	}
}
