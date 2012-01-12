package com.ctp.android.ppm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctp.android.ppm.components.DayLayout;
import com.ctp.android.ppm.logic.CommonUtils;
import com.ctp.android.ppm.logic.IWSCallback;
import com.ctp.android.ppm.logic.GetLoggedHoursThreadMock;
import com.ctp.android.ppm.model.DayProgressModel;
import com.ctp.android.ppm.model.WeekProgressModel;

public class WeekViewActivity extends Activity implements IWSCallback {

	private int mWeekNumber;
	private int mYear;
	private TextView lblWeek;
	private TextView lblHoursLogged;
	private TextView lblHoursNotLogged;
	private ImageView imgPreviousWeek;
	private ImageView imgNextWeek;
	private List<DayLayout> mWeekLayoutList;
	private WeekProgressModel mWeekProgressModel;
	private static final String WEEK_NUMBER = "weekNumber";
	private static final String YEAR = "year";
	private static final int DAY_HOURS_LOGGED = 0;
	
	//handler to update the UI when the response from WS arrives
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			DayLayout layout;
			mWeekNumber = mWeekProgressModel.getWeekId();
			mYear = mWeekProgressModel.getYear();
			lblWeek.setText(mWeekProgressModel.getLabel());
			lblHoursLogged.setText(getString(R.string.logged) + " "
					+ mWeekProgressModel.getLoggedHoursAmmount());
			lblHoursNotLogged.setText(getString(R.string.not_logged) + " "
					+ mWeekProgressModel.getNotLoggedHoursAmmount());
			List<DayProgressModel> dayList = mWeekProgressModel.getDayList();
			for (int index = 0; index < dayList.size(); index++) {
				layout = mWeekLayoutList.get(index);
				DayProgressModel dayModel = dayList.get(index);
				layout.setOnClickListener(getDaySelectionListener());
				layout.setLabel(dayModel.getLabel());
				layout.setProgress(dayModel.getProgress());
				layout.setDayId(dayModel.getDayId());
				layout.setDayOfTheYear(dayModel.getDayOfTheYear());
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.week_view);

		lblWeek = (TextView) findViewById(R.id.txtWeekLabel);
		lblHoursLogged = (TextView) findViewById(R.id.txtLoggedHours);
		lblHoursNotLogged = (TextView) findViewById(R.id.txtNotLoggedHours);
		imgPreviousWeek = (ImageView) findViewById(R.id.imgPreviousWeek);
		imgPreviousWeek.setOnClickListener(prevWeekListener());
		imgNextWeek = (ImageView) findViewById(R.id.imgNextWeek);
		imgNextWeek.setOnClickListener(nextWeekListener());
		
		mWeekLayoutList = new ArrayList<DayLayout>();
		mWeekLayoutList.add((DayLayout) findViewById(R.id.btnMonday));
		mWeekLayoutList.add((DayLayout) findViewById(R.id.btnTuesday));
		mWeekLayoutList.add((DayLayout) findViewById(R.id.btnWednesday));
		mWeekLayoutList.add((DayLayout) findViewById(R.id.btnThursday));
		mWeekLayoutList.add((DayLayout) findViewById(R.id.btnFriday));
		mWeekLayoutList.add((DayLayout) findViewById(R.id.btnSaturday));
		mWeekLayoutList.add((DayLayout) findViewById(R.id.btnSunday));

		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			mWeekNumber = extras.getInt(WEEK_NUMBER);
			mYear = extras.getInt(YEAR);
		}
		else {
			Calendar cal = Calendar.getInstance();
			mWeekNumber = cal.get(Calendar.WEEK_OF_YEAR);
			mYear = cal.get(Calendar.YEAR);
		}
		
		getThisWeekProgressFromWS();
	}

	// call WS to get hours logged for this week
	private void getThisWeekProgressFromWS() {
		ProgressDialog progressDialog = ProgressDialog.show(
				WeekViewActivity.this, getString(R.string.please_wait),
				getString(R.string.loading_hours_for_this_week), true, true,
				null);

		GetLoggedHoursThreadMock thread = new GetLoggedHoursThreadMock(mWeekNumber, mYear,
				progressDialog, this);
		thread.start();
	}

	/**
	 * Method receiving the answers from the WS, using
	 * a handler to update the UI.
	 */
	@Override
	public void returnWeeklyHoursLoggedResponse(
			WeekProgressModel weekProgressModel) {
		mWeekProgressModel = weekProgressModel;
		handler.sendEmptyMessage(0);
	}

	/************* PREV & NEXT WEEK FUNCTIONALITY *************/
	private OnClickListener prevWeekListener() {
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goToPreviousWeek();
			}
		};
	}
	
	private void goToPreviousWeek() {
		Intent intent = new Intent(this, WeekViewActivity.class);
		intent.putExtra(WEEK_NUMBER, mWeekNumber - 1);
		intent.putExtra(YEAR, mYear);
		startActivity(intent);
	}
	
	private OnClickListener nextWeekListener() {
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goToNextWeek();
			}
		};
	}
	
	private void goToNextWeek() {
		Intent intent = new Intent(this, WeekViewActivity.class);
		intent.putExtra(WEEK_NUMBER, mWeekNumber + 1);
		intent.putExtra(YEAR, mYear);
		startActivity(intent);
	}
	/************* END PREV & NEXT WEEK FUNCTIONALITY *************/

	
	/************* SELECTING A DAY FUNCTIONALITY *************/
	private View.OnClickListener getDaySelectionListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int dayId = ((DayLayout) v).getDayId();
				int dayOfTheYear = ((DayLayout) v).getDayOfTheYear();
				goToDailyView(dayId, dayOfTheYear);
			}
		};
	}

	protected void goToDailyView(int dayId, int dayOfTheYear) {
		Intent intent = new Intent(this, DailyViewActivity.class);
		intent.putExtra(DailyViewActivity.DAY, dayOfTheYear);
		intent.putExtra(DailyViewActivity.YEAR, mYear);
		startActivityForResult(intent, dayId);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if(intent == null) {
			return;
		}
		Bundle extras = intent.getExtras();
		DayLayout layout = null;
		int progressLogged = extras.getInt(DailyViewActivity.PROGRESS_LOGGED);;
		switch (requestCode) {
		case R.id.btnMonday:
			layout = mWeekLayoutList.get(CommonUtils.MONDAY);
			break;
		case R.id.btnTuesday:
			layout = mWeekLayoutList.get(CommonUtils.TUESDAY);
			break;
		case R.id.btnWednesday:
			layout = mWeekLayoutList.get(CommonUtils.WEDNESDAY);
			break;
		case R.id.btnThursday:
			layout = mWeekLayoutList.get(CommonUtils.THURSDAY);
			break;
		case R.id.btnFriday:
			layout = mWeekLayoutList.get(CommonUtils.FRIDAY);
			break;
		case R.id.btnSaturday:
			layout = mWeekLayoutList.get(CommonUtils.SATURDAY);
			break;
		case R.id.btnSunday:
			layout = mWeekLayoutList.get(CommonUtils.SUNDAY);
			break;
		}
		if(layout != null) {
			layout.setProgress(progressLogged);	
		}
	}
	/************* END SELECTING A DAY FUNCTIONALITY *************/
	
	public int getWeekNumber() {
		return mWeekNumber;
	}

	public void setWeekNumber(int weekNumber) {
		this.mWeekNumber = weekNumber;
	}
}
