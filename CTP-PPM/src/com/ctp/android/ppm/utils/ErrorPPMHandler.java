package com.ctp.android.ppm.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Handler used to show an error message from a background
 * running thread. Informs about the server errors.
 */
public class ErrorPPMHandler extends Handler {

	private Context context;
	
	public ErrorPPMHandler(Context context) {
		this.context = context;
	}
	
	public void handleMessage(Message msg) {
		String error = msg.getData().getString(CommonUtils.ERROR_MSG);
		Toast.makeText(
				context,
				error,
				Toast.LENGTH_LONG).show();
	}
}
