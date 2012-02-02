package com.ctp.android.ppm.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.ctp.android.ppm.R;

/**
 * Service running in the background informing about the accepted hours in PPM.
 * 
 * @author kbiels
 * 
 */
public class AcceptedHoursService extends IntentService {

	private static final int WAIT_TIME = 60 * 1000;
	private Handler mHandler;

	/**
	 * A constructor is required, and must call the super IntentService(String)
	 * constructor with a name for the worker thread.
	 */
	public AcceptedHoursService() {
		super("AcceptedHoursService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mHandler = new Handler();
	}

	/**
	 * The IntentService calls this method from the default worker thread with
	 * the intent that started the service. When this method returns,
	 * IntentService stops the service, as appropriate.
	 */
	@Override
	protected void onHandleIntent(Intent arg0) {

		// TODO: call a WS to check if the hours were accepted?
		for (long i = 0; i <= 100; i++) {
			//Log.e("Service Example", " " + i);
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(
							AcceptedHoursService.this,
							getString(R.string.hours_accepted) + " Project XYZ",
							Toast.LENGTH_LONG).show();
				}
			});

			try {
				Thread.sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
