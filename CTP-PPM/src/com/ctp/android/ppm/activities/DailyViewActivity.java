package com.ctp.android.ppm.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.components.OnChangedSliderListener;
import com.ctp.android.ppm.components.SliderLayout;
import com.ctp.android.ppm.logic.GetLoggedHoursDayThread;
import com.ctp.android.ppm.utils.SharedOptionsMenu;

public class DailyViewActivity extends Activity {

	private TextView mLblDay;
	private TextView mLblLoggedHours;
	private Button btnSaveDay;
	private List<SliderLayout> mProjectSliderList;
	private int mDayOfTheYear;
	private int mYear;
	private double mHoursLogged;
	public static final String DAY = "day";
	public static final String YEAR = "year";
	public static final String HOURS_LOGGED = "hoursLogged";
	public static final String PROGRESS_LOGGED = "progressLogged";

	// handler to update the UI when the response from WS arrives
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// TODO: update the view with the WS response
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.day_view);

		mLblDay = (TextView) findViewById(R.id.txtDayLabel);
		mLblLoggedHours = (TextView) findViewById(R.id.txtLoggedHoursDailyView);
		btnSaveDay = (Button) findViewById(R.id.btnSaveDay);
		btnSaveDay.setOnClickListener(saveDayListener());

		mHoursLogged = 0;
		mProjectSliderList = new ArrayList<SliderLayout>();
		SliderLayout sl = (SliderLayout) findViewById(R.id.slider1);
		sl.setOnChangedSliderListener(changeSliderListener());
		mProjectSliderList.add(sl);
		sl = (SliderLayout) findViewById(R.id.slider2);
		sl.setOnChangedSliderListener(changeSliderListener());
		mProjectSliderList.add(sl);

		// get the day for this view
		Calendar cal = Calendar.getInstance();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mDayOfTheYear = extras.getInt(DAY);
			mYear = extras.getInt(YEAR);
			cal.clear();
			cal.set(Calendar.DAY_OF_YEAR, mDayOfTheYear);
			cal.set(Calendar.YEAR, mYear);
		} else {
			mDayOfTheYear = cal.get(Calendar.DAY_OF_YEAR);
			mYear = cal.get(Calendar.YEAR);
		}
		DateFormat df = new SimpleDateFormat("EEE, dd.MM.yyyy");
		mLblDay.setText(df.format(cal.getTime()));

		getHoursLoggedForThisDay();
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

	private void getHoursLoggedForThisDay() {
		ProgressDialog progressDialog = ProgressDialog.show(
				DailyViewActivity.this, getString(R.string.please_wait),
				getString(R.string.loading_hours_for_this_day), true, true,
				null);

		GetLoggedHoursDayThread threadMock = new GetLoggedHoursDayThread(
				mDayOfTheYear, mYear, this, progressDialog);
		threadMock.start();
	}

	// listen to the slider change and update the total hours logged amount
	private OnChangedSliderListener changeSliderListener() {
		return new OnChangedSliderListener() {
			@Override
			public void onSliderChanged(double hoursLogged) {
				mHoursLogged = 0;
				for (SliderLayout sl : mProjectSliderList) {
					mHoursLogged += sl.getHoursLogged();
				}
				mLblLoggedHours.setText(getString(R.string.logged) + " "
						+ mHoursLogged + "h");
			}
		};
	}

	/**
	 * Callback function when receiving the response from the WS.
	 * 
	 * @param dailyProgressModel
	 *            Model to fill the view with the logged progress
	 */
	public void callBackFromWS(Object dailyProgressModel) {
		// TODO: update the fields, but not UI components

		handler.sendEmptyMessage(0);
	}

	/********** SAVE THE HOURS IN PPM LOGIC ***********/

	private OnClickListener saveDayListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO: call WS to save the hours logged for this day in PPM
				// ..

				// TODO: return to the week view the hours logged on this day
				Bundle bundle = new Bundle();
				Random randomGenerator = new Random();
				bundle.putFloat(HOURS_LOGGED, 3.0f);
				bundle.putInt(PROGRESS_LOGGED, randomGenerator.nextInt(100));
				Intent i = new Intent();
				i.putExtras(bundle);
				setResult(RESULT_OK, i);
				finish();
			}
		};
	}

	/********** END SAVE THE HOURS IN PPM LOGIC ***********/
}
