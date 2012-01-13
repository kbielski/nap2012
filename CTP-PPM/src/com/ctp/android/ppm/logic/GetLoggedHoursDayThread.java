package com.ctp.android.ppm.logic;

import com.ctp.android.ppm.activities.DailyViewActivity;
import com.ctp.android.ppm.activities.WeekViewActivity;

import android.app.Activity;
import android.app.ProgressDialog;

public class GetLoggedHoursDayThread extends Thread {

	private int mDayOfYear;
	private int mYear;
	private Activity mActivity;
	private ProgressDialog mProgressDialog;

	public GetLoggedHoursDayThread(int dayOfYear, int year, Activity activity,
			ProgressDialog pDialog) {
		this.mDayOfYear = dayOfYear;
		this.mYear = year;
		this.mActivity = activity;
		this.mProgressDialog = pDialog;
	}

	@Override
	public void run() {

		// TODO:
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {

		}

		// close the login progress dialog
		mProgressDialog.dismiss();
		
		//TODO: mock the WS getting the list of projects with the logged hours
		//..
		
		Object dailyProgressModel = new Object();
		
		DailyViewActivity activity = (DailyViewActivity) mActivity;
		activity.callBackFromWS(dailyProgressModel);
	}
}
