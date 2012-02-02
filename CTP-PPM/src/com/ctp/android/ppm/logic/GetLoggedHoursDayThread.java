package com.ctp.android.ppm.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.springframework.web.client.RestClientException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.activities.DailyViewActivity;
import com.ctp.android.ppm.model.JsonHoursModel;
import com.ctp.android.ppm.model.JsonProject;
import com.ctp.android.ppm.model.ProjectModel;
import com.ctp.android.ppm.utils.ModelAssembler;

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

		// close the login progress dialog
		mProgressDialog.dismiss();

		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.YEAR, mYear);
		cal.set(Calendar.DAY_OF_YEAR, mDayOfYear);
		DateFormat df = new SimpleDateFormat(mActivity.getString(R.string.PpmDateFormat));
		String jsonDate = df.format(cal.getTime());
		
		RestClient restClient = new RestClient(mActivity);
		//call the MyProject to get the project list
		List<JsonProject> myProjects = restClient.myProjects();
		List<JsonHoursModel> hoursList = null;
		//call the /HoursRange to get the hours logged for the projects for the selected day
		try {
			hoursList = restClient.hoursRange(cal.getTime(), cal.getTime());
			Log.i(getName(), "Received the list of logged hours for the day of size = " + hoursList.size());
		} catch(RestClientException ex) {
			showErrorMessage();
			Log.e(this.getName(), "FAILED getting the hours range: " + ex.toString());
			return;
		}
		
		List<ProjectModel> projectList = ModelAssembler.toDailyProjectList(myProjects, hoursList, jsonDate);
		if (mActivity != null && mActivity instanceof DailyViewActivity) {
			((DailyViewActivity) mActivity).callBackFromWS(projectList);
		}
	}

	/**
	 * Calling a method from the activity to show a Toast
	 * with the error message.
	 */
	private void showErrorMessage() {
		if (mActivity != null && mActivity instanceof DailyViewActivity) {
			((DailyViewActivity) mActivity).displayErrorMsg("Error in the HTTP connection. Please try again.");
		}
	}
}
