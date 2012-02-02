package com.ctp.android.ppm.model;

import java.util.List;

public class ParentProjectModel {

	private int parentId;
	private String parentName;
	private List<ProjectModel> projectList;
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public List<ProjectModel> getProjectList() {
		return projectList;
	}
	public void setProjectList(List<ProjectModel> projectList) {
		this.projectList = projectList;
	}
	
	
}
