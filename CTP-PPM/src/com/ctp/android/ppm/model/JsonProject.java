package com.ctp.android.ppm.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class JsonProject {

	@JsonProperty("Id")
	private int projectId;
	
	@JsonProperty("Name")
	private String projectName;

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
