package com.ctp.android.ppm.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ctp.android.ppm.R;

public class SliderLayout extends LinearLayout {

	private TextView mLblProject;
	private SeekBar mSeekBar;
	private double hoursLogged;
	private OnChangedSliderListener onChangedSliderListener;
	
	public SliderLayout(Context context) {
		super(context);
		setupView();
	}

	public SliderLayout(Context context, AttributeSet attr) {
		super(context, attr);
		setupView();
	}

	private void setupView() {
		LayoutInflater layoutInflanter = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflanter.inflate(R.layout.slider_layout, this, true);
		
		mLblProject = (TextView) findViewById(R.id.lblSliderHoursLogged);
		mSeekBar = (SeekBar) findViewById(R.id.seekbarSlider);
		int maxHours = getResources().getInteger(R.integer.hoursMax);
		mSeekBar.setMax(2 * maxHours);
		mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener());
		
	}

	private OnSeekBarChangeListener seekBarChangeListener() {
		return new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				hoursLogged = (double) progress/2;
				mLblProject.setText("Project XYZ, " + " " + hoursLogged + "h");
				onChangedSliderListener.onSliderChanged(hoursLogged);
			}
		};
	}

	/**
	 * Custom listener for the event of chaning the value of the slider.
	 * @param onChangedSliderListener
	 */
	public void setOnChangedSliderListener(
			OnChangedSliderListener onChangedSliderListener) {
		this.onChangedSliderListener = onChangedSliderListener;
	}

	public double getHoursLogged() {
		return hoursLogged;
	}

	public void setHoursLogged(double hoursLogged) {
		this.hoursLogged = hoursLogged;
	}
	
	
}
