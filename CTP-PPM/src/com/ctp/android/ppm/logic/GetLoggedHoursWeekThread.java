package com.ctp.android.ppm.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.activities.WeekViewActivity;
import com.ctp.android.ppm.model.DayProgressModel;
import com.ctp.android.ppm.model.WeekProgressModel;

public class GetLoggedHoursWeekThread extends Thread {

	private int mWeek;
	private int mYear;
	private ProgressDialog mDialog;
	private Activity mActivity;

	public GetLoggedHoursWeekThread(int week, int year, ProgressDialog dialog,
			Activity activity) {
		this.mWeek = week;
		this.mYear = year;
		this.mDialog = dialog;
		this.mActivity = activity;
	}

	@Override
	public void run() {

		// TODO:
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {

		}

		// close the login progress dialog
		mDialog.dismiss();
		
		Calendar cal = Calendar.getInstance();
		int weekNumber = mWeek;
		int year = mYear;
		cal.clear();
		if(weekNumber == 0) {
			year--;
			cal.set(Calendar.YEAR, year);
			weekNumber = cal.getMaximum(Calendar.WEEK_OF_YEAR);
		} else if(weekNumber == cal.getMaximum(Calendar.WEEK_OF_YEAR)) {
			year++;
			weekNumber = 1;
		}
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.WEEK_OF_YEAR, weekNumber);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		DateFormat dfMonth = new SimpleDateFormat("MMM yyyy");
		
		//TODO: delete the hardcode
		//TODO: call the WS to get the data
		Random rnd = new Random();
		double logged = rnd.nextInt(42);
		double notLogged = 42.5 - logged;
		
		WeekProgressModel weekProgressModel = new WeekProgressModel();
		weekProgressModel.setWeekId(weekNumber);
		weekProgressModel.setYear(year);
		weekProgressModel.setLabel("Week " + weekNumber + ", " + dfMonth.format(cal.getTime()));
		weekProgressModel.setLoggedHoursAmmount(logged);
		weekProgressModel.setNotLoggedHoursAmmount(notLogged);
		List<DayProgressModel> dayList = new ArrayList<DayProgressModel>();
		
		DayProgressModel dayProgressModel = new DayProgressModel();
		dayProgressModel.setDayId(R.id.btnMonday);
		dayProgressModel.setDayOfTheYear(cal.get(Calendar.DAY_OF_YEAR));
		dayProgressModel.setLabel(mActivity.getString(R.string.monday) + ", " + df.format(cal.getTime()));
		cal.add(Calendar.DATE, 1);
		dayProgressModel.setProgress(95);
		dayList.add(dayProgressModel);
		
		DayProgressModel dayProgressModel2 = new DayProgressModel();
		dayProgressModel2.setDayId(R.id.btnTuesday);
		dayProgressModel2.setDayOfTheYear(cal.get(Calendar.DAY_OF_YEAR));
		dayProgressModel2.setLabel(mActivity.getString(R.string.tuesday) + ", " + df.format(cal.getTime()));
		cal.add(Calendar.DATE, 1);
		dayProgressModel2.setProgress(55);
		dayList.add(dayProgressModel2);
		
		DayProgressModel dayProgressModel3 = new DayProgressModel();
		dayProgressModel3.setDayId(R.id.btnWednesday);
		dayProgressModel3.setDayOfTheYear(cal.get(Calendar.DAY_OF_YEAR));
		dayProgressModel3.setLabel(mActivity.getString(R.string.wednesday) + ", " + df.format(cal.getTime()));
		cal.add(Calendar.DATE, 1);
		dayProgressModel3.setProgress(100);
		dayList.add(dayProgressModel3);
		
		DayProgressModel dayProgressModel4 = new DayProgressModel();
		dayProgressModel4.setDayId(R.id.btnThursday);
		dayProgressModel4.setDayOfTheYear(cal.get(Calendar.DAY_OF_YEAR));
		dayProgressModel4.setLabel(mActivity.getString(R.string.thursday) + ", " + df.format(cal.getTime()));
		cal.add(Calendar.DATE, 1);
		dayProgressModel4.setProgress(90);
		dayList.add(dayProgressModel4);
		
		DayProgressModel dayProgressModel5 = new DayProgressModel();
		dayProgressModel5.setDayId(R.id.btnFriday);
		dayProgressModel5.setDayOfTheYear(cal.get(Calendar.DAY_OF_YEAR));
		dayProgressModel5.setLabel(mActivity.getString(R.string.friday) + ", " + df.format(cal.getTime()));
		cal.add(Calendar.DATE, 1);
		dayProgressModel5.setProgress(0);
		dayList.add(dayProgressModel5);
		
		DayProgressModel dayProgressModel6 = new DayProgressModel();
		dayProgressModel6.setDayId(R.id.btnSaturday);
		dayProgressModel6.setDayOfTheYear(cal.get(Calendar.DAY_OF_YEAR));
		dayProgressModel6.setLabel(mActivity.getString(R.string.saturday) + ", " + df.format(cal.getTime()));
		cal.add(Calendar.DATE, 1);
		dayProgressModel6.setProgress(0);
		dayList.add(dayProgressModel6);
		
		DayProgressModel dayProgressModel7 = new DayProgressModel();
		dayProgressModel7.setDayId(R.id.btnSunday);
		dayProgressModel7.setDayOfTheYear(cal.get(Calendar.DAY_OF_YEAR));
		dayProgressModel7.setLabel(mActivity.getString(R.string.sunday) + ", " + df.format(cal.getTime()));
		dayProgressModel7.setProgress(0);
		dayList.add(dayProgressModel7);
		
		weekProgressModel.setDayList(dayList);
		
		WeekViewActivity weekActivity = (WeekViewActivity) mActivity;
		weekActivity.returnWeeklyHoursLoggedResponse(weekProgressModel);
	}
}
