package com.ctp.android.ppm.logic;

import android.app.Activity;
import android.app.ProgressDialog;

public class SubmitWeekThread extends Thread {

	private int mWeek;
	private int mYear;
	private Activity mActivity;
	private ProgressDialog mDialog;

	public SubmitWeekThread(int week, int year, Activity activity,
			ProgressDialog dialog) {
		this.mWeek = week;
		this.mYear = year;
		this.mActivity = activity;
		this.mDialog = dialog;
	}

	@Override
	public void run() {

		//TODO:
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {

		}

		// close the login progress dialog
		mDialog.dismiss();
		
		//TODO: call the WS to submit this week
		//..
	}
}
