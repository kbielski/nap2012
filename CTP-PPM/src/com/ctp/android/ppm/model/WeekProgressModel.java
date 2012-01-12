package com.ctp.android.ppm.model;

import java.util.List;

public class WeekProgressModel {

	private int weekId;
	private int year;
	private String label;
	private float loggedHoursAmmount;
	private float notLoggedHoursAmmount;
	private List<DayProgressModel> dayList;

	public int getWeekId() {
		return weekId;
	}

	public void setWeekId(int weekId) {
		this.weekId = weekId;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<DayProgressModel> getDayList() {
		return dayList;
	}

	public void setDayList(List<DayProgressModel> dayList) {
		this.dayList = dayList;
	}

	public float getLoggedHoursAmmount() {
		return loggedHoursAmmount;
	}

	public void setLoggedHoursAmmount(float loggedHoursAmmount) {
		this.loggedHoursAmmount = loggedHoursAmmount;
	}

	public float getNotLoggedHoursAmmount() {
		return notLoggedHoursAmmount;
	}

	public void setNotLoggedHoursAmmount(float notLoggedHoursAmmount) {
		this.notLoggedHoursAmmount = notLoggedHoursAmmount;
	}
}
