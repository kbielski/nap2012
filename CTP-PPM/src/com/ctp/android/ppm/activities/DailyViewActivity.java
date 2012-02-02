package com.ctp.android.ppm.activities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.components.OnChangedSliderListener;
import com.ctp.android.ppm.components.SliderLayout;
import com.ctp.android.ppm.logic.GetLoggedHoursDayThread;
import com.ctp.android.ppm.logic.SaveLoggedHoursDayThread;
import com.ctp.android.ppm.model.ProjectModel;
import com.ctp.android.ppm.model.ProjectModel.OperationFlag;
import com.ctp.android.ppm.utils.CommonUtils;
import com.ctp.android.ppm.utils.ErrorPPMHandler;
import com.ctp.android.ppm.utils.SharedOptionsMenu;


/**
 * 
 * The daily activity containing the list of projects assigned to the user. The
 * view is constructed dynamically after receiving the data from the REST web service.
 * 
 * Each project has a slider to log hours worked on this project. Each project
 * has a comment icon to include a comment for the selected project.
 * 
 * @author kbiels
 *
 */
public class DailyViewActivity extends Activity {

	/** Label including the date for this day **/
	private TextView mLblDay;
	
	/** Label including the amount of hours logged on this day **/
	private TextView mLblLoggedHours;
	
	/** Button used to save the hours logged on this day **/
	private Button btnSaveDay;
	
	/** Container layout used in the dynamic creation of the project list **/
	private LinearLayout mContainerLayout;
	
	/** List of UI slider components, which are inside the container layout **/
	private List<SliderLayout> mProjectSliderList;
	
	//	private DayProgressModel mDayProgressModel;
	//	private EnhancedDayProgressModel mEnhacedDayProgressModel;
	
	/** Internal model - list of projects for this day **/
	private List<ProjectModel> mProjectList;
	
	/** Handler to update the UI when the response comes from WS-thread **/
	private Handler mHandler;
	
	/** Handler to show a message when an HTTP error occurs **/
	private Handler mErrorHandler;
	
	private int mDayOfTheYear;
	private int mYear;
	private double mHoursLogged;
	public static final String DAY = "day";
	public static final String YEAR = "year";
	public static final String HOURS_LOGGED = "hoursLogged";
	public static final String PROGRESS_LOGGED = "progressLogged";

	/**
	 * onCreate - called when the activity is first created.
	 * 
	 * Initialization of the member fields, getting the date from the
	 * Intent or calculating the current date.
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.day_view);

		mLblDay = (TextView) findViewById(R.id.txtDayLabel);
		mLblLoggedHours = (TextView) findViewById(R.id.txtLoggedHoursDailyView);
		btnSaveDay = (Button) findViewById(R.id.btnSaveDay);
		btnSaveDay.setOnClickListener(saveDayListener());
		mContainerLayout = (LinearLayout) findViewById(R.id.containerDayView);

		mHoursLogged = 0;
		mProjectSliderList = new ArrayList<SliderLayout>();

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

		mHandler = new ConstructingViewHandler();
		mErrorHandler = new ErrorPPMHandler(this);
		
		getHoursLoggedForThisDay();
	}

	/**
	 * Creating the Options Menu - using the Shared Options Menu
	 * which has the logout option.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		SharedOptionsMenu.onCreateOptionsMenu(menu, menuInflater);
		return true;
	}

	/**
	 * Called when the item from the Options Menu is selected.
	 * Using the Shared Options Menu listener.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return SharedOptionsMenu.onOptionsItemSelected(item, this) ? true
				: super.onOptionsItemSelected(item);
	}

	/**
	 * Calling the WS to get the model for this view.
	 */
	private void getHoursLoggedForThisDay() {
		ProgressDialog progressDialog = ProgressDialog.show(
				DailyViewActivity.this, getString(R.string.please_wait),
				getString(R.string.loading_hours_for_this_day), true, true,
				null);

		GetLoggedHoursDayThread threadMock = new GetLoggedHoursDayThread(
				mDayOfTheYear, mYear, this, progressDialog);
		threadMock.start();
	}

	/**
	 * Listen to the slider change and update the total hours logged amount
	 * @return OnChangedSliderListener
	 */
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
	public void callBackFromWS(List<ProjectModel> projectList) {
		// TODO: update the fields, but not UI components
		//mDayProgressModel = dailyProgressModel;
		//mEnhacedDayProgressModel = dailyProgressModel;
		mProjectList = projectList;
		mHandler.sendEmptyMessage(0);
	}

	/********** SAVE THE HOURS IN PPM LOGIC ***********/

	/**
	 * Listener to the save button in the daily view. Calls the 
	 * WS method to save the modified day.
	 * 
	 * @return OnClickListener
	 */
	private OnClickListener saveDayListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				getChangesFromUIToModel();
				
				//Call WS to save the hours logged for this day in PPM
				SaveLoggedHoursDayThread thread = new SaveLoggedHoursDayThread(
						mProjectList, DailyViewActivity.this, null, null);
				thread.start();
			}
		};
	}
	
	/**
	 * Callback method used in the thread, which is saving the modified day.
	 * Closes the daily activity and provides a result to the weekly activity.
	 */
	public void callbackFinishDailyActivity() {
		Bundle bundle = new Bundle();
		bundle.putDouble(HOURS_LOGGED, mHoursLogged);
		int progress = (int) (mHoursLogged
				/ Double.parseDouble(getString(R.string.dailyHoursBase)) * 100);
		if (progress > 100) {
			progress = 100;
		}
		bundle.putInt(PROGRESS_LOGGED, progress);
		bundle.putInt(DAY, mDayOfTheYear);
		bundle.putInt(YEAR, mYear);
		Intent i = new Intent();
		i.putExtras(bundle);
		setResult(RESULT_OK, i);
		finish();
	}
	
	/**
	 * Propagating the changes from UI to the internal model.
	 */
	private void getChangesFromUIToModel() {
		for (SliderLayout sl : mProjectSliderList) {
			//check if creating a new entry or modifying an existing one
			//used to determine which HttpMethod to use afterwards (PUT or POST)
			ProjectModel pModel = mProjectList.get(sl.getId());
			if(pModel.getHoursLogged() == 0 && sl.getHoursLogged() > 0) {
				pModel.setOperation(OperationFlag.CREATE);
			} else if(pModel.getHoursLogged() != sl.getHoursLogged()) {
				pModel.setOperation(OperationFlag.UPDATE);
			}
			pModel.setHoursLogged(sl.getHoursLogged());
			pModel.setComment(sl.getmComment());
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
	
	/********** END SAVE THE HOURS IN PPM LOGIC ***********/
	
	/********** CONSTRUCTING THE DYNAMIC VIEW FROM THE WS RESPONSE *****/
	private class ConstructingViewHandler extends Handler {
		public void handleMessage(Message msg) {
			int sliderIndex = 0; 
			for(ProjectModel projectModel : mProjectList) {
				 SliderLayout slider = new SliderLayout(DailyViewActivity.this);
				 slider.setId(sliderIndex++);
				 slider.setOnChangedSliderListener(changeSliderListener());
				 slider.setmProjectName(projectModel.getProjectName());
				 slider.setHoursLogged(projectModel.getHoursLogged());
				 slider.setmComment(projectModel.getComment());
				 slider.updateView();
				 mContainerLayout.addView(slider);
				 mProjectSliderList.add(slider);
			 }
			/*************** ACCORDION VIEW WITH PARENT PROJECTS ************/
//			for (ParentProjectModel parentModel : mEnhacedDayProgressModel
//					.getParentProjectList()) {
//				Button b1 = new Button(DailyViewActivity.this);
//				b1.setId(parentModel.getParentId());
//				b1.setText(parentModel.getParentName());
//				LinearLayout lLayout = new LinearLayout(DailyViewActivity.this);
//				lLayout.setOrientation(LinearLayout.VERTICAL);
//				lLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//				lLayout.setVisibility(View.GONE);
//				for(ProjectModel projectModel : parentModel.getProjectList()) {
//					SliderLayout sliderLayout = new SliderLayout(DailyViewActivity.this);
//					sliderLayout.setOnChangedSliderListener(changeSliderListener());
//					sliderLayout.setmProjectName(projectModel.getProjectName());
//					sliderLayout.setHoursLogged(projectModel.getHoursLogged());
//					sliderLayout.setmComment(projectModel.getComment());
//					sliderLayout.updateView();
//					lLayout.addView(sliderLayout);
//					mProjectSliderList.add(sliderLayout);
//				}
//				mContainerLayout.addView(b1);
//				mContainerLayout.addView(lLayout);
//				b1.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						LinearLayout parent = (LinearLayout) v.getParent();
//						for (int childrenIndex = 0; childrenIndex < parent.getChildCount(); childrenIndex++) {
//							if (v.getId() == parent.getChildAt(childrenIndex).getId()) {
//								if (parent.getChildAt(childrenIndex + 1).getVisibility() == View.GONE) {
//									// Change visibility
//									parent.getChildAt(childrenIndex + 1).setVisibility(View.VISIBLE);
//								} else {
//									parent.getChildAt(childrenIndex + 1).setVisibility(View.GONE);
//								}
//							}
//						}
//					}
//				});
//			}
		}
	}
	
}
