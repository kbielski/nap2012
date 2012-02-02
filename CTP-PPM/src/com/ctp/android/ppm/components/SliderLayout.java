package com.ctp.android.ppm.components;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ctp.android.ppm.R;

public class SliderLayout extends LinearLayout {

	private TextView mLblProject;
	private SeekBar mSeekBar;
	private ImageView mCommentImage;
	private String mProjectName;
	private String mComment;
	private Dialog mDialog;
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
		mCommentImage = (ImageView) findViewById(R.id.imgSliderComment);
		mCommentImage.setOnClickListener(commentClickListener());
 		int maxHours = getResources().getInteger(R.integer.hoursMax);
		mSeekBar.setMax(2 * maxHours);
		mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener());
		
	}

	/**
	 * Opening a dialog to enter the comment for the selected project.
	 * @return OnClickListener
	 */
	private OnClickListener commentClickListener() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 //set up dialog
				mDialog = new Dialog(getContext());
				mDialog.setContentView(R.layout.dialog_comment);
				mDialog.setTitle(getContext().getString(R.string.add_comment));
				mDialog.setCancelable(true);
 
				if(mComment != null) {
					EditText txt = (EditText) mDialog.findViewById(R.id.txtDialogComment);
					txt.setText(mComment);
				}
				
                //set up button
                Button btnOK = (Button) mDialog.findViewById(R.id.btnOkDialogComment);
                btnOK.setOnClickListener(saveCommentFromDialog());
                Button btnCancel = (Button) mDialog.findViewById(R.id.btnCancelDialogComment);
                btnCancel.setOnClickListener(cancelCommitFromDialog());
                
                mDialog.show();
			}
		};
	}
	
	/**
	 * Saving the comment from the dialog window.
	 * @return OnClickListener
	 */
	private OnClickListener saveCommentFromDialog() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText txt = (EditText) mDialog.findViewById(R.id.txtDialogComment);
				mComment = txt.getText().toString();
				if(mComment != null && mComment.length() > 0) {
					mCommentImage.setImageResource(R.drawable.comment_full);
				}
				else {
					mCommentImage.setImageResource(R.drawable.comment_empty);
				}
				mDialog.dismiss();
			}
		};
	}
	
	
	/**
	 * Closing the dialog without saving the comment.
	 * @return OnClickListener
	 */
	private OnClickListener cancelCommitFromDialog() {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		};
	}

	/**
	 * 
	 * @return
	 */
	private OnSeekBarChangeListener seekBarChangeListener() {
		return new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				hoursLogged = (double) progress/2;
				updateView();
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

	/**
	 * Updates the label with the actual project name and the amount
	 * of hours logged on this project.
	 */
	public void updateView() {
		mLblProject.setText(mProjectName + ", " + " " + hoursLogged + "h");
	}
	
	public double getHoursLogged() {
		return hoursLogged;
	}

	public void setHoursLogged(double hoursLogged) {
		this.hoursLogged = hoursLogged;
		this.mSeekBar.setProgress( (int) (hoursLogged * 2));
		onChangedSliderListener.onSliderChanged(hoursLogged);
	}
	
	public String getmProjectName() {
		return mProjectName;
	}

	public void setmProjectName(String mProjectName) {
		this.mProjectName = mProjectName;
	}

	public String getmComment() {
		return mComment;
	}

	public void setmComment(String mComment) {
		this.mComment = mComment;
		if(this.mComment != null && this.mComment.length() > 0) {
			mCommentImage.setImageResource(R.drawable.comment_full);
		}
	}
	

	
}
