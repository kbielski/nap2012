package com.ctp.android.ppm.logic;

import com.ctp.android.ppm.model.WeekProgressModel;

public interface IWSCallback {

	public void returnWeeklyHoursLoggedResponse(WeekProgressModel weekProgressModel);
}
