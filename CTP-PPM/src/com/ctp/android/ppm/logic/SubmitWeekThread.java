package com.ctp.android.ppm.logic;

import java.util.List;

import org.springframework.http.HttpMethod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.ctp.android.ppm.activities.WeekViewActivity;
import com.ctp.android.ppm.model.JsonHoursModel;
import com.ctp.android.ppm.model.WeekProgressModel;
import com.ctp.android.ppm.utils.ModelAssembler;

public class SubmitWeekThread extends Thread {

	private Activity mActivity;
	private ProgressDialog mDialog;
	private WeekProgressModel weekModel;

	public SubmitWeekThread(Activity activity,
			ProgressDialog dialog, WeekProgressModel weekModel) {
		this.mActivity = activity;
		this.mDialog = dialog;
		this.weekModel = weekModel;
	}

	@Override
	public void run() {
		
		// close the login progress dialog
		mDialog.dismiss();

		RestClient restClient = new RestClient(mActivity);
		
		//One option:
		//List<JsonHoursModel> hoursList = SharedModel.getInstance().getWeeklyDataToSubmit();
		
		List<JsonHoursModel> hoursList = ModelAssembler.fromWeekProgressModel(weekModel);
		
		for(JsonHoursModel hour : hoursList) {
			hour.setSubmitted(true);
			String result = restClient.createHour(hour, HttpMethod.POST);
			Log.i(getName(), "Submitting week = " + result);
		}
		
		if(mActivity != null && mActivity instanceof WeekViewActivity) {
			((WeekViewActivity) mActivity).onSubmittedComplete();
		}
	}

}
