package com.ctp.android.ppm.components;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.logic.SaveLoggedHoursDayThread;
import com.ctp.android.ppm.model.DayProgressModel;
import com.ctp.android.ppm.model.ProjectModel;
import com.ctp.android.ppm.model.ProjectModel.OperationFlag;
import com.ctp.android.ppm.model.SharedModel;

/**
 * Custom layout for a day component in the weekly view.
 * 
 * @author kbiels
 * 
 */
public class DayLayout extends LinearLayout {

	private int dayId;
	private int dayOfTheYear;
	private TextView mLabel;
	private ProgressBar mProgressBar;
	private DayProgressModel mDayProgressModel;
	private static final int COPY_DAY = 1;
	private static final int PASTE_DAY = 2;

	public DayLayout(Context context) {
		super(context);
		setupView();
		setListners();
	}

	public DayLayout(Context context, AttributeSet attr) {
		super(context, attr);
		setupView();
		setListners();
	}

	/**
	 * Constructs the view for this element.
	 */
	private void setupView() {
		LayoutInflater layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.day_layout, this, true);

		mLabel = (TextView) findViewById(R.id.lblDay);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBarLoggedHours);
		mProgressBar.setProgressDrawable(getResources().getDrawable(
				R.drawable.daily_progress));
	}

	/************************* LISTENERS *****************************/

	/**
	 * Set the listeners for events in this components like onFocus, onClick.
	 * Creating the context menu to copy the selected day.
	 */
	private void setListners() {
//		this.setOnFocusChangeListener(setLabelBold());
//		mLabel.setOnFocusChangeListener(setLabelBold());
//		mLabel.setOnTouchListener(new View.OnTouchListener() {
//			public boolean onTouch(View v, MotionEvent event) {
//				((TextView) mLabel).setTypeface(Typeface.DEFAULT_BOLD);
//				return false;
//			}
//		});
		this.setOnCreateContextMenuListener(createContextMenuListener());
	}

	private OnFocusChangeListener setLabelBold() {
		return new View.OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus)
					((TextView) mLabel).setTypeface(Typeface.DEFAULT_BOLD);
				else
					((TextView) mLabel).setTypeface(Typeface.DEFAULT);
			}
		};
	}

	/**
	 * Creates the context menu for the select day component. If there is one
	 * copied day in the shared memory it shows the menu of pasting a day.
	 * 
	 * @return OnCreateContextMenuListener
	 */
	private OnCreateContextMenuListener createContextMenuListener() {
		return new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.add(0, COPY_DAY, 0, createCopyLabel());
				menu.getItem(0).setOnMenuItemClickListener(
						onMenuItemClickListener());
				if (SharedModel.getInstance().getCopiedDay() != null) {
					menu.add(0, PASTE_DAY, 0, R.string.paste_day);
					menu.getItem(1).setOnMenuItemClickListener(
							onMenuItemClickListener());
				}

			}
		};
	}
	
	/**
	 * Create a label for the copy option in the Options Menu.
	 * @return Label with the selected day string.
	 */
	private String createCopyLabel() {
		String lbl = getContext().getString(R.string.copy_day);
		String str = mLabel.getText().toString();
		int startIndex = str.indexOf(",");
		lbl += str.substring(startIndex, str.length());
		return lbl;
	}

	/**
	 * Reacts to the context menu options (COPY or PASTE the selected day).
	 * 
	 * @return OnMenuItemClickListener
	 */
	public OnMenuItemClickListener onMenuItemClickListener() {
		return new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case COPY_DAY:
					doTestAction(getContext().getString(R.string.day_copied));
					SharedModel.getInstance().setCopiedDay(mDayProgressModel);
					return true;
				case PASTE_DAY:
					doTestAction(getContext().getString(R.string.day_pasted));
					updateViewWithCopiedModel(SharedModel.getInstance()
							.getCopiedDay());
					SharedModel.getInstance().setCopiedDay(null);
					return true;
				}
				return false;
			}
		};
	}

	/**
	 * Update the actual model and UI elements with the given model.
	 * 
	 * @param dayModel
	 *            Data to update the current component model and UI
	 */
	private void updateViewWithCopiedModel(DayProgressModel dayModel) {
		//set the progress
		setProgress(dayModel.getProgress());
		mDayProgressModel.setProgress(dayModel.getProgress());
		
		//delete the last data and create the pasted one
		if (mDayProgressModel.getProjectList() != null
				&& mDayProgressModel.getProjectList().size() > 0) {
			for(ProjectModel project : mDayProgressModel.getProjectList()) {
				project.setOperation(OperationFlag.DELETE);
			}
		}
		List<ProjectModel> mergedProjectList = mDayProgressModel.getProjectList();
		DateFormat dfJson = new SimpleDateFormat(getContext().getString(
				R.string.PpmDateFormat));
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Calendar.DAY_OF_YEAR, dayOfTheYear);
		cal.set(Calendar.YEAR, mDayProgressModel.getYear());
		List<ProjectModel> copiedProjectList = dayModel.getProjectList();
		for(ProjectModel project : copiedProjectList) {
			project.setOperation(OperationFlag.CREATE);
			project.setJsonDate(dfJson.format(cal.getTime()));
			//FIXME:
			project.setIdhours((new Date().getTime()) + copiedProjectList.indexOf(project));
		}
		mergedProjectList.addAll(copiedProjectList);
		mDayProgressModel.setProjectList(mergedProjectList);
		
		//Call the WS to save the copied day
		SaveLoggedHoursDayThread thread = new SaveLoggedHoursDayThread(
				mergedProjectList, null, null, getContext());
		thread.start();

	}

	// FIXME: delete this
	private void doTestAction(String txt) {
		Log.i("ContextMenu", txt);
		Toast.makeText(getContext(), txt, Toast.LENGTH_LONG).show();
	}

	/************************* END LISTENERS *****************************/

	/**
	 * Setting the progress for the progress bar component.
	 * 
	 * @param progress
	 *            in % of the logged hours
	 */
	public void setProgress(int progress) {
		mProgressBar.setProgress(progress);
	}

	/**
	 * Getting the progress from the progress bar component.
	 * 
	 * @return progress in % of the logged hours
	 */
	public int getProgress() {
		return mProgressBar.getProgress();
	}

	/**
	 * 
	 * @param txt
	 */
	public void setLabel(String txt) {
		mLabel.setText(txt);
	}

	/**
	 * 
	 * @return
	 */
	public String getLabel() {
		return mLabel.getText().toString();
	}

	/**
	 * Getting the identifier of this component (different for each day of the
	 * week).
	 * 
	 * @return identifier of this component
	 */
	public int getDayId() {
		return dayId;
	}

	/**
	 * Setting the identifier of this component (different for each day of the
	 * week).
	 * 
	 * @param identifier
	 *            of this component
	 */
	public void setDayId(int dayId) {
		this.dayId = dayId;
	}

	/**
	 * Day of the year for this component
	 * 
	 * @return day of the year
	 */
	public int getDayOfTheYear() {
		return dayOfTheYear;
	}

	/**
	 * 
	 * @param dayOfTheYear
	 */
	public void setDayOfTheYear(int dayOfTheYear) {
		this.dayOfTheYear = dayOfTheYear;
	}

	public DayProgressModel getmDayProgressModel() {
		return mDayProgressModel;
	}

	public void setmDayProgressModel(DayProgressModel mDayProgressModel) {
		this.mDayProgressModel = mDayProgressModel;
	}

}
