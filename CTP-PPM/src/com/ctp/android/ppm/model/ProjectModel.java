package com.ctp.android.ppm.model;

public class ProjectModel {

	public enum OperationFlag { CREATE, UPDATE, DELETE };
	
	private int id;
	private String projectName;
	private String jsonDate;
	private double hoursLogged;
	private String comment;
	private OperationFlag operation;
	//FIXME:
	private long idhours;

	public double getHoursLogged() {
		return hoursLogged;
	}

	public void setHoursLogged(double hoursLogged) {
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

	public OperationFlag getOperation() {
		return operation;
	}

	public void setOperation(OperationFlag operation) {
		this.operation = operation;
	}

	public String getJsonDate() {
		return jsonDate;
	}

	public void setJsonDate(String jsonDate) {
		this.jsonDate = jsonDate;
	}

	//FIXME:
	public long getIdhours() {
		return idhours;
	}

	public void setIdhours(long idhours) {
		this.idhours = idhours;
	}
	
	
}
