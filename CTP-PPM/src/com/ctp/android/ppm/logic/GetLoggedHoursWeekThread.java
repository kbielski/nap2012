package com.ctp.android.ppm.logic;

import java.util.Calendar;
import java.util.List;

import org.springframework.web.client.RestClientException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.ctp.android.ppm.activities.WeekViewActivity;
import com.ctp.android.ppm.model.JsonHoursModel;
import com.ctp.android.ppm.model.SharedModel;
import com.ctp.android.ppm.model.WeekProgressModel;
import com.ctp.android.ppm.utils.ModelAssembler;

/**
 * 
 * Thread getting the data for the week view. Marshaling the data from the WS
 * response to the internal model and returning it with a callback to the weekly
 * activity.
 * 
 * @author kbiels
 * 
 */
public class GetLoggedHoursWeekThread extends Thread {

	private int mWeek;
	private int mYear;
	private ProgressDialog mDialog;
	private Activity mActivity;
	private boolean isCacheable;
	private boolean updateUI;

	public GetLoggedHoursWeekThread(int week, int year, ProgressDialog dialog,
			Activity activity, boolean isCacheable, boolean updateUI) {
		this.mWeek = week;
		this.mYear = year;
		this.mDialog = dialog;
		this.mActivity = activity;
		this.isCacheable = isCacheable;
		this.updateUI = updateUI;
	}

	@Override
	public void run() {

		WeekObject weekObject = calculateDates(mWeek, mYear);
		WeekProgressModel weekProgressModel = null;
		// close the login progress dialog
		if(mDialog != null) {
			mDialog.dismiss();	
		}
		
		if(isCacheable == true && SharedModel.getInstance().getCacheMap() != null) {
			weekProgressModel = SharedModel.getInstance().getCacheMap().get(mWeek);
		}
		 
		if(weekProgressModel == null) {
			RestClient restClient = new RestClient(mActivity);
			List<JsonHoursModel> hoursList = null;
			try {
				hoursList = restClient.hoursRange(
						weekObject.mondayCalendar.getTime(),
						weekObject.sundayCalendar.getTime());
				Log.i(getName(), "Received the list of logged hours for the week " + mWeek 
						+" of size = " + hoursList.size());
			} catch (RestClientException ex) {
				showErrorMessage();
				Log.e(this.getName(), "FAILED getting the hours range: " + ex.toString());
				return;
			}
			weekProgressModel = ModelAssembler
					.toWeekProgressModel(hoursList, weekObject.weekNumber, weekObject.year,
							weekObject.mondayCalendar, mActivity);
			
			SharedModel.getInstance().getCacheMap().put(mWeek, weekProgressModel);
		} 
		else {
			Log.i(getName(), "Using cache for the week "
					+ weekProgressModel.getWeekId());
		}

		if (mActivity != null && mActivity instanceof WeekViewActivity
				&& updateUI == true) {
			((WeekViewActivity) mActivity)
					.updateWeeklyHoursLogged(weekProgressModel);
		}
	}

	/**
	 * Calling a method from the activity to show a Toast with the error
	 * message.
	 */
	private void showErrorMessage() {
		if (mActivity != null && mActivity instanceof WeekViewActivity) {
			((WeekViewActivity) mActivity)
					.displayErrorMsg("Error in the HTTP connection. Please try again.");
		}
	}

	/**
	 * Calculating the calendars for the given week.
	 * @param weekArg week number in the year
	 * @param yearArg
	 * @return WeekObject containing the data for the given week
	 */
	private WeekObject calculateDates(int weekArg, int yearArg) {
		WeekObject weekObject = new WeekObject();
		weekObject.mondayCalendar = Calendar.getInstance();
		weekObject.sundayCalendar = Calendar.getInstance();

		weekObject.sundayCalendar.clear();
		weekObject.weekNumber = weekArg;
		weekObject.year = yearArg;
		weekObject.mondayCalendar.clear();
		if (weekObject.weekNumber == 0) {
			weekObject.year--;
			weekObject.mondayCalendar.set(Calendar.YEAR, weekObject.year);
			weekObject.weekNumber = weekObject.mondayCalendar
					.getMaximum(Calendar.WEEK_OF_YEAR);
		} else if (weekObject.weekNumber == weekObject.mondayCalendar
				.getMaximum(Calendar.WEEK_OF_YEAR)) {
			weekObject.year++;
			weekObject.weekNumber = 1;
		}
		weekObject.mondayCalendar.set(Calendar.YEAR, weekObject.year);
		weekObject.mondayCalendar.set(Calendar.WEEK_OF_YEAR, weekObject.weekNumber);
		weekObject.mondayCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		weekObject.sundayCalendar.set(Calendar.YEAR, weekObject.year);
		weekObject.sundayCalendar.set(Calendar.WEEK_OF_YEAR, weekObject.weekNumber);
		weekObject.sundayCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		weekObject.sundayCalendar.add(Calendar.DATE, 6);
		return weekObject;
	}

	/**
	 * Helper class to calculate the weekly dates.
	 */
	private class WeekObject {
		public Calendar mondayCalendar;
		public Calendar sundayCalendar;
		public int weekNumber;
		public int year;
	}
}
