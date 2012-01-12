package com.ctp.android.ppm.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ctp.android.ppm.R;

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

	public DayLayout(Context context) {
		super(context);
		setupView();
		setListners();
	}
	
	public DayLayout(Context context, AttributeSet attr)  {
		super(context, attr);
		setupView();
		setListners();
	}

	private void setupView() {
		LayoutInflater layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.btn_day, this, true);

		mLabel = (TextView) findViewById(R.id.lblDay);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBarLoggedHours);
		mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.daily_progress));
	}

	private void setListners() {
		this.setOnFocusChangeListener(setLabelBold());
		mLabel.setOnFocusChangeListener(setLabelBold());
		mLabel.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				((TextView) mLabel).setTypeface(Typeface.DEFAULT_BOLD);
				return false;
			}
		});
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
	 * Setting the progress for the progress bar component.
	 * @param progress in % of the logged hours
	 */
	public void setProgress(int progress) {
		mProgressBar.setProgress(progress);
	}
	
	/**
	 * Getting the progress from the progress bar component.
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
	 * Getting the identifier of this component (different for each day of the week).
	 * @return identifier of this component
	 */
	public int getDayId() {
		return dayId;
	}

	/**
	 * Setting the identifier of this component (different for each day of the week).
	 * @param identifier of this component
	 */
	public void setDayId(int dayId) {
		this.dayId = dayId;
	}

	/**
	 * Day of the year for this component
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
	
	
}
