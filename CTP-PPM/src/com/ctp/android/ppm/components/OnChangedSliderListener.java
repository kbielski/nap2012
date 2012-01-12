package com.ctp.android.ppm.components;

/**
 * Custom listener interface for capturing the even of the custom slider changing
 * 
 * @author kbiels
 *
 */
public interface OnChangedSliderListener {

	// Define our custom Listener interface
	public abstract void onSliderChanged(double hoursLogged);
}
