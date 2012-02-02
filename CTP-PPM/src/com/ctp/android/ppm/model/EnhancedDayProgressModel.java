package com.ctp.android.ppm.model;

import java.util.Date;
import java.util.List;

public class EnhancedDayProgressModel {

	private int dayId;
	private int dayOfTheYear;
	private String label;
	private Date date;
	private int progress;
	private List<ParentProjectModel> parentProjectList;
	
	public int getDayId() {
		return dayId;
	}
	public void setDayId(int dayId) {
		this.dayId = dayId;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public int getDayOfTheYear() {
		return dayOfTheYear;
	}
	public void setDayOfTheYear(int dayOfTheYear) {
		this.dayOfTheYear = dayOfTheYear;
	}
	public List<ParentProjectModel> getParentProjectList() {
		return parentProjectList;
	}
	public void setParentProjectList(List<ParentProjectModel> parentProjectList) {
		this.parentProjectList = parentProjectList;
	}
	
	
}
