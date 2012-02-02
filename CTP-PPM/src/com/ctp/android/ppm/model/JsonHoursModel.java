package com.ctp.android.ppm.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class JsonHoursModel {

	//FIXME
	@JsonProperty("Idhours")
	private long idhours;
	
	@JsonProperty("Date")
	private String date;

	@JsonProperty("ProjectId")
	private int projectId;

	@JsonProperty("Hours")
	private double hours;

	@JsonProperty("Submitted")
	private boolean submitted;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public double getHours() {
		return hours;
	}

	public void setHours(double hours) {
		this.hours = hours;
	}

	public boolean isSubmitted() {
		return submitted;
	}

	public void setSubmitted(boolean submitted) {
		this.submitted = submitted;
	}
	
	@Override
	public String toString() {
		return "Hours(" + date + ", " + projectId + ", " + hours + ", " + submitted +")";
	}

	//FIXME:
	
	public long getIdhours() {
		return idhours;
	}

	public void setIdhours(long idhours) {
		this.idhours = idhours;
	}
	
	
	
}
