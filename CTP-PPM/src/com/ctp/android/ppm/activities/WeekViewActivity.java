package com.ctp.android.ppm.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.components.DayLayout;
import com.ctp.android.ppm.logic.GetLoggedHoursWeekThread;
import com.ctp.android.ppm.logic.IWSCallback;
import com.ctp.android.ppm.logic.SubmitWeekThread;
import com.ctp.android.ppm.model.DayProgressModel;
import com.ctp.android.ppm.model.SharedModel;
import com.ctp.android.ppm.model.WeekProgressModel;
import com.ctp.android.ppm.utils.CommonUtils;
import com.ctp.android.ppm.utils.ErrorPPMHandler;
import com.ctp.android.ppm.utils.ModelAssembler;
import com.ctp.android.ppm.utils.SharedOptionsMenu;

/**
 * 
 * The weekly activity containing an overview of the selected week. Each
 * day has a progress bar representing the status of logged hours to 
 * the required daily base. 
 * 
 * The whole week can be submitted from this view. The arrows and swipe
 * gesture allows navigation between different weeks. Click the day to
 * open the daily activity and have the possibility to log the hours.
 * 
 * Options menu is allowing to navigate to the selected day, copying the
 * current week to the memory or logging out of the the application.
 * 
 * Context menu on the day components is allowing to copy the selected
 * day and paste it's data into another one.
 * 
 * @author kbiels
 *
 */
public class WeekViewActivity extends Activity implements IWSCallback {

	/** Label including the number of the week and the year **/
	private TextView mLblWeek;
	
	/** Label including the amount of hours logged for this week **/
	private TextView mLblHoursLogged;
	
	/** Label including the amount of hours not logged for this week **/
	private TextView mLblHoursNotLogged;
	
	/** Icon - when clicked navigating to the previous week **/
	private ImageView mImgPreviousWeek;
	
	/** Icon - when clicked navigating to the next week **/
	private ImageView mImgNextWeek;
	
	/** Button used to submit the current week **/
	private Button mBtnSubmit;
	
	/** List of UI components containing the progress bar for a day **/
	private List<DayLayout> mWeekLayoutList;
	
	/** Internal model - list of day for this week with the projects **/
	private WeekProgressModel mWeekProgressModel;
	
	/** Detects if a horizontal swipe was made - navigating between weeks **/
	private GestureDetector mGestureDetector;
	
	/** Handler to update the UI when the response from WS arrives **/ 
	private Handler handler;
	
	/** Handler to show a message when an HTTP error occurs **/
	private Handler mErrorHandler;
	
	private int mWeekNumber;
	private int mYear;
	private static final String WEEK_NUMBER = "weekNumber";
	private static final String YEAR = "year";
	private static final int SWIPE_MIN_DISTANCE = 80;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 80;
	public static final int COPY_WEEK = 101;
	public static final int PASTE_WEEK = 102;
	public static final int SELECT_DAY = 103;
	public static final int VIBRATE_DURATION = 100;

	/**
	 * onCreate - called when the activity is first created.
	 * 
	 * Initialization of the member fields, getting the date from the
	 * Intent or calculating the current week.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.week_view);

		handler = new UpdatingUIHandler();
		mErrorHandler = new ErrorPPMHandler(this);

		mLblWeek = (TextView) findViewById(R.id.txtWeekLabel);
		mLblHoursLogged = (TextView) findViewById(R.id.txtLoggedHours);
		mLblHoursNotLogged = (TextView) findViewById(R.id.txtNotLoggedHours);
		mImgPreviousWeek = (ImageView) findViewById(R.id.imgPreviousWeek);
		mImgPreviousWeek.setOnClickListener(prevWeekListener());
		mImgNextWeek = (ImageView) findViewById(R.id.imgNextWeek);
		mImgNextWeek.setOnClickListener(nextWeekListener());
		mBtnSubmit = (Button) findViewById(R.id.btnSubmit);
		mBtnSubmit.setOnClickListener(clickBtnSubmitListener());

		mWeekLayoutList = new ArrayList<DayLayout>();
		mWeekLayoutList.add((DayLayout) findViewById(R.id.btnMonday));
		mWeekLayoutList.add((DayLayout) findViewById(R.id.btnTuesday));
		mWeekLayoutList.add((DayLayout) findViewById(R.id.btnWednesday));
		mWeekLayoutList.add((DayLayout) findViewById(R.id.btnThursday));
		mWeekLayoutList.add((DayLayout) findViewById(R.id.btnFriday));
		mWeekLayoutList.add((DayLayout) findViewById(R.id.btnSaturday));
		mWeekLayoutList.add((DayLayout) findViewById(R.id.btnSunday));

		mGestureDetector = new GestureDetector(new MyGestureDetector());

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mWeekNumber = extras.getInt(WEEK_NUMBER);
			mYear = extras.getInt(YEAR);
		} else {
			// TODO: hardcode to match the data from PPM webservice
//			mWeekNumber = 39;
//			mYear = 2011;

			// GOOD SOLUTION - UNCOMMENT
			 Calendar cal = Calendar.getInstance();
			 mWeekNumber = cal.get(Calendar.WEEK_OF_YEAR);
			 mYear = cal.get(Calendar.YEAR);
		}

		getThisWeekProgressFromWS(true, true);
	}
	
	/**
	 * Using the Shared Options Menu, also adding options to
	 * copy the week and select a day from the DatePicker.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		SharedOptionsMenu.onCreateOptionsMenu(menu, menuInflater);

		// add the copying week functionality
		MenuItem copyItem = menu.add(0, COPY_WEEK, 0,
				getString(R.string.copy_week));
		copyItem.setIcon(R.drawable.copy);

		// add the select DatePicker dialog
		MenuItem selectDay = menu.add(0, SELECT_DAY, 0,
				getString(R.string.select_day));
		selectDay.setIcon(R.drawable.day);
		return true;
	}

	/**
	 * This is called right before the menu is shown. Adding or removing the 
	 * paste week option depending if there is a week model in the shared model.
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// add the paste week functionality if a week has been copied already
		if (menu.findItem(PASTE_WEEK) != null
				&& SharedModel.getInstance().getCopiedWeek() == null) {
			menu.removeItem(PASTE_WEEK);
		} else if (menu.findItem(PASTE_WEEK) == null
				&& SharedModel.getInstance().getCopiedWeek() != null) {
			MenuItem pasteItem = menu.add(0, PASTE_WEEK, 0,
					getString(R.string.paste_week));
			pasteItem.setIcon(R.drawable.paste);
		}
		return true;
	}

	/**
	 * Options menu on the week view has the shared options menu and the copy
	 * and paste functionality.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (SharedOptionsMenu.onOptionsItemSelected(item, this)) {
			return true;
		} else if (item.getItemId() == COPY_WEEK) {
			SharedModel sharedModel = SharedModel.getInstance();
			sharedModel.setCopiedWeek(mWeekProgressModel);
			return true;
		} else if (item.getItemId() == PASTE_WEEK) {
			Toast.makeText(this, getString(R.string.week_pasted), Toast.LENGTH_LONG).show();
			Log.i(this.toString(), "PASTING THE WEEK");
			WeekProgressModel copiedWeek = ModelAssembler.copyOtherWeek(
					mWeekProgressModel, SharedModel.getInstance()
							.getCopiedWeek());
			updateWeeklyHoursLogged(copiedWeek);
			SharedModel.getInstance().setCopiedWeek(null);
			return true;
		} else if (item.getItemId() == SELECT_DAY) {
			openDialogForDaySelection();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/************* PREV & NEXT WEEK FUNCTIONALITY *************/
	
	/**
	 * Listener for the Icon for navigation to the previous week.
	 * @return OnClickListener
	 */
	private OnClickListener prevWeekListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToPreviousWeek();
			}
		};
	}

	/**
	 * Listener for the Icon for navigation to the previous week.
	 * @return OnClickListener
	 */
	private OnClickListener nextWeekListener() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToNextWeek();
			}
		};
	}
	
	/**
	 * Starts a new activity with the previous week.
	 */
	private void goToPreviousWeek() {
		showGivenWeek(mWeekNumber - 1, mYear);
	}

	/**
	 * Starts a new activity with the next week.
	 */
	private void goToNextWeek() {
		showGivenWeek(mWeekNumber + 1, mYear);
	}
	
	/**
	 * Starts a new activity for a given week and year.
	 * @param weekOfTheYear 
	 * @param year
	 */
	private void showGivenWeek(int weekOfTheYear, int year) {
		Intent intent = new Intent(this, WeekViewActivity.class);
		intent.putExtra(WEEK_NUMBER, weekOfTheYear);
		intent.putExtra(YEAR, year);
		startActivity(intent);
	}

	/************* END PREV & NEXT WEEK FUNCTIONALITY *************/

	/************* SELECTING A DAY FUNCTIONALITY *************/
	
	/**
	 * Listener for the day UI component, starting a new activity 
	 * for the selected day.
	 *
	 * @return OnClickListener
	 */
	private View.OnClickListener getDaySelectionListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int dayId = ((DayLayout) v).getDayId();
				int dayOfTheYear = ((DayLayout) v).getDayOfTheYear();
				goToDailyView(dayId, dayOfTheYear, mYear);
			}
		};
	}

	/**
	 * Starts a new activity to show the daily view.
	 * 
	 * @param dayId unique identifier of the day, used afterwards to identify
	 *            which field to update
	 * @param dayOfTheYear 
	 * @param year 
	 * 
	 */
	protected void goToDailyView(int dayId, int dayOfTheYear, int year) {
		Intent intent = new Intent(this, DailyViewActivity.class);
		intent.putExtra(DailyViewActivity.DAY, dayOfTheYear);
		intent.putExtra(DailyViewActivity.YEAR, year);
		startActivityForResult(intent, dayId);
	}

	/**
	 * Called when a daily activity is finished. Includes two use cases:
	 * 
	 * 		- when returning from a Selected Day (OptionsMenu) then need to start 
	 * 	 	  a new activity with the week corresponding for the selected day
	 * 
	 * 		- when returning from the standard day view then need to update
	 * 		  the progress logged in the selected day
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (intent == null) {
			return;
		}
		Bundle extras = intent.getExtras();
		//when selected a day from the DatePicker widget
		//and should refresh the view to the selected week
		if(requestCode == SELECT_DAY) {
			int dayOfTheYear = extras.getInt(DailyViewActivity.DAY);
			int year = extras.getInt(DailyViewActivity.YEAR);
			Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.DAY_OF_YEAR, dayOfTheYear);
			showGivenWeek(cal.get(Calendar.WEEK_OF_YEAR), year);
		} 
		//when saved a day in the daily view and should update 
		//the view with the logged progress
		else {
			DayLayout layout = null;
			int progressLogged = extras.getInt(DailyViewActivity.PROGRESS_LOGGED);

			getThisWeekProgressFromWS(false, false);
			// requestCode is the dayId, which identifies the selected day
			// it should be reduced by 1, because the list elements starts from 0
			layout = mWeekLayoutList.get(requestCode - 1);
			if (layout != null) {
				layout.setProgress(progressLogged);
			}
		}
	}

	/************* END SELECTING A DAY FUNCTIONALITY *************/

	/************** SUBMITING A WEEK *********************/
	
	/**
	 * Listener for the Submit button. Calls the WS to submit the current week.
	 * @return OnClickListener
	 */
	private OnClickListener clickBtnSubmitListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				callWebserviceForSubmitting();
			}
		};
	}

	/**
	 * Calls a web service to submit the current week to the server.
	 * Opens a progress dialog while performing the operation.
	 */
	private void callWebserviceForSubmitting() {
		ProgressDialog progressDialog = ProgressDialog.show(
				WeekViewActivity.this, getString(R.string.please_wait),
				getString(R.string.submitting_hours), true, true, null);

		SubmitWeekThread thread = new SubmitWeekThread(this, progressDialog,
				 mWeekProgressModel);
		thread.start();
	}
	
	public void onSubmittedComplete() {
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(VIBRATE_DURATION);
	}

	/************** END SUBMITING A WEEK *********************/

	public int getWeekNumber() {
		return mWeekNumber;
	}

	public void setWeekNumber(int weekNumber) {
		this.mWeekNumber = weekNumber;
	}

	/******************** SWIPE **************************/

	/**
	 * Gesture detector used to detect if a swipe was made. Detects the
	 * horizontal swipes and changes the view to the next or previous week.
	 */
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					goToNextWeek();

				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					goToPreviousWeek();
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}

	/**
	 * On touch event detection to lunch the swipe functionality.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return true;
		else
			return false;
	}

	/******************** END SWIPE **************************/

	/******************** SELECTING A SPECIFIC DAY **************/

	/**
	 * Opens a dialog to select the specific day in the DatePicker widget.
	 */
	private void openDialogForDaySelection() {

		// the callback received when the user "sets" the date in the dialog
		DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Log.i(getClass().getName(), "DatePicker selected: " + year
						+ "-" + monthOfYear + "-" + dayOfMonth);
				showDayView(year, monthOfYear, dayOfMonth);
			}
		};
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		Dialog dialog = new DatePickerDialog(this, mDateSetListener, year,
				month, day);
		dialog.show();
	}

	/**
	 * Display the DailyViewActivity for the selected date in the DatePicker
	 * widget.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	private void showDayView(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		int dayOfTheYear = cal.get(Calendar.DAY_OF_YEAR);

		Intent intent = new Intent(this, DailyViewActivity.class);
		intent.putExtra(DailyViewActivity.DAY, dayOfTheYear);
		intent.putExtra(DailyViewActivity.YEAR, year);
		startActivityForResult(intent, SELECT_DAY);
	}

	/******************** END SELECTING A SPECIFIC DAY **************/

	/************** GET DATA FROM WS FOR THIS WEEK ******************/

	/**
	 * Call WS to get hours logged for this week.
	 */
	private void getThisWeekProgressFromWS(boolean isCacheable, boolean downloadOtherWeeks) {
		ProgressDialog progressDialog = ProgressDialog.show(
				WeekViewActivity.this, getString(R.string.please_wait),
				getString(R.string.loading_hours_for_this_week), true, true,
				null);

		GetLoggedHoursWeekThread thread = new GetLoggedHoursWeekThread(
				mWeekNumber, mYear, progressDialog, this, isCacheable, true);
		thread.start();
		
		//download other weeks to optimize the performance
		if(downloadOtherWeeks == true) {
			GetLoggedHoursWeekThread t1 = new GetLoggedHoursWeekThread(
					mWeekNumber - 1, mYear, null, this, true, false);
			t1.start();
			
			GetLoggedHoursWeekThread t2 = new GetLoggedHoursWeekThread(
					mWeekNumber + 1, mYear, null, this, true, false);
			t2.start();
		}
	}

	
	/**
	 * Displaying an error to the user from a background
	 * running thread.
	 */
	public void displayErrorMsg(String errorMsg) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString(CommonUtils.ERROR_MSG, errorMsg);
		msg.setData(data);
		mErrorHandler.sendMessage(msg);
	}
	
	/**
	 * Method receiving the weekly data model to display, Using a handler to
	 * update the UI.
	 * 
	 * @param weekProgressModel
	 *            the weekly progress model contain all the data to show the
	 *            progress
	 */
	@Override
	public void updateWeeklyHoursLogged(WeekProgressModel weekProgressModel) {
		mWeekProgressModel = weekProgressModel;
		handler.sendEmptyMessage(0);
	}
	
	
	/**
	 * Custom handler updating the UI with the results from WS.
	 */
	private class UpdatingUIHandler extends Handler {
		public void handleMessage(Message msg) {
			DayLayout layout;
			mWeekNumber = mWeekProgressModel.getWeekId();
			mYear = mWeekProgressModel.getYear();
			mLblWeek.setText(mWeekProgressModel.getLabel());
			mLblHoursLogged.setText(getString(R.string.logged) + " "
					+ mWeekProgressModel.getLoggedHoursAmmount());
			mLblHoursNotLogged.setText(getString(R.string.not_logged) + " "
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
				layout.setmDayProgressModel(dayModel);
			}
		}
	}
	/************** END GET DATA FROM WS FOR THIS WEEK ******************/
}
