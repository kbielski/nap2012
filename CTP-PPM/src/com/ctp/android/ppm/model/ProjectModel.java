package com.ctp.android.ppm.model;

public class ProjectModel {

	private int id;
	private String projectName;
	private float hoursLogged;
	private String comment;

	public float getHoursLogged() {
		return hoursLogged;
	}

	public void setHoursLogged(float hoursLogged) {
		this.hoursLogged = hoursLogged;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
